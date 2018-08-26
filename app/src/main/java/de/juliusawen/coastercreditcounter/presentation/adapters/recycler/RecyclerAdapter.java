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
import de.juliusawen.coastercreditcounter.Toolbox.StringTool;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.content.Park;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
{
    public Element selectedElement;
    public View selectedView = null;

    private List<Element> elements;
    private boolean isExpandable;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout linearLayout;
        TextView textView;
        ImageView imageViewDropDown;
        Context context;

        boolean isExpanded = false;

        private ViewHolder(LinearLayout linearLayout)
        {
            super(linearLayout);

            this.context = linearLayout.getContext();
            this.textView = linearLayout.findViewById(R.id.textViewRecyclerViewContentHolder);
            this.imageViewDropDown = linearLayout.findViewById(R.id.imageViewRecyclerViewContentHolder);
            this.linearLayout = linearLayout;
        }
    }

    public RecyclerAdapter(List<Element> elements, boolean isExpandable)
    {
        this.isExpandable = isExpandable;
        this.elements = elements;
    }

    public void updateList(List<Element> elements)
    {
        this.elements = elements;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_content_holder, parent, false);

        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position)
    {
        Element element = elements.get(position);

        viewHolder.textView.setText(StringTool.getSpannableString(element.getName(), Typeface.BOLD));
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

        if(this.isExpandable && element.getClass() == Location.class)
        {
            Location location = (Location) element;

            if(!location.getParks().isEmpty())
            {
                viewHolder.imageViewDropDown.setVisibility(View.VISIBLE);


                if(viewHolder.isExpanded)
                {
                    viewHolder.imageViewDropDown.setImageDrawable(viewHolder.context.getDrawable(R.drawable.ic_baseline_arrow_drop_left));
                }
                else
                {
                    viewHolder.imageViewDropDown.setImageDrawable(viewHolder.context.getDrawable(R.drawable.ic_baseline_arrow_drop_down));
                }

                for(Park park : location.getParks())
                {
                    LayoutInflater layoutInflater = (LayoutInflater) viewHolder.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View childView = Objects.requireNonNull(layoutInflater).inflate(R.layout.recycler_view_content_holder, viewHolder.linearLayout, false);
                    TextView textView = childView.findViewById(R.id.textViewRecyclerViewContentHolder);
                    textView.setText(park.getName());

                    if(viewHolder.isExpanded)
                    {
                        childView.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        childView.setVisibility(View.GONE);
                    }

                    viewHolder.linearLayout.addView(childView);
                }
            }
            else
            {
                viewHolder.imageViewDropDown.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return elements.size();
    }

    public interface RecyclerAdapterOnClickListener
    {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
}
