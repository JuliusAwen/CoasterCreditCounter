package de.juliusawen.coastercreditcounter.frontend.visits;

import android.app.DatePickerDialog;

import java.util.Calendar;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Park;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Visit;

class CreateVisitActivityViewModel extends ViewModel
{
    Park park;
    Visit visit;
    Visit existingVisit;
    DatePickerDialog datePickerDialog;
    Calendar calendar;
    boolean datePicked = false;
}
