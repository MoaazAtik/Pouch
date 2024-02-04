package com.example.sqliteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SearchView svSearchNotes;
    private ImageButton btnSort;
    private TextView noNotesView;
    private NotesAdapter mAdapter;
    private List<Note> notesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        svSearchNotes = findViewById(R.id.sv_search_notes);
        btnSort = findViewById(R.id.btn_sort);
        recyclerView = findViewById(R.id.recycler_view);
        noNotesView = findViewById(R.id.empty_notes_view);

        databaseHelper = new DatabaseHelper(this);
        notesList.addAll(databaseHelper.getAllNotes());

        mAdapter = new NotesAdapter(notesList);
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        toggleEmptyNotes();

        FloatingActionButton btnCreateNote = findViewById(R.id.btn_create_note);
        btnCreateNote.setOnClickListener(v ->
                openNote(null, -1)
        );

        /*
        Touch Listener of Recycler View Items.
        on Click on RecyclerView item open note, on Swipe delete note.
         */
        RecyclerTouchListener recyclerTouchListener = new RecyclerTouchListener(
                this,
                recyclerView,
                new RecyclerTouchListener.TouchListener() {
                    @Override
                    public void onClick(View view, int position) {
                        openNote(notesList.get(position), position);
                    }

                    @Override
                    public void onSwiped(int position) {
                        deleteNote(position);
                    }
                }
        );
        // ItemTouchHelper to handle onSwiped
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(recyclerTouchListener);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnItemTouchListener(recyclerTouchListener);

        findViewById(R.id.activity_main_root)
                .setOnClickListener(v -> {
                            // Clear Focus of Sv search note, and Hide Soft Keyboard when outside of Sv search note is clicked
                            clearFocusAndHideKeyboard(svSearchNotes);
                        }
                );

        svSearchNotes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        // Button Sort
        btnSort.setOnClickListener(v -> {
            showSortingPopupMenu();
        });
    }//onCreate

    /**
     * Add new note to the Database, Notes List of Recycler View, and Adapter's notes lists
     *
     * @param noteTitle of newly added note
     * @param noteBody  of newly added note
     */
    private void createNote(String noteTitle, String noteBody) {
        // inserting note in Database
        long id = databaseHelper.insertNote(noteTitle, noteBody);

        // get the newly inserted note from Database
        Note n = databaseHelper.getNote(id);
        // check if the newly added note could be queried from the Database. It is needed especially in SQL Shard Failure situations.
        if (n != null) {
            // adding new note to Notes List at position 0
            notesList.add(0, n);
            // refreshing the Recycler view
            mAdapter.notifyItemInserted(0);
            // add note to the Adapter's notesListFull
            mAdapter.editNotesListFull(n, 0, NoteFragment.ACTION_CREATE);
            recyclerView.scrollToPosition(0);

            toggleEmptyNotes();
        }
    }

    /**
     * Update note in Database, Notes List of Recycler View, and Adapter's notes lists
     *
     * @param noteTitle of updated note
     * @param noteBody  of updated note
     * @param position  of note in Notes List to be updated
     */
    private void updateNote(String noteTitle, String noteBody, int position) {
        // getting reference to the note
        Note n = notesList.get(position);
        // updating note values in the Notes List
        n.setNoteTitle(noteTitle);
        n.setNoteBody(noteBody);
        // Get Current timestamp in Local time zone for Storing in Notes List
        n.setTimestamp(databaseHelper.getFormattedDateTime(2, null));
        // updating note in Database
        databaseHelper.updateNote(n);
        // refreshing the Recycler view
        mAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    /**
     * Delete note from Database, Notes List of Recycler View, and Adapter's notes lists
     *
     * @param position of note in Notes List to be deleted
     */
    private void deleteNote(int position) {
        // deleting the note from Database
        databaseHelper.deleteNote(notesList.get(position));

        // removing the note from the Notes List
        notesList.remove(position);
        // refreshing the Recycler view
        mAdapter.notifyItemRemoved(position);
        // remove note from the Adapter's notesListFull
        mAdapter.editNotesListFull(null, position, NoteFragment.ACTION_DELETE);

        toggleEmptyNotes();
    }

    /**
     * Open Existing or New Note to create or edit a note.
     *
     * @param note     that will be updated, or null when creating new note.
     * @param position of note to be updated, or -1 when creating new note.
     */
    private void openNote(final Note note, final int position) {
        /*
        Clear Focus of Sv search note when opening note fragment, and Hide the virtual keyboard if it was opened by clicking Sv search note.
        If Sv search note has the focus then a note fragment is opened and nothing was clicked in the fragment, while the fragment's screen is open if the Hard Keyboard got input it will be directed to Et search note.
        This step fixes that.
         */
        clearFocusAndHideKeyboard(svSearchNotes);

        NoteFragment noteFragment = new NoteFragment();
        // Pass note vales to fragment when Updating note
        if (note != null) {
            Bundle argsBundle = new Bundle();

            argsBundle.putString(DatabaseHelper.COLUMN_NOTE_TITLE, note.getNoteTitle());
            argsBundle.putString(DatabaseHelper.COLUMN_NOTE_BODY, note.getNoteBody());
            argsBundle.putString(DatabaseHelper.COLUMN_TIMESTAMP, note.getTimestamp());

            noteFragment.setArguments(argsBundle);
        }

        // Direct to NoteFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        /*
        Animations. this has to be before fragmentTransaction.replace()
         */
        fragmentTransaction.setCustomAnimations(
                androidx.fragment.R.animator.fragment_fade_enter, // Enter animation
                androidx.fragment.R.animator.fragment_fade_exit, // Exit animation
                androidx.fragment.R.animator.fragment_close_enter, // Pop enter animation (when navigating back)
                androidx.fragment.R.animator.fragment_fade_exit // Pop exit animation (when navigating back)
        );
        fragmentTransaction.replace(R.id.fragment_container_note, noteFragment);
        fragmentTransaction.commit();


        noteFragment.setDataPassListener(new NoteFragment.DataPassListener() {
            @Override
            public void onDataPass(int action, String noteTitle, String noteBody) {
                // check wanted action
                switch (action) {
                    case NoteFragment.ACTION_CREATE:
                        // Create note only if it has content
                        if (!TextUtils.isEmpty(noteBody) || !TextUtils.isEmpty(noteTitle))
                            createNote(noteTitle, noteBody);
                        break;
                    case NoteFragment.ACTION_UPDATE:
                        // Update note only if its content was changed
                        if (note != null && (!note.getNoteBody().equals(noteBody) || !note.getNoteTitle().equals(noteTitle)))
                            updateNote(noteTitle, noteBody, position);
                        break;
                    case NoteFragment.ACTION_DELETE:
                        deleteNote(position);
                        break;
                    default: // ACTION_CLOSE_ONLY
                        break;
                }
            }
        });
    }

    /**
     * Clear the Focus of the passed view, and Hide Soft (Virtual / Device's) Keyboard.
     *
     * @param view to clear its focus.
     */
    public void clearFocusAndHideKeyboard(View view) {
        // Hide Soft (Virtual) Keyboard when outside of Et search note is clicked
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        // Clear Focus of Et search note when clicking outside
        view.clearFocus();
    }

    /**
     * Show Popup Menu to Sort Notes
     */
    private void showSortingPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(this, btnSort);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.popup_menu_sort, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_option_a_z) {
                sortNotes(Constants.SORT_A_Z, notesList);
                sortNotes(Constants.SORT_A_Z, mAdapter.notesListFull);
                return true;
            } else if (itemId == R.id.menu_option_z_a) {
                sortNotes(Constants.SORT_Z_A, notesList);
                sortNotes(Constants.SORT_Z_A, mAdapter.notesListFull);
                return true;
            } else if (itemId == R.id.menu_option_o) {
                sortNotes(Constants.SORT_OLDEST_FIRST, notesList);
                sortNotes(Constants.SORT_OLDEST_FIRST, mAdapter.notesListFull);
                return true;
            } else if (itemId == R.id.menu_option_n) {
                sortNotes(Constants.SORT_NEWEST_FIRST, notesList);
                sortNotes(Constants.SORT_NEWEST_FIRST, mAdapter.notesListFull);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    /**
     * Sort Notes by the wanted parameter
     *
     * @param sortBy Constants.SORT_A_Z, Constants.SORT_Z_A, Constants.SORT_OLDEST_FIRST, or Constants.SORT_NEWEST_FIRST.
     */
    private void sortNotes(int sortBy, List<Note> notesList) {
        switch (sortBy) {
            case Constants.SORT_A_Z:
                Collections.sort(notesList, (o1, o2) -> {
                    String o1NoteTitle = o1.getNoteTitle();
                    String o2NoteTitle = o2.getNoteTitle();
                    if (o1NoteTitle == null) o1NoteTitle = "";
                    if (o2NoteTitle == null) o2NoteTitle = "";
                    return (o1NoteTitle + o1.getNoteBody()).compareToIgnoreCase(o2NoteTitle + o2.getNoteBody());
                });
                break;
            case Constants.SORT_Z_A:
                Collections.sort(notesList, (o1, o2) -> {
                    String o1NoteTitle = o1.getNoteTitle();
                    String o2NoteTitle = o2.getNoteTitle();
                    if (o1NoteTitle == null) o1NoteTitle = "";
                    if (o2NoteTitle == null) o2NoteTitle = "";
                    return (o1NoteTitle + o1.getNoteBody()).compareToIgnoreCase(o2NoteTitle + o2.getNoteBody());
                });
                Collections.reverse(notesList);
                break;
            case Constants.SORT_OLDEST_FIRST:
                Collections.sort(notesList,
                        (o1, o2) -> Long.compare(
                                getTimeInMillis(o1.getTimestamp()),
                                getTimeInMillis(o2.getTimestamp())
                        ));
                break;
            case Constants.SORT_NEWEST_FIRST:
                Collections.sort(notesList, new Comparator<Note>() {
                    @Override
                    public int compare(Note o1, Note o2) {
                        return Long.compare(
                                getTimeInMillis(o1.getTimestamp()),
                                getTimeInMillis(o2.getTimestamp())
                        );
                    }
                });
                Collections.reverse(notesList);
                break;
        }
        mAdapter.notifyDataSetChanged();
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
            Log.d(TAG, "stringToMillis: catch e " + e);
            throw new RuntimeException(e);
        }
        return dateMs;
    }

    /**
     * Toggle list when there are notes to display, Or empty notes View when there are none.
     */
    private void toggleEmptyNotes() {
        if (notesList.size() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }
}
