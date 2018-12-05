package de.juliusawen.coastercreditcounter.data.attractions;

import org.json.JSONException;
import org.json.JSONObject;

import de.juliusawen.coastercreditcounter.data.elements.IElement;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;

public interface IAttraction extends IElement
{
    JSONObject toJson() throws JSONException;

    AttractionCategory getAttractionCategory();
    void setAttractionCategory(AttractionCategory attractionCategory);
    int getTotalRideCount();
    void increaseTotalRideCount(int increment);
    void decreaseTotalRideCount(int decrement);
}
