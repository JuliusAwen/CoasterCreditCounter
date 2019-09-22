package de.juliusawen.coastercreditcounter.dataModel.elements.interfaces;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Marks Element as persistable
 */
public interface IPersistable
{
    JSONObject toJson() throws JSONException;

}
