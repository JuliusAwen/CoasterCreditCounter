package de.juliusawen.coastercreditcounter.dataModel.elements.attractions;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Model;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;

/**
 * Base interface for all attractions
 */
public interface IAttraction extends IElement
{
    CreditType getCreditType();
    void setCreditType(CreditType creditType);

    Category getCategory();
    void setCategory(Category category);

    Manufacturer getManufacturer();
    void setManufacturer(Manufacturer manufacturer);

    Model getModel();
    void setModel(Model model);

    Status getStatus();
    void setStatus(Status status);

    void increaseTrackedRideCount(int increment);
    void decreaseTrackedRideCount(int decrement);

    int fetchTotalRideCount();

    int getUntracktedRideCount();
    void setUntracktedRideCount(int untracktedRideCount);
}
