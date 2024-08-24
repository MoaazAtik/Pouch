package com.thewhitewings.pouch.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.thewhitewings.pouch.Constants;

import java.util.List;

public class OfflineNotesRepository implements NotesRepository, DatabaseChangeListener {

    private final DatabaseHelper mainDatabaseHelper;
    private final DatabaseHelper bomDatabaseHelper;
    private DatabaseHelper currentZoneDatabaseHelper;
    private final PouchPreferences pouchPreferences;
    private final MutableLiveData<List<Note>> notesLiveData;

    public OfflineNotesRepository(DatabaseHelper mainDatabaseHelper, DatabaseHelper bomDatabaseHelper, PouchPreferences pouchPreferences) {
        this.mainDatabaseHelper = mainDatabaseHelper;
        this.bomDatabaseHelper = bomDatabaseHelper;
        currentZoneDatabaseHelper = mainDatabaseHelper;
        this.pouchPreferences = pouchPreferences;

        mainDatabaseHelper.setDatabaseChangeListener(this);
        bomDatabaseHelper.setDatabaseChangeListener(this);
        notesLiveData = new MutableLiveData<>(currentZoneDatabaseHelper.getAllNotes());
    }

    @Override
    public LiveData<List<Note>> getAllNotes() {
        return notesLiveData;
    }

    @Override
    public void createNote(String noteTitle, String noteBody) {
        currentZoneDatabaseHelper.createNote(noteTitle, noteBody);
    }

    @Override
    public void updateNote(String newNoteTitle, String noteBody, Note oldNote) {
        Note updatedNote = new Note();
        updatedNote.setId(oldNote.getId());
        updatedNote.setNoteTitle(newNoteTitle);
        updatedNote.setNoteBody(noteBody);
        updatedNote.setTimestamp(currentZoneDatabaseHelper.getFormattedDateTime(Constants.CURRENT_LOCAL, null));

        currentZoneDatabaseHelper.updateNote(updatedNote);
    }

    @Override
    public void deleteNote(Note note) {
        currentZoneDatabaseHelper.deleteNote(note);
    }

    @Override
    public void searchNotes(String searchQuery, SortOption sortOption) {
        updateNotesLiveData(
                currentZoneDatabaseHelper.searchNotes(searchQuery, sortOption)
        );
    }

    @Override
    public void sortNotes(SortOption sortOption) {
        updateNotesLiveData(
                currentZoneDatabaseHelper.sortNotes(sortOption)
        );
    }

    @Override
    public void onDatabaseChanged() {
        updateNotesLiveData(currentZoneDatabaseHelper.getAllNotes());
    }

    @Override
    public void saveSortOption(SortOption sortOption, Constants.Zone zone) {
        pouchPreferences.saveSortOption(sortOption, zone);
    }

    @Override
    public SortOption getSortOption(Constants.Zone zone) {
        return pouchPreferences.getSortOption(zone);
    }

    private void updateNotesLiveData(List<Note> notes) {
        notesLiveData.postValue(notes);
    }

    public void toggleZone(Constants.Zone newZone) {
        if (newZone == Constants.Zone.BOX_OF_MYSTERIES) {
            currentZoneDatabaseHelper = bomDatabaseHelper;
        } else {
            currentZoneDatabaseHelper = mainDatabaseHelper;
        }

        updateNotesLiveData(currentZoneDatabaseHelper.getAllNotes());
    }
}

