package com.example.sqliteapp;

public class Constants {

    // Database
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "notes_db";
    public static final String TABLE_NAME = "Notes";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NOTE_TITLE = "NoteTitle";
    public static final String COLUMN_NOTE_BODY = "NoteBody";
    public static final String COLUMN_TIMESTAMP = "Timestamp";

    // Note Actions
    public static final int ACTION_CLOSE_ONLY = -1;
    public static final int ACTION_CREATE = 0;
    public static final int ACTION_UPDATE = 1;
    public static final int ACTION_DELETE = 2;

    // Date Time Formatting
    public static final int UTC_TO_LOCAL = 0;
    public static final int LOCAL_TO_UTC = 1;
    public static final int CURRENT_LOCAL = 2;
    public static final int FORMATTING_LOCAL = 3;

    // Sorting parameters
    public static final int SORT_A_Z = 0;
    public static final int SORT_Z_A = 1;
    public static final int SORT_OLDEST_FIRST = 2;
    public static final int SORT_NEWEST_FIRST = 3;
}
