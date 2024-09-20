package com.thewhitewings.pouch.ui

import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.data.Note
import com.thewhitewings.pouch.ui.navigation.NavigationDestination
import com.thewhitewings.pouch.ui.theme.PouchTheme
import com.thewhitewings.pouch.utils.DateTimeFormatType
import com.thewhitewings.pouch.utils.DateTimeUtils

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
                start = dimensionResource(R.dimen.padding_small),
                end = dimensionResource(R.dimen.padding_small),
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
                onClick = onNavigateUp,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_small))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back_description),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }
            IconButton(
                onClick = onNoteDelete,
                modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_medium))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_note_description),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }
        }
        TextField(
            value = noteUiState.note.noteTitle,
            onValueChange = onNoteTitleChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.padding_large)),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            placeholder = {
                Text(
                    stringResource(R.string.note_title_hint),
                    color = Color.Gray,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            textStyle = MaterialTheme.typography.titleLarge,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            )
        )
        TextField(
            value = noteUiState.note.noteBody,
            onValueChange = onNoteBodyChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .weight(1f),
            placeholder = {
                Text(
                    stringResource(R.string.note_body_hint),
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            )
        )
        if (noteUiState.note.timestamp.isNotEmpty()) {
            val formattedTimestamp = DateTimeUtils.getFormattedDateTime(
                DateTimeFormatType.LOCAL_TO_LOCAL_MEDIUM_LENGTH_FORMAT,
                noteUiState.note.timestamp
            )
            Text(
                text = stringResource(R.string.timestamp_edited, formattedTimestamp),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun NoteScreenWithoutTimestampPreview() {
    PouchTheme(dynamicColor = false) {
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
fun NoteScreenPreview() {
    PouchTheme(dynamicColor = false) {
        NoteScreen(
            noteUiState = NoteViewModel.NoteUiState(
                Note(
                    timestamp = stringResource(
                        R.string.timestamp_not_formatted
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

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun NoteScreenNightPreview() {
    PouchTheme(dynamicColor = false) {
        NoteScreen(
            noteUiState = NoteViewModel.NoteUiState(
                Note(
                    timestamp = stringResource(
                        R.string.timestamp_not_formatted
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
