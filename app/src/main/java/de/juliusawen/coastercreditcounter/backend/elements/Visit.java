package de.juliusawen.coastercreditcounter.backend.elements;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.backend.temporaryElements.VisitedAttraction;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.toolbox.JsonTool;

/**
 * Parent: Park
 * Children: VisitedAttraction
 */
public class Visit extends Element
{
    private static Visit openVisit;
    private static SortOrder sortOrder = SortOrder.DESCENDING;

    private final Calendar calendar;

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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT_DATE_PATTERN, Locale.getDefault());
        Visit visit = new Visit(simpleDateFormat.format(calendar.getTime()), uuid == null ? UUID.randomUUID() : uuid, calendar);

        Log.v(Constants.LOG_TAG,  String.format("Visit.create:: %s created.", visit.getFullName()));
        return visit;
    }

    @Override
    @NonNull
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT_DATE_PATTERN, Locale.getDefault());
        String date = simpleDateFormat.format(this.calendar.getTime());

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
        for(VisitedAttraction visitedAttraction : this.getChildrenAsType(VisitedAttraction.class))
        {
            visitedAttraction.deleteElementAndDescendants();
        }
        super.deleteElement();
    }

    private boolean isOpenVisit()
    {
        return Visit.getOpenVisit() != null && this.equals(Visit.getOpenVisit());
    }





    public static Element getOpenVisit()
    {
        return Visit.openVisit;
    }

    public static void setOpenVisit(Element visit)
    {
        if(visit != null)
        {
            Log.i(Constants.LOG_TAG, String.format("Visit.setOpenVisit:: %s set as open visit", visit));
            Visit.openVisit = (Visit) visit;
        }
        else
        {
            Log.i(Constants.LOG_TAG,"Visit.setOpenVisit:: open visit cleared");
            Visit.openVisit = null;
        }
    }

    public static boolean validateOpenVisit()
    {
        if(Visit.getOpenVisit() != null)
        {
            Calendar calendar = Calendar.getInstance();
            Calendar visitCalender = Visit.openVisit.getCalendar();

            if(Visit.isSameDay(calendar, visitCalender))
            {
                return true;
            }
            else
            {
                Visit.setOpenVisit(null);
            }
        }
        return false;
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
}
