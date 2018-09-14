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
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class CountableRecyclerAdapter extends RecyclerView.Adapter<CountableRecyclerAdapter.ViewHolder>
{
    private RecyclerView recyclerView;

    private static Set<Element> elementsToExpand = new HashSet<>();
    private List<Element> elements;
    private RecyclerOnClickListener.OnClickListener onClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        private LinearLayout rootLinearLayout;
        private TextView textView_parent;
        private ImageView imageViewExpandToggle;
        private int ArchievementCounter;

        private int childCount = 0;
        private boolean isExpanded = false;

        ViewHolder(LinearLayout rootLinearLayout)
        {
            super(rootLinearLayout);

            this.rootLinearLayout = rootLinearLayout;
            this.textView_parent = rootLinearLayout.findViewById(R.id.textViewContentHolderExpandable_Parent);
            this.imageViewExpandToggle = rootLinearLayout.findViewById(R.id.imageViewContentHolderExpandable_ExpandToggle);
        }
    }

    public CountableRecyclerAdapter(List<Element> elements, RecyclerOnClickListener.OnClickListener onClickListener)
    {
        Log.d(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.Constructor:: instantiating RecyclerAdapter with #[%d] elements...", elements.size()));

        this.elements = elements;
        this.onClickListener = onClickListener;
    }

    public void updateElements(List<Element> elements)
    {
        Log.d(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.updateElements:: updating with #[%d] elements...", elements.size()));
        this.elements = elements;
        notifyDataSetChanged();
        Log.d(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.updateElements:: updated with #[%d] elements", elements.size()));
    }

    public List<Element> getElements()
    {
        return this.elements;
    }

    public void smoothScrollToElement(Element element)
    {
        if(this.elements.contains(element))
        {
            Log.d(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.smoothScrollToElement:: scrolling to %s", element));
            int position = this.elements.indexOf(element);
            this.recyclerView.smoothScrollToPosition(position);
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.smoothScrollToElement:: %s not found", element));
        }
    }

    public void expandElement(Element element)
    {
        if(!elementsToExpand.contains(element))
        {
            Log.v(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.expandElement:: expanding %s", element));
            elementsToExpand.add(element);
            notifyDataSetChanged();
        }
        else
        {
            Log.v(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.expandElement:: %s already expanded", element));
        }
    }

    public void collapseElement(Element element)
    {
        if(elementsToExpand.contains(element))
        {
            Log.v(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.collapseElement:: collapsing %s", element));
            elementsToExpand.remove(element);
            notifyDataSetChanged();
        }
        else
        {
            Log.v(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.collapseElement:: %s already collapsed", element));
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
    public CountableRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.content_holder_expandable, parent, false);
        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position)
    {
        final Element element = elements.get(position);
        Log.v(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.onBindViewHolder:: binding ViewHolder %s)", element));

        RecyclerOnClickListener recyclerOnClickListener = new RecyclerOnClickListener(viewHolder, this.onClickListener);

        this.handleExpandToggle(viewHolder, element);
        this.removeChildViews(viewHolder);

        if(element.isInstance(AttractionCategory.class))
        {
            Log.v(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.onBindViewHolder:: %s has #[%d] child attractions", element, element.getChildCountOfInstance(Attraction.class)));
            this.addChildViews(viewHolder, element.getChildrenOfInstance(Attraction.class), recyclerOnClickListener);
        }

        viewHolder.textView_parent.setText(StringTool.getSpannableString(element.getName(), Typeface.BOLD));
        viewHolder.textView_parent.setTag(element);
        viewHolder.textView_parent.setOnClickListener(recyclerOnClickListener);
        viewHolder.textView_parent.setOnLongClickListener(recyclerOnClickListener);
        viewHolder.textView_parent.setVisibility(View.VISIBLE);
    }

    private void removeChildViews(ViewHolder viewHolder)
    {
        if(viewHolder.childCount > 0)
        {
            for (int i = 0; i < viewHolder.childCount; i++)
            {
                viewHolder.rootLinearLayout.removeView(viewHolder.rootLinearLayout.findViewById(Constants.VIEW_TYPE_CHILD + i));
            }

            Log.v(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.removeChildViews:: #[%d] ChildViews removed", viewHolder.childCount));
            viewHolder.childCount = 0;
        }
    }

    private void handleExpandToggle(final ViewHolder viewHolder, final Element element)
    {
        if(elementsToExpand.contains(element))
        {
            viewHolder.isExpanded = true;
            viewHolder.imageViewExpandToggle.setImageDrawable(viewHolder.rootLinearLayout.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_down));

            Log.v(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.handleExpandToggle:: ViewHolder %s is <EXPANDED>", element));
        }
        else
        {
            viewHolder.isExpanded = false;
            viewHolder.imageViewExpandToggle.setImageDrawable(viewHolder.rootLinearLayout.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_right));

            Log.v(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.handleExpandToggle:: ViewHolder %s is <COLLAPSED>", element));
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
            Log.i(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.onClickExpandToggle:: collapsing %s...", element));
            this.collapseElement(element);
        }
        else
        {
            Log.i(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.onClickExpandToggle:: expanding %s...", element));
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
            Log.v(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.addChildViews:: ExpandToggle for %s is <VISIBLE>", elements.get(0)));
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
                Log.v(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.handleChildViewCreation:: View %s is <VISIBLE>", element));
            }
            else
            {
                childView.setVisibility(View.GONE);
                Log.v(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.handleChildViewCreation:: View %s is <GONE>", element));
            }

            increment ++;
        }

        Log.v(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.handleChildViewCreation:: #[%d] ChildViews added.", increment));
    }

    private View createChildView(final ViewHolder viewHolder, Element element, int increment, RecyclerOnClickListener recyclerOnClickListener)
    {
        View childView = viewHolder.rootLinearLayout.findViewById(Constants.VIEW_TYPE_CHILD + increment);

        if(childView == null)
        {
            Log.v(Constants.LOG_TAG, String.format("CountableRecyclerAdapter.createChildView:: creating ChildViews %s...", element));

            LayoutInflater layoutInflater = (LayoutInflater) viewHolder.rootLinearLayout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            childView = Objects.requireNonNull(layoutInflater).inflate(R.layout.content_holder_expandable, viewHolder.rootLinearLayout, false);
            childView.findViewById(R.id.linearLayoutContentHolderExpandable).getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
            childView.setId(Constants.VIEW_TYPE_CHILD + increment);
            childView.setTag(element);

            LinearLayout linearLayout = childView.findViewById(R.id.linearLayoutContentHolderCountableChild);
            linearLayout.setVisibility(View.VISIBLE);


            final TextView textViewName = childView.findViewById(R.id.textViewContentHolder_CountableChildName);
            textViewName.setText(element.getName());

            final TextView textViewCount = childView.findViewById(R.id.textViewContentHolder_CountableChildCount);
            textViewCount.setText(StringTool.getSpannableString(String.valueOf(0), Typeface.BOLD));

            ImageView imageViewDecrease = childView.findViewById(R.id.imageViewContentHolderCountableChild_Decrease);
            imageViewDecrease.setImageDrawable(viewHolder.rootLinearLayout.getContext().getDrawable(R.drawable.ic_baseline_remove_circle_outline));
            imageViewDecrease.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int count = Integer.parseInt(textViewCount.getText().toString()) - 1;

                    if(count >= 0)
                    {
                        textViewCount.setText(StringTool.getSpannableString(String.valueOf(count), Typeface.BOLD));
                    }
                }
            });

            ImageView imageViewIncrease = childView.findViewById(R.id.imageViewContentHolderCountableChild_Increase);
            imageViewIncrease.setImageDrawable(viewHolder.rootLinearLayout.getContext().getDrawable(R.drawable.ic_baseline_add_circle_outline));
            imageViewIncrease.setTag(0);
            imageViewIncrease.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int count = Integer.parseInt(textViewCount.getText().toString()) + 1;
                    textViewCount.setText(StringTool.getSpannableString(String.valueOf(count), Typeface.BOLD));

                    if(count == 3 && viewHolder.ArchievementCounter == 0)
                    {
                        Toaster.makeToast(viewHolder.rootLinearLayout.getContext(),"Errungenschaft: 3x ist Bremer Recht!");
                        viewHolder.ArchievementCounter ++;
                    }
                    else if(count == 7 && viewHolder.ArchievementCounter == 1)
                    {
                        Toaster.makeToast(viewHolder.rootLinearLayout.getContext(),"Errungenschaft:Die Glorreichen Sieben");
                        viewHolder.ArchievementCounter ++;
                    }
                    else if(count == 12 && viewHolder.ArchievementCounter == 2)
                    {
                        Toaster.makeToast(viewHolder.rootLinearLayout.getContext(),"Errungenschaft: Im Dutzend billiger");
                        viewHolder.ArchievementCounter ++;
                    }
                    else if(count == 27 && viewHolder.ArchievementCounter == 3)
                    {
                        Toaster.makeToast(viewHolder.rootLinearLayout.getContext(),"Du kannst jetzt aufhören den Knopf zu drücken...");
                        viewHolder.ArchievementCounter ++;
                    }
                    else if(count == 50 && viewHolder.ArchievementCounter == 4)
                    {
                        Toaster.makeToast(viewHolder.rootLinearLayout.getContext(),"Ehrlich: es kommt nichts mehr!");
                        viewHolder.ArchievementCounter ++;
                    }
                    else if(count == 100 && viewHolder.ArchievementCounter == 5)
                    {
                        Toaster.makeToast(viewHolder.rootLinearLayout.getContext(),"Du hast ja 'nen Knall! xD");
                        viewHolder.ArchievementCounter ++;
                    }
                }
            });

            viewHolder.rootLinearLayout.addView(childView);
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