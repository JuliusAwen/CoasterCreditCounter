package de.juliusawen.coastercreditcounter.persistence;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *      Marks Element as being persistable.<br>
 *
 */
public interface IPersistable
{
    JSONObject toJson() throws JSONException;
}
