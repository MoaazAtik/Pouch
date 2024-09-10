package com.thewhitewings.pouch.data;

import static com.thewhitewings.pouch.utils.DateTimeUtils.getFormattedDateTime;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.thewhitewings.pouch.utils.DateTimeFormatType;
import com.thewhitewings.pouch.utils.Zone;

import java.util.ArrayList;
import java.util.List;

/**
 * Offline Notes Repository Class
 */
public class OfflineNotesRepositoryOld implements NotesRepositoryOld, DatabaseChangeListener {

    private static final String TAG = "OfflineNotesRepository";

    // database helper that interacts with the database of the creative zone
    private final DatabaseHelper creativeDatabaseHelper;

    // database helper that interacts with the database of the box of mysteries zone
    private final DatabaseHelper bomDatabaseHelper;

    // database helper that interacts with the current zone database
    private DatabaseHelper currentZoneDatabaseHelper;

    // pouch preferences
    private final PouchPreferences pouchPreferences;

    // live data of notes
    private final MutableLiveData<List<Note>> notesLiveData;

    // current zone
    private Zone currentZone;

    public OfflineNotesRepositoryOld(DatabaseHelper creativeDatabaseHelper, DatabaseHelper bomDatabaseHelper, PouchPreferences pouchPreferences) {
        this.creativeDatabaseHelper = creativeDatabaseHelper;
        this.bomDatabaseHelper = bomDatabaseHelper;
        currentZoneDatabaseHelper = creativeDatabaseHelper;
        this.pouchPreferences = pouchPreferences;

        creativeDatabaseHelper.setDatabaseChangeListener(this);
        bomDatabaseHelper.setDatabaseChangeListener(this);

        currentZone = Zone.CREATIVE;
        notesLiveData = new MutableLiveData<>(new ArrayList<>());
        updateNotesLiveData(
                currentZoneDatabaseHelper.getAllNotes(
//                        pouchPreferences.getSortOption(currentZone)
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
    public void updateNote(String newNoteTitle, String newNoteBody, Note oldNote) {
        Note updatedNote = new Note(
                oldNote.getId(),
                newNoteTitle,
                newNoteBody,
                getFormattedDateTime(DateTimeFormatType.CURRENT_LOCAL, "")
        );

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
//                        pouchPreferences.getSortOption(currentZone)
                )
        );
    }

    @Override
    public void saveSortOption(SortOption sortOption, Zone zone) {
        pouchPreferences.saveSortOptionBlocking(sortOption, zone);
    }

    @Override
    public SortOption getSortOption(Zone zone) {
//        return pouchPreferences.getSortOption(zone);
        return SortOption.Z_A;
    }

    /**
     * Updates the notes live data with the given list of notes.
     *
     * @param notes the new list of notes
     */
    private void updateNotesLiveData(List<Note> notes) {
        notesLiveData.postValue(notes);
    }

    @Override
    public void toggleZone(Zone newZone) {
        if (currentZone == Zone.CREATIVE) {
            currentZone = Zone.BOX_OF_MYSTERIES;
            currentZoneDatabaseHelper = bomDatabaseHelper;
        } else {
            currentZone = Zone.CREATIVE;
            currentZoneDatabaseHelper = creativeDatabaseHelper;
        }

        updateNotesLiveData(
                currentZoneDatabaseHelper.getAllNotes(
//                        pouchPreferences.getSortOption(currentZone)
                )
        );
    }
}

