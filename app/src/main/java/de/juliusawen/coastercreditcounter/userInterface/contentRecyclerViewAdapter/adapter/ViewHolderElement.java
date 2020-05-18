package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.juliusawen.coastercreditcounter.R;

class ViewHolderElement extends RecyclerView.ViewHolder
{
    final LinearLayout linearLayoutItem;
    final ImageView imageViewExpandToggle;
    final TextView textViewDetailAbove;
    final TextView textViewName;
    final TextView textViewDetailBelow;
    final TextView textViewPrettyPrint;


    ViewHolderElement(View view)
    {
        super(view);
        this.linearLayoutItem = view.findViewById(R.id.linearLayoutRecyclerViewItem);
        this.textViewDetailAbove = view.findViewById(R.id.textViewRecyclerViewItem_DetailAbove);
        this.textViewName = view.findViewById(R.id.textViewRecyclerViewItem_Name);
        this.textViewDetailBelow = view.findViewById(R.id.textViewRecyclerViewItem_DetailBelow);
        this.textViewPrettyPrint = view.findViewById(R.id.textViewRecyclerViewItem_PrettyPrint);

        this.imageViewExpandToggle = view.findViewById(R.id.imageViewRecyclerViewItem);
    }
}
