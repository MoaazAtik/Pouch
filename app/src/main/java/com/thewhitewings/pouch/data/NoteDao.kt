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

    @Query("""
        SELECT * FROM ${Constants.TABLE_NAME}
        ORDER BY :sortOptionSqlClause
        """)
    fun getAllNotes(sortOptionSqlClause: String): Flow<List<Note>>

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("""
        SELECT * FROM ${Constants.TABLE_NAME}
        WHERE ${Constants.COLUMN_NOTE_TITLE} LIKE '%' || :searchQuery || '%' 
        OR ${Constants.COLUMN_NOTE_BODY} LIKE '%' || :searchQuery || '%'
        ORDER BY :sortOptionSqlClause
        """)
    fun searchNotes(searchQuery: String, sortOptionSqlClause: String): Flow<List<Note>>
}