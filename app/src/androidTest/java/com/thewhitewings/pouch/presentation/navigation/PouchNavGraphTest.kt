package com.thewhitewings.pouch.presentation.navigation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.feature_note.presentation.add_edit_note.AddEditNoteDestination
import com.thewhitewings.pouch.feature_note.presentation.navigation.PouchNavHost
import com.thewhitewings.pouch.feature_note.presentation.notes.NotesDestination
import com.thewhitewings.pouch.rules.onNodeWithContentDescriptionForStringId
import com.thewhitewings.pouch.rules.onNodeWithStringId
import com.thewhitewings.pouch.rules.onNodeWithTagForStringId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PouchNavGraphTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: NavHostController

    @Before
    fun setUp() {
        composeTestRule.setContent {
            // Set up a NavController for testing
            navController = rememberNavController()
            // Initialize the PouchNavHost with the navController
            PouchNavHost(navController = navController)
        }
    }

    /**
     * Test that initially, the [NotesScreen] is displayed
     * Happy path for [PouchNavHost]
     */
    @Test
    fun pouchNavHost_onAppInitialization_notesScreenIsDisplayed() {
        // Assert that current route in the navController is NotesScreen's route
        navController.assertCurrentRouteName(NotesDestination.route)

        // Assert that the NotesScreen is displayed
        composeTestRule.onNodeWithTagForStringId(R.string.notes_screen_tag)
            .assertIsDisplayed()
    }

    /**
     * Test that when the create note FAB is clicked, the [AddEditNoteScreen] is displayed
     * Happy path for [PouchNavHost]
     */
    @Test
    fun pouchNavHost_whenCreateNoteButtonClicked_navigateToAddEditNoteScreen() {
        // Simulate a click on the FAB to trigger note creation
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.add_new_note)
            .performClick()

        // Assert that current route in the navController is correct
        navController.assertCurrentRouteName(AddEditNoteDestination.routeWithArgs)

        // Verify that the noteId argument is passed as 0 (indicating new note creation)
        val noteIdArg =
            navController.currentBackStackEntry?.arguments?.getInt(AddEditNoteDestination.noteIdArg)
        assertEquals(0, noteIdArg)

        // Verify that the AddEditNoteScreen is displayed
        composeTestRule.onNodeWithTagForStringId(R.string.add_edit_note_screen_tag)
            .assertIsDisplayed()
    }

    /**
     * Test that when the back button is clicked on the [AddEditNoteScreen],
     * the app navigates back to the [NotesScreen]
     * Happy path for [PouchNavHost]
     */
    @Test
    fun pouchNavHost_backClickedOnAddEditNoteScreen_navigateBackToNotesScreen() {
        // Simulate clicking the FAB to navigate to the AddEditNoteScreen (create note scenario)
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.add_new_note)
            .performClick()

        // Verify that we are on the AddEditNoteScreen by checking if the AddEditNoteScreen UI element is displayed
        composeTestRule.onNodeWithTagForStringId(R.string.add_edit_note_screen_tag)
            .assertIsDisplayed()

        // Simulate clicking the back button (assuming this will save the note and navigate back)
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.back_description)
            .performClick()

        // Verify that the app navigates back to the NotesScreen
        composeTestRule.onNodeWithTagForStringId(R.string.notes_screen_tag)
            .assertIsDisplayed()
    }

    /**
     * Test that when the delete button is clicked on the [AddEditNoteScreen],
     * the app navigates back to the [NotesScreen]
     * Happy path for [PouchNavHost]
     */
    @Test
    fun pouchNavHost_deleteClickedOnAddEditNoteScreen_navigateBackToNotesScreen() {
        // Simulate clicking the FAB to navigate to the AddEditNoteScreen (create note scenario)
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.add_new_note)
            .performClick()

        // Verify that we are on the AddEditNoteScreen by checking if the AddEditNoteScreen UI element is displayed
        composeTestRule.onNodeWithTagForStringId(R.string.add_edit_note_screen_tag)
            .assertIsDisplayed()

        // Simulate clicking the back button (assuming this will save the note and navigate back)
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.delete_note_description)
            .performClick()

        // Verify that the app navigates back to the NotesScreen
        composeTestRule.onNodeWithTagForStringId(R.string.notes_screen_tag)
            .assertIsDisplayed()
    }

    /**
     * Test that when a note is clicked in the [NotesScreen], we navigate to the [AddEditNoteScreen]
     * Happy path for [PouchNavHost]
     */
    @Test
    fun pouchNavHost_noteClickedOnNotesScreen_navigateToAddEditNoteScreen() {
        // Simulate clicking the FAB to navigate to the AddEditNoteScreen (create note scenario)
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.add_new_note)
            .performClick()

        // Add a note title or note body text so that the note can be saved
        composeTestRule.onNodeWithStringId(R.string.note_title_hint)
            .performTextInput("test title")

        // Navigate back to the NotesScreen
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.back_description)
            .performClick()

        // Simulate clicking on a note item in the NotesScreen
        composeTestRule.onNodeWithTagForStringId(R.string.notes_list_tag)
            .onChildAt(0)
            .performClick()

        // Assert that current route in the navController is AddEditNoteScreen's route
        navController.assertCurrentRouteName(AddEditNoteDestination.routeWithArgs)

        // Verify that we are on the AddEditNoteScreen by checking if the AddEditNoteScreen UI element is displayed
        composeTestRule.onNodeWithTagForStringId(R.string.add_edit_note_screen_tag)
            .assertIsDisplayed()

        // Verify that the noteId argument is Not passed as 0 (indicating editing an existing note)
        val noteIdArg =
            navController.currentBackStackEntry?.arguments?.getInt(AddEditNoteDestination.noteIdArg)
        assertNotEquals(0, noteIdArg)
    }
}


/**
 * Extension function on NavController to assert the current route name.
 */
fun NavController.assertCurrentRouteName(expectedRouteName: String) {
    assertEquals(expectedRouteName, currentBackStackEntry?.destination?.route)
}