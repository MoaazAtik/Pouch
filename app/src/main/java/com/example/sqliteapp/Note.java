package com.example.sqliteapp;

import androidx.annotation.NonNull;

public class Note {

    private int id;
    private String noteBody;
    private String timestamp;

    public Note() {
    }

    public Note(int id, String noteBody, String timestamp) {
        this.id = id;
        this.noteBody = noteBody;
        this.timestamp = timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", noteBody='" + noteBody + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNoteBody() {
        return noteBody;
    }

    public void setNoteBody(String noteBody) {
        this.noteBody = noteBody;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
