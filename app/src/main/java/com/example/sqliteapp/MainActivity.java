package com.example.sqliteapp;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.recyclerview.widget.DefaultItemAnimator;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.util.ArrayList;
        import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NotesAdapter mAdapter;
    private List<Note> notesList = new ArrayList<>();
    private RecyclerView recyclerView;

    private TextView noNotesView;

    private DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.recycler_view);
        noNotesView = findViewById(R.id.empty_notes_view);


        db = new DatabaseHelper(this);
        notesList.addAll(db.getAllNote());


        mAdapter = new NotesAdapter(this, notesList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        toggleEmptyNotes();


        Button addNote = findViewById(R.id.addNoteBtn);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showNoteDialog(false, null, -1);
            }
        });


        /**
         * on long press on RecyclerView item, open alert dialog
         * with options to choose: Edit and Delete
         */
        recyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(this, recyclerView,

                        new RecyclerTouchListener.ClickListener() {
                            //the 2 methods below belongs to ClickListener
                            @Override
                            public void onClick(View view, int position) {
                            }

                            @Override
                            public void onLongClick(View view, int position) {

                                showActionsDialog(position);
                            }
                        }));

    }//onCreate


    /**
     * inserting new note in db (DatabaseHelper)
     * and refreshing the list
     */
    private void createNote(String note) {

        //inserting note in db
        long id = db.insertNote(note);


        //get the newly inserted note from db
        Note n = db.getNote(id);


        if(n != null) {

            //adding new note to array list at 0 position
            notesList.add(0, n);

            //refreshing the list
            mAdapter.notifyDataSetChanged();


            toggleEmptyNotes();
        }

    }//createNote()


    /**
     * updating note in db
     * and updating item in the list by its position
     */
    private void updateNote(String note, int position) {

        Note n = notesList.get(position);

        //updating note text (note body)
        n.setNote(note);

        //updating note in db
        db.updateNote(n);

        ///updating the note in the list
        notesList.set(position, n);


        //refreshing the list
        mAdapter.notifyDataSetChanged();


        toggleEmptyNotes();

    }//updateNote()


    /**
     * Deleting note from SQLite and
     * removing the item from the list by its position
     */
    private void deleteNote(int position) {

        //deleting the note from db
        db.deleteNote(notesList.get(position));

        //removing the note from the list
        notesList.remove(position);

        mAdapter.notifyDataSetChanged();


        toggleEmptyNotes();

    }//deleteNote()



    /**
     * opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    //what does Edit - 0 mean ??

    private void showActionsDialog(final int position) {

        CharSequence colors[] = new CharSequence[] {"Edit", "Delete"};
        //why did we call it colors??


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Choosee option");

        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 0) {

                    showNoteDialog(true, notesList.get(position), position);

                } else {

                    deleteNote(position);

                }
            }
        });


        builder.show();

    }//showActionDialog()


    /**
     * shows alert dialog with EditText options to enter / edit a note.
     * when shouldUpdate = true, it automatically displays old note and
     * changes the button text to UPDATE
     */
    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());

        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);


        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);

        alertDialogBuilderUserInput.setView(view);


        final EditText inputNote = view.findViewById(R.id.note);


        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if(shouldUpdate && note != null) {

            inputNote.setText(note.getNote());
        }


        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "updatee" : "savee", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .setNegativeButton("cancell", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });


        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();

        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //show toast message when no text is entered
                if (TextUtils.isEmpty(inputNote.getText().toString())) {

                    Toast.makeText(MainActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();

                    return;

                } else {

                    alertDialog.dismiss();
                }


                //check if user updating note
                if(shouldUpdate && note != null) {

                    //update note by its id
                    updateNote(inputNote.getText().toString(), position);

                } else {

                    //create new note
                    createNote(inputNote.getText().toString());
                }
            }
        });

    }//showNoteDialog()



    //toggling list and empty notes view
    private void toggleEmptyNotes() {

        //you can check notesList.size() > 0

//        if(db.getNotesCount() > 0) {
        //in the course it's written like this but there is no "getNotesCount()" method in DatabaseHelper, so I did it as following

        if(db.getAllNote().size() > 0) {

            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }


}//class MainActivity
