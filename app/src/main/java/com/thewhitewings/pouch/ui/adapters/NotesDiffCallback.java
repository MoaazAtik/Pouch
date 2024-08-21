package com.thewhitewings.pouch.ui.adapters;

import android.util.Log;

import androidx.recyclerview.widget.DiffUtil;

import com.thewhitewings.pouch.data.Note;

import java.util.List;

public class NotesDiffCallback extends DiffUtil.Callback {

    private static final String TAG = "NotesDiffCallback";
    private final List<Note> oldList;
    private final List<Note> newList;

    public NotesDiffCallback(List<Note> oldList, List<Note> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equalContent(newList.get(newItemPosition));
    }
}

