package com.thewhitewings.pouch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.thewhitewings.pouch.data.Note;
import com.thewhitewings.pouch.databinding.ActivityBoxOfMysteriesBinding;
import com.google.android.material.snackbar.Snackbar;
import com.thewhitewings.pouch.ui.BoxOfMysteriesViewModel;
import com.thewhitewings.pouch.ui.NotesAdapter;
import com.thewhitewings.pouch.ui.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BoxOfMysteriesActivity extends AppCompatActivity {

    private static final String TAG = "BoxOfMysteriesActivity";
    private ActivityBoxOfMysteriesBinding binding;
    private NotesAdapter adapter;
    private BoxOfMysteriesViewModel vm;
    private LiveData<List<Note>> notesLiveData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBoxOfMysteriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(BoxOfMysteriesViewModel.class);
        notesLiveData = vm.getNotesLiveData();

        showWelcomingMessage();
        hideLoadingAnimation();

        setupRecyclerView();
        setupListeners();
        setupViewModelObservers();
    }

    private void showWelcomingMessage() {
        Snackbar.make(binding.activityBomRoot, "Box of Mysteries is revealed", Snackbar.LENGTH_LONG)
                .show();
    }

    private void hideLoadingAnimation() {
        new Handler(Looper.getMainLooper())
                .postDelayed(
                        () -> binding.lvRevealLoader.setVisibility(View.GONE),
                        2000
                );
    }

    private void setupRecyclerView() {
        adapter = new NotesAdapter();
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setAdapter(adapter);

        RecyclerTouchListener recyclerTouchListener = new RecyclerTouchListener(
                this, binding.recyclerView, new RecyclerTouchListener.TouchListener() {
            @Override
            public void onClick(int position) {
                openNote(Objects.requireNonNull(notesLiveData.getValue()).get(position), position);
            }

            @Override
            public void onSwiped(int position) {
                vm.deleteNote(position);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(recyclerTouchListener);
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);
        binding.recyclerView.addOnItemTouchListener(recyclerTouchListener);
    }

    private void setupListeners() {
        binding.btnCreateNote.setOnClickListener(v -> openNote(null, -1));

        binding.activityBomRoot.setOnClickListener(v -> clearFocusAndHideKeyboard(binding.svSearchNotes));

        binding.svSearchNotes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                vm.searchNotes(newText);
                return false;
            }
        });

        binding.btnSort.setOnClickListener(v -> showSortingPopupMenu());
    }

    private void setupViewModelObservers() {
        notesLiveData.observe(this, notes -> {
            adapter.setNotes(notes);
            toggleEmptyNotes();
        });
    }

    private void openNote(final Note note, final int position) {
        clearFocusAndHideKeyboard(binding.svSearchNotes);

        NoteFragment noteFragment = new NoteFragment();
        if (note != null) {
            Bundle argsBundle = new Bundle();
            argsBundle.putString(Constants.COLUMN_NOTE_TITLE, note.getNoteTitle());
            argsBundle.putString(Constants.COLUMN_NOTE_BODY, note.getNoteBody());
            argsBundle.putString(Constants.COLUMN_TIMESTAMP, vm.databaseHelper.getFormattedDateTime(Constants.FORMATTING_LOCAL, note.getTimestamp()));
            noteFragment.setArguments(argsBundle);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                androidx.fragment.R.animator.fragment_fade_enter,
                androidx.fragment.R.animator.fragment_fade_exit,
                androidx.fragment.R.animator.fragment_close_enter,
                androidx.fragment.R.animator.fragment_fade_exit
        );
        fragmentTransaction.replace(R.id.fragment_container_note, noteFragment);
        fragmentTransaction.commit();

        noteFragment.setDataPassListener((action, newNoteTitle, newNoteBody) -> {
            vm.handleNoteClosingAction(action, newNoteTitle, newNoteBody, note, position);
        });
    }

    void clearFocusAndHideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }

    private void showSortingPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(this, binding.btnSort);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.popup_menu_sort, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            vm.handleSortOption(item.getItemId());
            return true;
        });
        popupMenu.show();
    }

    private void toggleEmptyNotes() {
        if (!Objects.requireNonNull(notesLiveData.getValue()).isEmpty()) {
            binding.txtEmptyNotes.setVisibility(View.GONE);
        } else {
            binding.txtEmptyNotes.setVisibility(View.VISIBLE);
        }
    }
}
