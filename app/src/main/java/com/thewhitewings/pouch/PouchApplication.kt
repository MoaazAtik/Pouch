package com.thewhitewings.pouch

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.thewhitewings.pouch.data.NoteDatabase
import com.thewhitewings.pouch.data.NotesRepository
import com.thewhitewings.pouch.data.OfflineNotesRepository
import com.thewhitewings.pouch.data.PouchPreferencesImpl
import com.thewhitewings.pouch.utils.Constants

// The name of the stored preference DataStore file
private const val POUCH_PREFERENCE_NAME = "pouch_preferences"
// The DataStore instance of the app for storing preferences
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = POUCH_PREFERENCE_NAME
)

/**
 * The main application class for the Pouch app.
 * Custom app entry point for manual dependency injection.
 */
class PouchApplication : Application() {

    /**
     * The repository of the app that manages the interactions with the databases and preferences.
     */
    lateinit var notesRepository: NotesRepository
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
            OfflineNotesRepository(
                creativeDatabase.noteDao(),
                bomDatabase.noteDao(),
                pouchPreferences
            )
    }
}
