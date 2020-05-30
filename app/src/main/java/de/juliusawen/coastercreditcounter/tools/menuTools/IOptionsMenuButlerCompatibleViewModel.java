package de.juliusawen.coastercreditcounter.tools.menuTools;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterConfiguration;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.IContentRecyclerViewAdapter;

public interface IOptionsMenuButlerCompatibleViewModel
{
    RequestCode getRequestCode();

    IContentRecyclerViewAdapter getContentRecyclerViewAdapter();
    ContentRecyclerViewAdapterConfiguration getContentRecyclerViewAdapterConfiguration();

    List<IElement> getElements();
    void setElements(List<IElement> elements);

    IElement getElement();
}
