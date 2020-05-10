package de.juliusawen.coastercreditcounter.dataModel.elements.attractions;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCategory;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasManufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasModel;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasStatus;

/**
 * Base interface for all attractions
 */
public interface IAttraction extends IElement, IHasCreditType, IHasCategory, IHasManufacturer, IHasModel, IHasStatus
{
    void increaseTrackedRideCount(int increment);
    void decreaseTrackedRideCount(int decrement);

    int fetchTotalRideCount();

    int getUntracktedRideCount();
    void setUntracktedRideCount(int untracktedRideCount);
}
