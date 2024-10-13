package com.thewhitewings.pouch.feature_note.data.preferences

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thewhitewings.pouch.feature_note.domain.preferences.PouchPreferences
import com.thewhitewings.pouch.feature_note.domain.util.SortOption
import com.thewhitewings.pouch.feature_note.util.Zone
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val TAG = "PouchPreferencesImpl"

/**
 * A class that implements [PouchPreferences] and interacts with the DataStore of the app and stores user preferences
 */
class PouchPreferencesImpl(
    private val dataStore: DataStore<Preferences>
) : PouchPreferences {

    /**
     * Save the [SortOption] preference in DataStore for the provided zone
     * @param sortOption preference to be saved
     * @param zone       current [Zone]
     */
    override suspend fun saveSortOption(sortOption: SortOption, zone: Zone) {
        val sortOptionKey = getSortOptionKey(zone)

        dataStore.edit { preference ->
            preference[sortOptionKey] = sortOption.name
        }
    }

    /**
     * Get the [SortOption] Stream from DataStore for the provided zone
     * @param zone current [Zone]
     * @return Flow of stored sort option
     */
    override fun getSortOptionFlow(zone: Zone): Flow<SortOption> {
        val sortOptionKey = getSortOptionKey(zone)

        return dataStore.data
            .catch {
                if (it is IOException) {
                    Log.e(TAG, "Error reading preferences.", it)
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }
            .map { preference ->
                SortOption.valueOf(
                    preference[sortOptionKey] ?: DEFAULT_SORT_OPTION.name
                )
            }
    }

    /**
     * Get the [SortOption] preference Key for the provided zone
     * @param zone current [Zone]
     * @return Sort Option Key related to the provided zone
     */
    private fun getSortOptionKey(zone: Zone): Preferences.Key<String> {
        return if (zone == Zone.CREATIVE) CREATIVE_ZONE_SORT_OPTION_PREFERENCE_KEY
        else BOM_ZONE_SORT_OPTION_PREFERENCE_KEY
    }


    companion object {

        /**
         * Key for the [SortOption] preference in the DataStore of the Creative Zone
         */
        val CREATIVE_ZONE_SORT_OPTION_PREFERENCE_KEY =
            stringPreferencesKey("creative_zone_sort_option")

        /**
         * Key for the [SortOption] preference in the DataStore of the Box of Mysteries Zone
         */
        val BOM_ZONE_SORT_OPTION_PREFERENCE_KEY = stringPreferencesKey("bom_zone_sort_option")

        /**
         * Default [SortOption] value
         */
        val DEFAULT_SORT_OPTION by lazy { SortOption.NEWEST_FIRST }
    }
}