package com.thewhitewings.pouch.ui

import com.thewhitewings.pouch.data.Note
import com.thewhitewings.pouch.data.NotesRepository
import com.thewhitewings.pouch.data.SortOption
import com.thewhitewings.pouch.rules.MainDispatcherRule
import com.thewhitewings.pouch.utils.Zone
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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

    private val initialZone = Zone.CREATIVE
    private val initialSortOption = SortOption.NEWEST_FIRST
    private val mockInitialNotesList = listOf(
        Note(1, "Test Note1", "Test Body1", "2024 1"),
        Note(2, "Test Note2", "Test Body2", "2024 2"),
        Note(3, "Test Note3", "Test Body3", "2024 3")
    )

    /**
     * Set up the test environment before each test.
     */
    @Before
    fun setUp() {
        // Mocking the repository
        notesRepository = mock(NotesRepository::class.java)

        // Mocking the repository methods that are needed
        // because they are being observed by the view model.
        // Their observation starts on the view model initialization
        whenever(notesRepository.getSortOptionFlow(initialZone))
            .thenReturn(flowOf(initialSortOption))
        whenever(notesRepository.getAllNotesStream(initialSortOption))
            .thenReturn(flowOf(mockInitialNotesList))

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
        val expectedZone = Zone.BOX_OF_MYSTERIES
        val expectedMockSortOption = SortOption.OLDEST_FIRST
        val expectedMockNotesList = mockInitialNotesList.reversed()

        whenever(notesRepository.getSortOptionFlow(expectedZone))
            .thenReturn(flowOf(expectedMockSortOption))
        whenever(notesRepository.getAllNotesStream(expectedMockSortOption))
            .thenReturn(flowOf(expectedMockNotesList))

        // Act: Update the zone and trigger the function to collect the sort option
        viewModel.toggleZone()
        assertEquals(expectedZone, viewModel.homeUiState.value.zone)

        // Assert: Verify that the repository's getSortOptionFlow() is called with the new zone
        verify(notesRepository).getSortOptionFlow(expectedZone)

        // Assert: Ensure that the sort option is correctly updated in the UI state
        assertEquals(
            expectedMockSortOption,
            viewModel.homeUiState.value.sortOption
        )
    }

    /**
     * Test that sort option changes are collected and notes list is updated correctly.
     * Happy path for [HomeViewModel.collectSortOptionSearchQueryZoneAndCollectNotesList]
     */
    @Test
    fun `When sort option changes and search query is empty, update notes list correctly`() =
        runTest {
            // Given: the initial sort option is NEWEST_FIRST, and search query is empty
            val mockSortOption = SortOption.OLDEST_FIRST
            val expectedMockNotesList = mockInitialNotesList.reversed()

            whenever(notesRepository.getAllNotesStream(mockSortOption))
                .thenReturn(flowOf(expectedMockNotesList))

            viewModel.updateSortOptionStateForTesting(mockSortOption.id)
            assertEquals(mockSortOption, viewModel.homeUiState.value.sortOption)
            verify(notesRepository).getSortOptionFlow(initialZone)

            verify(notesRepository).getAllNotesStream(mockSortOption)
            assertEquals(expectedMockNotesList, viewModel.homeUiState.value.notesList)
        }

    /**
     * Test that sort option changes are collected and notes list is updated correctly.
     * Happy path for [HomeViewModel.collectSortOptionSearchQueryZoneAndCollectNotesList]
     */
    @Test
    fun `When sort option changes and search query is Not empty, update notes list correctly`() =
        runTest {
            // Given: the initial sort option is NEWEST_FIRST, and search query is empty
            val mockSortOption = SortOption.OLDEST_FIRST
            val mockSearchQuery = "Test"
            val expectedMockNotesList = mockInitialNotesList.reversed()

            whenever(notesRepository.getAllNotesStream(mockSortOption))
                .thenReturn(flowOf(expectedMockNotesList))
            whenever(notesRepository.searchNotesStream(mockSearchQuery, mockSortOption))
                .thenReturn(flowOf(expectedMockNotesList))

            viewModel.updateSortOptionStateForTesting(mockSortOption.id)
            assertEquals(mockSortOption, viewModel.homeUiState.value.sortOption)
            verify(notesRepository).getSortOptionFlow(initialZone)

            viewModel.updateSearchQuery(mockSearchQuery)
            assertEquals(mockSearchQuery, viewModel.homeUiState.value.searchQuery)
            verify(notesRepository).searchNotesStream(mockSearchQuery, mockSortOption)

            assertEquals(expectedMockNotesList, viewModel.homeUiState.value.notesList)
        }

    /**
     * Test that search query changes are collected and notes list is updated correctly.
     * Happy path for [HomeViewModel.collectSortOptionSearchQueryZoneAndCollectNotesList]
     */
    @Test
    fun `When search query changes and search query is Not empty, update notes list correctly`() =
        runTest {
            // Given: the initial search query is empty
            val mockSearchQuery = "Test"
            val expectedMockNotesList = mockInitialNotesList.reversed()

            whenever(notesRepository.searchNotesStream(mockSearchQuery, initialSortOption))
                .thenReturn(flowOf(expectedMockNotesList))

            viewModel.updateSearchQuery(mockSearchQuery)
            assertEquals(mockSearchQuery, viewModel.homeUiState.value.searchQuery)
            verify(notesRepository).searchNotesStream(mockSearchQuery, initialSortOption)

            assertEquals(expectedMockNotesList, viewModel.homeUiState.value.notesList)
        }

    /**
     * Test that search query changes are collected and notes list is updated correctly.
     * Happy path for [HomeViewModel.collectSortOptionSearchQueryZoneAndCollectNotesList]
     */
    @Test
    fun `When search query changes and search query is empty, update notes list correctly`() =
        runTest {
            // Given: the initial search query is empty
            val expectedMockNotesListFirst = mockInitialNotesList.reversed()
            val expectedMockNotesListLast = mockInitialNotesList
            val mockSearchQueryFirstChange = "Test"
            val mockSearchQueryLastChange = ""

            whenever(
                notesRepository.searchNotesStream(
                    mockSearchQueryFirstChange,
                    initialSortOption
                )
            )
                .thenReturn(flowOf(expectedMockNotesListFirst))
            whenever(notesRepository.getAllNotesStream(initialSortOption))
                .thenReturn(flowOf(expectedMockNotesListLast))

            viewModel.updateSearchQuery(mockSearchQueryFirstChange)
            assertEquals(mockSearchQueryFirstChange, viewModel.homeUiState.value.searchQuery)
            verify(notesRepository).searchNotesStream(mockSearchQueryFirstChange, initialSortOption)
            assertEquals(expectedMockNotesListFirst, viewModel.homeUiState.value.notesList)

            viewModel.updateSearchQuery(mockSearchQueryLastChange)
            assertEquals(mockSearchQueryLastChange, viewModel.homeUiState.value.searchQuery)
            verify(notesRepository, times(2)).getAllNotesStream(initialSortOption)
            assertEquals(expectedMockNotesListLast, viewModel.homeUiState.value.notesList)
        }

    /**
     * Test that zone changes are collected and notes list is updated correctly.
     * Happy path for [HomeViewModel.collectSortOptionSearchQueryZoneAndCollectNotesList]
     */
    @Test
    fun `When zone changes and search query is empty, update notes list correctly`() =
        runTest {
            // Given: the initial zone is CREATIVE, and the sort option is NEWEST_FIRST
            val expectedZone = Zone.BOX_OF_MYSTERIES
            val expectedMockNotesList = mockInitialNotesList.reversed()

            whenever(notesRepository.getSortOptionFlow(expectedZone))
                .thenReturn(flowOf(initialSortOption))
            whenever(notesRepository.getAllNotesStream(initialSortOption))
                .thenReturn(flowOf(expectedMockNotesList))

            viewModel.toggleZone()
            assertEquals(expectedZone, viewModel.homeUiState.value.zone)
            verify(notesRepository).getSortOptionFlow(expectedZone)

            verify(notesRepository, times(2)).getAllNotesStream(initialSortOption)
            assertEquals(expectedMockNotesList, viewModel.homeUiState.value.notesList)
        }

    /**
     * Test that zone changes are collected and notes list is updated correctly.
     * Happy path for [HomeViewModel.collectSortOptionSearchQueryZoneAndCollectNotesList]
     */
    @Test
    fun `When zone changes and search query is Not empty, update notes list correctly`() = runTest {
        // Given: the initial zone is CREATIVE, search query is empty, and the sort option is NEWEST_FIRST
        val expectedZone = Zone.BOX_OF_MYSTERIES
        val mockSearchQuery = "Test"
        val expectedMockNotesList = mockInitialNotesList.reversed()

        whenever(notesRepository.searchNotesStream(mockSearchQuery, initialSortOption))
            .thenReturn(flowOf(expectedMockNotesList))
        whenever(notesRepository.getSortOptionFlow(expectedZone))
            .thenReturn(flowOf(initialSortOption))
        whenever(notesRepository.getAllNotesStream(initialSortOption))
            .thenReturn(flowOf(expectedMockNotesList))

        viewModel.updateSearchQuery(mockSearchQuery)
        assertEquals(mockSearchQuery, viewModel.homeUiState.value.searchQuery)
        verify(notesRepository).searchNotesStream(mockSearchQuery, initialSortOption)

        viewModel.toggleZone()
        assertEquals(expectedZone, viewModel.homeUiState.value.zone)
        verify(notesRepository).getSortOptionFlow(expectedZone)

        verify(notesRepository, times(3)).getAllNotesStream(initialSortOption)
        assertEquals(expectedMockNotesList, viewModel.homeUiState.value.notesList)
    }

    /**
     * Test that after [HomeViewModel] is initialized, the state of [HomeViewModel.HomeUiState.showAnimations] is updated correctly.
     * Happy path for [HomeViewModel.updateShowAnimationsStateDelayed]
     */
    @Test
    fun `When the view model is initialized, showAnimations is updated correctly`() = runTest {
        // Given: the initial showAnimations state is true
        val expectedShowAnimations = false
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(expectedShowAnimations, viewModel.homeUiState.value.showAnimations)
    }

}
