package com.thewhitewings.pouch.feature_note.presentation.notes

import android.content.Context
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.annotation.RawRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.feature_note.domain.model.Note
import com.thewhitewings.pouch.feature_note.domain.util.SortOption
import com.thewhitewings.pouch.feature_note.presentation.navigation.NavigationDestination
import com.thewhitewings.pouch.ui.theme.PouchTheme
import com.thewhitewings.pouch.ui.theme.grayLogoBom
import com.thewhitewings.pouch.feature_note.util.DateTimeFormatType
import com.thewhitewings.pouch.feature_note.util.DateTimeUtils
import com.thewhitewings.pouch.feature_note.util.Zone

private const val TAG = "NotesScreen"

/**
 * Navigation destination for [NotesScreen]
 */
object NotesDestination : NavigationDestination {
    override val route = "notes"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for [NotesScreen]
 */
@Composable
fun NotesScreen(
    uiState: NotesUiState,
    navigateBack: () -> Unit,
    navigateToCreateNote: () -> Unit,
    navigateToEditNote: (Int) -> Unit,
    onSearchNotes: (searchQuery: String) -> Unit,
    onSortNotes: (sortOptionId: Int) -> Unit,
    onToggleZone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.testTag(stringResource(R.string.notes_screen_tag)),
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        NotesScreenBody(
            uiState = uiState,
            onItemClick = navigateToEditNote,
            onSearchNotes = onSearchNotes,
            onSortNotes = onSortNotes,
            onToggleZone = onToggleZone,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
        BackHandler(onBack = navigateBack)

        if (uiState.showAnimations)
            ShowAnimations(
                zone = uiState.zone,
                snackbarHostState = snackbarHostState,
                context = LocalContext.current
            )
    }
}

/**
 * Animations to be displayed on each zone initialization.
 */
@Composable
fun ShowAnimations(
    zone: Zone,
    snackbarHostState: SnackbarHostState,
    context: Context,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .testTag(stringResource(R.string.zone_initialization_animations_tag))
    ) {
        // Show the appropriate animation based on the current zone
        when (zone) {
            Zone.CREATIVE -> {
                RevealScreenAnimation(R.raw.reveal_screen_red, 1f)
            }

            Zone.BOX_OF_MYSTERIES -> {
                RevealScreenAnimation(R.raw.reveal_screen_black, 0.5f)

                RevealLoaderAnimation()

                LaunchedEffect(Unit) {
                    snackbarHostState.showSnackbar(context.getString(R.string.bom_revealing_message))
                }
            }
        }
    }
}

@Composable
fun RevealScreenAnimation(
    @RawRes animationResId: Int,
    speed: Float,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationResId))
    LottieAnimation(
        composition,
        modifier = modifier
            .testTag(animationResId.toString()),
        contentScale = ContentScale.FillBounds,
        speed = speed
    )
}

@Composable
fun RevealLoaderAnimation(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.reveal_loader))
    LottieAnimation(
        composition,
        modifier = modifier
            .testTag(stringResource(R.string.reveal_loader_animation_tag))
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)
            .size(200.dp),
        speed = 0.6f
    )
}

@Composable
private fun NotesScreenBody(
    uiState: NotesUiState,
    onItemClick: (Int) -> Unit,
    onSearchNotes: (searchQuery: String) -> Unit,
    onSortNotes: (sortOptionId: Int) -> Unit,
    onToggleZone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)
            .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMedium)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.notesList.isEmpty())
            ZoneText(currentZone = uiState.zone)
        LogoImage(currentZone = uiState.zone)
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
            SearchNotesTextField(
                value = uiState.searchQuery,
                onValueChange = { onSearchNotes(it) },
                modifier = Modifier
                    .weight(1f)
            )

            SortNotesButton(
                onSortNotes = onSortNotes,
                focusManager = focusManager,
                modifier = Modifier
            )
        }
        BomRevealingButton(
            onToggleZone = onToggleZone,
            focusManager = focusManager,
            zone = uiState.zone
        )
        NotesList(
            notesList = uiState.notesList,
            onItemClick = { onItemClick(it.id) },
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small))
        )
    }
}

