package de.juliusawen.coastercreditcounter.presentation.activities.visits;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.data.elements.Visit;

public class ShowVisitActivityViewModel extends ViewModel
{
    public Visit visit;
    public Park parentPark;
}
