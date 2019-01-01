package de.juliusawen.coastercreditcounter.backend.objects.orphanElements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.backend.objects.elements.Element;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.globals.Constants;

public abstract class OrphanElement extends Element implements IElement, IOrphanElement
{
    public OrphanElement(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.JSON_STRING_ELEMENT, Element.toJson(this, true));

            Log.v(Constants.LOG_TAG, String.format("OrphanElement.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("OrphanElement.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}
