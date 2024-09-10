package com.thewhitewings.pouch.data

import androidx.lifecycle.LiveData
import com.thewhitewings.pouch.utils.Zone

/**
 * Interface for the Notes Repository
 */
interface NotesRepositorySpare {

    /**
     * Get all notes from the database
     *
     * @return LiveData of a list of notes
     */
//    @JvmField
//    val allNotes: LiveData<List<Note?>?>?
    val allNotes: LiveData<List<Note>>

    /**
     * Create a new note in the database
     *
     * @param noteTitle Title of the note
     * @param noteBody  Body of the note
     */
    fun createNote(noteTitle: String?, noteBody: String?)

    /**
     * Update an existing note in the database
     *
     * @param newNoteTitle New title of the note
     * @param newNoteBody  New body of the note
     * @param oldNote      Old note to be updated
     */
    fun updateNote(newNoteTitle: String?, newNoteBody: String?, oldNote: Note?)

    /**
     * Delete a note from the database
     *
     * @param note Note to be deleted
     */
    fun deleteNote(note: Note?)

    /**
     * Search Notes by Note title or body
     *
     * @param searchQuery Note title and/or body
     * @param sortOption  to be used for sorting the results
     */
    fun searchNotes(searchQuery: String?, sortOption: SortOption?)

    /**
     * Sort filtered Notes by Note title and/or body
     *
     * @param sortOption  to be used for sorting the results
     * @param searchQuery Note title and/or body
     */
    fun sortNotes(sortOption: SortOption?, searchQuery: String?)

    /**
     * Toggle between Zones [Zone.CREATIVE] and [Zone.BOX_OF_MYSTERIES]
     *
     * @param newZone New Zone to be used
     */
    fun toggleZone(newZone: Zone?)

    /**
     * Save the Sort Option in SharedPreferences
     *
     * @param sortOption to be saved
     * @param zone       current zone
     */
    fun saveSortOption(sortOption: SortOption?, zone: Zone?)

    /**
     * Get the Sort Option from SharedPreferences
     *
     * @param zone current zone
     * @return Sort Option
     */
    fun getSortOption(zone: Zone?): SortOption?
}
