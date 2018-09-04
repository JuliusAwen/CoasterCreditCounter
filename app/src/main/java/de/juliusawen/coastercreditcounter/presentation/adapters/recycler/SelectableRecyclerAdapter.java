package de.juliusawen.coastercreditcounter.presentation.adapters.recycler;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;

public class SelectableRecyclerAdapter extends RecyclerView.Adapter<SelectableRecyclerAdapter.ViewHolder>
{
    private List<Element> elementsToSelectFrom;
    private boolean selectMultiple;
    private Map<Element, View> selectedViewsByElement = new HashMap<>();
    private RecyclerOnClickListener.OnClickListener onClickListener = null;

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

    public boolean isAllSelected()
    {
        if(this.elementsToSelectFrom != null)
        {
            List<Element> compareList = new ArrayList<>(this.elementsToSelectFrom);
            compareList.removeAll(selectedViewsByElement.keySet());
            return compareList.isEmpty();
        }

        return false;
    }

    public SelectableRecyclerAdapter(List<Element> elementsToSelectFrom, boolean selectMultiple)
    {
        Log.i(Constants.LOG_TAG, String.format("SelectableRecyclerAdapter.Constructor:: creating instance - selectMultiple[%S]...", selectMultiple));
        this.elementsToSelectFrom = elementsToSelectFrom;
        this.selectMultiple = selectMultiple;
    }

    public SelectableRecyclerAdapter(List<Element> elementsToSelectFrom, boolean selectMultiple, RecyclerOnClickListener.OnClickListener onClickListener)
    {
        Log.i(Constants.LOG_TAG, String.format("SelectableRecyclerAdapter.Constructor:: creating instance - selectMultiple[%S]...", selectMultiple));

        this.elementsToSelectFrom = elementsToSelectFrom;
        this.selectMultiple = selectMultiple;
        this.onClickListener = onClickListener;
    }

    public void updateList(List<Element> elements)
    {
        Log.v(Constants.LOG_TAG, String.format("SelectableRecyclerAdapter.updateList:: updating list with #[%d] elements...", elements.size()));

        this.elementsToSelectFrom = elements;
        notifyDataSetChanged();
    }

    public List<Element> getElementsToSelectFrom()
    {
        return this.elementsToSelectFrom;
    }

    public List<Element> getSelectedElementsInOrderOfSelection()
    {
        return new ArrayList<>(this.selectedViewsByElement.keySet());
    }

    public void selectAllElements()
    {
        Log.i(Constants.LOG_TAG, "SelectableRecyclerAdapter.selectAllElements:: selecting all elements...");

        this.selectedViewsByElement.clear();
        this.selectElements(this.elementsToSelectFrom);
        notifyDataSetChanged();
    }

    public void deselectAllElements()
    {
        Log.i(Constants.LOG_TAG, "SelectableRecyclerAdapter.deselectAllElements:: deselecting all elements...");

        this.selectedViewsByElement.clear();
        notifyDataSetChanged();
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

        Log.d(Constants.LOG_TAG, String.format("SelectableRecyclerAdapter.selectElement:: %s selected", element));
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
        Element element = elementsToSelectFrom.get(position);

        Log.d(Constants.LOG_TAG, String.format("SelectableRecyclerAdapter.onBindViewHolder:: binding ViewHolder %s (position[%d])", element, position));

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
                    Log.i(Constants.LOG_TAG, String.format("SelectableRecyclerAdapter.onBindViewHolder:: %s deselected", element));
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
                    Log.i(Constants.LOG_TAG, String.format("SelectableRecyclerAdapter.onBindViewHolder:: %s selected", element));
                }

                view.setSelected(!view.isSelected());

                if(onClickListener != null)
                {
                    onClickListener.onClick(view, viewHolder.getAdapterPosition());
                }

                notifyDataSetChanged();
            }
        });

        viewHolder.textView.setText(StringTool.getSpannableString(element.getName(), Typeface.BOLD));
    }

    @Override
    public int getItemCount()
    {
        return elementsToSelectFrom.size();
    }
}