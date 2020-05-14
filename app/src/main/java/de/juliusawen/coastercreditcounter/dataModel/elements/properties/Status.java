package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.tools.JsonTool;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

/**
 *      Parent: none<br>
 *      Children: Attractions (just for convenience: children are shown in ManageProperty - but are not really used otherwise)<br>
 */
public final class Status extends Element implements IProperty
{
    private static Status defaultStatus;

    private Status(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Status create(String name)
    {
        return Status.create(name, null);
    }

    public static Status create(String name, UUID uuid)
    {
        Status status = null;
        if(Element.isNameValid(name))
        {
            status = new Status(name, uuid);
            Log.d(String.format("%s created", status));
        }

        return status;
    }

    @Override
    public boolean isDefault()
    {
        return Status.getDefault().equals(this);
    }

    public static Status getDefault()
    {
        if(Status.defaultStatus == null)
        {
            Log.w("no default set - creating default");
            Status.setDefault(Status.create((App.getContext().getString(R.string.default_status_name))));
        }

        return Status.defaultStatus;
    }

    public static void setDefault(Status defaultStatus)
    {
        Status.defaultStatus = defaultStatus;
        Log.i(String.format("%s set as default", Status.defaultStatus));
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_IS_DEFAULT, this.isDefault());

            Log.v(String.format("created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(String.format("creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}
