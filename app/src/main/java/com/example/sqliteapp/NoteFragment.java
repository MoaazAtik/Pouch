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

    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);


        // Inflate a custom layout for the fragment
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        // Initialize the UI elements
//        btnBack = (ImageButton) view.findViewById(R.id.btn_back);
        btnBack = view.findViewById(R.id.btn_back);
        btnDelete = view.findViewById(R.id.btn_delete);
        etNoteTitle = view.findViewById(R.id.et_note_title);
        etNoteBody = view.findViewById(R.id.et_note_body);
        txtTimestamp = view.findViewById(R.id.txt_timestamp);

        databaseHelper = new DatabaseHelper(requireContext());

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

    private void initializeNote() {
        Bundle argsBundle = getArguments();
        Log.d(TAG, "initializeNote: ");
        Log.d(TAG, "argsBundle "+argsBundle);

        if (argsBundle != null) {
            int id = argsBundle.getInt(DatabaseHelper.COLUMN_ID);
            String noteTitle = argsBundle.getString(DatabaseHelper.COLUMN_NOTE_TITLE);
            String noteBody = argsBundle.getString(DatabaseHelper.COLUMN_NOTE_BODY);
            String timestamp = argsBundle.getString(DatabaseHelper.COLUMN_TIMESTAMP);
            Log.d(TAG, "id "+id);
            Log.d(TAG, "noteTitle "+noteTitle);
            Log.d(TAG, "noteBody "+noteBody);
            Log.d(TAG, "timestamp "+timestamp);

            etNoteTitle.setText(noteTitle);
            etNoteBody.setText(noteBody);
            txtTimestamp.setText("Edited " + timestamp);
        }
    }

    private void closeNote() {

        String noteTitle = etNoteTitle.getText().toString();
        String noteBody = etNoteBody.getText().toString();

        if (dataPassListener != null) {
            dataPassListener.onDataPass(
                    noteTitle,
                    noteBody
//                    "Neww Tt",
//                    "Hello World!",
            );
        }

        // Get the FragmentManager
//        assert getFragmentManager() != null; // Ensure that getFragmentManager() is not null
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Begin a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Animations. this has to be before fragmentTransaction.replace()
        fragmentTransaction.setCustomAnimations(
                androidx.fragment.R.animator.fragment_fade_enter, // Enter animation
                androidx.fragment.R.animator.fragment_fade_exit, // Exit animation
                androidx.fragment.R.animator.fragment_close_enter, // Pop enter animation (when navigating back)
                androidx.fragment.R.animator.fragment_fade_exit // Pop exit animation (when navigating back)
        );

        // Remove the current fragment from the container
        fragmentTransaction.remove(this);

        // Optionally, add the transaction to the back stack
        // This allows the user to navigate back to the previous fragment
        // fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();
    }

    public void setDataPassListener(DataPassListener listener) {
        this.dataPassListener = listener;
    }


    public interface DataPassListener {
        void onDataPass(
                String noteTitle,
                String noteBody
//                boolean shouldUpdate
        );
    }
}
