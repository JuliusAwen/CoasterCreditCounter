package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

class ViewHolderBottomSpacer extends RecyclerView.ViewHolder
{
    ViewHolderBottomSpacer(View view)
    {
        super(view);
        view.setClickable(false);
    }
}
