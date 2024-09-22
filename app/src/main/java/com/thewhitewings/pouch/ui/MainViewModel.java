package com.thewhitewings.pouch.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.thewhitewings.pouch.data.Note;
import com.thewhitewings.pouch.data.NotesRepository;
import com.thewhitewings.pouch.data.SortOption;
import com.thewhitewings.pouch.utils.Zone;

import java.util.List;

/**
 * ViewModel to interact with the {@link  NotesRepository}'s data source and the main activity.
 */
public class MainViewModel extends ViewModel {

    private static final String TAG = "MainViewModel";
    private final NotesRepository repository;
    private final LiveData<List<Note>> notesLiveData;
    private final MutableLiveData<Zone> currentZoneLiveData;
    private SortOption sortOption;
    private String searchQuery;

    /**
     * Constructor for {@link MainViewModel}.
     *
     * @param repository to be used for interacting with the data source
     */
    public MainViewModel(NotesRepository repository) {
        this.repository = repository;
        this.notesLiveData = repository.getAllNotes();
        this.currentZoneLiveData = new MutableLiveData<>(Zone.CREATIVE);
        this.sortOption = repository.getSortOption(Zone.CREATIVE);
        this.searchQuery = "";
    }

    /**
     * Get the notes live data.
     *
     * @return the notes live data
     */
    public LiveData<List<Note>> getNotesLiveData() {
        return notesLiveData;
    }

    /**
     * Get the current zone live data.
     *
     * @return the current zone live data
     */
    public LiveData<Zone> getCurrentZoneLiveData() {
        return currentZoneLiveData;
    }

    /**
     * Toggle the current zone.
     */
    public void toggleZone() {
        currentZoneLiveData.postValue(
                currentZoneLiveData.getValue() == Zone.CREATIVE ?
                        Zone.BOX_OF_MYSTERIES : Zone.CREATIVE
        );

        repository.toggleZone(currentZoneLiveData.getValue());
    }


    /**
     * Handle the selection of a sort option from the popup menu.
     *
     * @param menuItemId the id of the selected popup menu item
     */
    public void handleSortOptionSelection(int menuItemId) {
        SortOption selectedOption = SortOption.fromMenuItemId(menuItemId);
        if (selectedOption != null) {
            sortNotes(selectedOption);
        }
    }

    /**
     * Delete a note.
     *
     * @param note the note to be deleted
     */
    public void deleteNote(Note note) {
        repository.deleteNote(note);
    }

    /**
     * Search notes based on the given query and the current sort option.
     *
     * @param query the search query that represents a part of a note's title and/or body
     */
    public void searchNotes(String query) {
        searchQuery = query;
        repository.searchNotes(query, sortOption);
    }

    /**
     * Sort notes based on the given sort option.
     *
     * @param sortOption the sort option to be used for sorting the notes
     */
    public void sortNotes(SortOption sortOption) {
        this.sortOption = sortOption;
        repository.saveSortOption(sortOption, currentZoneLiveData.getValue());

        repository.sortNotes(sortOption, searchQuery);
    }


    /**
     * Factory class for creating instances of {@link MainViewModel}.
     */
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
