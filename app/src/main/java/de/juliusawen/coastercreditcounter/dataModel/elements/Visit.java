package de.juliusawen.coastercreditcounter.dataModel.elements;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.dataModel.temporaryElements.VisitedAttraction;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.tools.JsonTool;
import de.juliusawen.coastercreditcounter.tools.StringTool;

/**
 * Parent: Park
 * Children: VisitedAttraction
 */
public class Visit extends Element
{
    private static final List<Visit> currentVisits = new ArrayList<>();
    private static SortOrder sortOrder = SortOrder.DESCENDING;
    private final Calendar calendar;

    private boolean isEditingEnabled;

    private Visit(String name, UUID uuid, Calendar calendar)
    {
        super(name, uuid);
        this.calendar = calendar;
    }

    public static Visit create(int year, int month, int day, UUID uuid)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return Visit.create(calendar, uuid);
    }

    public static Visit create(Calendar calendar, UUID uuid)
    {
        Visit visit = new Visit(StringTool.fetchSimpleDate(calendar), uuid == null ? UUID.randomUUID() : uuid, calendar);

        Log.v(Constants.LOG_TAG,  String.format("Visit.show:: %s created.", visit.getFullName()));
        return visit;
    }

    @Override
    public String toString()
    {
        if(this.getParent() != null)
        {
            return String.format(Locale.getDefault(), "[%s \"%s\" @ %s]", this.getClass().getSimpleName(), this.getName(), this.getParent().getName());
        }
        else
        {
            return super.toString();
        }
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
            JSONArray jsonArrayRidesByAttractions = new JSONArray();
            for(VisitedAttraction visitedAttraction : this.getChildrenAsType(VisitedAttraction.class))
            {
                JSONObject jsonObjectRideCountByAttraction = visitedAttraction.toJson();
                jsonArrayRidesByAttractions.put(jsonObjectRideCountByAttraction);
                hasVisitedAttractions = true;
            }
            jsonObject.put(Constants.JSON_STRING_RIDES_BY_ATTRACTIONS, hasVisitedAttractions ? jsonArrayRidesByAttractions : JSONObject.NULL);

            Log.v(Constants.LOG_TAG, String.format("Visit.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Visit.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
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
        this.setDateInRides(calendar);
    }

    private void setName()
    {
        String date = StringTool.fetchSimpleDate(this.calendar);

        Log.d(Constants.LOG_TAG, String.format("Visit.setName:: set date for %s to [%s] - changing name...", this, date));

        super.setName(date);
    }

    private void setDateInRides(Calendar calendar)
    {
        for(VisitedAttraction visitedAttraction : this.getChildrenAsType(VisitedAttraction.class))
        {
            for(Ride ride : visitedAttraction.getChildrenAsType(Ride.class))
            {
                ride.setRideDate(calendar);
            }
        }
    }

    @Override
    public void deleteElementAndDescendants()
    {

        if(Visit.isCurrentVisit(this))
        {
            Visit.currentVisits.remove(this);
            Log.i(Constants.LOG_TAG, String.format("Visit.deleteElementAndDescendands:: %s removed from current visits", this));
        }

        for(VisitedAttraction visitedAttraction : this.getChildrenAsType(VisitedAttraction.class))
        {
            visitedAttraction.deleteElementAndDescendants();
        }
        super.deleteElement();
    }

    public static List<Visit> getCurrentVisits()
    {
        return Visit.currentVisits;
    }

    public static void addCurrentVisit(Visit visit)
    {
        if(visit != null)
        {
            Log.i(Constants.LOG_TAG, String.format("Visit.addCurrentVisit:: %s added to current visits", visit));
            visit.setEditingEnabled(true);
            Visit.currentVisits.add(visit);
        }
    }

    public static boolean isCurrentVisit(Visit visit)
    {
        return Visit.currentVisits.contains(visit);
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
        Log.d(Constants.LOG_TAG, String.format("Visit.setSortOrder:: sort order set to [%s]", sortOrder.toString()));
        Visit.sortOrder = sortOrder;
    }

    public static List<Visit> fetchVisitsForYearAndDay(Calendar calendar, List<Visit> visits)
    {
        List<Visit> foundVisits = new ArrayList<>();

        for(Visit visit : visits)
        {
            if(Visit.isSameDay(visit.getCalendar(), calendar))
            {
                Log.d(Constants.LOG_TAG, String.format("Visit.fetchVisitsForYearAndDay:: Found %s for [%s]", visit, StringTool.fetchSimpleDate(calendar)));
                foundVisits.add(visit);
            }
        }

        Log.d(Constants.LOG_TAG, String.format("Visit.fetchVisitsForYearAndDay:: [%d] visits found for [%s]", foundVisits.size(), StringTool.fetchSimpleDate(calendar)));
        return foundVisits;
    }

    public void setEditingEnabled(boolean enabled)
    {
        Log.d(Constants.LOG_TAG, String.format("Visit.setEditingEnabled:: %s editing enabled set to [%s]", this, enabled));
        this.isEditingEnabled = enabled;
    }

    public boolean isEditingEnabled()
    {
        return this.isEditingEnabled;
    }
}
