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

    private final HashMap<IElement, Integer> generationByItem = new HashMap<>();
    private final Set<Class<? extends IElement>> relevantChildTypesInSortOrder = new LinkedHashSet<>();
    private final HashSet<IElement> expandedItems = new HashSet<>();

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

    private ArrayList<IElement> initializeItems(List<IElement> items, int generation)
    {
        Log.d(String.format(Locale.getDefault(), "initializing [%d] items - generation [%d]...", items.size(), generation));

        ArrayList<IElement> initializedItems = new ArrayList<>();
        for(IElement item : items)
        {
            initializedItems.add(item);
            this.generationByItem.put(item, generation);

            if(this.expandedItems.contains(item))
            {
                ArrayList<IElement> relevantChildren = this.getRelevantChildren(item);
                Log.v(String.format(Locale.getDefault(), "item %s is expanded - adding [%d] children", item, relevantChildren.size()));
                initializedItems.addAll(this.initializeItems(relevantChildren, generation + 1));
            }
        }

        return initializedItems;
    }

    protected IElement bindViewHolderElement(final ViewHolderElement viewHolder, int position)
    {
        IElement item = super.bindViewHolderElement(viewHolder, position);

        if(this.isExpandable)
        {
            int generation = this.getGeneration(item);
            Log.v(String.format(Locale.getDefault(), "binding %s for position [%d] - generation [%d]...", item, position, generation));

            if(!this.getRelevantChildren(item).isEmpty())
            {
                viewHolder.imageViewExpandToggle.setVisibility(View.VISIBLE);

                if(this.expandedItems.contains(item))
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

        return item;
    }

    @Override
    protected void handleOnClick(View view, boolean performExternalClick)
    {
        super.handleOnClick(view, performExternalClick);

        if(this.isExpandable)
        {
            IElement item = super.fetchItem(view);
            this.toggleExpansion(item);
        }
    }


    public void toggleExpansion(IElement item)
    {
        if(!this.isExpandable)
        {
            throw new IllegalAccessError("ContentRecyclerViewAdapter is not expandable");
        }

        if(!this.relevantChildTypesInSortOrder.isEmpty())
        {
            Log.d("toggling expansion...");

            if(!this.expandedItems.contains(item))
            {
                this.expandElement(item, true);
            }
            else
            {
                this.collapseElement(item, true);
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

            int expandedItemsCount;
            do
            {
                Log.v("expanding next generation");

                List<IElement> itemsToExpand = new ArrayList<>(this.content);
                expandedItemsCount = itemsToExpand.size();

                for(IElement element : itemsToExpand)
                {
                    this.expandElement(element, false);
                }
            }
            while(expandedItemsCount != super.getItemCount());

            super.scrollToItem(super.getItem(0));

            Log.d(String.format(Locale.getDefault(), "all [%d] items expanded", expandedItemsCount));
        }
        else
        {
            Log.v("no items to expand");
        }
    }

    private void expandGroupHeaderOfItem(IElement item)
    {
        for(IElement groupHeader : this.content)
        {
            if(groupHeader.isGroupHeader() && groupHeader.getChildren().contains(item))
            {
                this.expandElement(groupHeader, true);
                break;
            }
        }
    }

    public void expandElement(IElement item, boolean scrollToElement)
    {
        if(!this.isExpandable)
        {
            throw new IllegalAccessError("ContentRecyclerViewAdapter is not expandable");
        }

        if(!this.expandedItems.contains(item))
        {
            ArrayList<IElement> relevantChildren = this.getRelevantChildren(item);
            if(!relevantChildren.isEmpty())
            {
                Log.v(String.format("expanding item %s...", item));

                this.expandedItems.add(item);
                super.notifyItemChanged(item);

                int generation = this.getGeneration(item) + 1;
                int position = super.getPosition(item);
                for(IElement child : relevantChildren)
                {
                    this.generationByItem.put(child, generation);

                    position++;
                    super.insertItem(position, child);
                    Log.v(String.format(Locale.getDefault(), "added child %s at position [%d] - generation [%d]", child, position, generation));
                }

                if(scrollToElement)
                {
                    //scroll to item above expanded item (if any)
                    position = super.getPosition(item);
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
            Log.d(String.format("item [%s] is either already expanded or unknown", item));
        }
    }

    private int getGeneration(IElement item)
    {
        Integer generation = this.generationByItem.get(item);

        if(generation == null)
        {
            Log.e(String.format("could not determine generation for %s - returning -1...", item));
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

        for(IElement item : this.content)
        {
            for(IElement relevantChild : this.getRelevantChildren(item))
            {
                //Todo: test with deep location --> getRelevantAncestors needed?
                if(relevantChild.isLocation())
                {
                    List<IElement> relevantGrandchildren = this.getRelevantChildren(relevantChild);
                    if(!relevantGrandchildren.isEmpty())
                    {
                        if(!this.expandedItems.contains(relevantChild))
                        {
                            return false;
                        }
                    }
                }
                else if(relevantChild.isAttraction() || relevantChild.isVisit() || relevantChild.isModel())
                {
                    if(!this.expandedItems.contains(item))
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
            List<IElement> itemsToCollapse = new ArrayList<>(this.expandedItems);

            Log.d(String.format(Locale.getDefault(), "collapsing all [%s] items...", itemsToCollapse.size()));

            for(IElement element : itemsToCollapse)
            {
                this.collapseElement(element, false);
            }

            Log.v("all items collapsed");
        }
        else
        {
            Log.v("no elements to collapse");
        }
    }

    public void collapseElement(IElement item, boolean scrollToItem)
    {
        if(!this.isExpandable)
        {
            throw new IllegalAccessError("ContentRecyclerViewAdapter is not expandable");
        }

        if(this.expandedItems.contains(item))
        {
            List<IElement> relevantChildren = this.getRelevantChildren(item);
            if(!relevantChildren.isEmpty())
            {
                Log.v(String.format("collapsing item %s...", item));

                this.expandedItems.remove(item);
                super.notifyItemChanged(item);

                for(IElement child : relevantChildren)
                {
                    if(this.expandedItems.contains(child))
                    {
                        this.collapseElement(child, false);
                    }

                    int index = this.content.indexOf(child);
                    this.content.remove(child);
                    super.notifyItemRemoved(index);

                    this.generationByItem.remove(child);

                    Log.v(String.format(Locale.getDefault(), "removed child %s at index [%d]", child, index));
                }

                if(scrollToItem)
                {
                    this.scrollToItem(item);
                }
            }
        }
    }

    private ArrayList<IElement> getRelevantChildren(IElement item)
    {
        ArrayList<IElement> distinctRelevantChildren = new ArrayList<>();

        for(IElement child : item.getChildren())
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

        return this.expandedItems.isEmpty();
    }

    @Override
    public void groupContent(GroupType groupType)
    {
        super.groupContent(groupType);

        if(this.isExpandable)
        {
            this.generationByItem.clear();
            super.content = this.initializeItems(super.content, 0);
        }
    }
}