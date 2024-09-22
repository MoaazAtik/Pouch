package com.thewhitewings.pouch.data;

import android.util.Log;

import com.thewhitewings.pouch.R;

/**
 * Sort Option Enum
 * <p>
 * Used to sort the notes
 * </p>
 */
public enum SortOption {

    /**
     * Alphabetical ascending order.
     */
    A_Z(R.id.menu_option_a_z),

    /**
     * Alphabetical descending order.
     */
    Z_A(R.id.menu_option_z_a),

    /**
     * Chronological ascending order based on the note's timestamp, i.e., Oldest first.
     */
    OLDEST_FIRST(R.id.menu_option_o),

    /**
     * Chronological descending order based on the note's timestamp, i.e., Newest first.
     */
    NEWEST_FIRST(R.id.menu_option_n);

    private static final String TAG = "SortOption";

    // The id of the corresponding item in the sorting pop-up menu
    private final int menuItemId;

    /**
     * Constructor of SortOption
     *
     * @param menuItemId The id of the corresponding item in the sorting pop-up menu
     */
    SortOption(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    /**
     * Get the id of the corresponding item in the sorting pop-up menu
     *
     * @return The id
     */
    public int getMenuItemId() {
        return menuItemId;
    }

    /**
     * Get the {@link SortOption} that corresponds to the given menu item id
     *
     * @param menuItemId The id of the corresponding item in the sorting pop-up menu
     * @return The {@link SortOption} enum entry that corresponds to the given menu item id
     */
    public static SortOption fromMenuItemId(int menuItemId) {
        for (SortOption option : values()) {
            if (option.menuItemId == menuItemId) {
                return option;
            }
        }

        Log.e(TAG, "Invalid menuItemId: " + menuItemId, new IllegalArgumentException());
        return null; // Or throw an exception if preferred
    }

    /**
     * Used by {@link DatabaseHelper} to sort the notes
     *
     * @return The SQL string to be used for database query
     */
    public String toSqlString() {
        switch (this) {
            case A_Z:
                return "noteTitle COLLATE NOCASE ASC, noteBody COLLATE NOCASE ASC";
            case Z_A:
                return "noteTitle COLLATE NOCASE DESC, noteBody COLLATE NOCASE DESC";
            case OLDEST_FIRST:
                return "timestamp ASC";
            case NEWEST_FIRST:
            default:
                return "timestamp DESC";
        }
    }
}
