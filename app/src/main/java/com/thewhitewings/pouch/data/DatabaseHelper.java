package com.thewhitewings.pouch.data;

import static com.thewhitewings.pouch.utils.DateTimeUtils.getFormattedDateTime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.thewhitewings.pouch.utils.Constants;
import com.thewhitewings.pouch.utils.DateTimeFormatType;

import java.util.ArrayList;
import java.util.List;

///**
// * Database Helper Class
// */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private DatabaseChangeListener databaseChangeListener;

    /**
     * Constructor of DatabaseHelper
     *
     * @param context         of App or Activity
     * @param databaseName    {@link Constants#CREATIVE_DATABASE_NAME} for database of the creative zone or {@link Constants#BOM_DATABASE_NAME} for database of Box of Mysteries zone
     * @param databaseVersion {@link Constants#CREATIVE_DATABASE_VERSION} for database of the creative zone or {@link Constants#BOM_DATABASE_VERSION} for database of Box of Mysteries zone
     */
    public DatabaseHelper(Context context, String databaseName, int databaseVersion) {
        super(context, databaseName, null, databaseVersion);
    }

    /**
     * Set the listener to be notified when the database changes.
     *
     * @param listener the listener to be set
     */
    public void setDatabaseChangeListener(DatabaseChangeListener listener) {
        this.databaseChangeListener = listener;
    }

    /**
     * Create Database Table.
     * <p>
     * {@inheritDoc}
     * </p>
     * <p>
     * <strong>Note:</strong> Create data Table on app's First Run, or after Clearing Storage related to the app, or when onCreate is called Explicitly by upgradeOrDowngrade()
     * </p>
     *
     * @param db The database.
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
     * Create a new Note in Database
     *
     * @param noteTitle of the new note
     * @param noteBody  of the new note
     */
    public void createNote(String noteTitle, String noteBody) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_NOTE_TITLE, noteTitle);
        values.put(Constants.COLUMN_NOTE_BODY, noteBody);

        db.insert(
                Constants.TABLE_NAME,
                null,
                values
        );
        db.close();

        // Notify the observers
        if (databaseChangeListener != null)
            databaseChangeListener.onDatabaseChanged();
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
                    getFormattedDateTime(DateTimeFormatType.UTC_TO_LOCAL, cursor.getString(i4))
            );
        }

        cursor.close();
        db.close();
        return note;
    }

    /**
     * Get all Notes from Database organized by timestamp in descending order.
     *
     * @return all Notes in Database
     */
    public List<Note> getAllNotes() {
        List<Note> allNotes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String orderBy = "timestamp DESC";

        Cursor cursor = db.query(
                Constants.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                orderBy);

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
                note.setTimestamp(getFormattedDateTime(DateTimeFormatType.UTC_TO_LOCAL, cursor.getString(i4)));

                allNotes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return allNotes;
    }

    /**
     * Get all Notes from Database organized by given {@link SortOption}.
     *
     * @param sortOption to be used for sorting
     * @return all Notes in Database
     */
    public List<Note> getAllNotes(SortOption sortOption) {
        List<Note> allNotes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                Constants.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                sortOption.toSqlString());

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
                note.setTimestamp(getFormattedDateTime(DateTimeFormatType.UTC_TO_LOCAL, cursor.getString(i4)));

                allNotes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return allNotes;
    }

    /**
     * Update the given note in Database
     *
     * @param note to be updated
     */
    public void updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_NOTE_TITLE, note.getNoteTitle());
        values.put(Constants.COLUMN_NOTE_BODY, note.getNoteBody());
        // Convert timestamp to UTC for Storing in Database
        values.put(Constants.COLUMN_TIMESTAMP, getFormattedDateTime(DateTimeFormatType.LOCAL_TO_UTC, note.getTimestamp()));


        db.update(
                Constants.TABLE_NAME,
                values,
                Constants.COLUMN_ID + " = ? ",
                new String[]{String.valueOf(note.getId())}
        );
        db.close();

        // Notify the observers
        if (databaseChangeListener != null)
            databaseChangeListener.onDatabaseChanged();
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

        // Notify the observers
        if (databaseChangeListener != null)
            databaseChangeListener.onDatabaseChanged();
    }

    /**
     * Search Notes by Note title or body
     *
     * @param searchQuery Note title and/or body
     * @param sortOption  to be used for sorting the results
     * @return List of Notes that match the search query
     */
    public List<Note> searchNotes(String searchQuery, SortOption sortOption) {
        List<Note> filteredNotes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = "noteTitle LIKE ? OR noteBody LIKE ?";
        String[] selectionArgs = new String[]{"%" + searchQuery + "%", "%" + searchQuery + "%"};
        String orderBy = sortOption.toSqlString();

        Cursor cursor = db.query(
                Constants.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                orderBy);

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
                note.setTimestamp(getFormattedDateTime(DateTimeFormatType.UTC_TO_LOCAL, cursor.getString(i4)));

                filteredNotes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return filteredNotes;
    }


    /**
     * Transfer data from Old table to New Table for the Common columns and Renamed columns.
     * <p>
     * It doesn't transfer data of columns that no longer exist in New table.
     * </p>
     * <strong>Notes:</strong>
     * <ul>
     *     <li>When upgrading or downgrading the database, modify {@link Constants#CREATIVE_DATABASE_VERSION} and {@link Constants#BOM_DATABASE_VERSION}.</li>
     *     <li>When renaming columns, modify the column mappings by calling {@link #setAndGetColumnMappings()}.</li>
     * </ul>
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
     * Get the names of columns in a table.
     *
     * @param db        database where Table is stored
     * @param tableName to get its columns
     * @return list of column names
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
     * Set and Get column mappings.
     * To Rename a table's Columns, you need to create a new Column Mapping and populate this list with the new column mappings.
     *
     * @return list of Column Mappings which have Old and New names of Renamed Columns.
     */
    private List<ColumnMapping> setAndGetColumnMappings() {
        List<ColumnMapping> columnMappings = new ArrayList<>();
        // Add new column mappings here
//        columnMappings.add(new ColumnMapping("NoteBody2", COLUMN_NOTE_BODY));
        return columnMappings;
    }


    /**
     * A class to hold column mapping information to be used when Renaming Columns
     */
    private class ColumnMapping {
        public String oldColumnName;
        public String newColumnName;

        /**
         * Constructor of ColumnMapping
         *
         * @param oldColumnName the old name of the column
         * @param newColumnName the new name of the column
         */
        public ColumnMapping(String oldColumnName, String newColumnName) {
            this.oldColumnName = oldColumnName;
            this.newColumnName = newColumnName;
        }
    }

}
