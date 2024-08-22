package com.thewhitewings.pouch.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.thewhitewings.pouch.Constants;

import java.util.List;

public class OfflineNotesRepository implements NotesRepository, DatabaseChangeListener {

    private final DatabaseHelper databaseHelper;
    private final MutableLiveData<List<Note>> notesLiveData;

    public OfflineNotesRepository(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        this.notesLiveData = new MutableLiveData<>(databaseHelper.getAllNotes());
        databaseHelper.setDatabaseChangeListener(this);
    }

    @Override
    public LiveData<List<Note>> getAllNotes() {
        return notesLiveData;
    }

    @Override
    public void createNote(String noteTitle, String noteBody) {
        databaseHelper.createNote(noteTitle, noteBody);
    }

    @Override
    public void updateNote(String newNoteTitle, String noteBody, Note oldNote) {
        Note updatedNote = new Note();
        updatedNote.setId(oldNote.getId());
        updatedNote.setNoteTitle(newNoteTitle);
        updatedNote.setNoteBody(noteBody);
        updatedNote.setTimestamp(databaseHelper.getFormattedDateTime(Constants.CURRENT_LOCAL, null));

        databaseHelper.updateNote(updatedNote);
    }

    @Override
    public void deleteNote(Note note) {
        databaseHelper.deleteNote(note);
    }

    @Override
    public void onDatabaseChanged() {
        notesLiveData.postValue(databaseHelper.getAllNotes());
    }
}

