package com.thewhitewings.pouch.presentation

import com.thewhitewings.pouch.feature_note.domain.model.Note
import com.thewhitewings.pouch.feature_note.domain.repository.OfflineNotesRepository
import com.thewhitewings.pouch.feature_note.domain.util.SortOption
import com.thewhitewings.pouch.feature_note.presentation.notes.NotesUiState
import com.thewhitewings.pouch.feature_note.presentation.notes.NotesViewModel
import com.thewhitewings.pouch.feature_note.util.Zone
import com.thewhitewings.pouch.rules.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
class NotesViewModelTest {

    // Rule to set main dispatcher to a test coroutine dispatcher
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Mocks and ViewModel
    private lateinit var viewModel: NotesViewModel
    private lateinit var notesRepository: OfflineNotesRepository

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
        notesRepository = mock(OfflineNotesRepository::class.java)

        // Mocking the repository methods that are needed
        // because they are being observed by the view model.
        // Their observation starts on the view model initialization
        whenever(notesRepository.getSortOptionFlow(initialZone))
            .thenReturn(flowOf(initialSortOption))
        whenever(notesRepository.getAllNotesStream(initialSortOption))
            .thenReturn(flowOf(mockInitialNotesList))

        // Initialize ViewModel before each test, with a test dispatcher
        viewModel = NotesViewModel(notesRepository, testDispatcher)
    }

    /**
     * Test that zone changes are collected and sort option is updated correctly.
     * Happy path test for [NotesViewModel.collectZoneAndCollectSortOption]
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
        assertEquals(expectedZone, viewModel.uiState.value.zone)

        // Assert: Verify that the repository's getSortOptionFlow() is called with the new zone
        verify(notesRepository).getSortOptionFlow(expectedZone)

        // Assert: Ensure that the sort option is correctly updated in the UI state
        assertEquals(
            expectedMockSortOption,
            viewModel.uiState.value.sortOption
        )
    }

    /**
     * Test that sort option changes are collected and notes list is updated correctly.
     * Happy path for [NotesViewModel.collectSortOptionSearchQueryZoneAndCollectNotesList]
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
            assertEquals(mockSortOption, viewModel.uiState.value.sortOption)
            verify(notesRepository).getSortOptionFlow(initialZone)

            verify(notesRepository).getAllNotesStream(mockSortOption)
            assertEquals(expectedMockNotesList, viewModel.uiState.value.notesList)
        }

    /**
     * Test that sort option changes are collected and notes list is updated correctly.
     * Happy path for [NotesViewModel.collectSortOptionSearchQueryZoneAndCollectNotesList]
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
            assertEquals(mockSortOption, viewModel.uiState.value.sortOption)
            verify(notesRepository).getSortOptionFlow(initialZone)

            viewModel.updateSearchQuery(mockSearchQuery)
            assertEquals(mockSearchQuery, viewModel.uiState.value.searchQuery)
            verify(notesRepository).searchNotesStream(mockSearchQuery, mockSortOption)

            assertEquals(expectedMockNotesList, viewModel.uiState.value.notesList)
        }

    /**
     * Test that search query changes are collected and notes list is updated correctly.
     * Happy path for [NotesViewModel.collectSortOptionSearchQueryZoneAndCollectNotesList]
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
            assertEquals(mockSearchQuery, viewModel.uiState.value.searchQuery)
            verify(notesRepository).searchNotesStream(mockSearchQuery, initialSortOption)

            assertEquals(expectedMockNotesList, viewModel.uiState.value.notesList)
        }

    /**
     * Test that search query changes are collected and notes list is updated correctly.
     * Happy path for [NotesViewModel.collectSortOptionSearchQueryZoneAndCollectNotesList]
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
            assertEquals(mockSearchQueryFirstChange, viewModel.uiState.value.searchQuery)
            verify(notesRepository).searchNotesStream(mockSearchQueryFirstChange, initialSortOption)
            assertEquals(expectedMockNotesListFirst, viewModel.uiState.value.notesList)

            viewModel.updateSearchQuery(mockSearchQueryLastChange)
            assertEquals(mockSearchQueryLastChange, viewModel.uiState.value.searchQuery)
            verify(notesRepository, times(2)).getAllNotesStream(initialSortOption)
            assertEquals(expectedMockNotesListLast, viewModel.uiState.value.notesList)
        }

    /**
     * Test that zone changes are collected and notes list is updated correctly.
     * Happy path for [NotesViewModel.collectSortOptionSearchQueryZoneAndCollectNotesList]
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
            assertEquals(expectedZone, viewModel.uiState.value.zone)
            verify(notesRepository).getSortOptionFlow(expectedZone)

            verify(notesRepository, times(2)).getAllNotesStream(initialSortOption)
            assertEquals(expectedMockNotesList, viewModel.uiState.value.notesList)
        }

    /**
     * Test that zone changes are collected and notes list is updated correctly.
     * Happy path for [NotesViewModel.collectSortOptionSearchQueryZoneAndCollectNotesList]
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
        assertEquals(mockSearchQuery, viewModel.uiState.value.searchQuery)
        verify(notesRepository).searchNotesStream(mockSearchQuery, initialSortOption)

        viewModel.toggleZone()
        assertEquals(expectedZone, viewModel.uiState.value.zone)
        verify(notesRepository).getSortOptionFlow(expectedZone)

        verify(notesRepository, times(3)).getAllNotesStream(initialSortOption)
        assertEquals(expectedMockNotesList, viewModel.uiState.value.notesList)
    }

    /**
     * Test that after [NotesViewModel] is initialized, the state of [NotesUiState.showAnimations] is updated correctly.
     * Happy path for [NotesViewModel.updateShowAnimationsStateDelayed]
     */
    @Test
    fun `When the view model is initialized, showAnimations is updated correctly`() = runTest {
        // Given: the initial showAnimations state is true
        val expectedShowAnimations = false
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(expectedShowAnimations, viewModel.uiState.value.showAnimations)
    }

    /**
     * Test that [NotesViewModel.updateSearchQuery] updates the search query state correctly.
     * Happy path for [NotesViewModel.updateSearchQuery]
     */
    @Test
    fun `When updating search query, update search query state correctly`() =
        runTest {
            // Given: the initial search query is empty
            val mockSearchQuery = "Test"
            val expectedMockNotesList = mockInitialNotesList.reversed()

            /*
            Updating the search query will trigger the collecting of the flow of
            notesRepository.searchNotesStream in
            collectSortOptionSearchQueryZoneAndCollectNotesList function.
            */
            whenever(notesRepository.searchNotesStream(mockSearchQuery, initialSortOption))
                .thenReturn(flowOf(expectedMockNotesList))

            viewModel.updateSearchQuery(mockSearchQuery)
            assertEquals(mockSearchQuery, viewModel.uiState.value.searchQuery)
        }

    /**
     * Test that [NotesViewModel.updateSortOption] saves the sort option with [notesRepository.saveSortOption].
     * Happy path for [NotesViewModel.updateSortOption]
     */
    @Test
    fun `When updating sort option, save the sort option`() = runTest {
        // Given: the initial sort option is NEWEST_FIRST, and search query is empty
        val mockSortOption = SortOption.OLDEST_FIRST
        val expectedMockNotesList = mockInitialNotesList.reversed()

        /*
        Updating the sort option with updateSortOption will trigger the collecting
        of the flow of notesRepository.getSortOptionFlow(zone) in
        collectZoneAndCollectSortOption function and the collecting in
        collectSortOptionSearchQueryZoneAndCollectNotesList function.
        */
        whenever(notesRepository.getAllNotesStream(mockSortOption))
            .thenReturn(flowOf(expectedMockNotesList))

        viewModel.updateSortOption(mockSortOption.id)
        verify(notesRepository).saveSortOption(mockSortOption, initialZone)
        verify(notesRepository).getSortOptionFlow(initialZone)
    }

    /**
     * Test that [NotesViewModel.updateSortOptionStateForTesting] updates the sort option state correctly.
     * Happy path for [NotesViewModel.updateSortOptionStateForTesting]
     */
    @Test
    fun `When updating sort option, update sort option state correctly`() = runTest {
        // Given: the initial sort option is NEWEST_FIRST, and search query is empty
        val mockSortOption = SortOption.OLDEST_FIRST
        val expectedMockNotesList = mockInitialNotesList.reversed()

        /*
        Updating the sort option with updateSortOptionStateForTesting will trigger the collecting
        of the flow of notesRepository.getSortOptionFlow(zone) in
        collectZoneAndCollectSortOption function.
        */
        whenever(notesRepository.getAllNotesStream(mockSortOption))
            .thenReturn(flowOf(expectedMockNotesList))

        viewModel.updateSortOptionStateForTesting(mockSortOption.id)
        assertEquals(mockSortOption, viewModel.uiState.value.sortOption)
        verify(notesRepository).getSortOptionFlow(initialZone)
    }

    /**
     * Test that [NotesViewModel.deleteNote] interacts with the repository to delete a note.
     * Happy path for [NotesViewModel.deleteNote]
     */
    @Test
    fun `When deleteNote is called, call deleteNote on notesRepository`() = runTest {
        val mockNote = Note(1, "Test Note", "Test Body", "2024 1")
        viewModel.deleteNote(mockNote)
        verify(notesRepository).deleteNote(mockNote)
    }

    /**
     * Test that [NotesViewModel.knockBoxOfMysteries] triggers the sequence of
     * revealing the Box of mysteries.
     * The zone toggles to [Zone.BOX_OF_MYSTERIES] after 5 times of calling
     * [NotesViewModel.knockBoxOfMysteries] by calling
     * [NotesViewModel.startBoxRevealTimeout] and then [NotesViewModel.toggleZone] gets called.
     * Happy path for [NotesViewModel.knockBoxOfMysteries]
     */
    @Test
    fun `When knocked on Bom 5 times, zone is toggled to Bom`() =
        runTest {
            // Given: the initial zone is CREATIVE, and the search query is empty
            val expectedZone = Zone.BOX_OF_MYSTERIES

            repeat(5) {
                viewModel.knockBoxOfMysteries()
            }

            whenever(notesRepository.getSortOptionFlow(expectedZone))
                .thenReturn(flowOf(initialSortOption))
            whenever(notesRepository.getAllNotesStream(initialSortOption))
                .thenReturn(flowOf(mockInitialNotesList))

            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(Zone.BOX_OF_MYSTERIES, viewModel.uiState.value.zone)
        }

    /**
     * Test that [NotesViewModel.knockBoxOfMysteries] triggers the sequence of
     * revealing the Box of mysteries.
     * The zone doesn't toggle to [Zone.BOX_OF_MYSTERIES] before 5 times of calling
     * [NotesViewModel.knockBoxOfMysteries].
     * Case: less than 5 times of calling [NotesViewModel.knockBoxOfMysteries]
     * for [NotesViewModel.knockBoxOfMysteries]
     */
    @Test
    fun `When knocked on Bom less than 5 times, zone should not change`() =
        runTest {
            // Given: the initial zone is CREATIVE
            repeat(4) {
                viewModel.knockBoxOfMysteries()
            }
            assertEquals(initialZone, viewModel.uiState.value.zone)
        }

    /**
     * Test that [NotesViewModel.toggleZone] toggles the zone and updates the UI state correctly.
     * Happy path for [NotesViewModel.toggleZone]
     */
    @Test
    fun `When toggleZone is called, zone changes and ui state updates correctly`() = runTest {
        // Given: the initial zone is CREATIVE
        whenever(notesRepository.getSortOptionFlow(Zone.BOX_OF_MYSTERIES))
            .thenReturn(flowOf(initialSortOption))
        whenever(notesRepository.getAllNotesStream(initialSortOption))
            .thenReturn(flowOf(mockInitialNotesList))

        viewModel.toggleZone()
        verify(notesRepository).toggleZone()
        assertEquals(Zone.BOX_OF_MYSTERIES, viewModel.uiState.value.zone)
        assertEquals("", viewModel.uiState.value.searchQuery)
        assertEquals(true, viewModel.uiState.value.showAnimations)

        viewModel.toggleZone()
        verify(notesRepository, times(2)).toggleZone()
        assertEquals(Zone.CREATIVE, viewModel.uiState.value.zone)
    }
}
