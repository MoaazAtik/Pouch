package com.thewhitewings.pouch.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.thewhitewings.pouch.PouchApplication
import com.thewhitewings.pouch.data.Note
import com.thewhitewings.pouch.data.NotesRepository
import com.thewhitewings.pouch.data.SortOption
import com.thewhitewings.pouch.data.fromMenuItemId
import com.thewhitewings.pouch.utils.Zone

class MainViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    @JvmField
    val notesLiveData: LiveData<List<Note>> =
        notesRepository.allNotes
    private val currentZoneLiveData = MutableLiveData(Zone.CREATIVE)
    private var sortOption: SortOption
    private var searchQuery: String

    init {
        this.sortOption = notesRepository.getSortOption(Zone.CREATIVE)
        this.searchQuery = ""
    }

    /**
     * Get the current zone live data.
     *
     * @return the current zone live data
     */
    fun getCurrentZoneLiveData(): LiveData<Zone> {
        return currentZoneLiveData
    }

    /**
     * Toggle the current zone.
     */
    fun toggleZone() {
        currentZoneLiveData.postValue(
            if (currentZoneLiveData.value == Zone.CREATIVE)
                Zone.BOX_OF_MYSTERIES
            else Zone.CREATIVE
        )

        notesRepository.toggleZone(currentZoneLiveData.value)
    }


    /**
     * Handle the selection of a sort option from the popup menu.
     *
     * @param menuItemId the id of the selected popup menu item
     */
    fun handleSortOptionSelection(menuItemId: Int) {
        val selectedOption = fromMenuItemId(menuItemId)
        if (selectedOption != null) {
            sortNotes(selectedOption)
        }
    }

    /**
     * Delete a note.
     *
     * @param note the note to be deleted
     */
    fun deleteNote(note: Note?) {
        notesRepository.deleteNote(note)
    }

    /**
     * Search notes based on the given query and the current sort option.
     *
     * @param query the search query that represents a part of a note's title and/or body
     */
    fun searchNotes(query: String) {
        searchQuery = query
        notesRepository.searchNotes(query, sortOption)
    }

    /**
     * Sort notes based on the given sort option.
     *
     * @param sortOption the sort option to be used for sorting the notes
     */
    private fun sortNotes(sortOption: SortOption) {
        this.sortOption = sortOption
        notesRepository.saveSortOption(sortOption, currentZoneLiveData.value)

        notesRepository.sortNotes(sortOption, searchQuery)
    }

    companion object {
        private const val TAG = "MainViewModel"

        /**
         * Factory for [MainViewModel] that takes [NotesRepository] as a dependency
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as PouchApplication)
                val notesRepository = application.notesRepository
                MainViewModel(notesRepository = notesRepository)
            }
        }
    }

}
