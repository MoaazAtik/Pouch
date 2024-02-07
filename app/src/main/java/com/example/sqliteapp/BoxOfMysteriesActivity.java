package com.example.sqliteapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.sqliteapp.databinding.ActivityBoxOfMysteriesBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class BoxOfMysteriesActivity extends AppCompatActivity {

    private static final String TAG = "BoxOfMysteriesActivity";
    private ActivityBoxOfMysteriesBinding binding;
    private BoxOfMysteriesVM vm;

    // use binding. instead
//    private TextView noNotesView;
//    private SearchView svSearchNotes;
//    private ImageButton btnSort;
//    private RecyclerView recyclerView;

    // use VM
//    private NotesAdapter mAdapter;
//    private List<Note> notesList = new ArrayList<>();
//    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBoxOfMysteriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(BoxOfMysteriesVM.class);

        showWelcomingMessage();
        buildRecyclerView();
        toggleEmptyNotes();

//        binding.btnCreateNote.setOnClickListener(v ->
//                vm.openNote(null, -1)
//        );

    } // onCreate

    private void showWelcomingMessage() {
        Snackbar.make(binding.activityBomRoot, "Box of Mysteries is open", Snackbar.LENGTH_LONG)
                .show();
    }

    private void buildRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setAdapter(vm.mAdapter);
    }
    /**
     * Toggle list when there are notes to display, Or empty notes View when there are none.
     */
    private void toggleEmptyNotes() {
        if (vm.notesList.size() > 0) {
            binding.txtEmptyNotes.setVisibility(View.GONE);
        } else {
            binding.txtEmptyNotes.setVisibility(View.VISIBLE);
        }
    }



}
