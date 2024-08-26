package com.thewhitewings.pouch.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.thewhitewings.pouch.Constants;
import com.thewhitewings.pouch.data.Note;
import com.thewhitewings.pouch.data.NotesRepository;
import com.thewhitewings.pouch.data.SortOption;

import java.util.List;


public class MainViewModel extends ViewModel {

    private static final String TAG = "MainViewModel";
    private final NotesRepository repository;
    public final LiveData<List<Note>> notesLiveData;
    private final MutableLiveData<Constants.Zone> currentZoneLiveData;
    private SortOption sortOption;
    private String searchQuery;

    public MainViewModel(NotesRepository repository) {
        this.repository = repository;
        this.notesLiveData = repository.getAllNotes();
        this.currentZoneLiveData = new MutableLiveData<>(Constants.Zone.MAIN);
        this.sortOption = repository.getSortOption(Constants.Zone.MAIN);
        this.searchQuery = "";
    }

    public LiveData<Constants.Zone> getCurrentZoneLiveData() {
        return currentZoneLiveData;
    }

    public void toggleZone() {
        currentZoneLiveData.postValue(
                currentZoneLiveData.getValue() == Constants.Zone.MAIN ? Constants.Zone.BOX_OF_MYSTERIES : Constants.Zone.MAIN
        );

        repository.toggleZone(currentZoneLiveData.getValue());
    }


    public void handleSortOption(int menuItemId) {
        SortOption selectedOption = SortOption.fromMenuItemId(menuItemId);
        if (selectedOption != null) {
            sortNotes(selectedOption);
        }
    }

    public void deleteNote(Note note) {
        repository.deleteNote(note);
    }

    public void searchNotes(String query) {
        searchQuery = query;
        repository.searchNotes(query, sortOption);
    }

    public void sortNotes(SortOption sortOption) {
        this.sortOption = sortOption;
        repository.saveSortOption(sortOption, currentZoneLiveData.getValue());

        repository.sortNotes(sortOption, searchQuery);
    }


    public static class MainViewModelFactory implements ViewModelProvider.Factory {
        private final NotesRepository repository;

        public MainViewModelFactory(NotesRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(MainViewModel.class)) {
                return (T) new MainViewModel(repository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
