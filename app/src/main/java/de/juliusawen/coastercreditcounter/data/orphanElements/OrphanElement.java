package de.juliusawen.coastercreditcounter.data.orphanElements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.IElement;
import de.juliusawen.coastercreditcounter.globals.Constants;

public abstract class OrphanElement extends Element implements IElement
{
    public OrphanElement(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public JSONObject toJson()
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("element", Element.toJson(this, false));

            Log.v(Constants.LOG_TAG, String.format("OrphanElement.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("OrphanElement.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            return null;
        }
    }

    public static void removeAllChildren(List<? extends OrphanElement> orphanElements)
    {
        for(OrphanElement orphanElement : orphanElements)
        {
            orphanElement.getChildren().clear();
        }
        Log.v(Constants.LOG_TAG,  String.format("OrphanElement.removeAllChildren:: children removed from [%d] OrphanElements", orphanElements.size()));
    }
}
