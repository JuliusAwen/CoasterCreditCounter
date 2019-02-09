package de.juliusawen.coastercreditcounter.backend.objects.elements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.NonNull;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class Ride extends Element
{
    private Ride(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Ride create(int year, int month, int day, int hour, int minute, int second, UUID uuid)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);

        return Ride.create(calendar, uuid);
    }

    public static Ride create(Calendar calendar, UUID uuid)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT_DATETIME_PATTERN, Locale.getDefault());
        Ride ride = new Ride(simpleDateFormat.format(calendar.getTime()), uuid == null ? UUID.randomUUID() : uuid);

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
        return null;
    }
}
