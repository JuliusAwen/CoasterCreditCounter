package de.juliusawen.coastercreditcounter.tools.menuTools;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.OLD_ContentRecyclerViewAdapter;

public interface IOptionsMenuButlerCompatibleViewModel
{
    RequestCode getRequestCode();
    OLD_ContentRecyclerViewAdapter getOldContentRecyclerViewAdapter();

    List<IElement> getElements();
    void setElements(List<IElement> elements);

    IElement getElement();
}
