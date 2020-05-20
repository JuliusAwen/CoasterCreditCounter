package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public interface IContentRecyclerViewAdapter
{
    void setContent(List<IElement> content);

    void insertElement(IElement element);
    void insertElement(int position, IElement element);
    void notifyElementChanged(IElement element);
    void removeElement(IElement element);
}
