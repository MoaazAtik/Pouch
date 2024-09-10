package com.thewhitewings.pouch.data

import android.util.Log
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
    val menuItemId: Int
) {
    A_Z(R.id.menu_option_a_z),
    Z_A(R.id.menu_option_z_a),
    OLDEST_FIRST(R.id.menu_option_o),
    NEWEST_FIRST(R.id.menu_option_n)
}

/**
 * Get the [SortOption] that corresponds to the given menu item id
 *
 * @param menuItemId The id of the corresponding item in the sorting pop-up menu
 * @return The [SortOption] that corresponds to the given menu item id
 */
fun fromMenuItemId(menuItemId: Int): SortOption? {
    for (option in SortOption.entries) {
        if (option.menuItemId == menuItemId) {
            return option
        }
    }

    Log.e(TAG, "Invalid menuItemId: $menuItemId", IllegalArgumentException())
    return null // Or throw an exception if preferred
}

/**
 * Used by [DatabaseHelper] to sort the notes
 *
 * @return The SQL string to be used for database query
 */
fun SortOption.toSqlString(): String {
    return when (this) {
        A_Z -> "${Constants.COLUMN_NOTE_TITLE} COLLATE NOCASE ASC, ${Constants.COLUMN_NOTE_BODY} COLLATE NOCASE ASC"
        Z_A -> "${Constants.COLUMN_NOTE_TITLE} COLLATE NOCASE DESC, ${Constants.COLUMN_NOTE_BODY} COLLATE NOCASE DESC"
        OLDEST_FIRST -> "${Constants.COLUMN_TIMESTAMP} ASC"
        NEWEST_FIRST -> "${Constants.COLUMN_TIMESTAMP} DESC"
    }
}
