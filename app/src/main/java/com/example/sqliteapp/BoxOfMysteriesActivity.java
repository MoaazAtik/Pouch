package com.example.sqliteapp;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.sqliteapp.databinding.ActivityBoxOfMysteriesBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class BoxOfMysteriesActivity extends AppCompatActivity {

    private static final String TAG = "BoxOfMysteriesActivity";
    private ActivityBoxOfMysteriesBinding binding;
    private BoxOfMysteriesVM vm;

    // use binding. instead
//    private TextView noNotesView;
//    private SearchView svSearchNotes;
//    private ImageButton btnSort;
//    private RecyclerView recyclerView;

    // use VM
//    private NotesAdapter mAdapter;
//    private List<Note> notesList = new ArrayList<>();
//    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBoxOfMysteriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(BoxOfMysteriesVM.class);

        showWelcomingMessage();
        handleRecyclerView();
        toggleEmptyNotes();

        // Set Observer for note Action done by View Model after closing Note Fragment
        observeDoneNoteAction();

        // Button Create Note
        binding.btnCreateNote.setOnClickListener(v ->
                openNote(-1)
        );

        // Root Layout
        binding.activityBomRoot.setOnClickListener(v -> {
                    // Clear Focus of Sv search note, and Hide Soft Keyboard when outside of Sv search note is clicked
                    clearFocusAndHideKeyboard(binding.svSearchNotes);
                }
        );

        // Search View
        binding.svSearchNotes.setOnQueryTextListener(vm.getOnQueryTextListener());

        // Button Sort Notes
        binding.btnSort.setOnClickListener(v ->
            showSortingPopupMenu()
        );

    } // onCreate

    private void showWelcomingMessage() {
        Snackbar.make(binding.activityBomRoot, "Box of Mysteries is revealed", Snackbar.LENGTH_LONG)
                .show();
    }

    /**
     * Build Recycler View and Handle Item Click and Swipe events
     */
    private void handleRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setAdapter(vm.mAdapter);

        /*
        Touch Listener of Recycler View Items.
        on Click on RecyclerView item open note, on Swipe delete note.
         */
        RecyclerTouchListener recyclerTouchListener = new RecyclerTouchListener(
                this,
                binding.recyclerView,
                new RecyclerTouchListener.TouchListener() {
                    @Override
                    public void onClick(View view, int position) {
                        openNote(position);
                    }

                    @Override
                    public void onSwiped(int position) {
                        vm.deleteNote(position);
                    }
                }
        );
        // ItemTouchHelper to handle onSwiped
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(recyclerTouchListener);
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);
        binding.recyclerView.addOnItemTouchListener(recyclerTouchListener);
    }

    /**
     * Toggle list when there are notes to display, Or empty notes View when there are none.
     */
    private void toggleEmptyNotes() {
        if (vm.notesList.size() > 0) {
            binding.txtEmptyNotes.setVisibility(View.GONE);
        } else {
            binding.txtEmptyNotes.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Open Existing or New Note to create or edit a note.
     *
     * @param position of note to be updated, or -1 when creating new note.
     */
    private void openNote(final int position) {
        /*
        Clear Focus of Sv search note when opening note fragment, and Hide the virtual keyboard if it was opened by clicking Sv search note.
        If Sv search note has the focus then a note fragment is opened and nothing was clicked in the fragment, while the fragment's screen is open if the Hard Keyboard got input it will be directed to Et search note.
        This step fixes that.
         */
        clearFocusAndHideKeyboard(binding.svSearchNotes);

        NoteFragment noteFragment = new NoteFragment();

        // Pass note vales to fragment when Opening an already created note
        if (position > -1) {
            // Pass position to ViewModel and Get Note at current position
            Note currentNote = vm.passPositionToVM(position);

            Bundle argsBundle = new Bundle();

            argsBundle.putString(Constants.COLUMN_NOTE_TITLE, currentNote.getNoteTitle());
            argsBundle.putString(Constants.COLUMN_NOTE_BODY, currentNote.getNoteBody());
            argsBundle.putString(Constants.COLUMN_TIMESTAMP, vm.databaseHelper.getFormattedDateTime(Constants.FORMATTING_LOCAL, currentNote.getTimestamp()));

            noteFragment.setArguments(argsBundle);
        }

        // Direct to NoteFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        /*
        Animations. this has to be before fragmentTransaction.replace()
         */
        fragmentTransaction.setCustomAnimations(
                androidx.fragment.R.animator.fragment_fade_enter, // Enter animation
                androidx.fragment.R.animator.fragment_fade_exit, // Exit animation
                androidx.fragment.R.animator.fragment_close_enter, // Pop enter animation (when navigating back)
                androidx.fragment.R.animator.fragment_fade_exit // Pop exit animation (when navigating back)
        );
        fragmentTransaction.replace(R.id.fragment_container_note, noteFragment);
        fragmentTransaction.commit();

        // Set up data pass listener
        noteFragment.setDataPassListener(vm.getDataPassListener());
    }

    /**
     * Clear the Focus of the passed view, and Hide Soft (Virtual / Device's) Keyboard.
     *
     * @param view to clear its focus.
     */
    public void clearFocusAndHideKeyboard(View view) {
        // Hide Soft (Virtual) Keyboard when outside of Et search note is clicked
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        // Clear Focus of Et search note when clicking outside
        view.clearFocus();
    }

    /**
     * Observe done note action after closing note fragment
     */
    private void observeDoneNoteAction() {
        vm.getDoneNoteAction().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == Constants.ACTION_CREATE)
                    binding.recyclerView.scrollToPosition(0);

                toggleEmptyNotes();
            }
        });
    }

    /**
     * Show Popup Menu to Sort Notes
     */
    private void showSortingPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(this, binding.btnSort);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.popup_menu_sort, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_option_a_z) {
                vm.sortNotesHelper(Constants.SORT_A_Z);
                return true;
            } else if (itemId == R.id.menu_option_z_a) {
                vm.sortNotesHelper(Constants.SORT_Z_A);
                return true;
            } else if (itemId == R.id.menu_option_o) {
                vm.sortNotesHelper(Constants.SORT_OLDEST_FIRST);
                return true;
            } else if (itemId == R.id.menu_option_n) {
                vm.sortNotesHelper(Constants.SORT_NEWEST_FIRST);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

}
