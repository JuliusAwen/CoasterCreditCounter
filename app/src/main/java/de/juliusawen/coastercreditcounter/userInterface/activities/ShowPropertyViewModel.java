package de.juliusawen.coastercreditcounter.userInterface.activities;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IProperty;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuTools.BaseViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterConfiguration;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterFacade;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.IContentRecyclerViewAdapter;

public class ShowPropertyViewModel extends BaseViewModel
{
    public RequestCode requestCode;
    public IProperty property;
    public ContentRecyclerViewAdapterFacade adapterFacade;

    @Override
    public RequestCode getRequestCode()
    {
        return this.requestCode;
    }

    @Override
    public IContentRecyclerViewAdapter getContentRecyclerViewAdapter()
    {
        return this.adapterFacade.getAdapter();
    }

    @Override
    public ContentRecyclerViewAdapterConfiguration getContentRecyclerViewAdapterConfiguration()
    {
        return this.adapterFacade.getConfiguration();
    }

    @Override
    public IElement getElement()
    {
        return this.property;
    }
}
