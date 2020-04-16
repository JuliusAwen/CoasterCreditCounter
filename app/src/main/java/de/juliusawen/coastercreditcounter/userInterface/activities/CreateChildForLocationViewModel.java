package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;

public class CreateChildForLocationViewModel extends ViewModel
{
    public RequestCode requestCode;

    public Location parentLocation;
    public IElement createdChild;
}
