package com.thewhitewings.pouch.presentation

import androidx.lifecycle.SavedStateHandle
import com.thewhitewings.pouch.feature_note.domain.repository.NotesRepository
import com.thewhitewings.pouch.mocks.mockNote1
import com.thewhitewings.pouch.feature_note.presentation.add_edit_note.NoteDestination
import com.thewhitewings.pouch.feature_note.presentation.add_edit_note.NoteUiState
import com.thewhitewings.pouch.feature_note.presentation.add_edit_note.NoteViewModel
import com.thewhitewings.pouch.rules.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModelTest {

    // Rule to set main dispatcher to a test coroutine dispatcher
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Mocks and ViewModel
    private lateinit var viewModel: NoteViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var notesRepository: NotesRepository

    // Test dispatcher for coroutines
    private val testDispatcher = UnconfinedTestDispatcher()

    /**
     * Try to initialize NoteViewModel without passing in a noteIdArg. No repository call should be made and no ui state update should happen.
     * This case represents creating a new note.
     * Case: noteIdArg is not provided
     * for [NoteViewModel.initializeNote]
     */
    @Test
    fun `When initializing note and noteId is not provided, no repository call and no ui state update`() =
        runTest {
            // When NoteViewModel is initialized, it calls initializeNote()
            // Since noteIdArg is not provided, no note should be loaded
            initializeViewModelToCreateNewNote()

            // Then: Verify that the initial NoteUiState remains unchanged
            val expectedState = NoteUiState()
            val currentState = viewModel.noteUiState.value
            assertEquals(expectedState, currentState)

            // Ensure that no interaction with repository happens
            verify(notesRepository, never()).getNoteById(anyInt())
        }

    /**
     * Try to initialize NoteViewModel with a noteIdArg. A repository call should be made and ui state should be updated.
     * This case represents updating an existing note.
     * Case: noteIdArg is provided
     * for [NoteViewModel.initializeNote]
     */
    @Test
    fun `When initializing note and noteId is provided, get note from repository and update ui state`() =
        runTest {
            // Initialize the ViewModel to update an existing note
            initializeViewModelToUpdateExistingNote()

            // Collect the latest noteUiState value
            val currentState = viewModel.noteUiState.first()

            // Assert that the repository function was called with the correct noteId
            verify(notesRepository).getNoteById(1)

            // Assert that noteUiState contains the correct note
            assertEquals(mockNote1, currentState.note)
        }

    /**
     * Try to initialize NoteViewModel with a noteIdArg, but the note is not found in the repository. A repository call should be made, but ui state should not be updated.
     * Case: noteIdArg is provided, but note is not found
     * for [NoteViewModel.initializeNote]
     */
    @Test
    fun `When initializing note and noteId is provided, but note is not found, do not update ui state`() =
        runTest {
            // Mock the repository
            notesRepository = mock(NotesRepository::class.java)

            // SavedStateHandle with noteIdArg
            savedStateHandle = SavedStateHandle(mapOf(NoteDestination.noteIdArg to 1))

            // Mock the repository to return null for the provided note ID
            whenever(notesRepository.getNoteById(1)).thenReturn(flowOf(null))

            // Initialize the ViewModel
            viewModel = NoteViewModel(savedStateHandle, notesRepository, testDispatcher)

            // Collect the latest noteUiState value
            val currentState = viewModel.noteUiState.first()

            // Assert that the repository function was called with the correct noteId
            verify(notesRepository).getNoteById(1)

            // Assert that noteUiState remains in the default state (no note found)
            assertEquals(NoteUiState(), currentState)
        }

    /**
     * Pass note title to update note title of the current note's state correctly.
     * Happy path for [NoteViewModel.updateNoteTitle]
     */
    @Test
    fun `When updating note title, update ui state correctly`() = runTest {
        // Initialize the ViewModel to create or update a note
        // Given: Initialize the ViewModel to create a new note
        // with default note ui state values
        initializeViewModelToCreateNewNote()

        // Initial note ui state
        val initialNoteUiState = NoteUiState()

        // When: Update the note title
        val newTitle = "New Title"
        viewModel.updateNoteTitle(newTitle)

        // Collect the latest noteUiState value
        val currentState = viewModel.noteUiState.first()

        // Then: Verify that the note title is updated in the state
        assertEquals(newTitle, currentState.note.noteTitle)

        // Assert that the rest of the note properties remain unchanged
        assertEquals(initialNoteUiState.note.id, currentState.note.id)
        assertEquals(initialNoteUiState.note.noteBody, currentState.note.noteBody)
        assertEquals(initialNoteUiState.note.timestamp, currentState.note.timestamp)
    }

    /**
     * Pass note body to update note body of the current note's state correctly.
     * Happy path for [NoteViewModel.updateNoteBody]
     */
    @Test
    fun `When updating note body, update ui state correctly`() = runTest {
        // Initialize the ViewModel to create or update a note
        // Given: Initialize the ViewModel to create a new note
        // with default note ui state values
        initializeViewModelToCreateNewNote()

        // Initial note ui state
        val initialNoteUiState = NoteUiState()

        // When: Update the note body
        val newBody = "New Body"
        viewModel.updateNoteBody(newBody)

        // Collect the latest noteUiState value
        val currentState = viewModel.noteUiState.first()

        // Then: Verify that the note body is updated in the state
        assertEquals(newBody, currentState.note.noteBody)

        // Assert that the rest of the note properties remain unchanged
        assertEquals(initialNoteUiState.note.id, currentState.note.id)
        assertEquals(initialNoteUiState.note.noteTitle, currentState.note.noteTitle)
        assertEquals(initialNoteUiState.note.timestamp, currentState.note.timestamp)
    }

    /**
     * When creating a new note, and title and/or body is not empty,
     * call the repository to create note.
     * Case: Creating a note when title is not empty
     * Happy path for [NoteViewModel.createOrUpdateNote]
     */
    @Test
    fun `When creating new note, and title or body is not empty, create note`() = runTest {
        // Initialize the ViewModel to create a note.
        // Thus, oldNote is null and title and body are empty.
        initializeViewModelToCreateNewNote()

        // A new non-empty title
        val newTitle = mockNote1.noteTitle

        // Expected note to be passed to the repository
        val expectedNote = NoteUiState().note.copy(
            noteTitle = newTitle
        )

        // Update note state with the new title
        viewModel.updateNoteTitle(newTitle)

        // When: The createOrUpdateNote function is called
        viewModel.createOrUpdateNote()

        // Then: Verify that createNote was called
        verify(notesRepository).createNote(expectedNote)
    }

    /**
     * When creating a new note, and title and body are empty,
     * do not call the repository to create note.
     * Case: Creating a note when title and body are empty
     * Happy path for [NoteViewModel.createOrUpdateNote]
     */
    @Test
    fun `When creating new note, and title and body are empty, do not create note`() = runTest {
        // Initialize the ViewModel to create a note.
        // Thus, oldNote is null and title and body are empty.
        initializeViewModelToCreateNewNote()

        // Collect the latest noteUiState value
        val currentState = viewModel.noteUiState.first()

        // When: The createOrUpdateNote function is called
        viewModel.createOrUpdateNote()

        // Then: Verify that createNote is not called
        verify(notesRepository, never()).createNote(currentState.note)
    }

    /**
     * When updating an existing note, and title and/or body is modified,
     * call the repository to update note.
     * Case: Updating an existing note when title is modified
     * Happy path for [NoteViewModel.createOrUpdateNote]
     */
    @Test
    fun `When updating an existing note, and title or body is modified, update note`() = runTest {
        // Initialize the ViewModel to update an existing note
        initializeViewModelToUpdateExistingNote()

        // Given: An existing note (oldNote is not null)
        val existingNote = mockNote1

        // A new non-empty title
        val newTitle = "Updated Title"

        // Expected note to be passed to the repository
        val updatedNote = existingNote.copy(
            noteTitle = newTitle
        )

        // Update note state with the new title
        viewModel.updateNoteTitle(newTitle)

        // When: The createOrUpdateNote function is called
        viewModel.createOrUpdateNote()

        // Then: Verify that updateNote is called because the title/body has changed
        verify(notesRepository).updateNote(updatedNote)
    }

    /**
     * When updating an existing note, and title and body are unchanged,
     * do not call the repository to update note.
     * Case: Updating an existing note when title and body are unchanged
     * Happy path for [NoteViewModel.createOrUpdateNote]
     */
    @Test
    fun `When updating an existing note, and title and body are unchanged, do not update note`() =
        runTest {
            // Initialize the ViewModel to update an existing note
            initializeViewModelToUpdateExistingNote()

            // Collect the latest noteUiState value
            val currentState = viewModel.noteUiState.first()

            // When: The createOrUpdateNote function is called
            viewModel.createOrUpdateNote()

            // Then: Verify that updateNote is not called
            verify(notesRepository, never()).updateNote(currentState.note)
        }

    /**
     * When deleting a note, call the repository to delete note.
     * Happy path for [NoteViewModel.deleteNote]
     */
    @Test
    fun `When deleting a note, delete it`() = runTest {
        // Initialize the ViewModel to create or update a note
        // Given: Initialize the ViewModel to create a new note
        initializeViewModelToCreateNewNote()

        // When: The deleteNote function is called
        viewModel.deleteNote()

        // Collect the latest noteUiState value
        val currentState = viewModel.noteUiState.first()

        // Then: Verify that repository is called
        verify(notesRepository).deleteNote(currentState.note)
    }


    /**
     * Initialize the ViewModel without passing in a [NoteDestination.noteIdArg] to create a new note.
     */
    private fun initializeViewModelToCreateNewNote() {
        // Mock the repository
        notesRepository = mock(NotesRepository::class.java)

        // Initialize SavedStateHandle without noteIdArg
        savedStateHandle = SavedStateHandle()

        // Initialize ViewModel before each test, with a test dispatcher
        viewModel = NoteViewModel(savedStateHandle, notesRepository, testDispatcher)
    }

    /**
     * Initialize the ViewModel with a [NoteDestination.noteIdArg] to update an existing note.
     */
    private fun initializeViewModelToUpdateExistingNote() {
        // Mock the repository
        notesRepository = mock(NotesRepository::class.java)

        // SavedStateHandle with noteIdArg
        savedStateHandle = SavedStateHandle(mapOf(NoteDestination.noteIdArg to 1))

        // Mock the repository response to return the mock note
        whenever(notesRepository.getNoteById(1)).thenReturn(flowOf(mockNote1))

        // Initialize ViewModel with the new SavedStateHandle
        viewModel = NoteViewModel(savedStateHandle, notesRepository, testDispatcher)
    }
}