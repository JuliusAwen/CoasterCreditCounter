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

    private boolean isGroupable;
    private GroupType groupType = GroupType.NONE;
    private GroupHeaderProvider groupHeaderProvider;

    AdapterContentHandler(Configuration configuration)
    {
        this.isGroupable = configuration.isGroupable;

        if(this.isGroupable)
        {
            this.groupType = configuration.getGroupType();
            this.groupHeaderProvider = new GroupHeaderProvider();
            Log.wrap(LogLevel.VERBOSE, String.format(Locale.getDefault(), "instantiated with GroupType[%s]", this.groupType), '=', false);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        this.recyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
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
    }

    public void groupContent(GroupType groupType)
    {
        if(!this.isGroupable)
        {
            throw new IllegalAccessError("ContentRecyclerView is not groupable");
        }

        this.groupType = groupType;
        Log.d(String.format("grouping Content by GroupType[%s]", this.groupType));

//        this.selectedItemsInOrderOfSelection.clear();
//        if(App.preferences.expandLatestYearHeaderByDefault())
//        {
//            SpecialGroupHeader latestSpecialGroupHeader = this.OLDGroupHeaderProvider.getSpecialGroupHeaderForLatestYear(groupedElements);
//            this.expandedItems.add(latestSpecialGroupHeader);
//        }

        List<IElement> groupedElements = this.groupType == GroupType.NONE
                ? new ArrayList<>(this.content)
                : this.groupHeaderProvider.groupElements(this.ungroupedContent, this.groupType);

        notifyDataSetChanged();

        if(!this.content.isEmpty())
        {
            this.scrollToElement(this.getElement(0));
        }

        this.content = groupedElements;
    }

    protected int getPosition(IElement element)
    {
        return this.content.indexOf(element);
    }

    protected IElement getElement(int position)
    {
        return this.content.get(position);
    }

    public void insertElement(IElement element)
    {
        this.insertElement(this.getItemCount(), element);
    }

    public void insertElement(int position, IElement element)
    {
        this.content.add(position, element);
        super.notifyItemInserted(this.content.indexOf(element));
    }

    public void notifyElementChanged(IElement element)
    {
        super.notifyItemChanged(this.content.indexOf(element));
    }

    public void removeElement(IElement element)
    {
        super.notifyItemRemoved(this.content.indexOf(element));
        this.content.remove(element);
    }

    private void swapItems(IElement item1, IElement item2)
    {
        int fromPosition = this.content.indexOf(item1);
        int toPosition = this.content.indexOf(item2);

        Collections.swap(this.content, fromPosition, toPosition);
        super.notifyItemMoved(fromPosition, toPosition);
        this.scrollToElement(item1);
    }

    protected void scrollToElement(IElement element)
    {
        if(element != null && this.content.contains(element) && this.recyclerView != null)
        {
            Log.d(String.format("scrolling to %s", element));
            this.recyclerView.scrollToPosition(content.indexOf(element));
        }
    }

    protected RecyclerView.LayoutManager getLayoutManager()
    {
        return this.recyclerView.getLayoutManager();
    }
}
