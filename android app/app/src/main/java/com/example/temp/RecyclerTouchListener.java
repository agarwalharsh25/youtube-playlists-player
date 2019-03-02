package com.example.temp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

    //GestureDetector to intercept touch events
    GestureDetector gestureDetector;
    private HomeFragment.RecyclerViewItemClickListener clickListenerHomeFragment;
    private VideoPlayActivity.RecyclerViewItemClickListener clickListenerVideoPlayActivity;
    private VideoPlaylistActivity.RecyclerViewItemClickListener clickListenerVideoPlaylistActivity;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final HomeFragment.RecyclerViewItemClickListener clickListener) {
        this.clickListenerHomeFragment = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });
    }
    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final VideoPlayActivity.RecyclerViewItemClickListener clickListener) {
        this.clickListenerVideoPlayActivity = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });
    }
    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final VideoPlaylistActivity.RecyclerViewItemClickListener clickListener) {
        this.clickListenerVideoPlaylistActivity = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {

        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child != null && clickListenerHomeFragment != null && gestureDetector.onTouchEvent(e)) {
            clickListenerHomeFragment.onClick(child, recyclerView.getChildLayoutPosition(child));
        } else if (child != null && clickListenerVideoPlayActivity != null && gestureDetector.onTouchEvent(e)) {
            clickListenerVideoPlayActivity.onClick(child, recyclerView.getChildLayoutPosition(child));
        } else if (child != null && clickListenerVideoPlaylistActivity != null && gestureDetector.onTouchEvent(e)) {
            clickListenerVideoPlaylistActivity.onClick(child, recyclerView.getChildLayoutPosition(child));
        }
        return false;

    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

}
