package com.thewhitewings.pouch.data;

import static com.thewhitewings.pouch.utils.Constants.BOM_ZONE_SORT_OPTION_PREFERENCE_KEY;
import static com.thewhitewings.pouch.utils.Constants.CREATIVE_ZONE_SORT_OPTION_PREFERENCE_KEY;
import static com.thewhitewings.pouch.utils.Constants.PREFERENCES_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.thewhitewings.pouch.utils.Zone;

/**
 * A class that interacts with the SharedPreferences of the app and stores user preferences
 */
public class PouchPreferences {

    private static final String TAG = "PouchPreferences";
    private final SharedPreferences preferences;

    /**
     * Constructor of PouchPreferences
     *
     * @param context of the app or activity
     */
    public PouchPreferences(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Save the Sort Option preference in SharedPreferences for the provided zone
     *
     * @param sortOption preference to be saved
     * @param zone       current zone
     */
    public void saveSortOption(SortOption sortOption, Zone zone) {
        String sortOptionKey = zone == Zone.CREATIVE ?
                CREATIVE_ZONE_SORT_OPTION_PREFERENCE_KEY : BOM_ZONE_SORT_OPTION_PREFERENCE_KEY;

        preferences.edit()
                .putString(sortOptionKey, sortOption.name())
                .apply();
        Log.i(TAG, "Changed sort option preference to " + sortOption.name() +
                " for zone " + zone.name());
    }

    /**
     * Get the Sort Option preference from SharedPreferences for the provided zone
     *
     * @param zone current zone
     * @return stored Sort Option preference
     */
    public SortOption getSortOption(Zone zone) {
        String sortOptionKey = zone == Zone.CREATIVE ?
                CREATIVE_ZONE_SORT_OPTION_PREFERENCE_KEY : BOM_ZONE_SORT_OPTION_PREFERENCE_KEY;

        String savedSortOption = preferences.getString(sortOptionKey, SortOption.NEWEST_FIRST.name());
        return SortOption.valueOf(savedSortOption);
    }
}
