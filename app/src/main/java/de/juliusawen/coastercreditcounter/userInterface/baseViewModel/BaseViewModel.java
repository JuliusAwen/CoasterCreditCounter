package de.juliusawen.coastercreditcounter.userInterface.baseViewModel;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterFacade;

public abstract class BaseViewModel extends ViewModel implements IBaseViewModel
{
    private RequestCode requestCode = RequestCode.INVALID;

    private List<IElement> elements = new ArrayList<>();
    private IElement element;

    private ContentRecyclerViewAdapterFacade contentRecyclerViewAdapterFacade;

    @Override
    public RequestCode getRequestCode()
    {
        return this.requestCode;
    }

    @Override
    public void setRequestCode(RequestCode requestCode)
    {
        this.requestCode = requestCode;
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
        return this.element;
    }

    @Override
    public void setElement(IElement element)
    {
        this.element = element;
    }

    @Override
    public ContentRecyclerViewAdapterFacade getContentRecyclerViewAdapterFacade()
    {
        return this.contentRecyclerViewAdapterFacade;
    }

    @Override
    public void setContentRecyclerViewAdapterFacade(ContentRecyclerViewAdapterFacade contentRecyclerViewAdapterFacade)
    {
        this.contentRecyclerViewAdapterFacade = contentRecyclerViewAdapterFacade;
    }
}
