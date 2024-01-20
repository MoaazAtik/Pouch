package com.example.sqliteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

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

        mAdapter = new NotesAdapter(this, notesList);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        toggleEmptyNotes();

//        Button addNote = findViewById(R.id.addNoteBtn);
        FloatingActionButton addNote = findViewById(R.id.addNoteBtn);
        addNote.setOnClickListener(v ->
//                showNoteDialog(false, null, -1)
                openNote(false,null, -1)
        );

        /*
        Touch Listener of Recycler View Items.
        on Long Press on RecyclerView item, open alert dialog with options to choose: Edit or Delete todo edit
         */
        recyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(
                        this,
                        recyclerView,
                        new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                Log.d(TAG, "onClick: ");
                                openNote(true, notesList.get(position), position);
                            }

                            @Override
                            public void onLongClick(View view, int position) {
//                                showActionsDialog(position); todo
                            }
                        }));
    }//onCreate

    /**
     * Add new note to the Database and Notes List of Recycler View
     *
     * @param noteTitle newly added note
     * @param noteBody newly added note
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
     * @param noteBody of updated note
     * @param position of note in Notes List to be updated
     */
    private void updateNote(String noteTitle, String noteBody, int position) {
        // getting reference to the note
        Note n = notesList.get(position);
        // updating note title and body in the Notes List
        n.setNoteTitle(noteTitle);
        n.setNoteBody(noteBody);
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
     * Open Dialog with Edit and Delete Note options
     *
     * @param position of clicked Item in Recycler View
     */
//    private void showActionsDialog(final int position) {
//        CharSequence[] optionsNames = new CharSequence[]{"Edit", "Delete"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setItems(
//                optionsNames,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == 0) {
//                            showNoteDialog(
//                                    true,
//                                    notesList.get(position),
//                                    position);
//                        } else {
//                            deleteNote(position);
//                        }
//                    }
//                });
//
//        builder.show();
//    }

    /**
//     * Show alert dialog with EditText options to create or edit a note.
//     * when shouldUpdate = true, it automatically displays old note and
//     * changes the button text to UPDATE
//     *
//     * @param shouldUpdate Updating or Creating a new Note.
//     * @param note         that will be updated, or null when creating new note.
//     * @param position     of note to be updated, or -1 when creating new note.
     */
//    private void addNote(final boolean shouldUpdate, final Note note, final int position) {
    private void openNote(final boolean shouldUpdate, final Note note, final int position) {

        NoteFragment noteFragment = new NoteFragment();
        if (note != null) {
            Bundle argsBundle = new Bundle();

            argsBundle.putInt(DatabaseHelper.COLUMN_ID, note.getId());
            argsBundle.putString(DatabaseHelper.COLUMN_NOTE_TITLE, note.getNoteTitle());
            argsBundle.putString(DatabaseHelper.COLUMN_NOTE_BODY, note.getNoteBody());
            argsBundle.putString(DatabaseHelper.COLUMN_TIMESTAMP, note.getTimestamp());

            noteFragment.setArguments(argsBundle);
            Log.d(TAG, "openNote: argsBundle " + argsBundle);
        }

        // Direct to MoreFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Animations. this has to be before fragmentTransaction.replace()
        fragmentTransaction.setCustomAnimations(
                androidx.fragment.R.animator.fragment_fade_enter, // Enter animation
                androidx.fragment.R.animator.fragment_fade_exit, // Exit animation
                androidx.fragment.R.animator.fragment_close_enter, // Pop enter animation (when navigating back)
                androidx.fragment.R.animator.fragment_fade_exit // Pop exit animation (when navigating back)
        );
//        fragmentTransaction.replace(R.id.fragment_container_note, new NoteFragment());
        fragmentTransaction.replace(R.id.fragment_container_note, noteFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        Log.d(TAG, "openNote: ");

        noteFragment.setDataPassListener(new NoteFragment.DataPassListener() {
            @Override
            public void onDataPass(String noteTitle, String noteBody) {
                Log.d(TAG, "onDataPass: noteTitle "+noteTitle);
                Log.d(TAG, "onDataPass: noteBody "+noteBody);

//                String noteBody = noteBody;

                // show toast message when no text is entered
//                if (TextUtils.isEmpty(noteBody)) {
                if (TextUtils.isEmpty(noteTitle) && TextUtils.isEmpty(noteBody)) {
                    Log.d(TAG, "empty note ");
                    Toast.makeText(MainActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                }

//                else {
//                    dialog.dismiss();
//                }
                // check if user updating note
                if (shouldUpdate) {
                    updateNote(noteTitle, noteBody, position);
                } else {
                    createNote(noteTitle, noteBody);
                }

            }
        });

    }

    /**
     * Show alert dialog with EditText options to create or edit a note.
     * when shouldUpdate = true, it automatically displays old note and
     * changes the button text to UPDATE
     *
     * @param shouldUpdate Updating or Creating a new Note.
     * @param note         that will be updated, or null when creating new note.
     * @param position     of note to be updated, or -1 when creating new note.
     */
//    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
//        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
//        View view = layoutInflater.inflate(R.layout.note_dialog, null);
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setView(view);
//
//        final EditText etNoteBody = view.findViewById(R.id.note);
//
//        TextView dialogTitle = view.findViewById(R.id.dialog_title);
//        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));
//
//        if (shouldUpdate) {
//            etNoteBody.setText(note.getNoteBody());
//        }
//
//        builder
//                .setCancelable(false)
//                .setPositiveButton(
//                        shouldUpdate ? "update" : "save",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                String noteBody = etNoteBody.getText().toString();
//
//                                // show toast message when no text is entered
//                                if (TextUtils.isEmpty(noteBody)) {
//                                    Toast.makeText(MainActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
//                                    return;
//                                } else {
//                                    dialog.dismiss();
//                                }
//                                // check if user updating note
//                                if (shouldUpdate) {
//                                    updateNote(noteBody, position);
//                                } else {
//                                    createNote(noteBody);
//                                }
//                            }
//                        })
//
//                .setNegativeButton(
//                        "cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//
//        final AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }//showNoteDialog()

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
