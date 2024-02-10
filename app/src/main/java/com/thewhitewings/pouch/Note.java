package com.thewhitewings.pouch;

import androidx.annotation.NonNull;

public class Note {

    private int id;
    private String noteTitle;
    private String noteBody;
    private String timestamp;

    public Note() {
    }

    public Note(int id, String noteTitle, String noteBody, String timestamp) {
        this.id = id;
        this.noteTitle = noteTitle;
        this.noteBody = noteBody;
        this.timestamp = timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", noteTitle='" + noteTitle + '\'' +
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

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
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
