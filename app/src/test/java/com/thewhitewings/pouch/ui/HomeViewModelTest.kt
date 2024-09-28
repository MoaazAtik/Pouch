package com.thewhitewings.pouch.ui

import com.thewhitewings.pouch.data.Note
import com.thewhitewings.pouch.data.NotesRepository
import com.thewhitewings.pouch.data.SortOption
import com.thewhitewings.pouch.rules.MainDispatcherRule
import com.thewhitewings.pouch.utils.Zone
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.times
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

        val initialZone = Zone.CREATIVE
        val initialSortOption = SortOption.NEWEST_FIRST
        val mockSecondSortOption = SortOption.OLDEST_FIRST
        val secondZone = Zone.BOX_OF_MYSTERIES
        val mockSortOptionFlow = flowOf(initialSortOption)
        val mockSecondSortOptionFlow = flowOf(mockSecondSortOption)

        val mockNotesList = listOf(
            Note(1, "Test Note1", "Test Body1", "2024 1"),
            Note(2, "Test Note2", "Test Body2", "2024 2"),
            Note(3, "Test Note3", "Test Body3", "2024 3")
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

    /**
     * Test that zone changes are collected and sort option is updated correctly.
     * Happy path test for [HomeViewModel.collectZoneAndCollectSortOption]
     */
    @Test
    fun `When zone changes, update sort option correctly`() = runTest {
        // Given: the initial zone is CREATIVE, and the sort option is NEWEST_FIRST
        val expectedMockSortOption = SortOption.OLDEST_FIRST
        val expectedZone = Zone.BOX_OF_MYSTERIES

        // Act: Update the zone and trigger the function to collect the sort option
        viewModel.toggleZone()

        // Assert: Verify that the repository's getSortOptionFlow() is called with the new zone
        verify(notesRepository).getSortOptionFlow(expectedZone)

        // Collect the state of homeUiState from the ViewModel
        val uiState = viewModel.homeUiState.value
        // Assert: Ensure that the sort option is correctly updated in the UI state
        assertEquals(
            expectedMockSortOption,
            uiState.sortOption
        )  // Assert the sort option was updated
    }

    /**
     * Test that sort option changes are collected and notes list is updated correctly.
     * Happy path for [HomeViewModel.collectSortOptionSearchQueryZoneAndCollectNotesList]
     */
    @Test
    fun `When sort option changes and search query is empty, update notes list correctly`() =
        runTest {
            // Given: the initial sort option is NEWEST_FIRST, and search query is empty
            val expectedMockNotesList = listOf(
                Note(4, "Test Note4", "Test Body4", "2024 4"),
                Note(5, "Test Note5", "Test Body5", "2024 5"),
                Note(6, "Test Note6", "Test Body6", "2024 6")
            )
            val mockSortOption = SortOption.OLDEST_FIRST
            val initialZone = Zone.CREATIVE

            whenever(notesRepository.getSortOptionFlow(initialZone))
                .thenReturn(flowOf(mockSortOption))
            whenever(notesRepository.getAllNotesStream(mockSortOption))
                .thenReturn(flowOf(expectedMockNotesList))
            viewModel = HomeViewModel(notesRepository, testDispatcher)

            viewModel.updateSortOption(sortOptionId = mockSortOption.id)

            verify(notesRepository).getAllNotesStream(mockSortOption)

            val uiState = viewModel.homeUiState.value
            assertEquals(expectedMockNotesList, uiState.notesList)
        }

    /**
     * Test that sort option changes are collected and notes list is updated correctly.
     * Happy path for [HomeViewModel.collectSortOptionSearchQueryZoneAndCollectNotesList]
     */
    @Test
    fun `When sort option changes and search query is Not empty, update notes list correctly`() = runTest {
        val expectedMockNotesList = listOf(
            Note(4, "Test Note4", "Test Body4", "2024 4"),
            Note(5, "Test Note5", "Test Body5", "2024 5"),
            Note(6, "Test Note6", "Test Body6", "2024 6")
        )
        // Given: the initial sort option is NEWEST_FIRST, and search query is empty
        val mockSortOption = SortOption.OLDEST_FIRST
        val mockSearchQuery = "Test"
        val initialZone = Zone.CREATIVE

        whenever(notesRepository.getSortOptionFlow(initialZone))
            .thenReturn(flowOf(mockSortOption))
        whenever(notesRepository.searchNotesStream(mockSearchQuery, mockSortOption))
            .thenReturn(flowOf(expectedMockNotesList))
        viewModel = HomeViewModel(notesRepository, testDispatcher)

        viewModel.updateSortOption(mockSortOption.id)
        viewModel.updateSearchQuery(mockSearchQuery)

        verify(notesRepository).searchNotesStream(mockSearchQuery, mockSortOption)

        assertEquals(viewModel.homeUiState.value.sortOption, mockSortOption)
        assertEquals(viewModel.homeUiState.value.searchQuery, mockSearchQuery)
        assertEquals(viewModel.homeUiState.value.notesList, expectedMockNotesList)
    }

    /**
     * Test that search query changes are collected and notes list is updated correctly.
     * Happy path for [HomeViewModel.collectSortOptionSearchQueryZoneAndCollectNotesList]
     */
    @Test
    fun `When search query changes and search query is Not empty, update notes list correctly`() = runTest {
        val expectedMockNotesList = listOf(
            Note(4, "Test Note4", "Test Body4", "2024 4"),
            Note(5, "Test Note5", "Test Body5", "2024 5"),
            Note(6, "Test Note6", "Test Body6", "2024 6")
        )
        // Given: the initial search query is empty
        val mockSearchQuery = "Test"
        val initialSortOption = SortOption.NEWEST_FIRST

        whenever(notesRepository.searchNotesStream(mockSearchQuery, initialSortOption))
            .thenReturn(flowOf(expectedMockNotesList))

        viewModel.updateSearchQuery(mockSearchQuery)

        verify(notesRepository).searchNotesStream(mockSearchQuery, initialSortOption)

        assertEquals(viewModel.homeUiState.value.searchQuery, mockSearchQuery)
        assertEquals(viewModel.homeUiState.value.notesList, expectedMockNotesList)
    }

    /**
     * Test that search query changes are collected and notes list is updated correctly.
     * Happy path for [HomeViewModel.collectSortOptionSearchQueryZoneAndCollectNotesList]
     */
    @Test
    fun `When search query changes and search query is empty, update notes list correctly`() = runTest {

        // Given: the initial search query is empty
        val expectedMockNotesListFirst = listOf(
            Note(4, "Test Note4", "Test Body4", "2024 4"),
            Note(5, "Test Note5", "Test Body5", "2024 5"),
            Note(6, "Test Note6", "Test Body6", "2024 6")
        )
        val expectedMockNotesListLast = expectedMockNotesListFirst.reversed()
        val mockSearchQueryFirstChange = "Test"
        val mockSearchQueryLastChange = ""
        val initialSortOption = SortOption.NEWEST_FIRST

        whenever(notesRepository.searchNotesStream(mockSearchQueryFirstChange, initialSortOption))
            .thenReturn(flowOf(expectedMockNotesListFirst))
        viewModel.updateSearchQuery(mockSearchQueryFirstChange)
        verify(notesRepository).searchNotesStream(mockSearchQueryFirstChange, initialSortOption)
        assertEquals(expectedMockNotesListFirst, viewModel.homeUiState.value.notesList)

        whenever(notesRepository.getAllNotesStream(initialSortOption))
            .thenReturn(flowOf(expectedMockNotesListLast))
        viewModel.updateSearchQuery(mockSearchQueryLastChange)
        verify(notesRepository, times(2)).getAllNotesStream(initialSortOption)
        assertEquals(expectedMockNotesListLast, viewModel.homeUiState.value.notesList)
    }


}
