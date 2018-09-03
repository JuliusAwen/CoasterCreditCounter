package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.toolbox.Constants;

public abstract class Element
{
    public boolean undoDeleteElementAndChildrenPossible = false;
    public boolean undoRemoveElementPossible = false;

    private List<Element> deletedElementsChildren = new ArrayList<>();
    private Element deletedElementsParent = null;
    private int deletedElementsIndex = -1;

    private List<Element> removedElementsChildren = new ArrayList<>();
    private Element removedElementsParent = null;
    private int removedElementsIndex = -1;

    private Element parent = null;
    private List<Element> children = new ArrayList<>();

    private String name;
    private UUID uuid;

    Element(String name, UUID uuid)
    {
        this.setName(name);
        this.uuid = uuid;
    }

    @Override
    public String toString()
    {
        return String.format(Locale.getDefault(), "[%s \"%s\"]", this.getClass().getSimpleName(), this.getName());
    }

    public <T> boolean isInstance(Class<T> type)
    {
        return type.isInstance(this);
    }

    public Element getRootElement()
    {
        if(this.parent != null)
        {
            Log.v(Constants.LOG_TAG,  String.format("Element.getRootElement:: %s is not root element - calling parent.", this));
            return this.parent.getRootElement();
        }
        else
        {
            return this;
        }
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        if(!name.trim().isEmpty())
        {
            this.name = name.trim();
        }
        else
        {
            Log.w(Constants.LOG_TAG,  String.format("Element.setName:: name[%s] is invalid.", name));
        }
    }

    public UUID getUuid()
    {
        return this.uuid;
    }

    public boolean containsChild(Element child)
    {
        return this.children.contains(child);
    }

    public int indexOfChild(Element child)
    {
        return this.children.indexOf(child);
    }

    public List<Element> getChildren()
    {
        return this.children;
    }

    public <T> List<Element> getChildrenOfInstance(Class<T> type)
    {
        List<Element> children = new ArrayList<>();

        for(Element element : this.children)
        {
            if(element.isInstance(type))
            {
                children.add(element);
            }
        }

        return children;
    }

    public boolean hasChildren()
    {
        return !this.children.isEmpty();
    }

    public <T> boolean hasChildrenOfInstance(Class<T> type)
    {
        return !this.getChildrenOfInstance(type).isEmpty();
    }

    public int getChildCount()
    {
        return this.children.size();
    }

    public <T> int getChildCountOfInstance(Class<T> type)
    {
        return this.getChildrenOfInstance(type).size();
    }

    public void setChildren(List<Element> children)
    {
        Log.v(Constants.LOG_TAG,  String.format("Element.setChildren:: %s -> children cleared.", this));
        this.children.clear();

        this.addChildren(children);
    }

    public void setChild(Element child)
    {
        Log.v(Constants.LOG_TAG,  String.format("Element.setChild:: %s -> children cleared.", this));
        this.children.clear();

        this.addChild(child);
    }

    public void addChildren(List<Element> children)
    {
        for (Element child : children)
        {
            this.addChild(child);
        }
    }

    private void addChildren(int index, List<Element> children)
    {
        int increment = -1;

        for (Element child : children)
        {
            increment ++;
            this.addChild(index + increment, child);
            child.setParent(this);
        }
    }

    public void addChild(Element child)
    {
        this.addChild(this.getChildren().size(), child);
    }

    private void addChild(int index, Element child)
    {
        child.setParent(this);

        Log.v(Constants.LOG_TAG,  String.format("Element.addChild:: %s -> child %s added.", this, child));
        this.children.add(index, child);
    }

    public Element getParent()
    {
        return this.parent;
    }

    private void setParent(Element parent)
    {
        Log.v(Constants.LOG_TAG,  String.format("Element.setParent:: %s -> parent %s set.", this, parent));
        this.parent = parent;
    }

    public void insertElement(Element newElement, List<Element> children)
    {
        newElement.addChildren(new ArrayList<>(children));

        Log.v(Constants.LOG_TAG,  String.format("Element.insertElement:: %s -> #[%d]children removed.", this, this.getChildCount()));
        this.children.removeAll(children);

        this.addChild(this.getChildren().size(), newElement);
    }

