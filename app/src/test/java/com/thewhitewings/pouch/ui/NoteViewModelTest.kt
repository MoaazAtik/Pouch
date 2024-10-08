package com.thewhitewings.pouch.ui

import androidx.lifecycle.SavedStateHandle
import com.thewhitewings.pouch.data.NotesRepository
import com.thewhitewings.pouch.data.mockNote1
import com.thewhitewings.pouch.rules.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
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

    @Before
    fun setUp() {
        // Mock the repository
        notesRepository = mock(NotesRepository::class.java)

//        // Initialize SavedStateHandle without noteIdArg
//        savedStateHandle = SavedStateHandle()
//
//        // Initialize ViewModel before each test, with a test dispatcher
//        viewModel = NoteViewModel(savedStateHandle, notesRepository, testDispatcher)

            // SavedStateHandle with noteIdArg
            savedStateHandle = SavedStateHandle(mapOf(NoteDestination.noteIdArg to 1))

            // Mock the repository response to return the mock note
            whenever(notesRepository.getNoteById(1)).thenReturn(flowOf(mockNote1))

            // Initialize ViewModel with the new SavedStateHandle
            viewModel = NoteViewModel(savedStateHandle, notesRepository, testDispatcher)
    }

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

            // Then: Verify that the initial NoteUiState remains unchanged
            val expectedState = NoteViewModel.NoteUiState()
            val currentState = viewModel.noteUiState.value
            assertEquals(expectedState, currentState)

            // Additionally, ensure that no interaction with notesRepository happens
            verify(notesRepository, never()).getNoteById(anyInt())
        }

    @Test
    fun `When initializing note and noteId is provided, get note from repository and update ui state`() =
        runTest {
//            // SavedStateHandle with noteIdArg
//            savedStateHandle = SavedStateHandle(mapOf(NoteDestination.noteIdArg to 1))
//
//            // Mock the repository response to return the mock note
//            whenever(notesRepository.getNoteById(1)).thenReturn(flowOf(mockNote1))
//
//            // Initialize ViewModel with the new SavedStateHandle
//            viewModel = NoteViewModel(savedStateHandle, notesRepository, testDispatcher)

            // Collect the latest noteUiState value
            val currentState = viewModel.noteUiState.first()

            // Assert that the repository function was called with the correct noteId
            verify(notesRepository).getNoteById(1)

            // Assert that noteUiState contains the correct note
            assertEquals(mockNote1, currentState.note)
        }

}