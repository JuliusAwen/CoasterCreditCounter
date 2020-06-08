package de.juliusawen.coastercreditcounter.userInterface.activities;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.userInterface.baseViewModel.BaseViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterFacade;

public class PickElementsViewModel extends BaseViewModel
{
    public RequestCode requestCode;
    public ContentRecyclerViewAdapterFacade adapterFacade;
    public List<IElement> elementsToPickFrom;

    public boolean isSinglePick;


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
        return this.elementsToPickFrom;
    }

    @Override
    public void setElements(List<IElement> elements)
    {
        this.elementsToPickFrom = elements;
    }
}
