package de.juliusawen.coastercreditcounter.dataModel.elements;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;
import de.juliusawen.coastercreditcounter.tools.JsonTool;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

/**
 * Parent: Park<br>
 * Children: VisitedAttractions<br>
 */
public final class Visit extends Element implements IPersistable
{
    private static SortOrder sortOrder = SortOrder.DESCENDING;
    private final Calendar calendar;

    private boolean isEditingEnabled;

    private Visit(String name, UUID uuid, Calendar calendar)
    {
        super(name, uuid);
        this.calendar = calendar;
    }

    public static Visit create(int year, int month, int day)
    {
        return Visit.create(year, month, day, null);
    }

    public static Visit create(int year, int month, int day, UUID uuid)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return Visit.create(calendar, uuid);
    }

    public static Visit create(Calendar calendar)
    {
        return Visit.create(calendar, null);
    }

    public static Visit create(Calendar calendar, UUID uuid)
    {
        Visit visit = new Visit(StringTool.fetchSimpleDate(calendar), uuid, calendar);
        Log.d(String.format("%s created", visit.getFullName()));

        return visit;
    }

    @Override
    public String toString()
    {
        return String.format(Locale.getDefault(), "[%s \"%s\" @ %s]", this.getClass().getSimpleName(), this.getName(), this.getParent() != null ? this.getParent().getName() : "no parent");
    }

    @Override
    public void deleteElementAndDescendants()
    {
        for(VisitedAttraction visitedAttraction : this.getChildrenAsType(VisitedAttraction.class))
        {
            visitedAttraction.deleteElementAndDescendants();
        }
        super.delete();
    }

    public Calendar getCalendar()
    {
        return this.calendar;
    }

    public void setDateAndAdjustName(int year, int month, int day)
    {
        this.calendar.set(year, month, day);
        this.setName();
    }

    public void setDateAndAdjustName(Calendar calendar)
    {
        this.calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        this.setName();
    }

    private void setName()
    {
        String date = StringTool.fetchSimpleDate(this.calendar);

        Log.d(String.format("set date for %s to [%s] - changing name...", this, date));

        super.setName(date);
    }

    public static boolean isCurrentVisit(Visit visit)
    {
        return Visit.isSameDay(Calendar.getInstance(), visit.getCalendar());
    }

    public static boolean isSameDay(Calendar calendar, Calendar compareCalendar)
    {
        return calendar.get(Calendar.YEAR) == compareCalendar.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == compareCalendar.get(Calendar.DAY_OF_YEAR);
    }

    public static SortOrder getSortOrder()
    {
        return Visit.sortOrder;
    }

    public static void setSortOrder(SortOrder sortOrder)
    {
        Log.d(String.format("sort order set to [%s]", sortOrder.toString()));
        Visit.sortOrder = sortOrder;
    }

    public static List<IElement> fetchVisitsForYearAndDay(Calendar calendar, List<Visit> visits)
    {
        List<IElement> foundVisits = new ArrayList<>();

        for(Visit visit : visits)
        {
            if(Visit.isSameDay(visit.getCalendar(), calendar))
            {
                Log.i(String.format("found %s for [%s]", visit, StringTool.fetchSimpleDate(calendar)));
                foundVisits.add(visit);
            }
        }

        Log.i(String.format(Locale.getDefault(), "[%d] visits found for [%s]", foundVisits.size(), StringTool.fetchSimpleDate(calendar)));
        return foundVisits;
    }

    public void setEditingEnabled(boolean enabled)
    {
        Log.i(String.format("%s editing enabled set to [%s]", this, enabled));
        this.isEditingEnabled = enabled;
    }

    public boolean isEditingEnabled()
    {
        return this.isEditingEnabled;
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

            boolean hasVisitedAttractions = false;
            JSONArray jsonArrayRideCountsByAttraction = new JSONArray();
            for(VisitedAttraction visitedAttraction : this.getChildrenAsType(VisitedAttraction.class))
            {
                JSONObject jsonObjectRideCountByAttraction = visitedAttraction.toJson();
                jsonArrayRideCountsByAttraction.put(jsonObjectRideCountByAttraction);
                hasVisitedAttractions = true;
            }
            jsonObject.put(Constants.JSON_STRING_RIDE_COUNTS_BY_ATTRACTION, hasVisitedAttractions ? jsonArrayRideCountsByAttraction : JSONObject.NULL);

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
