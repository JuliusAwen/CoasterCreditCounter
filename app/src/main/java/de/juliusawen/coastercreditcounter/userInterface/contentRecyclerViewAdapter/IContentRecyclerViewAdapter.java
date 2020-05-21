package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public interface IContentRecyclerViewAdapter
{
    void setContent(List<IElement> content);
    void notifyContentChanged();

    void insertItem(IElement element);
    void insertItem(int position, IElement element);
    void notifyItemChanged(IElement element);
    void removeItem(IElement element);


    void groupContent(GroupType groupType);


    boolean isAllExpanded();
    void expandAll();
    void expandItem(IElement element, boolean scrollToItem);

    boolean isAllCollapsed();
    void collapseAll();
    void collapseItem(IElement element, boolean scrollToItem);


    boolean isAllSelected();
    void selectAll();
    void selectItem(IElement element, boolean scrollToItem);

    boolean isAllDeselected();
    void deselectAll();
    void deselectItem(IElement element, boolean scrollToItem);

    List<IElement> getSelectedItemsInOrderOfSelection();
    IElement getLastSelectedItem();
}
