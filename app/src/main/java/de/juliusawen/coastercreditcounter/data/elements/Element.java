package de.juliusawen.coastercreditcounter.data.elements;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.globals.Constants;

public abstract class Element
{
    public boolean undoIsPossible = false;

    private String name;
    private UUID uuid;

    private Element parent = null;
    private List<Element> children = new ArrayList<>();

    private Element backupParent = null;
    private List<Element> backupChildren = new ArrayList<>();
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

        if(!element.isInstance(this.getClass()))
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
                "[%s \"%s\" (%s) - [%d]children]",
                this.getClass().getSimpleName(),
                this.getName(),
                this.getUuid(),
                this.getChildCount()
        );
    }

    public UUID getUuid()
    {
        return this.uuid;
    }

    public <T extends Element> boolean isInstance(Class<T> type)
    {
        return type.isInstance(this);
    }

    public long getItemId()
    {
        return this.itemId;
    }

    public Element getRootElement()
    {
        if(!this.isRootElement())
        {
            Log.v(Constants.LOG_TAG,  String.format("Element.getRootLocation:: %s is not root element - calling parent", this));
            return this.parent.getRootElement();
        }
        else
        {
            return this;
        }
    }

    public boolean isRootElement()
    {
        return this.getParent() == null;
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
        Log.v(Constants.LOG_TAG, String.format("Element.addChildren:: called with [%d] children", children.size()));
        int increment = 0;
        for (Element child : children)
        {
            if(this.addChild(index + increment, child))
            {
                child.setParent(this);
                increment ++;
            }
        }
    }

    public void addChild(Element child)
    {
        this.addChild(this.getChildCount(), child);
    }

    private boolean addChild(int index, Element child)
    {
        if(!this.isInstance(OrphanElement.class))
        {
            if(!this.containsChild(child))
            {
                if(child.getParent() != null)
                {
                    Log.w(Constants.LOG_TAG, String.format("Element.addChild:: %s already has parent %s - setting new parent %s", child, child.getParent(), this));
                }
                child.setParent(this);

                Log.v(Constants.LOG_TAG, String.format("Element.addChild:: %s -> child %s added", this, child));
                this.children.add(index, child);
                return true;
            }
            else
            {
                Log.w(Constants.LOG_TAG, String.format("Element.addChild:: %s already contains child [%s]", this, child));
                return false;
            }
        }
        else
        {
            String errorMessage = String.format(Locale.getDefault(), "type mismatch: %s is instance of <OrphanElement> - adding not possible", child);
            Log.e(Constants.LOG_TAG, "Element.addChild:: " + errorMessage);
            throw new IllegalStateException(errorMessage);
        }

    }

    public void reorderChildren(List<Element> children)
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

    public void addChildToOrphanElement(Element child)
    {
        if(this.isInstance(OrphanElement.class))
        {
            this.getChildren().add(child);
            Log.v(Constants.LOG_TAG, String.format("Element.addChildToOrphanElement:: %s -> child %s added", this, child));
        }
        else
        {
            String errorMessage = String.format(Locale.getDefault(), "type mismatch: %s is not instance of <OrphanElement>", child);
            Log.e(Constants.LOG_TAG, "Element.addChildToOrphanElement:: " + errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }

    public boolean containsChild(Element child)
    {
        return this.getChildren().contains(child);
    }

    public int indexOfChild(Element child)
    {
        return this.getChildren().indexOf(child);
    }

    public boolean hasChildren()
    {
        return !this.getChildren().isEmpty();
    }

    public boolean hasChildrenOfInstance(Class<? extends Element> type)
    {
        return !this.getChildrenOfType(type).isEmpty();
    }

    public int getChildCount()
    {
        return this.getChildren().size();
    }

    public int getChildCountOfType(Class<? extends Element> type)
    {
        return this.getChildrenOfType(type).size();
    }

    public List<Element> getChildren()
    {
        return this.children;
    }

    public List<Element> getChildrenOfType(Class<? extends Element> type)
    {
        List<Element> children = new ArrayList<>();
        for(Element element : this.getChildren())
        {
            if(element.isInstance(type))
            {
                children.add(element);
            }
        }
        return children;
    }

    public <T extends Element> List<T> getChildrenAsType(Class<T> type)
    {
        List<T> children = new ArrayList<>();
        for(Element element : this.getChildren())
        {
            if(element.isInstance(type))
            {
                children.add(type.cast(element));
            }
        }
        return children;
    }

    public void deleteChildren(List<Element> children)
    {
        for(Element child : children)
        {
            this.deleteChild(child);
        }
    }

    public void deleteChild(Element child)
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

    public Element getParent()
    {
        return this.parent;
    }

    private void setParent(Element parent)
    {
        Log.v(Constants.LOG_TAG,  String.format("Element.setParent:: %s -> parent %s set", this, parent));
        this.parent = parent;
    }

    public void insertElements(Element newElement, List<Element> children)
    {
        Log.d(Constants.LOG_TAG, String.format("Element.insertElements:: inserting %s into %s", newElement, this));
        newElement.addChildren(new ArrayList<>(children));
        this.deleteChildren(children);
        this.addChild(newElement);
    }

    public void relocateElement(Element newParent)
    {
        this.getParent().getChildren().remove(this);
        newParent.addChild(this);
    }

    public boolean deleteElementAndChildren()
    {
        Log.d(Constants.LOG_TAG, String.format("Element.removeElementAndChildren:: deleting %s and children", this));
        if (this.parent != null)
        {
            this.backupChildren = new ArrayList<>(this.getChildren());
            this.backupParent = this.parent;
            this.undoIndex = this.parent.indexOfChild(this);
            this.undoIsPossible = true;
            this.parent.deleteChild(this);
            this.deleteChildren(this.backupChildren);
            return true;
        }
        Log.e(Constants.LOG_TAG, String.format("Element.removeElementAndChildren:: unable to delete %s as it is the root element", this));
        return false;
    }

    public boolean undoDeleteElementAndChildren()
    {
        Log.d(Constants.LOG_TAG, String.format("Element.undoDeleteElementAndChildren:: restoring %s and children", this));
        boolean success = false;
        if(this.undoIsPossible && this.backupParent != null && this.undoIndex != -1)
        {
            this.addChildren(this.backupChildren);
            this.backupParent.addChild(this.undoIndex, this);
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
            this.undoIndex = this.parent.indexOfChild(this);
            this.undoIsPossible = true;
            this.parent.deleteChild(this);
            this.parent.addChildren(this.undoIndex, this.backupChildren);
            this.deleteChildren(this.backupChildren);
            return true;
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Element.removeElement:: unable to remove %s as it is the root element", this));
            return false;
        }
    }

    public boolean undoRemoveElement()
    {
        Log.d(Constants.LOG_TAG, String.format("Element.undoRemoveElement:: restoring %s", this));
        boolean success = false;
        if(this.undoIsPossible && this.backupParent != null && this.undoIndex != -1)
        {
            this.addChildren(this.backupChildren);
            this.backupParent.deleteChildren(this.backupChildren);
            this.backupParent.addChild(this.undoIndex, this);
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

    public static void sortElementsByNameAscending(List<? extends Element> elements)
    {
        if(elements.size() > 1)
        {
            Collections.sort(elements, new Comparator<Element>()
            {
                @Override
                public int compare(Element element1, Element element2)
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

    public static void sortElementsByNameDescending(List<? extends Element> elements)
    {
        if(elements.size() > 1)
        {
            Collections.sort(elements, new Comparator<Element>()
            {
                @Override
                public int compare(Element element1, Element element2)
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

    public static List<Element> sortElementsBasedOnComparisonList(List<Element> elementsToSort, List<Element> comparisonList)
    {
        if(elementsToSort.size() > 1)
        {
            Log.v(Constants.LOG_TAG,  String.format("Element.sortElementsBasedOnComparisonList:: sorted #[%d] elements based on comparison list containing [%d] elements",
                    elementsToSort.size(), comparisonList.size()));
            List<Element> sortedElements = new ArrayList<>();
            for(Element element : comparisonList)
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

    public static <T extends Element> List<T> convertElementsToType(List<? extends Element> elements, Class<T> type)
    {
        Log.v(Constants.LOG_TAG,String.format("Element.convertElementsToType:: casting [%d] elements to type <%s>", elements.size(), type.getSimpleName()));

        List<T> returnList = new ArrayList<>();
        for(Element element : elements)
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
