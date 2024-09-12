package com.thewhitewings.pouch.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.thewhitewings.pouch.utils.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert
    suspend fun insert(note: Note)

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

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("""
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
        """)
    fun searchNotes(searchQuery: String, sortOptionName: String): Flow<List<Note>>
}