@Composable
private fun SearchNotesTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.magnifier),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.clear_search_notes_hint)
                    )
                }
            }
        },
        modifier = modifier
            .testTag(stringResource(R.string.search_notes_text_field_tag))
            .background(
                color = Color.White,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                shape = RoundedCornerShape(10.dp)
            ),
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(10.dp),
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.mulish_regular))
        ),
        placeholder = {
            Text(
                text = stringResource(R.string.search_notes_hint),
                color = Color.Gray
            )
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Search
        ),
        maxLines = Int.MAX_VALUE
    )
}

@Composable
private fun SortNotesButton(
    onSortNotes: (sortOptionId: Int) -> Unit,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    var expandedSortMenu by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = {
                focusManager.clearFocus()
                expandedSortMenu = !expandedSortMenu
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.sort),
                contentDescription = stringResource(R.string.sort_notes),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
        }
        DropdownMenu(
            expanded = expandedSortMenu,
            onDismissRequest = { expandedSortMenu = false },
            modifier = Modifier.testTag(stringResource(R.string.sort_options_menu_tag))
        ) {

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

@Composable
fun BomRevealingButton(
    onToggleZone: () -> Unit,
    focusManager: FocusManager,
    zone: Zone,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            focusManager.clearFocus()
            onToggleZone()
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier =
        if (zone == Zone.CREATIVE)
            modifier
                .size(width = 80.dp, height = 20.dp)
                .testTag(stringResource(R.string.bom_button_tag))
        else
            modifier
                .size(width = 0.dp, height = 20.dp)
                .testTag(stringResource(R.string.bom_button_tag))
    ) {}
}

@Composable
private fun NotesList(
    notesList: List<Note>,
    onItemClick: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(140.dp),
        modifier = modifier
            .testTag(stringResource(R.string.notes_list_tag))
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
        modifier = modifier.testTag(stringResource(R.string.notes_list_item_tag)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_medium),
                    vertical = dimensionResource(R.dimen.padding_10)
                ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Text(
                text = note.noteTitle,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = DateTimeUtils.getFormattedDateTime(
                    DateTimeFormatType.LOCAL_TO_LOCAL_SHORT_LENGTH_FORMAT,
                    note.timestamp
                ),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.inversePrimary,
                fontSize = 14.sp
            )
            Text(
                text = note.noteBody,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun LogoImage(currentZone: Zone) {
    val targetColor =
        if (currentZone == Zone.BOX_OF_MYSTERIES) grayLogoBom
        else MaterialTheme.colorScheme.primaryContainer

    // Animate the color change from currentColor to finalColor
    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 3500),
        label = "LogoColorAnimation"
    )

    // Display the logo with the animated color filter
    Image(
        painter = painterResource(R.drawable.logo_the_white_wings),
        contentDescription = stringResource(R.string.the_white_wings_logo),
        modifier = Modifier
            .size(140.dp),
        colorFilter = ColorFilter.tint(animatedColor)
    )
}

@Composable
fun ZoneText(currentZone: Zone) {
    // Determine values based on the current zone
    val zoneName: String
    val typeface: FontFamily
    val fontWeight: FontWeight
    val targetTextSize: Float
    val targetTextColor: Color

    if (currentZone == Zone.BOX_OF_MYSTERIES) {
        zoneName = stringResource(id = R.string.box_of_mysteries)
        typeface = FontFamily.Cursive
        fontWeight = FontWeight.Bold
        targetTextSize = 32f
        targetTextColor = Color.Black
    } else {
        zoneName = stringResource(id = R.string.creative_zone)
        typeface = FontFamily.SansSerif
        fontWeight = FontWeight.Light
        targetTextSize = 26f
        targetTextColor = MaterialTheme.colorScheme.primaryContainer
    }

    // Animate the text size
    val animatedTextSize by animateFloatAsState(
        targetValue = targetTextSize,
        animationSpec = tween(durationMillis = 1000),
        label = "ZoneNameTextSizeAnimation"
    )

    // Animate the color change
    val animatedTextColor by animateColorAsState(
        targetValue = targetTextColor,
        animationSpec = tween(durationMillis = 4500),
        label = "ZoneNameColorAnimation"
    )

    // Display the animated text
    Text(
        text = zoneName,
        fontFamily = typeface,
        fontWeight = fontWeight,
        fontSize = animatedTextSize.sp,
        color = animatedTextColor,
        textAlign = TextAlign.Center
    )
}


