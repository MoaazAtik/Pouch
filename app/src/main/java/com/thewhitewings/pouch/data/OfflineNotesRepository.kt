package com.thewhitewings.pouch.data

import com.thewhitewings.pouch.utils.DateTimeFormatType
import com.thewhitewings.pouch.utils.DateTimeUtils.getFormattedDateTime
import com.thewhitewings.pouch.utils.Zone
import kotlinx.coroutines.flow.Flow
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

    override suspend fun createNote(note: Note) {
        currentZoneDao.insert(
            with(note) {
                Note(
                    noteTitle = noteTitle,
                    noteBody = noteBody,
                )
            }
        )
    }

    override fun getNoteById(noteId: Int): Flow<Note?> {
        return currentZoneDao.getNoteById(noteId).map { note ->
            note?.let {
                Note(
                    id = it.id,
                    noteTitle = it.noteTitle,
                    noteBody = it.noteBody,
                    timestamp = getFormattedDateTime(DateTimeFormatType.UTC_TO_LOCAL, it.timestamp)
                )
            }
        }
    }

    override fun getAllNotesStream(sortOption: SortOption): Flow<List<Note>> {
        return currentZoneDao.getAllNotes(sortOption.name)
    }

    override fun searchNotesStream(searchQuery: String, sortOption: SortOption): Flow<List<Note>> {
        return currentZoneDao.searchNotes(searchQuery, sortOption.name)
    }

    override suspend fun updateNote(updatedNote: Note) {
        currentZoneDao.updateNote(
            with(updatedNote) {
                Note(
                    id = id,
                    noteTitle = noteTitle,
                    noteBody = noteBody,
                    timestamp = getFormattedDateTime(DateTimeFormatType.CURRENT_UTC)
                )
            }
        )
    }

    override suspend fun deleteNote(note: Note) {
        currentZoneDao.deleteNote(note)
    }

    override suspend fun saveSortOption(
        sortOption: SortOption,
        zone: Zone
    ) {
        pouchPreferences.saveSortOption(sortOption, zone)
    }

    override fun getSortOptionFlow(zone: Zone): Flow<SortOption> {
        return pouchPreferences.getSortOptionFlow(zone)
    }

    override fun toggleZone() {
        currentZoneDao =
            if (currentZoneDao == creativeNoteDao)
                bomNoteDao
            else creativeNoteDao
    }
}