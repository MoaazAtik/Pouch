package com.example.sqliteapp;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerTouchListener extends ItemTouchHelper.SimpleCallback implements RecyclerView.OnItemTouchListener {

    private static final String TAG = "RecyclerTouchListener";

    private ClickListener clickListener;
    private GestureDetector gestureDetector;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
        super(0, ItemTouchHelper.START);
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
                    public void onLongPress(MotionEvent e) { //todo remove
                        View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                        if (childView != null)
                            clickListener.onLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                    }
                }
        );
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

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        Log.d(TAG, "onSwiped: ");
        clickListener.onSwiped(viewHolder.getAdapterPosition());
    }


    public interface ClickListener {
        void onClick(View view, int position);
        void onSwiped(int position);

        void onLongClick(View view, int position); //todo delete
    }

}
