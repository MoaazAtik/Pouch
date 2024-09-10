package com.thewhitewings.pouch.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.thewhitewings.pouch.PouchApplication
import com.thewhitewings.pouch.data.Note
import com.thewhitewings.pouch.data.NotesRepository
import com.thewhitewings.pouch.data.SortOption
import com.thewhitewings.pouch.data.fromMenuItemId
import com.thewhitewings.pouch.utils.Zone
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.log

class MainViewModel(private val notesRepository: NotesRepository) : ViewModel() {

    private val _searchQueryFlow = MutableStateFlow("")
    private val _currentZoneFlow = MutableStateFlow(Zone.CREATIVE)
    private val _sortOptionFlow = notesRepository.getSortOptionFlow(_currentZoneFlow.value)

    init {
        viewModelScope.launch {
            _sortOptionFlow.collect {
                Log.d(TAG, "init: _sortOptionFlow.value $it")
            }
            Log.d(TAG, "init: _sortOptionFlow.value ${_sortOptionFlow.first()}")
        }
    }

    val notesFlow: Flow<List<Note>> =
        notesRepository.getNotesFlow(_sortOptionFlow, _searchQueryFlow, _currentZoneFlow)


    /**
     * Get the current zone live data.
     *
     * @return the current zone live data
     */
    fun getCurrentZoneFlow(): StateFlow<Zone> {
        return _currentZoneFlow
    }

    fun getCurrentSortOptionFlow(): Flow<SortOption> {
        viewModelScope.launch {
            Log.d(TAG, "getCurrentSortOptionFlow: _sortOptionFlow.value ${_sortOptionFlow.first()}")
        }
        return _sortOptionFlow
    }

    /**
     * Toggle the current zone.
     */
    fun toggleZone() {
        _currentZoneFlow.update {
            if (_currentZoneFlow.value == Zone.CREATIVE)
                Zone.BOX_OF_MYSTERIES
            else Zone.CREATIVE
        }
    }


    /**
     * Handle the selection of a sort option from the popup menu.
     *
     * @param menuItemId the id of the selected popup menu item
     */
    fun handleSortOptionSelection(menuItemId: Int) {
        val selectedOption = fromMenuItemId(menuItemId)
        updateSortOption(selectedOption!!)
    }

    /**
     * Delete a note.
     *
     * @param note the note to be deleted
     */
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            notesRepository.deleteNote(note)
        }
    }

    fun updateSearchQuery(newQuery: String) {
        _searchQueryFlow.value = newQuery
    }

    fun updateSortOption(newSortOption: SortOption) {
        viewModelScope.launch {
            Log.d(TAG, "coroutine started")
            Log.d(TAG, "_currentZoneFlow.value ${_currentZoneFlow.value}")
            notesRepository.saveSortOption(newSortOption, _currentZoneFlow.value)
        }

        Log.d(TAG, "updateSortOption: ")
        Log.d(TAG, "newSortOption $newSortOption")
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
