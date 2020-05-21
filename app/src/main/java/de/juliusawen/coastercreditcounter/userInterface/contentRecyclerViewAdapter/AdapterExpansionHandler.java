package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

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
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

abstract class AdapterExpansionHandler extends AdapterDecorationHandler
{
    private boolean isExpandable;

    private final HashMap<IElement, Integer> generationByElement = new HashMap<>();
    private final Set<Class<? extends IElement>> relevantChildTypesInSortOrder = new LinkedHashSet<>();
    private final HashSet<IElement> expandedElements = new HashSet<>();

    AdapterExpansionHandler()
    {
        super();
        Log.v("instantiated");

    }

    protected void configure(Configuration configuration)
    {
        super.configure(configuration);

        this.isExpandable = configuration.isExpandable;
        if(this.isExpandable)
        {
            this.relevantChildTypesInSortOrder.addAll(configuration.getChildTypesToExpandInSortOrder());
            Log.wrap(LogLevel.VERBOSE, String.format(Locale.getDefault(), "instantiated with [%d] relevant child types", this.relevantChildTypesInSortOrder.size()), '=', false);
        }
    }

    private ArrayList<IElement> initializeItems(List<IElement> elements, int generation)
    {
        Log.d(String.format(Locale.getDefault(), "initializing [%d] elements - generation [%d]...", elements.size(), generation));

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

    protected IElement bindViewHolderElement(final ContentRecyclerViewAdapter.ViewHolderElement viewHolder, int position)
    {
        IElement element = super.bindViewHolderElement(viewHolder, position);

        if(this.isExpandable)
        {
            int generation = this.getGeneration(element);
            Log.v(String.format(Locale.getDefault(), "binding %s for position [%d] - generation [%d]...", element, position, generation));

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

            super.setPadding(generation, viewHolder);
        }

        return element;
    }

    @Override
    protected void handleOnClick(View view, boolean performExternalClick)
    {
        super.handleOnClick(view, performExternalClick);

        if(this.isExpandable)
        {
            IElement element = super.fetchElement(view);
            this.toggleExpansion(element);
        }
    }


    public void toggleExpansion(IElement element)
    {
        if(!this.isExpandable)
        {
            throw new IllegalAccessError("ContentRecyclerViewAdapter is not expandable");
        }

        if(!this.relevantChildTypesInSortOrder.isEmpty())
        {
            Log.d("toggling expansion...");

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

    public void expandAll()
    {
        if(!this.isExpandable)
        {
            throw new IllegalAccessError("ContentRecyclerViewAdapter is not expandable");
        }

        if(!super.content.isEmpty() && !this.isAllExpanded())
        {
            Log.d("expanding all elements");

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
            while(expandedElementsCount != super.getItemCount());

            super.scrollToElement(super.getElement(0));

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

    public void expandElement(IElement element, boolean scrollToElement)
    {
        if(!this.isExpandable)
        {
            throw new IllegalAccessError("ContentRecyclerViewAdapter is not expandable");
        }

        if(!this.expandedElements.contains(element))
        {
            ArrayList<IElement> relevantChildren = this.getRelevantChildren(element);
            if(!relevantChildren.isEmpty())
            {
                Log.v(String.format("expanding element %s...", element));

                this.expandedElements.add(element);
                super.notifyElementChanged(element);

                int generation = this.getGeneration(element) + 1;
                int position = super.getPosition(element);
                for(IElement child : relevantChildren)
                {
                    this.generationByElement.put(child, generation);

                    position++;
                    super.insertElement(position, child);
                    Log.v(String.format(Locale.getDefault(), "added child %s at position [%d] - generation [%d]", child, position, generation));
                }

                if(scrollToElement)
                {
                    //scroll to element above expanded element (if any)
                    position = super.getPosition(element);
                    if(position > 0)
                    {
                        position--;
                    }
                    ((LinearLayoutManager) super.getLayoutManager()).scrollToPositionWithOffset(position, 0);
                }
            }
        }
        else
        {
            Log.d(String.format("element [%s] is either already expanded or unknown", element));
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

    public boolean isAllExpanded()
    {
        if(!this.isExpandable)
        {
            throw new IllegalAccessError("ContentRecyclerViewAdapter is not expandable");
        }

        for(IElement element : this.content)
        {
            for(IElement relevantChild : this.getRelevantChildren(element))
            {
                //Todo: test with deep location --> getRelevantAncestors needed?
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

    public void collapseAll()
    {
        if(!this.isExpandable)
        {
            throw new IllegalAccessError("ContentRecyclerViewAdapter is not expandable");
        }

        if(!this.content.isEmpty() && !this.isAllCollapsed())
        {
            List<IElement> elementsToCollapse = new ArrayList<>(this.expandedElements);

            Log.d(String.format(Locale.getDefault(), "collapsing all [%s] elements...", elementsToCollapse.size()));

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

    public void collapseElement(IElement element, boolean scrollToElement)
    {
        if(!this.isExpandable)
        {
            throw new IllegalAccessError("ContentRecyclerViewAdapter is not expandable");
        }

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

    public boolean isAllCollapsed()
    {
        if(!this.isExpandable)
        {
            throw new IllegalAccessError("ContentRecyclerViewAdapter is not expandable");
        }

        return this.expandedElements.isEmpty();
    }

    @Override
    public void groupContent(GroupType groupType)
    {
        super.groupContent(groupType);

        if(this.isExpandable)
        {
            this.generationByElement.clear();
            super.content = this.initializeItems(super.content, 0);
        }
    }
}