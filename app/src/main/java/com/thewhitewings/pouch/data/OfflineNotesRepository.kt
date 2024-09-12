package com.thewhitewings.pouch.data

import android.util.Log
import com.thewhitewings.pouch.utils.DateTimeFormatType
import com.thewhitewings.pouch.utils.DateTimeUtils.getFormattedDateTime
import com.thewhitewings.pouch.utils.Zone
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

private const val TAG = "OfflineNotesRepository"

/**
 * Offline Notes Repository Class
 */
class OfflineNotesRepository(
    private val creativeNoteDao: NoteDao,
    private val bomNoteDao: NoteDao,
    private val pouchPreferences: PouchPreferences
) : NotesRepository {

    private var currentZoneDao: NoteDao = creativeNoteDao

    override suspend fun createNote(noteTitle: String, noteBody: String) {
        currentZoneDao.insert(
            Note(noteTitle = noteTitle, noteBody = noteBody)
        )
    }

    override suspend fun updateNote(newNoteTitle: String, newNoteBody: String, oldNote: Note) {
        val updatedNote = oldNote.copy(
            noteTitle = newNoteTitle,
            noteBody = newNoteBody,
            timestamp = getFormattedDateTime(DateTimeFormatType.CURRENT_UTC)
        )

        currentZoneDao.updateNote(updatedNote)
    }

    override suspend fun deleteNote(note: Note) {
        currentZoneDao.deleteNote(note)
    }

    override fun toggleZone() {
        currentZoneDao =
            if (currentZoneDao == creativeNoteDao)
                bomNoteDao
            else creativeNoteDao
    }

    override fun getAllNotesStream(sortOption: SortOption): Flow<List<Note>> {
        return currentZoneDao.getAllNotes(sortOption.name)
    }

    override fun searchNotesStream(searchQuery: String, sortOption: SortOption): Flow<List<Note>> {
        return currentZoneDao.searchNotes(searchQuery, sortOption.name)
    }

    override suspend fun saveSortOption(sortOption: SortOption, zone: Zone) { // rename to changeSortOption
        pouchPreferences.saveSortOption(sortOption, zone)
    }

    override fun getSortOptionFlow(zone: Zone): Flow<SortOption> {
        return pouchPreferences.getSortOptionFlow(zone)
    }

}

