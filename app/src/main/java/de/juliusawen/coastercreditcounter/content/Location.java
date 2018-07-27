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

    public Location addChild(Location child)
    {
        child.setParent(this);
        this.children.add(child);
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


//    public Location getRoot()
//    {
//        if(this.parent == null)
//        {
//            return this;
//        }
//
//        return this.parent.getRoot();
//    }

//    public void deleteNode()
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
//        else
//        {
//           deleteRootNode();
//        }
//
//        this.getChildren().clear();
//    }

//    public Location deleteRootNode()
//    {
//        if (parent != null)
//        {
//            throw new IllegalStateException("deleteRootNode not called on root");
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
//        }
//
//        this.getChildren().clear();
//
//        return newParent;
//    }
}

