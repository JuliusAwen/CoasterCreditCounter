package de.juliusawen.coastercreditcounter.dataModel.elements.attractions;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCategoryProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCreditTypeProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasManufacturerProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasStatusProperty;
import de.juliusawen.coastercreditcounter.globals.Constants;

/**
 * Individual attraction located at a particular park
 *
 * Parent: Park
 * Children: none
 */
public class CustomAttraction extends Attraction implements IOnSiteAttraction, IHasCreditTypeProperty, IHasCategoryProperty, IHasManufacturerProperty, IHasStatusProperty
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
        if(Element.nameIsValid(name))
        {
            customAttraction = new CustomAttraction(name, untrackedRideCount, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("CustomAttraction.create:: %s created", customAttraction.getFullName()));
        }
        return customAttraction;
    }
}
