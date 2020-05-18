package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;

class ViewHolderVisitedAttraction extends RecyclerView.ViewHolder
{
    final LinearLayout linearLayoutEditable;
    final LinearLayout linearLayoutCounter;
    final TextView textViewName;
    final TextView textViewCount;
    final ImageView imageViewDecrease;
    final ImageView imageViewIncrease;
    final TextView textViewPrettyPrint;

    ViewHolderVisitedAttraction(View view)
    {
        super(view);

        this.linearLayoutEditable = view.findViewById(R.id.linearLayoutRecyclerViewVisitedAttraction_OpenForEditing);

        this.linearLayoutCounter = view.findViewById(R.id.linearLayoutRecyclerViewVisitedAttraction_Counter);

        this.textViewName = view.findViewById(R.id.textViewRecyclerViewItemVisitedAttraction_Name);
        this.textViewCount = view.findViewById(R.id.textViewRecyclerViewItemVisitedAttraction_Count);

        this.imageViewIncrease = view.findViewById(R.id.imageViewRecyclerViewItemVisitedAttraction_Increase);
        this.imageViewIncrease.setImageDrawable(App.getContext().getDrawable(R.drawable.add_circle_outline));

        this.imageViewDecrease = view.findViewById(R.id.imageViewRecyclerViewItemVisitedAttraction_Decrease);
        this.imageViewDecrease.setImageDrawable(App.getContext().getDrawable(R.drawable.remove_circle_outline));

        this.textViewPrettyPrint = view.findViewById(R.id.textViewRecyclerViewItemVisitedAttraction_PrettyPrint);
    }
}