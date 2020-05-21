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
    private GroupHeaderProvider groupHeaderProvider;

    AdapterContentHandler()
    {
        Log.v("instantiated");
    }

    protected void configure(Configuration configuration)
    {
        this.groupType = configuration.getGroupType();
        this.groupHeaderProvider = new GroupHeaderProvider();

        Log.wrap(LogLevel.VERBOSE, String.format(Locale.getDefault(), "GroupType[%s]", this.groupType), '=', false);
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

        notifyDataSetChanged();

        if(!this.content.isEmpty())
        {
            this.scrollToItem(this.getItem(0));
        }

        this.content = groupedItems;
    }

    protected int getPosition(IElement item)
    {
        return this.content.indexOf(item);
    }

    protected IElement getItem(int position)
    {
        return this.content.get(position);
    }

    public void insertItem(IElement item)
    {
        this.insertItem(this.getItemCount(), item);
    }

    public void insertItem(int position, IElement item)
    {
        this.content.add(position, item);
        super.notifyItemInserted(this.getPosition(item));
    }

    public void notifyItemChanged(IElement item)
    {
        super.notifyItemChanged(this.getPosition(item));
    }

    public void removeItem(IElement item)
    {
        super.notifyItemRemoved(this.getPosition(item));
        this.content.remove(item);
    }

    private void swapItems(IElement item1, IElement item2)
    {
        int fromPosition = this.content.indexOf(item1);
        int toPosition = this.content.indexOf(item2);

        Collections.swap(this.content, fromPosition, toPosition);
        super.notifyItemMoved(fromPosition, toPosition);
        this.scrollToItem(item1);
    }

    protected void scrollToItem(IElement element)
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
