package com.example.sqliteapp;

        import android.content.Context;
        import android.util.Log;
        import android.view.GestureDetector;
        import android.view.MotionEvent;
        import android.view.View;

        import androidx.recyclerview.widget.RecyclerView;

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

    private static final String TAG = "RecyclerTouchListener";

    private ClickListener clickListener;
    private GestureDetector gestureDetector;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener){
        this.clickListener = clickListener;

        gestureDetector = new GestureDetector(
                context,
                new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null)
                    clickListener.onClick(childView, recyclerView.getChildAdapterPosition(childView));
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null)
                    clickListener.onLongClick(childView, recyclerView.getChildAdapterPosition(childView));
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }


    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

}
