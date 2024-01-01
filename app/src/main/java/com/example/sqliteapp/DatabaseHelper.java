package com.example.sqliteapp;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

        import androidx.annotation.Nullable;

        import java.util.ArrayList;
        import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notes_db";

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Note.CREATE_TABLE);
    }

    //to upgrade it removes the old one and add a new one
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NAME);
        onCreate(db);
    }


    //add new note
    public long insertNote(String note) {
        SQLiteDatabase db = this.getWritableDatabase(); // 'this' here refers to this class which is called 'DatabaseHelper'. getWritableDatabase() method is inherited from 'SQLiteOpenHelper.
//        SQLiteDatabase db = getWritableDatabase(); // this also works. I think it can be used
        Log.d(TAG, "insertNote: db "+db);

        ContentValues values = new ContentValues();
        Log.d(TAG, "insertNote: values "+values);
        values.put(Note.COLUMN_NAME, note);
        Log.d(TAG, "insertNote: values "+values);

        long id = db.insert(Note.TABLE_NAME, null, values);
        db.close();
        return id;
        //although we could make this method void, we made it return id so we can use it e.g. in debugging
        //the id could be int as well
    }


    //get note
    public Note getNote(long id) {
        SQLiteDatabase dp = this.getReadableDatabase();

        Cursor cursor = dp.query(Note.TABLE_NAME,
                new String[]{Note.COLUMN_ID, Note.COLUMN_NAME, Note.COLUMN_TIMESTAMP},
                Note.COLUMN_ID + " = ? ", /// added spaces
                new String[]{String.valueOf(id)}, null, null, null, null
        );
        //"=?" means "if it does".
        //Note.COLUMN_ID + "=?"     means: check if the parameter "id" that was passed in "getNote" equals the "Note.COLUMN_ID"
        //and if it does continue (new String[]{String.valueOf...)
        //is it " =?" ??

//        Log.d(TAG, "getNote: cursor "+cursor.getPosition());
//        Log.d(TAG, "getNote: cursor.moveToFirst() " + cursor.moveToFirst()); /// it affects the cursor position even in a Log, 'if', 'while' statement
//        Log.d(TAG, "getNote: cursor "+cursor.getPosition());
        if(cursor != null) {
//        if(cursor.moveToFirst()) { /// replace the line above and below with this
            cursor.moveToFirst(); /// what does this do (?) , do I need it even though dp.query or db.rawQuery return 'A Cursor object, which is positioned before the first entry.' as in the documentation.
            /// db.query vs db.rawQuery
            //what does it mean??
        }
        Log.d(TAG, "getNote: cursor "+cursor.getPosition());
//        Log.d(TAG, "getNote: cursor "+cursor.moveToNext());
//        Log.d(TAG, "getNote: cursor "+cursor.getPosition());


        //E lines (don't work)
//        Note note = new Note(
//                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
//                cursor.getString(cursor.getColumnIndex(Note.COLUMN_NAME)),
//                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP))
//        );
        //why they don't work??

        //lines I added (working)
        int i1 = cursor.getColumnIndex(Note.COLUMN_ID);
        int i2 = cursor.getColumnIndex(Note.COLUMN_NAME);
        int i3 = cursor.getColumnIndex(Note.COLUMN_TIMESTAMP);

        Note note = new Note(
                cursor.getInt(i1),
                cursor.getString(i2),
                cursor.getString(i3)
        );



        cursor.close(); // add db.close(); like Sluiter (is it needed (?)) YT: 1:07:50
        return note;
    }


    //get all notes
    public List<Note> getAllNote() { /// rename to getAllNotes
        List<Note> notes = new ArrayList<>();

        //select all query:
        String selectQuery = "SELECT * FROM " + Note.TABLE_NAME + " ORDER BY " + Note.COLUMN_TIMESTAMP + " DESC";
        //..." ORDERED BY "...??
        // * means All.     DESC means Descending.

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        //looping for cursor
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();

                //E lines (don't work)
//                note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
//                note.setNote(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NAME)));
//                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));
                //why they don't work??

                //the lines I added (working)
                int i1 = cursor.getColumnIndex(Note.COLUMN_ID);
                int i2 = cursor.getColumnIndex(Note.COLUMN_NAME);
                int i3 = cursor.getColumnIndex(Note.COLUMN_TIMESTAMP);

                note.setId(cursor.getInt(i1));
                note.setNote(cursor.getString(i2));
                note.setTimestamp(cursor.getString(i3));



                notes.add(note);
            } while(cursor.moveToNext()); /// is cursor.moveToNext() executed or just checked and I need to execute it above. It is executed.
        }

        db.close(); /// add cursor.close(); like Sluiter (is it needed (?)) YT: 1:07:50
        return notes;
    }


    //update note:
    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(Note.COLUMN_NAME, note.getNote());

        //because the database takes the id and the timestamp automatically I need to pass the note body only.
        //update for database value
        return db.update(Note.TABLE_NAME, value, Note.COLUMN_ID + " = ? ", new String[]{String.valueOf(note.getId())});
    }


    //Delete note:
    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
//        Log.d(TAG, "deleteNote: this "+this);

        db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " = ? ", new String[]{String.valueOf(note.getId())});

        /// Or like Sluiter, instead of db.delete
//        String queryString = "DELETE FROM " + Note.TABLE_NAME + " WHERE " + Note.COLUMN_ID + " = " + note.getId();
//        Cursor cursor = db.rawQuery(queryString, null);
//        if (cursor.moveToFirst()) // there is a match

        db.close();
    }


}//class
