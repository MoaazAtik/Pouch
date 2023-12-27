package com.example.sqliteapp;

public class Note {
}

package com.example.sqliteapp;

//#5.7
public class Note {
    //
    public static final String TABLE_NAME = "notes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "note";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    //each "row" refers to a note (whole note i.e. id, note body, timestamp)
    //COLUMN_ID refers to the "column" that contains the id's of the rows, and not an id of the column
    //COLUMN_NAME refers to the "column" that contains the bodies of the notes, and not the name (title/header) of each column

    private int id;
    private String note;
    private String timestamp;

    //create SQL: create table
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";
    //the spaces within the CREATE_TABLE Statement are not so important (e.g. " DATETIME DEFAULT CURRENT_TIMESTAMP     " is right also)


    public Note() {
    }

    public Note(int id, String note, String timestamp) {
        this.id = id;
        this.note = note;
        this.timestamp = timestamp;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}//class