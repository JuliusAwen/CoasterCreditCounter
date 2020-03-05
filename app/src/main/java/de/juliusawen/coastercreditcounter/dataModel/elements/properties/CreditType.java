package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

public final class CreditType extends Element implements IProperty
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

    public static CreditType getDefault()
    {
        if(CreditType.defaultCreditType == null)
        {
            Log.w(Constants.LOG_TAG, "CreditType.getDefault:: no default set - creating default");
            CreditType.setDefault(CreditType.create(App.getContext().getString(R.string.default_credit_type_name)));
        }
        return CreditType.defaultCreditType;
    }

    public static void setDefault(CreditType defaultCreditType)
    {
        CreditType.defaultCreditType = defaultCreditType;
        Log.i(Constants.LOG_TAG, String.format("CreditType.setDefault:: %s set as default", defaultCreditType));
    }

    public boolean isDefault()
    {
        return CreditType.getDefault().equals(this);
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
