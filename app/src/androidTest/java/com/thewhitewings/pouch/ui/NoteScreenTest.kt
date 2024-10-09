package com.thewhitewings.pouch.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.data.Note
import com.thewhitewings.pouch.data.mockNote1
import com.thewhitewings.pouch.data.mockTimestamp1
import com.thewhitewings.pouch.rules.onNodeWithContentDescriptionForStringId
import com.thewhitewings.pouch.rules.onNodeWithStringId
import com.thewhitewings.pouch.rules.onNodeWithTagForStringId
import com.thewhitewings.pouch.utils.DateTimeFormatType
import com.thewhitewings.pouch.utils.DateTimeUtils
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    /**
     * Test that the NoteScreen is displayed correctly.
     * Happy path for [NoteScreen]
     */
    @Test
    fun noteScreen_isDisplayed() {
        composeTestRule.setContent {
            NoteScreen(
                noteUiState = NoteViewModel.NoteUiState(),
                navigateBack = { },
                onNavigateUp = { },
                onNoteDelete = { },
                onNoteTitleChange = {},
                onNoteBodyChange = {}
            )
        }

        composeTestRule.onNodeWithTagForStringId(R.string.note_screen_tag).assertIsDisplayed()
    }

    /**
     * Test that the elements in the NoteScreen are displayed correctly.
     * Happy path for [NoteScreen]
     */
    @Test
    fun noteScreen_containsBackAndDeleteButtonsAndTextFields() {
        composeTestRule.setContent {
            NoteScreen(
                noteUiState = NoteViewModel.NoteUiState(),
                navigateBack = {},
                onNavigateUp = {},
                onNoteDelete = {},
                onNoteTitleChange = {},
                onNoteBodyChange = {}
            )
        }

        // Verify that the Back button is displayed
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.back_description)
            .assertIsDisplayed()

        // Verify that the Delete button is displayed
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.delete_note_description)
            .assertIsDisplayed()

        // Verify that the title TextField is displayed
        composeTestRule.onNodeWithStringId(R.string.note_title_hint)
            .assertIsDisplayed()

        // Verify that the body TextField is displayed
        composeTestRule.onNodeWithStringId(R.string.note_body_hint)
            .assertIsDisplayed()
    }

    /**
     * Test that when typing in the title and body TextFields, their corresponding callbacks are triggered.
     * Happy path for note title and body text fields in [NoteScreenBody]
     */
    @Test
    fun noteScreen_typingInTextFields_triggersCallbacks() {
        var updatedTitle = ""
        var updatedBody = ""

        composeTestRule.setContent {
            NoteScreen(
                noteUiState = NoteViewModel.NoteUiState(),
                navigateBack = {},
                onNavigateUp = {},
                onNoteDelete = {},
                onNoteTitleChange = { updatedTitle = it },
                onNoteBodyChange = { updatedBody = it }
            )
        }

        // Type into the title TextField
        val expectedNewTitle = "New Note Title"
        composeTestRule.onNodeWithStringId(R.string.note_title_hint)
            .performTextInput(expectedNewTitle)

        // Verify that the title callback was triggered with the correct value
        assertEquals(expectedNewTitle, updatedTitle)

        // Type into the body TextField
        val expectedNewBody = "New Note Body"
        composeTestRule.onNodeWithStringId(R.string.note_body_hint)
            .performTextInput(expectedNewBody)

        // Verify that the body callback was triggered with the correct value
        assertEquals(expectedNewBody, updatedBody)
    }

    /**
     * Test that when clicking on the up or delete button,
     * the corresponding callbacks are triggered.
     * Happy path for up and delete buttons in [NoteScreenBody]
     */
    @Test
    fun noteScreen_upAndDeleteButtons_triggerCallbacksCorrectly() {
        var upTriggered = false
        var deleteTriggered = false

        composeTestRule.setContent {
            NoteScreen(
                noteUiState = NoteViewModel.NoteUiState(),
                navigateBack = { },
                onNavigateUp = { upTriggered = true },
                onNoteDelete = { deleteTriggered = true },
                onNoteTitleChange = {},
                onNoteBodyChange = {}
            )
        }

        // Perform click on the up button
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.back_description)
            .performClick()

        // Verify that the up callback was triggered
        assert(upTriggered)

        // Perform click on the delete button
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.delete_note_description)
            .performClick()

        // Verify that the delete callback was triggered
        assert(deleteTriggered)
    }

    /**
     * Test that when pressing the system back button,
     * the navigateBack callback is triggered.
     * Happy path for system back button in [NoteScreen]
     */
    @Test
    fun noteScreen_backHandler_triggersNavigateBack() {
        var backTriggered = false

        composeTestRule.setContent {
            NoteScreen(
                noteUiState = NoteViewModel.NoteUiState(),
                navigateBack = { backTriggered = true },
                onNavigateUp = {},
                onNoteDelete = {},
                onNoteTitleChange = {},
                onNoteBodyChange = {}
            )
        }

        // Simulate system back press
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }

        // Verify that the navigateBack callback was triggered
        assert(backTriggered)
    }

    /**
     * Test that when creating a new note,
     * the placeholder texts are displayed for note title and body text fields,
     * and timestamp text is not displayed.
     * Case: New note
     * for [NoteScreen]
     */
    @Test
    fun noteScreen_whenCreatingNote_displaysEmptyFields() {
        // Given the default note title, body, and timestamp of NoteUiState in the NoteScreen are empty strings
        composeTestRule.setContent {
            NoteScreen(
                noteUiState = NoteViewModel.NoteUiState(),
                navigateBack = {},
                onNavigateUp = {},
                onNoteDelete = {},
                onNoteTitleChange = {},
                onNoteBodyChange = {}
            )
        }

        // Assert that the placeholder text of title text field is displayed
        composeTestRule.onNodeWithStringId(R.string.note_title_hint)
            .assertIsDisplayed()

        // Assert that the placeholder text of body text field is displayed
        composeTestRule.onNodeWithStringId(R.string.note_body_hint)
            .assertIsDisplayed()

        // Assert that the timestamp text does not exist
        composeTestRule.onNodeWithTagForStringId(R.string.timestamp_in_note_screen_tag)
            .assertDoesNotExist()
    }

    /**
     * Test that when updating an existing note,
     * note title and body text fields are filled correctly.
     * Case: Updating note
     * for [NoteScreen]
     */
    @Test
    fun noteScreen_whenUpdatingNote_displaysFilledFields() {
        // Given: NoteScreen with a non-empty note
        composeTestRule.setContent {
            NoteScreen(
                noteUiState = NoteViewModel.NoteUiState(
                    note = mockNote1
                ),
                navigateBack = {},
                onNavigateUp = {},
                onNoteDelete = {},
                onNoteTitleChange = {},
                onNoteBodyChange = {}
            )
        }

        // Assert that placeholder text of title text field does not exist
        composeTestRule.onNodeWithStringId(R.string.note_title_hint)
            .assertDoesNotExist()
        // Assert that the title text filed displays the note title
        composeTestRule.onNodeWithTagForStringId(R.string.note_title_text_field_tag)
            .assertTextEquals(mockNote1.noteTitle)

        // Assert that placeholder text of body text field does not exist
        composeTestRule.onNodeWithStringId(R.string.note_body_hint)
            .assertDoesNotExist()
        // Assert that the body text field displays the note body
        composeTestRule.onNodeWithTagForStringId(R.string.note_body_text_field_tag)
            .assertTextEquals(mockNote1.noteBody)

        // Asserting that the timestamp text exists and is displayed correctly
        // is done in a separate test function
    }

    /**
     * Test that when updating an existing note,
     * the timestamp text is displayed correctly.
     * Case: Updating note
     * for [NoteScreen]
     */
    @Test
    fun noteScreenBody_whenUpdatingNote_displaysTimestampCorrectly() {
        // Given: NoteScreen with a non-empty timestamp
        val testTimestamp = mockTimestamp1
        composeTestRule.setContent {
            NoteScreen(
                noteUiState = NoteViewModel.NoteUiState(
                    note = Note(timestamp = testTimestamp)
                ),
                navigateBack = {},
                onNavigateUp = {},
                onNoteDelete = {},
                onNoteTitleChange = {},
                onNoteBodyChange = {}
            )
        }

        // Expected formatted timestamp
        val formattedTimestamp = DateTimeUtils.getFormattedDateTime(
            DateTimeFormatType.LOCAL_TO_LOCAL_MEDIUM_LENGTH_FORMAT, testTimestamp
        )

        // Assert that the timestamp text is displayed correctly
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(
                    R.string.timestamp_edited,
                    formattedTimestamp
                )
            )
            .assertIsDisplayed()
    }
}