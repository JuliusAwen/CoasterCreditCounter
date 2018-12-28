package de.juliusawen.coastercreditcounter.frontend.parks;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Park;

public class ShowParkActivityViewModel extends ViewModel
{
    public Park park;
    public int currentTab = -1;
}
