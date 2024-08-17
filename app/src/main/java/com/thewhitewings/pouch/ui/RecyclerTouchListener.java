package com.thewhitewings.pouch.ui;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This class is used by the Recycle View to handle item interactions.
 * It includes: 1. Callback of ItemTouchHelper to handle item Swipes. <p>
 * 2. OnItemTouchListener with GestureDetector to handle item Clicks.
 */
public class RecyclerTouchListener extends ItemTouchHelper.SimpleCallback implements RecyclerView.OnItemTouchListener {

    private static final String TAG = "RecyclerTouchListener";

    private TouchListener touchListener;
    private GestureDetector gestureDetector;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final TouchListener touchListener) {
        super(0, ItemTouchHelper.START);
        this.touchListener = touchListener;

        gestureDetector = new GestureDetector(
                context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                        if (childView != null)
                            touchListener.onClick(childView, recyclerView.getChildAdapterPosition(childView));
                        return true;
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
        touchListener.onSwiped(viewHolder.getAdapterPosition());
    }


    public interface TouchListener {
        void onClick(View view, int position);

        void onSwiped(int position);
    }

}
