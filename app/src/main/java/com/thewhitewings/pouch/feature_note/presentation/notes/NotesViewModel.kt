package com.thewhitewings.pouch.feature_note.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.thewhitewings.pouch.PouchApplication
import com.thewhitewings.pouch.feature_note.domain.model.Note
import com.thewhitewings.pouch.feature_note.domain.repository.OfflineNotesRepository
import com.thewhitewings.pouch.feature_note.domain.util.getSortOptionFromId
import com.thewhitewings.pouch.feature_note.util.Zone
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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

private const val TAG = "NotesViewModel"

/**
 * ViewModel to interact with the [OfflineNotesRepository]'s data source and the [NotesScreen].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModel(
    private val notesRepository: OfflineNotesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    // Holds current NotesUiState
    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState = _uiState.asStateFlow()

    // The recently deleted note that it may be restored
    private var recentlyDeletedNote: Note? = null

    init {
        collectZoneAndCollectSortOption()

        collectSortOptionSearchQueryZoneAndCollectNotesList()

        // Disable zone initialization animations after the zone is initialized
        /*
        It is needed so the animations are not triggered everytime when navigating back from [AddEditNoteScreen].
        These animations should trigger only when a zone is initialized.
         */
        updateShowAnimationsStateDelayed(false)
    }

    /**
     * Collect the zone changes,
     * to collect the corresponding sortOption, and update the UI state.
     */
    private fun collectZoneAndCollectSortOption() {
        viewModelScope.launch(dispatcher) {
            _uiState.map { it.zone }
                .distinctUntilChanged()
                // Collect zone changes
                .flatMapLatest { zone ->
                    notesRepository.getSortOptionStream(zone)
                }
                .distinctUntilChanged()
                // Collect the corresponding sortOption
                .collect { sortOption ->
                    // Update the UI state with the new sortOption
                    _uiState.update {
                        it.copy(sortOption = sortOption)
                    }
                }
        }
    }

    /**
     * Collect the sortOption, searchQuery, and zone changes,
     * to collect the corresponding notesList, and update the UI state.
     */
    private fun collectSortOptionSearchQueryZoneAndCollectNotesList() {
        viewModelScope.launch(dispatcher) {
            combine(
                _uiState.map { it.sortOption }
                    .distinctUntilChanged(),
                _uiState.map { it.searchQuery }
                    .distinctUntilChanged(),
                _uiState.map { it.zone }
                    .distinctUntilChanged()
            ) { sortOption, searchQuery, _ ->
                Pair(sortOption, searchQuery)
            }
                // Collect sortOption, searchQuery, and zone changes
                .flatMapLatest { (sortOption, searchQuery) ->
                    if (searchQuery.isEmpty()) {
                        notesRepository.getAllNotesStream(sortOption)
                    } else {
                        notesRepository.searchNotesStream(searchQuery, sortOption)
                    }
                }
                // Collect the corresponding notesList
                .collect { notesList ->
                    // Update the UI state with the new notesList
                    _uiState.update {
                        it.copy(notesList = notesList)
                    }
                }
        }
    }

    /**
     * Updates the state of zone initialization animations after a delay.
     * @param canShowAnimations Boolean of whether to show animations or not.
     * @param delay Delay in milliseconds. It is needed to wait for the animations to finish before disabling them.
     */
    private fun updateShowAnimationsStateDelayed(canShowAnimations: Boolean, delay: Long = 2_000) {
        viewModelScope.launch(dispatcher) {
            delay(delay)
            _uiState.update {
                it.copy(showAnimations = canShowAnimations)
            }
        }
    }

    /**
     * Updates the search query state.
     * @param newQuery New search query.
     */
    fun updateSearchQuery(newQuery: String) {
        _uiState.update {
            it.copy(searchQuery = newQuery)
        }
    }

    /**
     * Updates the sort option state.
     * @param sortOptionId Id of the new sort option.
     */
    fun updateSortOption(sortOptionId: Int) {
        val sortOption = getSortOptionFromId(sortOptionId)
        viewModelScope.launch(dispatcher) {
            notesRepository.saveSortOption(sortOption, _uiState.value.zone)
        }
    }

    /**
     * Updates the sort option state for testing.
     *
     * **Note:** This function is only for testing purposes.
     * @param sortOptionId Id of the new sort option.
     */
    fun updateSortOptionStateForTesting(sortOptionId: Int) {
        val sortOption = getSortOptionFromId(sortOptionId)
        _uiState.update {
            it.copy(sortOption = sortOption)
        }
    }

    /**
     * Deletes a note.
     * @param note The note to delete.
     */
    fun deleteNote(note: Note) {
        viewModelScope.launch(dispatcher) {
            notesRepository.deleteNote(note)
            recentlyDeletedNote = note
        }
    }

    /**
     * Restores a note by creating a note with the same title and body as the deleted note.
     *
     * **Note:**
     * The note ID and the timestamp are not restored.
     */
    fun restoreNote() {
        viewModelScope.launch(dispatcher) {
            recentlyDeletedNote?.let { notesRepository.createNote(it) }
            recentlyDeletedNote = null
        }
    }

    /**
     * Toggles the current zone.
     */
    fun toggleZone() {
        notesRepository.toggleZone()
        val newZone =
            if (_uiState.value.zone == Zone.CREATIVE)
                Zone.BOX_OF_MYSTERIES else Zone.CREATIVE

        _uiState.update {
            it.copy(
                zone = newZone,
                searchQuery = "",
                showAnimations = true
            )
        }

        updateShowAnimationsStateDelayed(false)
    }


    companion object {

        /**
         * Factory for [NotesViewModel] that takes [OfflineNotesRepository] as a dependency
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PouchApplication)
                val notesRepository = application.notesRepository
                NotesViewModel(notesRepository = notesRepository)
            }
        }
    }
}