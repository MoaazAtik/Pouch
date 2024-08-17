package com.thewhitewings.pouch.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.thewhitewings.pouch.Constants;
import com.thewhitewings.pouch.data.DatabaseHelper;
import com.thewhitewings.pouch.data.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivityVM extends AndroidViewModel {

    private static final String TAG = "MainActivityVM";

    public DatabaseHelper databaseHelper;
    public List<Note> notesList = new ArrayList<>();
    public NotesAdapter mAdapter;

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
