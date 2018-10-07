package de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter;

import android.view.View;

public class RecyclerOnClickListener implements View.OnClickListener, View.OnLongClickListener
{
    private OnClickListener onClickListener;

    RecyclerOnClickListener(final OnClickListener onClickListener)
    {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View view)
    {
        this.onClickListener.onClick(view);
    }

    @Override
    public boolean onLongClick(View view)
    {
        return this.onClickListener.onLongClick(view);
    }

    public interface OnClickListener
    {
        void onClick(View view);

        boolean onLongClick(View view);
    }
}
