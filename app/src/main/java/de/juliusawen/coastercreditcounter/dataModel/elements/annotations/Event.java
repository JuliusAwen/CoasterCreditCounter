package de.juliusawen.coastercreditcounter.dataModel.elements.annotations;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;
import de.juliusawen.coastercreditcounter.tools.JsonTool;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

/**
 *  Parent: Park, Visit or Attraction
 *  Child: Note (must have)
 */
public class Event extends Element implements IElement, IPersistable, IHasNote
{
    private Calendar calendar;
    private Event(String name, Calendar calendar, Note note, UUID uuid)
    {
        super(name, uuid);
        this.calendar = calendar;
        super.addChildAndSetParent(note);
    }

    public static Event create(int year, int month, int day, Note note)
    {
        return Event.create(year, month, day, note, null);
    }

    public static Event create(int year, int month, int day, Note note, UUID uuid)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return Event.create(calendar, note, uuid);
    }

    public static Event create(Calendar calendar, Note note)
    {
        return Event.create(calendar, note, null);
    }

    public static Event create(Calendar calendar, Note note, UUID uuid)
    {
        Event event = new Event(StringTool.fetchSimpleDate(calendar), calendar, note, uuid);

        Log.d(String.format("%s created", event.getFullName()));
        return event;
    }

    @Override
    public String getFullName()
    {
        return String.format(Locale.getDefault(),
                "[%s \"%s\" Note[%s] (%s)]",
                this.getClass().getSimpleName(),
                this.getName(),
                this.getNote(),
                this.getUuid()
        );
    }

    public Calendar getCalendar()
    {
        return this.calendar;
    }

    public Note getNote()
    {
        return super.getChildrenAsType(Note.class).get(0);
    }

    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);

            jsonObject.put(Constants.JSON_STRING_DAY, this.getCalendar().get(Calendar.DAY_OF_MONTH));
            jsonObject.put(Constants.JSON_STRING_MONTH, this.getCalendar().get(Calendar.MONTH));
            jsonObject.put(Constants.JSON_STRING_YEAR, this.getCalendar().get(Calendar.YEAR));

            jsonObject.put(Constants.JSON_STRING_NOTE, this.getNote().toJson());

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
