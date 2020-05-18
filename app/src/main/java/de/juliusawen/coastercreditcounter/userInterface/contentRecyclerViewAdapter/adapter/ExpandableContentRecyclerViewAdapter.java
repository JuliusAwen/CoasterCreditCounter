package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.IDecorableExpandableContentRecyclerViewAdapter;

abstract class ExpandableContentRecyclerViewAdapter extends DecorableContentRecyclerViewAdapter implements IDecorableExpandableContentRecyclerViewAdapter
{
    private boolean isExpandable;
//    private boolean useDedicatedExpansionOnClickListener;

    private final HashMap<IElement, Integer> generationByElement = new HashMap<>();
    private final Set<Class<? extends IElement>> relevantChildTypesInSortOrder = new LinkedHashSet<>();
    private final HashSet<IElement> expandedElements = new HashSet<>();

    ExpandableContentRecyclerViewAdapter(List<IElement> content, ContentRecyclerViewAdapterConfiguration configuration)
    {
        super(content, configuration);
        this.isExpandable = configuration.isExpandable;
//        this.useDedicatedExpansionOnClickListener = configuration.useDedicatedExpansionToggleOnClickListener;
        this.relevantChildTypesInSortOrder.addAll(configuration.getRelevantChildTypesInSortOrder());

        this.content = this.initializeItems(this.content, 0);
    }

    private ArrayList<IElement> initializeItems(List<IElement> elements, int generation)
    {
        Log.v(String.format(Locale.getDefault(), "initializing [%d] elements - generation [%d]...", elements.size(), generation));

        ArrayList<IElement> initializedItems = new ArrayList<>();
        for(IElement item : elements)
        {
            initializedItems.add(item);
            this.generationByElement.put(item, generation);

            if(this.expandedElements.contains(item))
            {
                ArrayList<IElement> relevantChildren = this.getRelevantChildren(item);
                Log.v(String.format(Locale.getDefault(), "element %s is expanded - adding [%d] children", item, relevantChildren.size()));
                initializedItems.addAll(this.initializeItems(relevantChildren, generation + 1));
            }
        }

        return initializedItems;
    }

    protected IElement bindViewHolderElement(final ViewHolderElement viewHolder, int position)
    {
        IElement element = super.bindViewHolderElement(viewHolder, position);

        if(this.isExpandable)
        {
            int generation = this.getGeneration(element);

            Log.v(String.format(Locale.getDefault(), "binding %s for position [%d] - generation [%d]...", element, position, generation));

            viewHolder.imageViewExpandToggle.setTag(element);

            if(!this.getRelevantChildren(element).isEmpty())
            {
                viewHolder.imageViewExpandToggle.setVisibility(View.VISIBLE);

                if(this.expandedElements.contains(element))
                {
                    viewHolder.imageViewExpandToggle.setImageDrawable(App.getContext().getDrawable(R.drawable.arrow_drop_down));
                }
                else
                {
                    viewHolder.imageViewExpandToggle.setImageDrawable(App.getContext().getDrawable(R.drawable.arrow_drop_right));
                }
            }
            else
            {
                viewHolder.imageViewExpandToggle.setImageDrawable(DrawableProvider.getColoredDrawable(R.drawable.error_outline, R.color.default_color));
            }

//            if(this.useDedicatedExpansionOnClickListener)
//            {
//                viewHolder.imageViewExpandToggle.setOnClickListener(this.getDedicatedExpansionToggleOnClickListener());
//            }

            super.setPadding(generation, viewHolder);
        }

        return element;
    }

