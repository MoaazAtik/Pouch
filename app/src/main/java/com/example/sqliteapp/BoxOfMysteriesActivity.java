package com.example.sqliteapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
        buildRecyclerView();
        toggleEmptyNotes();

        binding.btnCreateNote.setOnClickListener(v ->
                openNote(null, -1)
        );

    } // onCreate

    private void showWelcomingMessage() {
        Snackbar.make(binding.activityBomRoot, "Box of Mysteries is open", Snackbar.LENGTH_LONG)
                .show();
    }

    private void buildRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setAdapter(vm.mAdapter);
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
     * @param note     that will be updated, or null when creating new note.
     * @param position of note to be updated, or -1 when creating new note.
     */
    private void openNote(final Note note, final int position) {
        /*
        Clear Focus of Sv search note when opening note fragment, and Hide the virtual keyboard if it was opened by clicking Sv search note.
        If Sv search note has the focus then a note fragment is opened and nothing was clicked in the fragment, while the fragment's screen is open if the Hard Keyboard got input it will be directed to Et search note.
        This step fixes that.
         */
        clearFocusAndHideKeyboard(binding.svSearchNotes);

        // Pass data to ViewModel
        vm.passNoteToVM(note, position);

        NoteFragment noteFragment = new NoteFragment();
        // Pass note vales to fragment when Updating note
        if (note != null) {
            Bundle argsBundle = new Bundle();

            argsBundle.putString(Constants.COLUMN_NOTE_TITLE, note.getNoteTitle());
            argsBundle.putString(Constants.COLUMN_NOTE_BODY, note.getNoteBody());
            argsBundle.putString(Constants.COLUMN_TIMESTAMP, vm.databaseHelper.getFormattedDateTime(Constants.FORMATTING_LOCAL, note.getTimestamp()));

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

        // Observe if new note is created
        vm.getIsNoteCreated().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.recyclerView.scrollToPosition(0);
                    toggleEmptyNotes();
                    vm.setIsNoteCreated(false);
                }
            }
        });
        
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

}
