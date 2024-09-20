package com.thewhitewings.pouch.utils

object Constants {

    // Databases
    // Database name of Creative zone
    const val CREATIVE_DATABASE_NAME: String = "notes_db"

    // Database name of BoxOfMysteries zone
    const val BOM_DATABASE_NAME: String = "bom_db"

    // Common database constants
    // version 2 (current)
    const val CREATIVE_AND_BOM_DATABASE_VERSION: Int = 2
    const val TABLE_NAME: String = "note"
    const val COLUMN_ID: String = "id"
    const val COLUMN_NOTE_TITLE: String = "note_title"
    const val COLUMN_NOTE_BODY: String = "note_body"
    const val COLUMN_TIMESTAMP: String = "timestamp"

    // Deprecated database constants
    // version 1 (old)
    const val CREATIVE_AND_BOM_DATABASE_VERSION_1: Int = 1

    // version 3 (old)
    const val CREATIVE_AND_BOM_DATABASE_VERSION_3: Int = 3
    const val TABLE_NAME_VERSION_3: String = "Notes"
    const val COLUMN_ID_VERSION_3: String = "ID"
    const val COLUMN_NOTE_TITLE_VERSION_3: String = "NoteTitle"
    const val COLUMN_NOTE_BODY_VERSION_3: String = "NoteBody"
    const val COLUMN_TIMESTAMP_VERSION_3: String = "Timestamp"
}