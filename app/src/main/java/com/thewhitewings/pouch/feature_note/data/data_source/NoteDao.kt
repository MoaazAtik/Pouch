package com.thewhitewings.pouch.feature_note.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.thewhitewings.pouch.feature_note.domain.model.Note
import com.thewhitewings.pouch.feature_note.data.util.Constants
import kotlinx.coroutines.flow.Flow

/**
 * Data access object (DAO) interface to access the database.
 */
@Dao
interface NoteDao {

    /**
     * Insert a new note into the database.
     * Conflict strategy is set to replace the note with the same ID if it already exists in the database.
     * @param note The note to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    /**
     * Get a note by its ID from the database.
     * @param noteId The ID of the note to retrieve.
     * @return A flow of the note with the specified ID.
     */
    @Query(
        """
        SELECT * FROM ${Constants.TABLE_NAME}
        WHERE ${Constants.COLUMN_ID} = :noteId
        LIMIT 1
        """
    )
    fun getNoteById(noteId: Int): Flow<Note?>

    /**
     * Get all notes sorted by the specified sort option from the database.
     * @param sortOptionName The name of the [SortOption] to use.
     * @return A flow of a sorted list of all notes in the database.
     */
    @Query(
        """
        SELECT * FROM ${Constants.TABLE_NAME}
        ORDER BY 
            CASE WHEN :sortOptionName = 'A_Z' THEN note_title END COLLATE NOCASE ASC,
            CASE WHEN :sortOptionName = 'A_Z' THEN note_body END COLLATE NOCASE ASC,
            
            CASE WHEN :sortOptionName = 'Z_A' THEN note_title END COLLATE NOCASE DESC,
            CASE WHEN :sortOptionName = 'Z_A' THEN note_body END COLLATE NOCASE DESC,
            
            CASE WHEN :sortOptionName = 'OLDEST_FIRST' THEN timestamp END ASC,
            
            CASE WHEN :sortOptionName = 'NEWEST_FIRST' THEN timestamp END DESC
        """
    )
    fun getAllNotes(sortOptionName: String): Flow<List<Note>>

    /**
     * Search for notes that match the specified search query and sort option from the database.
     * @param searchQuery The search query to use.
     * @param sortOptionName The name of the [SortOption] to use.
     * @return A flow of a filtered and sorted list of notes.
     */
    @Query(
        """
        SELECT * FROM ${Constants.TABLE_NAME}
        WHERE ${Constants.COLUMN_NOTE_TITLE} LIKE '%' || :searchQuery || '%' 
        OR ${Constants.COLUMN_NOTE_BODY} LIKE '%' || :searchQuery || '%'
        ORDER BY 
            CASE WHEN :sortOptionName = 'A_Z' THEN note_title END COLLATE NOCASE ASC,
            CASE WHEN :sortOptionName = 'A_Z' THEN note_body END COLLATE NOCASE ASC,
            
            CASE WHEN :sortOptionName = 'Z_A' THEN note_title END COLLATE NOCASE DESC,
            CASE WHEN :sortOptionName = 'Z_A' THEN note_body END COLLATE NOCASE DESC,
            
            CASE WHEN :sortOptionName = 'OLDEST_FIRST' THEN timestamp END ASC,
            
            CASE WHEN :sortOptionName = 'NEWEST_FIRST' THEN timestamp END DESC
        """
    )
    fun searchNotes(searchQuery: String, sortOptionName: String): Flow<List<Note>>

    /**
     * Update an existing note in the database.
     * @param note The updated note to be saved.
     */
    @Update
    suspend fun updateNote(note: Note)

    /**
     * Delete a note from the database.
     * @param note The note to delete.
     */
    @Delete
    suspend fun deleteNote(note: Note)
}