package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.View;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.juliusawen.coastercreditcounter.BuildConfig;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.IGroupHeader;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

abstract class AdapterSelectionHandler extends AdapterDecorationHandler
{
    private boolean isSelectable = false;
    private boolean isMultipleSelection = false;

    protected final Set<Class<? extends IElement>> relevantChildTypesInSortOrder = new LinkedHashSet<>();
    private final LinkedList<IElement> selectedItemsInOrderOfSelection = new LinkedList<>();

    AdapterSelectionHandler()
    {
        super();
        Log.frame(LogLevel.VERBOSE, "instantiated", '=', true);
    }

    @Override
    protected void configure(Configuration configuration)
    {
        super.configure(configuration);

        this.isSelectable = configuration.isSelectable;
        this.isMultipleSelection = configuration.isMultipleSelection;

        Log.v(String.format("isSelectable[%S], isMultipleSelection[%S]", configuration.isSelectable, configuration.isMultipleSelection));
    }

    @Override
    protected void setContent(List<IElement> content)
    {
        super.setContent(content);

        if(this.isSelectable)
        {
            Log.v("setting Content...");
            this.selectedItemsInOrderOfSelection.clear();
        }
    }

    @Override
    protected IElement bindViewHolderElement(final ViewHolderElement viewHolder, int position)
    {
        IElement item = super.bindViewHolderElement(viewHolder, position);

        if(this.isSelectable)
        {
            Log.v(String.format(Locale.getDefault(), "binding %s for position [%d]...", item, position));

            if(this.selectedItemsInOrderOfSelection.contains(item))
            {
                viewHolder.itemView.setBackgroundColor(App.getContext().getColor(R.color.selected_color));
            }
            else
            {
                viewHolder.itemView.setBackgroundColor(App.getContext().getColor(R.color.default_color));
            }
        }

        return item;
    }

    @Override
    protected boolean handleOnClick(View view, boolean performExternalClick)
    {
        boolean isConsumed = super.handleOnClick(view, performExternalClick);

        if(!isConsumed && this.isSelectable)
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
            if(this.isMultipleSelection)
            {
                this.selectItem(element, false);

                if(!this.relevantChildTypesInSortOrder.isEmpty())
                {
                    this.selectItems(this.getRelevantChildren(element));
                    this.selectParentIfAllRelevantChildrenAreSelected(this.getParentOfRelevantChild(element));
                }
            }
            else
            {
                if(!element.isGroupHeader())
                {
                    this.deselectItem(getLastSelectedItem(), false);
                    this.selectItem(element, false);
                }
                else
                {
                    Log.d(String.format("%s clicked - GroupHeaders are ignored.", element));
                }
            }
        }
        else
        {
            this.deselectItem(element, false);

            if(this.isMultipleSelection)
            {
                if(!this.relevantChildTypesInSortOrder.isEmpty())
                {
                    this.deselectItems(getRelevantChildren(element));
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

            if(BuildConfig.DEBUG)
            {
                throw new IllegalStateException(message);
            }

            return super.content.get(super.getPosition(item.getParent()));
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
        super.restrictAccess(this.isSelectable);

        Log.d(String.format(Locale.getDefault(), "selecting all [%d] items...", super.getItemCount()));

        this.selectedItemsInOrderOfSelection.clear();

        for(IElement item : super.content)
        {
            this.selectItem(item, false);

//            if(!this.expandedItems.contains(item))
//            {
//                List<IElement> relevantChildren = this.getRelevantChildren(item);
//                if(!relevantChildren.isEmpty())
//                {
//                    this.selectItems(relevantChildren);
//                }
//            }
        }
    }

    private void selectItems(List<IElement> elements)
    {
        for(IElement element : elements)
        {
            this.selectItem(element, false);
        }
    }

    protected void selectItem(IElement element, boolean scrollToItem)
    {
        super.restrictAccess(this.isSelectable);

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
        super.restrictAccess(this.isSelectable);

        Log.v("deselecting content...");
        LinkedList<IElement> selectedItems = new LinkedList<>(this.selectedItemsInOrderOfSelection);
        this.deselectItems(selectedItems);
    }

    private void deselectItems(List<IElement> elements)
    {
        for(IElement element : elements)
        {
            this.deselectItem(element, false);
        }
    }

    protected void deselectItem(IElement element, boolean scrollToItem)
    {
        super.restrictAccess(this.isSelectable);

        if(element != null)
        {
            if(this.selectedItemsInOrderOfSelection.contains(element))
            {
                selectedItemsInOrderOfSelection.remove(element);
                Log.v(String.format("%s deselected", element));
            }

            super.notifyItemChanged(element);
        }
    }

    protected boolean isAllContentSelected()
    {
        super.restrictAccess(this.isSelectable);

        List<IElement> items = new ArrayList<>(super.content);
        items.removeAll(this.selectedItemsInOrderOfSelection);

        return items.isEmpty();
    }

    protected boolean isAllContentDeselected()
    {
        super.restrictAccess(this.isSelectable);

        return this.selectedItemsInOrderOfSelection.isEmpty();
    }

    protected LinkedList<IElement> getSelectedItemsInOrderOfSelection()
    {
        super.restrictAccess(this.isSelectable);

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
        super.restrictAccess(this.isSelectable);

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
            this.selectItem(parent, false);
            Log.v(String.format("selected %s because all children are selected", parent));
        }
    }

    private void deselectParentIfNotAllRelevantChildrenAreSelected(IElement parent)
    {
        if(parent != null && !allRelevantChildrenAreSelected(parent))
        {
            this.deselectItem(parent, false);
            Log.v(String.format("deselected %s because not all children are selected", parent));
        }
    }

    private boolean allRelevantChildrenAreSelected(IElement parent)
    {
        boolean allRelevantChildrenAreSelected = false;
        List<IElement> relevantChildren = this.getRelevantChildren(parent);

        if(parent != null && !relevantChildren.isEmpty())
        {
            relevantChildren.removeAll(this.selectedItemsInOrderOfSelection);
            allRelevantChildrenAreSelected = relevantChildren.isEmpty();
        }

        return allRelevantChildrenAreSelected;
    }

    protected ArrayList<IElement> getRelevantChildren(IElement item)
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
}
