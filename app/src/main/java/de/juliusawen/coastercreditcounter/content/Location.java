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

    public static Location createLocation(String name)
    {
        Location location = null;

        if(!name.trim().isEmpty())
        {
            name = name.trim();

            Log.v(Constants.LOG_TAG,  String.format("Location.createLocation:: location[%s] created.", name));
            location = new Location(name, UUID.randomUUID());
        }

        return location;
    }

    public List<Location> getChildren()
    {
        return this.children;
    }

    public void setChildren(List<Location> children)
    {
        Log.v(Constants.LOG_TAG,  String.format("Location.setChildren:: node[%s] -> children set.", this.getName()));
        this.children = children;
    }

    public Location getParent()
    {
        return this.parent;
    }

    private void setParent(Location parent)
    {
        Log.v(Constants.LOG_TAG,  String.format("Location.setParent:: node[%s] -> parent[%s] set.", this.getName(), parent.getName()));
        this.parent = parent;
    }

    public void addChild(Location child)
    {
        child.setParent(this);

        Log.v(Constants.LOG_TAG,  String.format("Location.addChild:: node[%s] -> child[%s] added.", this.getName(), child.getName()));
        this.children.add(0, child);
    }

    public void addChildren(List<Location> children)
    {
        for (Location child :children)
        {
            this.addChild(child);
            child.setParent(this);
        }
    }

    public void insertNode(Location location)
    {
        location.setChildren(new ArrayList<>(this.getChildren()));

        Log.v(Constants.LOG_TAG,  String.format("Location.insertNode:: node[%s] -> children cleared.", this.getName()));
        this.getChildren().clear();

        this.addChild(location);
    }

    public void deleteNodeAndChildren()
    {
        if (this.parent != null)
        {
            Log.v(Constants.LOG_TAG,  String.format("Location.deleteNodeAndChildren:: node[%s] -> children cleared.", this.getName()));
            this.getChildren().clear();

            Log.v(Constants.LOG_TAG,  String.format("Location.deleteNodeAndChildren:: node[%s] -> removed from parent[%S].", this.getName(), this.parent.getName()));
            this.parent.getChildren().remove(this);
        }
    }

    public void removeNode()
    {
        if (this.parent != null)
        {
            int index = this.parent.getChildren().indexOf(this);

            Log.v(Constants.LOG_TAG,  String.format("Location.removeNode:: node[%s] -> removed from parent[%S].", this.getName(), this.parent.getName()));
            this.parent.getChildren().remove(this);

            for (Location location : getChildren())
            {
                location.setParent(this.parent);
            }

            Log.v(Constants.LOG_TAG,  String.format("Location.removeNode:: node[%s] -> children added.", this.parent.getName()));
            this.parent.getChildren().addAll(index, this.getChildren());
        }

        Log.v(Constants.LOG_TAG,  String.format("Location.removeNode:: node[%s] -> children cleared.", this.getName()));
        this.getChildren().clear();
    }
}

