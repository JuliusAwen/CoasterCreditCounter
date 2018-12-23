package de.juliusawen.coastercreditcounter.presentation.visits;

import android.app.DatePickerDialog;

import java.util.Calendar;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.Utilities.AttractionCategoryHeaderProvider;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.data.elements.Visit;

class CreateVisitActivityViewModel extends ViewModel
{
    Park park;
    Visit visit;
    Visit existingVisit;
    AttractionCategoryHeaderProvider attractionCategoryHeaderProvider;
    DatePickerDialog datePickerDialog;
    Calendar calendar;
    boolean datePicked = false;
}
