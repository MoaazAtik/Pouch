package com.thewhitewings.pouch.ui.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.thewhitewings.pouch.ui.HomeDestination
import com.thewhitewings.pouch.ui.HomeScreen
import com.thewhitewings.pouch.ui.HomeViewModel
import com.thewhitewings.pouch.ui.NoteDestination
import com.thewhitewings.pouch.ui.NoteScreen
import com.thewhitewings.pouch.ui.NoteViewModel
import com.thewhitewings.pouch.utils.Zone

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun PouchNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {

        // Home Screen
        composable(
            route = HomeDestination.route
        ) {
            val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
            val homeUiState by viewModel.homeUiState.collectAsState()

            HomeScreen(
                homeUiState = homeUiState,
                navigateBack = {
                    if (homeUiState.zone == Zone.BOX_OF_MYSTERIES) viewModel.toggleZone()
                    else (navController.context as? Activity)?.finish()
                },
                navigateToCreateNote = { navController.navigate("${NoteDestination.route}/0") },
                navigateToEditNote = { noteId -> navController.navigate("${NoteDestination.route}/$noteId") },
                onSearchNotes = viewModel::updateSearchQuery,
                onSortNotes = viewModel::updateSortOption,
                onToggleZone = viewModel::revealBoxOfMysteries
            )
        }

        // Note Screen
        composable(
            route = NoteDestination.routeWithArgs,
            arguments = listOf(navArgument(NoteDestination.noteIdArg) {
                type = NavType.IntType
                defaultValue = 0
            })
        ) {
            val viewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory)
            val noteUiState by viewModel.noteUiState.collectAsState()

            NoteScreen(
                noteUiState = noteUiState,
                navigateBack = { viewModel.createOrUpdateNote(); navController.popBackStack() },
                onNavigateUp = { viewModel.createOrUpdateNote(); navController.navigateUp() },
                onNoteDelete = { viewModel.deleteNote(); navController.popBackStack() },
                onNoteTitleChange = { viewModel.updateNoteTitle(it) },
                onNoteBodyChange = { viewModel.updateNoteBody(it) }
            )
        }
    }
}