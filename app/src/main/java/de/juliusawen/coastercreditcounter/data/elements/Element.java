package de.juliusawen.coastercreditcounter.data.elements;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.NonNull;
import de.juliusawen.coastercreditcounter.data.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;

public abstract class Element implements IElement
{
    public boolean undoIsPossible = false;

    private String name;
    private UUID uuid;

    public IElement parent = null;
    public List<IElement> children = new ArrayList<>();

    private IElement backupParent = null;
    private List<IElement> backupChildren = new ArrayList<>();
    private int undoIndex = -1;

    private long itemId;

    protected Element(String name, UUID uuid)
    {
        this.setName(name);
        this.uuid = uuid;
        this.itemId = uuid.getMostSignificantBits() & Long.MAX_VALUE;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof Element))
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
        else return element.getName() != null && element.getName().equals(this.getName());

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
            jsonObject.put(Constants.JSON_STRING_PARENT, element.getParent() == null ? JSONObject.NULL : element.getParent().getUuid().toString());

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
        this.addChildAndSetParent(this.getChildCount(), child);
    }

    public void addChildrenAndSetParent(List<UUID> childUuids)
    {
        for(UUID childUuid : childUuids)
        {
            this.addChildAndSetParent(App.content.getContentByUuid(childUuid));
        }
    }

    public void addChildAndSetParent(UUID childUuid)
    {
        this.addChildAndSetParent(App.content.getContentByUuid(childUuid));
    }

    public void addChildrenAndSetParents(int index, List<IElement> children)
    {
        Log.v(Constants.LOG_TAG, String.format("Element.addChildrenAndSetParents:: called with [%d] children", children.size()));
        int increment = 0;
        for (IElement child : children)
        {
            this.addChildAndSetParent(index + increment, child);
            child.setParent(this);
            increment ++;
        }
    }

    public void addChildAndSetParent(int index, IElement child)
    {
        if(!OrphanElement.class.isInstance(this))
        {
            if(!this.containsChild(child))
            {
                if(child.getParent() != null)
                {
                    Log.w(Constants.LOG_TAG, String.format("Element.addChildAndSetParent:: %s already has parent %s - setting new parent %s", child, child.getParent(), this));
                }
                child.setParent(this);

                Log.v(Constants.LOG_TAG, String.format("Element.addChildAndSetParent:: %s -> child %s added", this, child));
                this.children.add(index, child);
            }
            else
            {
                Log.w(Constants.LOG_TAG, String.format("Element.addChildAndSetParent:: %s already contains child [%s]", this, child));
            }
        }
        else
        {
            String errorMessage = String.format(Locale.getDefault(), "type mismatch: %s is instance of <OrphanElement> - adding not possible", child);
            Log.e(Constants.LOG_TAG, "Element.addChildAndSetParent:: " + errorMessage);
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

    public void addChild(UUID childUuid)
    {
        this.addChild(App.content.getContentByUuid(childUuid));
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
        if(this.children.contains(null))
        {
            int sizeWithNullElements = this.children.size();
            this.children.removeAll(Collections.singleton(null));
            int sizeWithoutNullElements = this.children.size();
            int difference = sizeWithNullElements - sizeWithoutNullElements;

            Log.e(Constants.LOG_TAG, String.format("Element.getChildren:: [%d] null objects removed from children ", difference));
        }
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

    public void deleteChildren(List<IElement> children)
    {
        for(IElement child : children)
        {
            this.deleteChild(child);
        }
    }

    public void deleteChild(IElement child)
    {
        if(this.containsChild(child))
        {
            this.getChildren().remove(child);
            Log.v(Constants.LOG_TAG,  String.format("Element.deleteChild:: %s -> child %s removed", this, child));
        }
        else
        {
            String errorMessage = String.format("Element.deleteChild:: %s -> child %s not found", this, child);
            Log.e(Constants.LOG_TAG, errorMessage);
            throw new IllegalStateException(errorMessage);
        }
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
        this.deleteChildren(children);
        this.addChildAndSetParent(newElement);
    }

    public void relocateElement(Element newParent)
    {
        this.getParent().getChildren().remove(this);
        newParent.addChildAndSetParent(this);
    }

    public boolean deleteElementAndChildren()
    {
        Log.d(Constants.LOG_TAG, String.format("Element.removeElementAndChildren:: deleting %s and children", this));
        if (this.parent != null)
        {
            this.backupChildren = new ArrayList<>(this.getChildren());
            this.backupParent = this.parent;
            this.undoIndex = this.parent.getIndexOfChild(this);
            this.undoIsPossible = true;
            this.parent.deleteChild(this);
            this.deleteChildren(this.backupChildren);
            return true;
        }
        Log.e(Constants.LOG_TAG, String.format("Element.removeElementAndChildren:: unable to delete %s as it is root or orphan element", this));
        return false;
    }

    public boolean undoDeleteElementAndChildren()
    {
        Log.d(Constants.LOG_TAG, String.format("Element.undoDeleteElementAndChildren:: restoring %s and children", this));
        boolean success = false;
        if(this.undoIsPossible && this.backupParent != null && this.undoIndex != -1)
        {
            this.addChildrenAndSetParents(this.backupChildren);
            this.backupParent.addChildAndSetParent(this.undoIndex, this);
            this.parent = this.backupParent;
            success = true;
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("Element.undoDeleteElementAndChildren:: not able to restore %s -" +
                            " undoIsPossible[%s]," +
                            " backupChildrenCount[%d]," +
                            " backupParent[%s]," +
                            " undoIndex[%d]",
                    this,
                    this.undoIsPossible,
                    this.backupChildren.size(),
                    this.backupParent,
                    this.undoIndex));
        }
        this.backupChildren.clear();
        this.backupParent = null;
        this.undoIndex = -1;
        this.undoIsPossible = false;
        Log.d(Constants.LOG_TAG,  String.format("Element.undoDeleteElement:: restore %s success[%s]", this, success));
        return success;
    }

    public boolean removeElement()
    {
        Log.d(Constants.LOG_TAG, String.format("Element.removeElement:: removing %s", this));
        if (this.parent != null)
        {
            this.backupChildren = new ArrayList<>(this.getChildren());
            this.backupParent = this.parent;
            this.undoIndex = this.parent.getIndexOfChild(this);
            this.undoIsPossible = true;
            this.parent.deleteChild(this);
            this.parent.addChildrenAndSetParents(this.undoIndex, this.backupChildren);
            this.deleteChildren(this.backupChildren);
            return true;
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Element.removeElement:: unable to remove %s as it is root or orphan element", this));
            return false;
        }
    }

    public boolean undoRemoveElement()
    {
        Log.d(Constants.LOG_TAG, String.format("Element.undoRemoveElement:: restoring %s", this));
        boolean success = false;
        if(this.undoIsPossible && this.backupParent != null && this.undoIndex != -1)
        {
            this.addChildrenAndSetParents(this.backupChildren);
            this.backupParent.deleteChildren(this.backupChildren);
            this.backupParent.addChildAndSetParent(this.undoIndex, this);
            this.parent = this.backupParent;
            success = true;
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("Element.undoRemoveElement:: not able to restore %s -" +
                            " undoIsPossible[%s]," +
                            " backupChildrenCount[%d]," +
                            " backupParent[%s]," +
                            " undoIndex[%d]",
                    this,
                    this.undoIsPossible,
                    this.backupChildren.size(),
                    this.backupParent,
                    this.undoIndex));
        }
        this.backupChildren.clear();
        this.backupParent = null;
        this.undoIndex = -1;
        this.undoIsPossible = false;
        Log.d(Constants.LOG_TAG,  String.format("Element.undoRemoveElement:: restore %s success[%s]", this, success));
        return success;
    }

    public boolean undoIsPossible()
    {
        return this.undoIsPossible;
    }

    public static void sortElementsByNameAscending(List<? extends IElement> elements)
    {
        if(elements.size() > 1)
        {
            Collections.sort(elements, new Comparator<IElement>()
            {
                @Override
                public int compare(IElement element1, IElement element2)
                {
                    return element1.getName().compareToIgnoreCase(element2.getName());
                }
            });
            Log.i(Constants.LOG_TAG,  String.format("Element.sortElementsByNameAscending:: [%s] elements sorted", elements.size()));
        }
        else
        {
            Log.v(Constants.LOG_TAG,  "Element.sortElementsByNameAscending:: not sorted - list contains only one element");
        }
    }

    public static void sortElementsByNameDescending(List<? extends IElement> elements)
    {
        if(elements.size() > 1)
        {
            Collections.sort(elements, new Comparator<IElement>()
            {
                @Override
                public int compare(IElement element1, IElement element2)
                {
                    return element2.getName().compareToIgnoreCase(element1.getName());
                }
            });
            Log.i(Constants.LOG_TAG,  String.format("Element.sortElementsByNameDescending:: [%s] elements sorted", elements.size()));
        }
        else
        {
            Log.v(Constants.LOG_TAG,  "Element.sortElementsByNameDescending:: not sorted - list contains only one element");
        }

    }

    public static List<IElement> sortElementsBasedOnComparisonList(List<IElement> elementsToSort, List<IElement> comparisonList)
    {
        if(elementsToSort.size() > 1)
        {
            Log.v(Constants.LOG_TAG,  String.format("Element.sortElementsBasedOnComparisonList:: sorted #[%d] elements based on comparison list containing [%d] elements",
                    elementsToSort.size(), comparisonList.size()));
            List<IElement> sortedElements = new ArrayList<>();
            for(IElement element : comparisonList)
            {
                if(elementsToSort.contains(element))
                {
                    sortedElements.add(elementsToSort.get(elementsToSort.indexOf(element)));
                }
            }
            return sortedElements;
        }
        else
        {
            Log.v(Constants.LOG_TAG,"Element.sortElementsBasedOnComparisonList:: not sorted - list contains only one element");
            return elementsToSort;
        }
    }

    public static <T extends IElement> List<T> convertElementsToType(List<? extends IElement> elements, Class<T> type)
    {
        Log.v(Constants.LOG_TAG,String.format("Element.convertElementsToType:: casting [%d] elements to type <%s>", elements.size(), type.getSimpleName()));

        List<T> returnList = new ArrayList<>();
        for(IElement element : elements)
        {
            try
            {
                returnList.add(type.cast(element));
            }
            catch(ClassCastException e)
            {
                String errorMessage = String.format("%s is not of type <%s>", element, type.getSimpleName());
                Log.v(Constants.LOG_TAG, "Element.convertElementsToType:: " + errorMessage);
                throw new IllegalStateException(errorMessage + "\n" + e);
            }
        }
        return returnList;
    }
}
