package com.thewhitewings.pouch.ui.adapters;

import static com.thewhitewings.pouch.utils.DateTimeUtils.getFormattedDateTime;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.thewhitewings.pouch.data.Note;
import com.thewhitewings.pouch.databinding.NoteRvItemBinding;
import com.thewhitewings.pouch.utils.DateTimeFormatType;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for Notes RecyclerView
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private static final String TAG = "NotesAdapter";
    private final List<Note> notesList = new ArrayList<>();

    public NotesAdapter() {
    }

    /**
     * Sets the notes list and updates the adapter with the new list of notes using DiffUtil
     *
     * @param newNotesList the new list of notes
     */
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

    /**
     * ViewHolder class for Notes RecyclerView
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final NoteRvItemBinding binding;

        public MyViewHolder(NoteRvItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Binds the note data to the view holder
         *
         * @param note the note to bind
         */
        public void bind(Note note) {
            binding.txtNoteTitleRv.setText(note.getNoteTitle());
            binding.txtNoteBodyRv.setText(note.getNoteBody());
            binding.txtTimestampRv.setText(getFormattedDateTime(DateTimeFormatType.LOCAL_TO_LOCAL_SHORT_LENGTH_FORMAT, (note.getTimestamp())));
        }
    }

}
