package de.juliusawen.coastercreditcounter.frontend.attractions;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.backend.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.elements.Park;
import de.juliusawen.coastercreditcounter.backend.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Status;
import de.juliusawen.coastercreditcounter.frontend.activityDistributor.RequestCode;

public class CreateOrEditCustomAttractionActivityViewModel extends ViewModel
{
    RequestCode requestCode;
    boolean isEditMode = false;

    String toolbarTitle;
    String toolbarSubtitle;

    Park parentPark;
    IAttraction attraction;
    String name;
    int attractionType;
    Manufacturer manufacturer;
    AttractionCategory attractionCategory;
    Status status;
    int untrackedRideCount;
}
