package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public interface IContentRecyclerViewAdapter
{
    void notifyElementInserted(IElement element);
    void notifyElementChanged(IElement element);
    void notifyElementRemoved(IElement element);
}
