package com.thewhitewings.pouch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thewhitewings.pouch.feature_note.data.repository.PouchPreferencesImpl
import com.thewhitewings.pouch.feature_note.domain.repository.PouchPreferences
import com.thewhitewings.pouch.feature_note.domain.util.SortOption
import com.thewhitewings.pouch.feature_note.util.Zone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.createTestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val TEST_DATASTORE_NAME: String = "test_datastore"

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PouchPreferencesImplInstrumentedTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val dispatcher = UnconfinedTestDispatcher()
    private val testCoroutineScope = createTestCoroutineScope(dispatcher + Job())
    private val dataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = testCoroutineScope,
            produceFile =
            { context.preferencesDataStoreFile(TEST_DATASTORE_NAME) }
        )

    private val pouchPreferences: PouchPreferences = PouchPreferencesImpl(dataStore)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        runTest {
            dataStore.edit { it.clear() }
        }
        testCoroutineScope.cancel()
    }

    /**
     * Test the saving of the [SortOption] preference for the Creative Zone.
     * Happy path for [PouchPreferencesImpl.saveSortOption]
     */
    @Test
    fun pouchPreferences_saveSortOptionForCreativeZone_savesCorrectPreference() = runTest {
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
    fun pouchPreferences_saveSortOptionForBomZone_savesCorrectPreference() = runTest {
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
     * Happy path for [PouchPreferencesImpl.getSortOptionStream]
     */
    @Test
    fun pouchPreferences_getSortOptionStreamForCreativeZone_returnsCorrectSortOption() = runTest {
        // Given: Save a sort option for the CREATIVE zone
        val expectedSortOption = SortOption.A_Z
        dataStore.edit { preferences ->
            preferences[PouchPreferencesImpl.CREATIVE_ZONE_SORT_OPTION_PREFERENCE_KEY] =
                expectedSortOption.name
        }

        // When: Retrieve the sort option flow
        val retrievedSortOption = pouchPreferences.getSortOptionStream(Zone.CREATIVE).first()

        // Then: Assert that the correct sort option is returned
        assertEquals(expectedSortOption, retrievedSortOption)
    }

    /**
     * Test the retrieval of the [SortOption] preference flow for the Bom Zone.
     * Happy path for [PouchPreferencesImpl.getSortOptionStream]
     */
    @Test
    fun pouchPreferences_getSortOptionStreamForBomZone_returnsCorrectSortOption() = runTest {
        // Given: Save a sort option for the BOM zone
        val expectedSortOption = SortOption.OLDEST_FIRST
        dataStore.edit { preferences ->
            preferences[PouchPreferencesImpl.BOM_ZONE_SORT_OPTION_PREFERENCE_KEY] =
                expectedSortOption.name
        }

        // When: Retrieve the sort option flow
        val retrievedSortOption =
            pouchPreferences.getSortOptionStream(Zone.BOX_OF_MYSTERIES).first()

        // Then: Assert that the correct sort option is returned
        assertEquals(expectedSortOption, retrievedSortOption)
    }

    /**
     * Test the retrieval of the [SortOption] preference flow when not set.
     * Happy path for [PouchPreferencesImpl.getSortOptionStream]
     */
    @Test
    fun pouchPreferences_getSortOptionStreamWhenNotSet_returnDefaultSortOption() = runTest {
        // When: Retrieve the sort option flow without setting any value in DataStore
        val retrievedSortOption = pouchPreferences.getSortOptionStream(Zone.CREATIVE).first()

        // Then: Assert that the default sort option is returned
        assertEquals(PouchPreferencesImpl.DEFAULT_SORT_OPTION, retrievedSortOption)
    }
}