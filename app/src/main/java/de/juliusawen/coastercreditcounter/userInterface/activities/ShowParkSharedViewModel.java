package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.DatePickerDialog;

import java.util.Calendar;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuTools.OptionsMenuButlerCompatibleBaseViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.IContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.OLD.OLD_ContentRecyclerViewAdapter;

public class ShowParkSharedViewModel extends OptionsMenuButlerCompatibleBaseViewModel
{
    public RequestCode requestCode;
    public OLD_ContentRecyclerViewAdapter oldContentRecyclerViewAdapter;
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
        return this.oldContentRecyclerViewAdapter;
    }
}
