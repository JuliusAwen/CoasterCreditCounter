package de.juliusawen.coastercreditcounter.data.attractions;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class StockAttraction extends Attraction implements IOnSiteAttraction
{
    private IBlueprint blueprint;

    private StockAttraction(String name, IBlueprint blueprint, UUID uuid)
    {
        super(name, uuid);
        this.blueprint = blueprint;
    }

    public static StockAttraction create(String name, IBlueprint blueprint, UUID uuid)
    {
        StockAttraction stockAttraction = null;
        name = name.trim();

        if(!name.isEmpty())
        {
            stockAttraction = new StockAttraction(name, blueprint, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("StockAttraction.create:: %s created.", stockAttraction.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("StockAttraction.create:: invalid name[%s] - StockAttraction not created.", name));
        }
        return stockAttraction;
    }

    @Override
    public AttractionCategory getAttractionCategory()
    {
        return this.blueprint.getAttractionCategory();
    }
}
