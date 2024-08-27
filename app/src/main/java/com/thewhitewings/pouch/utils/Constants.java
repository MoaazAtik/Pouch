package com.thewhitewings.pouch.utils;

public class Constants {

    // Databases
    // Name and Version of Database of Creative zone
    public static final int CREATIVE_DATABASE_VERSION = 3;
    public static final String CREATIVE_DATABASE_NAME = "notes_db";
    // Name and Version of Database of BoxOfMysteries zone
    public static final int BOM_DATABASE_VERSION = 1;
    public static final String BOM_DATABASE_NAME = "bom_db";
    // Common
    public static final String TABLE_NAME = "Notes";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NOTE_TITLE = "NoteTitle";
    public static final String COLUMN_NOTE_BODY = "NoteBody";
    public static final String COLUMN_TIMESTAMP = "Timestamp";

    // Preferences
    public static final String PREFERENCES_NAME = "userPreferences";
    public static final String CREATIVE_ZONE_SORT_OPTION_PREFERENCE_KEY = "mainZoneSortOption";
    public static final String BOM_ZONE_SORT_OPTION_PREFERENCE_KEY = "bomZoneSortOption";
}
