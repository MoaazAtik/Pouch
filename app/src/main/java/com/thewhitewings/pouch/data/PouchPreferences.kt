package com.thewhitewings.pouch.data

import com.thewhitewings.pouch.utils.Zone
import kotlinx.coroutines.flow.Flow

/**
 * An interface that interacts with the DataStore of the app and stores user preferences
 */
interface PouchPreferences {

    /**
     * Save the [SortOption] preference in DataStore for the provided zone
     * @param sortOption preference to be saved
     * @param zone       current [Zone]
     */
    suspend fun saveSortOption(sortOption: SortOption, zone: Zone)

    /**
     * Get the [SortOption] Stream from DataStore for the provided zone
     * @param zone current [Zone]
     * @return Flow of stored sort option
     */
    fun getSortOptionFlow(zone: Zone): Flow<SortOption>
}