    private View.OnClickListener getDedicatedExpansionToggleOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                toggleExpansion((IElement) view.getTag());
            }
        };
    }

    private void toggleExpansion(IElement element)
    {
        if(!this.relevantChildTypesInSortOrder.isEmpty())
        {
            Log.v("toggling...");

            if(!this.expandedElements.contains(element))
            {
                this.expandElement(element, true);
            }
            else
            {
                this.collapseElement(element, true);
            }
        }
    }

    private void expandAll()
    {
        if(!this.content.isEmpty() && !this.isAllExpanded())
        {
            Log.v("expanding all elements");

            int expandedElementsCount;
            do
            {
                Log.v("expanding next generation");

                List<IElement> elementsToExpand = new ArrayList<>(this.content);
                expandedElementsCount = elementsToExpand.size();

                for(IElement element : elementsToExpand)
                {
                    this.expandElement(element, false);
                }
            }
            while(expandedElementsCount != this.content.size());

            super.scrollToElement(this.content.get(0));

            Log.d(String.format(Locale.getDefault(), "all [%d] elements expanded", expandedElementsCount));
        }
        else
        {
            Log.v("no elements to expand");
        }
    }

    private void expandGroupHeaderOfElement(IElement element)
    {
        for(IElement groupHeader : this.content)
        {
            if(groupHeader.isGroupHeader() && groupHeader.getChildren().contains(element))
            {
                this.expandElement(groupHeader, true);
                break;
            }
        }
    }

    private void expandElement(IElement element, boolean scrollToElement)
    {
        if(!this.expandedElements.contains(element))
        {
            ArrayList<IElement> relevantChildren = this.getRelevantChildren(element);
            if(!relevantChildren.isEmpty())
            {
                Log.v(String.format("expanding element %s...", element));

                this.expandedElements.add(element);
                super.notifyElementChanged(element);

                int generation = this.getGeneration(element) + 1;
                int index = this.content.indexOf(element);
                for(IElement child : relevantChildren)
                {
                    this.generationByElement.put(child, generation);

                    index ++;
                    this.content.add(index, child);
                    super.notifyItemInserted(index);

                    Log.v(String.format(Locale.getDefault(), "added child %s at index [%d] - generation [%d]", child, index, generation));
                }

                if(scrollToElement)
                {
                    //scroll to element above expanded element (if any)
                    index = this.content.indexOf(element);
                    if(index > 0)
                    {
                        index--;
                    }
                    ((LinearLayoutManager) this.getLayoutManager()).scrollToPositionWithOffset(index, 0);
                }
            }
        }
    }

    private int getGeneration(IElement element)
    {
        Integer generation = this.generationByElement.get(element);

        if(generation == null)
        {
            Log.e(String.format("could not determine generation for %s - returning -1...", element));
            generation = -1;
        }

        return generation;
    }

    private RecyclerView.LayoutManager getLayoutManager()
    {
        return this.recyclerView.getLayoutManager();
    }

    private boolean isAllExpanded()
    {
        for(IElement element : this.content)
        {
            for(IElement relevantChild : this.getRelevantChildren(element))
            {
                if(relevantChild.isLocation())
                {
                    List<IElement> relevantGrandchildren = this.getRelevantChildren(relevantChild);
                    if(!relevantGrandchildren.isEmpty())
                    {
                        if(!this.expandedElements.contains(relevantChild))
                        {
                            return false;
                        }
                    }
                }
                else if(relevantChild.isAttraction() || relevantChild.isVisit() || relevantChild.isModel())
                {
                    if(!this.expandedElements.contains(element))
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void collapseAll()
    {
        if(!this.content.isEmpty() && !this.isAllCollapsed())
        {
            List<IElement> elementsToCollapse = new ArrayList<>(this.expandedElements);

            Log.v(String.format(Locale.getDefault(), "collapsing all [%s] elements...", elementsToCollapse.size()));

            for(IElement element : elementsToCollapse)
            {
                this.collapseElement(element, false);
            }

            Log.v("all elements collapsed");
        }
        else
        {
            Log.v("no elements to collapse");
        }
    }

    private void collapseElement(IElement element, boolean scrollToElement)
    {
        if(this.expandedElements.contains(element))
        {
            List<IElement> relevantChildren = this.getRelevantChildren(element);
            if(!relevantChildren.isEmpty())
            {
                Log.v(String.format("collapsing element %s...", element));

                this.expandedElements.remove(element);
                super.notifyElementChanged(element);

                for(IElement child : relevantChildren)
                {
                    if(this.expandedElements.contains(child))
                    {
                        this.collapseElement(child, false);
                    }

                    int index = this.content.indexOf(child);
                    this.content.remove(child);
                    super.notifyItemRemoved(index);

                    this.generationByElement.remove(child);

                    Log.v(String.format(Locale.getDefault(), "removed child %s at index [%d]", child, index));
                }

                if(scrollToElement)
                {
                    this.scrollToElement(element);
                }
            }
        }
    }

    private boolean isAllCollapsed()
    {
        for(IElement element : this.content)
        {
            List<IElement> relevantChildren = this.getRelevantChildren(element);
            int relevantChildCount = relevantChildren.size();

            relevantChildren.removeAll(this.content);

            if(relevantChildCount != relevantChildren.size())
            {
                return false;
            }
        }

        return true;
    }

    private ArrayList<IElement> getRelevantChildren(IElement element)
    {
        ArrayList<IElement> distinctRelevantChildren = new ArrayList<>();

        for(IElement child : element.getChildren())
        {
            for(Class<? extends IElement> childType : this.relevantChildTypesInSortOrder)
            {
                if(childType.isAssignableFrom(child.getClass()) && !distinctRelevantChildren.contains(child))
                {
                    distinctRelevantChildren.add(child);
                    break;
                }
            }
        }

        return distinctRelevantChildren;
    }
}