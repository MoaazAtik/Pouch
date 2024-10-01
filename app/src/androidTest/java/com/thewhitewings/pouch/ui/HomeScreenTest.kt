package com.thewhitewings.pouch.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.onNodeWithContentDescriptionForStringId
import com.thewhitewings.pouch.onNodeWithTagForStringId
import com.thewhitewings.pouch.utils.Zone
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    /**
     * Test that the FAB is displayed on the home screen
     * Happy path for [HomeScreen]
     */
    @Test
    fun homeScreen_displaysFAB() {
        // Provide the home screen state and other parameters
        composeTestRule.setContent {
            HomeScreen(
                homeUiState = HomeViewModel.HomeUiState(),
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
     * Test that animations are displayed when initializing a zone, i.e., [HomeUiState.showAnimations] is true
     * Happy path for [HomeScreen]
     */
    @Test
    fun homeScreen_displaysAnimations_whenInitializingZone() {
        // Given: showAnimations' default is true
        // Set the content with showAnimations = true
        composeTestRule.setContent {
            HomeScreen(
                homeUiState = HomeViewModel.HomeUiState(),
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
     * Test that animations are not displayed when a zone is already initialized, i.e., [HomeUiState.showAnimations] is false
     * Case: zone is already initialized
     * for [HomeScreen]
     */
    @Test
    fun homeScreen_noAnimations_whenZoneAlreadyInitialized() {
        // Given: showAnimations' default true
        // Set the content with showAnimations = true
        composeTestRule.setContent {
            HomeScreen(
                homeUiState = HomeViewModel.HomeUiState(showAnimations = false),
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
     * Happy path for [ShowAnimations]
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
     * Happy path for [ShowAnimations]
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


}
