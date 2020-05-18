package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.OLD;

import android.view.View;

public class OLD_ContentRecyclerViewOnClickListener implements View.OnClickListener, View.OnLongClickListener
{
    private final CustomOnClickListener customOnClickListener;

    public OLD_ContentRecyclerViewOnClickListener(final CustomOnClickListener customOnClickListener)
    {
        this.customOnClickListener = customOnClickListener;
    }

    @Override
    public void onClick(View view)
    {
        this.customOnClickListener.onClick(view);
    }

    @Override
    public boolean onLongClick(View view)
    {
        return this.customOnClickListener.onLongClick(view);
    }

    public interface CustomOnClickListener
    {
        void onClick(View view);

        boolean onLongClick(View view);
    }
}
