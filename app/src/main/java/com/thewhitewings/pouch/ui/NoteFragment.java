package com.thewhitewings.pouch.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.thewhitewings.pouch.PouchApplication;
import com.thewhitewings.pouch.R;
import com.thewhitewings.pouch.data.Note;
import com.thewhitewings.pouch.data.NotesRepository;
import com.thewhitewings.pouch.databinding.FragmentNoteBinding;

public class NoteFragment extends Fragment {

    private FragmentNoteBinding binding;
    private NoteViewModel noteViewModel;
    private LiveData<Note> noteLiveData;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNoteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NotesRepository repository = ((PouchApplication) requireActivity().getApplication()).getNotesRepository();
        noteViewModel = new ViewModelProvider(this, new NoteViewModel.NoteViewModelFactory(repository)).get(NoteViewModel.class);

        noteViewModel.initializeNote(getArguments());
        noteLiveData = noteViewModel.getNoteLiveData();

        setupListeners();
        setupViewModelObservers();
    }

    private void setupListeners() {
        binding.getRoot().setOnClickListener(v -> {
            ((MainActivity) requireActivity()).clearFocusAndHideKeyboard(binding.etNoteTitle);
            ((MainActivity) requireActivity()).clearFocusAndHideKeyboard(binding.etNoteBody);
        });

        binding.btnBack.setOnClickListener(v -> {
            noteViewModel.createOrUpdateNote(
                    binding.etNoteTitle.getText().toString(),
                    binding.etNoteBody.getText().toString()
            );
            closeNote();
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                noteViewModel.createOrUpdateNote(
                        binding.etNoteTitle.getText().toString(),
                        binding.etNoteBody.getText().toString()
                );
                closeNote();
            }
        });

        binding.btnDelete.setOnClickListener(v -> {
            noteViewModel.deleteNote();
            closeNote();
        });
    }

    private void setupViewModelObservers() {
        noteLiveData.observe(getViewLifecycleOwner(), note -> {
            binding.etNoteTitle.setText(note.getNoteTitle());
            binding.etNoteBody.setText(note.getNoteBody());
            binding.txtTimestamp.setText(getString(R.string.edited_timestamp, note.getTimestamp()));
        });
    }


    private void closeNote() {
        /*
        // Clear focus to prevent issues with SearchView focus in MainActivity
        Needed for back arrow and device's back button so the focus won't be automatically passed to Sv Search note.
         */
        binding.etNoteTitle.clearFocus();
        binding.etNoteBody.clearFocus();

        // Navigate back (remove the fragment)
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Avoid memory leaks
    }
}
