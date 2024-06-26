package com.thewhitewings.pouch;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> implements Filterable {

    private static final String TAG = "NotesAdapter";

    private List<Note> notesList;
    public List<Note> notesListFull;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtNoteBodyRv, txtNoteTitleRv, txtTimestamp;

        public MyViewHolder(View view) {
            super(view);

            txtNoteBodyRv = view.findViewById(R.id.txt_note_body_rv);
            txtNoteTitleRv = view.findViewById(R.id.txt_note_title_rv);
            txtTimestamp = view.findViewById(R.id.txt_timestamp_rv);
        }
    }//class MyViewHolder


    public NotesAdapter(List<Note> notesList) {
        this.notesList = notesList;
        notesListFull = new ArrayList<>(notesList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_rv_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note note = notesList.get(position);

        holder.txtNoteTitleRv.setText(note.getNoteTitle());
        holder.txtNoteBodyRv.setText(note.getNoteBody());
        //formatting and displaying timestamp
        holder.txtTimestamp.setText(formatDate(note.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    @Override
    public Filter getFilter() {
        return notesFilter;
    }


    private Filter notesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Note> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(notesListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Note note : notesListFull) {
                    String noteTitle = note.getNoteTitle();
                    String noteBody = note.getNoteBody();
                    boolean matchTitle = false;
                    boolean matchBody;

                    if (noteTitle != null) {
                        matchTitle = noteTitle.toLowerCase().contains(filterPattern);
                    }
                    if (matchTitle) {
                        filteredList.add(note);
                    } else if (noteBody != null) {
                        matchBody = noteBody.toLowerCase().contains(filterPattern);
                        if (matchBody) {
                            filteredList.add(note);
                        }
                    }
                }
            }

            FilterResults results;
            results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notesList.clear();
            notesList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    /**
     * Reflect changes in {@link #notesList} to {@link #notesListFull} for {@link #notesFilter}.<p>
     * Note: Sorting notesList is reflected by sortNotes() in {@link MainActivity} and {@link BoxOfMysteriesVM}.
     *
     * @param note     pass null for action delete.
     * @param position of note in notesList. Pass 0 for action create.
     * @param action   Wanted action to handle the note: {@link Constants#ACTION_CLOSE_ONLY}, {@link Constants#ACTION_CREATE}, {@link Constants#ACTION_UPDATE}, or {@link Constants#ACTION_DELETE}.
     */
    public void editNotesListFull(Note note, int position, int action) {
        /*
        With 'notesListFull.add(0, note)' instead of 'notesListFull.add(0, new Note(note.getId(),...' in 'case Constants.ACTION_CREATE', notesList and notesListFull refer to the same note in the RAM.
        So, there is no need for 'case Constants.ACTION_UPDATE' because note is automatically updated in notesListFull when it's updated in notesList.
         */
        switch (action) {
            case Constants.ACTION_CREATE:
                notesListFull.add(position, note);
                break;
            case Constants.ACTION_DELETE:
                notesListFull.remove(position);
                break;
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
