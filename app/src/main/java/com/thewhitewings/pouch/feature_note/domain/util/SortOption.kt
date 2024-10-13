package com.thewhitewings.pouch.feature_note.domain.util

import android.util.Log
import androidx.annotation.StringRes
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.feature_note.domain.util.SortOption.NEWEST_FIRST

private const val TAG = "SortOption"

/**
 * Sort Option Enum.
 * Used to sort the notes.
 */
enum class SortOption(

    /**
     * The id of the sort option to be used in the sorting pop-up menu
     */
    val id: Int,

    /**
     * The label of the sort option to be displayed in the sorting pop-up menu
     */
    @StringRes val label: Int
) {

    /**
     * Alphabetical ascending order.
     */
    A_Z(id = 0, label = R.string.sort_option_a_z),

    /**
     * Alphabetical descending order.
     */
    Z_A(id = 1, label = R.string.sort_option_z_a),

    /**
     * Chronological ascending order based on the note's timestamp, i.e., Oldest first.
     */
    OLDEST_FIRST(id = 2, label = R.string.sort_option_oldest_first),

    /**
     * Chronological descending order based on the note's timestamp, i.e., Newest first.
     */
    NEWEST_FIRST(id = 3, label = R.string.sort_option_newest_first)
}

/**
 * Get the [SortOption] that corresponds to the given menu item id
 * @param  sortOptionId The id of the sort option
 * @return The [SortOption] that corresponds to the given entry id
 */
fun getSortOptionFromId(sortOptionId: Int): SortOption {
    for (option in SortOption.entries) {
        if (option.id == sortOptionId) {
            return option
        }
    }

    Log.e(TAG, "Invalid sortOptionId: $sortOptionId", IllegalArgumentException())
    return NEWEST_FIRST
}