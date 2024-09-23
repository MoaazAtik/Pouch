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

/**
 * ViewModel to interact with the [NotesRepository]'s data source and the Notes list screen.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val notesRepository: NotesRepository
) : ViewModel() {

    // Holds current HomeUiState
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    // Count of how many times the Box of mysteries reveal button has been pressed (knocked)
    private var bomKnocks = 0

    // Boolean of whether the timeout for revealing the Box of mysteries has started
    private var bomTimeoutStarted = false

    init {
        viewModelScope.launch {
            // Collect zone changes,
            // and collect the corresponding sortOption for that zone to display
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
            // Collect sortOption, searchQuery, and zone changes,
            // and collect the corresponding flow of notes to display
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

        // Disable zone initialization animations after the zone is initialized
        /*
        It is needed so the animations are not triggered everytime when navigating back from a Note screen.
        These animations should trigger only when a zone is initialized.
         */
        updateShowAnimationsStateDelayed(false)
    }

    /**
     * Updates the state of zone initialization animations after a delay.
     * @param canShowAnimations Boolean of whether to show animations or not.
     * @param delay Delay in milliseconds. It is needed to wait for the animations to finish before disabling them.
     */
    private fun updateShowAnimationsStateDelayed(canShowAnimations: Boolean, delay: Long = 2_000) {
        viewModelScope.launch {
            delay(delay)
            _homeUiState.update {
                it.copy(showAnimations = canShowAnimations)
            }
        }
    }

    /**
     * Updates the search query state.
     * @param newQuery New search query.
     */
    fun updateSearchQuery(newQuery: String) {
        _homeUiState.update {
            it.copy(searchQuery = newQuery)
        }
    }

    /**
     * Updates the sort option state.
     * @param sortOptionId Id of the new sort option.
     */
    fun updateSortOption(sortOptionId: Int) {
        val sortOption = getSortOptionFromId(sortOptionId)
        viewModelScope.launch {
            notesRepository.saveSortOption(sortOption, _homeUiState.value.zone)
        }
    }

    /**
     * Deletes a note.
     * @param note The note to delete.
     */
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            notesRepository.deleteNote(note)
        }
    }

    /**
     * Triggers the sequence of revealing the Box of mysteries.
     */
    fun revealBoxOfMysteries() {
        bomKnocks++
        if (!bomTimeoutStarted) {
            viewModelScope.launch {
                startBoxRevealTimeout()
            }
        }
    }

    /**
     * Starts the timeout for completing the sequence of revealing the Box of mysteries.
     * If the sequence of revealing the Box of mysteries is completed before the timeout,
     * the Box of mysteries will be revealed.
     * Otherwise, the time window will be closed and the sequence will be reset.
     */
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

    /**
     * Toggles the current zone.
     */
    fun toggleZone() {
        notesRepository.toggleZone()
        val newZone =
            if (_homeUiState.value.zone == Zone.CREATIVE)
                Zone.BOX_OF_MYSTERIES else Zone.CREATIVE

        _homeUiState.update {
            it.copy(
                zone = newZone,
                searchQuery = "",
                showAnimations = true
            )
        }

        updateShowAnimationsStateDelayed(false)
    }


    /**
     * UI state for the Home screen.
     */
    data class HomeUiState(
        val notesList: List<Note> = emptyList(),
        val zone: Zone = Zone.CREATIVE,
        val sortOption: SortOption = SortOption.NEWEST_FIRST,
        val searchQuery: String = "",
        val showAnimations: Boolean = true
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