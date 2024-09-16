package com.thewhitewings.pouch.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.data.Note
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
    Scaffold { innerPadding ->
        NoteScreenBody(
            noteUiState = noteUiState,
            navigateBack = navigateBack,
            onNavigateUp = onNavigateUp,
            onNoteDelete = onNoteDelete,
            onNoteTitleChange = onNoteTitleChange,
            onNoteBodyChange = onNoteBodyChange,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}

@Composable
fun NoteScreenBody(
    noteUiState: NoteViewModel.NoteUiState,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    onNoteDelete: () -> Unit,
    onNoteTitleChange: (String) -> Unit,
    onNoteBodyChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(
                start = dimensionResource(R.dimen.padding_medium),
                end = dimensionResource(R.dimen.padding_medium),
                top = dimensionResource(R.dimen.padding_medium),
                bottom = dimensionResource(R.dimen.padding_small)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BackHandler(onBack = navigateBack)
            IconButton(
                onClick = onNavigateUp
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back_description)
                )
            }
            IconButton(onClick = onNoteDelete) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ExitToApp,
                    contentDescription = stringResource(R.string.delete_note_description)
                )
            }
        }
        TextField(
            value = noteUiState.note.noteTitle,
            onValueChange = onNoteTitleChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.padding_extra_large))
                .background(Color.Transparent),
            placeholder = { Text(stringResource(R.string.note_title_hint)) },
            textStyle = MaterialTheme.typography.titleLarge,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text
            )
        )
        TextField(
            value = noteUiState.note.noteBody,
            onValueChange = onNoteBodyChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(R.dimen.padding_small))
                .background(Color.Transparent)
                .weight(1f),
            placeholder = { Text(stringResource(R.string.note_body_hint)) },
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text
            )
        )
        if (noteUiState.note.timestamp.isNotEmpty())
            Text(
                text = stringResource(R.string.timestamp_edited, noteUiState.note.timestamp),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium,
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

@Preview(showBackground = true)
@Composable
fun NoteScreenWithTimestampPreview() {
    PouchTheme {
        NoteScreen(
            noteUiState = NoteViewModel.NoteUiState(
                Note(
                    timestamp = stringResource(
                        R.string.timestamp_edited,
                        R.string.timestamp_edited
                    )
                )
            ),
            navigateBack = {},
            onNavigateUp = {},
            onNoteDelete = {},
            onNoteTitleChange = {},
            onNoteBodyChange = {}
        )
    }
}
