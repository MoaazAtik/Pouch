package com.thewhitewings.pouch.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thewhitewings.pouch.utils.Constants.TABLE_NAME
import com.thewhitewings.pouch.utils.Constants.COLUMN_ID
import com.thewhitewings.pouch.utils.Constants.COLUMN_NOTE_TITLE
import com.thewhitewings.pouch.utils.Constants.COLUMN_NOTE_BODY
import com.thewhitewings.pouch.utils.Constants.COLUMN_TIMESTAMP
import com.thewhitewings.pouch.utils.DateTimeFormatType
import com.thewhitewings.pouch.utils.DateTimeUtils
import com.thewhitewings.pouch.utils.DateTimeUtils.DEFAULT_FORMAT
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = TABLE_NAME)
data class Note(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int = 0,

    @ColumnInfo(name = COLUMN_NOTE_TITLE)
    val noteTitle: String = "",

    @ColumnInfo(name = COLUMN_NOTE_BODY)
    val noteBody: String = "",

    @ColumnInfo(name = COLUMN_TIMESTAMP)
    val timestamp: String = DateTimeUtils.getFormattedDateTime(DateTimeFormatType.CURRENT_UTC)
){

    override fun toString(): String {
        return "Note{" +
                "id=" + id +
                ", noteTitle='" + noteTitle + '\'' +
                ", noteBody='" + noteBody + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}'
    }

    /**
     * Checks if two notes are equal based on their content.
     *
     * @param note The note to compare with.
     * @return True if the notes are equal, false otherwise.
     */
    fun equalContent(note: Note): Boolean {
        return id == note.id &&
                noteTitle == note.noteTitle &&
                noteBody == note.noteBody &&
                timestamp == note.timestamp
    }
}
