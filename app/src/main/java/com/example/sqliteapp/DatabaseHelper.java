
package com.example.sqliteapp;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

        import androidx.annotation.Nullable;

        import java.util.ArrayList;
        import java.util.List;

//#5.8
//we could also name it DatabaseController, DatabaseHandler or any other name
public class DatabaseHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notes_db";

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    //#5.11*
    //2:15 create constructor with context parameter
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
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_NAME, note);

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
                Note.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null
        );
        //"=?" means "if it does".
        //Note.COLUMN_ID + "=?"     means: check if the parameter "id" that was passed in "getNote" equals the "Note.COLUMN_ID"
        //and if it does continue (new String[]{String.valueOf...)
        //is it " =?" ??

        if(cursor != null) {
            cursor.moveToFirst();
            //what does it mean??
        }


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



        cursor.close();
        return note;
    }


    //get all notes
    public List<Note> getAllNote() {
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
            } while(cursor.moveToNext());
        }

        db.close();
        return notes;
    }


    //#5.9
    //update note:
    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(Note.COLUMN_NAME, note.getNote());

        //because the database takes the id and the timestamp automatically I need to pass the note body only.
        //update for database value
        return db.update(Note.TABLE_NAME, value, Note.COLUMN_ID + " =?", new String[]{String.valueOf(note.getId())});
    }


    //Delete note:
    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " =?", new String[]{String.valueOf(note.getId())});
        db.close();
    }


}//class

//package com.example.sqliteapp;
//
//public class DatabaseHelper {
//}