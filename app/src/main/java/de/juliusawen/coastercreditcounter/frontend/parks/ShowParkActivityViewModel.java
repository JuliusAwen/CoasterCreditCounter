package de.juliusawen.coastercreditcounter.frontend.parks;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.backend.elements.Park;

class ShowParkActivityViewModel extends ViewModel
{
    Park park;
    int currentTab = -1;
}
