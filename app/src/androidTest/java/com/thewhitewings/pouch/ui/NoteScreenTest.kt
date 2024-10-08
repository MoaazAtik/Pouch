package com.thewhitewings.pouch.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.rules.onNodeWithTagForStringId
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

}