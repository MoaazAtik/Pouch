package com.thewhitewings.pouch.utils;

import com.thewhitewings.pouch.data.SortOption;

public class Constants {

    // Databases
    /**
     * Database version of Creative zone
     */
    public static final int CREATIVE_DATABASE_VERSION = 3;

    /**
     * Database name of Creative zone
     */
    public static final String CREATIVE_DATABASE_NAME = "notes_db";

    /**
     * Database version of Box of Mysteries zone
     */
    public static final int BOM_DATABASE_VERSION = 1;

    /**
     * Database name of Box of Mysteries zone
     */
    public static final String BOM_DATABASE_NAME = "bom_db";


    // Common database constants
    /**
     * Database's table name
     */
    public static final String TABLE_NAME = "Notes";
    /**
     * Database's column name for the ID property
     */
    public static final String COLUMN_ID = "ID";
    /**
     * Database's column name for the title property
     */
    public static final String COLUMN_NOTE_TITLE = "NoteTitle";
    /**
     * Database's column name for the body property
     */
    public static final String COLUMN_NOTE_BODY = "NoteBody";
    /**
     * Database's column name for the timestamp property
     */
    public static final String COLUMN_TIMESTAMP = "Timestamp";


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
