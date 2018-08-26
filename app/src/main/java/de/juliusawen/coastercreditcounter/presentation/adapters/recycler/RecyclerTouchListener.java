package de.juliusawen.coastercreditcounter.presentation.adapters.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener
{
    private GestureDetector gestureDetector;
    private OnItemClickListener onItemClickListener;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent)
            {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent)
            {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (child != null && onItemClickListener != null)
                {
                    onItemClickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent)
    {
        View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (child != null && onItemClickListener != null && gestureDetector.onTouchEvent(motionEvent))
        {
            onItemClickListener.onClick(child, recyclerView.getChildAdapterPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e)
    {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
    {

    }

    public interface OnItemClickListener
    {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
}
