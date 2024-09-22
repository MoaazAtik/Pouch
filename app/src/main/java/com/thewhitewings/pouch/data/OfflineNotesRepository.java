package com.thewhitewings.pouch.data;

import static com.thewhitewings.pouch.utils.DateTimeUtils.getFormattedDateTime;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.thewhitewings.pouch.utils.DateTimeFormatType;
import com.thewhitewings.pouch.utils.Zone;

import java.util.ArrayList;
import java.util.List;

/**
 * Offline Notes Repository Class.
 * <p>
 * Implementation of the {@link NotesRepository} interface.
 * </p>
 * <p>
 * It is the gate to interact with the Room databases and the SharedPreferences.
 * </p>
 */
public class OfflineNotesRepository implements NotesRepository, DatabaseChangeListener {

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

    /**
     * Constructor of OfflineNotesRepository
     *
     * @param creativeDatabaseHelper Database Helper of the creative zone
     * @param bomDatabaseHelper      Database Helper of the box of mysteries zone
     * @param pouchPreferences       Pouch Preferences
     */
    public OfflineNotesRepository(
            DatabaseHelper creativeDatabaseHelper,
            DatabaseHelper bomDatabaseHelper,
            PouchPreferences pouchPreferences
    ) {
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
        // Note: notes live data is updated automatically by the database helper
        // when it calls onDatabaseChanged()
    }

    @Override
    public void updateNote(String newNoteTitle, String newNoteBody, Note oldNote) {
        Note updatedNote = new Note();
        updatedNote.setId(oldNote.getId());
        updatedNote.setNoteTitle(newNoteTitle);
        updatedNote.setNoteBody(newNoteBody);
        updatedNote.setTimestamp(getFormattedDateTime(DateTimeFormatType.CURRENT_LOCAL, null));

        currentZoneDatabaseHelper.updateNote(updatedNote);
        // Note: notes live data is updated automatically by the database helper
        // when it calls onDatabaseChanged()
    }

    @Override
    public void deleteNote(Note note) {
        currentZoneDatabaseHelper.deleteNote(note);
        // Note: notes live data is updated automatically by the database helper
        // when it calls onDatabaseChanged()
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
                        pouchPreferences.getSortOption(currentZone)
                )
        );
    }
}
