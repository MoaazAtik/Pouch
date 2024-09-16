package com.thewhitewings.pouch.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.data.Note
import com.thewhitewings.pouch.databinding.FragmentNoteBinding

class NoteFragment : Fragment() {
    private lateinit var binding: FragmentNoteBinding
    private val noteViewModel by viewModels<NoteViewModel> { NoteViewModel.Factory }
    private lateinit var noteLiveData: LiveData<Note?>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        noteViewModel.initializeNote(arguments)
//        noteLiveData = noteViewModel.getNoteLiveData()
//
//        setupListeners()
//        setupViewModelObservers()
    }

    override fun onResume() {
        super.onResume()
//        setupPressingBehaviour()
    }

    override fun onPause() {
        super.onPause()
//        if (noteLiveData.value != null) noteViewModel.updateNoteLiveData(
//            Note(
//                noteLiveData.value!!.id,
//                binding.etNoteTitle.text.toString(),
//                binding.etNoteBody.text.toString(),
//                noteLiveData.value!!.timestamp
//            )
//        )
    }

    /**
     * Sets up the listeners for the UI elements.
     */
    private fun setupListeners() {
        binding.root.setOnClickListener {
            (requireActivity() as MainActivity).clearFocusAndHideKeyboard(
                binding.etNoteTitle
            )
            (requireActivity() as MainActivity).clearFocusAndHideKeyboard(binding.etNoteBody)
        }

        binding.btnBack.setOnClickListener { handleNavigationBack() }

        binding.btnDelete.setOnClickListener {
            noteViewModel.deleteNote()
            closeNote()
        }
    }

    /**
     * Sets up the observers for the ViewModel's LiveData.
     */
    private fun setupViewModelObservers() {
        noteLiveData.observe(viewLifecycleOwner) { note: Note? ->
            binding.etNoteTitle.setText(note!!.noteTitle)
            binding.etNoteBody.setText(note.noteBody)
//            binding.txtTimestamp.text = getString(R.string.edited_timestamp, note.timestamp)
        }
    }

    /**
     * Sets up the back pressing behaviour for the fragment.
     *
     * **Note:**
     *
     * It should not be called before [onResume].
     * Otherwise, after a configuration change, it will interfere with the [OnBackPressedCallback] of the activity,
     * thus, the fragment will not be able to handle the back press.
     *
     */
    private fun setupBackPressingBehaviour() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleNavigationBack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /**
     * Handles the back navigation.
     */
    private fun handleNavigationBack() {
//        noteViewModel.createOrUpdateNote(
//            binding.etNoteTitle.text.toString(),
//            binding.etNoteBody.text.toString()
//        )
        closeNote()
    }

    /**
     * Close the note.
     */
    private fun closeNote() {
        // Clear focus to prevent issues with SearchView focus in MainActivity
        /*
        Needed for back arrow and device's back button so the focus won't be automatically passed to Sv Search note.
         */
        binding.etNoteTitle.clearFocus()
        binding.etNoteBody.clearFocus()

        // Navigate back (remove the fragment)
        val fragmentManager = requireActivity().supportFragmentManager
        // Begin a fragment transaction
        val fragmentTransaction = fragmentManager.beginTransaction()
        // Set fragment transaction Animations
        fragmentTransaction.setCustomAnimations(
            androidx.fragment.R.animator.fragment_fade_enter,  // Enter animation
            androidx.fragment.R.animator.fragment_fade_exit,  // Exit animation
            androidx.fragment.R.animator.fragment_close_enter,  // Pop enter animation (when navigating back)
            androidx.fragment.R.animator.fragment_fade_exit // Pop exit animation (when navigating back)
        )

        // Remove the current fragment from the container
        fragmentTransaction.remove(this)
        // Commit the transaction
        fragmentTransaction.commit()
    }

    companion object {
        private const val TAG = "NoteFragment"
    }
}
