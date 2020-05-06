package de.juliusawen.coastercreditcounter.dataModel.elements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

/**
 * Parent: Location
 * Children: Locations, Parks
 */
public final class Location extends Element implements IPersistable
{
    private Location(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Location create(String name)
    {
        return Location.create(name, null);
    }

    public static Location create(String name, UUID uuid)
    {
        Location location = null;
        if(Element.isNameValid(name))
        {
            location = new Location(name, uuid);
            Log.v(Constants.LOG_TAG,  String.format("Location.create:: %s created.", location.getFullName()));
        }

        return location;
    }

    public Location getRootLocation()
    {
        if(!this.isRootLocation())
        {
            Log.v(Constants.LOG_TAG,  String.format("Location.getRootLocation:: %s is not root location - calling parent", this));
            return ((Location)super.getParent()).getRootLocation();
        }
        else
        {
            return this;
        }
    }

    public boolean isRootLocation()
    {
        return this.getParent() == null;
    }

    @Override
    public void addChildAtIndexAndSetParent(int index, IElement child)
    {
        super.addChildAtIndexAndSetParent(index, child);
        this.sortChildTypes();
    }

    private void sortChildTypes()
    {
        Log.d(Constants.LOG_TAG,  String.format("Location.sortChildTypes:: sorting child types - parks to the top[%S] according to App.Preferences",
                App.preferences.sortParksToTopOfLocationsChildren()));

        List<IElement> sortedChildren = new LinkedList<>();
        if(App.preferences.sortParksToTopOfLocationsChildren())
        {
            sortedChildren.addAll(this.getChildrenOfType(Park.class));
            sortedChildren.addAll(this.getChildrenOfType(Location.class));
        }
        else
        {
            sortedChildren.addAll(this.getChildrenOfType(Location.class));
            sortedChildren.addAll(this.getChildrenOfType(Park.class));
        }

        this.reorderChildren(sortedChildren);
    }

    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            JsonTool.putChildren(jsonObject, this);

            Log.v(Constants.LOG_TAG, String.format("Location.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Location.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}

