package de.juliusawen.coastercreditcounter.dataModel.elements.attractions;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Model;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;

/**
 * Abstract base class for all Attractions containing all base methods.<br>
 * Can be CustomAttraction or VisitedAttraction<br>
 */
public abstract class Attraction extends Element implements IAttraction
{
    private CreditType creditType;
    private Category category;
    private Manufacturer manufacturer;
    private Model model;
    private Status status;

    private int untracktedRideCount = 0;
    private int trackedRideCount = 0;

    protected Attraction(String name, UUID uuid)
    {
        super(name, uuid);
    }

    protected Attraction(String name, int untrackedRideCount, UUID uuid)
    {
        super(name, uuid);
        this.setUntracktedRideCount(untrackedRideCount);
    }

    @Override
    public void delete()
    {
        if(this.creditType != null)
        {
            this.creditType.deleteChild(this);
        }

        if(this.category != null)
        {
            this.category.deleteChild(this);
        }

        if(this.manufacturer != null)
        {
            this.manufacturer.deleteChild(this);
        }

        if(this.model != null)
        {
            this.model.deleteChild(this);
        }

        if(this.status != null)
        {
            this.status.deleteChild(this);
        }

        super.delete();
    }

    public CreditType getCreditType()
    {
        return this.creditType != null ? this.creditType : CreditType.getDefault();
    }

    public void setCreditType(CreditType creditType)
    {
        if(this.creditType != null)
        {
            this.creditType.deleteChild(this);
        }

        if(!creditType.containsChild(this))
        {
            creditType.addChild(this);
        }

        this.creditType = creditType;
        Log.d(Constants.LOG_TAG,  String.format("Attraction.setCreditType:: set %s's CreditType to %s", this, this.creditType));
    }

    public Category getCategory()
    {
        return this.category != null ? this.category : Category.getDefault();
    }

    public void setCategory(Category category)
    {
        if(this.category != null)
        {
            this.category.deleteChild(this);
        }

        if(!category.containsChild(this))
        {
            category.addChild(this);
        }

        this.category = category;
        Log.d(Constants.LOG_TAG,  String.format("Attraction.setCategory:: set %s's Category to %s", this, this.category));
    }

    public Manufacturer getManufacturer()
    {
        return this.manufacturer != null ? this.manufacturer : Manufacturer.getDefault();
    }

    public void setManufacturer(Manufacturer manufacturer)
    {
        if(this.manufacturer != null)
        {
            this.manufacturer.deleteChild(this);
        }

        if(!manufacturer.containsChild(this))
        {
            manufacturer.addChild(this);
        }

        this.manufacturer = manufacturer;
        Log.d(Constants.LOG_TAG,  String.format("Attraction.setManufacturer:: set %s's Manufacturer to %s", this, this.manufacturer));
    }

    public Model getModel()
    {
        return this.model != null ? this.model : Model.getDefault();
    }

    public void setModel(Model model)
    {
        if(this.manufacturer != null)
        {
            this.model.deleteChild(this);
        }

        if(!model.containsChild(this))
        {
            model.addChild(this);
        }

        this.model = model;
        Log.d(Constants.LOG_TAG,  String.format("Attraction.setModel:: set %s's Model to %s", this, this.model));

        this.creditType = model.getCreditType();
        this.category = model.getCategory();
        this.manufacturer = model.getManufacturer();
    }

    public Status getStatus()
    {
        return this.status != null ? this.status : Status.getDefault();
    }

    public void setStatus(Status status)
    {
        if(this.status != null)
        {
            this.status.deleteChild(this);
        }

        if(!status.containsChild(this))
        {
            status.addChild(this);
        }

        this.status = status;
        Log.d(Constants.LOG_TAG,  String.format("Attraction.setStatus:: set %s's Status to %s", this, this.status));
    }

    public int getUntracktedRideCount()
    {
        return this.untracktedRideCount;
    }

    public void setUntracktedRideCount(int untracktedRideCount)
    {
        if(untracktedRideCount != this.untracktedRideCount)
        {
            this.untracktedRideCount = untracktedRideCount;
            Log.d(Constants.LOG_TAG,  String.format("Attraction.setUntracktedRideCount:: set %s's untracked ride count to [%d]", this, this.untracktedRideCount));
        }
        else
        {
            Log.v(Constants.LOG_TAG,  String.format("Attraction.setUntracktedRideCount:: %s's untracked ride count unchanged at [%d]", this, this.untracktedRideCount));
        }
    }

    public int getTrackedRideCount()
    {
        return this.trackedRideCount;
    }

    public int fetchTotalRideCount()
    {
        return this.trackedRideCount + this.untracktedRideCount;
    }

    public void increaseTrackedRideCount(int increment)
    {
        if(increment > 0)
        {
            this.trackedRideCount += increment;
            Log.d(Constants.LOG_TAG, String.format("Attraction.increaseTrackedRideCount:: increased %s's total ride count by [%d] to [%d] ([%d] rides untracked)",
                    this, increment, this.fetchTotalRideCount(), this.untracktedRideCount));
        }
    }

    public void decreaseTrackedRideCount(int decrement)
    {
        if(decrement > 0)
        {
            if((this.trackedRideCount - decrement) >= 0)
            {
                this.trackedRideCount -= decrement;
                Log.d(Constants.LOG_TAG, String.format("Attraction.decreaseTrackedRideCount:: decreased %s's total ride count by [%d] to [%d] ([%d] rides untracked)",
                        this, decrement, this.fetchTotalRideCount(), this.untracktedRideCount));
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("Attraction.decreaseTrackedRideCount:: %s's total ride count is [%d] ([%d] rides untracked): " +
                        "decreasing by [%d] would make it negative - not decreasing", this, this.fetchTotalRideCount(), this.untracktedRideCount, decrement));
            }
        }
    }
}