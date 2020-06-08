package de.juliusawen.coastercreditcounter.userInterface.baseViewModel;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterFacade;

public interface IBaseViewModel
{
    RequestCode getRequestCode();
    void setRequestCode(RequestCode requestCode);

    List<IElement> getElements();
    void setElements(List<IElement> elements);

    IElement getElement();
    void setElement(IElement element);

    ContentRecyclerViewAdapterFacade getContentRecyclerViewAdapterFacade();
    void setContentRecyclerViewAdapterFacade(ContentRecyclerViewAdapterFacade contentRecyclerViewAdapterFacade);
}
