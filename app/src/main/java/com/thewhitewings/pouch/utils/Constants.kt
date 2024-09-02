package com.thewhitewings.pouch.utils

object Constants {

    // Databases
    // Name and Version of Database of Creative zone
    const val CREATIVE_DATABASE_VERSION: Int = 3
    const val CREATIVE_DATABASE_NAME: String = "notes_db"

    // Name and Version of Database of BoxOfMysteries zone
    const val BOM_DATABASE_VERSION: Int = 1
    const val BOM_DATABASE_NAME: String = "bom_db"

    // Common database constants
    const val TABLE_NAME: String = "Notes"
    const val COLUMN_ID: String = "ID"
    const val COLUMN_NOTE_TITLE: String = "NoteTitle"
    const val COLUMN_NOTE_BODY: String = "NoteBody"
    const val COLUMN_TIMESTAMP: String = "Timestamp"

    // Preferences
    const val PREFERENCES_NAME: String = "userPreferences"
    const val CREATIVE_ZONE_SORT_OPTION_PREFERENCE_KEY: String = "mainZoneSortOption"
    const val BOM_ZONE_SORT_OPTION_PREFERENCE_KEY: String = "bomZoneSortOption"
}
