package de.juliusawen.coastercreditcounter.presentation.activities.visits;

import android.arch.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.data.elements.Visit;

public class ShowVisitViewModel extends ViewModel
{
    public Visit visit;
    public Park parentPark;
}
