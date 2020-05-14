package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.DatePickerDialog;

import androidx.lifecycle.ViewModel;

import java.util.Calendar;
import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuTools.IOptionsMenuButlerCompatibleViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowParkSharedViewModel extends ViewModel implements IOptionsMenuButlerCompatibleViewModel
{
    public RequestCode requestCode;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
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
    public ContentRecyclerViewAdapter getContentRecyclerViewAdapter()
    {
        return this.contentRecyclerViewAdapter;
    }

    @Override
    public List<IElement> getElements()
    {
        return null;
    }

    @Override
    public void setElements(List<IElement> elements) {}

    @Override
    public IElement getElement()
    {
        return this.park;
    }
}
