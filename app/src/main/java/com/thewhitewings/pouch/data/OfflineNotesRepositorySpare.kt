package com.thewhitewings.pouch.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.thewhitewings.pouch.utils.DateTimeFormatType
import com.thewhitewings.pouch.utils.DateTimeUtils.getFormattedDateTime
import com.thewhitewings.pouch.utils.Zone

/**
 * Offline Notes Repository Class
 */
class OfflineNotesRepositorySpare(
    private val creativeDatabaseHelper: DatabaseHelper, // database helper that interacts with the database of the creative zone
    private val bomDatabaseHelper: DatabaseHelper, // database helper that interacts with the database of the box of mysteries zone
    pouchPreferences: PouchPreferences
) : NotesRepositorySpare, DatabaseChangeListener {
    // database helper that interacts with the current zone database
    private var currentZoneDatabaseHelper: DatabaseHelper

    // pouch preferences
    private val pouchPreferences: PouchPreferences

    // live data of notes
    private val notesLiveData: MutableLiveData<List<Note>>

    // current zone
    private var currentZone: Zone

    init {
        currentZoneDatabaseHelper = creativeDatabaseHelper
        this.pouchPreferences = pouchPreferences

        creativeDatabaseHelper.setDatabaseChangeListener(this)
        bomDatabaseHelper.setDatabaseChangeListener(this)

        currentZone = Zone.CREATIVE
        notesLiveData = MutableLiveData(ArrayList())
        updateNotesLiveData(
            currentZoneDatabaseHelper.getAllNotes(
//                pouchPreferences.getSortOption(currentZone)
            )
        )
    }

    override val allNotes: LiveData<List<Note>>
        get() = notesLiveData

    override fun createNote(noteTitle: String?, noteBody: String?) {
        currentZoneDatabaseHelper.createNote(noteTitle, noteBody)
    }

    override fun updateNote(newNoteTitle: String?, newNoteBody: String?, oldNote: Note?) {
        val updatedNote = Note(
            oldNote!!.id,
            newNoteTitle!!,
            newNoteBody!!,
            getFormattedDateTime(DateTimeFormatType.CURRENT_LOCAL, "")
        )

        currentZoneDatabaseHelper.updateNote(updatedNote)
    }

    override fun deleteNote(note: Note?) {
        currentZoneDatabaseHelper.deleteNote(note)
    }

    override fun searchNotes(searchQuery: String?, sortOption: SortOption?) {
        updateNotesLiveData(
            currentZoneDatabaseHelper.searchNotes(searchQuery, sortOption)
        )
    }

    override fun sortNotes(sortOption: SortOption?, searchQuery: String?) {
        updateNotesLiveData(
            if (searchQuery!!.isEmpty()) currentZoneDatabaseHelper.getAllNotes(sortOption) else currentZoneDatabaseHelper.searchNotes(
                searchQuery,
                sortOption
            )
        )
    }

    override fun onDatabaseChanged() {
        updateNotesLiveData(
            currentZoneDatabaseHelper.getAllNotes(
//                pouchPreferences.getSortOption(currentZone)
            )
        )
    }

    override fun saveSortOption(sortOption: SortOption?, zone: Zone?) {
        pouchPreferences.saveSortOptionBlocking(sortOption!!, zone!!)
    }

    override fun getSortOption(zone: Zone?): SortOption? {
//        return pouchPreferences.getSortOption(zone!!)
        return null
    }

    /**
     * Updates the notes live data with the given list of notes.
     *
     * @param notes the new list of notes
     */
    private fun updateNotesLiveData(notes: List<Note>) {
        notesLiveData.postValue(notes)
    }

    override fun toggleZone(newZone: Zone?) {
        if (currentZone == Zone.CREATIVE) {
            currentZone = Zone.BOX_OF_MYSTERIES
            currentZoneDatabaseHelper = bomDatabaseHelper
        } else {
            currentZone = Zone.CREATIVE
            currentZoneDatabaseHelper = creativeDatabaseHelper
        }

        updateNotesLiveData(
            currentZoneDatabaseHelper.getAllNotes(
//                pouchPreferences.getSortOption(currentZone)
            )
        )
    }

    companion object {
        private const val TAG = "OfflineNotesRepository"
    }
}

