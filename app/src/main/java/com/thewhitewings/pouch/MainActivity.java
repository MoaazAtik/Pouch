package com.thewhitewings.pouch;

import static com.thewhitewings.pouch.utils.DateTimeUtils.getFormattedDateTime;

import androidx.activity.OnBackPressedCallback;
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

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.thewhitewings.pouch.data.Note;
import com.thewhitewings.pouch.data.NotesRepository;
import com.thewhitewings.pouch.databinding.ActivityMainBinding;
import com.thewhitewings.pouch.ui.MainViewModel;
import com.thewhitewings.pouch.ui.adapters.NotesAdapter;
import com.thewhitewings.pouch.ui.adapters.RecyclerTouchListener;
import com.thewhitewings.pouch.utils.DateTimeFormatType;
import com.thewhitewings.pouch.utils.Zone;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private MainViewModel vm;
    private NotesAdapter adapter;
    private LiveData<List<Note>> notesLiveData;
    private LiveData<Zone> currentZone;

    private int bomKnocks = 0;
    private boolean bomTimeoutStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NotesRepository repository = ((PouchApplication) getApplication()).getNotesRepository();
        vm = new ViewModelProvider(this, new MainViewModel.MainViewModelFactory(repository)).get(MainViewModel.class);

        notesLiveData = vm.notesLiveData;
        currentZone =  vm.getCurrentZoneLiveData();

        setupRecyclerView();
        setupListeners();
        setupViewModelObservers();
        setupBackPressingBehaviour();

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
                openNote(Objects.requireNonNull(notesLiveData.getValue()).get(position));
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
        binding.btnCreateNote.setOnClickListener(v -> openNote(null));

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
            toggleZoneNameVisibility();
        });

        currentZone.observe(this, zone -> {
            clearFocusAndHideKeyboard(binding.svSearchNotes);
            if (zone == Zone.BOX_OF_MYSTERIES)
                goToBoxOfMysteries();
            else goToMainZone();
        });
    }

    private void setupBackPressingBehaviour() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (currentZone.getValue() == Zone.BOX_OF_MYSTERIES)
                    vm.toggleZone();
                else finish();
            }
        });
    }

    private void openNote(final Note note) {
        clearFocusAndHideKeyboard(binding.svSearchNotes);

        NoteFragment noteFragment = new NoteFragment();
        if (note != null) {
            Bundle argsBundle = new Bundle();
            argsBundle.putInt(Constants.COLUMN_ID, note.getId());
            argsBundle.putString(Constants.COLUMN_NOTE_TITLE, note.getNoteTitle());
            argsBundle.putString(Constants.COLUMN_NOTE_BODY, note.getNoteBody());
            argsBundle.putString(Constants.COLUMN_TIMESTAMP, getFormattedDateTime(DateTimeFormatType.LOCAL_TO_LOCAL_MEDIUM_LENGTH_FORMAT, note.getTimestamp()));
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

    private void toggleZoneNameVisibility() {
        if (!Objects.requireNonNull(notesLiveData.getValue()).isEmpty())
            binding.txtZoneName.setVisibility(View.GONE);
        else
            binding.txtZoneName.setVisibility(View.VISIBLE);
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
                            bomTimeoutStarted = false;
                            bomKnocks = 0;

                            vm.toggleZone();
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

    private void goToBoxOfMysteries() {
        binding.btnRevealBom.setBackgroundColor(Color.TRANSPARENT);
        binding.btnRevealBom.setVisibility(View.GONE);

        modifyLogo();
        modifyZoneName();

        binding.lvRevealScreen.setAnimation(R.raw.reveal_screen_black);
        binding.lvRevealScreen.setSpeed(0.5f);
        binding.lvRevealScreen.playAnimation();

        binding.lvRevealLoader.setVisibility(View.VISIBLE);
        binding.lvRevealLoader.playAnimation();
        new Handler(Looper.getMainLooper())
                .postDelayed(
                        () -> binding.lvRevealLoader.setVisibility(View.GONE),
                        2000
                );

        Snackbar.make(binding.activityMainRoot, getString(R.string.bom_revealing_message), Snackbar.LENGTH_LONG)
                .show();
    }

    private void goToMainZone() {
        binding.btnRevealBom.setVisibility(View.VISIBLE);

        modifyLogo();
        modifyZoneName();

        binding.lvRevealScreen.setAnimation(R.raw.reveal_screen_red);
        binding.lvRevealScreen.setSpeed(1f);
        binding.lvRevealScreen.playAnimation();
    }

    private void modifyLogo() {
        int initialColor;
        int finalColor;

        /*
        note that the currentZone here represents the updated value which is the destination you just arrived
         */
        if (currentZone.getValue() == Zone.BOX_OF_MYSTERIES) {
            initialColor = getResources().getColor(R.color.md_theme_light_primaryContainer, null);
            finalColor = getResources().getColor(R.color.gray_logo_bom, null);
        }
        else {
            initialColor = getResources().getColor(R.color.gray_logo_bom, null);
            finalColor = getResources().getColor(R.color.md_theme_light_primaryContainer, null);
        }

        ValueAnimator tintAnimator = ValueAnimator.ofArgb(initialColor, finalColor);
        tintAnimator.setDuration(3500);
        tintAnimator.addUpdateListener(animator -> {
            int animatedValue = (int) animator.getAnimatedValue();
            binding.imgLogo.setColorFilter(animatedValue);
        });
        tintAnimator.start();
    }

    private void modifyZoneName() {
        TextView txtZoneName = binding.txtZoneName;
        String zoneName;
        Typeface typeface;
        float initialTextSize;
        float finalTextSize;
        int initialColor;
        int finalColor;

        /*
        note that the currentZone here represents the updated value which is the destination you just arrived
         */
        if (currentZone.getValue() == Zone.BOX_OF_MYSTERIES) {
            zoneName = getString(R.string.box_of_mysteries);
            typeface = Typeface.create("cursive", Typeface.BOLD);
            initialTextSize = 26f;
            finalTextSize = 32f;
            initialColor = getResources().getColor(R.color.md_theme_light_inversePrimary, null);
            finalColor = Color.BLACK;
        }
        else {
            zoneName = getString(R.string.creative_zone);
            typeface = Typeface.create("sans-serif-light", Typeface.NORMAL);
            initialTextSize = 32f;
            finalTextSize = 26f;
            initialColor = Color.BLACK;
            finalColor = getResources().getColor(R.color.md_theme_light_inversePrimary, null);
        }

        txtZoneName.setText(zoneName);

        txtZoneName.setTypeface(typeface);

        ObjectAnimator textSizeAnimator = ObjectAnimator.ofFloat(txtZoneName, "textSize", initialTextSize, finalTextSize);
        textSizeAnimator.setDuration(1000);
        textSizeAnimator.start();

        ValueAnimator colorAnimator = ValueAnimator.ofArgb(initialColor, finalColor);
        colorAnimator.setDuration(3500);
        colorAnimator.addUpdateListener(animator -> txtZoneName.setTextColor((int) animator.getAnimatedValue()));
        colorAnimator.start();
    }
}
