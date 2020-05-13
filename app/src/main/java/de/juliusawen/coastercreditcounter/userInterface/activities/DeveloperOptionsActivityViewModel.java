package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuTools.IOptionsMenuButlerCompatibleViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class DeveloperOptionsActivityViewModel extends ViewModel implements IOptionsMenuButlerCompatibleViewModel
{
    public RequestCode requestCode = RequestCode.DEVELOPER_OPTIONS;

    public DeveloperOptionsActivity.Mode mode;

    @Override
    public RequestCode getRequestCode()
    {
        return this.requestCode;
    }

    @Override
    public ContentRecyclerViewAdapter getContentRecyclerViewAdapter()
    {
        return null;
    }

    @Override
    public List<IElement> getElements()
    {
        return null;
    }

    @Override
    public void setElements(List<IElement> elements) {}

    @Override
    public IElement getElement()
    {
        return null;
    }
}
