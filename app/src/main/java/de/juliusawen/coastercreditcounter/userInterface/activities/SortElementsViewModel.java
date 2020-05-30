package de.juliusawen.coastercreditcounter.userInterface.activities;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuTools.OptionsMenuButlerCompatibleBaseViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.IContentRecyclerViewAdapter;

public class SortElementsViewModel extends OptionsMenuButlerCompatibleBaseViewModel
{
    public RequestCode requestCode;
    public List<IElement> elementsToSort;
    public IContentRecyclerViewAdapter contentRecyclerViewAdapter;

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
        return this.contentRecyclerViewAdapter;
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
}