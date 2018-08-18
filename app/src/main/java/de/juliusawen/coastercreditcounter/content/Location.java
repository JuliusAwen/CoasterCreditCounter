package de.juliusawen.coastercreditcounter.content;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Location extends Element
{
    private List<Location> children = new ArrayList<>();
    private Location parent = null;

    public Location(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public void addChild(Location child)
    {
        child.setParent(this);
        this.children.add(0, child);
    }

    public Location createChild(String childName)
    {
        Location child = null;

        if(!childName.trim().isEmpty())
        {
            childName = childName.trim();
            child = new Location(childName, UUID.randomUUID());

            this.addChild(child);
        }

        return child;
    }

    public List<Location> getChildren()
    {
        return this.children;
    }

    public Location getParent()
    {
        return this.parent;
    }

    private void setParent(Location parent)
    {
        this.parent = parent;
    }

    public void addChildren(List<Location> children)
    {
        for (Location child :children)
        {
            child.setParent(this);
        }

        this.children.addAll(children);
    }

    public void setChildren(List<Location> children)
    {
        this.children = children;
    }

//    public Location getRoot()
//    {
//        if(this.parent == null)
//        {
//            return this;
//        }
//
//        return this.parent.getRoot();
//    }

    public void deleteNodeAndChildren()
    {
        if (this.parent != null)
        {
            this.parent.getChildren().remove(this);
            this.getChildren().clear();
        }
    }

//    public void removeNode()
//    {
//        if (this.parent != null)
//        {
//            int index = this.parent.getChildren().indexOf(this);
//            this.parent.getChildren().remove(this);
//
//            for (Location location : getChildren())
//            {
//                location.setParent(this.parent);
//            }
//
//            this.parent.getChildren().addAll(index, this.getChildren());
//        }
//
//        this.getChildren().clear();
//    }
//
//    private Location removeRootNode()
//    {
//        if (parent != null)
//        {
//            throw new IllegalStateException("removeRootNode not called on root");
//        }
//
//        Location newParent = null;
//
//        if (!getChildren().isEmpty())
//        {
//            newParent = getChildren().get(0);
//            newParent.setParent(null);
//
//            getChildren().remove(0);
//
//            for (Location child : getChildren())
//            {
//                child.setParent(newParent);
//            }
//
//            newParent.getChildren().addAll(getChildren());
//            Content.getInstance().deleteElement(this);
//            Content.getInstance().setLocationRoot(newParent);
//        }
//
//        this.getChildren().clear();
//
//        return newParent;
//    }
}

