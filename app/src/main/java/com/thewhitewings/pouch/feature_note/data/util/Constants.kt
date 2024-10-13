package com.thewhitewings.pouch.feature_note.data.util

object Constants {

    // Databases
    /**
     * Database name of Creative zone
     */
    const val CREATIVE_DATABASE_NAME: String = "notes_db"

    /**
     * Database name of BoxOfMysteries zone
     */
    const val BOM_DATABASE_NAME: String = "bom_db"

    // Common database constants
    // version 2 (current)
    /**
     * Current version of both databases
     */
    const val CREATIVE_AND_BOM_DATABASE_VERSION: Int = 2
    /**
     * Database's table name
     */
    const val TABLE_NAME: String = "note"
    /**
     * Database's column name for the ID property
     */
    const val COLUMN_ID: String = "id"
    /**
     * Database's column name for the note title property
     */
    const val COLUMN_NOTE_TITLE: String = "note_title"
    /**
     * Database's column name for the note body property
     */
    const val COLUMN_NOTE_BODY: String = "note_body"
    /**
     * Database's column name for the timestamp property
     */
    const val COLUMN_TIMESTAMP: String = "timestamp"

    // Deprecated database constants
    /**
     * Old version 1 of both databases
     */
    const val CREATIVE_AND_BOM_DATABASE_VERSION_1: Int = 1

    // version 3 (old)
    /**
     * Old version 3 of both databases
     */
    const val CREATIVE_AND_BOM_DATABASE_VERSION_3: Int = 3
    const val TABLE_NAME_VERSION_3: String = "Notes"
    const val COLUMN_ID_VERSION_3: String = "ID"
    const val COLUMN_NOTE_TITLE_VERSION_3: String = "NoteTitle"
    const val COLUMN_NOTE_BODY_VERSION_3: String = "NoteBody"
    const val COLUMN_TIMESTAMP_VERSION_3: String = "Timestamp"
}