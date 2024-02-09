package com.thewhitewings.pouch;

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
import java.util.TimeZone;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    /**
     * Constructor of DatabaseHelper
     *
     * @param context         of App or Activity
     * @param databaseName    {@link Constants#MAIN_DATABASE_NAME} for the default database of Main Activity or {@link Constants#BOM_DATABASE_NAME} for the Box of Mysteries database
     * @param databaseVersion {@link Constants#MAIN_DATABASE_VERSION} for the default database of Main Activity or {@link Constants#BOM_DATABASE_VERSION} for the Box of Mysteries database
     */
    public DatabaseHelper(Context context, String databaseName, int databaseVersion) {
        super(context, databaseName, null, databaseVersion);
    }

    /*
    create data Table on app's First Run, or after Clearing Storage related to the app, or when onCreate is called Explicitly by upgradeOrDowngrade()
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement =
                "CREATE TABLE " + Constants.TABLE_NAME + "("
                        + Constants.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Constants.COLUMN_NOTE_TITLE + " TEXT,"
                        + Constants.COLUMN_NOTE_BODY + " TEXT,"
                        + Constants.COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
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
        values.put(Constants.COLUMN_NOTE_TITLE, noteTitle);
        values.put(Constants.COLUMN_NOTE_BODY, noteBody);

        long id = db.insert(Constants.TABLE_NAME, null, values);
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
                Constants.TABLE_NAME,
                new String[]{Constants.COLUMN_ID, Constants.COLUMN_NOTE_TITLE, Constants.COLUMN_NOTE_BODY, Constants.COLUMN_TIMESTAMP},
                Constants.COLUMN_ID + " = ? ",
                new String[]{String.valueOf(id)},
                null, null, null, null
        );

        Note note = null;
        if (cursor.moveToFirst()) {
            int i1 = cursor.getColumnIndex(Constants.COLUMN_ID);
            int i2 = cursor.getColumnIndex(Constants.COLUMN_NOTE_TITLE);
            int i3 = cursor.getColumnIndex(Constants.COLUMN_NOTE_BODY);
            int i4 = cursor.getColumnIndex(Constants.COLUMN_TIMESTAMP);
            note = new Note(
                    cursor.getInt(i1),
                    cursor.getString(i2),
                    cursor.getString(i3),
                    getFormattedDateTime(Constants.UTC_TO_LOCAL, cursor.getString(i4))
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

        String selectQueryStatement = "SELECT * FROM " + Constants.TABLE_NAME + " ORDER BY " + Constants.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQueryStatement, null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                int i1 = cursor.getColumnIndex(Constants.COLUMN_ID);
                int i2 = cursor.getColumnIndex(Constants.COLUMN_NOTE_TITLE);
                int i3 = cursor.getColumnIndex(Constants.COLUMN_NOTE_BODY);
                int i4 = cursor.getColumnIndex(Constants.COLUMN_TIMESTAMP);
                note.setId(cursor.getInt(i1));
                note.setNoteTitle(cursor.getString(i2));
                note.setNoteBody(cursor.getString(i3));
                note.setTimestamp(getFormattedDateTime(Constants.UTC_TO_LOCAL, cursor.getString(i4)));

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
        values.put(Constants.COLUMN_NOTE_TITLE, note.getNoteTitle());
        values.put(Constants.COLUMN_NOTE_BODY, note.getNoteBody());
        // Convert timestamp to UTC for Storing in Database
        values.put(Constants.COLUMN_TIMESTAMP, getFormattedDateTime(Constants.LOCAL_TO_UTC, note.getTimestamp()));

        return db.update(
                Constants.TABLE_NAME,
                values,
                Constants.COLUMN_ID + " = ? ",
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
                Constants.TABLE_NAME,
                Constants.COLUMN_ID + " = ? ",
                new String[]{String.valueOf(note.getId())}
        );

        db.close();
    }

    /**
     * Get Formatted date and time. The Basic format is yyyy-MM-dd HH:mm:ss = 2024-01-02 19:16:19 <p>
     * Note: Date and time are stored in the Database in UTC, and in Notes List in Local Time Zone.
     *
     * @param usage    {@link Constants#UTC_TO_LOCAL}: Date and time from UTC to Local Time Zone for Retrieving,
     *                 {@link Constants#LOCAL_TO_UTC}: Date and time from Local Time Zone to UTC for Storing in Database,
     *                 {@link Constants#CURRENT_LOCAL}: Current date and time in Local Time Zone for Storing in Notes List.
     *                 {@link Constants#FORMATTING_LOCAL}: Formatted Date in Local Time Zone for Retrieving in Note Fragment. "MMM d, yyyy" = Feb 4, 2024
     * @param dateTime (Optional) Provide date and/or time to format.
     * @return Formatted date or time.
     */
    public String getFormattedDateTime(int usage, String dateTime) {

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;

        switch (usage) {
            case Constants.UTC_TO_LOCAL:
                try {
                    sdFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    date = sdFormat.parse(dateTime);
                    sdFormat.setTimeZone(TimeZone.getDefault());
                    return sdFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e(TAG, "getFormattedDateTime: catch e case UTC_TO_LOCAL ", e);
                    return "e " + dateTime;
                }
            case Constants.LOCAL_TO_UTC:
                try {
                    date = sdFormat.parse(dateTime);
                    sdFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    return sdFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e(TAG, "getFormattedDateTime: case LOCAL_TO_UTC ", e);
                    return "e " + dateTime;
                }
            case Constants.CURRENT_LOCAL:
                date = new Date();
                return sdFormat.format(date);
            case Constants.FORMATTING_LOCAL:
                try {
                    date = sdFormat.parse(dateTime);
                    sdFormat.applyPattern("MMM d, yyyy");
                    return sdFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e(TAG, "getFormattedDateTime: case FORMATTING_LOCAL ", e);
                    return "e " + dateTime;
                }
        }
        return null;
    }


    /**
     * Transfer data from Old table to New Table for the Common columns and Renamed columns.
     * It doesn't transfer data of columns that no longer exist in New table.
     * <p>
     * Note: 1. When upgrading or downgrading the database, modify {@link Constants#MAIN_DATABASE_VERSION} and {@link Constants#BOM_DATABASE_VERSION}.<P>
     * 2. When renaming columns, modify {@link #setAndGetColumnMappings()}.
     *
     * @param db to be upgraded or downgraded
     */
    private void upgradeOrDowngrade(SQLiteDatabase db) {
        // Create a temporary table with the same structure as the old table
        String tempTableName = Constants.TABLE_NAME + "_temp";
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + tempTableName +
                        " AS SELECT * FROM " + Constants.TABLE_NAME
        );

        // Drop the old table
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);

        // Create the new table
        onCreate(db);

        // Get column information for the old and new tables.
        List<String> oldTableColumns = getTableColumns(db, tempTableName);
        List<String> newTableColumns = getTableColumns(db, Constants.TABLE_NAME);

        // Find common columns between old and new tables. Note: Renamed columns are not added to this temporary commonColumns List
        List<String> commonColumns = new ArrayList<>(oldTableColumns);
        commonColumns.retainAll(newTableColumns);

        // Copy data from the temporary table to the new table for the common columns
        String commonColumnsString = TextUtils.join(",", commonColumns);
        db.execSQL(
                "INSERT INTO " + Constants.TABLE_NAME + " (" + commonColumnsString +
                        ") SELECT " + commonColumnsString +
                        " FROM " + tempTableName
        );

        // Check and transfer data to Renamed columns
        List<ColumnMapping> columnMappings = setAndGetColumnMappings();
        for (ColumnMapping mapping : columnMappings) {
            if (oldTableColumns.contains(mapping.oldColumnName) && newTableColumns.contains(mapping.newColumnName)) {
                // Perform data transfer or adjustments based on the column mapping
                String transferQuery =
                        "UPDATE " + Constants.TABLE_NAME +
                                " SET " + mapping.newColumnName + " = " + mapping.oldColumnName +
                                " FROM " + tempTableName +
                                " WHERE " + Constants.TABLE_NAME + " . " + Constants.COLUMN_ID + " = " + tempTableName + " . " + Constants.COLUMN_ID;
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
