package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.IGroupHeader;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

abstract class AdapterSelectionHandler extends AdapterDecorationHandler
{
    private final LinkedList<IElement> selectedItemsInOrderOfSelection = new LinkedList<>();

    @Deprecated
    AdapterSelectionHandler()
    {
        super();
        Log.frame(LogLevel.VERBOSE, "instantiated", '=', true);
    }

    AdapterSelectionHandler(ContentRecyclerViewAdapterConfiguration configuration)
    {
        super(configuration);
        Log.frame(LogLevel.VERBOSE, "instantiated", '=', true);
    }

    @Override
    protected void setContent(List<IElement> content)
    {
        super.setContent(content);
        this.selectedItemsInOrderOfSelection.clear();
    }

    @Override
    protected IElement bindViewHolderElement(final ViewHolderElement viewHolder, int position)
    {
        IElement item = super.bindViewHolderElement(viewHolder, position);

        Log.v(String.format(Locale.getDefault(), "binding %s for position [%d]...", item, position));

        if(this.selectedItemsInOrderOfSelection.contains(item))
        {
            viewHolder.itemView.setBackgroundColor(App.getContext().getColor(R.color.selected_color));
        }
        else
        {
            viewHolder.itemView.setBackgroundColor(App.getContext().getColor(R.color.default_color));
        }

        return item;
    }

    @Override
    protected boolean handleOnClick(View view, boolean performExternalClick)
    {
        boolean isConsumed = super.handleOnClick(view, performExternalClick);

        if(super.isSelectable() && !isConsumed)
        {
            IElement selectedItem = super.fetchItem(view);
            this.toggleSelection(selectedItem);
            return true;
        }

        return isConsumed;
    }

    private void toggleSelection(IElement element)
    {
        if(!this.selectedItemsInOrderOfSelection.contains(element))
        {
            if(super.isMultipleSelection())
            {
                this.selectItem(element);

                if(super.hasRelevantChildTypes())
                {
                    this.selectItems(super.fetchRelevantChildren(element));
                    this.selectParentIfAllRelevantChildrenAreSelected(this.getParentOfRelevantChild(element));
                }
            }
            else
            {
                if(!element.isGroupHeader())
                {
                    this.deselectItem(this.getLastSelectedItem());
                    this.selectItem(element);
                }
                else
                {
                    Log.d(String.format("%s clicked - GroupHeaders are ignored.", element));
                }
            }
        }
        else
        {
            this.deselectItem(element);

            if(super.isMultipleSelection())
            {
                if(super.hasRelevantChildTypes())
                {
                    this.deselectItems(super.fetchRelevantChildren(element));
                    this.deselectParentIfNotAllRelevantChildrenAreSelected(this.getParentOfRelevantChild(element));
                }
            }
        }
    }

    private IElement getParentOfRelevantChild(IElement item)
    {
        if(item.isAttraction())
        {
            return this.getGroupHeaderForItem(item);
        }
        else if(!item.isOrphan())
        {
            String message =
                    String.format("********** IT HAPPENED! CRVA.getParentOfRelevantChild for item not being OrphanElement or Attraction was called! Class [%s]",
                            item.getClass().getSimpleName());
            Log.e(message);

            return super.getItem(super.getPosition(item.getParent()));
        }
        else
        {
            return null;
        }
    }

    private IGroupHeader getGroupHeaderForItem(IElement groupElement)
    {
        for(IElement item : super.content)
        {
            if(item.isGroupHeader())
            {
                if(item.getChildren().contains(groupElement))
                {
                    return (IGroupHeader)item;
                }
            }
        }

        return null;
    }

    protected void selectAllContent()
    {
        Log.d(String.format(Locale.getDefault(), "selecting all [%d] items...", super.getItemCount()));

        this.selectedItemsInOrderOfSelection.clear();

        for(IElement item : super.content)
        {
            this.selectItem(item);

            for(IElement relevantChild : super.fetchRelevantChildren(item))
            if(!this.exists(relevantChild))
            {
                this.selectItem(relevantChild);
            }
        }
    }

    private void selectItems(List<IElement> elements)
    {
        for(IElement element : elements)
        {
            this.selectItem(element);
        }
    }

    protected void selectItem(IElement element)
    {
        if(element != null)
        {
            if(!this.selectedItemsInOrderOfSelection.contains(element))
            {
                this.selectedItemsInOrderOfSelection.add(element);
                Log.v(String.format("%s selected", element));
            }

            super.notifyItemChanged(element);
        }
    }

    protected void deselectAllContent()
    {
        Log.v("deselecting content...");
        LinkedList<IElement> selectedItems = new LinkedList<>(this.selectedItemsInOrderOfSelection);
        this.deselectItems(selectedItems);
    }

    private void deselectItems(List<IElement> elements)
    {
        for(IElement element : elements)
        {
            this.deselectItem(element);
        }
    }

    protected void deselectItem(IElement element)
    {
        if(element != null)
        {
            if(this.selectedItemsInOrderOfSelection.contains(element))
            {
                this.selectedItemsInOrderOfSelection.remove(element);
                Log.v(String.format("%s deselected", element));
            }

            super.notifyItemChanged(element);
        }
    }

    protected boolean isAllContentSelected()
    {
        List<IElement> items = new ArrayList<>(super.content);
        items.removeAll(this.selectedItemsInOrderOfSelection);
        return items.isEmpty();
    }

    protected boolean isAllContentDeselected()
    {
        return this.selectedItemsInOrderOfSelection.isEmpty();
    }

    protected LinkedList<IElement> getSelectedItemsInOrderOfSelection()
    {
        LinkedList<IElement> selectedItems = new LinkedList<>();

        for(IElement item : this.selectedItemsInOrderOfSelection)
        {
            if(!(item.isGroupHeader()))
            {
                selectedItems.add(item);
            }
        }

        return selectedItems;
    }

    protected IElement getLastSelectedItem()
    {
        if(!this.selectedItemsInOrderOfSelection.isEmpty())
        {
            return this.selectedItemsInOrderOfSelection.get(0);
        }

        return null;
    }

    private void selectParentIfAllRelevantChildrenAreSelected(IElement parent)
    {
        if(parent != null && this.allRelevantChildrenAreSelected(parent))
        {
            this.selectItem(parent);
            Log.v(String.format("selected %s because all children are selected", parent));
        }
    }

    private void deselectParentIfNotAllRelevantChildrenAreSelected(IElement parent)
    {
        if(parent != null && !this.allRelevantChildrenAreSelected(parent))
        {
            this.deselectItem(parent);
            Log.v(String.format("deselected %s because not all children are selected", parent));
        }
    }

    private boolean allRelevantChildrenAreSelected(IElement parent)
    {
        boolean allRelevantChildrenAreSelected = false;
        List<IElement> relevantChildren = super.fetchRelevantChildren(parent);

        if(parent != null && !relevantChildren.isEmpty())
        {
            relevantChildren.removeAll(this.selectedItemsInOrderOfSelection);
            allRelevantChildrenAreSelected = relevantChildren.isEmpty();
        }

        return allRelevantChildrenAreSelected;
    }
}
