package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.util.LinkedList;
import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public interface IContentRecyclerViewAdapter
{
    void configure(Configuration configuration);

    void setContent(List<IElement> content);

    void insertItem(IElement element);
    void insertItem(int position, IElement element);
    void notifyItemChanged(IElement element);
    void removeItem(IElement element);


    void groupContent(GroupType groupType);


    boolean isAllContentExpanded();
    void expandAllContent();
    void expandItem(IElement element, boolean scrollToItem);

    boolean isAllContentCollapsed();
    void collapseAllContent();
    void collapseItem(IElement element, boolean scrollToItem);


    boolean isAllContentSelected();
    void selectAllContent();
    void selectItem(IElement element, boolean scrollToItem);

    boolean isAllContentDeselected();
    void deselectAllContent();
    void deselectItem(IElement element, boolean scrollToItem);

    LinkedList<IElement> getSelectedItemsInOrderOfSelection();
    IElement getLastSelectedItem();
}
