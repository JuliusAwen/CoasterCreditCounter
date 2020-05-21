package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

abstract class AdapterSelectionHandler extends AdapterDecorationHandler
{
    private boolean isSelectable = false;
    private boolean isMultipleSelection = false;
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
    public void setContent(List<IElement> content)
    {
        super.setContent(content);

        if(this.isSelectable)
        {
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

//            viewHolder.itemView.setOnClickListener(this.getSelectionOnClickListener());

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
    protected void handleOnClick(View view, boolean performExternalClick)
    {
        super.handleOnClick(view, performExternalClick);

        if(this.isSelectable)
        {

        }
    }

    public void selectAll()
    {
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

//    private void selectItems(List<IElement> elements)
//    {
//        for(IElement element : elements)
//        {
//            this.selectItem(element, false);
//        }
//    }

    public void selectItem(IElement element, boolean scrollToItem)
    {
        if(!this.isSelectable)
        {
            Log.w("ContentRecyclerViewAdapter is not selectable");
            return;
        }

        if(element != null)
        {
            if(!this.selectedItemsInOrderOfSelection.contains(element))
            {
                this.selectedItemsInOrderOfSelection.add(element);
                Log.v(String.format("%s selected", element));
            }

            if(super.exists(element))
            {
                super.notifyItemChanged(element);
            }
        }
    }

    public void deselectAll()
    {
        if(!this.isSelectable)
        {
            Log.w("ContentRecyclerViewAdapter is not selectable");
            return;
        }

        Log.i("deselecting all elements...");
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

    public void deselectItem(IElement element, boolean scrollToItem)
    {
        if(!this.isSelectable)
        {
            Log.w("ContentRecyclerViewAdapter is not selectable");
            return;
        }

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

    public boolean isAllSelected()
    {
        if(!this.isSelectable)
        {
            Log.w("ContentRecyclerViewAdapter is not selectable");
            return false;
        }

        List<IElement> items = new ArrayList<>(super.content);
        items.removeAll(this.selectedItemsInOrderOfSelection);

        return items.isEmpty();
    }

    public boolean isAllDeselected()
    {
        if(!this.isSelectable)
        {
            Log.w("ContentRecyclerViewAdapter is not selectable");
            return false;
        }

        return this.selectedItemsInOrderOfSelection.isEmpty();
    }

    public LinkedList<IElement> getSelectedItemsInOrderOfSelection()
    {
        if(!this.isSelectable)
        {
            Log.w("ContentRecyclerViewAdapter is not selectable");
            return new LinkedList<>();
        }

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

    public IElement getLastSelectedItem()
    {
        if(!this.isSelectable)
        {
            Log.w("ContentRecyclerViewAdapter is not selectable");
            return null;
        }

        if(!this.selectedItemsInOrderOfSelection.isEmpty())
        {
            return this.selectedItemsInOrderOfSelection.get(0);
        }

        return null;
    }

//    private View.OnClickListener getSelectionOnClickListener()
//    {
//        return new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                final IElement selectedItem = (IElement) view.getTag();
//
//                if(!selectedItemsInOrderOfSelection.contains(selectedItem))
//                {
//                    if(selectMultipleItems)
//                    {
//                        selectItem(selectedItem);
//
//                        if(!relevantChildTypesInSortOrder.isEmpty())
//                        {
//                            setItemsSelected(getRelevantChildren(selectedItem));
//                            selectParentIfAllRelevantChildrenAreSelected(getParentOfRelevantChild(selectedItem));
//                        }
//                    }
//                    else
//                    {
//                        if(!selectedItem.isGroupHeader())
//                        {
//                            deselectItem(getLastSelectedItem());
//                            selectItem(selectedItem);
//                        }
//                        else
//                        {
//                            Log.d(String.format("%s clicked - GroupHeaders are ignored.", selectedItem));
//                        }
//                    }
//                }
//                else
//                {
//                    deselectItem(selectedItem);
//
//                    if(selectMultipleItems)
//                    {
//                        if(!relevantChildTypesInSortOrder.isEmpty())
//                        {
//                            deselectItems(getRelevantChildren(selectedItem));
//                            deselectParentIfNotAllRelevantChildrenAreSelected(getParentOfRelevantChild(selectedItem));
//                        }
//                    }
//                }
//
//                if(recyclerCustomOnClickListener != null)
//                {
//                    recyclerCustomOnClickListener.onClick(view);
//                }
//            }
//        };
//    }
//
//    private void selectParentIfAllRelevantChildrenAreSelected(IElement parent)
//    {
//        if(parent != null && this.allRelevantChildrenAreSelected(parent))
//        {
//            this.selectItem(parent);
//        }
//    }
//
//    private void deselectParentIfNotAllRelevantChildrenAreSelected(IElement parent)
//    {
//        if(parent != null && !allRelevantChildrenAreSelected(parent))
//        {
//            this.deselectItem(parent);
//        }
//    }
//
//    private boolean allRelevantChildrenAreSelected(IElement parent)
//    {
//        boolean allRelevantChildrenAreSelected = false;
//        List<IElement> relevantChildren = this.getRelevantChildren(parent);
//
//        if(parent != null && !relevantChildren.isEmpty())
//        {
//            relevantChildren.removeAll(this.selectedItemsInOrderOfSelection);
//            allRelevantChildrenAreSelected = relevantChildren.isEmpty();
//        }
//
//        return allRelevantChildrenAreSelected;
//    }
}
