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

    // Sorting parameters
    public static final int SORT_A_Z = 0;
    public static final int SORT_Z_A = 1;
    public static final int SORT_OLDEST_FIRST = 2;
    public static final int SORT_NEWEST_FIRST = 3;
}
