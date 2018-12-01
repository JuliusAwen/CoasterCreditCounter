package de.juliusawen.coastercreditcounter.data.attractions;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;

public class StockAttraction extends Element implements IOnSiteAttraction
{
    private IBlueprint blueprint;

    public StockAttraction(IBlueprint blueprint)
    {
        super(blueprint.getName(), UUID.randomUUID());
        this.blueprint = blueprint;
    }

    @Override
    public AttractionCategory getAttractionCategory()
    {
        return this.blueprint.getAttractionCategory();
    }

    @Override
    public void setAttractionCategory(AttractionCategory attractionCategory)
    {
        this.blueprint.setAttractionCategory(attractionCategory);
    }

    @Override
    public int getTotalRideCount()
    {
        return this.blueprint.getTotalRideCount();
    }

    @Override
    public void increaseTotalRideCount(int increment)
    {
        this.blueprint.increaseTotalRideCount(increment);
    }

    @Override
    public void decreaseTotalRideCount(int decrement)
    {
        this.blueprint.decreaseTotalRideCount(decrement);
    }
}
