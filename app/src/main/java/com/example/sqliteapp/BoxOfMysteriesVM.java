package com.example.sqliteapp;

import com.example.sqliteapp.NoteFragment.DataPassListener;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SearchView;

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
    private SearchView.OnQueryTextListener onQueryTextListener;
    private int currentPosition;
    private Note currentNote;
    private MutableLiveData<Integer> doneNoteAction = new MutableLiveData<>();

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

        // Initialize onQueryTextListener
        onQueryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
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
     * Get OnQueryTextListener for SearchView
     *
     * @return {@link #onQueryTextListener} which is tied to SearchView in Activity to search notes
     */
    public SearchView.OnQueryTextListener getOnQueryTextListener() {
        return onQueryTextListener;
    }

    /**
     * Pass data from the Activity to the ViewModel to be used by {@link #handleDataPass(int, String, String)} for {@link #dataPassListener}.<p>
     * Note: {@link #currentPosition} and {@link #currentNote} is to be used when Updating or Deleting a note, and not when Creating one.
     *
     * @param position of the note in notesList
     */
    public Note passPositionToVM(int position) {
        currentPosition = position;
        currentNote = notesList.get(position);
        return currentNote;
    }

    public LiveData<Integer> getDoneNoteAction() {
        return doneNoteAction;
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

        // Perform database operations based on action
        switch (action) {
            case Constants.ACTION_CREATE:
                if (!TextUtils.isEmpty(noteBody) || !TextUtils.isEmpty(noteTitle)) {
                    createNote(noteTitle, noteBody);
                }
                break;
            case Constants.ACTION_UPDATE:
                if (currentNote != null && (!currentNote.getNoteBody().equals(noteBody) || !currentNote.getNoteTitle().equals(noteTitle))) {
                    updateNote(noteTitle, noteBody, currentPosition);
                }
                break;
            case Constants.ACTION_DELETE:
                deleteNote(currentPosition);
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
            // update doneNoteAction which is observed by the Activity
            doneNoteAction.setValue(Constants.ACTION_CREATE);
        }
    }

    /**
     * Update note in Database, Notes List of Recycler View, and Adapter's notes lists
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
        // Get Current timestamp in Local time zone for Storing in Notes List
        /*
        Then it will be automatically converted to UTC by DatabaseHelper.updateNote() for Storing in Database
         */
        n.setTimestamp(databaseHelper.getFormattedDateTime(Constants.CURRENT_LOCAL, null));
        // updating note in Database
        databaseHelper.updateNote(n);
        // refreshing the Recycler view
        mAdapter.notifyItemChanged(position);
        // update doneNoteAction which is observed by the Activity
        doneNoteAction.setValue(Constants.ACTION_UPDATE);
    }

    /**
     * Delete note from Database, Notes List of Recycler View, and Adapter's notes lists
     *
     * @param position of note in Notes List to be deleted
     */
    public void deleteNote(int position) {
        // deleting the note from Database
        databaseHelper.deleteNote(notesList.get(position));

        // removing the note from the Notes List
        notesList.remove(position);
        // refreshing the Recycler view
        mAdapter.notifyItemRemoved(position);
        // remove note from the Adapter's notesListFull
        mAdapter.editNotesListFull(null, position, Constants.ACTION_DELETE);
        // update doneNoteAction which is observed by the Activity
        doneNoteAction.setValue(Constants.ACTION_DELETE);
    }

}
