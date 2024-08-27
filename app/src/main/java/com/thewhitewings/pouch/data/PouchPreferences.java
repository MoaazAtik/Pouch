package com.thewhitewings.pouch.data;

import static com.thewhitewings.pouch.utils.Constants.BOM_ZONE_SORT_OPTION_PREFERENCE_KEY;
import static com.thewhitewings.pouch.utils.Constants.CREATIVE_ZONE_SORT_OPTION_PREFERENCE_KEY;
import static com.thewhitewings.pouch.utils.Constants.PREFERENCES_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.thewhitewings.pouch.utils.Zone;


public class PouchPreferences {

    private static final String TAG = "PouchPreferences";
    private final SharedPreferences preferences;

    public PouchPreferences(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void saveSortOption(SortOption sortOption, Zone zone) {
        String sortOptionKey =
                zone == Zone.CREATIVE ? CREATIVE_ZONE_SORT_OPTION_PREFERENCE_KEY : BOM_ZONE_SORT_OPTION_PREFERENCE_KEY;

        preferences.edit()
                .putString(sortOptionKey, sortOption.name())
                .apply();
        Log.i(TAG, "Changed sort option preference to " + sortOption.name() + " for zone " + zone.name() + ".");
    }

    public SortOption getSortOption(Zone zone) {
        String sortOptionKey =
                zone == Zone.CREATIVE ? CREATIVE_ZONE_SORT_OPTION_PREFERENCE_KEY : BOM_ZONE_SORT_OPTION_PREFERENCE_KEY;

        String savedSortOption = preferences.getString(sortOptionKey, SortOption.NEWEST_FIRST.name());
        return SortOption.valueOf(savedSortOption);
    }
}

