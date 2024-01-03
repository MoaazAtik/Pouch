package com.example.sqliteapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "notes_db";
    public static final String TABLE_NAME = "Notes";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NOTE_BODY = "NoteBody";
    public static final String COLUMN_TIMESTAMP = "Timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // create data Table on app's First Run, or after Clearing Storage related to the app
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement =
                "CREATE TABLE " + TABLE_NAME + "("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_NOTE_BODY + " TEXT,"
                        + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";

        db.execSQL(createTableStatement);
    }

    // when upgrading the Database, Remove the old one and Create a new one
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    /**
     * Insert new note to Database
     *
     * @param noteBody of the new note
     * @return the Row/Note ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertNote(String noteBody) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
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
                new String[]{COLUMN_ID, COLUMN_NOTE_BODY, COLUMN_TIMESTAMP},
                COLUMN_ID + " = ? ",
                new String[]{String.valueOf(id)},
                null, null, null, null
        );

        Note note = null;
        if (cursor.moveToFirst()) {
            int i1 = cursor.getColumnIndex(COLUMN_ID);
            int i2 = cursor.getColumnIndex(COLUMN_NOTE_BODY);
            int i3 = cursor.getColumnIndex(COLUMN_TIMESTAMP);
            note = new Note(
                    cursor.getInt(i1),
                    cursor.getString(i2),
                    cursor.getString(i3)
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
                int i2 = cursor.getColumnIndex(COLUMN_NOTE_BODY);
                int i3 = cursor.getColumnIndex(COLUMN_TIMESTAMP);
                note.setId(cursor.getInt(i1));
                note.setNoteBody(cursor.getString(i2));
                note.setTimestamp(cursor.getString(i3));

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

        ContentValues value = new ContentValues();
        value.put(COLUMN_NOTE_BODY, note.getNoteBody());

        return db.update(
                TABLE_NAME,
                value,
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
}
