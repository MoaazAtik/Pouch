package com.thewhitewings.pouch.data;

import static com.thewhitewings.pouch.Constants.BOM_ZONE_SORT_OPTION_PREFERENCE_KEY;
import static com.thewhitewings.pouch.Constants.MAIN_ZONE_SORT_OPTION_PREFERENCE_KEY;
import static com.thewhitewings.pouch.Constants.PREFERENCES_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.thewhitewings.pouch.Constants;



public class PouchPreferences {

    private static final String TAG = "PouchPreferences";
    private final SharedPreferences preferences;

    public PouchPreferences(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void saveSortOption(SortOption sortOption, Constants.Zone zone) {
        String sortOptionKey =
                zone == Constants.Zone.MAIN ? MAIN_ZONE_SORT_OPTION_PREFERENCE_KEY : BOM_ZONE_SORT_OPTION_PREFERENCE_KEY;

        preferences.edit()
                .putString(sortOptionKey, sortOption.name())
                .apply();
        Log.d(TAG, "saveSortOption: sortOption " + sortOption.name());
    }

    public SortOption getSortOption(Constants.Zone zone) {
        String sortOptionKey =
                zone == Constants.Zone.MAIN ? MAIN_ZONE_SORT_OPTION_PREFERENCE_KEY : BOM_ZONE_SORT_OPTION_PREFERENCE_KEY;

        String savedSortOption = preferences.getString(sortOptionKey, SortOption.NEWEST_FIRST.name());
        Log.d(TAG, "getSortOption: sortOption " + savedSortOption);
        return SortOption.valueOf(savedSortOption);
    }
}

