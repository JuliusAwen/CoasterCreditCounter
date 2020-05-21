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
    protected List<IElement> content = new ArrayList<>();
    protected ArrayList<IElement> ungroupedContent = new ArrayList<>();

    private GroupType groupType = GroupType.NONE;
    private final GroupHeaderProvider groupHeaderProvider;

    AdapterContentHandler()
    {
        this.groupHeaderProvider = new GroupHeaderProvider();
        Log.frame(LogLevel.VERBOSE, "instantiated", '=', true);
    }

    protected void configure(Configuration configuration)
    {
        this.groupType = configuration.getGroupType();
        Log.v(String.format("GroupType is [%s]", configuration.getGroupType()));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        Log.d("attaching...");
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
    }

    public void notifyContentChanged()
    {
        super.notifyDataSetChanged();
    }

    protected boolean exists(IElement element)
    {
        if(element != null)
        {
            return this.content.contains(element);
        }

        throw new IllegalArgumentException("Element can not be null");
    }

    protected IElement getItem(int position)
    {
        if(position < this.getItemCount())
        {
            return this.content.get(position);
        }

        throw new IllegalStateException(String.format(Locale.getDefault(), "Position[%d] does not exist: Content contains [%d] items", position, this.getItemCount()));
    }

    protected int getPosition(IElement element)
    {
        if(exists(element))
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
        if(this.exists(element))
        {
            super.notifyItemChanged(this.getPosition(element));
        }
    }

    public void removeItem(IElement element)
    {
        if(this.exists(element))
        {
            super.notifyItemRemoved(this.getPosition(element));
            this.content.remove(element);
        }
    }

    private void swapItems(IElement element1, IElement element2)
    {
        if(this.exists(element1) && this.exists(element2))
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
        if(this.content.isEmpty())
        {
            Log.w("not scrolling - Content is empty");
            return;
        }

        if(!this.exists(element))
        {
            Log.w(String.format("not scrolling - %s does not exist in Content", element));
            return;
        }

        if(this.recyclerView == null)
        {
            Log.w("not scrolling - ContentRecyclerViewAdapter is not attached to RecyclerView yet");
            return;
        }

        Log.d(String.format("scrolling to %s", element));
        this.recyclerView.scrollToPosition(this.getPosition(element));
    }

    public void groupContent(GroupType groupType)
    {
        this.groupType = groupType;
        Log.v(String.format("grouping Content by GroupType[%s]", groupType));

        //        this.selectedItemsInOrderOfSelection.clear();
        //        if(App.preferences.expandLatestYearHeaderByDefault())
        //        {
        //            SpecialGroupHeader latestSpecialGroupHeader = this.OLDGroupHeaderProvider.getSpecialGroupHeaderForLatestYear(groupedItems);
        //            this.expandedItems.add(latestSpecialGroupHeader);
        //        }

        this.content = this.groupType == GroupType.NONE
                ? new ArrayList<>(this.content)
                : this.groupHeaderProvider.groupElements(this.ungroupedContent, this.groupType);

        this.notifyContentChanged();
        this.scrollToItem(this.getItem(0));
    }
}
