package com.thewhitewings.pouch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thewhitewings.pouch.utils.Zone
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PouchPreferencesImplInstrumentedTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var pouchPreferences: PouchPreferencesImpl

    @Before
    fun setUp() {
        // Initialize context
        val context: Context = ApplicationProvider.getApplicationContext()

        // Initialize DataStore without using the 'by preferencesDataStore' delegate
        dataStore = PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("test_datastore") }
        )

        // Initialize PouchPreferencesImpl with the DataStore
        pouchPreferences = PouchPreferencesImpl(dataStore)
    }

    @After
    fun tearDown() {
        // Clear the data after the test to avoid data leakage
        runBlocking {
            dataStore.edit { it.clear() }
        }
    }

    /**
     * Test the saving of the [SortOption] preference for the Creative Zone.
     * Happy path for [PouchPreferencesImpl.saveSortOption]
     */
    @Test
    fun pouchPreferences_saveSortOptionForCreativeZone_savesCorrectPreference() = runBlocking {
        // Given: A sort option and zone
        val sortOption = SortOption.A_Z
        val zone = Zone.CREATIVE

        // When: saveSortOption is called
        pouchPreferences.saveSortOption(sortOption, zone)

        // Then: Retrieve the preference to verify it was saved correctly
        val preferences = dataStore.data.first()
        val retrievedSortOption =
            preferences[PouchPreferencesImpl.CREATIVE_ZONE_SORT_OPTION_PREFERENCE_KEY]

        // Assert that the saved sort option is correct
        assertEquals(sortOption.name, retrievedSortOption)
    }

    /**
     * Test the saving of the [SortOption] preference for the Bom Zone.
     * Happy path for [PouchPreferencesImpl.saveSortOption]
     */
    @Test
    fun pouchPreferences_saveSortOptionForBomZone_savesCorrectPreference() = runBlocking {
        // Given: A sort option and zone
        val sortOption = SortOption.OLDEST_FIRST
        val zone = Zone.BOX_OF_MYSTERIES

        // When: saveSortOption is called
        pouchPreferences.saveSortOption(sortOption, zone)

        // Then: Retrieve the preference to verify it was saved correctly
        val preferences = dataStore.data.first()
        val retrievedSortOption =
            preferences[PouchPreferencesImpl.BOM_ZONE_SORT_OPTION_PREFERENCE_KEY]

        // Assert that the saved sort option is correct
        assertEquals(sortOption.name, retrievedSortOption)
    }

    /**
     * Test the retrieval of the [SortOption] preference flow for the Creative Zone.
     * Happy path for [PouchPreferencesImpl.getSortOptionFlow]
     */
    @Test
    fun pouchPreferences_getSortOptionFlowForCreativeZone_returnsCorrectSortOption() = runBlocking {
        // Given: Save a sort option for the CREATIVE zone
        val expectedSortOption = SortOption.A_Z
        dataStore.edit { preferences ->
            preferences[PouchPreferencesImpl.CREATIVE_ZONE_SORT_OPTION_PREFERENCE_KEY] =
                expectedSortOption.name
        }

        // When: Retrieve the sort option flow
        val retrievedSortOption = pouchPreferences.getSortOptionFlow(Zone.CREATIVE).first()

        // Then: Assert that the correct sort option is returned
        assertEquals(expectedSortOption, retrievedSortOption)
    }

    /**
     * Test the retrieval of the [SortOption] preference flow for the Bom Zone.
     * Happy path for [PouchPreferencesImpl.getSortOptionFlow]
     */
    @Test
    fun pouchPreferences_getSortOptionFlowForBomZone_returnsCorrectSortOption() = runBlocking {
        // Given: Save a sort option for the BOM zone
        val expectedSortOption = SortOption.OLDEST_FIRST
        dataStore.edit { preferences ->
            preferences[PouchPreferencesImpl.BOM_ZONE_SORT_OPTION_PREFERENCE_KEY] =
                expectedSortOption.name
        }

        // When: Retrieve the sort option flow
        val retrievedSortOption =
            pouchPreferences.getSortOptionFlow(Zone.BOX_OF_MYSTERIES).first()

        // Then: Assert that the correct sort option is returned
        assertEquals(expectedSortOption, retrievedSortOption)
    }

    /**
     * Test the retrieval of the [SortOption] preference flow when not set.
     * Happy path for [PouchPreferencesImpl.getSortOptionFlow]
     */
    @Test
    fun pouchPreferences_getSortOptionFlowWhenNotSet_returnDefaultSortOption() = runBlocking {
        // When: Retrieve the sort option flow without setting any value in DataStore
        val retrievedSortOption = pouchPreferences.getSortOptionFlow(Zone.CREATIVE).first()

        // Then: Assert that the default sort option is returned
        assertEquals(PouchPreferencesImpl.DEFAULT_SORT_OPTION, retrievedSortOption)
    }
}