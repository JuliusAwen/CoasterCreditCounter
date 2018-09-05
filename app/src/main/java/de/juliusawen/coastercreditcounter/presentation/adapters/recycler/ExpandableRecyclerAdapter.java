package de.juliusawen.coastercreditcounter.presentation.adapters.recycler;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Park;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;

public class ExpandableRecyclerAdapter extends RecyclerView.Adapter<ExpandableRecyclerAdapter.ViewHolder>
{
    private static Set<Element> elementsToExpand = new HashSet<>();
    private List<Element> elements;
    private RecyclerOnClickListener.OnClickListener onClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        private LinearLayout linearLayout;
        private TextView textView;
        private ImageView imageViewExpandToggle;

        private int childCount = 0;
        private boolean isExpanded = false;

        ViewHolder(LinearLayout linearLayout)
        {
            super(linearLayout);

            this.linearLayout = linearLayout;
            this.textView = linearLayout.findViewById(R.id.textViewShowLocationsContentHolder_Parent);
            this.imageViewExpandToggle = linearLayout.findViewById(R.id.imageViewShowLocationsContentHolder_ExpandToggle);
        }
    }

    public ExpandableRecyclerAdapter(List<Element> elements, RecyclerOnClickListener.OnClickListener onClickListener)
    {
        Log.i(Constants.LOG_TAG, "ExpandableRecyclerAdapter.Constructor:: creating instance...");

        this.elements = elements;
        this.onClickListener = onClickListener;
    }

    public void updateList(List<Element> elements)
    {
        Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.updateList:: updating list with #[%d] elements...", elements.size()));

        this.elements = elements;

        Set<Element> orphanedElements = new HashSet<>(elementsToExpand);
        orphanedElements.removeAll(elements);

        if(!orphanedElements.isEmpty())
        {
            elementsToExpand.removeAll(orphanedElements);

            Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.updateList:: #[%d] orphaned elements removed", orphanedElements.size()));
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpandableRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.show_locations_content_holder, parent, false);
        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position)
    {
        final Element element = elements.get(position);
        Log.d(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.onBindViewHolder:: binding ViewHolder %s (position[%d])", element, position));

        RecyclerOnClickListener recyclerOnClickListener = new RecyclerOnClickListener(viewHolder, this.onClickListener);

        this.removeChildViews(viewHolder);

        viewHolder.textView.setText(StringTool.getSpannableString(element.getName(), Typeface.BOLD));
        viewHolder.textView.setTag(element);
        viewHolder.textView.setOnClickListener(recyclerOnClickListener);
        viewHolder.textView.setOnLongClickListener(recyclerOnClickListener);
        viewHolder.textView.setVisibility(View.VISIBLE);

        this.handleExpandToggle(viewHolder, element);

        if(element.hasChildrenOfInstance(Park.class))
        {
            Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.onBindViewHolder:: %s has #[%d] child parks", element, element.getChildCountOfInstance(Park.class)));
            this.addChildViews(viewHolder, element.getChildrenOfInstance(Park.class), recyclerOnClickListener);

            viewHolder.imageViewExpandToggle.setVisibility(View.VISIBLE);
            Log.d(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.onBindViewHolder:: ExpandToggle for %s is <VISIBLE>", element));
        }
        else
        {
            viewHolder.imageViewExpandToggle.setVisibility(View.INVISIBLE);
            Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.onBindViewHolder:: ExpandToggle for %s is <INVISIBLE>", element));
        }
    }

    private void handleExpandToggle(final ViewHolder viewHolder, final Element element)
    {
        if(elementsToExpand.contains(element))
        {
            viewHolder.isExpanded = true;
            viewHolder.imageViewExpandToggle.setImageDrawable(viewHolder.linearLayout.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_down));

            Log.d(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.handleExpandToggle:: ViewHolder %s is <EXPANDED>", element));
        }
        else
        {
            viewHolder.isExpanded = false;
            viewHolder.imageViewExpandToggle.setImageDrawable(viewHolder.linearLayout.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_right));

            Log.d(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.handleExpandToggle:: ViewHolder %s is <COLLAPSED>", element));
        }

        viewHolder.imageViewExpandToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!viewHolder.isExpanded)
                {

                    elementsToExpand.add(element);
                    Log.i(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.handleExpandToggle.OnClick:: expanded %s", element));

                    notifyDataSetChanged();
                }
                else
                {
                    elementsToExpand.remove(element);
                    Log.i(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.handleExpandToggle.OnClick:: collapsed %s", element));

                    notifyDataSetChanged();
                }
            }
        });

    }

    private void removeChildViews(ViewHolder viewHolder)
    {
        if(viewHolder.childCount > 0)
        {
            for (int i = 0; i < viewHolder.childCount; i++)
            {
                viewHolder.linearLayout.removeView(viewHolder.linearLayout.findViewById(Constants.VIEW_TYPE_CHILD + i));
            }

            Log.d(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.removeChildViews:: #[%d] ChildViews removed", viewHolder.childCount));

            viewHolder.childCount = 0;
        }
    }

    private void addChildViews(final ViewHolder viewHolder, List<Element> elements, RecyclerOnClickListener recyclerOnClickListener)
    {
        int increment = 0;
        for(Element element : elements)
        {
            View childView = this.createChildView(viewHolder, element, increment, recyclerOnClickListener);

            if(viewHolder.isExpanded)
            {
                childView.setVisibility(View.VISIBLE);

                Log.d(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.addChildViews:: View %s is <VISIBLE>", element));
            }
            else
            {
                childView.setVisibility(View.GONE);

                Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.addChildViews:: View %s is <GONE>", element));
            }

            increment ++;
        }

        Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.addChildViews:: #[%d] child views added.", increment));
    }

    private View createChildView(ViewHolder viewHolder, Element element, int increment, RecyclerOnClickListener recyclerOnClickListener)
    {
        View childView = viewHolder.linearLayout.findViewById(Constants.VIEW_TYPE_CHILD + increment);

        if(childView == null)
        {
            Log.d(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.createChildView:: creating View %s...", element));

            LayoutInflater layoutInflater = (LayoutInflater) viewHolder.linearLayout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            childView = Objects.requireNonNull(layoutInflater).inflate(R.layout.show_locations_content_holder, viewHolder.linearLayout, false);
            childView.findViewById(R.id.linearLayoutShowLocationsContentHolder).getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
            childView.setId(Constants.VIEW_TYPE_CHILD + increment);
            childView.setTag(element);
            childView.setOnClickListener(recyclerOnClickListener);
            childView.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;

            TextView textView = childView.findViewById(R.id.textViewShowLocationsContentHolder_Child);
            textView.setText(element.getName());
            textView.setVisibility(View.VISIBLE);

            viewHolder.linearLayout.addView(childView);
            viewHolder.childCount++;
        }

        return childView;
    }

    @Override
    public int getItemCount()
    {
        return elements.size();
    }
}