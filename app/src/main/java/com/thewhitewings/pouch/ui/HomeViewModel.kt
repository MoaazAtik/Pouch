package com.thewhitewings.pouch.ui

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(private val notesRepository: NotesRepository) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    private var bomKnocks = 0
    private var bomTimeoutStarted = false

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

        _homeUiState.update {
            it.copy(
                zone = newZone,
                searchQuery = "",
                isBomRevealed = !it.isBomRevealed
            )
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            notesRepository.deleteNote(note)
        }
    }

    fun revealBoxOfMysteries() {
        bomKnocks++
        if (!bomTimeoutStarted) {
            viewModelScope.launch {
                startBoxRevealTimeout()
            }
        }
    }

    private suspend fun startBoxRevealTimeout() {
        bomTimeoutStarted = true
        val timeoutKnocking = 7_000L // 7 seconds timeout
        val startKnockingTime = System.currentTimeMillis()

        while (bomTimeoutStarted) {
            val elapsedKnockingTime = System.currentTimeMillis() - startKnockingTime

            if (elapsedKnockingTime >= timeoutKnocking) {
                bomTimeoutStarted = false
                bomKnocks = 0
                break
            } else if (bomKnocks == 5) {
                delay(500) // wait 500ms before completing the reveal
                bomTimeoutStarted = false
                bomKnocks = 0
                toggleZone()
                break
            }

            delay(200) // Wait for 200ms before checking again
        }
    }

    data class HomeUiState(
        val notesList: List<Note> = emptyList(),
        val zone: Zone = Zone.CREATIVE,
        val sortOption: SortOption = SortOption.NEWEST_FIRST,
        val searchQuery: String = "",
        val isBomRevealed: Boolean = false
    )

    companion object {

        /**
         * Factory for [HomeViewModel] that takes [NotesRepository] as a dependency
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as PouchApplication)
                val notesRepository = application.notesRepository
                HomeViewModel(notesRepository = notesRepository)
            }
        }
    }

}
