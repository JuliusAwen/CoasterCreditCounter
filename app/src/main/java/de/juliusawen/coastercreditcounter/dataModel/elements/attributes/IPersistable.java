package de.juliusawen.coastercreditcounter.dataModel.elements.attributes;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Marks Element as persistable
 */
public interface IPersistable
{
    JSONObject toJson() throws JSONException;

}
