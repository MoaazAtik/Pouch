package com.thewhitewings.pouch.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.thewhitewings.pouch.PouchApplication
import com.thewhitewings.pouch.data.Note
import com.thewhitewings.pouch.data.NotesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "NoteViewModel"

/**
 * ViewModel to interact with the [NotesRepository]'s data source and the Note screen.
 */
class NoteViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    // the note that is opened for updating purpose
    private var oldNote: Note? = null

    // Holds current NoteUiState
    private val _noteUiState = MutableStateFlow(NoteUiState())
    val noteUiState = _noteUiState.asStateFlow()

    init {
        initializeNote()
    }

    /**
     * Initialize the note with the given ID when opening an existing note.
     */
    private fun initializeNote() {
        val noteId: Int = savedStateHandle[NoteDestination.noteIdArg]
            ?: return

        viewModelScope.launch(dispatcher) {
            notesRepository.getNoteById(noteId)
                .collect { note ->
                    if (note != null) {
                        _noteUiState.value = _noteUiState.value.copy(
                            note = note
                        )
                        oldNote = note
                    }
                }
        }
    }

    /**
     * Updates the note title of the current note's state
     */
    fun updateNoteTitle(title: String) {
        _noteUiState.update {
            it.copy(
                note = it.note.copy(noteTitle = title)
            )
        }
    }

    /**
     * Updates the note body of the current note's state
     */
    fun updateNoteBody(body: String) {
        _noteUiState.update {
            it.copy(
                note = it.note.copy(noteBody = body)
            )
        }
    }

    /**
     * Create a new note or update the currently opened note.
     *
     * To create a new note, the note title or note body must not be empty.
     *
     * To update a note, the note title and note body must not be the same as the old note.
     * Otherwise, the note will not be updated, i.e., its timestamp will stay the same.
     */
    fun createOrUpdateNote() {
        with(_noteUiState.value.note) {
            if (oldNote == null) {
                if (noteTitle.isNotEmpty() || noteBody.isNotEmpty())
                    createNote()
            } else if (!oldNote?.noteBody.equals(noteBody) ||
                !oldNote?.noteTitle.equals(noteTitle)
            ) {
                updateNote()
            }
        }
    }

    /**
     * Create a new note.
     */
    private fun createNote() {
        viewModelScope.launch(dispatcher) {
            notesRepository.createNote(_noteUiState.value.note)
        }
    }

    /**
     * Update an existing note.
     */
    private fun updateNote() {
        viewModelScope.launch(dispatcher) {
            notesRepository.updateNote(_noteUiState.value.note)
        }
    }

    /**
     * Deletes a note.
     */
    fun deleteNote() {
        viewModelScope.launch(dispatcher) {
            notesRepository.deleteNote(_noteUiState.value.note)
        }
    }


    /**
     * UI state for the Note screen
     */
    data class NoteUiState(
        val note: Note = Note(timestamp = "")
    )


    companion object {

        /**
         * Factory for [NoteViewModel] that takes [NotesRepository] as a dependency
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as PouchApplication)
                val notesRepository = application.notesRepository
                NoteViewModel(
                    savedStateHandle = this.createSavedStateHandle(),
                    notesRepository = notesRepository
                )
            }
        }
    }
}
