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
    //must be protected in order to allow OnSiteAttraction to use correct Properties in toJson()
    protected CreditType creditType;
    protected Category category;
    protected Manufacturer manufacturer;

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
        if(this.getModel().creditTypeIsSet())
        {
            Log.v(Constants.LOG_TAG,  String.format("Attraction.getCreditType:: getting %s's CreditType from %s", this, this.getModel()));
            return this.getModel().getCreditType();
        }

        return this.creditType != null ? this.creditType : CreditType.getDefault();
    }

    public void setCreditType(CreditType creditType)
    {
        if(this.creditType != null)
        {
            this.creditType.deleteChild(this);
        }

        this.creditType = creditType;

        if(!this.creditType.containsChild(this))
        {
            this.creditType.addChild(this);
        }

        Log.d(Constants.LOG_TAG,  String.format("Attraction.setCreditType:: set %s's CreditType to %s", this, this.creditType));
    }

    public Category getCategory()
    {
        if(this.getModel().categoryIsSet())
        {
            Log.d(Constants.LOG_TAG,  String.format("Attraction.getCategory:: getting %s's Category from %s", this, this.getModel()));
            return this.getModel().getCategory();
        }

        return this.category != null ? this.category : Category.getDefault();
    }

    public void setCategory(Category category)
    {
        if(this.category != null)
        {
            this.category.deleteChild(this);
        }

        this.category = category;

        if(!this.category.containsChild(this))
        {
            this.category.addChild(this);
        }

        Log.d(Constants.LOG_TAG,  String.format("Attraction.setCategory:: set %s's Category to %s", this, this.category));
    }

    public Manufacturer getManufacturer()
    {
        if(this.getModel().manufacturerIsSet())
        {
            Log.d(Constants.LOG_TAG,  String.format("Attraction.getManufacturer:: getting %s's Manufacturer from %s", this, this.getModel()));
            return this.getModel().getManufacturer();
        }

        return this.manufacturer != null ? this.manufacturer : Manufacturer.getDefault();
    }

    public void setManufacturer(Manufacturer manufacturer)
    {
        if(this.manufacturer != null)
        {
            this.manufacturer.deleteChild(this);
        }

        this.manufacturer = manufacturer;

        if(!this.manufacturer.containsChild(this))
        {
            this.manufacturer.addChild(this);
        }

        Log.d(Constants.LOG_TAG,  String.format("Attraction.setManufacturer:: set %s's Manufacturer to %s", this, this.manufacturer));
    }

    public Model getModel()
    {
        return this.model != null ? this.model : Model.getDefault();
    }

    public void setModel(Model model)
    {
        if(model != null)
        {
            if(this.model != null)
            {
                this.model.deleteChild(this);
            }

            if(!model.containsChild(this))
            {
                model.addChild(this);
            }

            this.model = model;
            Log.d(Constants.LOG_TAG, String.format("Attraction.setModel:: set %s's Model to %s - setting Properties...", this, model.getFullName()));

            if(this.model.creditTypeIsSet())
            {
                this.setCreditType(model.getCreditType());
            }

            if(this.model.categoryIsSet())
            {
                this.setCategory(model.getCategory());
            }

            if(this.model.manufacturerIsSet())
            {
                this.setManufacturer(model.getManufacturer());
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("Attraction.setModel:: %s's Model cannot be null", this));
        }

    }

    public Status getStatus()
    {
        return this.status != null ? this.status : Status.getDefault();
    }

    public void setStatus(Status status)
    {
        if(status != null)
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
            Log.d(Constants.LOG_TAG,  String.format("Attraction.setStatus:: set %s's Status to %s", this, status));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("Attraction.setStaus:: %s's Status cannot be null", this));
        }
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