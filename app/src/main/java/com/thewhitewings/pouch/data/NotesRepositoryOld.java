package com.thewhitewings.pouch.data;

import androidx.lifecycle.LiveData;

import com.thewhitewings.pouch.utils.Zone;

import java.util.List;

/**
 * Interface for the Notes Repository
 */
public interface NotesRepositoryOld {

    /**
     * Get all notes from the database
     *
     * @return LiveData of a list of notes
     */
    LiveData<List<Note>> getAllNotes();

    /**
     * Create a new note in the database
     *
     * @param noteTitle Title of the note
     * @param noteBody  Body of the note
     */
    void createNote(String noteTitle, String noteBody);

    /**
     * Update an existing note in the database
     *
     * @param newNoteTitle New title of the note
     * @param newNoteBody  New body of the note
     * @param oldNote      Old note to be updated
     */
    void updateNote(String newNoteTitle, String newNoteBody, Note oldNote);

    /**
     * Delete a note from the database
     *
     * @param note Note to be deleted
     */
    void deleteNote(Note note);

    /**
     * Search Notes by Note title or body
     *
     * @param searchQuery Note title and/or body
     * @param sortOption  to be used for sorting the results
     */
    void searchNotes(String searchQuery, SortOption sortOption);

    /**
     * Sort filtered Notes by Note title and/or body
     *
     * @param sortOption  to be used for sorting the results
     * @param searchQuery Note title and/or body
     */
    void sortNotes(SortOption sortOption, String searchQuery);

    /**
     * Toggle between Zones {@link Zone#CREATIVE} and {@link Zone#BOX_OF_MYSTERIES}
     *
     * @param newZone New Zone to be used
     */
    void toggleZone(Zone newZone);

    /**
     * Save the Sort Option in SharedPreferences
     *
     * @param sortOption to be saved
     * @param zone       current zone
     */
    void saveSortOption(SortOption sortOption, Zone zone);

    /**
     * Get the Sort Option from SharedPreferences
     *
     * @param zone current zone
     * @return Sort Option
     */
    SortOption getSortOption(Zone zone);
}
