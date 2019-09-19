package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Category;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Status;

public class CreateOrEditCustomAttractionActivityViewModel extends ViewModel
{
    public boolean isEditMode = false;

    public String toolbarTitle;
    public String toolbarSubtitle;

    public Park parentPark;
    public IAttraction attraction;
    public String name;
    public int attractionType;
    public Manufacturer manufacturer;
    public Category category;
    public Status status;
    public int untrackedRideCount;
}
