package com.thewhitewings.pouch.ui

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.thewhitewings.pouch.PouchApplication
import com.thewhitewings.pouch.data.Note
import com.thewhitewings.pouch.data.NotesRepository
import com.thewhitewings.pouch.utils.Constants

class NoteViewModel(private val notesRepository: NotesRepository) : ViewModel() {

    // the note that is opened for updating purpose
    private var oldNote: Note? = null

    // represents updated state of the currently opened note
    private val noteLiveData = MutableLiveData<Note?>()

    /**
     * Initialize the note LiveData and [oldNote] with the provided arguments.
     * It is needed for the first initialization when a note is opened for updating.
     *
     * @param args the arguments bundle passed from the activity containing the note data
     */
    fun initializeNote(args: Bundle?) {
        if (args == null) // creating new note
            return

        if (noteLiveData.value != null) // updating existing note - after configuration change
            return

        // updating existing note - first initialization
        val id = args.getInt(Constants.COLUMN_ID)
        val noteTitle = args.getString(Constants.COLUMN_NOTE_TITLE)
        val noteBody = args.getString(Constants.COLUMN_NOTE_BODY)
        val timestamp = args.getString(Constants.COLUMN_TIMESTAMP)

        oldNote = Note(id, noteTitle!!, noteBody!!, timestamp!!)
        updateNoteLiveData(oldNote)
    }

    /**
     * Update the note LiveData with the provided note.
     *
     * @param note the note with updated data
     */
    fun updateNoteLiveData(note: Note?) {
        noteLiveData.value = note
    }

    /**
     * Get the note LiveData.
     *
     * @return the note LiveData
     */
    fun getNoteLiveData(): LiveData<Note?> {
        return noteLiveData
    }


    /**
     * Create or update the note based on the provided data.
     *
     * @param newNoteTitle the new title of the note
     * @param newNoteBody  the new body of the note
     */
    fun createOrUpdateNote(newNoteTitle: String, newNoteBody: String) {
        if (oldNote == null &&
            (newNoteTitle.isNotEmpty() || newNoteBody.isNotEmpty())
        )
            createNote(
                newNoteTitle,
                newNoteBody
            )
        else if (oldNote != null &&
            (oldNote!!.noteBody != newNoteBody || oldNote!!.noteTitle != newNoteTitle)
        )
            updateNote(
                newNoteTitle,
                newNoteBody
            )
    }

    /**
     * Create a new note with the provided title and body.
     *
     * @param noteTitle the title of the new note
     * @param noteBody  the body of the new note
     */
    private fun createNote(noteTitle: String, noteBody: String) {
        notesRepository.createNote(noteTitle, noteBody)
    }

    /**
     * Update the existing note with the provided title and body.
     *
     * @param noteTitle the new title of the note
     * @param noteBody  the new body of the note
     */
    private fun updateNote(noteTitle: String, noteBody: String) {
        notesRepository.updateNote(noteTitle, noteBody, noteLiveData.value)
    }

    /**
     * Delete the currently opened note.
     */
    fun deleteNote() {
        if (noteLiveData.value != null) notesRepository.deleteNote(noteLiveData.value)
    }


    companion object {
        private const val TAG = "MainViewModel"

        /**
         * Factory for [NoteViewModel] that takes [NotesRepository] as a dependency
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as PouchApplication)
                val notesRepository = application.notesRepository
                NoteViewModel(notesRepository = notesRepository)
            }
        }
    }
}
