package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.OrphanElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.attributes.IPersistable;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

public class Status extends OrphanElement implements IPersistable
{
    private Status(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Status create(String name)
    {
        return Status.create(name, UUID.randomUUID());
    }

    public static Status create(String name, UUID uuid)
    {
        Status status = null;
        if(Element.nameIsValid(name))
        {
            status = new Status(name, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("Status.create:: %s created", status));
        }
        return status;
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_IS_DEFAULT, this.equals(App.settings.getDefaultStatus()));

            Log.v(Constants.LOG_TAG, String.format("Status.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Status.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}
