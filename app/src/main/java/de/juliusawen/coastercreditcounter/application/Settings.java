package de.juliusawen.coastercreditcounter.application;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.attributes.IPersistable;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.persistence.Persistence;
import de.juliusawen.coastercreditcounter.tools.Stopwatch;

public class Settings implements IPersistable
{
    private CreditType defaultCreditType;
    private Category defaultCategory;
    private Manufacturer defaultManufacturer;
    private Status defaultStatus;


    private SortOrder defaultSortOrderParkVisits;
    private boolean expandLatestYearInListByDefault;
    private int firstDayOfTheWeek;

    private int defaultIncrement;


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
                Visit.setSortOrder(this.getDefaultSortOrderParkVisits());
                Log.i(Constants.LOG_TAG, String.format("Settings.initialize:: loading settings successful - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("Settings.initialize:: validation failed - took [%d]ms", stopwatch.stop()));
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("Settings.initialize:: loading settings failed - took [%d]ms", stopwatch.stop()));
        }

        return false;
    }

    private boolean validate()
    {
        Log.i(Constants.LOG_TAG, "Settings.validate:: validating settings...");

        if(this.getDefaultSortOrderParkVisits() == null)
        {
            String message = "Settings validation failed: default sort order for park visits is null";
            Log.e(Constants.LOG_TAG, String.format("Settings.validate:: %s", message));
            throw  new IllegalStateException(message);
        }
        else
        {
            Log.d(Constants.LOG_TAG, String.format("Settings.validate:: default sort order for park visits is [%s]", this.getDefaultSortOrderParkVisits()));
        }

        Log.i(Constants.LOG_TAG, String.format("Settings.validate:: expand latest year in visits list [%S]", this.expandLatestYearInListByDefault()));

        Log.i(Constants.LOG_TAG, String.format("Settings.validate:: first day of the week is [%s]", this.dayNames[this.getFirstDayOfTheWeek()]));

        Log.i(Constants.LOG_TAG, String.format("Settings.validate:: default increment is [%d]", this.getDefaultIncrement()));


        Log.i(Constants.LOG_TAG, "Settings.validate:: settings validation successful");
        return true;
    }

    public void useDefaults()
    {
        Log.i(Constants.LOG_TAG, "Settings.useDatabaseMock:: setting defaults...");

        this.setDefaultSortOrderParkVisits(SortOrder.DESCENDING);
        this.setExpandLatestYearInListByDefault(true);
        this.setFirstDayOfTheWeek(Calendar.MONDAY);
        this.setDefaultIncrement(1);
    }

    public CreditType getDefaultCreditType()
    {
        if(this.defaultCreditType == null)
        {
            Log.w(Constants.LOG_TAG, "Settings.getDefaultCreditType:: no default set - creating default");
            this.setDefaultCreditType(CreditType.create(App.getContext().getString(R.string.default_credit_type_name)));
        }
        return this.defaultCreditType;
    }

    public void setDefaultCreditType(CreditType defaultCreditType)
    {
        this.defaultCreditType = defaultCreditType;
        Log.i(Constants.LOG_TAG, String.format("Settings.setDefaultCreditType:: %s set as default", defaultCreditType));
    }

    public Category getDefaultCategory()
    {
        if(this.defaultCategory == null)
        {
            Log.w(Constants.LOG_TAG, "Settings.getDefaultCategory:: no default set - creating default");
            this.setDefaultCategory(Category.create((App.getContext().getString(R.string.default_category_name))));
        }
        return this.defaultCategory;
    }

    public void setDefaultCategory(Category defaultCategory)
    {
        this.defaultCategory = defaultCategory;
        Log.i(Constants.LOG_TAG, String.format("Settings.setDefaultCategory:: %s set as default", defaultCategory));
    }

    public Manufacturer getDefaultManufacturer()
    {
        if(this.defaultManufacturer == null)
        {
            Log.w(Constants.LOG_TAG, "Settings.getDefaultManufacturer:: no default set - creating default");
            this.setDefaultManufacturer(Manufacturer.create((App.getContext().getString(R.string.default_manufacturer_name))));
        }
        return this.defaultManufacturer;
    }

    public void setDefaultManufacturer(Manufacturer defaultManufacturer)
    {
        this.defaultManufacturer = defaultManufacturer;
        Log.i(Constants.LOG_TAG, String.format("Settings.setDefaultManufacturer:: %s set as default", defaultManufacturer));
    }

    public Status getDefaultStatus()
    {
        if(this.defaultStatus == null)
        {
            Log.w(Constants.LOG_TAG, "Settings.getDefaultStatus:: no default set - creating default");
            this.setDefaultStatus(Status.create((App.getContext().getString(R.string.default_status_name))));
        }
        return this.defaultStatus;
    }

    public void setDefaultStatus(Status defaultStatus)
    {
        this.defaultStatus = defaultStatus;
        Log.i(Constants.LOG_TAG, String.format("Settings.setDefaultStatus:: %s set as default", defaultStatus));
    }

    public SortOrder getDefaultSortOrderParkVisits()
    {
        return defaultSortOrderParkVisits;
    }

    public void setDefaultSortOrderParkVisits(SortOrder defaultSortOrderParkVisits)
    {
        this.defaultSortOrderParkVisits = defaultSortOrderParkVisits;
        Log.i(Constants.LOG_TAG, String.format("Settings.setDefaultSortOrderParkVisits:: [%s] set as default", defaultSortOrderParkVisits));
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

    public int getDefaultIncrement()
    {
        return this.defaultIncrement;
    }

    public void setDefaultIncrement(int defaultIncrement)
    {
        this.defaultIncrement = defaultIncrement;
        Log.i(Constants.LOG_TAG, String.format("Settings.setDefaultIncrement:: [%d] set as default", defaultIncrement));
    }

    public JSONObject toJson()
    {
        Log.d(Constants.LOG_TAG, ("Settings.toJson:: creating json object from settings..."));

        try
        {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(Constants.JSON_STRING_DEFAULT_SORT_ORDER, this.getDefaultSortOrderParkVisits().ordinal());
            jsonObject.put(Constants.JSON_STRING_EXPAND_LATEST_YEAR_HEADER, this.expandLatestYearInListByDefault());
            jsonObject.put(Constants.JSON_STRING_FIRST_DAY_OF_THE_WEEK, this.getFirstDayOfTheWeek());
            jsonObject.put(Constants.JSON_STRING_DEFAULT_INCREMENT, this.getDefaultIncrement());

            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();

            Log.e(Constants.LOG_TAG, String.format("Element.toJson:: failed with JSONException [%s]", e.getMessage()));
            return null;
        }
    }
}
