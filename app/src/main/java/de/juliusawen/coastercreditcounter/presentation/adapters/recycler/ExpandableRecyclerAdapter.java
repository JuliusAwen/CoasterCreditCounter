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

import java.util.List;
import java.util.Objects;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.Toolbox.StringTool;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.content.Park;

public class ExpandableRecyclerAdapter extends RecyclerView.Adapter<ExpandableRecyclerAdapter.ViewHolder>
{
    private List<Element> elements;
    private RecyclerClickListener.OnClickListener onClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public boolean isExpanded = false;

        LinearLayout linearLayout;
        TextView textView;
        ImageView imageViewToggleExpand;
        Context context;
        int childrenCount = 0;

        ViewHolder(LinearLayout linearLayout)
        {
            super(linearLayout);

            this.context = linearLayout.getContext();
            this.textView = linearLayout.findViewById(R.id.textViewRecyclerViewContentHolder);
            this.imageViewToggleExpand = linearLayout.findViewById(R.id.imageViewRecyclerViewContentHolder_ToggleExpand);
            this.linearLayout = linearLayout;
        }
    }

    public ExpandableRecyclerAdapter(List<Element> elements, RecyclerClickListener.OnClickListener onClickListener)
    {
        this.elements = elements;
        this.onClickListener = onClickListener;
    }

    public void updateList(List<Element> elements)
    {
        this.elements = elements;
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
        Element element = elements.get(position);

        if(viewHolder.textView.getTag() != null && !((Element)viewHolder.textView.getTag()).getUuid().equals(element.getUuid()))
        {
            this.removeChildViews(viewHolder);
        }

        RecyclerClickListener recyclerClickListener = new RecyclerClickListener(viewHolder, this.onClickListener);

        viewHolder.textView.setText(StringTool.getSpannableString(element.getName(), Typeface.BOLD));
        viewHolder.textView.setTag(element);
        viewHolder.textView.setOnClickListener(recyclerClickListener);
        viewHolder.textView.setOnLongClickListener(recyclerClickListener);

        if(element.getClass() == Location.class)
        {
            Location location = (Location) element;

            if(!location.getParks().isEmpty())
            {
                int increment = 0;

                for(Park park : location.getParks())
                {
                    this.createChildView(viewHolder, recyclerClickListener, park, increment);
                    increment ++;
                }

                this.decorateExpandableView(viewHolder, recyclerClickListener);
            }
            else
            {
                viewHolder.imageViewToggleExpand.setVisibility(View.GONE);
            }
        }
    }

    private void removeChildViews(ViewHolder viewHolder)
    {
        if(viewHolder.childrenCount > 0)
        {
            for (int i = 0; i < viewHolder.childrenCount; i++)
            {
                viewHolder.linearLayout.removeView(viewHolder.linearLayout.findViewById(Constants.VIEW_TYPE_CHILD + i));
            }

            viewHolder.isExpanded = false;
            viewHolder.childrenCount = 0;
        }
    }

    private void createChildView(ViewHolder viewHolder, RecyclerClickListener recyclerClickListener, Element element, int increment)
    {
        View childView = viewHolder.linearLayout.findViewById(Constants.VIEW_TYPE_CHILD + increment);
        if(childView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) viewHolder.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            childView = Objects.requireNonNull(layoutInflater).inflate(R.layout.recycler_view_content_holder, viewHolder.linearLayout, false);
            childView.setId(Constants.VIEW_TYPE_CHILD + increment);
            childView.setTag(element);
            childView.setOnClickListener(recyclerClickListener);

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

    private void decorateExpandableView(ViewHolder viewHolder, RecyclerClickListener recyclerClickListener)
    {
        viewHolder.imageViewToggleExpand.setOnClickListener(recyclerClickListener);
        viewHolder.imageViewToggleExpand.setId(Constants.BUTTON_TOGGLE_EXPAND);

        if(viewHolder.isExpanded)
        {
            viewHolder.imageViewToggleExpand.setImageDrawable(viewHolder.context.getDrawable(R.drawable.ic_baseline_arrow_drop_down));
        }
        else
        {
            viewHolder.imageViewToggleExpand.setImageDrawable(viewHolder.context.getDrawable(R.drawable.ic_baseline_arrow_drop_left));
        }

        viewHolder.imageViewToggleExpand.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount()
    {
        return elements.size();
    }
}