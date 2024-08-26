package com.thewhitewings.pouch.data;

import androidx.lifecycle.LiveData;

import com.thewhitewings.pouch.utils.Zone;

import java.util.List;

public interface NotesRepository {
    LiveData<List<Note>> getAllNotes();

    void createNote(String noteTitle, String noteBody);

    void updateNote(String newNoteTitle, String noteBody, Note oldNote);

    void deleteNote(Note note);

    void searchNotes(String searchQuery, SortOption sortOption);

    void sortNotes(SortOption sortOption, String searchQuery);

    void toggleZone(Zone newZone);

    void saveSortOption(SortOption sortOption, Zone zone);

    SortOption getSortOption(Zone zone);
}
