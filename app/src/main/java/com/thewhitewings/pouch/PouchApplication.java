package com.thewhitewings.pouch;

import android.app.Application;

import com.thewhitewings.pouch.data.DatabaseHelper;
import com.thewhitewings.pouch.data.NotesRepository;
import com.thewhitewings.pouch.data.OfflineNotesRepository;

public class PouchApplication extends Application {
    private NotesRepository notesRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHelper databaseHelper = new DatabaseHelper(
                this,
                Constants.MAIN_DATABASE_NAME,
                Constants.MAIN_DATABASE_VERSION
        );
        notesRepository = new OfflineNotesRepository(databaseHelper);
    }

    public NotesRepository getNotesRepository() {
        return notesRepository;
    }
}
