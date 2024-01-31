package com.example.sqliteapp;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class NoteFragment extends Fragment {

    private static final String TAG = "NoteFragment";

    public static final int ACTION_CLOSE_ONLY = -1;
    public static final int ACTION_CREATE = 0;
    public static final int ACTION_UPDATE = 1;
    public static final int ACTION_DELETE = 2;

    private ImageButton btnBack, btnDelete;
    private EditText etNoteTitle, etNoteBody;
    private TextView txtTimestamp;
    private DataPassListener dataPassListener;
    private boolean isNewNote;

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

        setIsNewNote();
        if (!isNewNote)
            initializeNote();

        // Clear Focus of EditText's and Hide Soft keyboard when Root layout is clicked
        view.getRootView()
                .setOnClickListener(v -> {
                        ((MainActivity) requireActivity())
                                .clearFocusAndHideKeyboard(
                                        etNoteTitle
                                );
                        ((MainActivity) requireActivity())
                                .clearFocusAndHideKeyboard(
                                        etNoteBody
                                );
                });

        //btnBack
        /*
        When updating note, note values would be passed to fragment. When nothing is passed it means I am creating a new note.
         */
        btnBack.setOnClickListener(v ->
                closeNote(
                        isNewNote ? ACTION_CREATE : ACTION_UPDATE
                )
        );

        //btnDelete
        btnDelete.setOnClickListener(v ->
                closeNote(
                        !isNewNote ? ACTION_DELETE : ACTION_CLOSE_ONLY
                )
        );

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handle device's back button pressing
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                closeNote(
                        getArguments() == null ? ACTION_CREATE : ACTION_UPDATE
                );
            }
        });
    }

    /**
     * Figure out if this is a new note or not, and assign {@link #isNewNote}.
     */
    private void setIsNewNote() {
        isNewNote = getArguments() == null;
    }

    /**
     * Get and fill screen fields with corresponding note values when updating the note after calling {@link #setIsNewNote()}.
     */
    private void initializeNote() {
        Bundle argsBundle = getArguments();

        String noteTitle = argsBundle.getString(DatabaseHelper.COLUMN_NOTE_TITLE);
        String noteBody = argsBundle.getString(DatabaseHelper.COLUMN_NOTE_BODY);
        String timestamp = argsBundle.getString(DatabaseHelper.COLUMN_TIMESTAMP);

        etNoteTitle.setText(noteTitle);
        etNoteBody.setText(noteBody);
        txtTimestamp.setText("Edited " + timestamp);
    }

    /**
     * Close note fragment. Pass wanted action and note values with DataPassListener.onDataPass then Navigate to MainActivity.
     *
     * @param action Wanted action to handle the note: {@link #ACTION_CREATE}, {@link #ACTION_UPDATE}, or {@link #ACTION_DELETE}
     */
    private void closeNote(int action) {
        // Get note values from corresponding fields
        String noteTitle = etNoteTitle.getText().toString();
        String noteBody = etNoteBody.getText().toString();

        // Pass wanted action and note values
        if (dataPassListener != null) {
            dataPassListener.onDataPass(
                    action,
                    noteTitle,
                    noteBody
            );
        }

        /*
        Needed for back arrow and device's back button so the focus won't be automatically passed to Sv Search note.
         */
        etNoteTitle.clearFocus();
        etNoteBody.clearFocus();

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
     *
     * @param listener DataPassListener
     */
    public void setDataPassListener(DataPassListener listener) {
        this.dataPassListener = listener;
    }


    /**
     * Interface used to pass data and action from NoteFragment to MainActivity.
     */
    public interface DataPassListener {
        /**
         * Pass data and action to MainActivity
         *
         * @param action    Wanted action to handle the note: {@link #ACTION_CREATE}, {@link #ACTION_UPDATE}, or {@link #ACTION_DELETE}
         * @param noteTitle .
         * @param noteBody  .
         */
        void onDataPass(
                int action,
                String noteTitle,
                String noteBody
        );
    }
}
