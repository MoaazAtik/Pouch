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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getNotesFlow(
        sortOptionFlow: Flow<SortOption>,
        searchQueryFlow: Flow<String>,
        currentZoneFlow: Flow<Zone>
    ): Flow<List<Note>> {

        return combine(sortOptionFlow, searchQueryFlow, currentZoneFlow) { sortOption, searchQuery, zone ->
            Triple(sortOption, searchQuery, zone)
        }.flatMapLatest { (sortOption, searchQuery, zone) ->

            // Select the appropriate DAO based on the current zone
            currentZoneDao =
                if (zone == Zone.CREATIVE)
                    creativeNoteDao
                else bomNoteDao

            Log.d(TAG, "getNotesFlow: zone $zone, sortOption $sortOption, searchQuery $searchQuery, searchQuery.isEmpty() ${searchQuery.isEmpty()}")
            Log.d(TAG, "sortOption.toSqlString() ${sortOption.toSqlString()}")
            currentZoneDao.getAllNotes(sortOption.toSqlString()).map { notes ->
                notes.forEach { note ->
                    Log.d(TAG, "   note $note")
                }
            }

            // Fetch notes from the selected DAO based on the sortOption and searchQuery
            if (searchQuery.isEmpty())
                currentZoneDao.getAllNotes(sortOption.toSqlString())
            else
                currentZoneDao.searchNotes(searchQuery, sortOption.toSqlString())
        }
    }


    override suspend fun saveSortOption(sortOption: SortOption, zone: Zone) { // rename to changeSortOption
        Log.d(TAG, "saveSortOption: sortOption $sortOption, zone $zone")
        pouchPreferences.saveSortOption(sortOption, zone)
    }
    override val sortOptionFlow: Flow<SortOption> = pouchPreferences.sortOptionFlow

    override fun getSortOptionFlow(zone: Zone): Flow<SortOption> {
        return pouchPreferences.getSortOptionFlow(zone)
    }

}

