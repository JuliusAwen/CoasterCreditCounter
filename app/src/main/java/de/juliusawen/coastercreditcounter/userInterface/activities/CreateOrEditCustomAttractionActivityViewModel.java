package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Status;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;

public class CreateOrEditCustomAttractionActivityViewModel extends ViewModel
{
    public RequestCode requestCode;
    public boolean isEditMode = false;

    public String toolbarTitle;
    public String toolbarSubtitle;

    public Park parentPark;
    public IAttraction attraction;
    public String name;
    public int attractionType;
    public Manufacturer manufacturer;
    public AttractionCategory attractionCategory;
    public Status status;
    public int untrackedRideCount;
}
