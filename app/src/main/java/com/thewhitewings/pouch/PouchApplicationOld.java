package com.thewhitewings.pouch;

import android.app.Application;

import com.thewhitewings.pouch.data.DatabaseHelper;
import com.thewhitewings.pouch.data.NotesRepository;
import com.thewhitewings.pouch.data.OfflineNotesRepository;
import com.thewhitewings.pouch.data.PouchPreferences;
import com.thewhitewings.pouch.utils.Constants;

/**
 * The main application class for the Pouch app.
 */
public class PouchApplicationOld extends Application {

    // The notes repository of the app
    private NotesRepository notesRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the database helper of the Creative zone
        DatabaseHelper creativeDatabaseHelper = new DatabaseHelper(
                this,
                Constants.CREATIVE_DATABASE_NAME,
                Constants.CREATIVE_DATABASE_VERSION
        );

        // Initialize the database helper of the Box of mysteries zone
        DatabaseHelper bomDatabaseHelper = new DatabaseHelper(
                this,
                Constants.BOM_DATABASE_NAME,
                Constants.BOM_DATABASE_VERSION
        );

        // Initialize the preferences of the app
        PouchPreferences pouchPreferences = new PouchPreferences(this);

        // Initialize the notes repository with the database helpers and preferences
        notesRepository = new OfflineNotesRepository(creativeDatabaseHelper, bomDatabaseHelper, pouchPreferences);
    }

    /**
     * Get the notes repository of the app.
     *
     * @return the notes repository of the app
     */
    public NotesRepository getNotesRepository() {
        return notesRepository;
    }
}
