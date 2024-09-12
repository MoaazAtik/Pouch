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
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.log

class MainViewModel(private val notesRepository: NotesRepository) : ViewModel() {

    private val _searchQueryFlow = MutableStateFlow("")
    private val _currentZoneFlow = MutableStateFlow(Zone.CREATIVE)
    private val _sortOptionFlow = notesRepository.getSortOptionFlow(_currentZoneFlow.value)
        .stateIn(viewModelScope, WhileSubscribed(5000L), SortOption.NEWEST_FIRST)
    private val _notesListFlow = MutableStateFlow<List<Note>>(listOf())

    //    private val _homeUiState = MutableStateFlow(
    val homeUiState: StateFlow<HomeUiState> =
        combine(
            _searchQueryFlow,
            _currentZoneFlow,
            _sortOptionFlow,
            _notesListFlow
        ) { searchQuery, currentZone, sortOption, notesList ->
            HomeUiState(
                notesList = notesList,
                zone = currentZone,
                sortOption = sortOption,
                searchQuery = searchQuery
            )
        }
            .stateIn(
                viewModelScope,
                WhileSubscribed(5000L),
                HomeUiState()
            ) as StateFlow<HomeUiState>
//    val homeUiState: StateFlow<HomeUiState> = _homeUiState

    init {
        _notesListFlow.value =
            combine(
                _searchQueryFlow,
                _currentZoneFlow,
                _sortOptionFlow
            ) { searchQuery, currentZone, sortOption ->
                val notesListFlow =
                    if (searchQuery == "")
                        notesRepository.getAllNotesStream(sortOption)
                    else
                        notesRepository.searchNotesStream(searchQuery, sortOption)
//                _homeUiState.update {
//                    it.copy(
//                        notesList = notesListFlow.last(),
//                        zone = currentZone,
//                        sortOption = sortOption,
//                        searchQuery = searchQuery
//                    )
//                }
                notesListFlow
//        }.map { notesList ->
//            _homeUiState.update {
//                it.copy(notesList = notesList.first())
//            }
//            notesList
            }.stateIn(viewModelScope, WhileSubscribed(5000L), listOf<Note>())
                .value as List<Note>
//            .map {
//                notesList ->
//                _notesListFlow.value = notesList as MutableList<Note>
//                notesList
//            }
//        combine(_searchQueryFlow, _currentZoneFlow, _sortOptionFlow) {
//            searchQuery, currentZone, sortOption ->
//            if (searchQuery == "")
//                notesRepository.getAllNotesStream(sortOption)
//            else
//                notesRepository.searchNotesStream(searchQuery, sortOption)
//        }.flatMapLatest {
//            it
//        }.stateIn(viewModelScope, WhileSubscribed(5000L), listOf())
//            .collect {
//                _homeUiState.update {
//                    it.copy(notesList = it.notesList)
//                }
//            }
//        }
    }

    data class HomeUiState(
        val notesList: List<Note> = listOf(),
        val zone: Zone = Zone.CREATIVE,
        val sortOption: SortOption = SortOption.NEWEST_FIRST,
        val searchQuery: String = ""
    )


    /**
     * Get the current zone live data.
     *
     * @return the current zone live data
     */
//    fun getCurrentZoneFlow(): StateFlow<Zone> {
//        return _currentZoneFlow
//    }
//
//    fun getCurrentSortOptionFlow(): Flow<SortOption> {
//        viewModelScope.launch {
//            Log.d(TAG, "getCurrentSortOptionFlow: _sortOptionFlow.value ${_sortOptionFlow.first()}")
//        }
//        return _sortOptionFlow
//    }

    /**
     * Toggle the current zone.
     */
    fun toggleZone() {
        notesRepository.toggleZone()
        _currentZoneFlow.update {
            if (_currentZoneFlow.value == Zone.CREATIVE)
                Zone.BOX_OF_MYSTERIES
            else Zone.CREATIVE
        }
    }
    /*
     todo: update to comply with single source of truth by using a single flow in repository and collecting in viewmodel
      */


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
        Log.d(TAG, "newQuery: $newQuery")
        Log.d(TAG, "searchQueryFlow: ${_searchQueryFlow.value}")
        _searchQueryFlow.update { newQuery }
//        _searchQueryFlow.value += newQuery
        Log.d(TAG, "searchQueryFlow: ${_searchQueryFlow.value}")
//        viewModelScope.launch {
//            _homeUiState.update {
//                if (newQuery == "")
//                    it.copy(
//                        searchQuery = newQuery,
//                        notesList = notesRepository.getAllNotesStream(it.sortOption).first()
//                    )
//                else
//                    it.copy(
//                        searchQuery = newQuery,
//                        notesList = notesRepository.searchNotesStream(newQuery, it.sortOption)
//                            .first()
//                    )
//            }
//        }
    }

    fun updateSortOption(newSortOption: SortOption) {
        viewModelScope.launch {
            notesRepository.saveSortOption(newSortOption, _currentZoneFlow.value)
        }
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
