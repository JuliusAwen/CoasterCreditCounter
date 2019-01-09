package de.juliusawen.coastercreditcounter.backend.objects.attractions;

import org.json.JSONException;
import org.json.JSONObject;

import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.Manufacturer;

public interface IAttraction extends IElement
{
    JSONObject toJson() throws JSONException;

    AttractionCategory getAttractionCategory();
    void setAttractionCategory(AttractionCategory attractionCategory);
    Manufacturer getManufacturer();
    void setManufacturer(Manufacturer manufacturer);
    int getTotalRideCount();
    void increaseTotalRideCount(int increment);
    void decreaseTotalRideCount(int decrement);
}
