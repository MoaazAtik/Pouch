package com.thewhitewings.pouch.feature_note.presentation.notes

import com.thewhitewings.pouch.feature_note.domain.model.Note
import com.thewhitewings.pouch.feature_note.domain.util.SortOption
import com.thewhitewings.pouch.feature_note.util.Zone

/**
 * UI state for the [NotesScreen].
 */
data class HomeUiState(
    val notesList: List<Note> = emptyList(),
    val zone: Zone = Zone.CREATIVE,
    val sortOption: SortOption = SortOption.NEWEST_FIRST,
    val searchQuery: String = "",
    val showAnimations: Boolean = true
)