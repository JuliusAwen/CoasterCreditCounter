package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;

public class CreateParkActivityViewModel extends ViewModel
{
    public Location parentLocation;
    public Park newPark;
}
