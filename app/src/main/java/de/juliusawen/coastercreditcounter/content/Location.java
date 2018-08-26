package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.Toolbox.Constants;

public class Location extends Element
{
    public boolean undoDeleteNodeAndChildrenPossible = false;
    public boolean undoRemoveNodePossible = false;

    private List<Location> deletedNodesChildren = new ArrayList<>();
    private Location deltedNodesParent = null;
    private int deletedNodesIndex = -1;

    private List<Location> removedNodesChildren = new ArrayList<>();
    private Location removedNodesParent = null;
    private int removedNodesIndex = -1;

    private Location parent = null;
    private List<Location> children = new ArrayList<>();

    private List<Park> parks = new ArrayList<>();

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
        else
        {
            Log.w(Constants.LOG_TAG,  String.format("Location.createLocation:: not able to create location with name[%s].", name));
        }

        return location;
    }

    public List<Location> getChildren()
    {
        return this.children;
    }

    public void setChildren(List<Location> children)
    {
        Log.v(Constants.LOG_TAG,  String.format("Location.insertNode:: node[%s] -> children cleared.", this.getName()));
        this.children.clear();

        this.addChildren(children);
    }

    public void addChildren(List<Location> children)
    {
        for (Location child : children)
        {
            this.addChild(child);
            child.setParent(this);
        }
    }

    public void addChildren(int index, List<Location> children)
    {
        int increment = -1;

        for (Location child : children)
        {
            increment ++;
            this.addChild(index + increment, child);
            child.setParent(this);
        }
    }

    public void addChild(Location child)
    {
        this.addChild(this.getChildren().size(), child);
    }

    public void addChild(int index, Location child)
    {
        child.setParent(this);

        Log.v(Constants.LOG_TAG,  String.format("Location.addChild:: node[%s] -> child[%s] added.", this.getName(), child.getName()));
        this.children.add(index, child);
    }

    public void addPark(Park park)
    {
        this.addPark(this.parks.size(), park);
    }

    public void addPark(int index, Park park)
    {
        Log.v(Constants.LOG_TAG,  String.format("Location.addPark:: node[%s] -> park[%s] added.", this.getName(), park.getName()));
        this.parks.add(index, park);
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

    public void insertNode(Location location)
    {
        this.insertNode(this.getChildren().size(), location);
    }

    public void insertNode(int index, Location location)
    {
        location.addChildren(new ArrayList<>(this.getChildren()));

        Log.v(Constants.LOG_TAG,  String.format("Location.insertNode:: node[%s] -> children cleared.", this.getName()));
        this.children.clear();

        this.addChild(index, location);
    }

    public boolean deleteNodeAndChildren()
    {
        if (this.parent != null)
        {
            this.deletedNodesChildren = new ArrayList<>(this.children);
            this.deltedNodesParent = this.parent;
            this.deletedNodesIndex = this.parent.getChildren().indexOf(this);
            this.undoDeleteNodeAndChildrenPossible = true;

            Log.v(Constants.LOG_TAG,  String.format("Location.deleteNodeAndChildren:: node[%s] -> removed from parent[%S].",
                    this.getName(), this.parent.getName()));
            this.parent.getChildren().remove(this);

            Log.v(Constants.LOG_TAG,  String.format("Location.deleteNodeAndChildren:: node[%s] -> children cleared.", this.getName()));
            this.getChildren().clear();

            return true;
        }

        Log.w(Constants.LOG_TAG,  String.format("Location.deleteNodeAndChildren:: node[%s] -> can not delete node as it is the root node.", this.getName()));
        return false;
    }

    public boolean undoDeleteNodeAndChildren()
    {
        boolean deleteNodeAndChildrenUndone = false;

        if(this.undoDeleteNodeAndChildrenPossible
                && this.deltedNodesParent != null
                && this.deletedNodesIndex != -1)
        {
            this.addChildren(this.deletedNodesChildren);
            this.deltedNodesParent.addChild(this.deletedNodesIndex, this);
            this.parent = this.deltedNodesParent;

            deleteNodeAndChildrenUndone = true;
        }
        {
            Log.w(Constants.LOG_TAG, String.format("Location.undoDeleteNodeAndChildren:: not able to undo delete node[%s] -" +
                                    " undoDeleteLocationAndChildrenPossible[%s]," +
                                    " deletedNodesChildrenSize[%d]," +
                                    " deletedNodesParent[%s]," +
                                    " deletedNodesIndex[%d]",
                            this.getName(),
                            this.undoDeleteNodeAndChildrenPossible,
                            this.removedNodesChildren.size(),
                            this.removedNodesParent != null ? this.removedNodesParent.getName() : null,
                            this.removedNodesIndex));
        }

        this.deletedNodesChildren.clear();
        this.deltedNodesParent = null;
        this.deletedNodesIndex = -1;
        this.undoDeleteNodeAndChildrenPossible = false;

        return deleteNodeAndChildrenUndone;
    }

    public boolean removeNode()
    {
        if (this.parent != null)
        {
            this.removedNodesChildren = new ArrayList<>(this.children);
            this.removedNodesParent = this.parent;
            this.removedNodesIndex = this.parent.getChildren().indexOf(this);
            this.undoRemoveNodePossible = true;

            Log.v(Constants.LOG_TAG,  String.format("Location.removeNode:: node[%s] -> removed from parent[%S].", this.getName(), this.parent.getName()));
            this.parent.getChildren().remove(this);

            this.parent.addChildren(this.removedNodesIndex, this.getChildren());

            Log.v(Constants.LOG_TAG,  String.format("Location.removeNode:: node[%s] -> children cleared.", this.getName()));
            this.getChildren().clear();

            return true;
        }

        Log.w(Constants.LOG_TAG,  String.format("Location.removeNode:: can not remove node[%s] as it is the root node.", this.getName()));
        return false;
    }

    public boolean undoRemoveNode()
    {
        boolean removeNodeUndone = false;

        if(this.undoRemoveNodePossible
                && this.removedNodesParent != null
                && this.removedNodesIndex != -1)
        {
            this.addChildren(this.removedNodesChildren);
            this.removedNodesParent.getChildren().removeAll(this.removedNodesChildren);
            this.removedNodesParent.addChild(this.removedNodesIndex, this);
            this.parent = this.removedNodesParent;

            removeNodeUndone = true;
        }
        else
        {
            Log.w(Constants.LOG_TAG, String.format("Location.undoRemoveNode:: not able to undo remove node[%s] -" +
                                    " undoRemoveNodePossible[%s]," +
                                    " removedNodesChildrenSize[%d]," +
                                    " removedNodesParent[%s]," +
                                    " removedNodesIndex[%d]",
                            this.getName(),
                            this.undoDeleteNodeAndChildrenPossible,
                            this.removedNodesChildren.size(),
                            this.removedNodesParent != null ? this.removedNodesParent.getName() : null,
                            this.removedNodesIndex));
        }

        this.removedNodesChildren.clear();
        this.removedNodesParent = null;
        this.removedNodesIndex = -1;
        this.undoRemoveNodePossible = false;

        return removeNodeUndone;
    }
}

