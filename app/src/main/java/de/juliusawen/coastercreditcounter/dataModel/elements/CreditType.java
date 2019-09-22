package de.juliusawen.coastercreditcounter.dataModel.elements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.interfaces.IPersistable;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

public class CreditType extends OrphanElement implements IPersistable
{
    private static CreditType defaultCreditType;

    private CreditType(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static CreditType create(String name)
    {
        return CreditType.create(name, UUID.randomUUID());
    }

    public static CreditType create(String name, UUID uuid)
    {
        CreditType creditType = null;
        if(Element.nameIsValid(name))
        {
            creditType = new CreditType(name, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("CreditType.create:: %s created.", creditType));
        }
        return creditType;
    }

    public static void setDefault(CreditType creditType)
    {
        CreditType.defaultCreditType = creditType;
        Log.d(Constants.LOG_TAG, String.format("CreditType.setDefault:: set %s as default CreditType", creditType));
    }

    public static CreditType getDefault()
    {
        if(CreditType.defaultCreditType == null)
        {
            CreditType.createAndSetDefault();
        }
        return CreditType.defaultCreditType;
    }

    public static void createAndSetDefault()
    {
        CreditType.setDefault(new CreditType(App.getContext().getString(R.string.name_default_credit_type), UUID.randomUUID()));
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_IS_DEFAULT, this.equals(CreditType.getDefault()));

            Log.v(Constants.LOG_TAG, String.format("CreditType.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("CreditType.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}
