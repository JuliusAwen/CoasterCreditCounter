package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter;

import android.view.View;

public class ContentRecyclerViewOnClickListener implements View.OnClickListener, View.OnLongClickListener
{
    private final CustomItemOnClickListener customItemOnClickListener;

    public ContentRecyclerViewOnClickListener(final CustomItemOnClickListener customItemOnClickListener)
    {
        this.customItemOnClickListener = customItemOnClickListener;
    }

    @Override
    public void onClick(View view)
    {
        this.customItemOnClickListener.onClick(view);
    }

    @Override
    public boolean onLongClick(View view)
    {
        return this.customItemOnClickListener.onLongClick(view);
    }

    public interface CustomItemOnClickListener
    {
        void onClick(View view);

        boolean onLongClick(View view);
    }
}
