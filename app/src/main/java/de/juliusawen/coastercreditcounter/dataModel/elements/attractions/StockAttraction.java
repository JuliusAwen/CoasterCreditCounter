package de.juliusawen.coastercreditcounter.dataModel.elements.attractions;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasStatusProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

/**
 * Concrete physical entity created from blueprint
 *
 * Parent: Park
 * Children: none
 */
public class StockAttraction extends Attraction implements IOnSiteAttraction, IHasStatusProperty
{
    private final Blueprint blueprint;

    private StockAttraction(String name, Blueprint blueprint, int untrackedRideCount, UUID uuid)
    {
        super(name, untrackedRideCount, uuid);
        this.blueprint = blueprint;
    }

    public static StockAttraction create(String name, Blueprint blueprint)
    {
        return StockAttraction.create(name, blueprint, 0);
    }

    public static StockAttraction create(String name, Blueprint blueprint, int untrackedRideCount)
    {
        return StockAttraction.create(name, blueprint, untrackedRideCount, UUID.randomUUID());
    }

    public static StockAttraction create(String name, Blueprint blueprint, int untrackedRideCount, UUID uuid)
    {
        StockAttraction stockAttraction = null;
        if(Element.nameIsValid(name))
        {
            stockAttraction = new StockAttraction(name, blueprint, untrackedRideCount, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("StockAttraction.create:: %s created.", stockAttraction.getFullName()));
        }
        return stockAttraction;
    }

    @Override
    public CreditType getCreditType()
    {
        return this.blueprint.getCreditType();
    }

    @Override
    public void setCreditType(CreditType creditType)
    {
        String errorMessage = String.format("StockAttraction.setCreditType:: %s: StockAttractions can not have CreditType", this);
        Log.e(Constants.LOG_TAG, errorMessage);
        throw new IllegalStateException(errorMessage);
    }

    @Override
    public Category getCategory()
    {
        return this.blueprint.getCategory();
    }

    @Override
    public void setCategory(Category category)
    {
        String errorMessage = String.format("StockAttraction.setCategory:: %s: StockAttractions can not have Category", this);
        Log.e(Constants.LOG_TAG, errorMessage);
        throw new IllegalStateException(errorMessage);
    }

    @Override
    public Manufacturer getManufacturer()
    {
        return this.blueprint.getManufacturer();
    }

    @Override
    public void setManufacturer(Manufacturer manufacturer)
    {
        String errorMessage = String.format("StockAttraction.setManufacturer:: %s: StockAttractions can not have Manufacturer", this);
        Log.e(Constants.LOG_TAG, errorMessage);
        throw new IllegalStateException(errorMessage);
    }

    @Override
    public void increaseTotalRideCount(int increment)
    {
        this.blueprint.increaseTotalRideCount(increment);
        super.increaseTotalRideCount(increment);
    }

    @Override
    public void decreaseTotalRideCount(int decrement)
    {
        if((this.blueprint.getTotalRideCount() - decrement) >= 0 && (super.getTotalRideCount() - decrement) >= 0)
        {
            this.blueprint.decreaseTotalRideCount(decrement);
            super.decreaseTotalRideCount(decrement);
        }
        else
        {
            Log.d(Constants.LOG_TAG, String.format("StockAttraction.decreaseTotalRideCount:: %s's total ride count is [%d]: decreasing by [%d] would make it negative - not decreasing",
                    this, decrement, this.getTotalRideCount()));
        }
    }


    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_BLUEPRINT, this.blueprint.getUuid());
            jsonObject.put(Constants.JSON_STRING_STATUS, this.getStatus().getUuid());
            jsonObject.put(Constants.JSON_STRING_UNTRACKED_RIDE_COUNT, this.getUntracktedRideCount());

            Log.v(Constants.LOG_TAG, String.format("StockAttraction.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("StockAttraction.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}
