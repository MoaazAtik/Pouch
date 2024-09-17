package com.thewhitewings.pouch.ui

import android.util.Log
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
import com.thewhitewings.pouch.data.getSortOptionFromId
import com.thewhitewings.pouch.utils.Zone
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "MainViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(private val notesRepository: NotesRepository) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Collect zone changes and fetch the corresponding sortOption for that zone
            _homeUiState.map { it.zone }
                .distinctUntilChanged()
                .flatMapLatest { zone ->
                    notesRepository.getSortOptionFlow(zone)
                }
                .distinctUntilChanged()
                .collect { sortOption ->
                    _homeUiState.update {
                        it.copy(sortOption = sortOption)
                    }
                }
        }

        viewModelScope.launch {
            // Combine sortOption and searchQuery to determine the flow of notes to collect
            combine(
                _homeUiState.map { it.sortOption }
                    .distinctUntilChanged(),
                _homeUiState.map { it.searchQuery }
                    .distinctUntilChanged(),
                _homeUiState.map { it.zone }
                    .distinctUntilChanged()
            ) { sortOption, searchQuery, _ ->
                Pair(sortOption, searchQuery)
            }
                .flatMapLatest { (sortOption, searchQuery) ->
                    if (searchQuery.isEmpty()) {
                        notesRepository.getAllNotesStream(sortOption)
                    } else {
                        notesRepository.searchNotesStream(searchQuery, sortOption)
                    }
                }
                .collect { notesList ->
                    _homeUiState.update {
                        it.copy(notesList = notesList)
                    }
                }
        }
    }

    fun updateSearchQuery(newQuery: String) {
        _homeUiState.update {
            it.copy(searchQuery = newQuery)
        }
    }

    fun updateSortOption(sortOptionId: Int) {
        val sortOption = getSortOptionFromId(sortOptionId)
        viewModelScope.launch {
            notesRepository.saveSortOption(sortOption, _homeUiState.value.zone)
        }
    }

    fun toggleZone() {
        notesRepository.toggleZone()
        val newZone =
            if (_homeUiState.value.zone == Zone.CREATIVE)
                Zone.BOX_OF_MYSTERIES else Zone.CREATIVE

        viewModelScope.launch {
            _homeUiState.update {
                it.copy(
                    zone = newZone,
                    searchQuery = ""
                )
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            notesRepository.deleteNote(note)
        }
    }

    data class HomeUiState(
        val notesList: List<Note> = emptyList(),
        val zone: Zone = Zone.CREATIVE,
        val sortOption: SortOption = SortOption.NEWEST_FIRST,
        val searchQuery: String = ""
    )

    companion object {

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
