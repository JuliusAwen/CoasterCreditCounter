package de.juliusawen.coastercreditcounter.presentation.recycler;

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
import de.juliusawen.coastercreditcounter.data.Attraction;
import de.juliusawen.coastercreditcounter.data.AttractionCategory;
import de.juliusawen.coastercreditcounter.data.Element;
import de.juliusawen.coastercreditcounter.data.Location;
import de.juliusawen.coastercreditcounter.data.Park;
import de.juliusawen.coastercreditcounter.data.Visit;
import de.juliusawen.coastercreditcounter.data.YearHeader;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;

public class ExpandableRecyclerAdapter extends RecyclerView.Adapter<ExpandableRecyclerAdapter.ViewHolder>
{
    private RecyclerView recyclerView;

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
            this.textView = linearLayout.findViewById(R.id.textViewContentHolderExpandable_Parent);
            this.imageViewExpandToggle = linearLayout.findViewById(R.id.imageViewContentHolderExpandable_ExpandToggle);
        }
    }

    public ExpandableRecyclerAdapter(List<Element> elements, RecyclerOnClickListener.OnClickListener onClickListener)
    {
        Log.d(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.Constructor:: instantiating RecyclerAdapter with #[%d] elements...", elements.size()));

        this.elements = elements;
        this.onClickListener = onClickListener;
    }

    public void updateElements(List<Element> elements)
    {
        Log.d(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.updateElements:: updating with #[%d] elements...", elements.size()));
        this.elements = elements;
        notifyDataSetChanged();
        Log.d(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.updateElements:: updated with #[%d] elements", elements.size()));
    }

    public List<Element> getElements()
    {
        return this.elements;
    }

    public void smoothScrollToElement(Element element)
    {
        if(this.elements.contains(element))
        {
            Log.d(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.smoothScrollToElement:: scrolling to %s", element));
            int position = this.elements.indexOf(element);
            this.recyclerView.smoothScrollToPosition(position);
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.smoothScrollToElement:: %s not found", element));
        }
    }

    public void expandElement(Element element)
    {
        if(!elementsToExpand.contains(element))
        {
            Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.expandElement:: expanding %s", element));
            elementsToExpand.add(element);
            notifyDataSetChanged();
        }
        else
        {
            Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.expandElement:: %s already expanded", element));
        }
    }

    public void collapseElement(Element element)
    {
        if(elementsToExpand.contains(element))
        {
            Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.collapseElement:: collapsing %s", element));
            elementsToExpand.remove(element);
            notifyDataSetChanged();
        }
        else
        {
            Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.collapseElement:: %s already collapsed", element));
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ExpandableRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.content_holder_expandable, parent, false);
        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position)
    {
        final Element element = elements.get(position);
        Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.onBindViewHolder:: binding ViewHolder %s)", element));

        RecyclerOnClickListener recyclerOnClickListener = new RecyclerOnClickListener(viewHolder, this.onClickListener);

        this.handleExpandToggle(viewHolder, element);
        this.removeChildViews(viewHolder);

        if(element.isInstance(Location.class))
        {
            Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.onBindViewHolder:: %s has #[%d] child parks", element, element.getChildCountOfInstance(Park.class)));
            this.addChildViews(viewHolder, element.getChildrenOfInstance(Park.class), recyclerOnClickListener);
        }
        else if(element.isInstance(YearHeader.class))
        {
            Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.onBindViewHolder:: %s has #[%d] child visits", element, element.getChildCountOfInstance(Visit.class)));
            this.addChildViews(viewHolder, element.getChildrenOfInstance(Visit.class), recyclerOnClickListener);
        }
        else if(element.isInstance(AttractionCategory.class))
        {
            Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.onBindViewHolder:: %s has #[%d] child attractions", element, element.getChildCountOfInstance(Attraction.class)));
            this.addChildViews(viewHolder, element.getChildrenOfInstance(Attraction.class), recyclerOnClickListener);
        }

        viewHolder.textView.setText(StringTool.getSpannableString(element.getName(), Typeface.BOLD));
        viewHolder.textView.setTag(element);
        viewHolder.textView.setOnClickListener(recyclerOnClickListener);
        viewHolder.textView.setOnLongClickListener(recyclerOnClickListener);
        viewHolder.textView.setVisibility(View.VISIBLE);
    }

    private void removeChildViews(ViewHolder viewHolder)
    {
        if(viewHolder.childCount > 0)
        {
            for (int i = 0; i < viewHolder.childCount; i++)
            {
                viewHolder.linearLayout.removeView(viewHolder.linearLayout.findViewById(Constants.VIEW_TYPE_CHILD + i));
            }

            Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.removeChildViews:: #[%d] ChildViews removed", viewHolder.childCount));
            viewHolder.childCount = 0;
        }
    }

    private void handleExpandToggle(final ViewHolder viewHolder, final Element element)
    {
        if(elementsToExpand.contains(element))
        {
            viewHolder.isExpanded = true;
            viewHolder.imageViewExpandToggle.setImageDrawable(viewHolder.linearLayout.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_down));

            Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.handleExpandToggle:: ViewHolder %s is <EXPANDED>", element));
        }
        else
        {
            viewHolder.isExpanded = false;
            viewHolder.imageViewExpandToggle.setImageDrawable(viewHolder.linearLayout.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_right));

            Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.handleExpandToggle:: ViewHolder %s is <COLLAPSED>", element));
        }

        viewHolder.imageViewExpandToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onClickExpandToggle(viewHolder, element);
            }
        });
    }

    private void onClickExpandToggle(ViewHolder viewHolder, Element element)
    {
        if(viewHolder.isExpanded)
        {
            Log.i(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.onClickExpandToggle:: collapsing %s...", element));
            this.collapseElement(element);
        }
        else
        {
            Log.i(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.onClickExpandToggle:: expanding %s...", element));
            this.expandElement(element);
            this.smoothScrollToElement(element);
        }
    }

    private void addChildViews(ViewHolder viewHolder, List<Element> elements, RecyclerOnClickListener recyclerOnClickListener)
    {
        if(!elements.isEmpty())
        {
            this.handleChildViewCreation(viewHolder, elements, recyclerOnClickListener);
            viewHolder.imageViewExpandToggle.setVisibility(View.VISIBLE);
            Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.addChildViews:: ExpandToggle for %s is <VISIBLE>", elements.get(0)));
        }
        else
        {
            viewHolder.imageViewExpandToggle.setVisibility(View.INVISIBLE);
        }
    }

    private void handleChildViewCreation(final ViewHolder viewHolder, List<Element> elements, RecyclerOnClickListener recyclerOnClickListener)
    {
        int increment = 0;
        for(Element element : elements)
        {
            View childView = this.createChildView(viewHolder, element, increment, recyclerOnClickListener);

            if(viewHolder.isExpanded)
            {
                childView.setVisibility(View.VISIBLE);
                Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.handleChildViewCreation:: View %s is <VISIBLE>", element));
            }
            else
            {
                childView.setVisibility(View.GONE);
                Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.handleChildViewCreation:: View %s is <GONE>", element));
            }

            increment ++;
        }

        Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.handleChildViewCreation:: #[%d] ChildViews added.", increment));
    }

    private View createChildView(ViewHolder viewHolder, Element element, int increment, RecyclerOnClickListener recyclerOnClickListener)
    {
        View childView = viewHolder.linearLayout.findViewById(Constants.VIEW_TYPE_CHILD + increment);

        if(childView == null)
        {
            Log.v(Constants.LOG_TAG, String.format("ExpandableRecyclerAdapter.createChildView:: creating ChildViews %s...", element));

            LayoutInflater layoutInflater = (LayoutInflater) viewHolder.linearLayout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            childView = Objects.requireNonNull(layoutInflater).inflate(R.layout.content_holder_expandable, viewHolder.linearLayout, false);
            childView.findViewById(R.id.linearLayoutContentHolderExpandable).getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
            childView.setId(Constants.VIEW_TYPE_CHILD + increment);
            childView.setTag(element);
            childView.setOnClickListener(recyclerOnClickListener);
            childView.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;

            TextView textView = childView.findViewById(R.id.textViewContentHolder_Child);
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