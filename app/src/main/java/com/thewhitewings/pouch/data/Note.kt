package com.thewhitewings.pouch.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thewhitewings.pouch.utils.Constants.COLUMN_ID
import com.thewhitewings.pouch.utils.Constants.COLUMN_NOTE_BODY
import com.thewhitewings.pouch.utils.Constants.COLUMN_NOTE_TITLE
import com.thewhitewings.pouch.utils.Constants.COLUMN_TIMESTAMP
import com.thewhitewings.pouch.utils.Constants.TABLE_NAME
import com.thewhitewings.pouch.utils.DateTimeFormatType
import com.thewhitewings.pouch.utils.DateTimeUtils

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
)
