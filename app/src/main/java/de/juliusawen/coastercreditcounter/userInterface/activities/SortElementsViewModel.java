package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuTools.IOptionsMenuButlerCompatibleViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.IContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.OLD.OLD_ContentRecyclerViewAdapter;

public class SortElementsViewModel extends ViewModel implements IOptionsMenuButlerCompatibleViewModel
{
    public RequestCode requestCode;
    public List<IElement> elementsToSort;
    public OLD_ContentRecyclerViewAdapter oldContentRecyclerViewAdapter;

    public IElement selectedElement;
    public IElement defaultProperty;

    @Override
    public RequestCode getRequestCode()
    {
        return this.requestCode;
    }


    @Override
    public IContentRecyclerViewAdapter getContentRecyclerViewAdapter()
    {
        return this.oldContentRecyclerViewAdapter;
    }

    @Override
    public List<IElement> getElements()
    {
        return this.elementsToSort;
    }

    @Override
    public void setElements(List<IElement> elements)
    {
        this.elementsToSort = elements;
    }

    @Override
    public IElement getElement()
    {
        return null;
    }
}
