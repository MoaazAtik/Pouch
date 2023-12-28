
package com.example.sqliteapp;

        import android.content.Context;
        import android.text.Html;
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

//#5.10
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private Context context;
    private List<Note> notesList;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView note, dot, timestamp;


        public MyViewHolder(View view) {
            super(view);

            note = view.findViewById(R.id.note);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }//class MyViewHolder


    public NotesAdapter(Context context, List<Note> notesList) {
        this.context = context;
        this.notesList = notesList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return null;

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_row, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Note note = notesList.get(position);

        holder.note.setText(note.getNote());

        //displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));

        //formatting and displaying timestamp
        holder.timestamp.setText(formatDate(note.getTimestamp()));
    }


    @Override
    public int getItemCount() {
//        return 0;
        return notesList.size();
    }


    /**
     * formatting timestamp to 'MMM d' format
     * input: 2018-02-21 00:15:42
     * output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            //java.util.Date or java.sql.Date ??
            //are "fmt" and "fmt.parse" needed??

            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
        //why do we need this
    }

}//class NotesAdapter

//package com.example.sqliteapp;
//
//public class NotesAdapter {
//}
