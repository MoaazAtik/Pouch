package com.example.sqliteapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class NoteFragment extends Fragment {

    private static final String TAG = "NoteFragment";

    private ImageButton btnBack, btnDelete;
    private EditText etNoteTitle, etNoteBody;
    private TextView txtTimestamp;
    private DataPassListener dataPassListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate a custom layout for the fragment
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        // Initialize the UI elements
        btnBack = view.findViewById(R.id.btn_back);
        btnDelete = view.findViewById(R.id.btn_delete);
        etNoteTitle = view.findViewById(R.id.et_note_title);
        etNoteBody = view.findViewById(R.id.et_note_body);
        txtTimestamp = view.findViewById(R.id.txt_timestamp);

        initializeNote();

        //btnBack
        btnBack.setOnClickListener(v -> {
            closeNote();
        });

        //btnDelete
//        btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Direct to NotesFragment
//                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                // Animations. this has to be before fragmentTransaction.replace()
//                fragmentTransaction.setCustomAnimations(
//                        androidx.fragment.R.animator.fragment_fade_enter, // Enter animation
//                        androidx.fragment.R.animator.fragment_fade_exit, // Exit animation
//                        androidx.fragment.R.animator.fragment_close_enter, // Pop enter animation (when navigating back)
//                        androidx.fragment.R.animator.fragment_fade_exit // Pop exit animation (when navigating back)
//                );
//
//                fragmentTransaction.replace(R.id.fragment_container_notes, new NotesFragment());
//                fragmentTransaction.addToBackStack(null); // Optional, for back navigation
//
//                fragmentTransaction.commit();
//            }
//        });

        return view;
    }

    /**
     * Get and fill screen fields with corresponding note values when updating the note.
     */
    private void initializeNote() {
        Bundle argsBundle = getArguments();

        if (argsBundle != null) {
            String noteTitle = argsBundle.getString(DatabaseHelper.COLUMN_NOTE_TITLE);
            String noteBody = argsBundle.getString(DatabaseHelper.COLUMN_NOTE_BODY);
            String timestamp = argsBundle.getString(DatabaseHelper.COLUMN_TIMESTAMP);

            etNoteTitle.setText(noteTitle);
            etNoteBody.setText(noteBody);
            txtTimestamp.setText("Edited " + timestamp);
        }
    }

    /**
     * Close note fragment. Navigate to MainActivity and Save note values.
     */
    private void closeNote() {
        // Get note values from corresponding fields
        String noteTitle = etNoteTitle.getText().toString();
        String noteBody = etNoteBody.getText().toString();
        // Pass note values to be saved
        if (dataPassListener != null) {
            dataPassListener.onDataPass(
                    noteTitle,
                    noteBody
            );
        }

        // Get the FragmentManager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        // Begin a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Set fragment transaction Animations
        fragmentTransaction.setCustomAnimations(
                androidx.fragment.R.animator.fragment_fade_enter, // Enter animation
                androidx.fragment.R.animator.fragment_fade_exit, // Exit animation
                androidx.fragment.R.animator.fragment_close_enter, // Pop enter animation (when navigating back)
                androidx.fragment.R.animator.fragment_fade_exit // Pop exit animation (when navigating back)
        );

        // Remove the current fragment from the container
        fragmentTransaction.remove(this);
        // Commit the transaction
        fragmentTransaction.commit();
    }

    /**
     * Initialize DataPassListener
     * @param listener DataPassListener
     */
    public void setDataPassListener(DataPassListener listener) {
        this.dataPassListener = listener;
    }


    /**
     * Interface used to pass data from NoteFragment to MainActivity to be saved.
     */
    public interface DataPassListener {
        void onDataPass(
                String noteTitle,
                String noteBody
        );
    }
}
