package de.juliusawen.coastercreditcounter.userInterface.activities;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuTools.OptionsMenuButlerCompatibleBaseViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterConfiguration;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterFacade;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.IContentRecyclerViewAdapter;

public class ManagePropertiesViewModel extends OptionsMenuButlerCompatibleBaseViewModel
{
    public RequestCode requestCode;
    public ContentRecyclerViewAdapterFacade adapterFacade;

    public List<IElement> elements;

    public ElementType typeToManage;
    public IElement longClickedElement;
    public IElement propertyToReturn;

    public boolean isSelectionMode = false;

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
    public List<IElement> getElements()
    {
        return this.elements;
    }

    @Override
    public void setElements(List<IElement> elements)
    {
        this.elements = elements;
    }
}
