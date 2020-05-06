package de.juliusawen.coastercreditcounter.dataModel.elements.attractions;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.IHasNote;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.Note;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

/**
 * Individual attraction located at a particular park<br>
 * <br>
 * Parent: Park<br>
 * Children: Note<br>
 */
public final class OnSiteAttraction extends Attraction implements IAttraction, IPersistable, IHasNote
{
    private Note note;

    private OnSiteAttraction(String name, int untrackedRideCount, UUID uuid)
    {
        super(name, untrackedRideCount, uuid);
    }

    public static OnSiteAttraction create(String name)
    {
        return OnSiteAttraction.create(name, 0);
    }

    public static OnSiteAttraction create(String name, int untrackedRideCount)
    {
        return OnSiteAttraction.create(name, untrackedRideCount, null);
    }

    public static OnSiteAttraction create(String name, int untrackedRideCount, UUID uuid)
    {
        OnSiteAttraction onSiteAttraction = null;
        if(Element.isNameValid(name))
        {
            onSiteAttraction = new OnSiteAttraction(name, untrackedRideCount, uuid);
            Log.v(Constants.LOG_TAG,  String.format("OnSiteAttraction.create:: %s created", onSiteAttraction.getFullName()));
        }

        return onSiteAttraction;
    }

    @Override
    public Note getNote()
    {
        if(this.note == null && this.hasChildrenOfType(Note.class))
        {
            this.note = this.getChildrenAsType(Note.class).get(0);
        }
        return this.note;
    }

    @Override
    public void deleteChild(IElement child)
    {
        if(child.equals(this.note))
        {
            Log.v(Constants.LOG_TAG, String.format("OnSiteAttraction.deleteChild:: setting private field %s on %s to NULL", this.note, this));
            this.note = null;
        }

        super.deleteChild(child);
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            JsonTool.putChildren(jsonObject, this);

            jsonObject.put(Constants.JSON_STRING_CREDIT_TYPE, this.getCreditType().getUuid());
            jsonObject.put(Constants.JSON_STRING_CATEGORY, this.getCategory().getUuid());
            jsonObject.put(Constants.JSON_STRING_MANUFACTURER, this.getManufacturer().getUuid());
            jsonObject.put(Constants.JSON_STRING_MODEL, this.getModel().getUuid());
            jsonObject.put(Constants.JSON_STRING_STATUS, this.getStatus().getUuid());
            jsonObject.put(Constants.JSON_STRING_UNTRACKED_RIDE_COUNT, this.getUntracktedRideCount());

            Log.v(Constants.LOG_TAG, String.format("Attraction.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Attraction.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}
