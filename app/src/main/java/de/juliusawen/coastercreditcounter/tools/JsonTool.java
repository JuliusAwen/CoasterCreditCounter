package de.juliusawen.coastercreditcounter.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public abstract class JsonTool
{
    public static void putNameAndUuid(JSONObject jsonObject, IElement element) throws JSONException
    {
        try
        {
            jsonObject.put(Constants.JSON_STRING_NAME, element.getName());
            jsonObject.put(Constants.JSON_STRING_UUID, element.getUuid().toString());
        }
        catch(JSONException e)
        {
            e.printStackTrace();

            Log.e(String.format("creation for %s failed with JSONException [%s]", element, e.getMessage()));
            throw e;
        }
    }

    public static void putChildren(JSONObject jsonObject, IElement element) throws JSONException
    {
        try
        {
            JSONArray jsonArrayChildren = new JSONArray();

            if(element.getChildren().isEmpty())
            {
                jsonObject.put(Constants.JSON_STRING_CHILDREN, JSONObject.NULL);
            }
            else
            {
                for(IElement child : element.getChildren())
                {
                    jsonArrayChildren.put(child.getUuid().toString());
                }

                jsonObject.put(Constants.JSON_STRING_CHILDREN, jsonArrayChildren);
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();

            Log.e(String.format("creation for %s failed with JSONException [%s]", element, e.getMessage()));
            throw e;
        }
        catch(Exception e)
        {
            e.printStackTrace();

            Log.e(String.format("creation for %s failed with Exception [%s]", element, e.getMessage()));
            throw e;
        }
    }
}
