package de.juliusawen.coastercreditcounter.dataModel.elements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.IHasNote;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.Note;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

/***
 * Parent: Location<br>
 * Children: OnSiteAttractions, Visits, Note<br>
 */
public final class Park extends Element implements IHasNote, IPersistable
{
    private Note note;

    private Park(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Park create(String name)
    {
        return Park.create(name, null);
    }

    public static Park create(String name, UUID uuid)
    {
        Park park = null;
        if(Element.isNameValid(name))
        {
            park = new Park(name, uuid);
            Log.v(Constants.LOG_TAG,  String.format("Park.create:: %s created", park.getFullName()));
        }

        return park;
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
            Log.v(Constants.LOG_TAG, String.format("Park.deleteChild:: setting private field %s on %s to NULL", this.note, this));
            this.note = null;
        }

        super.deleteChild(child);
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
