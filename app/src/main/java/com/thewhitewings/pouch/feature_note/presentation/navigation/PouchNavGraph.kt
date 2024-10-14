package com.thewhitewings.pouch.feature_note.presentation.navigation

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
import com.thewhitewings.pouch.feature_note.presentation.notes.NotesDestination
import com.thewhitewings.pouch.feature_note.presentation.notes.NotesScreen
import com.thewhitewings.pouch.feature_note.presentation.notes.NotesViewModel
import com.thewhitewings.pouch.feature_note.presentation.add_edit_note.AddEditNoteDestination
import com.thewhitewings.pouch.feature_note.presentation.add_edit_note.AddEditNoteScreen
import com.thewhitewings.pouch.feature_note.presentation.add_edit_note.AddEditNoteViewModel
import com.thewhitewings.pouch.feature_note.util.Zone

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
        startDestination = NotesDestination.route,
        modifier = modifier
    ) {

        // Notes Screen
        composable(
            route = NotesDestination.route
        ) {
            val viewModel: NotesViewModel = viewModel(factory = NotesViewModel.Factory)
            val notesUiState by viewModel.uiState.collectAsState()

            NotesScreen(
                uiState = notesUiState,
                navigateBack = {
                    if (notesUiState.zone == Zone.BOX_OF_MYSTERIES) viewModel.toggleZone()
                    else (navController.context as? Activity)?.finish()
                },
                navigateToCreateNote = { navController.navigate("${AddEditNoteDestination.route}/0") },
                navigateToEditNote = { noteId -> navController.navigate("${AddEditNoteDestination.route}/$noteId") },
                onSearchNotes = viewModel::updateSearchQuery,
                onSortNotes = viewModel::updateSortOption,
                onToggleZone = viewModel::knockBoxOfMysteries
            )
        }

        // [AddEditNoteScreen]
        composable(
            route = AddEditNoteDestination.routeWithArgs,
            arguments = listOf(navArgument(AddEditNoteDestination.noteIdArg) {
                type = NavType.IntType
                defaultValue = 0
            })
        ) {
            val viewModel: AddEditNoteViewModel = viewModel(factory = AddEditNoteViewModel.Factory)
            val addEditNoteUiState by viewModel.uiState.collectAsState()

            AddEditNoteScreen(
                uiState = addEditNoteUiState,
                navigateBack = { viewModel.createOrUpdateNote(); navController.popBackStack() },
                onNavigateUp = { viewModel.createOrUpdateNote(); navController.navigateUp() },
                onNoteDelete = { viewModel.deleteNote(); navController.popBackStack() },
                onNoteTitleChange = { viewModel.updateNoteTitle(it) },
                onNoteBodyChange = { viewModel.updateNoteBody(it) }
            )
        }
    }
}