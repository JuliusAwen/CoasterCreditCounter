package de.juliusawen.coastercreditcounter.dataModel.elements;

import android.util.Log;

import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

/**
 * abstract base class for Elements which have no parent
 *
 * parent: none
 * children: Elements
 */
public abstract class OrphanElement extends Element
{
    public OrphanElement(String name, UUID uuid)
    {
        super(name, uuid);
    }

    @Override
    public IElement getParent()
    {
        String message = String.format("OrphanElement %s has no parent", this);
        Log.e(Constants.LOG_TAG, String.format("OrphanElement.getParent:: %s", message));
        throw new IllegalStateException(message);
    }

    @Override
    public void setParent(IElement parent)
    {
        String message = String.format("OrphanElement %s can have no parent", this);
        Log.e(Constants.LOG_TAG, String.format("OrphanElement.setParent:: %s", message));
        throw new IllegalStateException(message);
    }

    @Override
    public void addChildAndSetParent(IElement child)
    {
        String message = String.format("OrphanElement %s can have no parent", this);
        Log.e(Constants.LOG_TAG, String.format("OrphanElement.addChildAndSetParent:: %s", message));
        throw new IllegalStateException(message);
    }

    @Override
    public void addChildAndSetParentAtIndex(int index, IElement child)
    {
        String message = String.format("OrphanElement %s can have no parent", this);
        Log.e(Constants.LOG_TAG, String.format("OrphanElement.addChildAndSetParentAtIndex:: %s", message));
        throw new IllegalStateException(message);
    }

    @Override
    public void addChildrenAndSetParent(List<UUID> childUuids)
    {
        String message = String.format("OrphanElement %s can have no parent", this);
        Log.e(Constants.LOG_TAG, String.format("OrphanElement.addChildrenAndSetParent:: %s", message));
        throw new IllegalStateException(message);
    }

    @Override
    public void addChildrenAndSetParentsAtIndex(int index, List<IElement> children)
    {
        String message = String.format("OrphanElement %s can have no parent", this);
        Log.e(Constants.LOG_TAG, String.format("OrphanElement.addChildrenAndSetParentsAtIndex:: %s", message));
        throw new IllegalStateException(message);
    }

    @Override
    public boolean isDescendantOf(IElement ancestor)
    {
        Log.e(Constants.LOG_TAG, String.format("OrphanElement.isDescendantOf:: OrphanElement %s has no parent", this));
        return false;
    }
}
