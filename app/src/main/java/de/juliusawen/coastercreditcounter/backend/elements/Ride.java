package de.juliusawen.coastercreditcounter.backend.elements;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.JsonTool;

public class Ride extends Element
{
    private final Calendar calendar;

    private Ride(String name, UUID uuid, Calendar calendar)
    {
        super(name, uuid);
        this.calendar = calendar;
    }

    public static Ride create(int hour, int minute, UUID uuid)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        return Ride.create(calendar, uuid);
    }

    public static Ride create(Calendar calendar, UUID uuid)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT_TIME_PATTERN, Locale.getDefault());
        Ride ride = new Ride(simpleDateFormat.format(calendar.getTime()), uuid == null ? UUID.randomUUID() : uuid, calendar);

        Log.v(Constants.LOG_TAG,  String.format("Ride.create:: %s created.", ride.getFullName()));

        return ride;
    }

    @Override
    @NonNull
    public String toString()
    {
        if(this.getParent() != null)
        {
            return String.format(Locale.getDefault(), "[%s \"%s\" on %s]", this.getClass().getSimpleName(), this.getName(), this.getParent().getName());
        }
        else
        {
            return super.toString();
        }
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);

            jsonObject.put(Constants.JSON_STRING_HOUR, this.calendar.get(Calendar.HOUR_OF_DAY));
            jsonObject.put(Constants.JSON_STRING_MINUTE, this.calendar.get(Calendar.MINUTE));

            Log.v(Constants.LOG_TAG, String.format("Ride.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Ride.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }

    public void setRideTime(int hour, int minute)
    {
        this.calendar.set(Calendar.HOUR_OF_DAY, hour);
        this.calendar.set(Calendar.MINUTE, minute);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT_TIME_PATTERN, Locale.getDefault());
        String time = simpleDateFormat.format(this.calendar.getTime());

        Log.d(Constants.LOG_TAG, String.format("Ride.setTime:: set time for %s to [%s] - changing name...", this, time));

        super.setName(time);
    }

    public void setRideDate(Calendar calendar)
    {
        this.calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        this.calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        this.calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT_DATE_PATTERN, Locale.getDefault());
        String date = simpleDateFormat.format(this.calendar.getTime());

        Log.d(Constants.LOG_TAG, String.format("Ride.setDate:: set date for %s to [%s]", this, date));
    }
}
