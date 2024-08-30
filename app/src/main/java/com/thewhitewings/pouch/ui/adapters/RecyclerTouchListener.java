package com.thewhitewings.pouch.ui.adapters;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This class is used by the Recycle View to handle item interactions.
 * It listens to item interactions and notifies the caller of the touch event.
 * <p>
 * It includes:
 * </p>
 * <ul>
 *     <li>1. Callback of ItemTouchHelper to handle item Swipes.</li>
 *     <li>2. OnItemTouchListener with GestureDetector to handle item Clicks.</li>
 * </ul>
 */
public class RecyclerTouchListener extends ItemTouchHelper.SimpleCallback implements RecyclerView.OnItemTouchListener {

    private static final String TAG = "RecyclerTouchListener";

    private final TouchListener touchListener;
    private final GestureDetector gestureDetector;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final TouchListener touchListener) {
        super(0, ItemTouchHelper.START);
        this.touchListener = touchListener;

        gestureDetector = new GestureDetector(
                context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(@NonNull MotionEvent e) {
                        View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                        if (childView != null)
                            // Notify the touch listener of the click event
                            touchListener.onClick(recyclerView.getChildAdapterPosition(childView));
                        return true;
                    }
                }
        );
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        // Pass the motion event to the gesture detector to handle clicks
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
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
        // Notify the touch listener of the swipe event
        touchListener.onSwiped(viewHolder.getAdapterPosition());
    }


    /**
     * This interface is used to notify the caller of the touch event, i.e., the activity,
     * to handle Click and Swipe events of the Recycle View items.
     */
    public interface TouchListener {

        /**
         * Called when an item is clicked.
         *
         * @param position of the item that was clicked
         */
        void onClick(int position);

        /**
         * Called when an item is swiped.
         *
         * @param position of the item that was swiped
         */
        void onSwiped(int position);
    }

}
