package com.thewhitewings.pouch

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.thewhitewings.pouch.feature_note.data.data_source.NoteDatabase
import com.thewhitewings.pouch.feature_note.data.repository.OfflineNotesRepositoryImpl
import com.thewhitewings.pouch.feature_note.data.repository.PouchPreferencesImpl
import com.thewhitewings.pouch.feature_note.data.util.Constants
import com.thewhitewings.pouch.feature_note.domain.repository.OfflineNotesRepository

// The DataStore instance of the app for storing preferences
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.POUCH_PREFERENCE_NAME
)

/**
 * The main application class for the Pouch app.
 * Custom app entry point for manual dependency injection.
 */
class PouchApplication : Application() {

    /**
     * The repository of the app that manages the interactions with the databases and preferences.
     */
    lateinit var notesRepository: OfflineNotesRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Initialize the database of the Creative zone
        val creativeDatabase: NoteDatabase by lazy {
            NoteDatabase.getDatabase(this, Constants.CREATIVE_DATABASE_NAME)
        }

        // Initialize the database of the Box of mysteries zone
        val bomDatabase: NoteDatabase by lazy {
                NoteDatabase.getDatabase(this, Constants.BOM_DATABASE_NAME)
        }

        // Initialize the preference DataStore of the app
        val pouchPreferences = PouchPreferencesImpl(dataStore)

        // Initialize the notes repository with the databases and preferences
        notesRepository =
            OfflineNotesRepositoryImpl(
                creativeDatabase.noteDao(),
                bomDatabase.noteDao(),
                pouchPreferences
            )
    }
}
