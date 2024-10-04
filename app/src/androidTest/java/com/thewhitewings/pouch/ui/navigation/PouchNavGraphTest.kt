package com.thewhitewings.pouch.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.onNodeWithContentDescriptionForStringId
import com.thewhitewings.pouch.onNodeWithStringId
import com.thewhitewings.pouch.onNodeWithTagForStringId
import com.thewhitewings.pouch.ui.HomeDestination
import com.thewhitewings.pouch.ui.NoteDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
     * Test that initially, the home screen is displayed
     * Happy path for [PouchNavHost]
     */
    @Test
    fun pouchNavHost_onAppInitialization_homeScreenIsDisplayed() {
        // Assert that current route in the navController is HomeScreen's route
        navController.assertCurrentRouteName(HomeDestination.route)

        // Assert that the home screen is displayed
        composeTestRule.onNodeWithTagForStringId(R.string.home_screen_tag)
            .assertIsDisplayed()
    }

    /**
     * Test that when the create note FAB is clicked, the note screen is displayed
     * Happy path for [PouchNavHost]
     */
    @Test
    fun pouchNavHost_whenCreateNoteButtonClicked_navigateToNoteScreen() {
        // Simulate a click on the FAB to trigger note creation
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.add_new_note)
            .performClick()

        // Assert that current route in the navController is NoteScreen's route
        navController.assertCurrentRouteName(NoteDestination.routeWithArgs)

        // Verify that the noteId argument is passed as 0 (indicating new note creation)
        val noteIdArg =
            navController.currentBackStackEntry?.arguments?.getInt(NoteDestination.noteIdArg)
        assertEquals(0, noteIdArg)

        // Verify that the Note Screen is displayed
        composeTestRule.onNodeWithTagForStringId(R.string.note_screen_tag)
            .assertIsDisplayed()
    }

    /**
     * Test that when the back button is clicked on the note screen,
     * the app navigates back to the home screen
     * Happy path for [PouchNavHost]
     */
    @Test
    fun pouchNavHost_backClickedOnNoteScreen_navigateBackToHomeScreen() {
        // Simulate clicking the FAB to navigate to the NoteScreen (create note scenario)
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.add_new_note)
            .performClick()

        // Verify that we are on the NoteScreen by checking if the NoteScreen UI element is displayed
        composeTestRule.onNodeWithTagForStringId(R.string.note_screen_tag)
            .assertIsDisplayed()

        // Simulate clicking the back button (assuming this will save the note and navigate back)
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.back_description)
            .performClick()

        // Verify that the app navigates back to the HomeScreen
        composeTestRule.onNodeWithTagForStringId(R.string.home_screen_tag)
            .assertIsDisplayed()
    }

    /**
     * Test that when the delete button is clicked on the note screen,
     * the app navigates back to the home screen
     * Happy path for [PouchNavHost]
     */
    @Test
    fun pouchNavHost_deleteClickedOnNoteScreen_navigateBackToHomeScreen() {
        // Simulate clicking the FAB to navigate to the NoteScreen (create note scenario)
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.add_new_note)
            .performClick()

        // Verify that we are on the NoteScreen by checking if the NoteScreen UI element is displayed
        composeTestRule.onNodeWithTagForStringId(R.string.note_screen_tag)
            .assertIsDisplayed()

        // Simulate clicking the back button (assuming this will save the note and navigate back)
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.delete_note_description)
            .performClick()

        // Verify that the app navigates back to the HomeScreen
        composeTestRule.onNodeWithTagForStringId(R.string.home_screen_tag)
            .assertIsDisplayed()
    }

    /**
     * Test that when a note is clicked in the home screen, we navigate to the note screen
     * Happy path for [PouchNavHost]
     */
    @Test
    fun pouchNavHost_noteClickedOnHomeScreen_navigateToNoteScreen() {
        // Simulate clicking the FAB to navigate to the NoteScreen (create note scenario)
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.add_new_note)
            .performClick()

        // Add a note title or note body text so that the note can be saved
        composeTestRule.onNodeWithStringId(R.string.note_title_hint)
            .performTextInput("test title")

        // Navigate back to the HomeScreen
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.back_description)
            .performClick()

        // Simulate clicking on a note item in the HomeScreen
        composeTestRule.onNodeWithTagForStringId(R.string.notes_list_tag)
            .onChildAt(0)
            .performClick()

        // Assert that current route in the navController is NoteScreen's route
        navController.assertCurrentRouteName(NoteDestination.routeWithArgs)

        // Verify that we are on the NoteScreen by checking if the NoteScreen UI element is displayed
        composeTestRule.onNodeWithTagForStringId(R.string.note_screen_tag)
            .assertIsDisplayed()

        // Verify that the noteId argument is Not passed as 0 (indicating editing an existing note)
        val noteIdArg =
            navController.currentBackStackEntry?.arguments?.getInt(NoteDestination.noteIdArg)
        assertNotEquals(0, noteIdArg)
    }
}


/**
 * Extension function on NavController to assert the current route name.
 */
fun NavController.assertCurrentRouteName(expectedRouteName: String) {
    assertEquals(expectedRouteName, currentBackStackEntry?.destination?.route)
}