//@Preview(showBackground = true)
@Composable
private fun NotesScreenPreview() {
    PouchTheme(dynamicColor = false) {
        NotesScreen(
            uiState = NotesUiState(
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

//@Preview(
//    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
//)
@Composable
private fun NotesScreenNightPreview() {
    PouchTheme(dynamicColor = false) {
        NotesScreen(
            uiState = NotesUiState(
                notesList = listOf(
                    Note(1, "Game", "Note body", stringResource(R.string.timestamp_not_formatted)),
                    Note(
                        2,
                        "Pen",
                        "200.0\nStaggered",
                        stringResource(R.string.timestamp_not_formatted)
                    ),
                    Note(3, "TV", "300.0", stringResource(R.string.timestamp_not_formatted))
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

//@Preview(showBackground = true)
@Composable
private fun NotesScreenBodyPreview() {
    PouchTheme {
        NotesScreenBody(
            uiState = NotesUiState(
                notesList = listOf(
                    Note(1, "Game", "Note body", stringResource(R.string.timestamp_not_formatted)),
                    Note(
                        2,
                        "Pen",
                        "200.0\nStaggered",
                        stringResource(R.string.timestamp_not_formatted)
                    ),
                    Note(3, "TV", "300.0", stringResource(R.string.timestamp_not_formatted))
                )
            ),
            onSearchNotes = {},
            onSortNotes = {},
            onToggleZone = {},
            onItemClick = {}
        )
    }
}

//@Preview(showBackground = true)
@Composable
private fun NotesScreenBodyEmptyListPreview() {
    PouchTheme {
        NotesScreenBody(
            uiState = NotesUiState(notesList = listOf()),
            onSearchNotes = {},
            onSortNotes = {},
            onToggleZone = {},
            onItemClick = {}
        )
    }
}

//@Preview(showBackground = true)
@Composable
private fun NotesScreenBodyBomEmptyListPreview() {
    PouchTheme {
        NotesScreenBody(
            uiState = NotesUiState(
                notesList = listOf(),
                zone = Zone.BOX_OF_MYSTERIES
            ),
            onSearchNotes = {},
            onSortNotes = {},
            onToggleZone = {},
            onItemClick = {}
        )
    }
}

//@Preview(showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
//)
@Composable
private fun NotesScreenBodyEmptyListNightPreview() {
    PouchTheme {
        NotesScreenBody(
            uiState = NotesUiState(notesList = listOf()),
            onSearchNotes = {},
            onSortNotes = {},
            onToggleZone = {},
            onItemClick = {}
        )
    }
}

//@Preview(showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
//)
@Composable
private fun NotesScreenBodyBomEmptyListNightPreview() {
    PouchTheme {
        NotesScreenBody(
            uiState = NotesUiState(
                notesList = listOf(),
                zone = Zone.BOX_OF_MYSTERIES
            ),
            onSearchNotes = {},
            onSortNotes = {},
            onToggleZone = {},
            onItemClick = {}
        )
    }
}

//@Preview(showBackground = true)
@Composable
private fun SearchNotesPreview() {
    PouchTheme(dynamicColor = false) {
        SearchNotesTextField(
            value = "",
            onValueChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchNotesWithTextPreview() {
    PouchTheme(dynamicColor = false) {
        SearchNotesTextField(
            value = "note",
            onValueChange = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun SearchNotesNightPreview() {
    PouchTheme(dynamicColor = false) {
        SearchNotesTextField(
            value = "",
            onValueChange = {}
        )
    }
}

//@Preview(showBackground = true)
@Composable
private fun NotesListItemPreview() {
    PouchTheme {
        NotesListItem(
            Note(1, "Game", "Note body", stringResource(R.string.timestamp_not_formatted))
        )
    }
}