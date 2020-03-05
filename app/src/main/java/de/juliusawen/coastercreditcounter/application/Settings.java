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

public class Settings implements IPersistable
{

    private ArrayList<DetailType> detailsOrder = new ArrayList<>();

    private SortOrder defaultSortOrder;
    private boolean expandLatestYearInListByDefault;
    private int firstDayOfTheWeek;

    private int increment;


    private final Persistence persistence;
    private final String[] dayNames = new DateFormatSymbols().getWeekdays();

    private static Settings instance;

    public static Settings getInstance(Persistence persistence)
    {
        if(Settings.instance == null)
        {
            Settings.instance = new Settings(persistence);
        }
        return instance;
    }

    private Settings(Persistence persistence)
    {
        this.persistence = persistence;
        Log.i(Constants.LOG_TAG,"Settings.Constructor:: <Settings> instantiated");
    }

    public boolean initialize()
    {
        Log.i(Constants.LOG_TAG, "Settings.initialize:: loading Settings...");
        Stopwatch stopwatch = new Stopwatch(true);

        if(persistence.loadSettings(this))
        {
            if(this.validate())
            {
                Visit.setSortOrder(this.getDefaultSortOrder());
                Log.i(Constants.LOG_TAG, String.format("Settings.initialize:: initializing settings successful - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("Settings.initialize:: validation failed - took [%d]ms", stopwatch.stop()));
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("Settings.initialize:: initializing settings failed - took [%d]ms", stopwatch.stop()));
        }

        return false;
    }

    private boolean validate()
    {
        Log.i(Constants.LOG_TAG, "Settings.validate:: validating settings...");

        if(this.getDefaultSortOrder() == null)
        {
            String message = "Settings validation failed: default sort order is null";
            Log.e(Constants.LOG_TAG, String.format("Settings.validate:: %s", message));
            throw  new IllegalStateException(message);
        }
        else
        {
            Log.d(Constants.LOG_TAG, String.format("Settings.validate:: default sort order is [%s]", this.getDefaultSortOrder()));
        }

        Log.i(Constants.LOG_TAG, String.format("Settings.validate:: expand latest year in visits list [%S]", this.expandLatestYearInListByDefault()));

        Log.i(Constants.LOG_TAG, String.format("Settings.validate:: first day of the week is [%s]", this.dayNames[this.getFirstDayOfTheWeek()]));

        Log.i(Constants.LOG_TAG, String.format("Settings.validate:: default increment is [%d]", this.getIncrement()));


        Log.i(Constants.LOG_TAG, "Settings.validate:: settings validation successful");
        return true;
    }

    public void useDefaults()
    {
        Log.i(Constants.LOG_TAG, "Settings.useDefaults:: setting defaults...");

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
        Log.i(Constants.LOG_TAG, String.format("Settings.setDetailsOrder:: [%d] details set in order", detailsOrder.size()));
    }

    public SortOrder getDefaultSortOrder()
    {
        return defaultSortOrder;
    }

    public void setDefaultSortOrder(SortOrder defaultSortOrder)
    {
        this.defaultSortOrder = defaultSortOrder;
        Log.i(Constants.LOG_TAG, String.format("Settings.setDefaultSortOrder:: [%s] set as default", defaultSortOrder));
    }

    public boolean expandLatestYearInListByDefault()
    {
        return expandLatestYearInListByDefault;
    }

    public void setExpandLatestYearInListByDefault(boolean expandLatestYearInListByDefault)
    {
        this.expandLatestYearInListByDefault = expandLatestYearInListByDefault;
        Log.i(Constants.LOG_TAG, String.format("Settings.setExpandLatestYearInListByDefault:: set to [%S]", expandLatestYearInListByDefault));
    }

    public int getFirstDayOfTheWeek()
    {
        return this.firstDayOfTheWeek;
    }

    public void setFirstDayOfTheWeek(int firstDayOfTheWeek)
    {
        this.firstDayOfTheWeek = firstDayOfTheWeek;
        Log.i(Constants.LOG_TAG, String.format("Settings.setFirstDayOfTheWeek:: set to [%s]", this.dayNames[firstDayOfTheWeek]));
    }

    public int getIncrement()
    {
        return this.increment;
    }

    public void setIncrement(int increment)
    {
        this.increment = increment;
        Log.i(Constants.LOG_TAG, String.format("Settings.setIncrement:: [%d] set as default", increment));
    }

    public JSONObject toJson()
    {
        Log.d(Constants.LOG_TAG, ("Settings.toJson:: creating json object from settings..."));

        try
        {
            JSONObject jsonObjectSettings = new JSONObject();

            JSONArray jsonArrayDetailTypeOrder = new JSONArray();
            for(DetailType detailType : this.getDetailsOrder())
            {
                jsonArrayDetailTypeOrder.put(detailType.ordinal());
            }
            jsonObjectSettings.put(Constants.JSON_STRING_DETAIL_ORDER, jsonArrayDetailTypeOrder);

            jsonObjectSettings.put(Constants.JSON_STRING_DEFAULT_SORT_ORDER, this.getDefaultSortOrder().ordinal());
            jsonObjectSettings.put(Constants.JSON_STRING_EXPAND_LATEST_YEAR_HEADER, this.expandLatestYearInListByDefault());
            jsonObjectSettings.put(Constants.JSON_STRING_FIRST_DAY_OF_THE_WEEK, this.getFirstDayOfTheWeek());
            jsonObjectSettings.put(Constants.JSON_STRING_INCREMENT, this.getIncrement());

            return jsonObjectSettings;
        }
        catch(JSONException e)
        {
            e.printStackTrace();

            Log.e(Constants.LOG_TAG, String.format("Element.toJson:: failed with JSONException [%s]", e.getMessage()));
            return null;
        }
    }
}
