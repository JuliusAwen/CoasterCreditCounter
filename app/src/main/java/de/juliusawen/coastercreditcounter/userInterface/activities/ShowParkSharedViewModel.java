package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.DatePickerDialog;

import java.util.Calendar;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuTools.OptionsMenuButlerCompatibleBaseViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterFacade;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.IContentRecyclerViewAdapter;

public class ShowParkSharedViewModel extends OptionsMenuButlerCompatibleBaseViewModel
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
    public IContentRecyclerViewAdapter getContentRecyclerViewAdapter()
    {
        switch(this.requestCode)
        {
            case SHOW_ATTRACTIONS:
                return this.showAttractionsAdapterFacade.getAdapter();

            case SHOW_VISITS:
                return showVisitsAdapterFacade.getAdapter();

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
