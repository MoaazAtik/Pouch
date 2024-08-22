package com.thewhitewings.pouch.ui;

import com.thewhitewings.pouch.Constants;
import com.thewhitewings.pouch.data.DatabaseChangeListener;
import com.thewhitewings.pouch.data.DatabaseHelper;
import com.thewhitewings.pouch.data.Note;
import com.thewhitewings.pouch.NoteFragment.DataPassListener;
import com.thewhitewings.pouch.data.SortOption;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class BoxOfMysteriesViewModel extends AndroidViewModel implements DatabaseChangeListener {

    private static final String TAG = "BoxOfMysteriesViewModel";
    public DatabaseHelper databaseHelper;
    private final MutableLiveData<List<Note>> notesLiveData;

    public BoxOfMysteriesViewModel(@NonNull Application application) {
        super(application);

        databaseHelper = new DatabaseHelper(
                application.getApplicationContext(),
                Constants.BOM_DATABASE_NAME,
                Constants.BOM_DATABASE_VERSION
        );
        notesLiveData = new MutableLiveData<>(databaseHelper.getAllNotes());
        databaseHelper.setDatabaseChangeListener(this);
    }

    @Override
    public void onDatabaseChanged() {
        // Update LiveData with the latest notes when a change happens
        notesLiveData.postValue(databaseHelper.getAllNotes());
    }

    public LiveData<List<Note>> getNotesLiveData() {
        return notesLiveData;
    }

    public void handleNoteClosingAction(int action, String newNoteTitle, String newNoteBody, Note note, int position) {
        switch (action) {
            case Constants.ACTION_CREATE:
                if (!TextUtils.isEmpty(newNoteBody) || !TextUtils.isEmpty(newNoteTitle)) {
                    createNote(newNoteTitle, newNoteBody);
                }
                break;
            case Constants.ACTION_UPDATE:
                if (note != null && (!note.getNoteBody().equals(newNoteBody) || !note.getNoteTitle().equals(newNoteTitle))) {
                    updateNote(newNoteTitle, newNoteBody, note);
                }
                break;
            case Constants.ACTION_DELETE:
                deleteNote(note);
                break;
            default:
                break;
        }
    }

    public void handleSortOption(int menuItemId) {
        SortOption selectedOption = SortOption.fromMenuItemId(menuItemId);
        if (selectedOption != null) {
            sortNotes(selectedOption);
        }
    }

    public void createNote(String noteTitle, String noteBody) {
        databaseHelper.insertNote(noteTitle, noteBody);
    }

    public void updateNote(String newNoteTitle, String noteBody, Note oldNote) {
        Note updatedNote = new Note();
        updatedNote.setId(oldNote.getId());
        updatedNote.setNoteTitle(newNoteTitle);
        updatedNote.setNoteBody(noteBody);
        updatedNote.setTimestamp(databaseHelper.getFormattedDateTime(Constants.CURRENT_LOCAL, null));

        databaseHelper.updateNote(updatedNote);
    }

    public void deleteNote(Note note) {
        databaseHelper.deleteNote(note);
    }

    public void searchNotes(String query) {
        List<Note> allNotes = databaseHelper.getAllNotes();
        if (query.isEmpty()) {
            notesLiveData.postValue(allNotes);
        } else {
            List<Note> filteredNotes = new ArrayList<>();
            String filterPattern = query.toLowerCase().trim();

            for (Note note : allNotes) {
                String noteTitle = note.getNoteTitle();
                String noteBody = note.getNoteBody();
                boolean matchTitle = false;
                boolean matchBody;

                if (noteTitle != null) {
                    matchTitle = noteTitle.toLowerCase().contains(filterPattern);
                }
                if (matchTitle) {
                    filteredNotes.add(note);
                } else if (noteBody != null) {
                    matchBody = noteBody.toLowerCase().contains(filterPattern);
                    if (matchBody) {
                        filteredNotes.add(note);
                    }
                }
            }

            notesLiveData.setValue(filteredNotes);
        }
    }

    public void sortNotes(SortOption sortOption) {
        List<Note> allNotes = notesLiveData.getValue();
        if (allNotes != null) {
            switch (sortOption) {
                case A_Z:
                    allNotes.sort((o1, o2) -> {
                        String o1NoteTitle = o1.getNoteTitle();
                        String o2NoteTitle = o2.getNoteTitle();
                        if (o1NoteTitle == null) o1NoteTitle = "";
                        if (o2NoteTitle == null) o2NoteTitle = "";
                        return (o1NoteTitle + o1.getNoteBody()).compareToIgnoreCase(o2NoteTitle + o2.getNoteBody());
                    });
                    break;
                case Z_A:
                    allNotes.sort((o1, o2) -> {
                        String o1NoteTitle = o1.getNoteTitle();
                        String o2NoteTitle = o2.getNoteTitle();
                        if (o1NoteTitle == null) o1NoteTitle = "";
                        if (o2NoteTitle == null) o2NoteTitle = "";
                        return (o1NoteTitle + o1.getNoteBody()).compareToIgnoreCase(o2NoteTitle + o2.getNoteBody());
                    });
                    Collections.reverse(allNotes);
                    break;
                case OLDEST_FIRST:
                    allNotes.sort((o1, o2) -> Long.compare(
                            getTimeInMillis(o1.getTimestamp()),
                            getTimeInMillis(o2.getTimestamp())
                    ));
                    break;
                case NEWEST_FIRST:
                    allNotes.sort(new Comparator<Note>() {
                        @Override
                        public int compare(Note o1, Note o2) {
                            return Long.compare(
                                    getTimeInMillis(o1.getTimestamp()),
                                    getTimeInMillis(o2.getTimestamp())
                            );
                        }
                    });
                    Collections.reverse(allNotes);
                    break;
            }
            notesLiveData.postValue(allNotes);
        }
    }


    /**
     * Convert Date Time String to Time in Milliseconds
     *
     * @param dateTime to convert
     * @return time in millis
     */
    private long getTimeInMillis(String dateTime) {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        long dateMs;
        try {
            date = sdFormat.parse(dateTime);
            dateMs = date.getTime();
        } catch (ParseException e) {
            Log.d(TAG, "getTimeInMillis: catch e " + e);
            throw new RuntimeException(e);
        }
        return dateMs;
    }

}
