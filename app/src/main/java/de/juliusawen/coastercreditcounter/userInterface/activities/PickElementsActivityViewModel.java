package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuTools.IOptionsMenuButlerCompatibleViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class PickElementsActivityViewModel extends ViewModel implements IOptionsMenuButlerCompatibleViewModel
{
    public RequestCode requestCode;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    public List<IElement> elementsToPickFrom;

    public boolean isSinglePick;


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
        return this.elementsToPickFrom;
    }

    @Override
    public void setElements(List<IElement> elements)
    {
        this.elementsToPickFrom = elements;
    }

    @Override
    public IElement getElement()
    {
        return null;
    }
}
