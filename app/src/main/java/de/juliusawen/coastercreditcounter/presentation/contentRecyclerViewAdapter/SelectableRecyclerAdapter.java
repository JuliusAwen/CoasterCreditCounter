package de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter;

import android.graphics.Typeface;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;

public class SelectableRecyclerAdapter extends RecyclerView.Adapter<SelectableRecyclerAdapter.ViewHolder>
{
    private List<Element> elementsToSelectFrom;
    private boolean selectMultiple;
    private Map<Element, View> selectedViewsByElement = new HashMap<>();
    private RecyclerOnClickListener.OnClickListener onClickListener = null;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView;

        ViewHolder(LinearLayout linearLayout)
        {
            super(linearLayout);

            this.textView = linearLayout.findViewById(R.id.textViewContentHolderSelectable);
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
        Log.i(Constants.LOG_TAG, String.format("SelectableRecyclerAdapter.Constructor:: instantiating RecyclerAdapter with #[%d] elements and selectMultiple[%S]...",
                elementsToSelectFrom.size(), selectMultiple));
        this.elementsToSelectFrom = elementsToSelectFrom;
        this.selectMultiple = selectMultiple;
    }

    public SelectableRecyclerAdapter(List<Element> elementsToSelectFrom, boolean selectMultiple, RecyclerOnClickListener.OnClickListener onClickListener)
    {
        Log.i(Constants.LOG_TAG, String.format("SelectableRecyclerAdapter.Constructor:: instantiating RecyclerAdapter with #[%d] elements and selectMultiple[%S]...",
                elementsToSelectFrom.size(), selectMultiple));

        this.elementsToSelectFrom = elementsToSelectFrom;
        this.selectMultiple = selectMultiple;
        this.onClickListener = onClickListener;
    }

    public void updateElements(List<Element> elements)
    {
        Log.d(Constants.LOG_TAG, String.format("SelectableRecyclerAdapter.updateElements:: updating with #[%d] elements...", elements.size()));
        this.elementsToSelectFrom = elements;
        notifyDataSetChanged();
        Log.d(Constants.LOG_TAG, String.format("SelectableRecyclerAdapter.updateElements:: updated with #[%d] elements", elements.size()));
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
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.content_holder_selectable, parent, false);
        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position)
    {
        Element element = elementsToSelectFrom.get(position);

        Log.v(Constants.LOG_TAG, String.format("SelectableRecyclerAdapter.onBindViewHolder:: binding ViewHolder %s (position[%d])", element, position));

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
                onClickItemView(view);

                if(onClickListener != null)
                {
                    onClickListener.onClick(view, viewHolder.getAdapterPosition());
                }
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                return onClickListener != null && onClickListener.onLongClick(view, viewHolder.getAdapterPosition());
            }
        });

        viewHolder.textView.setText(StringTool.getSpannableString(element.getName(), Typeface.BOLD));
        viewHolder.textView.setVisibility(View.VISIBLE);
    }

    private void onClickItemView(View view)
    {
        Element element = (Element) view.getTag();

        if(view.isSelected())
        {
            this.selectedViewsByElement.remove(element);
            Log.i(Constants.LOG_TAG, String.format("SelectableRecyclerAdapter.onClickItemView:: %s deselected", element));
        }
        else
        {
            if(this.selectedViewsByElement.get(element) != null)
            {
                this.selectedViewsByElement.get(element).setSelected(false);
            }

            if(!this.selectMultiple)
            {
                this.selectedViewsByElement.clear();
            }

            this.selectedViewsByElement.put(element, view);
            Log.i(Constants.LOG_TAG, String.format("SelectableRecyclerAdapter.onClickItemView:: %s selected", element));
        }

        view.setSelected(!view.isSelected());

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return elementsToSelectFrom.size();
    }
}