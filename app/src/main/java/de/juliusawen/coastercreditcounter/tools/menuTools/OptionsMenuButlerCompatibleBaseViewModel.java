package de.juliusawen.coastercreditcounter.tools.menuTools;

import androidx.lifecycle.ViewModel;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterConfiguration;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.IContentRecyclerViewAdapter;

public abstract class OptionsMenuButlerCompatibleBaseViewModel extends ViewModel implements IOptionsMenuButlerCompatibleViewModel
{

    @Override
    public RequestCode getRequestCode()
    {
        return null;
    }

    @Override
    public IContentRecyclerViewAdapter getContentRecyclerViewAdapter()
    {
        return null;
    }

    @Override
    public ContentRecyclerViewAdapterConfiguration getContentRecyclerViewAdapterConfiguration()
    {
        return null;
    }

    @Override
    public List<IElement> getElements()
    {
        return null;
    }

    @Override
    public void setElements(List<IElement> elements)
    {

    }

    @Override
    public IElement getElement()
    {
        return null;
    }
}
