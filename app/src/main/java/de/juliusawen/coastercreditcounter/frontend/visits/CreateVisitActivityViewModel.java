package de.juliusawen.coastercreditcounter.frontend.visits;

import android.app.DatePickerDialog;

import androidx.lifecycle.ViewModel;

import java.util.Calendar;

import de.juliusawen.coastercreditcounter.backend.elements.Park;
import de.juliusawen.coastercreditcounter.backend.elements.Visit;

public class CreateVisitActivityViewModel extends ViewModel
{
    Park park;
    Visit visit;
    Visit existingVisit;
    DatePickerDialog datePickerDialog;
    Calendar calendar;
    boolean datePicked = false;
}
