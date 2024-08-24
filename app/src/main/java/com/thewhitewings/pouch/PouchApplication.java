package com.thewhitewings.pouch;

import android.app.Application;

import com.thewhitewings.pouch.data.DatabaseHelper;
import com.thewhitewings.pouch.data.NotesRepository;
import com.thewhitewings.pouch.data.OfflineNotesRepository;
import com.thewhitewings.pouch.data.PouchPreferences;

public class PouchApplication extends Application {
    private NotesRepository notesRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHelper mainDatabaseHelper = new DatabaseHelper(
                this,
                Constants.MAIN_DATABASE_NAME,
                Constants.MAIN_DATABASE_VERSION
        );
        DatabaseHelper bomDatabaseHelper = new DatabaseHelper(
                this,
                Constants.BOM_DATABASE_NAME,
                Constants.BOM_DATABASE_VERSION
        );
        PouchPreferences pouchPreferences = new PouchPreferences(this);

        notesRepository = new OfflineNotesRepository(mainDatabaseHelper, bomDatabaseHelper, pouchPreferences);
    }

    public NotesRepository getNotesRepository() {
        return notesRepository;
    }
}
