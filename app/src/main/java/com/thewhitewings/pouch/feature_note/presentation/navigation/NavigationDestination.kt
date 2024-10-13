package com.thewhitewings.pouch.feature_note.presentation.navigation

/**
 * Interface to describe the navigation destinations for the app.
 * It is implemented by each screen.
 */
interface NavigationDestination {

    /**
     * Unique name to define the path for a composable
     */
    val route: String

    /**
     * String resource id to that contains title to be displayed for the screen.
     */
    val titleRes: Int
}