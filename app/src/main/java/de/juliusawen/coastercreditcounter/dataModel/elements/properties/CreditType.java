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
 *      Children: Attractions<br>
 */
public final class CreditType extends Element implements IProperty
{
    private static CreditType defaultCreditType;
    private CreditType(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static CreditType create(String name)
    {
        return CreditType.create(name, null);
    }

    public static CreditType create(String name, UUID uuid)
    {
        CreditType creditType = null;
        if(Element.isNameValid(name))
        {
            creditType = new CreditType(name, uuid);
            Log.d(String.format("%s created", creditType));
        }

        return creditType;
    }

    @Override
    public boolean isDefault()
    {
        return CreditType.getDefault().equals(this);
    }

    public static CreditType getDefault()
    {
        if(CreditType.defaultCreditType == null)
        {
            Log.w("no default set - creating default");
            CreditType.setDefault(CreditType.create(App.getContext().getString(R.string.default_credit_type_name)));
        }

        return CreditType.defaultCreditType;
    }

    public static void setDefault(CreditType defaultCreditType)
    {
        CreditType.defaultCreditType = defaultCreditType;
        Log.i(String.format("%s set as default", CreditType.defaultCreditType));
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
