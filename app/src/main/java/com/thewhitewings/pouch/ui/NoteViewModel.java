package com.thewhitewings.pouch.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.thewhitewings.pouch.data.Note;
import com.thewhitewings.pouch.data.NotesRepository;
import com.thewhitewings.pouch.utils.Constants;

/**
 * ViewModel to interact with the {@link NotesRepository}'s data source and the Note screen.
 */
public class NoteViewModel extends ViewModel {

    private static final String TAG = "NoteViewModel";
    private final NotesRepository notesRepository;

    // the note that is opened for updating purpose
    private Note oldNote;

    // holds the state of the currently opened note
    private final MutableLiveData<Note> noteLiveData = new MutableLiveData<>();

    /**
     * Constructor for {@link NoteViewModel}.
     *
     * @param repository the repository to interact with the data source
     */
    public NoteViewModel(NotesRepository repository) {
        this.notesRepository = repository;
    }

    /**
     * Get the state of note LiveData.
     *
     * @return the note LiveData
     */
    public LiveData<Note> getNoteLiveData() {
        return noteLiveData;
    }

    /**
     * Update the state of note LiveData with the provided note.
     *
     * @param note the note with updated data
     */
    public void updateNoteLiveData(Note note) {
        noteLiveData.setValue(note);
    }

    /**
     * Initialize the note LiveData and {@link #oldNote} with the provided arguments.
     * It is needed for the first initialization when a note is opened for updating.
     *
     * @param args the arguments bundle passed from the activity containing the opened note data
     */
    public void initializeNote(Bundle args) {
        if (args == null) // creating new note
            return;

        if (noteLiveData.getValue() != null) // updating existing note - after configuration change
            return;

        // updating existing note - first initialization
        int id = args.getInt(Constants.COLUMN_ID);
        String noteTitle = args.getString(Constants.COLUMN_NOTE_TITLE);
        String noteBody = args.getString(Constants.COLUMN_NOTE_BODY);
        String timestamp = args.getString(Constants.COLUMN_TIMESTAMP);

        oldNote = new Note(id, noteTitle, noteBody, timestamp);
        updateNoteLiveData(oldNote);
    }


    /**
     * Create or update the note based on the provided data.
     *
     * @param newNoteTitle the new title of the note
     * @param newNoteBody  the new body of the note
     */
    public void createOrUpdateNote(String newNoteTitle, String newNoteBody) {
        if (oldNote == null && (!newNoteTitle.isEmpty() || !newNoteBody.isEmpty()))
            createNote(newNoteTitle, newNoteBody);
        else if (oldNote != null &&
                (!oldNote.getNoteBody().equals(newNoteBody) || !oldNote.getNoteTitle().equals(newNoteTitle)))
            updateNote(newNoteTitle, newNoteBody);
    }

    /**
     * Create a new note with the provided title and body.
     *
     * @param noteTitle the title of the new note
     * @param noteBody  the body of the new note
     */
    private void createNote(String noteTitle, String noteBody) {
        notesRepository.createNote(noteTitle, noteBody);
    }

    /**
     * Update the existing note with the provided title and body.
     *
     * @param noteTitle the new title of the note
     * @param noteBody  the new body of the note
     */
    private void updateNote(String noteTitle, String noteBody) {
        if (noteLiveData.getValue() != null)
            notesRepository.updateNote(noteTitle, noteBody, noteLiveData.getValue().getId());
    }

    /**
     * Delete the currently opened note.
     */
    public void deleteNote() {
        if (noteLiveData.getValue() != null)
            notesRepository.deleteNote(noteLiveData.getValue());
    }


    /**
     * Factory class for creating instances of {@link NoteViewModel}.
     */
    public static class NoteViewModelFactory implements ViewModelProvider.Factory {
        private final NotesRepository repository;

        public NoteViewModelFactory(NotesRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(NoteViewModel.class)) {
                return (T) new NoteViewModel(repository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
