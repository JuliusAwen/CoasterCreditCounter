package de.juliusawen.coastercreditcounter.backend.objects.elements;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.NonNull;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.globals.Constants;

public abstract class Element implements IElement
{
    private final UUID uuid;
    private final long itemId;

    private String name;

    private IElement parent = null;
    private final List<IElement> children = new ArrayList<>();

    protected Element(String name, UUID uuid)
    {
        this.setName(name);
        this.uuid = uuid;
        this.itemId = uuid.getMostSignificantBits() & Long.MAX_VALUE;
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

        if(!this.getClass().isInstance(element))
        {
            return false;
        }

        if(element.getUuid() != null && element.getUuid().equals(this.getUuid()))
        {
            return true;
        }
        else
        {
            return element.getName() != null && element.getName().equals(this.getName());
        }
    }

    @Override
    @NonNull
    public String toString()
    {
        return String.format(Locale.getDefault(), "[%s \"%s\"]", this.getClass().getSimpleName(), this.getName());
    }

    public static JSONObject toJson(IElement element, boolean parseChildren) throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(Constants.JSON_STRING_NAME, element.getName());
            jsonObject.put(Constants.JSON_STRING_UUID, element.getUuid().toString());

            JSONArray jsonArrayChildren = new JSONArray();

            if(element.getChildren().isEmpty() || !parseChildren)
            {
                jsonObject.put(Constants.JSON_STRING_CHILDREN, JSONObject.NULL);
            }
            else
            {
                for(IElement child : element.getChildren())
                {
                    jsonArrayChildren.put(child.getUuid().toString());
                }

                jsonObject.put(Constants.JSON_STRING_CHILDREN, jsonArrayChildren);
            }

            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();

            Log.e(Constants.LOG_TAG, String.format("Element.toJson:: creation for %s failed with JSONException [%s]", element, e.getMessage()));
            throw e;
        }
    }

    public String getName()
    {
        return this.name;
    }

    public boolean setName(String name)
    {
        if(!name.trim().isEmpty())
        {
            this.name = name.trim();
            return true;
        }
        else
        {
            Log.w(Constants.LOG_TAG,  String.format("Element.setName:: name[%s] is invalid", name));
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

    public long getItemId()
    {
        return this.itemId;
    }

    public void addChildrenAndSetParents(List<IElement> children)
    {
        for (IElement child : children)
        {
            this.addChildAndSetParent(child);
        }
    }

    public void addChildAndSetParent(IElement child)
    {
        this.addChildAndSetParentAtIndex(this.getChildCount(), child);
    }

    public void addChildrenAndSetParent(List<UUID> childUuids)
    {
        for(UUID childUuid : childUuids)
        {
            this.addChildAndSetParent(App.content.getContentByUuid(childUuid));
        }
    }

    public void addChildrenAndSetParentsAtIndex(int index, List<IElement> children)
    {
        Log.v(Constants.LOG_TAG, String.format("Element.addChildrenAndSetParentsAtIndex:: called with [%d] children", children.size()));
        int increment = 0;
        for (IElement child : children)
        {
            this.addChildAndSetParentAtIndex(index + increment, child);
            child.setParent(this);
            increment ++;
        }
    }

    public void addChildAndSetParentAtIndex(int index, IElement child)
    {
        if(!OrphanElement.class.isInstance(this))
        {
            if(!this.containsChild(child))
            {
                if(child.getParent() != null)
                {
                    Log.w(Constants.LOG_TAG, String.format("Element.addChildAndSetParentAtIndex:: %s already has parent %s - setting new parent %s", child, child.getParent(), this));
                }
                child.setParent(this);

                Log.v(Constants.LOG_TAG, String.format("Element.addChildAndSetParentAtIndex:: %s -> child %s added", this, child));
                this.children.add(index, child);
            }
            else
            {
                Log.w(Constants.LOG_TAG, String.format("Element.addChildAndSetParentAtIndex:: %s already contains child [%s]", this, child));
            }
        }
        else
        {
            String errorMessage = String.format(Locale.getDefault(), "type mismatch: %s is instance of <OrphanElement> - adding not possible", child);
            Log.e(Constants.LOG_TAG, "Element.addChildAndSetParentAtIndex:: " + errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }

    public void reorderChildren(List<? extends IElement> children)
    {
        if(!children.isEmpty())
        {
            this.getChildren().removeAll(children);
            this.getChildren().addAll(children);
            Log.v(Constants.LOG_TAG,
                    String.format("Element.reorderChildren:: %s -> [%d] children removed and then added again in given order", this, children.size()));
        }
        else
        {
            Log.w(Constants.LOG_TAG, String.format("Element.reorderChildren:: %s -> given list of children is empty", this));
        }
    }

    public void addChild(IElement child)
    {
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
        return !this.getChildrenOfType(type).isEmpty();
    }

    public int getChildCount()
    {
        return this.getChildren().size();
    }

    public int getChildCountOfType(Class<? extends IElement> type)
    {
        return this.getChildrenOfType(type).size();
    }

    public List<IElement> getChildren()
    {
        return this.children;
    }


    public List<IElement> getChildrenOfType(Class<? extends IElement> type)
    {
        List<IElement> children = new ArrayList<>();
        for(IElement element : this.getChildren())
        {
            if(type.isInstance(element))
            {
                children.add(element);
            }
        }
        return children;
    }

    public <T extends IElement> List<T> getChildrenAsType(Class<T> type)
    {
        List<T> children = new ArrayList<>();
        for(IElement element : this.getChildren())
        {
            if(type.isInstance(element))
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
        Log.v(Constants.LOG_TAG,  String.format("Element.setParent:: %s -> parent %s set", this, parent));
        this.parent = parent;
    }

    public void insertElements(Element newElement, List<IElement> children)
    {
        Log.d(Constants.LOG_TAG, String.format("Element.insertElements:: inserting %s into %s", newElement, this));
        newElement.addChildrenAndSetParents(new ArrayList<>(children));
        for(IElement child : children)
        {
            this.deleteChild(child);
        }
        this.addChildAndSetParent(newElement);
    }

    public void relocateElement(Element newParent)
    {
        this.parent.getChildren().remove(this);
        newParent.addChildAndSetParent(this);
    }

    public void deleteElementAndDescendants()
    {
        Log.d(Constants.LOG_TAG, String.format("Element.removeElementAndDescendants:: deleting %s and descendants", this));

        for(IElement child : new ArrayList<>(this.children))
        {
            child.deleteElementAndDescendants();
        }
        this.parent.deleteChild(this);
    }


    public void deleteChild(IElement child)
    {
        if(this.containsChild(child))
        {
            this.children.remove(child);
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
        Log.d(Constants.LOG_TAG, String.format("Element.removeElement:: removing %s", this));

        int index = this.parent.getIndexOfChild(this);

        this.parent.deleteChild(this);
        this.parent.addChildrenAndSetParentsAtIndex(index, new ArrayList<>(this.getChildren()));
        for(IElement child : new ArrayList<>(this.children))
        {
            this.deleteChild(child);
        }
    }
}
