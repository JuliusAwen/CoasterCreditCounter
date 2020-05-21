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
        Log.d("attaching to RecyclerView...");
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

    protected void setContent(List<IElement> content)
    {
        Log.d("setting Content...");

        this.content = content;
        this.ungroupedContent = new ArrayList<>(content);
        this.groupContent(this.groupType);
    }

    protected void notifyContentChanged()
    {
        super.notifyDataSetChanged();
    }

    protected boolean exists(IElement element)
    {
        if(element == null)
        {
            Log.w("Element is null");
            return false;
        }

        return this.content.contains(element);
    }

    protected IElement getItem(int position)
    {
        if(position >= this.getItemCount())
        {
            throw new IllegalStateException(String.format(Locale.getDefault(), "Position[%d] does not exist: Content contains [%d] items", position, this.getItemCount()));
        }

        return this.content.get(position);
    }

    protected int getPosition(IElement element)
    {
        if(!this.exists(element))
        {
            throw new IllegalStateException(String.format("Item %s does not exist in Content", element));
        }

        return this.content.indexOf(element);
    }

    protected void insertItem(IElement element)
    {
        this.insertItem(this.getItemCount(), element);
    }

    protected void insertItem(int position, IElement element)
    {
        this.content.add(position, element);
        super.notifyItemInserted(this.getPosition(element));
    }

    protected void notifyItemChanged(IElement element)
    {
        if(!this.exists(element))
        {
            Log.w(String.format("can not notify - %s does not exist in content", element));
            return;
        }

        super.notifyItemChanged(this.getPosition(element));
    }

    protected void removeItem(IElement element)
    {
        if(!this.exists(element))
        {
            Log.w(String.format("can not remove - %s does not exist in content", element));
            return;
        }

        super.notifyItemRemoved(this.getPosition(element));
        this.content.remove(element);
    }

    private void swapItems(IElement element1, IElement element2)
    {
        if(!this.exists(element1))
        {
            Log.w(String.format("can not swap - %s does not exist in content", element1));
            return;
        }

        if(!this.exists(element2))
        {
            Log.w(String.format("can not swap - %s does not exist in content", element2));
            return;
        }

        int fromPosition = this.getPosition(element1);
        int toPosition = this.getPosition(element2);

        Collections.swap(this.content, fromPosition, toPosition);
        super.notifyItemMoved(fromPosition, toPosition);
        this.scrollToItem(element1);
    }

    protected void scrollToItem(IElement element)
    {
        if(this.content.isEmpty())
        {
            Log.w("can not scroll - Content is empty");
            return;
        }

        if(!this.exists(element))
        {
            Log.w(String.format("can not scroll - %s does not exist in Content", element));
            return;
        }

        if(this.recyclerView == null)
        {
            Log.w("can not scroll - ContentRecyclerViewAdapter is not attached to RecyclerView yet");
            return;
        }

        Log.d(String.format("scrolling to %s", element));
        this.recyclerView.scrollToPosition(this.getPosition(element));
    }

    protected void groupContent(GroupType groupType)
    {
        Log.v("grouping Content...");
        this.groupType = groupType;

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
