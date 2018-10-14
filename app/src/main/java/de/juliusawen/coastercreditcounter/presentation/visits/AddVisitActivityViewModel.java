package de.juliusawen.coastercreditcounter.presentation.visits;

import android.app.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.data.elements.Visit;

class AddVisitActivityViewModel extends ViewModel
{
    Park park;
    Visit visit;
    Visit existingVisit;
    List<Element> attractionCategoryHeaders = new ArrayList<>();
    DatePickerDialog datePickerDialog;
    Calendar calendar;
    boolean datePicked = false;
}