    public boolean deleteElementAndChildren()
    {
        if (this.parent != null)
        {
            this.deletedElementsChildren = new ArrayList<>(this.children);
            this.deletedElementsParent = this.parent;
            this.deletedElementsIndex = this.parent.getChildren().indexOf(this);
            this.undoDeleteElementAndChildrenPossible = true;

            Log.v(Constants.LOG_TAG,  String.format("Element.deleteElementAndChildren:: %s -> removed from parent %s.", this, this.parent));
            this.parent.getChildren().remove(this);

            Log.v(Constants.LOG_TAG,  String.format("Element.deleteElementAndChildren:: %s -> #[%d]children cleared.", this, this.getChildCount()));
            this.children.clear();

            return true;
        }

        Log.w(Constants.LOG_TAG,  String.format("Element.deleteElementAndChildren:: unable to delete %s as it is the root element.", this));
        return false;
    }

    public boolean undoDeleteElementAndChildren()
    {
        boolean success = false;

        if(this.undoDeleteElementAndChildrenPossible
                && this.deletedElementsParent != null
                && this.deletedElementsIndex != -1)
        {
            this.addChildren(this.deletedElementsChildren);
            this.deletedElementsParent.addChild(this.deletedElementsIndex, this);
            this.parent = this.deletedElementsParent;

            success = true;
        }
        {
            Log.w(Constants.LOG_TAG, String.format("Element.undoDeleteElementAndChildren:: not able to undo delete %s -" +
                            " undoDeleteElementAndChildrenPossible[%s]," +
                            " deletedElementsChildrenSize[%d]," +
                            " deletedElementsParent[%s]," +
                            " deletedElementsIndex[%d]",
                    this,
                    this.undoDeleteElementAndChildrenPossible,
                    this.removedElementsChildren.size(),
                    this.removedElementsParent != null ? this.removedElementsParent.getName() : null,
                    this.removedElementsIndex));
        }

        this.deletedElementsChildren.clear();
        this.deletedElementsParent = null;
        this.deletedElementsIndex = -1;
        this.undoDeleteElementAndChildrenPossible = false;

        return success;
    }

    public boolean removeElement()
    {
        if (this.parent != null)
        {
            this.removedElementsChildren = new ArrayList<>(this.children);
            this.removedElementsParent = this.parent;
            this.removedElementsIndex = this.parent.getChildren().indexOf(this);
            this.undoRemoveElementPossible = true;

            Log.v(Constants.LOG_TAG,  String.format("Element.removeElement:: %s -> removed from parent %s.", this, this.parent));
            this.parent.getChildren().remove(this);

            this.parent.addChildren(this.removedElementsIndex, this.getChildren());

            Log.v(Constants.LOG_TAG,  String.format("Element.removeElement:: %s -> #[%d]children cleared.", this, this.getChildCount()));
            this.children.clear();

            return true;
        }
        else
        {
            Log.w(Constants.LOG_TAG,  String.format("Element.removeElement:: unable to remove %s as it is the root element.", this));
            return false;
        }
    }

    public boolean undoRemoveElement()
    {
        boolean success = false;

        if(this.undoRemoveElementPossible
                && this.removedElementsParent != null
                && this.removedElementsIndex != -1)
        {
            this.addChildren(this.removedElementsChildren);
            this.removedElementsParent.getChildren().removeAll(this.removedElementsChildren);
            this.removedElementsParent.addChild(this.removedElementsIndex, this);
            this.parent = this.removedElementsParent;

            success = true;
        }
        else
        {
            Log.w(Constants.LOG_TAG, String.format("Element.undoRemoveElement:: not able to undo remove %s -" +
                            " undoRemoveElementPossible[%s]," +
                            " removedElementsChildrenSize[%d]," +
                            " removedElementsParent[%s]," +
                            " removedElementsIndex[%d]",
                    this,
                    this.undoDeleteElementAndChildrenPossible,
                    this.removedElementsChildren.size(),
                    this.removedElementsParent != null ? this.removedElementsParent.getName() : null,
                    this.removedElementsIndex));
        }

        this.removedElementsChildren.clear();
        this.removedElementsParent = null;
        this.removedElementsIndex = -1;
        this.undoRemoveElementPossible = false;

        return success;
    }
}
