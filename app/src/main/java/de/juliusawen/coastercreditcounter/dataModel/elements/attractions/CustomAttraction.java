package de.juliusawen.coastercreditcounter.dataModel.elements.attractions;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

/**
 * Individual attraction located at a particular park
 *
 * Parent: Park
 * Children: none
 */
public final class CustomAttraction extends Attraction implements IOnSiteAttraction
{
    private CustomAttraction(String name, int untrackedRideCount, UUID uuid)
    {
        super(name, untrackedRideCount, uuid);
    }

    public static CustomAttraction create(String name)
    {
        return CustomAttraction.create(name, 0);
    }

    public static CustomAttraction create(String name, int untrackedRideCount)
    {
        return CustomAttraction.create(name, untrackedRideCount, UUID.randomUUID());
    }

    public static CustomAttraction create(String name, int untrackedRideCount, UUID uuid)
    {
        CustomAttraction customAttraction = null;
        if(Element.isNameValid(name))
        {
            customAttraction = new CustomAttraction(name, untrackedRideCount, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("CustomAttraction.create:: %s created", customAttraction.getFullName()));
        }
        return customAttraction;
    }

    public StockAttraction convertToStockAttraction(Blueprint blueprint)
    {
        StockAttraction stockAttraction = StockAttraction.create(this.getName(), blueprint, this.getUntracktedRideCount());

        for(Visit visit : this.getParent().getChildrenAsType(Visit.class))
        {
            for(VisitedAttraction originalVisitedAttraction : visit.getChildrenAsType(VisitedAttraction.class))
            {
                if(originalVisitedAttraction.getOnSiteAttraction().equals(this))
                {
                    int trackedRideCount = originalVisitedAttraction.fetchTotalRideCount();

                    visit.deleteChild(originalVisitedAttraction);

                    VisitedAttraction newVisitedAttraction = VisitedAttraction.create(stockAttraction);
                    newVisitedAttraction.increaseTrackedRideCount(trackedRideCount);

                    visit.addChildAndSetParent(newVisitedAttraction);
                }
            }
        }

        this.delete();
        this.getParent().addChildAndSetParent(stockAttraction);

        return stockAttraction;
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_CREDIT_TYPE, this.getCreditType().getUuid());
            jsonObject.put(Constants.JSON_STRING_CATEGORY, this.getCategory().getUuid());
            jsonObject.put(Constants.JSON_STRING_MANUFACTURER, this.getManufacturer().getUuid());
            jsonObject.put(Constants.JSON_STRING_STATUS, this.getStatus().getUuid());
            jsonObject.put(Constants.JSON_STRING_UNTRACKED_RIDE_COUNT, this.getUntracktedRideCount());

            Log.v(Constants.LOG_TAG, String.format("Attraction.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Attraction.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}
