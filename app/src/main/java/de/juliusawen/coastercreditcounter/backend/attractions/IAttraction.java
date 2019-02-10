package de.juliusawen.coastercreditcounter.backend.attractions;

import org.json.JSONException;
import org.json.JSONObject;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Manufacturer;


/**
 * Base interface for all attractions
 */
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
    int getUntracktedRideCount();
    void setUntracktedRideCount(int untracktedRideCount);
}
