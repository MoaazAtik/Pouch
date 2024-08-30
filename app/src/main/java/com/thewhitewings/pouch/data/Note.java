package com.thewhitewings.pouch.data;

import androidx.annotation.NonNull;

import java.util.Objects;

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

    public Note(Note note) {
        this.id = note.id;
        this.noteTitle = note.noteTitle;
        this.noteBody = note.noteBody;
        this.timestamp = note.timestamp;
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

    /**
     * Checks if two notes are equal based on their content.
     *
     * @param note The note to compare with.
     * @return True if the notes are equal, false otherwise.
     */
    public boolean equalContent(@NonNull Note note) {
        return id == note.id &&
                Objects.equals(noteTitle, note.noteTitle) &&
                Objects.equals(noteBody, note.noteBody) &&
                Objects.equals(timestamp, note.timestamp);
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
