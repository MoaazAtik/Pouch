package com.thewhitewings.pouch

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.thewhitewings.pouch.data.DatabaseHelper
import com.thewhitewings.pouch.data.NotesRepository
import com.thewhitewings.pouch.data.OfflineNotesRepository
import com.thewhitewings.pouch.data.PouchPreferences
import com.thewhitewings.pouch.utils.Constants


// The name of the stored preferences file
private const val POUCH_PREFERENCE_NAME = "pouch_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = POUCH_PREFERENCE_NAME
)

/**
 * The main application class for the Pouch app.
 * Custom app entry point for manual dependency injection.
 */
class PouchApplication : Application() {

    // The notes repository of the app
    lateinit var notesRepository: NotesRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Initialize the database helper of the Creative zone
        val creativeDatabaseHelper = DatabaseHelper(
            this,
            Constants.CREATIVE_DATABASE_NAME,
            Constants.CREATIVE_DATABASE_VERSION
        )

        // Initialize the database helper of the Box of mysteries zone
        val bomDatabaseHelper = DatabaseHelper(
            this,
            Constants.BOM_DATABASE_NAME,
            Constants.BOM_DATABASE_VERSION
        )

        // Initialize the preferences of the app
        val pouchPreferences = PouchPreferences(dataStore)

        // Initialize the notes repository with the database helpers and preferences
        notesRepository =
            OfflineNotesRepository(creativeDatabaseHelper, bomDatabaseHelper, pouchPreferences)
    }
}
