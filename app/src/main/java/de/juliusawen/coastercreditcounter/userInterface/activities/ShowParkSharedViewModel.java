package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.DatePickerDialog;

import java.util.Calendar;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.userInterface.baseViewModel.BaseViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterFacade;

public class ShowParkSharedViewModel extends BaseViewModel
{
    public RequestCode requestCode;
    public ContentRecyclerViewAdapterFacade showAttractionsAdapterFacade;
    public ContentRecyclerViewAdapterFacade showVisitsAdapterFacade;

    public Park park;

    public IElement longClickedElement;

    public Calendar calendar;
    public DatePickerDialog datePickerDialog;

    @Override
    public RequestCode getRequestCode()
    {
        return this.requestCode;
    }

    @Override
    public ContentRecyclerViewAdapterFacade getContentRecyclerViewAdapterFacade()
    {
        switch(this.requestCode)
        {
            case SHOW_ATTRACTIONS:
                return this.showAttractionsAdapterFacade;

            case SHOW_VISITS:
                return showVisitsAdapterFacade;

            default:
                return null;
        }
    }

    @Override
    public IElement getElement()
    {
        return this.park;
    }
}
