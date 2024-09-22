package com.thewhitewings.pouch.data;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Note Class.
 * A model that represents a single row (a note) in the database.
 */
public class Note {

    /**
     * Primary key is the unique ID of the note.
     */
    private int id;

    /**
     * Title of the note.
     */
    private String noteTitle;

    /**
     * Body of the note.
     */
    private String noteBody;

    /**
     * Timestamp of the note.
     * <p>
     * <strong>Note:</strong>
     * Date and time are stored in the Database in UTC,
     * and presented to the UI in the local time zone.
     * </p>
     */
    private String timestamp;

    /**
     * Default constructor.
     */
    public Note() {
    }

    /**
     * Constructor with all fields.
     */
    public Note(int id, String noteTitle, String noteBody, String timestamp) {
        this.id = id;
        this.noteTitle = noteTitle;
        this.noteBody = noteBody;
        this.timestamp = timestamp;
    }

    /**
     * Copy constructor.
     */
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
     * Checks if two notes are equal based on the content of all their fields.
     *
     * @param note The note to compare with.
     * @return {@code true} if the notes are equal, {@code false} otherwise.
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
