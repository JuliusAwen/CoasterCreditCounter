package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.PropertyType;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuTools.IOptionsMenuButlerCompatibleViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ManagePropertiesActivityViewModel extends ViewModel implements IOptionsMenuButlerCompatibleViewModel
{
    public RequestCode requestCode;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    public List<IElement> elements;

    public PropertyType propertyTypeToManage;
    public IElement longClickedElement;
    public IElement propertyToReturn;

    public boolean isSelectionMode = false;

    @Override
    public RequestCode getRequestCode()
    {
        return this.requestCode;
    }

    @Override
    public ContentRecyclerViewAdapter getContentRecyclerViewAdapter()
    {
        return this.contentRecyclerViewAdapter;
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

    @Override
    public IElement getElement()
    {
        return null;
    }
}
