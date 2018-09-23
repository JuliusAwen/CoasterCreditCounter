package de.juliusawen.coastercreditcounter.presentation.activities.parks;

import android.arch.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.data.elements.Park;

public class ShowParkViewModel extends ViewModel
{
    public Park park;
    public int currentTab = -1;
}
