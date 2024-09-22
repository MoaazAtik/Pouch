package com.thewhitewings.pouch.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thewhitewings.pouch.utils.Zone
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val TAG = "PouchPreferences"

/**
 * A class that interacts with the DataStore of the app and stores user preferences
 */
class PouchPreferences(
    private val dataStore: DataStore<Preferences>
) {

    private companion object {
        val CREATIVE_ZONE_SORT_OPTION_PREFERENCE_KEY =
            stringPreferencesKey("creative_zone_sort_option")
        val BOM_ZONE_SORT_OPTION_PREFERENCE_KEY = stringPreferencesKey("bom_zone_sort_option")
        val DEFAULT_SORT_OPTION by lazy { SortOption.NEWEST_FIRST }
    }

    /**
     * Save the [SortOption] preference in DataStore
     *
     * @param sortOption preference to be saved
     * @param zone       current [Zone]
     */
    suspend fun saveSortOption(sortOption: SortOption, zone: Zone) {
        val sortOptionKey = getSortOptionKey(zone)
        Log.d(TAG, "saveSortOption: $sortOption , zone $zone")
        dataStore.edit { preference ->
            preference[sortOptionKey] = sortOption.name
        }
    }

    /**
     * Get the [SortOption] Flow from DataStore
     *
     * @param zone current [Zone]
     * @return Flow of stored sort option
     */
    fun getSortOptionFlow(zone: Zone): Flow<SortOption> {
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
     *
     * @param zone current [Zone]
     * @return Sort Option Key related to the provided zone
     */
    private fun getSortOptionKey(zone: Zone): Preferences.Key<String> {
        return if (zone == Zone.CREATIVE) CREATIVE_ZONE_SORT_OPTION_PREFERENCE_KEY
        else BOM_ZONE_SORT_OPTION_PREFERENCE_KEY
    }

}