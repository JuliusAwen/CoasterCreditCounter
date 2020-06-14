package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Model;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;

public class CreateAttractionViewModel extends ViewModel
{
    public RequestCode requestCode;

    public Park parentPark;
    public IAttraction attraction;

    public String name;
    public CreditType creditType;
    public Category category;
    public Manufacturer manufacturer;
    public Model model;
    public Status status;
    public int untrackedRideCount;

    public boolean editUntrackedRideCountEnabled = false;
}
