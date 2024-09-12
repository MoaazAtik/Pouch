package com.thewhitewings.pouch.ui.navigation


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
//import com.example.inventory.ui.item.ItemDetailsDestination
//import com.example.inventory.ui.item.ItemDetailsScreen
//import com.example.inventory.ui.item.ItemEditDestination
//import com.example.inventory.ui.item.ItemEditScreen
//import com.example.inventory.ui.item.ItemEntryDestination
//import com.example.inventory.ui.item.ItemEntryScreen
import com.thewhitewings.pouch.ui.HomeDestination
import com.thewhitewings.pouch.ui.HomeScreen
import com.thewhitewings.pouch.ui.MainViewModel
import com.thewhitewings.pouch.ui.NoteDestination
import com.thewhitewings.pouch.ui.NoteScreen
import com.thewhitewings.pouch.ui.NoteViewModel

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun PouchNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(
            route = HomeDestination.route
        ) {
            val viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory)
            val homeUiState by viewModel.homeUiState.collectAsState()

            HomeScreen(
                homeUiState = homeUiState,
                navigateToNewNote = { navController.navigate(NoteDestination.route) },
                navigateToNoteUpdate = {
                    navController.navigate("${NoteDestination.route}/${it}")
                },
//                onSearchNotes = { searchQuery -> viewModel.updateSearchQuery(searchQuery) },
                onSearchNotes = viewModel::updateSearchQuery ,
                onSortNotes = { menuItemId -> viewModel.handleSortOptionSelection(menuItemId) },
                onToggleZone = { viewModel.toggleZone() }
            )
        }
//        composable(
//            route = NoteDestination.route,
//            arguments = listOf(navArgument(NoteDestination.noteIdArg) {
//                type = NavType.IntType
//            })
//        ) {
//            val viewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory)
//            val noteUiState by viewModel.noteUiState.collectAsState()
//
//            NoteScreen(
//                noteUiState = noteUiState,
//                navigateBack = { navController.popBackStack() },
//                onNavigateUp = { navController.navigateUp() },
//                onNoteDelete = { TODO() },
//                onNoteTitleChange = { TODO() },
//                onNoteBodyChange = { TODO() }
//            )
//        }

//        composable(
//            route = ItemDetailsDestination.routeWithArgs,
//            arguments = listOf(navArgument(ItemDetailsDestination.itemIdArg) {
//                type = NavType.IntType
//            })
//        ) {
//            ItemDetailsScreen(
//                navigateToEditItem = { navController.navigate("${ItemEditDestination.route}/$it") },
//                navigateBack = { navController.navigateUp() }
//            )
//        }
//        composable(
//            route = ItemEditDestination.routeWithArgs,
//            arguments = listOf(navArgument(ItemEditDestination.itemIdArg) {
//                type = NavType.IntType
//            })
//        ) {
//            ItemEditScreen(
//                navigateBack = { navController.popBackStack() },
//                onNavigateUp = { navController.navigateUp() }
//            )
//        }
    }
}