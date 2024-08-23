package com.thewhitewings.pouch.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.thewhitewings.pouch.data.Note;
import com.thewhitewings.pouch.databinding.NoteRvItemBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private static final String TAG = "NotesAdapter";
    private final List<Note> notesList = new ArrayList<>();

    public NotesAdapter() {}

    public void setNotes(List<Note> newNotesList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new NotesDiffCallback(notesList, newNotesList));
        notesList.clear();
        notesList.addAll(newNotesList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NoteRvItemBinding binding = NoteRvItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Note note = notesList.get(position);
        holder.bind(note);
    }

    @Override
    public int getItemCount() {
        return (notesList != null) ? notesList.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final NoteRvItemBinding binding;

        public MyViewHolder(NoteRvItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Note note) {
            binding.txtNoteTitleRv.setText(note.getNoteTitle());
            binding.txtNoteBodyRv.setText(note.getNoteBody());
            binding.txtTimestampRv.setText(formatDate(note.getTimestamp()));
        }
    }

    /**
     * Format timestamp to 'MMM d' format
     * input: 2018-02-21 00:15:42 "yyyy-MM-dd HH:mm:ss"
     * output: Feb 21 "MMM d"
     *
     * @param dateString provided date as String
     * @return date as string after formatting
     */
    private String formatDate(String dateString) {
        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdFormat.parse(dateString);

            sdFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
            return sdFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "formatDate: catch e ", e);
            return "";
        }
    }
}
