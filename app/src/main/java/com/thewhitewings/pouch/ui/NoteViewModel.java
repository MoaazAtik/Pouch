package com.thewhitewings.pouch.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.thewhitewings.pouch.utils.Constants;
import com.thewhitewings.pouch.data.Note;
import com.thewhitewings.pouch.data.NotesRepository;

public class NoteViewModel extends ViewModel {

    private final NotesRepository notesRepository;
    private final MutableLiveData<Note> noteLiveData = new MutableLiveData<>();

    public NoteViewModel(NotesRepository repository) {
        this.notesRepository = repository;
    }

    public void initializeNote(Bundle args) {
        if (args == null)
            return;

        int id = args.getInt(Constants.COLUMN_ID);
        String noteTitle = args.getString(Constants.COLUMN_NOTE_TITLE);
        String noteBody = args.getString(Constants.COLUMN_NOTE_BODY);
        String timestamp = args.getString(Constants.COLUMN_TIMESTAMP);
        updateNoteLiveData(
                new Note(id, noteTitle, noteBody, timestamp)
        ); // else it should stay null
    }

    public void updateNoteLiveData(Note note) {
        noteLiveData.setValue(note);
    }

    public LiveData<Note> getNoteLiveData() {
        return noteLiveData;
    }


    public void createOrUpdateNote(String newNoteTitle, String newNoteBody) {
        Note note = noteLiveData.getValue();

        if (note == null && (!newNoteTitle.isEmpty() || !newNoteBody.isEmpty()))
            createNote(newNoteTitle, newNoteBody);
        else if (note != null && (!note.getNoteBody().equals(newNoteBody) || !note.getNoteTitle().equals(newNoteTitle)))
            updateNote(newNoteTitle, newNoteBody);
    }

    public void createNote(String noteTitle, String noteBody) {
        notesRepository.createNote(noteTitle, noteBody);
    }

    public void updateNote(String noteTitle, String noteBody) {
        notesRepository.updateNote(noteTitle, noteBody, noteLiveData.getValue());
    }

    public void deleteNote() {
        if (noteLiveData.getValue() != null)
            notesRepository.deleteNote(noteLiveData.getValue());
    }


    public static class NoteViewModelFactory implements ViewModelProvider.Factory {
        private final NotesRepository repository;

        public NoteViewModelFactory(NotesRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(NoteViewModel.class)) {
                return (T) new NoteViewModel(repository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}
