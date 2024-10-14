package com.thewhitewings.pouch.feature_note.data.repository

import com.thewhitewings.pouch.feature_note.domain.model.Note
import com.thewhitewings.pouch.feature_note.domain.repository.OfflineNotesRepository
import com.thewhitewings.pouch.feature_note.domain.repository.PouchPreferences
import com.thewhitewings.pouch.feature_note.domain.util.SortOption
import com.thewhitewings.pouch.feature_note.domain.model.formatTimestamp
import com.thewhitewings.pouch.feature_note.data.data_source.NoteDao
import com.thewhitewings.pouch.feature_note.util.DateTimeFormatType
import com.thewhitewings.pouch.feature_note.util.Zone
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TAG = "OfflineNotesRepositoryImpl"

/**
 * Offline Notes Repository Class.
 * Implementation of [OfflineNotesRepository] interface.
 *
 * It is the gate to interact with the Room databases and the DataStore.
 */
class OfflineNotesRepositoryImpl(

    /**
     * DAO for the Creative Zone database.
     */
    private val creativeNoteDao: NoteDao,

    /**
     * DAO for the Box of Mysteries Zone database.
     */
    private val bomNoteDao: NoteDao,

    /**
     * Preferences DataStore for the app.
     */
    private val pouchPreferences: PouchPreferences
) : OfflineNotesRepository {

    /**
     * The DAO for the database of the zone that is currently being used.
     */
    private var currentZoneDao: NoteDao = creativeNoteDao

    /**
     * Insert a new note into the database.
     * @param note The note to be inserted.
     */
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

    /**
     * Get a note by its ID from the database.
     * @param noteId The ID of the note to retrieve.
     * @return A flow of the note with the specified ID.
     */
    override fun getNoteStream(noteId: Int): Flow<Note?> {
        return currentZoneDao.getNoteStream(noteId).map { note ->
            note?.formatTimestamp(DateTimeFormatType.UTC_TO_LOCAL)
        }
    }

    /**
     * Get all notes from the database sorted by the specified sort option.
     * @param sortOption The [SortOption] to use for sorting the notes.
     */
    override fun getAllNotesStream(sortOption: SortOption): Flow<List<Note>> {
        return currentZoneDao.getAllNotes(sortOption.name).map { notes ->
            notes.map { note -> note.formatTimestamp(DateTimeFormatType.UTC_TO_LOCAL) }
        }
    }

    /**
     * Search for notes that match the specified search query and sort option.
     * @param searchQuery The search query to use.
     * @param sortOption  The [SortOption] to use for sorting the notes.
     * @return A flow of a filtered and sorted list of notes.
     */
    override fun searchNotesStream(searchQuery: String, sortOption: SortOption): Flow<List<Note>> {
        return currentZoneDao.searchNotes(searchQuery, sortOption.name).map { notes ->
            notes.map { note -> note.formatTimestamp(DateTimeFormatType.UTC_TO_LOCAL) }
        }
    }

    /**
     * Update an existing note in the database.
     * @param updatedNote The updated note to be saved.
     */
    override suspend fun updateNote(updatedNote: Note) {
        currentZoneDao.updateNote(
            updatedNote.formatTimestamp(DateTimeFormatType.CURRENT_UTC)
        )
    }

    /**
     * Delete a note from the database.
     * @param note The note to be deleted.
     */
    override suspend fun deleteNote(note: Note) {
        currentZoneDao.deleteNote(note)
    }

    /**
     * Save the [SortOption] preference in DataStore for the provided zone.
     * @param sortOption The preference to be saved.
     * @param zone       The current [Zone].
     */
    override suspend fun saveSortOption(
        sortOption: SortOption,
        zone: Zone
    ) {
        pouchPreferences.saveSortOption(sortOption, zone)
    }

    /**
     * Get the [SortOption] Stream from DataStore for the provided zone.
     * @param zone The current [Zone].
     * @return A flow of stored sort option.
     */
    override fun getSortOptionStream(zone: Zone): Flow<SortOption> {
        return pouchPreferences.getSortOptionStream(zone)
    }

    /**
     * Toggle the current [Zone].
     */
    override fun toggleZone() {
        currentZoneDao =
            if (currentZoneDao == creativeNoteDao)
                bomNoteDao
            else creativeNoteDao
    }
}