package com.thewhitewings.pouch.feature_note.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thewhitewings.pouch.feature_note.data.util.Constants.COLUMN_ID
import com.thewhitewings.pouch.feature_note.data.util.Constants.COLUMN_NOTE_BODY
import com.thewhitewings.pouch.feature_note.data.util.Constants.COLUMN_NOTE_TITLE
import com.thewhitewings.pouch.feature_note.data.util.Constants.COLUMN_TIMESTAMP
import com.thewhitewings.pouch.feature_note.data.util.Constants.TABLE_NAME
import com.thewhitewings.pouch.feature_note.util.DateTimeFormatType
import com.thewhitewings.pouch.feature_note.util.DateTimeUtils

/**
 * Entity model represents a single row (a note) in the database.
 */
@Entity(tableName = TABLE_NAME)
data class Note(

    /**
     * Primary key is the unique ID of the note.
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int = 0,

    /**
     * Title of the note.
     */
    @ColumnInfo(name = COLUMN_NOTE_TITLE)
    val noteTitle: String = "",

    /**
     * Body of the note.
     */
    @ColumnInfo(name = COLUMN_NOTE_BODY)
    val noteBody: String = "",

    /**
     * Timestamp of the note.
     *
     * **Note:**
     * Date and time are stored in the Database in UTC, and presented to the UI in the local time zone.
     */
    @ColumnInfo(name = COLUMN_TIMESTAMP)
    val timestamp: String = DateTimeUtils.getFormattedDateTime(DateTimeFormatType.CURRENT_UTC)
)

/**
 * Empty note object.
 *
 * It has zeros and empty strings for all corresponding properties of the [Note].
 */
val EMPTY_NOTE = Note(timestamp = "")

/**
 * Extension function to format the timestamp of a note.
 * @param formatType The [DateTimeFormatType] to use for formatting the timestamp.
 * @return The note with the formatted timestamp.
 */
fun Note.formatTimestamp(formatType: DateTimeFormatType): Note {
    return Note(
        id = id,
        noteTitle = noteTitle,
        noteBody = noteBody,
        timestamp = DateTimeUtils.getFormattedDateTime(formatType, timestamp)
    )
}