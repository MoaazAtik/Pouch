package com.thewhitewings.pouch.data

import com.thewhitewings.pouch.utils.Zone
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Notes Repository
 */
interface NotesRepository {

    /**
     * Create a new note in the database
     */
    suspend fun createNote(note: Note)

    fun getNoteById(noteId: Int): Flow<Note?>

    fun getAllNotesStream(sortOption: SortOption): Flow<List<Note>>

    fun searchNotesStream(searchQuery: String, sortOption: SortOption): Flow<List<Note>>

    /**
     * Update an existing note in the database
     */
    suspend fun updateNote(updatedNote: Note)

    /**
     * Delete a note from the database
     *
     * @param note Note to be deleted
     */
    suspend fun deleteNote(note: Note)

    /**
     * Save the Sort Option in DataStore
     *
     * @param sortOption to be saved
     * @param zone       current zone
     */
    suspend fun saveSortOption(sortOption: SortOption, zone: Zone)

    fun getSortOptionFlow(zone: Zone): Flow<SortOption>

    fun toggleZone()
}
