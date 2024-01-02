package com.example.sqliteapp;

        import android.content.Context;
        import android.text.Html;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Date;
        import java.util.List;
        import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private static final String TAG = "NotesAdapter";

    private Context context;
    private List<Note> notesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView note, dot, timestamp;

        public MyViewHolder(View view) {
            super(view);

            note = view.findViewById(R.id.note);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
//            Log.d(TAG, "MyViewHolder: ");
        }
    }//class MyViewHolder


    public NotesAdapter(Context context, List<Note> notesList) {
//        this.context = context;
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_row, parent, false);
//        context = parent.getContext(); // maybe I can get the context here without the necessity of passing it while creating the Adapter instance
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note note = notesList.get(position);

        holder.note.setText(note.getNoteBody());
        //displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));
        //formatting and displaying timestamp
        holder.timestamp.setText(formatDate(note.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return notesList.size();
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
