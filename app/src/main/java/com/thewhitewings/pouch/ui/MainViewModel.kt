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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val notesRepository: NotesRepository) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    init {
        viewModelScope.launch {
            val sortOption = notesRepository.getSortOptionFlow(Zone.CREATIVE).first()
            val notesList = notesRepository.getAllNotesStream(sortOption).first()

            _homeUiState.update {
                it.copy(
                    notesList = notesList,
                    sortOption = sortOption
                )
            }
        }
    }

    data class HomeUiState(
        val notesList: List<Note> = emptyList(),
        val zone: Zone = Zone.CREATIVE,
        val sortOption: SortOption = SortOption.NEWEST_FIRST,
        val searchQuery: String = ""
    )

    fun toggleZone() {
        notesRepository.toggleZone()
        viewModelScope.launch {
            val notesList =
                getNotesList(
                    _homeUiState.value.searchQuery,
                    _homeUiState.value.sortOption
                )

            _homeUiState.update {
                it.copy(
                    notesList = notesList,
                    zone =
                    if (_homeUiState.value.zone == Zone.CREATIVE)
                        Zone.BOX_OF_MYSTERIES
                    else Zone.CREATIVE
                )
            }
        }
    }

    fun updateSortOption(sortOptionId: Int) {
        val sortOption = getSortOptionFromId(sortOptionId)

        viewModelScope.launch {
            notesRepository.saveSortOption(sortOption, _homeUiState.value.zone)
            val notesList =
                getNotesList(
                    _homeUiState.value.searchQuery,
                    sortOption
                )

            _homeUiState.update {
                it.copy(
                    notesList = notesList,
                    sortOption = sortOption
                )
            }
        }
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
        _homeUiState.update {
            it.copy(
                searchQuery = newQuery
            )
        }
        viewModelScope.launch {
            val notesList =
                getNotesList(
                    _homeUiState.value.searchQuery,
                    _homeUiState.value.sortOption
                )

            _homeUiState.update {
                it.copy(
                    notesList = notesList
                )
            }
        }
    }

    private suspend fun getNotesList(searchQuery: String, sortOption: SortOption): List<Note> {
        val deferred =
            viewModelScope.async {
                if (searchQuery.isEmpty())
                    notesRepository.getAllNotesStream(sortOption).first()
                else
                    notesRepository.searchNotesStream(searchQuery, sortOption).first()
            }
        return deferred.await()
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
