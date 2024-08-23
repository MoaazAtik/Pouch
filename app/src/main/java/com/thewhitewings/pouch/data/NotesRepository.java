package com.thewhitewings.pouch.data;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface NotesRepository {
    LiveData<List<Note>> getAllNotes();

    void createNote(String noteTitle, String noteBody);

    void updateNote(String newNoteTitle, String noteBody, Note oldNote);

    void deleteNote(Note note);

    void searchNotes(String query);

    void toggleZone();
}
