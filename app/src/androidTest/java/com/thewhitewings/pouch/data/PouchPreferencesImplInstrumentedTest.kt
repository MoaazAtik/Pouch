package com.thewhitewings.pouch.data

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.datastore.core.DataStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import java.util.prefs.Preferences

@RunWith(AndroidJUnit4::class)
class PouchPreferencesImplInstrumentedTest {

    // Context and DataStore to be used in the test
    @get:Rule
//    val contextRule = ActivityScenarioRule(TestActivity::class.java)
    val contextRule = ActivityScenarioRule(ComponentActivity::class.java)

//    private lateinit var context: Context
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var pouchPreferences: PouchPreferencesImpl

    @Before
    fun setUp() {
//        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
//        val context: Context = ApplicationProvider.getApplicationContext()

//        val scenario = launchActivity<TestActivity>()
//        val context = contextRule.scenario.context
        val context = contextRule.scenario.result.resultData

        // Initialize DataStore
//        dataStore = context.createDataStore(name = "test_preferences")
        dataStore = PreferencesDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("test_datastore") }
        )

        // Initialize PouchPreferencesImpl with the DataStore
        pouchPreferences = PouchPreferencesImpl(dataStore)
    }

}