package de.juliusawen.coastercreditcounter.dataModel.elements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.JsonTool;
import de.juliusawen.coastercreditcounter.tools.StringTool;

public class Status extends OrphanElement
{
    private static Status defaultStatus;

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
        if(StringTool.nameIsValid(name))
        {
            status = new Status(name, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("Status.create:: %s created", status));
        }
        return status;
    }

    public static void setDefault(Status status)
    {
        Status.defaultStatus = status;
        Log.d(Constants.LOG_TAG, String.format("Status.setDefault:: set %s as default status", status));
    }

    public static Status getDefault()
    {
        if(Status.defaultStatus == null)
        {
            Status.createAndSetDefault();
        }
        return Status.defaultStatus;
    }

    public static void createAndSetDefault()
    {
        Status.setDefault(new Status(App.getContext().getString(R.string.name_default_status), UUID.randomUUID()));
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_IS_DEFAULT, this.equals(Status.getDefault()));

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
