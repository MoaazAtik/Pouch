package com.thewhitewings.pouch.domain

import com.thewhitewings.pouch.feature_note.data.data_source.NoteDao
import com.thewhitewings.pouch.feature_note.data.repository.OfflineNotesRepositoryImpl
import com.thewhitewings.pouch.feature_note.domain.model.Note
import com.thewhitewings.pouch.feature_note.domain.repository.PouchPreferences
import com.thewhitewings.pouch.feature_note.domain.util.SortOption
import com.thewhitewings.pouch.feature_note.util.Zone
import com.thewhitewings.pouch.mocks.azSortedNotesLocal
import com.thewhitewings.pouch.mocks.azSortedNotesUtc
import com.thewhitewings.pouch.mocks.mockNote1
import com.thewhitewings.pouch.mocks.mockNoteFromRepository
import com.thewhitewings.pouch.mocks.mockNoteToDao
import com.thewhitewings.pouch.mocks.mockNoteWithTitleAndBody
import com.thewhitewings.pouch.mocks.newestFirstNotesLocal
import com.thewhitewings.pouch.mocks.newestFirstNotesUtc
import com.thewhitewings.pouch.mocks.oldestFirstNotesLocal
import com.thewhitewings.pouch.mocks.oldestFirstNotesUtc
import com.thewhitewings.pouch.mocks.zaSortedNotesLocal
import com.thewhitewings.pouch.mocks.zaSortedNotesUtc
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class OfflineNotesRepositoryTest {

    // The repository being tested
    private lateinit var repository: OfflineNotesRepositoryImpl

    // Mocked dependencies
    private lateinit var creativeNoteDao: NoteDao
    private lateinit var bomNoteDao: NoteDao
    private lateinit var pouchPreferences: PouchPreferences

    // Mock Dao for the current zone
    private lateinit var currentZoneDao: NoteDao

    @Before
    fun setUp() {
        // Initialize mocks
        creativeNoteDao = mock(NoteDao::class.java)
        bomNoteDao = mock(NoteDao::class.java)
        pouchPreferences = mock(PouchPreferences::class.java)

        // Set the currentZoneDao to creativeNoteDao initially
        currentZoneDao = creativeNoteDao

        // Initialize the repository with the mocks
        repository = OfflineNotesRepositoryImpl(
            creativeNoteDao = creativeNoteDao,
            bomNoteDao = bomNoteDao,
            pouchPreferences = pouchPreferences
        )
    }

    /**
     * Create a note and verify that the note is inserted via the Dao of the current zone.
     * Happy path for [OfflineNotesRepositoryImpl.createNote]
     */
    @Test
    fun offlineNotesRepository_createNote_insertsNoteToCurrentZone() = runTest {
        // When: createNote
        repository.createNote(mockNoteWithTitleAndBody)

        // Then: Verify that the note was inserted via the Dao of the current zone
        verify(currentZoneDao).insert(any())
    }

    /**
     * Get a note by ID and verify that the note is returned correctly.
     * Happy path for [OfflineNotesRepositoryImpl.getNoteStream]
     */
    @Test
    fun offlineNotesRepository_getNoteStream_returnsCorrectNote() = runTest {
        // Mock the Dao to return a note with a non-formatted timestamp
        whenever(currentZoneDao.getNoteStream(1)).thenReturn(flowOf(mockNoteWithTitleAndBody))

        // When: We retrieve the note by ID
        val result = repository.getNoteStream(1).first()

        // Then: Verify that the note getting call with the correct id passed
        // to the Dao of the current zone
        verify(currentZoneDao).getNoteStream(1)
        // Then: Assert that the returned note has the formatted timestamp
        assertEquals(mockNoteFromRepository, result)
    }

    /**
     * When querying all notes in the repository sorted by [SortOption.A_Z], get all notes in Local timezone and sorted correctly.
     * Happy path for [OfflineNotesRepositoryImpl.getAllNotesStream]
     */
    @Test
    fun offlineNotesRepository_getAllNotesStreamSortedAz_returnFormattedAndSortedNotes() = runTest {
        // Mock the Dao to return the notes in UTC timezone
        whenever(currentZoneDao.getAllNotes(SortOption.A_Z.name))
            .thenReturn(flowOf(azSortedNotesUtc))

        // When: Retrieve the notes stream sorted by A_Z
        val retrievedNotes = repository.getAllNotesStream(SortOption.A_Z).first()

        // Then: Verify that the note getting call with the correct sort option passed
        // to the Dao of the current zone
        verify(currentZoneDao).getAllNotes(SortOption.A_Z.name)
        // Then: Assert that the notes are correctly formatted and sorted
        assertEquals(azSortedNotesLocal, retrievedNotes)
    }

    /**
     * When querying all notes in the repository sorted by [SortOption.Z_A], get all notes in Local timezone and sorted correctly.
     * Happy path for [OfflineNotesRepositoryImpl.getAllNotesStream]
     */
    @Test
    fun offlineNotesRepository_getAllNotesStreamSortedZa_returnFormattedAndSortedNotes() = runTest {
        // Mock the Dao to return the notes in UTC timezone
        whenever(currentZoneDao.getAllNotes(SortOption.Z_A.name))
            .thenReturn(flowOf(zaSortedNotesUtc))

        // When: Retrieve the notes stream sorted by Z_A
        val retrievedNotes = repository.getAllNotesStream(SortOption.Z_A).first()

        // Then: Verify that the note getting call with the correct sort option passed
        // to the Dao of the current zone
        verify(currentZoneDao).getAllNotes(SortOption.Z_A.name)
        // Then: Assert that the notes are correctly formatted and sorted
        assertEquals(zaSortedNotesLocal, retrievedNotes)
    }

    /**
     * When querying all notes in the repository sorted by [SortOption.OLDEST_FIRST], get all notes in Local timezone and sorted correctly.
     * Happy path for [OfflineNotesRepositoryImpl.getAllNotesStream]
     */
    @Test
    fun offlineNotesRepository_getAllNotesStreamSortedOldestFirst_returnFormattedAndSortedNotes() =
        runTest {
            // Mock the Dao to return the notes in UTC timezone
            whenever(currentZoneDao.getAllNotes(SortOption.OLDEST_FIRST.name))
                .thenReturn(flowOf(oldestFirstNotesUtc))

            // When: Retrieve the notes stream sorted by OLDEST_FIRST
            val retrievedNotes = repository.getAllNotesStream(SortOption.OLDEST_FIRST).first()

            // Then: Verify that the note getting call with the correct sort option passed
            // to the Dao of the current zone
            verify(currentZoneDao).getAllNotes(SortOption.OLDEST_FIRST.name)
            // Then: Assert that the notes are correctly formatted and sorted
            assertEquals(oldestFirstNotesLocal, retrievedNotes)
        }

    /**
     * When querying all notes in the repository sorted by [SortOption.NEWEST_FIRST], get all notes in Local timezone and sorted correctly.
     * Happy path for [OfflineNotesRepositoryImpl.getAllNotesStream]
     */
    @Test
    fun offlineNotesRepository_getAllNotesStreamSortedNewestFirst_returnFormattedAndSortedNotes() =
        runTest {
            // Mock the Dao to return the notes in UTC timezone
            whenever(currentZoneDao.getAllNotes(SortOption.NEWEST_FIRST.name))
                .thenReturn(flowOf(newestFirstNotesUtc))

            // When: Retrieve the notes stream sorted by NEWEST_FIRST
            val retrievedNotes = repository.getAllNotesStream(SortOption.NEWEST_FIRST).first()

            // Then: Verify that the note getting call with the correct sort option passed
            // to the Dao of the current zone
            verify(currentZoneDao).getAllNotes(SortOption.NEWEST_FIRST.name)
            // Then: Assert that the notes are correctly formatted and sorted
            assertEquals(newestFirstNotesLocal, retrievedNotes)
        }

    /**
     * When searching for notes, get filtered notes sorted correctly.
     * Happy path for [OfflineNotesRepositoryImpl.searchNotesStream]
     */
    @Test
    fun offlineNotesRepository_searchNotes_returnFilteredAndSortedNotes() = runTest {
        // Mock the Dao to return the notes in UTC timezone
        whenever(currentZoneDao.searchNotes("Mo", SortOption.A_Z.name))
            .thenReturn(flowOf(listOf(mockNoteWithTitleAndBody)))

        // When: Search notes containing "Mo"
        val retrievedNotes = repository.searchNotesStream("Mo", SortOption.A_Z).first()

        // Then: Verify that the note searching call with the correct query passed
        // to the Dao of the current zone
        verify(currentZoneDao).searchNotes("Mo", SortOption.A_Z.name)
        // Then: Check if the notes containing "Mo" are returned
        assertEquals(listOf(mockNoteFromRepository), retrievedNotes)
    }

    /**
     * When searching for notes sorted by [SortOption.A_Z], get filtered notes sorted correctly.
     * Happy path for [OfflineNotesRepositoryImpl.searchNotesStream]
     */
    @Test
    fun offlineNotesRepository_searchNotesAndSortAz_returnFilteredAndSortedNotes() = runTest {
        // Mock the Dao to return the notes in UTC timezone
        whenever(currentZoneDao.searchNotes("Content", SortOption.A_Z.name))
            .thenReturn(flowOf(azSortedNotesUtc))

        // When: Retrieve the notes stream sorted by A_Z
        val retrievedNotes = repository.searchNotesStream("Content", SortOption.A_Z).first()

        // Then: Verify that the note searching call with the correct query passed
        // to the Dao of the current zone
        verify(currentZoneDao).searchNotes("Content", SortOption.A_Z.name)
        // Then: Assert that the notes are correctly formatted and sorted
        assertEquals(azSortedNotesLocal, retrievedNotes)
    }

    /**
     * When searching for notes sorted by [SortOption.Z_A], get filtered notes sorted correctly.
     * Happy path for [OfflineNotesRepositoryImpl.searchNotesStream]
     */
    @Test
    fun offlineNotesRepository_searchNotesAndSortZa_returnFilteredAndSortedNotes() = runTest {
        // Mock the Dao to return the notes in UTC timezone
        whenever(currentZoneDao.searchNotes("Content", SortOption.Z_A.name))
            .thenReturn(flowOf(zaSortedNotesUtc))

        // When: Retrieve the notes stream sorted by Z_A
        val retrievedNotes = repository.searchNotesStream("Content", SortOption.Z_A).first()

        // Then: Verify that the note searching call with the correct query passed
        // to the Dao of the current zone
        verify(currentZoneDao).searchNotes("Content", SortOption.Z_A.name)
        // Then: Assert that the notes are correctly formatted and sorted
        assertEquals(zaSortedNotesLocal, retrievedNotes)
    }

    /**
     * When searching for notes sorted by [SortOption.OLDEST_FIRST], get filtered notes sorted correctly.
     * Happy path for [OfflineNotesRepositoryImpl.searchNotesStream]
     */
    @Test
    fun offlineNotesRepository_searchNotesAndSortOldestFirst_returnFilteredAndSortedNotes() =
        runTest {
            // Mock the Dao to return the notes in UTC timezone
            whenever(currentZoneDao.searchNotes("Content", SortOption.OLDEST_FIRST.name))
                .thenReturn(flowOf(oldestFirstNotesUtc))

            // When: Retrieve the notes stream sorted by OLDEST_FIRST
            val retrievedNotes =
                repository.searchNotesStream("Content", SortOption.OLDEST_FIRST).first()

            // Then: Verify that the note searching call with the correct query passed
            // to the Dao of the current zone
            verify(currentZoneDao).searchNotes("Content", SortOption.OLDEST_FIRST.name)
            // Then: Assert that the notes are correctly formatted and sorted
            assertEquals(oldestFirstNotesLocal, retrievedNotes)
        }

    /**
     * When searching for notes sorted by [SortOption.NEWEST_FIRST], get filtered notes sorted correctly.
     * Happy path for [OfflineNotesRepositoryImpl.searchNotesStream]
     */
    @Test
    fun offlineNotesRepository_searchNotesAndSortNewestFirst_returnFilteredAndSortedNotes() =
        runTest {
            // Mock the Dao to return the notes in UTC timezone
            whenever(currentZoneDao.searchNotes("Content", SortOption.NEWEST_FIRST.name))
                .thenReturn(flowOf(newestFirstNotesUtc))

            // When: Retrieve the notes stream sorted by NEWEST_FIRST
            val retrievedNotes =
                repository.searchNotesStream("Content", SortOption.NEWEST_FIRST).first()

            // Then: Verify that the note searching call with the correct query passed
            // to the Dao of the current zone
            verify(currentZoneDao).searchNotes("Content", SortOption.NEWEST_FIRST.name)
            // Then: Assert that the notes are correctly formatted and sorted
            assertEquals(newestFirstNotesLocal, retrievedNotes)
        }

    /**
     * When searching for notes with no matches, get an empty list.
     * Case: Search notes with no matches.
     * for [OfflineNotesRepositoryImpl.searchNotesStream]
     */
    @Test
    fun offlineNotesRepository_searchNotesNoMatch_returnEmptyList() = runBlocking {
        // Mock the Dao to return the notes in UTC timezone
        whenever(currentZoneDao.searchNotes("NoMatch", SortOption.A_Z.name))
            .thenReturn(flowOf(emptyList()))

        // When: Search notes with no matches
        val noMatchNotes = repository.searchNotesStream("NoMatch", SortOption.A_Z).first()

        // Then: Verify that the note searching call with the correct query passed
        // to the Dao of the current zone
        verify(currentZoneDao).searchNotes("NoMatch", SortOption.A_Z.name)
        // Then: Check if the result is an empty list
        assertEquals(emptyList<Note>(), noMatchNotes)
    }

    /**
     * Update a note in the repository.
     * Happy path for [OfflineNotesRepositoryImpl.updateNote]
     */
    @Test
    fun offlineNotesRepository_updateNote_daoUpdatesNote() = runTest {
        // When: update note
        repository.updateNote(mockNoteWithTitleAndBody)

        // Then: Verify that the note updating call with the correct timestamp passed
        // to the Dao of the current zone
        verify(currentZoneDao).updateNote(mockNoteToDao)
    }

    /**
     * Delete a note from the repository.
     * Happy path for [OfflineNotesRepositoryImpl.deleteNote]
     */
    @Test
    fun offlineNotesRepository_deleteNote_daoDeletesNote() = runTest {
        // When: delete note
        repository.deleteNote(mockNote1)

        // Then: Verify that the note deleting call passed
        // to the Dao of the current zone
        verify(currentZoneDao).deleteNote(mockNote1)
    }

    /**
     * Save a sort option for Creative zone in the repository.
     * Happy path for [OfflineNotesRepositoryImpl.saveSortOption]
     */
    @Test
    fun offlineNotesRepository_saveSortOptionForCreativeZone_preferencesSavesSortOption() =
        runTest {
            // When: save sort option
            repository.saveSortOption(SortOption.A_Z, Zone.CREATIVE)

            // Then: Verify that the sort option saving call with the correct sort option
            // and zone passed to the preferences DataStore
            verify(pouchPreferences).saveSortOption(SortOption.A_Z, Zone.CREATIVE)
        }

    /**
     * Save a sort option for Bom zone in the repository.
     * Happy path for [OfflineNotesRepositoryImpl.saveSortOption]
     */
    @Test
    fun offlineNotesRepository_saveSortOptionForBomZone_preferencesSavesSortOption() = runTest {
        // When: save sort option
        repository.saveSortOption(SortOption.A_Z, Zone.BOX_OF_MYSTERIES)

        // Then: Verify that the sort option saving call with the correct sort option
        // and zone passed to the preferences DataStore
        verify(pouchPreferences).saveSortOption(SortOption.A_Z, Zone.BOX_OF_MYSTERIES)
    }

    /**
     * Get sort option preference for Creative zone from the repository.
     * Happy path for [OfflineNotesRepositoryImpl.getSortOptionFlow]
     */
    @Test
    fun offlineNotesRepository_getSortOptionFlowForCreativeZone_returnsCorrectSortOption() =
        runBlocking {
            // Mock the preferences DataStore to return a flow with a specific sort option
            whenever(pouchPreferences.getSortOptionFlow(Zone.CREATIVE))
                .thenReturn(flowOf(SortOption.A_Z))

            // When: Retrieve the sort option flow for the Creative zone
            val retrievedSortOption = repository.getSortOptionFlow(Zone.CREATIVE).first()

            // Then: Verify that the preferences DataStore is accessed with the correct zone
            verify(pouchPreferences).getSortOptionFlow(Zone.CREATIVE)
            // Then: Assert that the retrieved sort option matches the expected value
            assertEquals(SortOption.A_Z, retrievedSortOption)
        }

    /**
     * Get sort option preference for Bom zone from the repository.
     * Happy path for [OfflineNotesRepositoryImpl.getSortOptionFlow]
     */
    @Test
    fun offlineNotesRepository_getSortOptionFlowForBomZone_returnsCorrectSortOption() = runTest {
        // Mock the preferences DataStore to return a flow with a specific sort option
        whenever(pouchPreferences.getSortOptionFlow(Zone.BOX_OF_MYSTERIES))
            .thenReturn(flowOf(SortOption.A_Z))

        // When: Retrieve the sort option flow for the Bom zone
        val retrievedSortOption = repository.getSortOptionFlow(Zone.BOX_OF_MYSTERIES).first()

        // Then: Verify that the preferences DataStore is accessed with the correct zone
        verify(pouchPreferences).getSortOptionFlow(Zone.BOX_OF_MYSTERIES)
        // Then: Assert that the retrieved sort option matches the expected value
        assertEquals(SortOption.A_Z, retrievedSortOption)
    }

    /**
     * Toggle the current zone in the repository
     * and verify that the currentZoneDao in the repository is updated.
     * Happy path for [OfflineNotesRepositoryImpl.toggleZone]
     */
    @Test
    fun offlineNotesRepository_toggleZone_changeCurrentZone() = runTest {
        // When: Toggle the zone
        repository.toggleZone()

        // Then: Verify that the currentZoneDao in the repository is updated.
        // If so, creating a note, for example, should be done via the new Dao.
        repository.createNote(mockNote1)
        verify(bomNoteDao).insert(any())

        // When: Toggle the zone again
        repository.toggleZone()
        // Then: Verify that the currentZoneDao in the repository is updated.
        repository.createNote(mockNote1)
        verify(creativeNoteDao).insert(any())
    }
}