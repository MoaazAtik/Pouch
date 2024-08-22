package com.thewhitewings.pouch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.SearchView;

import com.thewhitewings.pouch.data.Note;
import com.thewhitewings.pouch.databinding.ActivityMainBinding;
import com.thewhitewings.pouch.ui.MainViewModel;
import com.thewhitewings.pouch.ui.adapters.NotesAdapter;
import com.thewhitewings.pouch.ui.adapters.RecyclerTouchListener;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private NotesAdapter adapter;
    private MainViewModel vm;
    private LiveData<List<Note>> notesLiveData;

    private int bomKnocks = 0;
    private boolean bomTimeoutStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(MainViewModel.class);
        notesLiveData = vm.getNotesLiveData();

        setupRecyclerView();
        setupListeners();
        setupViewModelObservers();

        showBtnRevealBom();
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
                vm.deleteNote(Objects.requireNonNull(notesLiveData.getValue()).get(position));
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(recyclerTouchListener);
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);
        binding.recyclerView.addOnItemTouchListener(recyclerTouchListener);
    }

    private void setupListeners() {
        binding.btnCreateNote.setOnClickListener(v -> openNote(null, -1));

        binding.activityMainRoot.setOnClickListener(v -> clearFocusAndHideKeyboard(binding.svSearchNotes));

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

        binding.btnRevealBom.setOnClickListener(v -> revealBoxOfMysteries());
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
            binding.emptyNotesView.setVisibility(View.GONE);
        } else {
            binding.emptyNotesView.setVisibility(View.VISIBLE);
        }
    }

    private void showBtnRevealBom() {
        new Handler().postDelayed(() -> binding.btnRevealBom.setVisibility(View.VISIBLE), 1500);
    }

    private void revealBoxOfMysteries() {
        bomKnocks++;
        Handler handler = new Handler(Looper.getMainLooper());

        if (!bomTimeoutStarted) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                long timeoutKnocking = 7 * 1000; // 7 seconds
                long startKnockingTime = System.currentTimeMillis();
                bomTimeoutStarted = true;
                while (bomTimeoutStarted) {
                    long elapsedKnockingTime = System.currentTimeMillis() - startKnockingTime;
                    if (elapsedKnockingTime >= timeoutKnocking) {
                        bomTimeoutStarted = false;
                        bomKnocks = 0;
                        break;
                    } else if (bomKnocks == 4) {
                        runOnUiThread(() -> binding.btnRevealBom.setBackgroundResource(R.drawable.ripple_revealed));
                    } else if (bomKnocks == 5) {
                        handler.postDelayed(() -> {
                            startActivity(new Intent(MainActivity.this, BoxOfMysteriesActivity.class));
                            bomTimeoutStarted = false;
                            bomKnocks = 0;
                            binding.btnRevealBom.setBackgroundColor(Color.TRANSPARENT);
                        }, 500);
                        break;
                    }

                    synchronized (this) {
                        try {
                            wait(200);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
    }
}
