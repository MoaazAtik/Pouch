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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(private val notesRepository: NotesRepository) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

//    init {
//        viewModelScope.launch {
////            val sortOption = notesRepository.getSortOptionFlow(Zone.CREATIVE).first()
////            val notesList = notesRepository.getAllNotesStream(sortOption).first()
////            _homeUiState.update {
////                it.copy(
////                    notesList = notesList,
////                    sortOption = sortOption
////                )
////            }
//
//            notesRepository.getSortOptionFlow(Zone.CREATIVE)
//                .collect { sortOption ->
//                    Log.d(TAG, "collecting sort option")
//                    _homeUiState.update {
//                        it.copy(
//                            sortOption = sortOption
//                        )
//                    }
//                }
//        }
//
//        Log.d(TAG, "before notes stream")
//        viewModelScope.launch {
//            notesRepository.getAllNotesStream(_homeUiState.value.sortOption)
//                .collect { noteList ->
//                    Log.d(TAG, "collecting notes: _homeUiState.value.sortOption ${_homeUiState.value.sortOption}")
//                    _homeUiState.update {
//                        it.copy(
//                            notesList = noteList
//                        )
//                    }
//                }
//        }
//        Log.d(TAG, "after notes stream")
//    }

//    init {
//        viewModelScope.launch {
//            notesRepository.getSortOptionFlow(_homeUiState.value.zone)
//                .collect { sortOption ->
//                    Log.d(TAG, "collecting sort option $sortOption")
//                    _homeUiState.update {
//                        it.copy(
//                            sortOption = sortOption
//                        )
//                    }
//                }
//        }
//        viewModelScope.launch {
//            combine(
//                _homeUiState.map { it.sortOption },
//                _homeUiState.map { it.searchQuery },
//                _homeUiState.map { it.zone }
//            ) { sortOption, searchQuery, zone ->
//                Triple(sortOption, searchQuery, zone)
//            }
//                .flatMapLatest { (sortOption, searchQuery, zone) ->
//                    Log.d(TAG, "flat map sortOption $sortOption, searchQuery $searchQuery, zone $zone")
//                    if (searchQuery.isEmpty()) {
//                        notesRepository.getAllNotesStream(sortOption, )
//                    } else {
//                        notesRepository.searchNotesStream(searchQuery, sortOption, )
//                    }
//                }
//                .collect { notesList ->
//                    Log.d(TAG, "collecting notes: sortOption: ${_homeUiState.value.sortOption} " +
//                            "zone: ${_homeUiState.value.zone} " +
//                            "searchQuery: ${_homeUiState.value.searchQuery}")
//                    if (_homeUiState.value.notesList.isNotEmpty()) {
//                        Log.d(TAG, "new note ${_homeUiState.value.notesList.first()}")
//                    }
//                    _homeUiState.update { it.copy(notesList = notesList) }
//                }
//        }
//    }

    init {
        viewModelScope.launch {
            _homeUiState.map { it.zone }
                .collect { zone ->
                    Log.d(TAG, "collecting zone $zone")
                    _homeUiState.update {
                        it.copy(
                            sortOption = notesRepository.getSortOptionFlow(zone).first()
                            // todo move this to toggleZone below
                        )
                    }
                }
        }
        viewModelScope.launch {
            notesRepository.getSortOptionFlow(_homeUiState.value.zone)
                .collect { sortOption ->
                    Log.d(TAG, "collecting sort option $sortOption")
                    _homeUiState.update {
                        it.copy(
                            sortOption = sortOption
                        )
                    }
                }
        }
        viewModelScope.launch {
            combine(
                _homeUiState.map { it.sortOption },
                _homeUiState.map { it.searchQuery },
                // zone is collected above -> it updates the sort option -> triggers the combine
            ) { sortOption, searchQuery ->
                Pair(sortOption, searchQuery)
            }
                .flatMapLatest { (sortOption, searchQuery) ->
                    Log.d(TAG, "flat map sortOption $sortOption, searchQuery $searchQuery")
                    //todo fix it is called twice
                    if (searchQuery.isEmpty()) {
                        notesRepository.getAllNotesStream(sortOption, )
                    } else {
                        notesRepository.searchNotesStream(searchQuery, sortOption, )
                    }
                }
                .collect { notesList ->
                    Log.d(TAG, "collecting notes: sortOption: ${_homeUiState.value.sortOption} " +
                            "zone: ${_homeUiState.value.zone} " +
                            "searchQuery: ${_homeUiState.value.searchQuery}")
                    _homeUiState.update { it.copy(notesList = notesList) }
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
//            val notesList =
//                getNotesList(
//                    _homeUiState.value.searchQuery,
//                    _homeUiState.value.sortOption
//                )

            _homeUiState.update {
                it.copy(
//                    notesList = notesList,
                    zone =
                    if (_homeUiState.value.zone == Zone.CREATIVE)
                        Zone.BOX_OF_MYSTERIES
                    else Zone.CREATIVE,
                    searchQuery = ""
                )
            }
        }
    }

    fun updateSortOption(sortOptionId: Int) {
        val sortOption = getSortOptionFromId(sortOptionId)

        viewModelScope.launch {
            notesRepository.saveSortOption(sortOption, _homeUiState.value.zone)
//            val notesList =
//                getNotesList(
//                    _homeUiState.value.searchQuery,
//                    sortOption
//                )

//            _homeUiState.update {
//                it.copy(
//                    notesList = notesList,
//                    sortOption = sortOption
//                )
//            }
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
//        viewModelScope.launch {
//            val notesList =
//                getNotesList(
//                    _homeUiState.value.searchQuery,
//                    _homeUiState.value.sortOption
//                )
//
//            _homeUiState.update {
//                it.copy(
//                    notesList = notesList
//                )
//            }
//        }
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
