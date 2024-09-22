package com.thewhitewings.pouch.ui.adapters;

import androidx.recyclerview.widget.DiffUtil;

import com.thewhitewings.pouch.data.Note;

import java.util.List;

/**
 * Callback class for calculating the diff between two lists of Note objects.
 * It is used to efficiently update the RecyclerView items when the data changes.
 */
public class NotesDiffCallback extends DiffUtil.Callback {

    private static final String TAG = "NotesDiffCallback";
    private final List<Note> oldList;
    private final List<Note> newList;

    /**
     * Constructor for NotesDiffCallback
     *
     * @param oldList the old list of notes
     * @param newList the new list of notes
     */
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
