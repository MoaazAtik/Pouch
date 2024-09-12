package com.thewhitewings.pouch.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.ui.navigation.NavigationDestination
import com.thewhitewings.pouch.ui.theme.PouchTheme

object NoteDestination : NavigationDestination {
    override val route = "note"
    override val titleRes = R.string.app_name
    const val noteIdArg = "noteId"
    val routeWithArgs = "$route/{$noteIdArg}"
}

@Composable
fun NoteScreen(
    noteUiState: NoteViewModel.NoteUiState,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    onNoteDelete: () -> Unit,
    onNoteTitleChange: (String) -> Unit,
    onNoteBodyChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_small)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.AutoMirrored.Default.ExitToApp, contentDescription = "")
            }
        }
        TextField(value = "Title", onValueChange = {}, modifier = Modifier.fillMaxWidth())
        TextField(
            value = "Note...",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Text(
            text = "Edited Apr 23, 2024",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_small))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoteScreenPreview() {
    PouchTheme {
        NoteScreen(
            noteUiState = NoteViewModel.NoteUiState(),
            navigateBack = {},
            onNavigateUp = {},
            onNoteDelete = {},
            onNoteTitleChange = {},
            onNoteBodyChange = {}
        )
    }
}