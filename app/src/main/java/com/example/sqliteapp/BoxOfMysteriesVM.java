package com.example.sqliteapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;
import java.util.List;

public class BoxOfMysteriesVM extends AndroidViewModel {

    private static final String TAG = "BoxOfMysteriesVM";

    DatabaseHelper databaseHelper;
    List<Note> notesList = new ArrayList<>();
    NotesAdapter mAdapter;

    public BoxOfMysteriesVM(@NonNull Application application) {
        super(application);

        databaseHelper = new DatabaseHelper(
                application.getApplicationContext(),
                Constants.BOM_DATABASE_NAME,
                Constants.BOM_DATABASE_VERSION
        );
        notesList.addAll(databaseHelper.getAllNotes());
        mAdapter = new NotesAdapter(notesList);
    }

}
