package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.Toolbox.Constants;

public class Location extends Element
{
    private List<Location> children = new ArrayList<>();
    private Location parent = null;

    public Location(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public List<Location> getChildren()
    {
        return this.children;
    }

    public void setChildren(List<Location> children)
    {
        for(Location child : children)
        {
            Log.d(Constants.LOG_TAG,  String.format("Location.setChildren:: node[%s] - child[%s] set.", this.getName(), child.getName()));
        }
        this.children = children;
    }

    public Location getParent()
    {
        return this.parent;
    }

    private void setParent(Location parent)
    {
        Log.d(Constants.LOG_TAG,  String.format("Location.setParent:: node[%s] - parent[%s] set.", this.getName(), parent.getName()));
        this.parent = parent;
    }

    public Location createLocation(String name)
    {
        Location location = null;

        if(!name.trim().isEmpty())
        {
            name = name.trim();

            Log.d(Constants.LOG_TAG,  String.format("Location.createLocation:: node[%s] created.", name));
            location = new Location(name, UUID.randomUUID());
        }

        return location;
    }

    public void addChild(Location child)
    {
        child.setParent(this);

        Log.d(Constants.LOG_TAG,  String.format("Location.addChild:: node[%s] - child[%s] added.", this.getName(), child.getName()));
        this.children.add(0, child);
    }

    public void addChildren(List<Location> children)
    {
        this.children.addAll(children);
        for(Location child : children)
        {
            Log.d(Constants.LOG_TAG,  String.format("Location.addChildren:: node[%s] - child[%s] added.", this.getName(), child.getName()));
        }

        for (Location child :children)
        {
            child.setParent(this);
        }
    }

    public void insertNode(Location location)
    {
        location.setChildren(new ArrayList<>(this.getChildren()));

        Log.d(Constants.LOG_TAG,  String.format("Location.insertNode:: node[%s] - children cleared.", this.getName()));
        this.getChildren().clear();

        this.addChild(location);
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
            Log.d(Constants.LOG_TAG,  String.format("Location.deleteNodeAndChildren:: node[%s] removed from parent[%S].", this.getName(), this.parent.getName()));
            this.parent.getChildren().remove(this);

            for(Location child : this.getChildren())
            {
                Log.d(Constants.LOG_TAG,  String.format("Location.deleteNodeAndChildren:: node[%s] - child[%s] removed.", this.getName(), child.getName()));
            }
            this.getChildren().clear();
        }
    }

    public void removeNode()
    {
        if (this.parent != null)
        {
            int index = this.parent.getChildren().indexOf(this);

            Log.d(Constants.LOG_TAG,  String.format("Location.removeNode:: node[%s] removed from parent[%S].", this.getName(), this.parent.getName()));
            this.parent.getChildren().remove(this);

            for (Location location : getChildren())
            {
                location.setParent(this.parent);
            }

            for(Location child : this.getChildren())
            {
                Log.d(Constants.LOG_TAG,  String.format("Location.addChildren:: node[%s] - child[%s] added.", this.parent.getName(), child.getName()));
            }
            this.parent.getChildren().addAll(index, this.getChildren());
        }

        this.getChildren().clear();
    }

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

