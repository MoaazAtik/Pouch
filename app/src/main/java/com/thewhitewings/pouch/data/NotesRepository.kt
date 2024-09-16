package com.thewhitewings.pouch.data

import com.thewhitewings.pouch.utils.Zone
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Notes Repository
 */
interface NotesRepository {

    /**
     * Create a new note in the database
     *
     * @param noteTitle Title of the note
     * @param noteBody  Body of the note
     */
    suspend fun createNote(note: Note)

    /**
     * Update an existing note in the database
     *
     * @param newNoteTitle New title of the note
     * @param newNoteBody  New body of the note
     * @param oldNote      Old note to be updated
     */
    suspend fun updateNote(updatedNote: Note)

    /**
     * Delete a note from the database
     *
     * @param note Note to be deleted
     */
    suspend fun deleteNote(note: Note)

    fun getNoteById(noteId: Int): Flow<Note?>

    fun toggleZone()

    fun getAllNotesStream(sortOption: SortOption): Flow<List<Note>>

    fun searchNotesStream(searchQuery: String, sortOption: SortOption): Flow<List<Note>>

    /**
     * Save the Sort Option in SharedPreferences
     *
     * @param sortOption to be saved
     * @param zone       current zone
     */
    suspend fun saveSortOption(sortOption: SortOption, zone: Zone)

//    val sortOptionFlow: Flow<SortOption>

    fun getSortOptionFlow(zone: Zone): Flow<SortOption>
}
