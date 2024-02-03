package com.example.sqliteapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "notes_db";
    public static final String TABLE_NAME = "Notes";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NOTE_TITLE = "NoteTitle";
    public static final String COLUMN_NOTE_BODY = "NoteBody";
    public static final String COLUMN_TIMESTAMP = "Timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
    create data Table on app's First Run, or after Clearing Storage related to the app, or when onCreate is called Explicitly by upgradeOrDowngrade()
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement =
                "CREATE TABLE " + TABLE_NAME + "("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_NOTE_TITLE + " TEXT,"
                        + COLUMN_NOTE_BODY + " TEXT,"
                        + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                        + ")";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        upgradeOrDowngrade(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        upgradeOrDowngrade(db);
    }


    /**
     * Insert new note to Database
     *
     * @param noteTitle of the new note
     * @param noteBody  of the new note
     * @return the Row/Note ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertNote(String noteTitle, String noteBody) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, noteTitle);
        values.put(COLUMN_NOTE_BODY, noteBody);

        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    /**
     * Get Note by its id from Database
     *
     * @param id of the wanted note
     * @return queried Note
     */
    public Note getNote(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_NOTE_TITLE, COLUMN_NOTE_BODY, COLUMN_TIMESTAMP},
                COLUMN_ID + " = ? ",
                new String[]{String.valueOf(id)},
                null, null, null, null
        );

        Note note = null;
        if (cursor.moveToFirst()) {
            int i1 = cursor.getColumnIndex(COLUMN_ID);
            int i2 = cursor.getColumnIndex(COLUMN_NOTE_TITLE);
            int i3 = cursor.getColumnIndex(COLUMN_NOTE_BODY);
            int i4 = cursor.getColumnIndex(COLUMN_TIMESTAMP);
            note = new Note(
                    cursor.getInt(i1),
                    cursor.getString(i2),
                    cursor.getString(i3),
                    getFormattedDateTime(0, cursor.getString(i4))
            );
        }

        cursor.close();
        db.close();
        return note;
    }

    /**
     * Get all Notes from Database
     *
     * @return all Notes in Database
     */
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();

        String selectQueryStatement = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQueryStatement, null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                int i1 = cursor.getColumnIndex(COLUMN_ID);
                int i2 = cursor.getColumnIndex(COLUMN_NOTE_TITLE);
                int i3 = cursor.getColumnIndex(COLUMN_NOTE_BODY);
                int i4 = cursor.getColumnIndex(COLUMN_TIMESTAMP);
                note.setId(cursor.getInt(i1));
                note.setNoteTitle(cursor.getString(i2));
                note.setNoteBody(cursor.getString(i3));
                note.setTimestamp(getFormattedDateTime(0, cursor.getString(i4)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notes;
    }

    /**
     * Update the given note in Database
     *
     * @param note to be updated
     * @return the number of rows affected
     */
    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getNoteTitle());
        values.put(COLUMN_NOTE_BODY, note.getNoteBody());
        // Convert timestamp to UTC for Storing in Database
        values.put(COLUMN_TIMESTAMP, getFormattedDateTime(1, note.getTimestamp()));

        return db.update(
                TABLE_NAME,
                values,
                COLUMN_ID + " = ? ",
                new String[]{String.valueOf(note.getId())}
        );
    }

    /**
     * Delete the given note from Database
     *
     * @param note to be deleted
     */
    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(
                TABLE_NAME,
                COLUMN_ID + " = ? ",
                new String[]{String.valueOf(note.getId())}
        );

        db.close();
    }

    /**
     * Get Formatted date and time. The format is yyyy-MM-dd HH:mm:ss = 2024-01-02 19:16:19 <p>
     * Note: Date and time are stored in the Database in UTC, and in Notes List in Local Time Zone.
     *
     * @param usage    0: Date and time in Local Time Zone for Retrieving,
     *                 1: Date and time in UTC for Storing in Database,
     *                 2: Current date and time in Local Time Zone for Storing in Notes List.
     * @param dateTime (Optional) Provide date and/or time to format.
     * @return Formatted date or time.
     */
    public String getFormattedDateTime(int usage, String dateTime) {

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;

        switch (usage) {
            case 0: // Local
                try {
                    sdFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    date = sdFormat.parse(dateTime);
                    sdFormat.setTimeZone(TimeZone.getDefault());
                    return sdFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e(TAG, "getFormattedDateTime: catch e case 0 ", e);
                    return "";
                }
            case 1: // UTC
                try {
                    date = sdFormat.parse(dateTime);
                    sdFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    return sdFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e(TAG, "getFormattedDateTime: case 1 ", e);
                    return "";
                }
            case 2: // Current Local
                date = new Date();
                return sdFormat.format(date);
        }
        return null;
    }


    /**
     * Transfer data from Old table to New Table for the Common columns and Renamed columns.
     * It doesn't transfer data of columns that no longer exist in New table.
     * <p>
     * Note: 1. When upgrading or downgrading the database, modify {@link #DATABASE_VERSION}.<P>
     * 2. When renaming columns, modify {@link #setAndGetColumnMappings()}.
     *
     * @param db to be upgraded or downgraded
     */
    private void upgradeOrDowngrade(SQLiteDatabase db) {
        // Create a temporary table with the same structure as the old table
        String tempTableName = TABLE_NAME + "_temp";
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + tempTableName +
                        " AS SELECT * FROM " + TABLE_NAME
        );

        // Drop the old table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create the new table
        onCreate(db);

        // Get column information for the old and new tables.
        List<String> oldTableColumns = getTableColumns(db, tempTableName);
        List<String> newTableColumns = getTableColumns(db, TABLE_NAME);

        // Find common columns between old and new tables. Note: Renamed columns are not added to this temporary commonColumns List
        List<String> commonColumns = new ArrayList<>(oldTableColumns);
        commonColumns.retainAll(newTableColumns);

        // Copy data from the temporary table to the new table for the common columns
        String commonColumnsString = TextUtils.join(",", commonColumns);
        db.execSQL(
                "INSERT INTO " + TABLE_NAME + " (" + commonColumnsString +
                        ") SELECT " + commonColumnsString +
                        " FROM " + tempTableName
        );

        // Check and transfer data to Renamed columns
        List<ColumnMapping> columnMappings = setAndGetColumnMappings();
        for (ColumnMapping mapping : columnMappings) {
            if (oldTableColumns.contains(mapping.oldColumnName) && newTableColumns.contains(mapping.newColumnName)) {
                // Perform data transfer or adjustments based on the column mapping
                String transferQuery =
                        "UPDATE " + TABLE_NAME +
                                " SET " + mapping.newColumnName + " = " + mapping.oldColumnName +
                                " FROM " + tempTableName +
                                " WHERE " + TABLE_NAME + " . " + COLUMN_ID + " = " + tempTableName + " . " + COLUMN_ID;
                db.execSQL(transferQuery);
            }
        }

        // Drop the temporary table
        db.execSQL("DROP TABLE IF EXISTS " + tempTableName);

        // Find removed and added columns (for debugging)
