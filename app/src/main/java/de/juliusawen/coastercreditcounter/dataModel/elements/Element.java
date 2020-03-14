package de.juliusawen.coastercreditcounter.dataModel.elements;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.Blueprint;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.CustomAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.StockAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.IGroupHeader;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCategoryProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCreditTypeProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasManufacturerProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasStatusProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IProperty;
import de.juliusawen.coastercreditcounter.dataModel.traits.IOrphan;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;

/**
 * Simple Node.
 * Has name and uuid.
 * Can have one other Element as parent and several Elements as children.
 * Provides tools to work with node structure.
 */
public abstract class Element implements IElement
{
    private final UUID uuid;
    private String name;

    private IElement parent = null;
    private final List<IElement> children = new ArrayList<>();

    protected Element(String name, UUID uuid)
    {
        this.setName(name);
        this.uuid = uuid == null ? UUID.randomUUID() : uuid;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == this)
        {
            return true;
        }

        if(!(obj instanceof Element))
        {
            return false;
        }

        Element element = (Element) obj;

        return element.getUuid() != null && element.getUuid().equals(this.getUuid());
    }

    public String toString()
    {
        return String.format(Locale.getDefault(), "[%s \"%s\"]", this.getClass().getSimpleName(), this.getName());
    }

    public String getName()
    {
        return this.name;
    }

    public boolean setName(String name)
    {
        if(Element.nameIsValid(name))
        {
            this.name = name.trim();
            return true;
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Element.setName:: name[%s] is invalid", name));
            return false;
        }
    }

    public static boolean nameIsValid(String name)
    {
        if(!name.trim().isEmpty())
        {
            name = name.trim();

            Log.v(Constants.LOG_TAG,  String.format("StringTool.verifyName:: name [%s] is valid", name));
            return true;
        }
        else
        {
            Log.w(Constants.LOG_TAG,  String.format("StringTool.verifyName:: name [%s] is invalid", name));
            return false;
        }
    }

    public String getFullName()
    {
        return String.format(Locale.getDefault(),
                "[%s \"%s\" (%s)]",
                this.getClass().getSimpleName(),
                this.getName(),
                this.getUuid()
        );
    }

    public UUID getUuid()
    {
        return this.uuid;
    }

    public void addChildAndSetParent(IElement child)
    {
        this.addChildAtIndexAndSetParent(this.getChildCount(), child);
    }

    public void addChildrenAndSetParent(List<UUID> childUuids)
    {
        for(UUID childUuid : childUuids)
        {
            this.addChildAndSetParent(App.content.getContentByUuid(childUuid));
        }
    }

    public void addChildrenAtIndexAndSetParent(int index, List<IElement> children)
    {
        Log.v(Constants.LOG_TAG, String.format("Element.addChildrenAtIndexAndSetParent:: called with [%d] children", children.size()));
        int increment = 0;
        for (IElement child : children)
        {
            this.addChildAtIndexAndSetParent(index + increment, child);
            child.setParent(this);
            increment ++;
        }
    }

    public void addChildAtIndexAndSetParent(int index, IElement child)
    {
        if(!this.containsChild(child))
        {
            if(child.getParent() != null)
            {
                Log.w(Constants.LOG_TAG, String.format("Element.addChildAtIndexAndSetParent:: %s already has parent %s - setting new parent %s", child, child.getParent(), this));
            }
            child.setParent(this);

            Log.v(Constants.LOG_TAG, String.format("Element.addChildAtIndexAndSetParent:: %s -> child %s added", this, child));
            this.children.add(index, child);
        }
        else
        {
            Log.w(Constants.LOG_TAG, String.format("Element.addChildAtIndexAndSetParent:: %s already contains child [%s]", this, child));
        }
    }

    public void reorderChildren(List<? extends IElement> children)
    {
        if(!children.isEmpty())
        {
            this.getChildren().removeAll(children);
            this.getChildren().addAll(children);
            Log.v(Constants.LOG_TAG, String.format("Element.reorderChildren:: %s -> [%d] children removed and then added again in given order", this, children.size()));
        }
        else
        {
            Log.w(Constants.LOG_TAG, String.format("Element.reorderChildren:: %s -> given list of children is empty", this));
        }
    }

    public void addChildren(List<IElement> children)
    {
        for(IElement child : children)
        {
            this.addChild(child);
        }
    }

    public void addChild(IElement child)
    {
        if(child.equals(this))
        {
            throw new IllegalStateException("Element can not be it's own child!");
        }

        this.getChildren().add(child);
        Log.v(Constants.LOG_TAG, String.format("Element.addChild:: %s -> child %s added", this, child));
    }

    public boolean containsChild(IElement child)
    {
        return this.getChildren().contains(child);
    }

    public int getIndexOfChild(IElement child)
    {
        return this.getChildren().indexOf(child);
    }

    public boolean hasChildren()
    {
        return !this.getChildren().isEmpty();
    }

    public boolean hasChildrenOfType(Class<? extends IElement> type)
    {
        return !this.fetchChildrenOfType(type).isEmpty();
    }

    public int getChildCount()
    {
        return this.getChildren().size();
    }

    public int getChildCountOfType(Class<? extends IElement> type)
    {
        return this.fetchChildrenOfType(type).size();
    }

    public List<IElement> getChildren()
    {
        return this.children;
    }

    public List<IElement> fetchChildrenOfType(Class<? extends IElement> type)
    {
        List<IElement> children = new ArrayList<>();
        for(IElement element : this.getChildren())
        {
            if(type.isAssignableFrom(element.getClass()))
            {
                children.add(element);
            }
        }
        return children;
    }

    public <T extends IElement> List<T> fetchChildrenAsType(Class<T> type)
    {
        List<T> children = new ArrayList<>();
        for(IElement element : this.getChildren())
        {
            if(type.isAssignableFrom(element.getClass()))
            {
                children.add(type.cast(element));
            }
        }
        return children;
    }

    public IElement getParent()
    {
        return this.parent;
    }

    public void setParent(IElement parent)
    {
        if(this.isOrphan())
        {
            Log.e(Constants.LOG_TAG, "setParent ORPHAN");
        }

        if(parent.equals(this))
        {
            throw new IllegalStateException("Element can not be it's own parent!");
        }

        this.parent = parent;
        Log.v(Constants.LOG_TAG,  String.format("Element.setParent:: %s -> parent %s set", this, parent));
    }

    public void relocateElement(IElement newParent)
    {
        if(this.isOrphan())
        {
            Log.e(Constants.LOG_TAG, "relocateElement ORPHAN");
        }

        this.getParent().getChildren().remove(this);
        newParent.addChildAndSetParent(this);
    }

    public void deleteElementAndDescendants()
    {
        for(IElement child : new ArrayList<>(this.getChildren()))
        {
            child.deleteElementAndDescendants();
        }
        this.deleteElement();
    }

    public void deleteElement()
    {
        if(this.isOrphan())
        {
            Log.e(Constants.LOG_TAG, "delete Element ORPHAN");
        }

        this.getParent().deleteChild(this);
    }

    public void deleteChild(IElement child)
    {
        if(this.containsChild(child))
        {
            this.getChildren().remove(child);
            Log.v(Constants.LOG_TAG,  String.format("Element.deleteChild:: %s -> child %s deleted", this, child));
        }
        else
        {
            String errorMessage = String.format("Element.deleteChild:: %s -> child %s not found", this, child);
            Log.e(Constants.LOG_TAG, errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }

    public void removeElement()
    {
        if(this.isOrphan())
        {
            Log.e(Constants.LOG_TAG, "removeElement ORPHAN");
        }

        Log.d(Constants.LOG_TAG, String.format("Element.removeElement:: removing %s...", this));

        int index = this.parent.getIndexOfChild(this);

        this.parent.deleteChild(this);
        this.parent.addChildrenAtIndexAndSetParent(index, new ArrayList<>(this.getChildren()));
        for(IElement child : new ArrayList<>(this.children))
        {
            this.deleteChild(child);
        }
    }

    public boolean isDescendantOf(IElement ancestor)
    {
        if(this.equals(ancestor))
        {
            return true;
        }
        else if(this.getParent() == null)
        {
            return false;
        }
        else
        {
            return this.getParent().isDescendantOf(ancestor);
        }
    }

    public boolean isLocation()
    {
        return this instanceof Location;
    }

    public boolean isRootLocation()
    {
        return this.isLocation() && this.equals(App.content.getRootLocation());
    }

    public boolean isPark()
    {
        return this instanceof Park;
    }

    public boolean isVisit()
    {
        return this instanceof Visit;
    }

    public boolean isAttraction()
    {
        return this instanceof IAttraction;
    }

    public boolean isBlueprint()
    {
        return this instanceof Blueprint;
    }

    public boolean isCustomAttraction()
    {
        return this instanceof CustomAttraction;
    }

    public boolean isStockAttraction()
    {
        return this instanceof StockAttraction;
    }

    public boolean isVisitedAttraction()
    {
        return this instanceof VisitedAttraction;
    }

    public boolean isProperty()
    {
        return this instanceof IProperty;
    }

    public boolean hasCreditType()
    {
        return this instanceof IHasCreditTypeProperty;
    }

    public boolean hasCategory()
    {
        return this instanceof IHasCategoryProperty;
    }

    public boolean hasManufacturer()
    {
        return this instanceof IHasManufacturerProperty;
    }

    public boolean hasStatus()
    {
        return this instanceof IHasStatusProperty;
    }

    public boolean isIPersistable()
    {
        return this instanceof IPersistable;
    }

    public boolean isGroupHeader()
    {
        return this instanceof IGroupHeader;
    }

    public boolean isOrphan()
    {
        return this instanceof IOrphan;
    }
}
