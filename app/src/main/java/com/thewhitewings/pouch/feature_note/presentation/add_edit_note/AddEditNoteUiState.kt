package com.thewhitewings.pouch.feature_note.presentation.add_edit_note

import com.thewhitewings.pouch.feature_note.domain.model.Note

/**
 * UI state for the [AddEditNoteScreen]
 */
data class AddEditNoteUiState(
    val note: Note = Note(timestamp = "")
)