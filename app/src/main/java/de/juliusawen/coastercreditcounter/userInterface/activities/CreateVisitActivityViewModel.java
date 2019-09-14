package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.DatePickerDialog;

import androidx.lifecycle.ViewModel;

import java.util.Calendar;

import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;

public class CreateVisitActivityViewModel extends ViewModel
{
    public Park park;
    public Visit visit;
    public Visit existingVisit;
    public DatePickerDialog datePickerDialog;
    public Calendar calendar;
    public boolean datePicked = false;
}
