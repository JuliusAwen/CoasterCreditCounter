package de.juliusawen.coastercreditcounter.dataModel.elements.attractions;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

/**
 * Concrete physical entity created from blueprint
 *
 * Parent: Park
 * Children: none
 */
public final class StockAttraction extends Attraction implements IOnSiteAttraction
{
    private Blueprint blueprint;

    private StockAttraction(String name, Blueprint blueprint, int untrackedRideCount, UUID uuid)
    {
        super(name, untrackedRideCount, uuid);
        this.blueprint = blueprint;
        this.blueprint.addChild(this);
        this.blueprint.increaseTrackedRideCount(untrackedRideCount);
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
        if(Element.isNameValid(name))
        {
            stockAttraction = new StockAttraction(name, blueprint, untrackedRideCount, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("StockAttraction.create:: %s created.", stockAttraction.getFullName()));
        }

        return stockAttraction;
    }

    public CustomAttraction convertToCustomAttraction()
    {
        CustomAttraction customAttraction = CustomAttraction.create(this.getName(), this.getUntracktedRideCount());
        customAttraction.setCreditType(this.getCreditType());
        customAttraction.setCategory(this.getCategory());
        customAttraction.setManufacturer(this.getManufacturer());
        customAttraction.setStatus(this.getStatus());

        for(Visit visit : this.getParent().getChildrenAsType(Visit.class))
        {
            for(VisitedAttraction originalVisitedAttraction : visit.getChildrenAsType(VisitedAttraction.class))
            {
                if(originalVisitedAttraction.getOnSiteAttraction().equals(this))
                {
                    int trackedRideCount = originalVisitedAttraction.fetchTotalRideCount();

                    visit.deleteChild(originalVisitedAttraction);

                    VisitedAttraction newVisitedAttraction = VisitedAttraction.create(customAttraction);
                    newVisitedAttraction.increaseTrackedRideCount(trackedRideCount);

                    visit.addChildAndSetParent(newVisitedAttraction);
                }
            }
        }

        this.delete();
        this.getParent().addChildAndSetParent(customAttraction);

        return customAttraction;
    }

    public Blueprint getBlueprint()
    {
        return this.blueprint;
    }

    public void changeBlueprint(Blueprint newBlueprint)
    {
        this.blueprint.decreaseTrackedRideCount(this.fetchTotalRideCount());
        this.blueprint.deleteChild(this);

        newBlueprint.increaseTrackedRideCount(this.fetchTotalRideCount());
        newBlueprint.addChild(this);
        this.blueprint = newBlueprint;
    }

    @Override
    public void delete()
    {
        this.blueprint.decreaseTrackedRideCount(this.fetchTotalRideCount());
        this.blueprint.deleteChild(this);
        super.delete();
    }

    @Override
    public CreditType getCreditType()
    {
        return this.blueprint.getCreditType();
    }

    @Override
    public void setCreditType(CreditType creditType)
    {
        Log.v(Constants.LOG_TAG,  String.format("StockAttraction.setCreditType:: %s: StockAttractions can not have CreditType", this));
    }

    @Override
    public Category getCategory()
    {
        return this.blueprint.getCategory();
    }

    @Override
    public void setCategory(Category category)
    {
        Log.v(Constants.LOG_TAG,  String.format("StockAttraction.setCategory:: %s: StockAttractions can not have Category\"", this));
    }

    @Override
    public Manufacturer getManufacturer()
    {
        return this.blueprint.getManufacturer();
    }

    @Override
    public void setManufacturer(Manufacturer manufacturer)
    {
        Log.v(Constants.LOG_TAG,  String.format("StockAttraction.setCategory:: %s: StockAttractions can not have Manufacturer\"", this));
    }

    @Override
    public void increaseTrackedRideCount(int increment)
    {
        this.blueprint.increaseTrackedRideCount(increment);
        super.increaseTrackedRideCount(increment);
    }

    @Override
    public void decreaseTrackedRideCount(int decrement)
    {
        if((this.blueprint.fetchTotalRideCount() - decrement) >= 0 && (super.fetchTotalRideCount() - decrement) >= 0)
        {
            this.blueprint.decreaseTrackedRideCount(decrement);
            super.decreaseTrackedRideCount(decrement);
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("StockAttraction.decreaseTrackedRideCount:: %s's total ride count is [%d]: decreasing by [%d] would make it negative - not decreasing",
                    this, decrement, this.fetchTotalRideCount()));
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
