package de.juliusawen.coastercreditcounter.presentation.adapters.recycler;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.StringTool;
import de.juliusawen.coastercreditcounter.content.Element;

public class SelectableRecyclerAdapter extends RecyclerView.Adapter<SelectableRecyclerAdapter.ViewHolder>
{
    private List<Element> elements;

    private boolean selectMultiple;
    private Map<Element, View> selectedViewsByElement = new HashMap<>();

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout linearLayout;
        TextView textView;

        ViewHolder(LinearLayout linearLayout)
        {
            super(linearLayout);

            this.textView = linearLayout.findViewById(R.id.textViewRecyclerViewContentHolder);
            this.linearLayout = linearLayout;
        }
    }

    public SelectableRecyclerAdapter(List<Element> elements, boolean selectMultiple)
    {
        this.elements = elements;
        this.selectMultiple = selectMultiple;
    }

    public void updateList(List<Element> elements)
    {
        this.elements = elements;
        notifyDataSetChanged();
    }

    public List<Element> getSelectedElements()
    {
        return new ArrayList<>(this.selectedViewsByElement.keySet());
    }

    public void selectElements(List<Element> elements)
    {
        for(Element element : elements)
        {
            this.selectElement(element);
        }
    }

    public void selectElement(Element element)
    {
        this.selectedViewsByElement.put(element, null);
    }

    @NonNull
    @Override
    public SelectableRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_content_holder, parent, false);
        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position)
    {
        Element element = elements.get(position);
        if(this.selectedViewsByElement.containsKey(element))
        {
            viewHolder.itemView.setSelected(true);
            this.selectedViewsByElement.put(element, viewHolder.itemView);
        }
        else
        {
            viewHolder.itemView.setSelected(false);
        }

        viewHolder.itemView.setTag(element);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element) view.getTag();

                if(view.isSelected())
                {
                    selectedViewsByElement.remove(element);
                }
                else
                {
                    if(selectedViewsByElement.get(element) != null)
                    {
                        selectedViewsByElement.get(element).setSelected(false);
                    }

                    if(!selectMultiple)
                    {
                        selectedViewsByElement.clear();
                    }

                    selectedViewsByElement.put(element, view);
                }

                view.setSelected(!view.isSelected());

                notifyDataSetChanged();
            }
        });

        viewHolder.textView.setText(StringTool.getSpannableString(element.getName(), Typeface.BOLD));
    }

    @Override
    public int getItemCount()
    {
        return elements.size();
    }
}