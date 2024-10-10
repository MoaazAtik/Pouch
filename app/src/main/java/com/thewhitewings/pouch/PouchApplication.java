package com.thewhitewings.pouch;

import android.app.Application;

import com.thewhitewings.pouch.data.DatabaseHelper;
import com.thewhitewings.pouch.data.NotesRepository;
import com.thewhitewings.pouch.data.OfflineNotesRepository;
import com.thewhitewings.pouch.data.PouchPreferences;
import com.thewhitewings.pouch.utils.Constants;

/**
 * The main application class for the Pouch app.
 * Custom app entry point for manual dependency injection.
 */
public class PouchApplication extends Application {

    // The repository of the app that manages the interactions with the databases and preferences.
    private NotesRepository notesRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the database helper of the Creative zone
        DatabaseHelper creativeDatabaseHelper = new DatabaseHelper(
                this,
                Constants.CREATIVE_DATABASE_NAME,
                Constants.CREATIVE_AND_BOM_DATABASE_VERSION
        );

        // Initialize the database helper of the Box of mysteries zone
        DatabaseHelper bomDatabaseHelper = new DatabaseHelper(
                this,
                Constants.BOM_DATABASE_NAME,
                Constants.CREATIVE_AND_BOM_DATABASE_VERSION
        );

        // Initialize the SharedPreferences of the app
        PouchPreferences pouchPreferences = new PouchPreferences(this);

        // Initialize the notes repository with the database helpers and preferences
        notesRepository =
                new OfflineNotesRepository(
                        creativeDatabaseHelper,
                        bomDatabaseHelper,
                        pouchPreferences
                );
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
