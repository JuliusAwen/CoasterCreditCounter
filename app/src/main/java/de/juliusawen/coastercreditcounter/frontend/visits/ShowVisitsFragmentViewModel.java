package de.juliusawen.coastercreditcounter.frontend.visits;

import android.app.DatePickerDialog;

import java.util.Calendar;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Park;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

class ShowVisitsFragmentViewModel extends ViewModel
{
    Park park;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    IElement longClickedElement;
    Calendar calendar;
    DatePickerDialog datePickerDialog;
}
