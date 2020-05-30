package de.juliusawen.coastercreditcounter.userInterface.activities;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuTools.OptionsMenuButlerCompatibleBaseViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.IContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.OLD.OLD_ContentRecyclerViewAdapter;

public class PickElementsViewModel extends OptionsMenuButlerCompatibleBaseViewModel
{
    public RequestCode requestCode;
    public OLD_ContentRecyclerViewAdapter oldContentRecyclerViewAdapter;
    public List<IElement> elementsToPickFrom;

    public boolean isSinglePick;


    @Override
    public RequestCode getRequestCode()
    {
        return this.requestCode;
    }


    @Override
    public IContentRecyclerViewAdapter getContentRecyclerViewAdapter()
    {
        return this.oldContentRecyclerViewAdapter;
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
