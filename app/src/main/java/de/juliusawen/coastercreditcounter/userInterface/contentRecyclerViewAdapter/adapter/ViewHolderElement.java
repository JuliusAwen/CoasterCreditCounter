package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.juliusawen.coastercreditcounter.R;

class ViewHolderElement extends RecyclerView.ViewHolder
{
    final LinearLayout linearLayout;
    final TextView textViewDetailAbove;
    final TextView textViewName;
    final TextView textViewDetailBelow;
    final ImageView imageViewExpandToggle;

    final TextView textViewPrettyPrint;


    ViewHolderElement(View view)
    {
        super(view);
        this.linearLayout = view.findViewById(R.id.linearLayoutRecyclerView);
        this.textViewDetailAbove = view.findViewById(R.id.textViewRecyclerView_DetailAbove);
        this.textViewName = view.findViewById(R.id.textViewRecyclerView_Name);
        this.textViewDetailBelow = view.findViewById(R.id.textViewRecyclerView_DetailBelow);
        this.imageViewExpandToggle = view.findViewById(R.id.imageViewRecyclerView);

        this.textViewPrettyPrint = view.findViewById(R.id.textViewRecyclerViewItem_PrettyPrint);
    }
}
