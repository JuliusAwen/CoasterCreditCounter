package de.juliusawen.coastercreditcounter.application;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;
import de.juliusawen.coastercreditcounter.persistence.Persistence;
import de.juliusawen.coastercreditcounter.tools.Stopwatch;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailType;

public class Preferences implements IPersistable
{
    private ArrayList<DetailType> detailsOrder = new ArrayList<>();

    private SortOrder defaultSortOrder;
    private boolean expandLatestYearInListByDefault;
    private int firstDayOfTheWeek;

    private int increment;


    private final Persistence persistence;
    private final String[] dayNames = new DateFormatSymbols().getWeekdays();

    private static Preferences instance;

    public static Preferences getInstance(Persistence persistence)
    {
        if(Preferences.instance == null)
        {
            Preferences.instance = new Preferences(persistence);
        }
        return instance;
    }

    private Preferences(Persistence persistence)
    {
        this.persistence = persistence;
        Log.i(Constants.LOG_TAG,"Preferences.Constructor:: <Preferences> instantiated");
    }

    public boolean initialize()
    {
        Log.i(Constants.LOG_TAG, "Preferences.initialize:: loading preferences...");
        Stopwatch stopwatch = new Stopwatch(true);

        if(persistence.loadPreferences(this))
        {
            if(this.validate())
            {
                Visit.setSortOrder(this.getDefaultSortOrder());
                Log.i(Constants.LOG_TAG, String.format("Preferences.initialize:: initializing preferences successful - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("Preferences.initialize:: validation failed - took [%d]ms", stopwatch.stop()));
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("Preferences.initialize:: initializing preferences failed - took [%d]ms", stopwatch.stop()));
        }

        return false;
    }

    private boolean validate()
    {
        Log.i(Constants.LOG_TAG, "Preferences.validate:: validating preferences...");

        if(this.getDefaultSortOrder() == null)
        {
            String message = "Preferences validation failed: default sort order is null";
            Log.e(Constants.LOG_TAG, String.format("Preferences.validate:: %s", message));
            throw  new IllegalStateException(message);
        }
        else
        {
            Log.d(Constants.LOG_TAG, String.format("Preferences.validate:: default sort order is [%s]", this.getDefaultSortOrder()));
        }

        Log.i(Constants.LOG_TAG, String.format("Preferences.validate:: expand latest year in visits list [%S]", this.expandLatestYearInListByDefault()));

        Log.i(Constants.LOG_TAG, String.format("Preferences.validate:: first day of the week is [%s]", this.dayNames[this.getFirstDayOfTheWeek()]));

        Log.i(Constants.LOG_TAG, String.format("Preferences.validate:: default increment is [%d]", this.getIncrement()));


        Log.i(Constants.LOG_TAG, "Preferences.validate:: validation successful");
        return true;
    }

    public void useDefaults()
    {
        Log.i(Constants.LOG_TAG, "Preferences.useDefaults:: setting defaults...");

        this.setDefaultSortOrder(SortOrder.DESCENDING);
        this.setExpandLatestYearInListByDefault(true);
        this.setFirstDayOfTheWeek(Calendar.MONDAY);
        this.setIncrement(1);
        this.setDetailsOrder(this.getDefaultDetailsOrder());
    }

    private ArrayList<DetailType> getDefaultDetailsOrder()
    {
        ArrayList<DetailType> defaultDetailsOrder = new ArrayList<>();
        defaultDetailsOrder.add(DetailType.LOCATION);
        defaultDetailsOrder.add(DetailType.CATEGORY);
        defaultDetailsOrder.add(DetailType.MANUFACTURER);
        // defaultDetailsOrder.add(DetailType.Model); //Todo: comment in as soon as models exist
        defaultDetailsOrder.add(DetailType.CREDIT_TYPE);
        defaultDetailsOrder.add(DetailType.STATUS);
        defaultDetailsOrder.add(DetailType.TOTAL_RIDE_COUNT);
        return defaultDetailsOrder;
    }

    public List<DetailType> getDetailsOrder()
    {
        return this.detailsOrder;
    }

    public void setDetailsOrder(ArrayList<DetailType> detailsOrder)
    {
        this.detailsOrder = detailsOrder;
        Log.i(Constants.LOG_TAG, String.format("Preferences.setDetailsOrder:: [%d] details set in order", detailsOrder.size()));
    }

    public SortOrder getDefaultSortOrder()
    {
        return defaultSortOrder;
    }

    public void setDefaultSortOrder(SortOrder defaultSortOrder)
    {
        this.defaultSortOrder = defaultSortOrder;
        Log.i(Constants.LOG_TAG, String.format("Preferences.setDefaultSortOrder:: [%s] set as default", defaultSortOrder));
    }

    public boolean expandLatestYearInListByDefault()
    {
        return expandLatestYearInListByDefault;
    }

    public void setExpandLatestYearInListByDefault(boolean expandLatestYearInListByDefault)
    {
        this.expandLatestYearInListByDefault = expandLatestYearInListByDefault;
        Log.i(Constants.LOG_TAG, String.format("Preferences.setExpandLatestYearInListByDefault:: set to [%S]", expandLatestYearInListByDefault));
    }

    public int getFirstDayOfTheWeek()
    {
        return this.firstDayOfTheWeek;
    }

    public void setFirstDayOfTheWeek(int firstDayOfTheWeek)
    {
        this.firstDayOfTheWeek = firstDayOfTheWeek;
        Log.i(Constants.LOG_TAG, String.format("Preferences.setFirstDayOfTheWeek:: set to [%s]", this.dayNames[firstDayOfTheWeek]));
    }

    public int getIncrement()
    {
        return this.increment;
    }

    public void setIncrement(int increment)
    {
        this.increment = increment;
        Log.i(Constants.LOG_TAG, String.format("Preferences.setIncrement:: [%d] set as default", increment));
    }

    public JSONObject toJson()
    {
        Log.d(Constants.LOG_TAG, ("Preferences.toJson:: creating json object from preferences..."));

        try
        {
            JSONObject jsonObjectPreferences = new JSONObject();

            JSONArray jsonArrayDetailTypeOrder = new JSONArray();
            for(DetailType detailType : this.getDetailsOrder())
            {
                jsonArrayDetailTypeOrder.put(detailType.ordinal());
            }
            jsonObjectPreferences.put(Constants.JSON_STRING_DETAIL_ORDER, jsonArrayDetailTypeOrder);

            jsonObjectPreferences.put(Constants.JSON_STRING_DEFAULT_SORT_ORDER, this.getDefaultSortOrder().ordinal());
            jsonObjectPreferences.put(Constants.JSON_STRING_EXPAND_LATEST_YEAR_HEADER, this.expandLatestYearInListByDefault());
            jsonObjectPreferences.put(Constants.JSON_STRING_FIRST_DAY_OF_THE_WEEK, this.getFirstDayOfTheWeek());
            jsonObjectPreferences.put(Constants.JSON_STRING_INCREMENT, this.getIncrement());

            return jsonObjectPreferences;
        }
        catch(JSONException e)
        {
            e.printStackTrace();

            Log.e(Constants.LOG_TAG, String.format("Preferences.toJson:: failed with JSONException [%s]", e.getMessage()));
            return null;
        }
    }
}