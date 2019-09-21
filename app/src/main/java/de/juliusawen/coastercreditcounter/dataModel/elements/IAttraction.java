package de.juliusawen.coastercreditcounter.dataModel.elements;

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
    Status getStatus();
    void setStatus(Status status);
    int getTotalRideCount();
    void increaseTotalRideCount(int increment);
    void decreaseTotalRideCount(int decrement);
    int getUntracktedRideCount();
    void setUntracktedRideCount(int untracktedRideCount);
}
