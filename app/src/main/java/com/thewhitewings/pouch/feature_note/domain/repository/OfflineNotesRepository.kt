package com.thewhitewings.pouch.feature_note.domain.repository

import com.thewhitewings.pouch.feature_note.domain.util.SortOption
import com.thewhitewings.pouch.feature_note.domain.model.Note
import com.thewhitewings.pouch.feature_note.util.Zone
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Notes Repository
 */
interface OfflineNotesRepository {

    /**
     * Insert a new note into the database
     * @param note The note to be inserted
     */
    suspend fun createNote(note: Note)

    /**
     * Get a note by its ID
     * @param noteId ID of the note to be retrieved
     * @return Flow of the note with the specified ID
     */
    fun getNoteStream(noteId: Int): Flow<Note?>

    /**
     * Get all notes from the database sorted by the specified sort option.
     * @param sortOption The [SortOption] to be used for sorting the notes.
     */
    fun getAllNotesStream(sortOption: SortOption): Flow<List<Note>>

    /**
     * Search for notes that match the specified search query and sort option from the database.
     * @param searchQuery The search query to use.
     * @param sortOption  The [SortOption] to be used for sorting the notes.
     * @return A flow of a filtered and sorted list of notes.
     */
    fun searchNotesStream(searchQuery: String, sortOption: SortOption): Flow<List<Note>>

    /**
     * Update an existing note in the database
     * @param updatedNote The updated note to be saved
     */
    suspend fun updateNote(updatedNote: Note)

    /**
     * Delete a note from the database
     * @param note Note to be deleted
     */
    suspend fun deleteNote(note: Note)

    /**
     * Save the [SortOption] preference in DataStore for the provided zone
     * @param sortOption preference to be saved
     * @param zone       current [Zone]
     */
    suspend fun saveSortOption(sortOption: SortOption, zone: Zone)

    /**
     * Get the [SortOption] Stream from DataStore for the provided zone
     * @param zone current [Zone]
     * @return Flow of stored sort option
     */
    fun getSortOptionStream(zone: Zone): Flow<SortOption>

    /**
     * Toggle the current [Zone]
     */
    fun toggleZone()
}
