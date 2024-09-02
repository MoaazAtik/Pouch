package com.thewhitewings.pouch

import android.app.Application
import com.thewhitewings.pouch.data.DatabaseHelper
import com.thewhitewings.pouch.data.NotesRepository
import com.thewhitewings.pouch.data.OfflineNotesRepository
import com.thewhitewings.pouch.data.PouchPreferences
import com.thewhitewings.pouch.utils.Constants

/**
 * The main application class for the Pouch app.
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
        val pouchPreferences = PouchPreferences(this)

        // Initialize the notes repository with the database helpers and preferences
        notesRepository =
            OfflineNotesRepository(creativeDatabaseHelper, bomDatabaseHelper, pouchPreferences)
    }
}
