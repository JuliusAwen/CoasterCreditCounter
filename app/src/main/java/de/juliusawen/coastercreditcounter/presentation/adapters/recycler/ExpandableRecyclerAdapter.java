package de.juliusawen.coastercreditcounter.presentation.adapters.recycler;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.Toolbox.StringTool;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.content.Park;

public class ExpandableRecyclerAdapter extends RecyclerView.Adapter<ExpandableRecyclerAdapter.ViewHolder>
{
    private static Set<Element> expandedElements = new HashSet<>();

    private List<Element> elements;
    private RecyclerOnClickListener.OnClickListener onClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout linearLayout;
        TextView textView;
        ImageView imageViewExpandToggle;
        int childrenCount = 0;

        private boolean isExpanded = false;

        ViewHolder(LinearLayout linearLayout)
        {
            super(linearLayout);

            this.linearLayout = linearLayout;
            this.textView = linearLayout.findViewById(R.id.textViewRecyclerViewContentHolder);
            this.imageViewExpandToggle = linearLayout.findViewById(R.id.imageViewRecyclerViewContentHolder_ExpandToggle);
        }
    }

    public ExpandableRecyclerAdapter(List<Element> elements, RecyclerOnClickListener.OnClickListener onClickListener)
    {
        this.elements = elements;
        this.onClickListener = onClickListener;
    }

    public void updateList(List<Element> elements)
    {
        this.elements = elements;

        Set<Element> orphanedElements = new HashSet<>(expandedElements);
        orphanedElements.removeAll(elements);
        expandedElements.removeAll(orphanedElements);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpandableRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_content_holder, parent, false);
        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position)
    {
        final Element element = elements.get(position);
        RecyclerOnClickListener recyclerOnClickListener = new RecyclerOnClickListener(viewHolder, this.onClickListener);

        this.handleParentView(viewHolder, element, recyclerOnClickListener);

        if(element.getClass() == Location.class)
        {
            this.handleChildViewsForLocation(viewHolder, (Location)element, recyclerOnClickListener);
        }
    }

    private void handleParentView(ViewHolder viewHolder, Element element, RecyclerOnClickListener recyclerOnClickListener)
    {
        this.removeChildViewsIfViewHolderIsRecycled(viewHolder, element);

        viewHolder.textView.setText(StringTool.getSpannableString(element.getName(), Typeface.BOLD));
        viewHolder.textView.setTag(element);
        viewHolder.textView.setOnClickListener(recyclerOnClickListener);
        viewHolder.textView.setOnLongClickListener(recyclerOnClickListener);
    }

    private void removeChildViewsIfViewHolderIsRecycled(ViewHolder viewHolder, Element element)
    {
        if(viewHolder.textView.getTag() != null && !((Element)viewHolder.textView.getTag()).getUuid().equals(element.getUuid()))
        {
            if(viewHolder.childrenCount > 0)
            {
                for (int i = 0; i < viewHolder.childrenCount; i++)
                {
                    viewHolder.linearLayout.removeView(viewHolder.linearLayout.findViewById(Constants.VIEW_TYPE_CHILD + i));
                }

                viewHolder.childrenCount = 0;
            }
        }
    }

    private void handleChildViewsForLocation(final ViewHolder viewHolder, final Location location, RecyclerOnClickListener recyclerOnClickListener)
    {
        if(!location.getParks().isEmpty())
        {
            this.handleExpandToggle(viewHolder, location);

            int increment = 0;
            for(Park park : location.getParks())
            {
                this.handleChildView(viewHolder, park, increment, recyclerOnClickListener);

                increment ++;
            }
        }
        else
        {
            viewHolder.imageViewExpandToggle.setVisibility(View.GONE);
        }
    }

    private void handleExpandToggle(final ViewHolder viewHolder, final Element element)
    {
        if(expandedElements.contains(element))
        {
            viewHolder.isExpanded = true;
            viewHolder.imageViewExpandToggle.setImageDrawable(viewHolder.linearLayout.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_down));
        }
        else
        {
            viewHolder.isExpanded = false;
            viewHolder.imageViewExpandToggle.setImageDrawable(viewHolder.linearLayout.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_left));
        }

        if(!viewHolder.imageViewExpandToggle.hasOnClickListeners())
        {
            viewHolder.imageViewExpandToggle.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View view)
                {
                    if(viewHolder.isExpanded)
                    {
                        expandedElements.remove(element);
                        notifyDataSetChanged();
                    }
                    else
                    {
                        expandedElements.add(element);
                        notifyDataSetChanged();
                    }
                }
            });
        }

        viewHolder.imageViewExpandToggle.setVisibility(View.VISIBLE);
    }

    private void handleChildView(ViewHolder viewHolder, Element element, int increment, RecyclerOnClickListener recyclerOnClickListener)
    {
        View childView = viewHolder.linearLayout.findViewById(Constants.VIEW_TYPE_CHILD + increment);
        if(childView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) viewHolder.linearLayout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            childView = Objects.requireNonNull(layoutInflater).inflate(R.layout.recycler_view_content_holder, viewHolder.linearLayout, false);
            childView.setId(Constants.VIEW_TYPE_CHILD + increment);
            childView.setTag(element);
            childView.setOnClickListener(recyclerOnClickListener);

            TextView textView = childView.findViewById(R.id.textViewRecyclerViewContentHolder);
            textView.setText(element.getName());

            viewHolder.linearLayout.addView(childView);
            viewHolder.childrenCount ++;
        }

        if(viewHolder.isExpanded)
        {
            childView.setVisibility(View.VISIBLE);
        }
        else
        {
            childView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount()
    {
        return elements.size();
    }
}