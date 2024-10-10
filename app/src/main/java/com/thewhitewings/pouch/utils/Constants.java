package com.thewhitewings.pouch.utils;

import com.thewhitewings.pouch.data.SortOption;

public class Constants {

    // Databases
    /**
     * Database name of Creative zone
     */
    public static final String CREATIVE_DATABASE_NAME = "notes_db";

    /**
     * Database name of Box of Mysteries zone
     */
    public static final String BOM_DATABASE_NAME = "bom_db";


    // Common database constants
    // version 2 (current)
    /**
     * Current version of both databases
     */
    public static final int CREATIVE_AND_BOM_DATABASE_VERSION = 2;
    /**
     * Database's table name
     */
    public static final String TABLE_NAME = "note";
    /**
     * Database's column name for the ID property
     */
    public static final String COLUMN_ID = "id";
    /**
     * Database's column name for the title property
     */
    public static final String COLUMN_NOTE_TITLE = "note_title";
    /**
     * Database's column name for the body property
     */
    public static final String COLUMN_NOTE_BODY = "note_body";
    /**
     * Database's column name for the timestamp property
     */
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // Deprecated database constants
    /**
     * Old version 1 of Box of Mysteries zone
     */
    public static final int BOM_DATABASE_VERSION_1 = 1;
    /**
     * Old version 3 of Creative zone
     */
    public static final int CREATIVE_DATABASE_VERSION_3 = 3;
    public static final String TABLE_NAME_VERSION_3 = "Notes";
    public static final String COLUMN_ID_VERSION_3 = "ID";
    public static final String COLUMN_NOTE_TITLE_VERSION_3 = "NoteTitle";
    public static final String COLUMN_NOTE_BODY_VERSION_3 = "NoteBody";
    public static final String COLUMN_TIMESTAMP_VERSION_3 = "Timestamp";


    // Preferences
    /**
     * Name of the SharedPreferences file
     */
    public static final String PREFERENCES_NAME = "userPreferences";

    /**
     * Key for the {@link SortOption} preference in the SharedPreferences of the Creative Zone
     */
    public static final String CREATIVE_ZONE_SORT_OPTION_PREFERENCE_KEY = "mainZoneSortOption";

    /**
     * Key for the {@link SortOption} preference in the SharedPreferences of the Box of Mysteries Zone
     */
    public static final String BOM_ZONE_SORT_OPTION_PREFERENCE_KEY = "bomZoneSortOption";
}
