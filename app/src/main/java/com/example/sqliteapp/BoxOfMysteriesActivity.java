package com.example.sqliteapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sqliteapp.databinding.ActivityBoxOfMysteriesBinding;
import com.google.android.material.snackbar.Snackbar;

public class BoxOfMysteriesActivity extends AppCompatActivity {

    private ActivityBoxOfMysteriesBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBoxOfMysteriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bttt.setOnClickListener(v -> {
            Snackbar.make(v, "lkjljl", Snackbar.LENGTH_LONG)
                    .setAnchorView(v)
                    .setAction("UndoO", v1 -> {})
                    .show();
        });

    }
}
