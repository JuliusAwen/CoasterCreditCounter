package de.juliusawen.coastercreditcounter.frontend.visits;

import android.app.DatePickerDialog;

import java.util.Calendar;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.backend.Utilities.AttractionCategoryHeaderProvider;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Park;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Visit;

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
