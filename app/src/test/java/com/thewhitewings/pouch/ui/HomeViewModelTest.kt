package com.thewhitewings.pouch.ui

import com.thewhitewings.pouch.data.Note
import com.thewhitewings.pouch.data.NotesRepository
import com.thewhitewings.pouch.data.SortOption
import com.thewhitewings.pouch.rules.MainDispatcherRule
import com.thewhitewings.pouch.utils.Zone
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    // Rule to set main dispatcher to a test coroutine dispatcher
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Mocks and ViewModel
    private lateinit var viewModel: HomeViewModel
    private lateinit var notesRepository: NotesRepository

    // Test dispatcher for coroutines
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        // Mocking the repository
        notesRepository = mock(NotesRepository::class.java)

        // Given: Mock repository returns a flow of sort options and notes
        val initialZone = Zone.CREATIVE
        val initialSortOption = SortOption.NEWEST_FIRST
        val mockSecondSortOption = SortOption.OLDEST_FIRST
        val secondZone = Zone.BOX_OF_MYSTERIES
        val mockSortOptionFlow = flowOf(initialSortOption)
        val mockSecondSortOptionFlow = flowOf(mockSecondSortOption)

        val mockNotesList = listOf(
            Note( 1, "Test Note1", "Test Body1",  "2024 1"),
            Note( 2, "Test Note2", "Test Body2",  "2024 2"),
            Note( 3, "Test Note3", "Test Body3",  "2024 3")
            )
        val mockNotesFlow = flowOf(mockNotesList)

        // Mocking the repository methods that are needed
        // because they are being observed by the view model.
        // Their observation starts on the view model initialization
        whenever(notesRepository.getSortOptionFlow(initialZone)).thenReturn(mockSortOptionFlow)
        whenever(notesRepository.getSortOptionFlow(secondZone)).thenReturn(mockSecondSortOptionFlow)

        whenever(notesRepository.getAllNotesStream(initialSortOption)).thenReturn(mockNotesFlow)
        whenever(notesRepository.getAllNotesStream(mockSecondSortOption)).thenReturn(mockNotesFlow)

        // Initialize ViewModel before each test, with a test dispatcher
        viewModel = HomeViewModel(notesRepository, testDispatcher)
    }

    // Happy path
    @Test
    fun `test initialization collects sort option flow`() = runTest {
        // Given: Mock repository returns a flow with a sort option
        val testZone = Zone.CREATIVE
        val mockSortOption = SortOption.NEWEST_FIRST
        val mockFlow = flowOf(mockSortOption)

        // When the repository's getSortOptionFlow is called, return the mock flow
        whenever(notesRepository.getSortOptionFlow(testZone)).thenReturn(mockFlow)

        // Then: Get the first emission from homeUiState
        val uiState = viewModel.homeUiState.first()

        // Verify that the homeUiState contains the correct sortOption
        assertEquals(mockSortOption, uiState.sortOption)
    }

    // Happy path
    @Test
    fun `test load all notes when search query is empty`() = runTest {
        // Given: Mock repository returns a flow of notes when getAllNotesStream is called
        val mockSortOption = SortOption.NEWEST_FIRST
        val mockNotesList = listOf(Note(1, "Test Note", "Test Body", "2024"))
        val mockFlow = flowOf(mockNotesList)

        // Mocking the repository to return the flow when getAllNotesStream is called
//        viewModel = HomeViewModel(notesRepository, testDispatcher) // when it's before, i get 'Expected... Actual...' error.
        whenever(notesRepository.getAllNotesStream(mockSortOption)).thenReturn(mockFlow)
//        viewModel = HomeViewModel(notesRepository, testDispatcher) // when it's after, i get 'Wanted 1 time... but was 2 times...' error.

        // Then: Collect the homeUiState and verify notesList is updated with mock data
        val uiState = viewModel.homeUiState.first() // This will collect the latest state from the flow

        // Verify that the notesList in the UI state matches the mockNotesList
        assertEquals(mockNotesList, uiState.notesList)

        // Verify: That getAllNotesStream was called with the correct sort option
        verify(notesRepository).getAllNotesStream(mockSortOption)
    }

    /**
     * Test that zone changes are collected and sort option is updated correctly.
     * Test for [HomeViewModel.collectZoneAndCollectSortOption]
     */
    @Test
    fun `When zone changes, update sort option correctly`() = runTest {
        // Given: the initial zone is CREATIVE, and the sort option is NEWEST_FIRST
        val mockSecondSortOption = SortOption.OLDEST_FIRST

        // Act: Update the zone and trigger the function to collect the sort option
        viewModel.toggleZone()

        // Collect the state of homeUiState from the ViewModel
        val uiState = viewModel.homeUiState.value

        // Assert: Ensure that the sort option is correctly updated
        assertEquals(mockSecondSortOption, uiState.sortOption)
    }


}
