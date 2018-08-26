package de.juliusawen.coastercreditcounter.presentation.adapters.recycler.baseRecycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Element;

public class BaseRecyclerAdapter extends RecyclerView.Adapter<BaseRecyclerAdapter.ViewHolder>
{
    public Element selectedElement;
    public View selectedView = null;

    private List<Element> elements;

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public LinearLayout linearLayout;
        public TextView textView;

        private ViewHolder(LinearLayout linearLayout)
        {
            super(linearLayout);
            this.textView = linearLayout.findViewById(R.id.textViewContentHolderLocation);
            this.linearLayout = linearLayout;
        }
    }

    public BaseRecyclerAdapter(List<Element> elements)
    {
        this.elements = elements;
    }

    public void updateList(List<Element> elements)
    {
        this.elements = elements;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.content_holder_location, parent, false);

        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position)
    {
        Element element = elements.get(position);

        viewHolder.textView.setText(element.getName());
        viewHolder.linearLayout.setTag(element);

        if(element.equals(this.selectedElement))
        {
            viewHolder.itemView.setSelected(true);
            this.selectedView = viewHolder.itemView;
        }
        else
        {
            viewHolder.itemView.setSelected(false);
        }
    }

    @Override
    public int getItemCount()
    {
        return elements.size();
    }
}
