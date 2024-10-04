package com.thewhitewings.pouch.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.onNodeWithContentDescriptionForStringId
import com.thewhitewings.pouch.onNodeWithTagForStringId
import com.thewhitewings.pouch.ui.HomeDestination
import com.thewhitewings.pouch.ui.NoteDestination
import org.junit.Assert.assertEquals
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
    fun onAppInitialization_homeScreenIsDisplayed() {
        // Assert that current route in the navController is HomeScreen's route
        assertEquals(HomeDestination.route, navController.currentDestination?.route)

        // Assert that the home screen is displayed
        composeTestRule.onNodeWithTagForStringId(R.string.home_screen_tag)
            .assertIsDisplayed()
    }

    @Test
    fun whenCreateNoteButtonClicked_navigateToNoteScreen() {
        // Simulate a click on the FAB to trigger note creation
        composeTestRule.onNodeWithContentDescriptionForStringId(R.string.add_new_note)
            .performClick()

        // Assert that we navigated to the Note Screen
        assertEquals(NoteDestination.routeWithArgs, navController.currentDestination?.route)

        // Verify that the noteId argument is passed as 0 (indicating new note creation)
        val noteIdArg = navController.currentBackStackEntry?.arguments?.getInt(NoteDestination.noteIdArg)
        assertEquals(0, noteIdArg)

        // Verify that the Note Screen is displayed
        composeTestRule.onNodeWithTagForStringId(R.string.note_screen_tag)
            .assertIsDisplayed()
    }

}