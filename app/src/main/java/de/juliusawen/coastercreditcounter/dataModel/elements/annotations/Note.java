package de.juliusawen.coastercreditcounter.dataModel.elements.annotations;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

/**
 *  Parent: Park, Visit, Attraction or Event
 *  Children: none
 */
public class Note extends Element implements IElement, IPersistable
{
    String text;

    private Note(String name, String text, UUID uuid)
    {
        super(name, uuid);
        this.text = text;
    }

    public static Note create(String text)
    {
        return Note.create(text, UUID.randomUUID());
    }

    public static Note create(String text, UUID uuid)
    {
        Note note = null;
        String name = Note.getShortenedText(text).replaceAll("\\n", " ");
        if(Element.isNameValid(name))
        {
            note = new Note(name, text, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("Note.create:: %s created", note));
        }
        return note;
    }

    private static String getShortenedText(String text)
    {
        return text.length() > App.config.maxCharacterCountForShortenedText
                ? String.format("%s%s", text.substring(0, App.config.maxCharacterCountForShortenedText), "...")
                : text;
    }

    public String getText()
    {
        return this.text;
    }

    public boolean setTextAndAdjustName(String text)
    {
        this.text = text;
        return super.setName(Note.getShortenedText(this.text));
    }

    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_TEXT, this.getText());

            Log.v(Constants.LOG_TAG, String.format("Note.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Note.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}
