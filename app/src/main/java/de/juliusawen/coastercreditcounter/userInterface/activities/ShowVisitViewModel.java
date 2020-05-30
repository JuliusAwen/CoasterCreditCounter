package de.juliusawen.coastercreditcounter.userInterface.activities;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuTools.OptionsMenuButlerCompatibleBaseViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.IContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.OLD.OLD_ContentRecyclerViewAdapter;

public class ShowVisitViewModel extends OptionsMenuButlerCompatibleBaseViewModel
{
    public RequestCode requestCode;
    public OLD_ContentRecyclerViewAdapter oldContentRecyclerViewAdapter;
    public Visit visit;

    public IElement longClickedElement;

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
    public IElement getElement()
    {
        return this.visit;
    }
}
