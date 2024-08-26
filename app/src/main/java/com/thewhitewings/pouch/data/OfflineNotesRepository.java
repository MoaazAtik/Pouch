package com.thewhitewings.pouch.data;

import static com.thewhitewings.pouch.utils.DateTimeUtils.getFormattedDateTime;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.thewhitewings.pouch.utils.Zone;
import com.thewhitewings.pouch.utils.DateTimeFormatType;

import java.util.ArrayList;
import java.util.List;

public class OfflineNotesRepository implements NotesRepository, DatabaseChangeListener {

    private static final String TAG = "OfflineNotesRepository";
    private final DatabaseHelper mainDatabaseHelper;
    private final DatabaseHelper bomDatabaseHelper;
    private DatabaseHelper currentZoneDatabaseHelper;
    private final PouchPreferences pouchPreferences;
    private final MutableLiveData<List<Note>> notesLiveData;
    private Zone currentZone;

    public OfflineNotesRepository(DatabaseHelper mainDatabaseHelper, DatabaseHelper bomDatabaseHelper, PouchPreferences pouchPreferences) {
        this.mainDatabaseHelper = mainDatabaseHelper;
        this.bomDatabaseHelper = bomDatabaseHelper;
        currentZoneDatabaseHelper = mainDatabaseHelper;
        this.pouchPreferences = pouchPreferences;

        mainDatabaseHelper.setDatabaseChangeListener(this);
        bomDatabaseHelper.setDatabaseChangeListener(this);

        currentZone = Zone.MAIN;
        notesLiveData = new MutableLiveData<>(new ArrayList<>());
        updateNotesLiveData(
                currentZoneDatabaseHelper.getAllNotes(
                        pouchPreferences.getSortOption(currentZone)
                )
        );
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
        updatedNote.setTimestamp(getFormattedDateTime(DateTimeFormatType.CURRENT_LOCAL, null));

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
    public void sortNotes(SortOption sortOption, String searchQuery) {
        updateNotesLiveData(
                searchQuery.isEmpty() ?
                        currentZoneDatabaseHelper.getAllNotes(sortOption) :
                        currentZoneDatabaseHelper.searchNotes(searchQuery, sortOption)
        );
    }

    @Override
    public void onDatabaseChanged() {
        updateNotesLiveData(
                currentZoneDatabaseHelper.getAllNotes(
                        pouchPreferences.getSortOption(currentZone)
                )
        );
    }

    @Override
    public void saveSortOption(SortOption sortOption, Zone zone) {
        pouchPreferences.saveSortOption(sortOption, zone);
    }

    @Override
    public SortOption getSortOption(Zone zone) {
        return pouchPreferences.getSortOption(zone);
    }

    private void updateNotesLiveData(List<Note> notes) {
        notesLiveData.postValue(notes);
    }

    public void toggleZone(Zone newZone) {
        if (currentZone == Zone.MAIN) {
            currentZone = Zone.BOX_OF_MYSTERIES;
            currentZoneDatabaseHelper = bomDatabaseHelper;
        } else {
            currentZone = Zone.MAIN;
            currentZoneDatabaseHelper = mainDatabaseHelper;
        }

        updateNotesLiveData(
                currentZoneDatabaseHelper.getAllNotes(
                        pouchPreferences.getSortOption(currentZone)
                )
        );
    }
}

