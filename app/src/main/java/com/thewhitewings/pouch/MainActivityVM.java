package com.thewhitewings.pouch;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivityVM extends AndroidViewModel {

    private static final String TAG = "MainActivityVM";

    DatabaseHelper databaseHelper;
    List<Note> notesList = new ArrayList<>();
    NotesAdapter mAdapter;

    public MainActivityVM(@NonNull Application application) {
        super(application);

        databaseHelper = new DatabaseHelper(
                application.getApplicationContext(),
                Constants.MAIN_DATABASE_NAME,
                Constants.MAIN_DATABASE_VERSION
        );
        notesList.addAll(databaseHelper.getAllNotes());
        mAdapter = new NotesAdapter(notesList);
    }

}
