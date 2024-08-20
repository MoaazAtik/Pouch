package com.thewhitewings.pouch.data;

import android.util.Log;

import com.thewhitewings.pouch.R;

public enum SortOption {
    A_Z(R.id.menu_option_a_z),
    Z_A(R.id.menu_option_z_a),
    OLDEST_FIRST(R.id.menu_option_o),
    NEWEST_FIRST(R.id.menu_option_n);

    private static final String TAG = "SortOption";

    private final int menuItemId;

    SortOption(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    // Static method to map menuItemId to SortOption
    public static SortOption fromMenuItemId(int menuItemId) {
        for (SortOption option : values()) {
            if (option.menuItemId == menuItemId) {
                return option;
            }
        }

        Log.e(TAG, "Invalid menuItemId: " + menuItemId, new IllegalArgumentException());
        return null; // Or throw an exception if preferred
    }
}
