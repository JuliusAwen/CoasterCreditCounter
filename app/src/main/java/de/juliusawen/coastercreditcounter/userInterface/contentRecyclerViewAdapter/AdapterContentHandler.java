package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

abstract class AdapterContentHandler extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    protected RecyclerView recyclerView;
    protected List<IElement> content;
    protected ArrayList<IElement> ungroupedContent;

    private GroupType groupType;
    private final GroupHeaderProvider groupHeaderProvider;

    AdapterContentHandler()
    {
        this.groupHeaderProvider = new GroupHeaderProvider();
        Log.frame(LogLevel.VERBOSE, "instantiated", '=', true);
    }

    protected void configure(Configuration configuration)
    {
        this.groupType = configuration.getGroupType();
        Log.v(String.format("set GroupType[%s]", groupType));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        this.recyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    protected RecyclerView.LayoutManager getLayoutManager()
    {
        return this.recyclerView.getLayoutManager();
    }

    @Override
    public int getItemCount()
    {
        return this.content.size();
    }

    public void setContent(List<IElement> content)
    {
        this.content = content;
        this.ungroupedContent = new ArrayList<>(content);
        this.groupContent(this.groupType);
        this.notifyContentChanged();
    }

    public void notifyContentChanged()
    {
        super.notifyDataSetChanged();
    }

    protected boolean itemExists(IElement element)
    {
        if(element != null)
        {
            return this.content.contains(element);
        }

        throw new IllegalArgumentException("Element can not be null");
    }

    protected IElement getItem(int position)
    {
        if(position >= this.getItemCount())
        {
            return this.content.get(position);
        }

        throw new IllegalStateException(String.format(Locale.getDefault(), "Position[%d] does not exist: Content contains [%d] items", position, this.getItemCount()));
    }

    protected int getPosition(IElement element)
    {
        if(itemExists(element))
        {
            return this.content.indexOf(element);
        }

        throw new IllegalStateException(String.format("Item %s does not exist in Content", element));
    }

    public void insertItem(IElement element)
    {
        this.insertItem(this.getItemCount(), element);
    }

    public void insertItem(int position, IElement element)
    {
        this.content.add(position, element);
        super.notifyItemInserted(this.getPosition(element));
    }

    public void notifyItemChanged(IElement element)
    {
        if(this.itemExists(element))
        {
            super.notifyItemChanged(this.getPosition(element));
        }
    }

    public void removeItem(IElement element)
    {
        if(this.itemExists(element))
        {
            super.notifyItemRemoved(this.getPosition(element));
            this.content.remove(element);
        }
    }

    private void swapItems(IElement element1, IElement element2)
    {
        if(this.itemExists(element1) && this.itemExists(element2))
        {
            int fromPosition = this.getPosition(element1);
            int toPosition = this.getPosition(element2);

            Collections.swap(this.content, fromPosition, toPosition);
            super.notifyItemMoved(fromPosition, toPosition);
            this.scrollToItem(element1);
        }
    }

    protected void scrollToItem(IElement element)
    {
        if(this.itemExists(element) && this.recyclerView != null)
        {
            Log.d(String.format("scrolling to %s", element));
            this.recyclerView.scrollToPosition(this.getPosition(element));
        }

        throw new IllegalStateException("RecyclerView can not be null");
    }

    public void groupContent(GroupType groupType)
    {
        this.groupType = groupType;
        Log.d(String.format("grouping Content by GroupType[%s]", this.groupType));

        //        this.selectedItemsInOrderOfSelection.clear();
        //        if(App.preferences.expandLatestYearHeaderByDefault())
        //        {
        //            SpecialGroupHeader latestSpecialGroupHeader = this.OLDGroupHeaderProvider.getSpecialGroupHeaderForLatestYear(groupedItems);
        //            this.expandedItems.add(latestSpecialGroupHeader);
        //        }

        List<IElement> groupedItems = this.groupType == GroupType.NONE
                ? new ArrayList<>(this.content)
                : this.groupHeaderProvider.groupElements(this.ungroupedContent, this.groupType);

        if(!this.content.isEmpty())
        {
            this.scrollToItem(this.getItem(0));
        }

        this.content = groupedItems;
    }
}