//        List<String> removedColumns = new ArrayList<>(oldTableColumns);
//        removedColumns.removeAll(newTableColumns);
//        List<String> addedColumns = new ArrayList<>(newTableColumns);
//        addedColumns.removeAll(oldTableColumns);
    }

    /**
     * Get Names of Columns of a Table
     *
     * @param db        database where Table is stored
     * @param tableName to get its columns
     * @return list of columns names
     */
    private List<String> getTableColumns(SQLiteDatabase db, String tableName) {
        List<String> columns = new ArrayList<>();
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    int columnOfTableColumnsNames = cursor.getColumnIndex("name");
                    columns.add(cursor.getString(columnOfTableColumnsNames));
                }
            } finally {
                cursor.close();
            }
        }

        return columns;
    }

    /**
     * Set and Get column mappings. It is used for Renaming Columns.
     * I need to populate this list with new column mappings.
     *
     * @return list of Column Mappings which have Old and New names of Renamed Columns.
     */
    private List<ColumnMapping> setAndGetColumnMappings() {
        List<ColumnMapping> columnMappings = new ArrayList<>();
        // Add new column mappings here
//        columnMappings.add(new ColumnMapping("NoteBody2", COLUMN_NOTE_BODY));
        return columnMappings;
    }


    // A class to hold column mapping information to be used when Renaming Columns
    private class ColumnMapping {
        public String oldColumnName;
        public String newColumnName;

        public ColumnMapping(String oldColumnName, String newColumnName) {
            this.oldColumnName = oldColumnName;
            this.newColumnName = newColumnName;
        }
    }

}
