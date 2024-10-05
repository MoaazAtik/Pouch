package com.thewhitewings.pouch.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class OfflineNotesRepositoryTest {

    // The repository being tested
    private lateinit var repository: OfflineNotesRepository

    // Mocked dependencies
    private lateinit var creativeNoteDao: NoteDao
    private lateinit var bomNoteDao: NoteDao
    private lateinit var pouchPreferences: PouchPreferences

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
        repository = OfflineNotesRepository(
            creativeNoteDao = creativeNoteDao,
            bomNoteDao = bomNoteDao,
            pouchPreferences = pouchPreferences
        )
    }

    @Test
    fun offlineNotesRepository_createNote_insertsNoteToCurrentZone() = runTest {
        // When: createNote
        repository.createNote(mockNoteWithTitleAndBody)

        // Then: Verify that the note was inserted via the currentZoneDao
        verify(currentZoneDao).insert(any())
    }
}