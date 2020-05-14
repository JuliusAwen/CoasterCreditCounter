package de.juliusawen.coastercreditcounter.dataModel.elements.annotations;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;
import de.juliusawen.coastercreditcounter.tools.JsonTool;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

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
        return Note.create(text, null);
    }

    public static Note create(String text, UUID uuid)
    {
        Note note = null;
        String name = Note.buildName(text);
        if(Element.isNameValid(name))
        {
            note = new Note(name, text, uuid);
            Log.d(String.format("%s created", note.getFullName()));
        }

        return note;
    }

    private static String buildName(String text)
    {
        String firstRow = text.contains("\n")
                ? text.substring(0, text.indexOf("\n"))
                : text;

        String name = firstRow.length() > App.config.maxCharacterCountForShortenedText
                ? String.format("%s%s", firstRow.substring(0, App.config.maxCharacterCountForShortenedText), "...")
                : firstRow;

        Log.v(String.format("built name [%s] from text [%s]", name, text));

        return name;
    }

    public boolean setTextAndAdjustName(String text)
    {
        this.text = text;
        return super.setName(Note.buildName(this.text));
    }

    public String getText()
    {
        return this.text;
    }

    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_NOTE_TEXT, this.getText());

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
