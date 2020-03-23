package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.Blueprint;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;

public class EditAttractionActivityViewModel extends ViewModel
{
    public IAttraction attraction;

    public String name;
    public Blueprint blueprint;
    public CreditType creditType;
    public Category category;
    public Manufacturer manufacturer;
    public Status status;
    public int untrackedRideCount;
}
