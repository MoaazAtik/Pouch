package com.thewhitewings.pouch.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thewhitewings.pouch.feature_note.data.data_source.NoteDao
import com.thewhitewings.pouch.feature_note.data.data_source.NoteDatabase
import com.thewhitewings.pouch.feature_note.domain.model.Note
import com.thewhitewings.pouch.feature_note.domain.util.SortOption
import com.thewhitewings.pouch.mocks.azSortedNotes
import com.thewhitewings.pouch.mocks.mockNote1
import com.thewhitewings.pouch.mocks.mockNote2
import com.thewhitewings.pouch.mocks.mockNote3
import com.thewhitewings.pouch.mocks.newestFirstNotes
import com.thewhitewings.pouch.mocks.oldestFirstNotes
import com.thewhitewings.pouch.mocks.zaSortedNotes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteDaoTest {

    private lateinit var database: NoteDatabase
    private lateinit var noteDao: NoteDao

    @Before
    fun setUp() {
        // Use an in-memory database to avoid impacting actual data.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NoteDatabase::class.java
        ).allowMainThreadQueries() // Allowing main thread queries for simplicity
            .build()

        noteDao = database.noteDao()
    }

    @After
    fun closeDb() {
        // Close the database after each test
        database.close()
    }

    /**
     * Test the insert and getNoteStream functionality of the NoteDao.
     * Happy path for [NoteDao.insert] and [NoteDao.getNoteStream]
     */
    @Test
    fun noteDao_insertAndGetNoteStream_insertNoteAndRetrieveFromDatabase() = runBlocking {
        // When: Insert the note into the database
        noteDao.insert(mockNote1)

        // Then: Collect from the Flow and check if the note was inserted correctly
        val retrievedNote = noteDao.getNoteStream(mockNote1.id).first()

        // Check the retrieved note matches the inserted note
        assertNotNull(retrievedNote)
        assertEquals(mockNote1, retrievedNote)
    }

    /**
     * On insertion conflict, i.e., inserting a note with the same id, replace the existing note.
     * Case: Inserting Conflict (insert a note with the same id as an existing note).
     * for [NoteDao.insert] and [NoteDao.getNoteStream]
     */
    @Test
    fun noteDao_insertNoteDuplicate_replaceOnConflict() = runBlocking {
        // When: Insert a note into the database
        noteDao.insert(mockNote1)

        // When: Insert a new note with the same id but different content
        val updatedNote = mockNote1.copy(
            noteTitle = "Updated Note",
            noteBody = "Updated content."
        )
        noteDao.insert(updatedNote)

        // Then: Query the database and check that the note was replaced
        val retrievedNote = noteDao.getNoteStream(mockNote1.id).first()

        // Check the retrieved note matches the updated note
        assertEquals(updatedNote.noteTitle, retrievedNote?.noteTitle)
        assertEquals(updatedNote.noteBody, retrievedNote?.noteBody)
    }

    /**
     * When querying all notes in the database sorted by [SortOption.A_Z], get all notes sorted correctly.
     * Happy path for [NoteDao.getAllNotes]
     */
    @Test
    fun noteDao_getAllNotesSortedAz_returnAllNotesSorted() = runBlocking {
        // Given: Insert multiple notes with different titles, bodies, and timestamps
        insertThreeNotes()

        // When: Retrieve notes sorted by 'A_Z'
        val retrievedNotes = noteDao.getAllNotes(SortOption.A_Z.name).first()

        // Then: Check if the notes are sorted alphabetically (A-Z)
        assertEquals(azSortedNotes, retrievedNotes)
    }

    /**
     * When querying all notes in the database sorted by [SortOption.Z_A], get all notes sorted correctly.
     * Happy path for [NoteDao.getAllNotes]
     */
    @Test
    fun noteDao_getAllNotesSortedZa_returnAllNotesSorted() = runBlocking {
        // Given: Insert multiple notes with different titles, bodies, and timestamps
        insertThreeNotes()

        // When: Retrieve notes sorted by 'Z_A'
        val retrievedNotes = noteDao.getAllNotes(SortOption.Z_A.name).first()

        // Then: Check if the notes are sorted alphabetically (Z-A)
        assertEquals(zaSortedNotes, retrievedNotes)
    }

    /**
     * When querying all notes in the database sorted by [SortOption.OLDEST_FIRST], get all notes sorted correctly.
     * Happy path for [NoteDao.getAllNotes]
     */
    @Test
    fun noteDao_getAllNotesSortedOldestFirst_returnAllNotesSorted() = runBlocking {
        // Given: Insert multiple notes with different titles, bodies, and timestamps
        insertThreeNotes()

        // When: Retrieve notes sorted by 'OLDEST_FIRST'
        val retrievedNotes = noteDao.getAllNotes(SortOption.OLDEST_FIRST.name).first()

        // Then: Check if the notes are sorted by oldest timestamp first
        assertEquals(oldestFirstNotes, retrievedNotes)
    }

    /**
     * When querying all notes in the database sorted by [SortOption.NEWEST_FIRST], get all notes sorted correctly.
     * Happy path for [NoteDao.getAllNotes]
     */
    @Test
    fun noteDao_getAllNotesSortedNewestFirst_returnAllNotesSorted() = runBlocking {
        // Given: Insert multiple notes with different titles, bodies, and timestamps
        insertThreeNotes()

        // When: Retrieve notes sorted by 'NEWEST_FIRST'
        val retrievedNotes = noteDao.getAllNotes(SortOption.NEWEST_FIRST.name).first()

        // Then: Check if the notes are sorted by newest timestamp first
        assertEquals(newestFirstNotes, retrievedNotes)
    }

    /**
     * When querying all notes in an empty database, get an empty list.
     * Case: Empty database.
     * for [NoteDao.getAllNotes]
     */
    @Test
    fun noteDao_getAllNotesEmptyDatabase_returnEmptyList() = runBlocking {
        // Given: Empty database

        // When: Retrieve notes sorted by 'NEWEST_FIRST'
        val retrievedNotes = noteDao.getAllNotes(SortOption.NEWEST_FIRST.name).first()

        // Then: Check if the result is an empty list
        assertEquals(emptyList<Note>(), retrievedNotes)
    }

    /**
     * When searching for notes, get filtered notes sorted correctly.
     * Happy path for [NoteDao.searchNotes]
     */
    @Test
    fun noteDao_searchNotes_returnFilteredAndSortedNotes() = runBlocking {
        // Given: Insert multiple notes with different titles, bodies, and timestamps
        insertThreeNotes()

        // When: Search notes containing "Ba"
        val retrievedNotes = noteDao.searchNotes("Ba", SortOption.A_Z.name).first()

        // Then: Check if the notes containing "Ba" are returned
        assertEquals(listOf(mockNote2), retrievedNotes)
    }

    /**
     * When searching for notes sorted by [SortOption.A_Z], get filtered notes sorted correctly.
     * Happy path for [NoteDao.searchNotes]
     */
    @Test
    fun noteDao_searchNotesAndSortAz_returnFilteredAndSortedNotes() = runBlocking {
        // Given: Insert multiple notes with different titles, bodies, and timestamps
        insertThreeNotes()

        // When: Search notes containing "Content" and sort by 'A_Z'
        val retrievedNotes = noteDao.searchNotes("Content", SortOption.A_Z.name).first()

        // Then: Check if the notes containing "Content" are sorted alphabetically (A-Z)
        assertEquals(azSortedNotes, retrievedNotes)
    }

    /**
     * When searching for notes sorted by [SortOption.Z_A], get filtered notes sorted correctly.
     * Happy path for [NoteDao.searchNotes]
     */
    @Test
    fun noteDao_searchNotesAndSortZa_returnFilteredAndSortedNotes() = runBlocking {
        // Given: Insert multiple notes with different titles, bodies, and timestamps
        insertThreeNotes()

        // When: Search notes containing "Content" and sort by 'Z_A'
        val retrievedNotes = noteDao.searchNotes("Content", SortOption.Z_A.name).first()

        // Then: Check if the notes containing "Content" are sorted alphabetically (Z-A)
        assertEquals(zaSortedNotes, retrievedNotes)
    }

    /**
     * When searching for notes sorted by [SortOption.OLDEST_FIRST], get filtered notes sorted correctly.
     * Happy path for [NoteDao.searchNotes]
     */
    @Test
    fun noteDao_searchNotesAndSortOldestFirst_returnFilteredAndSortedNotes() = runBlocking {
        // Given: Insert multiple notes with different titles, bodies, and timestamps
        insertThreeNotes()

        // When: Search notes containing "Content" and sort by 'OLDEST_FIRST'
        val retrievedNotes =
            noteDao.searchNotes("Content", SortOption.OLDEST_FIRST.name).first()

        // Then: Check if the notes are sorted by oldest timestamp first
        assertEquals(oldestFirstNotes, retrievedNotes)
    }

    /**
     * When searching for notes sorted by [SortOption.NEWEST_FIRST], get filtered notes sorted correctly.
     * Happy path for [NoteDao.searchNotes]
     */
    @Test
    fun noteDao_searchNotesAndSortNewestFirst_returnFilteredAndSortedNotes() = runBlocking {
        // Given: Insert multiple notes with different titles, bodies, and timestamps
        insertThreeNotes()

        // When: Search notes containing "Content" and sort by 'NEWEST_FIRST'
        val retrievedNotes =
            noteDao.searchNotes("Content", SortOption.NEWEST_FIRST.name).first()

        // Then: Check if the notes are sorted by newest timestamp first
        assertEquals(newestFirstNotes, retrievedNotes)
    }

    /**
     * When searching for notes with no matches, get an empty list.
     * Case: Search notes with no matches.
     * for [NoteDao.searchNotes]
     */
    @Test
    fun noteDao_searchNotesNoMatch_returnEmptyList() = runBlocking {
        // Given: Insert multiple notes with different titles, bodies, and timestamps
        insertThreeNotes()

        // When: Search notes with no matches
        val noMatchNotes = noteDao.searchNotes("NoMatch", SortOption.A_Z.name).first()

        // Then: Check if the result is an empty list
        assertEquals(emptyList<Note>(), noMatchNotes)
    }

    /**
     * Update a note in the database.
     * Happy path for [NoteDao.updateNote]
     */
    @Test
    fun noteDao_updateNote_noteIsUpdated() = runBlocking {
        // Given: Insert a note into the database
        noteDao.insert(mockNote1)

        // When: Update the note with a new title and body
        val updatedNote = mockNote1.copy(
            noteTitle = "Updated Title",
            noteBody = "Updated Body"
        )
        noteDao.updateNote(updatedNote)

        // Then: Check if the note is updated in the database
        val retrievedNote = noteDao.getNoteStream(mockNote1.id).first()

        // Assert that the note is not null and the title and content are updated
        assertEquals(updatedNote, retrievedNote)
    }

    /**
     * Update a note in the database without any changes.
     * Case: No changes in the note.
     * for [NoteDao.updateNote]
     */
    @Test
    fun noteDao_updateNoteNoChanges_updateNoteWithoutChanges() = runBlocking {
        // Given: Insert a note into the database
        noteDao.insert(mockNote1)

        // When: Update the note with the same title and body
        noteDao.updateNote(mockNote1)

        // Then: Retrieve the note and verify that nothing has changed
        val retrievedNote = noteDao.getNoteStream(mockNote1.id).first()

        // Assert that all of the note properties are unchanged
        assertEquals(mockNote1, retrievedNote)
    }

    /**
     * Update a note that doesn't exist in the database, i.e., a note with an invalid ID. No changes should be made to the database.
     * Case: non-existent note.
     * for [NoteDao.updateNote]
     */
    @Test
    fun noteDao_updateNonExistentNote_noChangesToDatabase() = runBlocking {
        // Given: Empty database

        // When: Trying to update a non-existent note
        noteDao.updateNote(mockNote1)

        // Then: No changes should be made to the database
        val retrievedNote = noteDao.getNoteStream(mockNote1.id).first()

        // Assert that the note is null
        // It should not be added to the database
        assertNull(retrievedNote)
    }

    /**
     * Delete a note from the database.
     * Happy path for [NoteDao.deleteNote]
     */
    @Test
    fun noteDao_deleteNote_removeNoteFromDatabase() = runBlocking {
        // Given: Insert a note into the database
        noteDao.insert(mockNote1)

        // Ensure the note exists in the database before deletion
        val insertedNote = noteDao.getNoteStream(mockNote1.id).first()
        assertNotNull(insertedNote)

        // When: Delete the note
        noteDao.deleteNote(mockNote1)

        // Then: Check if the note is removed from the database
        val deletedNote = noteDao.getNoteStream(mockNote1.id).first()
        assertNull(deletedNote)
    }

    /**
     * Delete a specific note from the database. The other notes should remain in the database.
     * Happy path for [NoteDao.deleteNote]
     */
    @Test
    fun noteDao_deleteNote_onlyRemoveSpecificNote() = runBlocking {
        // Given: Insert multiple notes into the database
        insertThreeNotes()

        // When: Delete one of the notes
        noteDao.deleteNote(mockNote2)

        // Then: Verify the deleted note is no longer in the database
        val remainingNotes = noteDao.getAllNotes(SortOption.A_Z.name).first()
        assertEquals(2, remainingNotes.size) // There should be two notes left
        assertFalse(remainingNotes.contains(mockNote2)) // The deleted note should not be present

        // Ensure the remaining notes are still in the database
        assertTrue(remainingNotes.contains(mockNote1))
        assertTrue(remainingNotes.contains(mockNote3))
    }

    /**
     * Delete a note that doesn't exist in the database, i.e., a note with an invalid ID. No changes should be made to the database.
     * Case: non-existent note.
     * for [NoteDao.deleteNote]
     */
    @Test
    fun noteDao_deleteNonExistentNote_noChangesToDatabase() = runBlocking {
        // Given: Empty database

        // Ensure the note doesn't exist in the database before deletion
        val allNotes = noteDao.getAllNotes(SortOption.A_Z.name).first()
        assertEquals(0, allNotes.size)

        // When: Attempt to delete a non-existent note
        noteDao.deleteNote(mockNote1)

        // Then: Verify no exceptions are thrown and the database remains unchanged
        val retrievedNotes = noteDao.getAllNotes(SortOption.A_Z.name).first()
        assertEquals(0, retrievedNotes.size)
    }


    /**
     * Insert three notes with all properties different from each other into the database for testing.
     */
    private suspend fun insertThreeNotes() {
        noteDao.insert(mockNote1)
        noteDao.insert(mockNote2)
        noteDao.insert(mockNote3)
    }
}