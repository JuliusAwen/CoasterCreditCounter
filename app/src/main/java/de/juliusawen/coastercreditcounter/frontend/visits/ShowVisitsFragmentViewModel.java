package de.juliusawen.coastercreditcounter.frontend.visits;

import android.app.DatePickerDialog;

import androidx.lifecycle.ViewModel;

import java.util.Calendar;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Park;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.toolbox.MenuAgent;

public class ShowVisitsFragmentViewModel extends ViewModel
{
    Park park;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    IElement longClickedElement;
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    MenuAgent optionsMenuAgent;

    @Override
    public void onCleared()
    {
        super.onCleared();
        this.contentRecyclerViewAdapter = null;
    }
}
