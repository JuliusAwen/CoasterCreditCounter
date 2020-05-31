package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

public class ContentRecyclerViewAdapter extends AdapterExpansionHandler implements IContentRecyclerViewAdapter
{
    public ContentRecyclerViewAdapter(ContentRecyclerViewAdapterConfiguration configuration)
    {
        super(configuration);
        Log.frame(LogLevel.INFO, "instantiated", '#', true);
    }

    @Override
    public void notifySomethingChanged()
    {
        super.notifyDataSetChanged();
    }


    @Override
    public void setContent(IElement element)
    {
        Log.d(String.format(Locale.getDefault(), "setting [%s] as content...", element));
        List<IElement> content = new ArrayList<>();
        content.add(element);
        this.setContent(content);
    }

    @Override
    public void setContent(List<IElement> content)
    {
        Log.d(String.format(Locale.getDefault(), "setting [%d] Items...", content.size()));
        super.setContent(content);
    }

    @Override
    protected boolean handleOnClick(View view, boolean performExternalClick)
    {
        return super.handleOnClick(view, performExternalClick);
    }

    @Override
    protected boolean handleOnLongClick(View view, boolean performExternalClick)
    {
        return super.handleOnLongClick(view, performExternalClick);
    }

    @Override
    public void insertItem(IElement element)
    {
        Log.d(String.format("inserting %s...", element));
        super.insertItem(element);
    }

    @Override
    public void insertItem(int position, IElement element)
    {
        Log.d(String.format(Locale.getDefault(), "inserting %s at position[%d]...", element, position));
        super.insertItem(position, element);
    }

    @Override
    public void notifyItemChanged(IElement element)
    {
        Log.d(String.format("notifying %s changed...", element));
        super.notifyItemChanged(element);
    }

    @Override
    public void removeItem(IElement element)
    {
        Log.d(String.format("removing %s...", element));
        super.removeItem(element);
    }

    @Override
    public void swapItems(IElement element1, IElement element2)
    {
        Log.d(String.format("swapping %s and %s...", element1, element2));
        super.swapItems(element1, element2);
    }

    @Override
    public void scrollToItem(IElement element)
    {
        Log.d(String.format("scrolling to %s...", element));
        super.scrollToItem(element);
    }

    @Override
    public void groupContent(GroupType groupType)
    {
        Log.d(String.format("grouping by %s...", groupType));
        super.groupContent(groupType);
    }

    @Override
    public GroupType getGroupType()
    {
        return super.getGroupType();
    }

    @Override
    public void toggleExpansion(IElement element)
    {
        Log.d("toggling expansion...");
        super.toggleExpansion(element);
    }

    @Override
    public boolean isAllContentExpanded()
    {
        return super.isAllContentExpanded();
    }

    @Override
    public void expandAllContent()
    {
        Log.d("expanding all content...");
        super.expandAllContent();
    }

    @Override
    public void expandItem(IElement element, boolean scrollToItem)
    {
        Log.d(String.format("expanding %s - scrollToItem[%S]...", element, scrollToItem));
        super.expandItem(element, scrollToItem);
    }

    @Override
    public boolean isAllContentCollapsed()
    {
        return super.isAllContentCollapsed();
    }

    @Override
    public void collapseAllContent()
    {
        Log.d("collapsing all content...");
        super.collapseAllContent();
    }

    @Override
    public void collapseItem(IElement element, boolean scrollToItem)
    {
        Log.d(String.format("collapsing %s - scrollToItem[%S]...", element, scrollToItem));
        super.collapseItem(element, scrollToItem);
    }

    @Override
    public boolean isAllContentSelected()
    {
        return super.isAllContentSelected();
    }

    @Override
    public void selectAllContent()
    {
        Log.d("selecting all content...");
        super.selectAllContent();
    }

    @Override
    public void selectItem(IElement element)
    {
        Log.d(String.format("selecting %s...", element));
        super.selectItem(element);
    }

    @Override
    public boolean isAllContentDeselected()
    {
        return super.isAllContentDeselected();
    }

    @Override
    public void deselectAllContent()
    {
        Log.d("deselecting all content...");
        super.deselectAllContent();
    }

    @Override
    public void deselectItem(IElement element)
    {
        Log.d(String.format("deselecting %s...", element));
        super.deselectItem(element);
    }

    @Override
    public LinkedList<IElement> getSelectedItemsInOrderOfSelection()
    {
        return super.getSelectedItemsInOrderOfSelection();
    }

    @Override
    public IElement getLastSelectedItem()
    {
        return super.getLastSelectedItem();
    }

    @Override
    public IContentRecyclerViewAdapter addBottomSpacer()
    {
        Log.d("adding BottomSpacer...");
        if(!super.tryAddBottomSpacer())
        {
            Log.w("BottomSpacer is already added");
        }

        return this;
    }

    @Override
    public int getItemViewType(int position)
    {
        IElement element = super.getItem(position);

        if(element.isVisitedAttraction())
        {
            return ItemViewType.VISITED_ATTRACTION.ordinal();
        }
        else if(element.isBottomSpacer())
        {
            return ItemViewType.BOTTOM_SPACER.ordinal();
        }
        else
        {
            return ItemViewType.ELEMENT.ordinal();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int typeOfView)
    {
        RecyclerView.ViewHolder viewHolder;
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());

        switch(ItemViewType.getValue(typeOfView))
        {
            case ELEMENT:
                view = layoutInflater.inflate(R.layout.layout_recycler_view_item_view_type_element, viewGroup, false);
                viewHolder = new ViewHolderElement(view);
                break;

            case VISITED_ATTRACTION:
                view = layoutInflater.inflate(R.layout.layout_recycler_view_item_view_type_visited_attraction, viewGroup, false);
                viewHolder = new ViewHolderVisitedAttraction(view);
                break;

            case BOTTOM_SPACER:
                view = layoutInflater.inflate(R.layout.layout_bottom_spacer, viewGroup, false);
                viewHolder = new ViewHolderBottomSpacer(view);
                break;

            default:
                throw new IllegalStateException(String.format("unknown ViewType [%s]", ItemViewType.getValue(typeOfView)));
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        ItemViewType itemViewType = ItemViewType.getValue(viewHolder.getItemViewType());
        Log.wrap(LogLevel.VERBOSE, String.format(Locale.getDefault(), "binding ViewType[%s] for position[%d]...", itemViewType, position), '+', true);

        switch (itemViewType)
        {
            case ELEMENT:
                ViewHolderElement viewHolderElement = (ViewHolderElement) viewHolder;
                this.bindViewHolderElement(viewHolderElement, position);
                break;

//            case VISITED_ATTRACTION:
//                ViewHolderVisitedAttraction viewHolderVisitedAttraction = (ViewHolderVisitedAttraction) viewHolder;
//                this.bindViewHolderVisitedAttraction(viewHolderVisitedAttraction, position);
//                break;

            case BOTTOM_SPACER:
                break;

            default:
                throw new IllegalStateException(String.format("unknown ViewType[%s]", itemViewType));
        }
    }


    private enum ItemViewType
    {
        UNDETERMINED,
        ELEMENT,
        VISITED_ATTRACTION,
        BOTTOM_SPACER;

        static ItemViewType getValue(int ordinal)
        {
            if(ItemViewType.values().length >= ordinal)
            {
                return ItemViewType.values()[ordinal];
            }
            else
            {
                Log.e(String.format("ordinal [%s] out of bounds (Enum has [%s] values) - returning [%s]", ordinal, values().length, values()[0]));
                return values()[0];
            }
        }
    }
}