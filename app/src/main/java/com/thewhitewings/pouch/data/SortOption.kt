package com.thewhitewings.pouch.data

import android.util.Log
import androidx.annotation.StringRes
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.data.SortOption.A_Z
import com.thewhitewings.pouch.data.SortOption.NEWEST_FIRST
import com.thewhitewings.pouch.data.SortOption.OLDEST_FIRST
import com.thewhitewings.pouch.data.SortOption.Z_A
import com.thewhitewings.pouch.utils.Constants

private const val TAG = "SortOption"

/**
 * Sort Option Enum.
 * Used to sort the notes.
 */
enum class SortOption(

    /**
     * The id of the corresponding item in the sorting pop-up menu
     */
    val id: Int,
    @StringRes val label: Int
) {
    A_Z(id = 0, label = R.string.sort_option_a_z),
    Z_A(id = 1, label = R.string.sort_option_z_a),
    OLDEST_FIRST(id = 2, label = R.string.sort_option_oldest_first),
    NEWEST_FIRST(id = 3, label = R.string.sort_option_newest_first)
}

/**
 * Get the [SortOption] that corresponds to the given menu item id
 *
 * @param sortOptionId The id of the corresponding item in the sorting pop-up menu
 * @return The [SortOption] that corresponds to the given menu item id
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
