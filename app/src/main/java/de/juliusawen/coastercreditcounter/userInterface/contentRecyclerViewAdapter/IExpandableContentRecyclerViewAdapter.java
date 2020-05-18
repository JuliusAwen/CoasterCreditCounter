package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public interface IExpandableContentRecyclerViewAdapter extends IContentRecyclerViewAdapter
{
    void toggleExpansion(IElement element);

    boolean isAllExpanded();
    void expandElement(IElement element, boolean scrollToElement);
    void expandAll();

    boolean isAllCollapsed();
    void collapseElement(IElement element, boolean scrollToElement);
    void collapseAll();
}
