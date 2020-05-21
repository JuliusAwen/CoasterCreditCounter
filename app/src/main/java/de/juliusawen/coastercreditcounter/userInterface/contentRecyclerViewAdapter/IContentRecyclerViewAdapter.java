package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public interface IContentRecyclerViewAdapter
{
    void setContent(List<IElement> content);

    void insertItem(IElement element);
    void insertItem(int position, IElement element);
    void notifyItemChanged(IElement element);
    void removeItem(IElement element);

    void groupContent(GroupType groupType);
}
