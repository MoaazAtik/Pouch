package com.thewhitewings.pouch.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.data.Note
import com.thewhitewings.pouch.databinding.ActivityMainBinding
import com.thewhitewings.pouch.ui.adapters.NotesAdapter
import com.thewhitewings.pouch.ui.adapters.RecyclerTouchListener
import com.thewhitewings.pouch.ui.adapters.RecyclerTouchListener.TouchListener
import com.thewhitewings.pouch.utils.Constants
import com.thewhitewings.pouch.utils.DateTimeFormatType
import com.thewhitewings.pouch.utils.DateTimeUtils
import com.thewhitewings.pouch.utils.Zone
import java.util.Objects
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> { MainViewModel.Factory }
    private val adapter: NotesAdapter = NotesAdapter()
    private lateinit var notesLiveData: LiveData<List<Note>>
    private lateinit var currentZone: LiveData<Zone>

    // Count of how many times the Box of mysteries reveal button has been pressed (knocked)
    private var bomKnocks = 0

    // Boolean of whether the timeout for revealing the Box of mysteries has started
    private var bomTimeoutStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notesLiveData = viewModel.notesLiveData
        currentZone = viewModel.getCurrentZoneLiveData()

        setupRecyclerView()
        setupListeners()
        setupViewModelObservers()
        setupBackPressingBehaviour()

        showBtnRevealBom()
    }

    /**
     * Sets up the RecyclerView for displaying notes.
     */
    private fun setupRecyclerView() {
        val mLayoutManager: RecyclerView.LayoutManager =
            StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = mLayoutManager
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter

        val recyclerTouchListener = RecyclerTouchListener(
            this, binding.recyclerView, object : TouchListener {
                override fun onClick(position: Int) {
                    openNote(
                            notesLiveData.value!![position]
                    )
                }

                override fun onSwiped(position: Int) {
                    viewModel.deleteNote(
                            notesLiveData.value!![position]
                    )
                }
            })

        val itemTouchHelper = ItemTouchHelper(recyclerTouchListener)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
        binding.recyclerView.addOnItemTouchListener(recyclerTouchListener)
    }

    /**
     * Sets up listeners for the UI elements.
     */
    private fun setupListeners() {
        binding.btnCreateNote.setOnClickListener { openNote(null) }

        binding.activityMainRoot.setOnClickListener {
            clearFocusAndHideKeyboard(
                binding.svSearchNotes
            )
        }

        binding.svSearchNotes.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.searchNotes(newText)
                return false
            }
        })

        binding.btnSort.setOnClickListener { showSortingPopupMenu() }

        binding.btnRevealBom.setOnClickListener { revealBoxOfMysteries() }
    }

    /**
     * Sets up observers for the ViewModel's LiveData.
     */
    private fun setupViewModelObservers() {
        notesLiveData.observe(this) { notes: List<Note>? ->
            adapter.setNotes(notes)
            toggleZoneNameVisibility()
        }

        currentZone.observe(this) { zone: Zone ->
            clearFocusAndHideKeyboard(binding.svSearchNotes)
            if (zone == Zone.BOX_OF_MYSTERIES) goToBoxOfMysteries()
            else goToCreativeZone()
            binding.svSearchNotes.setQuery("", false)
        }
    }

    /**
     * Sets up the back pressing behaviour for the activity.
     */
    private fun setupBackPressingBehaviour() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentZone.value == Zone.BOX_OF_MYSTERIES) viewModel.toggleZone()
                else finish()
            }
        })
    }

    /**
     * Opens a note in the fragment container.
     *
     * @param note the note to be opened, or `null` if a new note should be created
     */
    private fun openNote(note: Note?) {
        clearFocusAndHideKeyboard(binding.svSearchNotes)

        val noteFragment = NoteFragment()
        if (note != null) {
            val argsBundle = Bundle()
            argsBundle.putInt(Constants.COLUMN_ID, note.id)
            argsBundle.putString(Constants.COLUMN_NOTE_TITLE, note.noteTitle)
            argsBundle.putString(Constants.COLUMN_NOTE_BODY, note.noteBody)
            argsBundle.putString(
                Constants.COLUMN_TIMESTAMP,
                DateTimeUtils.getFormattedDateTime(
                    DateTimeFormatType.LOCAL_TO_LOCAL_MEDIUM_LENGTH_FORMAT,
                    note.timestamp
                )
            )
            noteFragment.arguments = argsBundle
        }

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            androidx.fragment.R.animator.fragment_fade_enter,
            androidx.fragment.R.animator.fragment_fade_exit,
            androidx.fragment.R.animator.fragment_close_enter,
            androidx.fragment.R.animator.fragment_fade_exit
        )
        fragmentTransaction.replace(R.id.fragment_container_note, noteFragment)
        fragmentTransaction.commit()
    }

    /**
     * Clears the focus from the given view and hides the soft keyboard.
     *
     * @param view the view to clear focus from
     */
    fun clearFocusAndHideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }

    /**
     * Shows and sets up the sorting popup menu to sort the notes in the RecyclerView.
     */
    private fun showSortingPopupMenu() {
        val popupMenu = PopupMenu(this, binding.btnSort)
        val menuInflater = popupMenu.menuInflater
        menuInflater.inflate(R.menu.popup_menu_sort, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            viewModel.handleSortOptionSelection(item.itemId)
            true
        }
        popupMenu.show()
    }

    /**
     * Toggles the visibility of the zone name text view.
     */
    private fun toggleZoneNameVisibility() {
        if (notesLiveData.value!!.isNotEmpty())
            binding.txtZoneName.visibility = View.GONE
        else binding.txtZoneName.visibility = View.VISIBLE
    }

    /**
     * Makes the reveal Box of mysteries button interactable.
     */
    private fun showBtnRevealBom() {
        Handler().postDelayed({ binding.btnRevealBom.visibility = View.VISIBLE }, 1500)
    }

    /**
     * Handles the logic for revealing the Box of mysteries.
     */
    private fun revealBoxOfMysteries() {
        bomKnocks++
        val handler = Handler(Looper.getMainLooper())

        if (!bomTimeoutStarted) {
            val executor = Executors.newSingleThreadExecutor()
            executor.execute {
                val timeoutKnocking = (7 * 1000).toLong() // 7 seconds
                val startKnockingTime = System.currentTimeMillis()
                bomTimeoutStarted = true
                while (bomTimeoutStarted) {
                    val elapsedKnockingTime = System.currentTimeMillis() - startKnockingTime
                    if (elapsedKnockingTime >= timeoutKnocking) {
                        bomTimeoutStarted = false
                        bomKnocks = 0
                        break
                    } else if (bomKnocks == 4) {
                        runOnUiThread { binding.btnRevealBom.setBackgroundResource(R.drawable.ripple_revealed_word) }
                    } else if (bomKnocks == 5) {
                        handler.postDelayed({
                            bomTimeoutStarted = false
                            bomKnocks = 0
                            viewModel.toggleZone()
                        }, 500)
                        break
                    }

                    synchronized(this) {
                        try {
                            (this as Object).wait(200)
                        } catch (e: InterruptedException) {
                            throw RuntimeException(e)
                        }
                    }
                }
            }
        }
    }

    /**
     * Makes the needed changes to the UI for navigating to the Box of mysteries zone.
     */
    private fun goToBoxOfMysteries() {
        binding.btnRevealBom.setBackgroundColor(Color.TRANSPARENT)
        binding.btnRevealBom.visibility = View.GONE

        modifyLogo()
        modifyZoneName()

        binding.lvRevealScreen.setAnimation(R.raw.reveal_screen_black)
        binding.lvRevealScreen.speed = 0.5f
        binding.lvRevealScreen.playAnimation()

        binding.lvRevealLoader.visibility = View.VISIBLE
        binding.lvRevealLoader.playAnimation()
        Handler(Looper.getMainLooper())
            .postDelayed(
                { binding.lvRevealLoader.visibility = View.GONE },
                2000
            )

        Snackbar.make(
            binding.activityMainRoot,
            getString(R.string.bom_revealing_message),
            Snackbar.LENGTH_LONG
        )
            .show()
    }

    /**
     * Makes the needed changes to the UI for navigating to the Creative zone.
     */
    private fun goToCreativeZone() {
        binding.btnRevealBom.visibility = View.VISIBLE

        modifyLogo()
        modifyZoneName()

        binding.lvRevealScreen.setAnimation(R.raw.reveal_screen_red)
        binding.lvRevealScreen.speed = 1f
        binding.lvRevealScreen.playAnimation()
    }

    /**
     * Modifies the logo image view for navigating to a different zone.
     */
    private fun modifyLogo() {
        val initialColor: Int
        val finalColor: Int

        /*
        note that the currentZone here represents the updated value which is the destination you just arrived
         */
        if (currentZone.value == Zone.BOX_OF_MYSTERIES) {
            initialColor = resources.getColor(R.color.md_theme_light_primaryContainer, null)
            finalColor = resources.getColor(R.color.gray_logo_bom, null)
        } else {
            initialColor = resources.getColor(R.color.gray_logo_bom, null)
            finalColor = resources.getColor(R.color.md_theme_light_primaryContainer, null)
        }

        val tintAnimator = ValueAnimator.ofArgb(initialColor, finalColor)
        tintAnimator.setDuration(3500)
        tintAnimator.addUpdateListener { animator: ValueAnimator ->
            val animatedValue = animator.animatedValue as Int
            binding.imgLogo.setColorFilter(animatedValue)
        }
        tintAnimator.start()
    }

    /**
     * Modifies the zone name text view for navigating to a different zone.
     */
    private fun modifyZoneName() {
        val txtZoneName = binding.txtZoneName
        val zoneName: String
        val typeface: Typeface
        val initialTextSize: Float
        val finalTextSize: Float
        val initialColor: Int
        val finalColor: Int

        /*
        note that the currentZone here represents the updated value which is the destination you just arrived
         */
        if (currentZone.value == Zone.BOX_OF_MYSTERIES) {
            zoneName = getString(R.string.box_of_mysteries)
            typeface = Typeface.create("cursive", Typeface.BOLD)
            initialTextSize = 26f
            finalTextSize = 32f
            initialColor = resources.getColor(R.color.md_theme_light_inversePrimary, null)
            finalColor = Color.BLACK
        } else {
            zoneName = getString(R.string.creative_zone)
            typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
            initialTextSize = 32f
            finalTextSize = 26f
            initialColor = Color.BLACK
            finalColor = resources.getColor(R.color.md_theme_light_inversePrimary, null)
        }

        txtZoneName.text = zoneName

        txtZoneName.typeface = typeface

        val textSizeAnimator =
            ObjectAnimator.ofFloat(txtZoneName, "textSize", initialTextSize, finalTextSize)
        textSizeAnimator.setDuration(1000)
        textSizeAnimator.start()

        val colorAnimator = ValueAnimator.ofArgb(initialColor, finalColor)
        colorAnimator.setDuration(3500)
        colorAnimator.addUpdateListener { animator: ValueAnimator ->
            txtZoneName.setTextColor(
                animator.animatedValue as Int
            )
        }
        colorAnimator.start()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
