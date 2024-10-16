package com.thewhitewings.pouch.presentation

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.feature_note.domain.model.Note
import com.thewhitewings.pouch.feature_note.domain.util.SortOption
import com.thewhitewings.pouch.feature_note.presentation.notes.NotesScreen
import com.thewhitewings.pouch.feature_note.presentation.notes.NotesUiState
import com.thewhitewings.pouch.feature_note.presentation.notes.ShowAnimations
import com.thewhitewings.pouch.rules.onNodeWithContentDescriptionForStringId
import com.thewhitewings.pouch.rules.onNodeWithStringId
import com.thewhitewings.pouch.rules.onNodeWithTagForStringId
import com.thewhitewings.pouch.feature_note.util.DateTimeFormatType
import com.thewhitewings.pouch.feature_note.util.DateTimeUtils
import com.thewhitewings.pouch.feature_note.util.Zone
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotesScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    /**
     * Test that the [NotesScreen] is displayed correctly.
     * Happy path for [NotesScreen]
     */
    @Test
    fun notesScreen_isDisplayed() {
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        composeTestRule.onNodeWithTagForStringId(R.string.notes_screen_tag)
            .assertIsDisplayed()
    }

    /**
     * Test that the FAB is displayed on the [NotesScreen]
     * Happy path for [NotesScreen]
     */
    @Test
    fun notesScreen_displaysFAB() {
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        // Assert that the FAB is displayed with the correct content description
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.add_new_note)
            .assertIsDisplayed()
    }

    /**
     * Test that when pressing the system back button,
     * the navigateBack callback is triggered.
     * Happy path for system back button in [NotesScreen]
     */
    @Test
    fun notesScreen_backHandler_triggersNavigateBack() {
        var backTriggered = false

        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(),
                navigateBack = { backTriggered = true },
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
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
     * Test that animations are displayed when initializing a zone, i.e., [NotesUiState.showAnimations] is true
     * Happy path for [NotesScreen] and [ShowAnimations]
     */
    @Test
    fun notesScreen_displaysAnimations_whenInitializingZone() {
        // Given: showAnimations' default is true
        // Set the content with showAnimations = true
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        composeTestRule.onNodeWithTagForStringId(R.string.zone_initialization_animations_tag)
            .assertIsDisplayed()
    }

    /**
     * Test that animations are not displayed when a zone is already initialized, i.e., [NotesUiState.showAnimations] is false
     * Case: zone is already initialized
     * for [NotesScreen]
     */
    @Test
    fun notesScreen_noAnimations_whenZoneAlreadyInitialized() {
        // Given: showAnimations' default true
        // Set the content with showAnimations = true
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(showAnimations = false),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        composeTestRule.onNodeWithTagForStringId(R.string.zone_initialization_animations_tag)
            .assertIsNotDisplayed()
    }

    /**
     * Test that the animation for the CREATIVE zone is displayed
     * Happy path for [ShowAnimations] and [RevealScreenAnimation]
     */
    @Test
    fun showAnimations_displaysCreativeZoneAnimation() {
        // Set content with CREATIVE zone
        composeTestRule.setContent {
            ShowAnimations(
                zone = Zone.CREATIVE,
                snackbarHostState = SnackbarHostState(),
                context = LocalContext.current
            )
        }

        // Assert that the animation for CREATIVE zone is displayed
        composeTestRule.onNodeWithTag(R.raw.reveal_screen_red.toString())
            .assertExists()
    }

    /**
     * Test that the animation for the BOX_OF_MYSTERIES zone is displayed
     * Happy path for [ShowAnimations], [RevealScreenAnimation], and [RevealLoaderAnimation]
     */
    @Test
    fun showAnimations_displaysBoxOfMysteriesAnimations() {
        // Set content with BOX_OF_MYSTERIES zone
        composeTestRule.setContent {
            ShowAnimations(
                zone = Zone.BOX_OF_MYSTERIES,
                snackbarHostState = SnackbarHostState(),
                context = LocalContext.current
            )
        }

        // Assert that the animation for BOX_OF_MYSTERIES zone is displayed
        composeTestRule.onNodeWithTag(R.raw.reveal_screen_black.toString())
            .assertExists()

        // Assert that the loader animation for BOX_OF_MYSTERIES zone is displayed
        composeTestRule.onNodeWithTagForStringId(R.string.reveal_loader_animation_tag)
            .assertExists()
    }

    /**
     * Test that the snackbar is displayed with the correct message for the BOX_OF_MYSTERIES zone
     * Happy path for [ShowAnimations]
     */
    @Test
    fun showAnimations_displaysSnackbar_forBoxOfMysteries() {
        val snackbarHostState = SnackbarHostState()

        // Set content with BOX_OF_MYSTERIES zone
        composeTestRule.setContent {
            ShowAnimations(
                zone = Zone.BOX_OF_MYSTERIES,
                snackbarHostState = snackbarHostState,
                context = LocalContext.current
            )
        }

        // Assert that the snackbar shows the correct message
        composeTestRule.waitUntil {
            snackbarHostState.currentSnackbarData?.visuals?.message ==
                    composeTestRule.activity.getString(R.string.bom_revealing_message)
        }
    }

    /**
     * Test that the zone text is displayed when the notes list is empty and the zone is CREATIVE
     * Happy path for [NotesScreen] and [ZoneText]
     */
    @Test
    fun notesScreen_emptyNotesListAndCreativeZone_displayZoneTextCorrectly() {
        // Given: initial notes list is empty, and zone is CREATIVE
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        composeTestRule.onNodeWithStringId(R.string.creative_zone_name)
            .assertIsDisplayed()
    }

    /**
     * Test that the zone text is displayed when the notes list is empty and the zone is BOX_OF_MYSTERIES
     * Happy path for [NotesScreen] and [ZoneText]
     */
    @Test
    fun notesScreen_emptyNotesListAndBomZone_displayZoneTextCorrectly() {
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(zone = Zone.BOX_OF_MYSTERIES),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        composeTestRule.onNodeWithStringId(R.string.box_of_mysteries_zone_name_multi_line)
            .assertIsDisplayed()
    }

    /**
     * Test that the zone text is not displayed when the notes list is not empty and the zone is Creative
     * Case: notes list is not empty and zone is CREATIVE
     * for [NotesScreen] and [ZoneText]
     */
    @Test
    fun notesScreen_nonEmptyNotesListAndCreativeZone_noZoneText() {
        val mockNotesList = listOf(Note())

        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(
                    notesList = mockNotesList
                ),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        composeTestRule.onNodeWithStringId(R.string.creative_zone_name)
            .assertIsNotDisplayed()
    }

    /**
     * Test that the logo image is displayed
     * Happy path for [NotesScreen] and [LogoImage]
     */
    @Test
    fun notesScreen_displayLogoImage() {
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.the_white_wings_logo)
            .assertIsDisplayed()
    }

    /**
     * Test that the search notes text field is displayed
     * Happy path for [SearchNotesTextField]
     */
    @Test
    fun searchNotesTextField_isDisplayed() {
        // The initial state of the search notes text field is empty
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(
                    searchQuery = ""
                ),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        // Check that the TextField is displayed
        composeTestRule.onNodeWithTagForStringId(R.string.search_notes_text_field_tag)
            .assertIsDisplayed()
    }

    /**
     * Test that when the search notes text field is empty,
     * the placeholder is displayed and the clear button is not displayed
     * Happy path for [SearchNotesTextField]
     */
    @Test
    fun searchNotesTextField_whenEmpty_placeholderDisplayedAndNoClearButton() {
        // The initial state of the search notes text field is empty
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(
                    searchQuery = ""
                ),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        // Check that the placeholder is visible
        composeTestRule.onNodeWithStringId(R.string.search_notes_hint)
            .assertIsDisplayed()

        // Check that the clear button is not visible
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.clear_search_notes_hint)
            .assertIsNotDisplayed()
    }

    /**
     * Test that the search notes text field is updated with the correct value
     * Happy path for [SearchNotesTextField]
     */
    @Test
    fun searchNotesTextField_whenTyping_updatesText() {
        var searchText = ""
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = { searchText = it },
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        // Type some text into the text field
        val typedText = "Note Search Query"
        composeTestRule.onNodeWithTagForStringId(R.string.search_notes_text_field_tag)
            .performTextInput(typedText)

        // Assert that the onValueChange callback is triggered with the correct text
        assertEquals(typedText, searchText)
    }

    /**
     * Test that when the search notes text field is not empty, the clear button is displayed and when clicked, the text field is cleared and the clear button is not displayed anymore
     * Happy path for [SearchNotesTextField]
     */
    @Test
    fun searchNotesTextField_whenNotEmpty_showsClearButtonAndClearsText() {
        var searchText by mutableStateOf("Test Text")
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(searchQuery = searchText),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = { searchText = it },
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        // Check that the clear button is visible
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.clear_search_notes_hint)
            .assertIsDisplayed()

        // Click the clear button
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.clear_search_notes_hint)
            .performClick()

        // Assert that the text is cleared
        assertEquals("", searchText)

        // Check that the clear button is not visible after clearing
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.clear_search_notes_hint)
            .assertDoesNotExist()
    }

    /**
     * Test that when the search notes text field is cleared, the placeholder is displayed again
     * Happy path for [SearchNotesTextField]
     */
    @Test
    fun searchNotesTextField_afterClearingText_showsPlaceholder() {
        var searchText by mutableStateOf("Test Text")
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(searchQuery = searchText),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = { searchText = it },
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        composeTestRule.onNodeWithTagForStringId(R.string.search_notes_text_field_tag)
            .performTextClearance()

        composeTestRule.onNodeWithStringId(R.string.search_notes_hint)
            .assertIsDisplayed()
    }

    /**
     * Test that the sort notes button is displayed and the sort options menu is not displayed initially
     * Happy path for [NotesScreen] and [SortNotesButtonAndMenu]
     */
    @Test
    fun sortNotesButtonAndMenu_initially_buttonDisplayed_AndOptionsMenuNotDisplayed() {
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        // Assert that the sort icon button is displayed
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.sort_notes)
            .assertIsDisplayed()

        // Assert that the sort options menu is not displayed initially
        composeTestRule.onNodeWithTagForStringId(R.string.sort_options_menu_tag)
            .assertDoesNotExist()
    }

    /**
     * Test that the sort notes button opens the sort options menu and the options are displayed
     * Happy path for [NotesScreen] and [SortNotesButtonAndMenu]
     */
    @Test
    fun sortNotesButtonAndMenu_onClick_expandMenu() {
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        // Click the sort button to expand the dropdown menu
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.sort_notes)
            .performClick()

        // Assert that the dropdown menu is now expanded and options are visible
        composeTestRule.onNodeWithTagForStringId(R.string.sort_options_menu_tag)
            .assertIsDisplayed()
        SortOption.entries.forEach { sortOption ->
            composeTestRule.onNodeWithStringId(sortOption.label)
                .assertExists()
        }
    }

    /**
     * Test that when a sort option is selected, the sort options menu is collapsed and the correct sort option is selected.
     * Happy path for [NotesScreen] and [SortNotesButtonAndMenu]
     */
    @Test
    fun sortNotesButtonAndMenu_onOptionClick_collapseMenu_andSelectOption() {
        var selectedSortOptionId: Int? = null
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = { sortOptionId -> selectedSortOptionId = sortOptionId },
                onToggleZone = {}
            )
        }

        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.sort_notes)
            .performClick()

        composeTestRule.onNodeWithStringId(SortOption.entries.first().label)
            .performClick()

        composeTestRule.onNodeWithTagForStringId(R.string.sort_options_menu_tag)
            .assertDoesNotExist()

        assertEquals(SortOption.entries.first().id, selectedSortOptionId)
    }

    /**
     * Test that when clicking outside the sort notes button, the sort options menu is dismissed.
     * Edge case for [NotesScreen] and [SortNotesButtonAndMenu]
     */
    @Test
    fun sortNotesButtonAndMenu_onOutsideClick_menuDismisses() {
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.sort_notes)
            .performClick()

        composeTestRule.onNodeWithTagForStringId(R.string.sort_options_menu_tag)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTagForStringId(R.string.sort_options_menu_tag).onParent()
            .performClick()

        composeTestRule.onNodeWithTagForStringId(R.string.sort_options_menu_tag)
            .assertIsNotDisplayed()
    }

    /**
     * Test that the Bom Revealing Button is displayed in CREATIVE zone and toggles the zone to BOX_OF_MYSTERIES when clicked.
     * Also, the it is shrunk to 0.dp width in BOX_OF_MYSTERIES zone.
     * Happy path for [BomRevealingButton]
     * Cases: CREATIVE zone and BOX_OF_MYSTERIES zone
     */
    @Test
    fun bomRevealingButton_basedOnZone_isDisplayedOrShrunk_AndTogglesZone() {
        // Given: initial zone is CREATIVE, so the button should be visible
        composeTestRule.setContent {
            // MutableState to manage the current zone
            val currentZone = remember { mutableStateOf(Zone.CREATIVE) }

            // NotesScreen composable with the state-driven zone
            NotesScreen(
                uiState = NotesUiState(zone = currentZone.value),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {
                    // Toggle to BOX_OF_MYSTERIES
                    currentZone.value = Zone.BOX_OF_MYSTERIES
                }
            )
        }

        // Assert that the button with the bom button tag is displayed
        composeTestRule.onNodeWithTagForStringId(R.string.bom_button_tag)
            .assertIsDisplayed()

        // Update the zone state to BOX_OF_MYSTERIES to trigger recomposition
        composeTestRule.onNodeWithTagForStringId(R.string.bom_button_tag)
            .performClick()

        // Assert that the button is shrunk to 0.dp width after the zone change
        composeTestRule.onNodeWithTagForStringId(R.string.bom_button_tag)
            .assertWidthIsEqualTo(0.dp)
    }

    /**
     * Test that the notes list is displayed when the notes list is not empty
     * Happy path for [NotesList]
     */
    @Test
    fun notesList_whenNotEmpty_displaysCorrectNumberOfNotes() {
        val mockNotesList = listOf(
            Note(1, "Title 1", "Body 1"),
            Note(2, "Title 2", "Body 2"),
            Note(3, "Title 3", "Body 3")
        )

        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(notesList = mockNotesList),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        // Assert: The notes list is displayed
        composeTestRule.onNodeWithTagForStringId(R.string.notes_list_tag)
            .assertIsDisplayed()
        // Assert: The correct number of items are displayed
        composeTestRule.onNodeWithTagForStringId(R.string.notes_list_tag)
            .onChildren()
            .assertCountEquals(mockNotesList.size)

        // Assert that specific notes are displayed by matching their title or body
        composeTestRule.onNodeWithText(mockNotesList[0].noteTitle)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(mockNotesList[1].noteBody)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(mockNotesList[2].noteBody)
            .assertIsDisplayed()
    }

    /**
     * Test that when a note is clicked, the correct note is passed to the onNoteClick callback
     * Happy path for [NotesList]
     */
    @Test
    fun notesList_whenNoteClicked_callsOnNoteClickWithCorrectNote() {
        val mockNotesList = listOf(
            Note(1, "Title 1", "Body 1"),
            Note(2, "Title 2", "Body 2"),
            Note(3, "Title 3", "Body 3")
        )

        // Create a mock for the onNoteClick callback
        var clickedNote: Note? = null
        val onNoteClick: (Int) -> Unit = { passedNoteId ->
            mockNotesList.forEach { note ->
                if (note.id == passedNoteId) {
                    clickedNote = note
                    return@forEach
                }
            }
        }

        // Set the content
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(notesList = mockNotesList),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = { passedNoteId: Int ->
                    onNoteClick(passedNoteId)
                },
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        // Action: Click on the second note
        composeTestRule.onNodeWithText(mockNotesList[1].noteTitle)
            .performClick()

        // Assert: The correct note is passed to the onNoteClick callback
        assertEquals(mockNotesList[1], clickedNote)
    }

    /**
     * Test that the note item is displayed correctly
     * Happy path for [NotesListItem]
     */
    @Test
    fun notesListItem_displaysNoteContentCorrectly() {
        val mockNote = Note(
            id = 1,
            noteTitle = "Sample Note Title",
            noteBody = "This is the body of the sample note."
        )

        // Set the content
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(notesList = listOf(mockNote)),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        // Assert: Note title is displayed
        composeTestRule.onNodeWithText(mockNote.noteTitle)
            .assertIsDisplayed()

        // Assert: Note body is displayed
        composeTestRule.onNodeWithText(mockNote.noteBody)
            .assertIsDisplayed()

        // Assert: The formatted timestamp is displayed
        composeTestRule.onNodeWithText(
            DateTimeUtils.getFormattedDateTime(
                DateTimeFormatType.LOCAL_TO_LOCAL_SHORT_LENGTH_FORMAT,
                mockNote.timestamp
            )
        ).assertIsDisplayed()
    }

    /**
     * Test that when a note has an empty title and body, it is handled gracefully
     * Case: empty title and body
     * for [NotesListItem]
     */
    @Test
    fun notesListItem_handlesEmptyNoteContentGracefully() {
        // Given: A note with an empty title and body
        val noteWithEmptyContent = Note()

        // Set the content
        composeTestRule.setContent {
            NotesScreen(
                uiState = NotesUiState(notesList = listOf(noteWithEmptyContent)),
                navigateBack = {},
                navigateToCreateNote = {},
                navigateToEditNote = {},
                onSearchNotes = {},
                onSortNotes = {},
                onToggleZone = {}
            )
        }

        // Assert: Note item is displayed even with empty content
        composeTestRule.onNodeWithTagForStringId(R.string.notes_list_item_tag)
            .assertExists()

        // Assert: The empty title is handled (nothing should crash)
        composeTestRule.onAllNodesWithText("")
            // There should be two empty text elements, title and body
            .assertCountEquals(2)

        // Assert: The formatted timestamp is still displayed correctly
        composeTestRule.onNodeWithText(
            DateTimeUtils.getFormattedDateTime(
                DateTimeFormatType.LOCAL_TO_LOCAL_SHORT_LENGTH_FORMAT,
                noteWithEmptyContent.timestamp
            )
        ).assertIsDisplayed()
    }

}
