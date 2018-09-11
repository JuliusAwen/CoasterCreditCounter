package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

public abstract class Element
{
    public boolean undoPossible = false;
    private Element backupParent = null;
    private List<Element> backupChildren = new ArrayList<>();
    private int undoIndex = -1;

    private Element parent = null;
    private List<Element> children = new ArrayList<>();

    private String name;
    private UUID uuid;

    protected Element(String name, UUID uuid)
    {
        this.setName(name);
        this.uuid = uuid;
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
                "[%s \"%s\" (%s) - #[%d] children]",
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

    public Element getRootElement()
    {
        if(!this.isRootElement())
        {
            Log.v(Constants.LOG_TAG,  String.format("Element.getRootElement:: %s is not root element - calling parent", this));
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
        Log.v(Constants.LOG_TAG, String.format("Element.addChildren:: called with #[%d] children", children.size()));

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
        if(!this.containsChild(child))
        {
            child.setParent(this);

            Log.v(Constants.LOG_TAG,  String.format("Element.addChild:: %s -> child %s added", this, child));
            this.children.add(index, child);
            return true;
        }
        else
        {
            Log.w(Constants.LOG_TAG, String.format("Element.addChild:: %s already contains child [%s]", this, child));
            return false;
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

    public <T extends Element> boolean hasChildrenOfInstance(Class<T> type)
    {
        return !this.getChildrenOfInstance(type).isEmpty();
    }

    public int getChildCount()
    {
        return this.getChildren().size();
    }

    public <T extends Element> int getChildCountOfInstance(Class<T> type)
    {
        return this.getChildrenOfInstance(type).size();
    }

    public List<Element> getChildren()
    {
        return this.children;
    }

    public <T extends Element> List<Element> getChildrenOfInstance(Class<T> type)
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

    public void deleteChildren(List<Element> children)
    {
        for(Element child : children)
        {
            this.deleteChild(child);
        }
    }

    private void deleteChild(Element child)
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
        Log.d(Constants.LOG_TAG, String.format("Element.deleteElementAndChildren:: deleting %s and children", this));

        if (this.parent != null)
        {
            this.backupChildren = new ArrayList<>(this.getChildren());
            this.backupParent = this.parent;
            this.undoIndex = this.parent.indexOfChild(this);
            this.undoPossible = true;

            this.parent.deleteChild(this);
            this.deleteChildren(this.backupChildren);

            return true;
        }

        Log.e(Constants.LOG_TAG, String.format("Element.deleteElementAndChildren:: unable to delete %s as it is the root element", this));
        return false;
    }

    public boolean undoDeleteElementAndChildren()
    {
        Log.d(Constants.LOG_TAG, String.format("Element.undoDeleteElementAndChildren:: restoring %s and children", this));

        boolean success = false;

        if(this.undoPossible
                && this.backupParent != null
                && this.undoIndex != -1)
        {
            this.addChildren(this.backupChildren);
            this.backupParent.addChild(this.undoIndex, this);
            this.parent = this.backupParent;

            success = true;
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("Element.undoDeleteElementAndChildren:: not able to restore %s -" +
                            " undoPossible[%s]," +
                            " backupChildrenCount[%d]," +
                            " backupParent[%s]," +
                            " undoIndex[%d]",
                    this,
                    this.undoPossible,
                    this.backupChildren.size(),
                    this.backupParent,
                    this.undoIndex));
        }

        this.backupChildren.clear();
        this.backupParent = null;
        this.undoIndex = -1;
        this.undoPossible = false;

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
            this.undoPossible = true;

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

        if(this.undoPossible
                && this.backupParent != null
                && this.undoIndex != -1)
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
                            " undoPossible[%s]," +
                            " backupChildrenCount[%d]," +
                            " backupParent[%s]," +
                            " undoIndex[%d]",
                    this,
                    this.undoPossible,
                    this.backupChildren.size(),
                    this.backupParent,
                    this.undoIndex));
        }

        this.backupChildren.clear();
        this.backupParent = null;
        this.undoIndex = -1;
        this.undoPossible = false;

        Log.d(Constants.LOG_TAG,  String.format("Element.undoRemoveElement:: restore %s success[%s]", this, success));
        return success;
    }

    public static void sortElementsByNameAscending(List<? extends Element> elements)
    {
        Collections.sort(elements, new Comparator<Element>()
        {
            @Override
            public int compare(Element element1, Element element2)
            {
                return element1.getName().compareToIgnoreCase(element2.getName());
            }
        });
        Log.i(Constants.LOG_TAG,  String.format("Element.sortElementsByNameAscending:: #[%s] elements sorted", elements.size()));
    }

    public static void sortElementsByNameDescending(List<? extends Element> elements)
    {
        Collections.sort(elements, new Comparator<Element>()
        {
            @Override
            public int compare(Element element1, Element element2)
            {
                return element2.getName().compareToIgnoreCase(element1.getName());
            }
        });
        Log.i(Constants.LOG_TAG,  String.format("Element.sortElementsByNameDescending:: #[%s] elements sorted", elements.size()));
    }

    public static List<Element> sortElementsBasedOnComparisonList(List<Element> elementsToSort, List<Element> comparisonList)
    {
        Log.d(Constants.LOG_TAG,  String.format("Element.sortElementsBasedOnComparisonList:: sorted #[%d] elements based on comparison list containing #[%d] elements",
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
}
