package com.thewhitewings.pouch.ui

import android.util.Log
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteViewModel(
    private val notesRepository: NotesRepository,
//    private val args: Bundle? = null,
//    savedStateHandle: SavedStateHandle? = null
//    savedStateHandle: SavedStateHandle // gpt // todo move upwards
    val savedStateHandle: SavedStateHandle // gpt // todo move upwards
) : ViewModel() {

    init {
        initializeNote()
    }

    // the note that is opened for updating purpose
    private var oldNote: Note? = null

    private val _noteUiState = MutableStateFlow(NoteUiState())
    val noteUiState = _noteUiState.asStateFlow()

    data class NoteUiState(
        val note: Note = Note(timestamp = ""),
    )

    /**
     * Initialize the note LiveData and [oldNote] with the provided arguments.
     * It is needed for the first initialization when a note is opened for updating.
     *
     * @param args the arguments bundle passed from the activity containing the note data
     */
    private fun initializeNote() {
        val noteId: Int = savedStateHandle[NoteDestination.noteIdArg] ?: return

        viewModelScope.launch {
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
     * Create or update the note based on the provided data.
     *
     * @param newNoteTitle the new title of the note
     * @param newNoteBody  the new body of the note
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
     * Create a new note with the provided title and body.
     *
     * @param noteTitle the title of the new note
     * @param noteBody  the body of the new note
     */
    private fun createNote() {
        viewModelScope.launch {
            notesRepository.createNote(_noteUiState.value.note)
        }
    }

    /**
     * Update the existing note with the provided title and body.
     *
     * @param noteTitle the new title of the note
     * @param noteBody  the new body of the note
     */
    private fun updateNote() {
        viewModelScope.launch {
            notesRepository.updateNote(_noteUiState.value.note)
        }
    }

    /**
     * Delete the currently opened note.
     */
    fun deleteNote() {
        viewModelScope.launch {
            notesRepository.deleteNote(_noteUiState.value.note)
        }
    }

    fun updateNoteTitle(title: String) {
        _noteUiState.update {
            it.copy(
                note = it.note.copy(noteTitle = title)
            )
        }
    }

    fun updateNoteBody(body: String) {
        _noteUiState.update {
            it.copy(
                note = it.note.copy(noteBody = body)
            )
        }
    }


    companion object {
        private const val TAG = "NoteViewModel"

        /**
         * Factory for [NoteViewModel] that takes [NotesRepository] as a dependency
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as PouchApplication)
                val notesRepository = application.notesRepository
                NoteViewModel(
                    notesRepository = notesRepository,
                    savedStateHandle = this.createSavedStateHandle()
                )
            }
        }
    }
}
