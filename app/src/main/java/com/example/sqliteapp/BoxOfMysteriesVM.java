package com.example.sqliteapp;

import com.example.sqliteapp.NoteFragment.DataPassListener;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class BoxOfMysteriesVM extends AndroidViewModel {

    private static final String TAG = "BoxOfMysteriesVM";

    DatabaseHelper databaseHelper;
    List<Note> notesList = new ArrayList<>();
    NotesAdapter mAdapter;

    private DataPassListener dataPassListener;
    private MutableLiveData<Note> currentNote = new MutableLiveData<>();
    private MutableLiveData<Integer> currentPosition = new MutableLiveData<>();
    private MutableLiveData<Boolean> isNoteCreated = new MutableLiveData<>();

    public BoxOfMysteriesVM(@NonNull Application application) {
        super(application);

        databaseHelper = new DatabaseHelper(
                application.getApplicationContext(),
                Constants.BOM_DATABASE_NAME,
                Constants.BOM_DATABASE_VERSION
        );
        notesList.addAll(databaseHelper.getAllNotes());
        mAdapter = new NotesAdapter(notesList);

        // Initialize dataPassListener
        dataPassListener = new DataPassListener() {
            @Override
            public void onDataPass(int action, String noteTitle, String noteBody) {
                // Handle data pass
                handleDataPass(action, noteTitle, noteBody);
            }
        };
    }


    /**
     * Get DataPassListener reference.
     *
     * @return {@link #dataPassListener} which is tied to noteFragment by the Activity to passe data from the Fragment to the ViewModel
     */
    public DataPassListener getDataPassListener() {
        return dataPassListener;
    }

    /**
     * Pass data from the Activity to the ViewModel to be used by {@link #handleDataPass(int, String, String)} for {@link #dataPassListener}
     *
     * @param note     note
     * @param position of the note in notesList
     */
    public void passNoteToVM(Note note, int position) {
        currentNote.setValue(note);
        currentPosition.setValue(position);
    }

    public LiveData<Boolean> getIsNoteCreated() {
        return isNoteCreated;
    }

    public void setIsNoteCreated(boolean isNoteCreated) {
        this.isNoteCreated.setValue(isNoteCreated);
    }

    /**
     * Handle data passed from noteFragment <p>
     * Refer to documentation of DataPassListener Interface
     *
     * @param action    .
     * @param noteTitle .
     * @param noteBody  .
     */
    private void handleDataPass(int action, String noteTitle, String noteBody) {
        Note note = this.currentNote.getValue();
        int position = this.currentPosition.getValue();
//        Integer position = this.currentPosition.getValue();

        // Perform database operations based on action
        switch (action) {
            case Constants.ACTION_CREATE:
                if (!TextUtils.isEmpty(noteBody) || !TextUtils.isEmpty(noteTitle)) {
                    createNote(noteTitle, noteBody);
                }
                break;
            case Constants.ACTION_UPDATE:
                if (note != null && (!note.getNoteBody().equals(noteBody) || !note.getNoteTitle().equals(noteTitle))) {
                    updateNote(noteTitle, noteBody, position);
                }
                break;
            case Constants.ACTION_DELETE:
                deleteNote(position);
                break;
            default: // ACTION_CLOSE_ONLY
                break;
        }
    }


    // Database operations methods

    /**
     * Add new note to the Database, Notes List of Recycler View, and Adapter's notes lists
     *
     * @param noteTitle of newly added note
     * @param noteBody  of newly added note
     */
    private void createNote(String noteTitle, String noteBody) {
        // insert note in Database
        long id = databaseHelper.insertNote(noteTitle, noteBody);

        // get the newly inserted note from Database
        Note n = databaseHelper.getNote(id);
        // check if the newly added note could be queried from the Database. It is needed especially in SQL Shard Failure situations.
        if (n != null) {
            // add new note to Notes List at position 0
            notesList.add(0, n);
            // refresh the adapter of Recycler view
            mAdapter.notifyItemInserted(0);
            // add note to the Adapter's notesListFull
            mAdapter.editNotesListFull(n, 0, Constants.ACTION_CREATE);
            // update isNoteCreated which is observed by the Activity
            isNoteCreated.setValue(true);
        }
    }

    private void updateNote(String noteTitle, String noteBody, int position) {
        // Implement update note logic
    }

    private void deleteNote(int position) {
        // Implement delete note logic
    }

}
