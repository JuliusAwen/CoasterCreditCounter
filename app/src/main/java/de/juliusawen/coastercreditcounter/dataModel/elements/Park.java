package de.juliusawen.coastercreditcounter.dataModel.elements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.IHasEvents;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.IHasNote;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.Note;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

/***
 * Parent: Location
 * Children: CustomAttractions, StockAttractions, Visits, Events, Note
 */
public final class Park extends Element implements IHasEvents, IHasNote, IPersistable
{
    private Park(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Park create(String name)
    {
        return Park.create(name, UUID.randomUUID());
    }

    public static Park create(String name, UUID uuid)
    {
        Park park = null;
        if(Element.isNameValid(name))
        {
            park = new Park(name, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("Park.create:: %s created", park.getFullName()));
        }
        return park;
    }

    @Override
    public Note getNote()
    {
        return this.hasChildrenOfType(Note.class) ? this.getChildrenAsType(Note.class).get(0) : null;
    }

    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            JsonTool.putChildren(jsonObject, this);

            Log.v(Constants.LOG_TAG, String.format("Park.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Park.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}
