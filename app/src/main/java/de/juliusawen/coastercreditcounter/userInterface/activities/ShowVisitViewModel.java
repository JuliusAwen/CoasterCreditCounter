package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuTools.IOptionsMenuButlerCompatibleViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowVisitViewModel extends ViewModel implements IOptionsMenuButlerCompatibleViewModel
{
    public RequestCode requestCode;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    public Visit visit;

    public IElement longClickedElement;

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
        return null;
    }

    @Override
    public void setElements(List<IElement> elements) {}

    @Override
    public IElement getElement()
    {
        return this.visit;
    }
}
