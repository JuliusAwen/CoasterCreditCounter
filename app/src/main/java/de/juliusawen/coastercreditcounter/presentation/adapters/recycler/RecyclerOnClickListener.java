package de.juliusawen.coastercreditcounter.presentation.adapters.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerOnClickListener implements View.OnClickListener, View.OnLongClickListener
{
    public RecyclerView.ViewHolder viewHolder;

    private OnClickListener onClickListener;

    RecyclerOnClickListener(RecyclerView.ViewHolder viewHolder, final OnClickListener onClickListener)
    {
        this.viewHolder = viewHolder;
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View view)
    {
        this.onClickListener.onClick(view, this.viewHolder.getLayoutPosition(), this.viewHolder);
    }

    @Override
    public boolean onLongClick(View view)
    {
        this.onClickListener.onLongClick(view, this.viewHolder.getLayoutPosition());
        return true;
    }

    public interface OnClickListener
    {
        void onClick(View view, int position, RecyclerView.ViewHolder viewHolder);

        void onLongClick(View view, int position);
    }
}
