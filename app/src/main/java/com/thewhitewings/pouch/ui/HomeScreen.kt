package com.thewhitewings.pouch.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thewhitewings.pouch.ui.navigation.NavigationDestination
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.data.Note
import com.thewhitewings.pouch.data.SortOption
import com.thewhitewings.pouch.ui.theme.PouchTheme
import com.thewhitewings.pouch.utils.DateTimeFormatType
import com.thewhitewings.pouch.utils.DateTimeUtils
import com.thewhitewings.pouch.utils.Zone

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeUiState: MainViewModel.HomeUiState,
    navigateBack: () -> Unit,
    navigateToCreateNote: () -> Unit,
    navigateToEditNote: (Int) -> Unit,
    onSearchNotes: (searchQuery: String) -> Unit,
    onSortNotes: (sortOptionId: Int) -> Unit,
    onToggleZone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
//        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
//        topBar = {
//            PouchTopAppBar(
//                title = stringResource(HomeDestination.titleRes),
//                canNavigateBack = false,
//                scrollBehavior = scrollBehavior
//            )
//        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToCreateNote,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_new_note)
                )
            }
        },
    ) { innerPadding ->
        HomeBody(
            homeUiState = homeUiState,
            onItemClick = navigateToEditNote,
            onSearchNotes = onSearchNotes,
            onSortNotes = onSortNotes,
            onToggleZone = onToggleZone,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = innerPadding,
        )
        BackHandler(onBack = navigateBack)
    }
}

@Composable
private fun HomeBody(
    homeUiState: MainViewModel.HomeUiState,
    onItemClick: (Int) -> Unit,
    onSearchNotes: (searchQuery: String) -> Unit,
    onSortNotes: (sortOptionId: Int) -> Unit,
    onToggleZone: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (homeUiState.notesList.isEmpty()) {
            Text(
                text = stringResource(if (homeUiState.zone == Zone.CREATIVE) R.string.creative_zone else R.string.box_of_mysteries),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        }
        Image(
            painter = painterResource(R.drawable.logo_the_white_wings),
            contentDescription = stringResource(R.string.the_white_wings_logo)
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.padding_medium),
                    top = dimensionResource(R.dimen.padding_medium),
                    end = dimensionResource(R.dimen.padding_medium)
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = homeUiState.searchQuery,
                onValueChange = { onSearchNotes(it) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "")
                },
                modifier = Modifier.weight(1f)
            )

            var expandedSortMenu by remember { mutableStateOf(false) }
            Box {
                IconButton(
                    onClick = { expandedSortMenu = !expandedSortMenu }
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = expandedSortMenu,
                    onDismissRequest = { expandedSortMenu = false }) {

                    SortOption.entries.forEach { sortOption ->
                        DropdownMenuItem(
                            text = {
                                Text(text = stringResource(sortOption.label))
                            },
                            onClick = {
                                onSortNotes(sortOption.id)
                                expandedSortMenu = false
                            }
                        )
                    }
                }
            }
        }
        Button(
            onClick = onToggleZone,
            modifier = Modifier
        ) {
            Text(text = "Toggle Zone")
        }
        NotesList(
            notesList = homeUiState.notesList,
            onItemClick = { onItemClick(it.id) },
            contentPadding = contentPadding,
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small))
        )
    }
}

@Composable
private fun NotesList(
    notesList: List<Note>,
    onItemClick: (Note) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(140.dp),
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        items(items = notesList, key = { it.id }) { note ->
            NotesListItem(
                note = note,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onItemClick(note) }
                    .animateItem()
            )
        }
    }
}

@Composable
private fun NotesListItem(
    note: Note,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Text(
                text = note.noteTitle,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = DateTimeUtils.getFormattedDateTime(
                    DateTimeFormatType.LOCAL_TO_LOCAL_SHORT_LENGTH_FORMAT,
                    note.timestamp
                ),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = note.noteBody,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    PouchTheme {
        HomeScreen(
            homeUiState = MainViewModel.HomeUiState(
                notesList = listOf(
                    Note(1, "Game", "Note body", "Apr 23"),
                    Note(2, "Pen", "200.0", "30"),
                    Note(3, "TV", "300.0", "50")
                )
            ),
            navigateBack = {},
            navigateToCreateNote = {},
            navigateToEditNote = {},
            onSearchNotes = {},
            onSortNotes = {},
            onToggleZone = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeBodyPreview() {
    PouchTheme {
        HomeBody(
            homeUiState = MainViewModel.HomeUiState(
                notesList = listOf(
                    Note(1, "Game", "Note body", "Apr 23"),
                    Note(2, "Pen", "200.0", "30"),
                    Note(3, "TV", "300.0", "50")
                )
            ),
            onSearchNotes = {},
            onSortNotes = {},
            onToggleZone = {},
            onItemClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeBodyEmptyListPreview() {
    PouchTheme {
        HomeBody(
            homeUiState = MainViewModel.HomeUiState(notesList = listOf()),
            onSearchNotes = {},
            onSortNotes = {},
            onToggleZone = {},
            onItemClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotesListItemPreview() {
    PouchTheme {
        NotesListItem(
            Note(1, "Game", "Note body", "Apr 23"),
        )
    }
}