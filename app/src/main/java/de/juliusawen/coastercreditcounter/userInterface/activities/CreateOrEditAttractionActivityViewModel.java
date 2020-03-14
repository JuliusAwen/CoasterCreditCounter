package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;

public class CreateOrEditAttractionActivityViewModel extends ViewModel
{
    public boolean isEditMode = false;

    public String toolbarTitle;
    public String toolbarSubtitle;

    public Park parentPark;
    public IAttraction attraction;
    public String name;
    public CreditType creditType;
    public Category category;
    public Manufacturer manufacturer;
    public Status status;
    public int untrackedRideCount;
}
