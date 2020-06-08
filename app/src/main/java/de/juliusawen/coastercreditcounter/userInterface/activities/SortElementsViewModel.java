package de.juliusawen.coastercreditcounter.userInterface.activities;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.userInterface.baseViewModel.BaseViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterFacade;

public class SortElementsViewModel extends BaseViewModel
{
    public RequestCode requestCode;
    public List<IElement> elementsToSort;
    public ContentRecyclerViewAdapterFacade adapterFacade;

    public IElement selectedElement;
    public IElement defaultProperty;

    @Override
    public RequestCode getRequestCode()
    {
        return this.requestCode;
    }

    @Override
    public ContentRecyclerViewAdapterFacade getContentRecyclerViewAdapterFacade()
    {
        return this.adapterFacade;
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