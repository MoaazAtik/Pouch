package com.example.sqliteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView noNotesView;
    private NotesAdapter mAdapter;
    private List<Note> notesList = new ArrayList<>();
    private RecyclerView recyclerView;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        noNotesView = findViewById(R.id.empty_notes_view);

        databaseHelper = new DatabaseHelper(this);
        notesList.addAll(databaseHelper.getAllNotes());

        mAdapter = new NotesAdapter(notesList);
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        toggleEmptyNotes();

        FloatingActionButton addNote = findViewById(R.id.addNoteBtn);
        addNote.setOnClickListener(v ->
                openNote(null, -1)
        );

        /*
        Touch Listener of Recycler View Items.
        on Click on RecyclerView item open note, on Swipe delete note.
         */
        RecyclerTouchListener recyclerTouchListener = new RecyclerTouchListener(
                this,
                recyclerView,
                new RecyclerTouchListener.TouchListener() {
                    @Override
                    public void onClick(View view, int position) {
                        openNote(notesList.get(position), position);
                    }

                    @Override
                    public void onSwiped(int position) {
                        deleteNote(position);
                    }
                }
        );
        // ItemTouchHelper to handle onSwiped
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(recyclerTouchListener);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnItemTouchListener(recyclerTouchListener);

        findViewById(R.id.activity_main_root)
                .setOnClickListener(v -> {
                            // Hide Soft (Virtual) Keyboard when outside of Et search note is clicked
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            // Clear Focus of Et search note when clicking outside
                            findViewById(R.id.et_search_notes).clearFocus();
                        }
                );
    }//onCreate

    /**
     * Add new note to the Database and Notes List of Recycler View
     *
     * @param noteTitle newly added note
     * @param noteBody  newly added note
     */
    private void createNote(String noteTitle, String noteBody) {
        // inserting note in Database
        long id = databaseHelper.insertNote(noteTitle, noteBody);

        // get the newly inserted note from Database
        Note n = databaseHelper.getNote(id);
        // check if the newly added note could be queried from the Database. It is needed especially in SQL Shard Failure situations.
        if (n != null) {
            // adding new note to Notes List at position 0
            notesList.add(0, n);
            // refreshing the Recycler view
            mAdapter.notifyItemInserted(0);

            toggleEmptyNotes();
        }
    }

    /**
     * Update note in Database and Notes List of Recycler View
     *
     * @param noteTitle of updated note
     * @param noteBody  of updated note
     * @param position  of note in Notes List to be updated
     */
    private void updateNote(String noteTitle, String noteBody, int position) {
        // getting reference to the note
        Note n = notesList.get(position);
        // updating note values in the Notes List
        n.setNoteTitle(noteTitle);
        n.setNoteBody(noteBody);
        n.setTimestamp(getCurrentDateTime());
        // updating note in Database
        databaseHelper.updateNote(n);
        // refreshing the Recycler view
        mAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    /**
     * Delete note from Database and Notes List of Recycler View
     *
     * @param position of note in Notes List to be deleted
     */
    private void deleteNote(int position) {
        // deleting the note from Database
        databaseHelper.deleteNote(notesList.get(position));

        // removing the note from the Notes List
        notesList.remove(position);
        // refreshing the Recycler view
        mAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();
    }

    /**
     * Open Existing or New Note to create or edit a note.
     *
     * @param note         that will be updated, or null when creating new note.
     * @param position     of note to be updated, or -1 when creating new note.
     */
    private void openNote(final Note note, final int position) {
        // Hide the virtual keyboard if it was open by clicking et_search_notes
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);

        // Clear Focus of Et search note when opening note fragment
        /*
        If Et search note has the focus then a note fragment is opened and nothing was clicked in the fragment, while the fragment's screen is open if the Hard Keyboard got input it will be directed to Et search note.
        This step fixes that.
         */
        findViewById(R.id.et_search_notes).clearFocus();

        NoteFragment noteFragment = new NoteFragment();
        // Pass note vales to fragment when Updating note
        if (note != null) {
            Bundle argsBundle = new Bundle();

            argsBundle.putString(DatabaseHelper.COLUMN_NOTE_TITLE, note.getNoteTitle());
            argsBundle.putString(DatabaseHelper.COLUMN_NOTE_BODY, note.getNoteBody());
            argsBundle.putString(DatabaseHelper.COLUMN_TIMESTAMP, note.getTimestamp());

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
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


        noteFragment.setDataPassListener(new NoteFragment.DataPassListener() {
            @Override
            public void onDataPass(int action, String noteTitle, String noteBody) {
                // check wanted action
                switch (action) {
                    case NoteFragment.ACTION_CREATE:
                        // Create note only if it has content
                        if (!TextUtils.isEmpty(noteBody) || !TextUtils.isEmpty(noteTitle))
                            createNote(noteTitle, noteBody);
                        break;
                    case NoteFragment.ACTION_UPDATE:
                        // Update note only if its content was changed
                        if (note != null && (!note.getNoteBody().equals(noteBody) || !note.getNoteTitle().equals(noteTitle)))
                            updateNote(noteTitle, noteBody, position);
                        break;
                    case NoteFragment.ACTION_DELETE:
                        deleteNote(position);
                        break;
                    default: // ACTION_CLOSE_ONLY
                        break;
                }
            }
        });
    }

    /**
     * Helper method to get the Current timestamp
     */
    private String getCurrentDateTime() {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return sdFormat.format(date);
    }

    public void clearFocusAndHideKeyboard(View view) {
        // todo add this to back button in fragment for etNoteBody, etNoteTitle too. Try passing array of views
        // todo add this to recycler view's click listener too
        // todo implement
    }

    /**
     * Toggle list when there are notes to display, Or empty notes View when there are none.
     */
    private void toggleEmptyNotes() {
        if (notesList.size() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }
}
