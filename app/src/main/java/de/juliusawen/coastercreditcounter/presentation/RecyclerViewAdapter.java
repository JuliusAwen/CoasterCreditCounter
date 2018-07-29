package de.juliusawen.coastercreditcounter.presentation;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Element;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
{
    private List<Element> elements;

    public Element selectedElement;
    public View selectedView = null;

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public Button button;
        private ViewHolder(Button button)
        {
            super(button);
            this.button = button;
        }
    }

    public RecyclerViewAdapter(List<Element> elements)
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
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Button button = (Button) LayoutInflater.from(parent.getContext()).inflate(R.layout.button_content, parent, false);
        ViewHolder viewHolder = new ViewHolder(button);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position)
    {
        Element element = elements.get(position);

        viewHolder.button.setText(element.getName());
        viewHolder.button.setTag(element);

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
