package de.juliusawen.coastercreditcounter.presentation.visits;

import android.app.DatePickerDialog;

import java.util.Calendar;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.data.elements.Visit;

class AddVisitActivityViewModel extends ViewModel
{
    Park park;
    Visit visit;
    DatePickerDialog datePickerDialog;
    Calendar calendar;
    boolean datePicked = false;
}
