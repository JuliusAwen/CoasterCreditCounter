package de.juliusawen.coastercreditcounter.userInterface.activities;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.userInterface.baseViewModel.BaseViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterFacade;

public class ShowLocationsViewModel extends BaseViewModel
{
    public RequestCode requestCode;
    public ContentRecyclerViewAdapterFacade adapterFacade;
    public IElement currentLocation;

    public IElement longClickedElement;
    public IElement newParent;

    public boolean relocationModeEnabled;

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
    public IElement getElement()
    {
        return this.currentLocation;
    }
}
