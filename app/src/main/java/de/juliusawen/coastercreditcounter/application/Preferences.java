package de.juliusawen.coastercreditcounter.application;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;
import de.juliusawen.coastercreditcounter.persistence.Persistence;
import de.juliusawen.coastercreditcounter.tools.Stopwatch;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailType;

@SuppressWarnings("FieldCanBeLocal") // Want this stuff up here for better overview
public class Preferences implements IPersistable
{
    private int firstDayOfTheWeek;

    private int increment;

    private SortOrder defaultSortOrder;
    private ArrayList<DetailType> detailsOrder = new ArrayList<>();

    private boolean expandLatestYearHeaderByDefault;

    private boolean defaultPropertiesAlwaysAtTop = true;
    private boolean sortParksToTopOfLocationsChildren = true;

    private String exportFileName = "CoasterCreditCounterExport.json";

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
        Log.frame(LogLevel.INFO, "instantiated", '#', true);
    }

    @Override
    public String toString()
    {
        return String.format(Locale.getDefault(),
                "App Preferences:\n" +
                        "Default SortOrder is [%s]\n" +
                        "Expand latest year in visits list [%S]\n" +
                        "First day of the week is [%s]\n" +
                        "Default increment is [%d]",

                this.getDefaultSortOrder(),
                this.expandLatestYearHeaderByDefault,
                this.getFirstDayOfTheWeek(),
                this.getIncrement()
        );
    }

    public boolean initialize()
    {
        Log.i("initializing...");
        Stopwatch stopwatch = new Stopwatch(true);

        boolean success = false;
        if(persistence.loadPreferences(this))
        {
            if(this.validate())
            {
                Visit.setSortOrder(this.getDefaultSortOrder());
                Log.i(String.format(Locale.getDefault(), "successful - took [%d]ms", stopwatch.stop()));
                success = true;
            }
            else
            {
                Log.e(String.format(Locale.getDefault(), "failed - took [%d]ms", stopwatch.stop()));
                success = false;
            }
        }

        if(!success)
        {
            this.createDefaultPreferences();
            Log.i(String.format(Locale.getDefault(), "using defaults - took [%d]ms", stopwatch.stop()));
            success = true;
        }

        Log.wrap(LogLevel.INFO, this.toString(), '-', false);
        return success;
    }

    public boolean createDefaultPreferences()
    {
        Log.w("creating default preferences");

        Stopwatch stopwatch = new Stopwatch(true);

        this.setDefaults();

        if(persistence.savePreferences(this))
        {
            Log.w(String.format(Locale.getDefault(), "default preferences successfully created - took [%d]ms", stopwatch.stop()));
            return true;
        }
        else
        {
            Log.e(String.format(Locale.getDefault(), "initializing preferences failed - took [%d]ms", stopwatch.stop()));
            return false;
        }
    }

    private boolean validate()
    {
        Log.i("validating preferences...");

        if(this.getDefaultSortOrder() == null)
        {
            Log.e("validation failed: default SortOrder is null");
            return false;
        }

        Log.d("validation successful");
        return true;
    }

    public void setDefaults()
    {
        Log.d("setting defaults...");

        this.setDefaultSortOrder(SortOrder.DESCENDING);
        this.setExpandLatestYearHeaderByDefault(true);
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
        defaultDetailsOrder.add(DetailType.MODEL);
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
        Log.d(String.format(Locale.getDefault(), "[%d] details set in order", detailsOrder.size()));
    }

    public SortOrder getDefaultSortOrder()
    {
        return this.defaultSortOrder;
    }

    public void setDefaultSortOrder(SortOrder defaultSortOrder)
    {
        this.defaultSortOrder = defaultSortOrder;
        Log.d(String.format("[%s] set as default", defaultSortOrder));
    }

    public boolean expandLatestYearHeaderByDefault()
    {
        return this.expandLatestYearHeaderByDefault;
    }

    public void setExpandLatestYearHeaderByDefault(boolean expandLatestYearHeaderByDefault)
    {
        this.expandLatestYearHeaderByDefault = expandLatestYearHeaderByDefault;
        Log.d(String.format("set to [%S]", expandLatestYearHeaderByDefault));
    }

    public boolean defaultPropertiesAlwaysAtTop()
    {
        return this.defaultPropertiesAlwaysAtTop;
    }

    public boolean sortParksToTopOfLocationsChildren()
    {
        return sortParksToTopOfLocationsChildren;
    }

    public int getFirstDayOfTheWeek()
    {
        return this.firstDayOfTheWeek;
    }

    public void setFirstDayOfTheWeek(int firstDayOfTheWeek)
    {
        this.firstDayOfTheWeek = firstDayOfTheWeek;
        Log.d(String.format("set to [%s]", this.dayNames[firstDayOfTheWeek]));
    }

    public int getIncrement()
    {
        return this.increment;
    }

    public void setIncrement(int increment)
    {
        this.increment = increment;
        Log.d(String.format(Locale.getDefault(), "[%d] set as default", increment));
    }

    public String getExportFileName()
    {
        return this.exportFileName;
    }

    public JSONObject toJson()
    {
        Log.v("creating json object from preferences...");

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
            jsonObjectPreferences.put(Constants.JSON_STRING_EXPAND_LATEST_YEAR_HEADER, this.expandLatestYearHeaderByDefault());
            jsonObjectPreferences.put(Constants.JSON_STRING_FIRST_DAY_OF_THE_WEEK, this.getFirstDayOfTheWeek());
            jsonObjectPreferences.put(Constants.JSON_STRING_INCREMENT, this.getIncrement());

            return jsonObjectPreferences;
        }
        catch(JSONException e)
        {
            e.printStackTrace();

            Log.e(String.format("failed with JSONException [%s]", e.getMessage()));
            return null;
        }
    }
}