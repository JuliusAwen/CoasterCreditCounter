package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.SpecialGroupHeader;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

abstract class AdapterExpansionHandler extends AdapterSelectionHandler
{
    private final HashMap<IElement, Integer> generationByItem = new HashMap<>();
    private final HashSet<IElement> expandedItems = new HashSet<>();

    AdapterExpansionHandler(ContentRecyclerViewAdapterConfiguration configuration)
    {
        super(configuration);
        Log.frame(LogLevel.VERBOSE, "instantiated", '=', true);
    }

    private boolean isExpandable()
    {
        return super.hasRelevantChildTypes();
    }

    @Override
    protected void setContent(List<IElement> content)
    {
        super.setContent(content);
        this.reInitializeItems();
    }

    @Override
    protected void groupContent(GroupType groupType)
    {
        super.groupContent(groupType);

        if(groupType == GroupType.YEAR && App.preferences.expandLatestYearHeaderByDefault())
        {
            SpecialGroupHeader latestSpecialGroupHeader = super.getLatestSpecialGroupHeader();
            if(latestSpecialGroupHeader != null)
            {
                Log.d(String.format("expanding %s according to App.preferences expandLatestYearHeaderByDefault=TRUE", latestSpecialGroupHeader));
                this.expandedItems.add(latestSpecialGroupHeader);
            }
        }

        this.reInitializeItems();
    }

    private void reInitializeItems()
    {
        if(this.isExpandable())
        {
            Log.v("re-initializing generations by item...");
            this.generationByItem.clear();
            super.content = this.initializeItems(super.content, 0);
        }
    }

    private ArrayList<IElement> initializeItems(List<IElement> items, int generation)
    {
        Log.d(String.format(Locale.getDefault(), "initializing [%d] items for generation [%d]...", items.size(), generation));

        ArrayList<IElement> initializedItems = new ArrayList<>();
        for(IElement item : items)
        {
            initializedItems.add(item);
            this.generationByItem.put(item, generation);

            if(this.expandedItems.contains(item))
            {
                ArrayList<IElement> relevantChildren = super.fetchRelevantChildren(item);
                Log.v(String.format(Locale.getDefault(), "item %s is expanded - adding [%d] children", item, relevantChildren.size()));
                initializedItems.addAll(this.initializeItems(relevantChildren, generation + 1));
            }
        }

        return initializedItems;
    }

    @Override
    protected IElement bindViewHolderElement(final ViewHolderElement viewHolder, int position)
    {
        IElement item = super.bindViewHolderElement(viewHolder, position);

        if(this.isExpandable())
        {
            int generation = this.getGeneration(item);
            Log.v(String.format(Locale.getDefault(), "binding %s for position [%d] - generation [%d]...", item, position, generation));

            if(super.hasRelevantChildren(item))
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
    protected boolean handleOnClick(View view, boolean performExternalClick)
    {
        if(view.getId() == R.id.imageViewRecyclerView)
        {
            this.toggleExpansion(super.fetchItem(view));
            return true;
        }

        return super.handleOnClick(view, performExternalClick);
    }

    protected void toggleExpansion(IElement item)
    {
        if(!this.isExpandable())
        {
            Log.w(String.format("[%s] is not expandable", this.getClass().getSimpleName()));
            return;
        }

        if(!this.expandedItems.contains(item))
        {
            this.expandItem(item, true);
        }
        else
        {
            this.collapseItem(item, true);
        }

        Log.v("expansion toggled");
    }

    protected void expandAllContent()
    {
        if(!this.isAllContentExpanded())
        {
            Log.d("expanding all items");

            int expandedItemsCount;
            do
            {
                Log.v("expanding next generation");

                List<IElement> itemsToExpand = new ArrayList<>(this.content);
                expandedItemsCount = itemsToExpand.size();

                for(IElement element : itemsToExpand)
                {
                    this.expandItem(element, false);
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

    protected void expandGroupHeaderForItem(IElement item)
    {
        for(IElement groupHeader : this.content)
        {
            if(groupHeader.isGroupHeader() && groupHeader.getChildren().contains(item))
            {
                this.expandItem(groupHeader, true);
                break;
            }
        }
    }

    protected void expandItem(IElement item, boolean scrollToItem)
    {
        if(!this.expandedItems.contains(item))
        {
            ArrayList<IElement> relevantChildren = super.fetchRelevantChildren(item);
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

                if(scrollToItem) //scroll to item above expanded item (if any)
                {
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

    protected boolean isAllContentExpanded()
    {
        for(IElement item : this.content)
        {
            for(IElement relevantChild : super.fetchRelevantChildren(item))
            {
                if(relevantChild.isLocation())
                {
                    List<IElement> relevantGrandchildren = super.fetchRelevantChildren(relevantChild);
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

    protected void collapseAllContent()
    {
        if(!this.isAllContentCollapsed())
        {
            List<IElement> itemsToCollapse = new ArrayList<>(this.expandedItems);

            Log.d(String.format(Locale.getDefault(), "collapsing all [%s] items...", itemsToCollapse.size()));

            for(IElement element : itemsToCollapse)
            {
                this.collapseItem(element, false);
            }

            Log.v("all items collapsed");
        }
        else
        {
            Log.v("no elements to collapse");
        }
    }

    protected void collapseItem(IElement item, boolean scrollToItem)
    {
        if(this.expandedItems.contains(item))
        {
            List<IElement> relevantChildren = super.fetchRelevantChildren(item);
            if(!relevantChildren.isEmpty())
            {
                Log.v(String.format("collapsing item %s...", item));

                this.expandedItems.remove(item);
                super.notifyItemChanged(item);

                for(IElement child : relevantChildren)
                {
                    if(this.expandedItems.contains(child))
                    {
                        this.collapseItem(child, false);
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

    protected boolean isAllContentCollapsed()
    {
        return this.expandedItems.isEmpty();
    }

    @Override
    protected void setFormatAsPrettyPrint(boolean formatAsPrettyPrint)
    {
        super.setFormatAsPrettyPrint(formatAsPrettyPrint);
        this.expandAllContent();
    }
}