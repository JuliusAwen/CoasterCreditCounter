package de.juliusawen.coastercreditcounter.globals;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.elements.IElement;
import de.juliusawen.coastercreditcounter.data.elements.Location;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.data.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.toolbox.Stopwatch;

public class Content
{
    private Map<UUID, IElement> elements = new HashMap<>();
    private Map<UUID, IElement> orphanElements = new HashMap<>();
    private List<AttractionCategory> attractionCategories = new ArrayList<>();

    public Location rootLocation;

    private static Content instance;

    private static IDatabaseWrapper databaseWrapper;

    static Content getInstance(IDatabaseWrapper databaseWrapper)
    {
        Content.databaseWrapper = databaseWrapper;
        Content.instance = new Content();
        return instance;
    }

    private Content()
    {
        Log.i(Constants.LOG_TAG,"Content.Constructor:: <Content> instantiated");
        Stopwatch stopwatchInitializeContent = new Stopwatch(true);


        Log.i(Constants.LOG_TAG, "Content.Constructor:: fetching content...");
        Stopwatch stopwatchFetchContent = new Stopwatch(true);
        Content.databaseWrapper.fetchContent(this);
        this.setRootLocation(Content.databaseWrapper.fetchRootLocation());
        Log.i(Constants.LOG_TAG,  String.format("Content.Constructor:: fetching content took [%d]ms", stopwatchFetchContent.stop()));


        Log.i(Constants.LOG_TAG, "Content.Constructor:: flattening content tree...");
        Stopwatch stopwatchFlattenContentTree = new Stopwatch(true);
        this.flattenContentTree(this.rootLocation);
        Log.i(Constants.LOG_TAG,  String.format("Content.Constructor:: flattening content tree took [%d]ms", stopwatchFlattenContentTree.stop()));


        Log.i(Constants.LOG_TAG, String.format("Content.Constructor:: initializing content took [%d]ms", stopwatchInitializeContent.stop()));
    }

    public Location getRootLocation()
    {
        return this.rootLocation;
    }

    private void setRootLocation(Location location)
    {
        Log.v(Constants.LOG_TAG,  String.format("Content.setRootLocation:: %s set as root", location));
        this.rootLocation = location;
    }

    public <T extends IElement> List<T> getContentAsType(Class<T> type)
    {
        List<T> content = new ArrayList<>();
        for(IElement element : this.elements.values())
        {
            if(type.isInstance(element))
            {
                content.add(type.cast(element));
            }
        }
        return content;
    }

    public List<AttractionCategory> getAttractionCategories()
    {
        return this.attractionCategories;
    }

    public void setAttractionCategories(List<AttractionCategory> attractionCategories)
    {
        this.attractionCategories = attractionCategories;

        Log.v(Constants.LOG_TAG,  String.format("Content.setAttractionCategories:: [%d]AttractionCategories set", attractionCategories.size()));
    }

    public void addAttractionCategory(AttractionCategory attractionCategory)
    {
        this.addAttractionCategory(0, attractionCategory);
    }

    public void addAttractionCategory(int index, AttractionCategory attractionCategory)
    {
        this.attractionCategories.add(index, attractionCategory);
        Log.v(Constants.LOG_TAG,  String.format("Content.addAttractionCategory:: %s added", attractionCategory));
    }

    public void removeAttractionCategory(AttractionCategory attractionCategory)
    {
        this.attractionCategories.remove(attractionCategory);
        Log.d(Constants.LOG_TAG,  String.format("Content.removeAttractionCategory:: %s removed", attractionCategory));
    }

    public <T extends OrphanElement> List<T> getOrphanElementsAsType(Class<T> type)
    {
        List<T> orphanElementsOfType = new ArrayList<>();
        for(IElement orphanElement : this.orphanElements.values())
        {
            if(type.isInstance(orphanElement))
            {
                orphanElementsOfType.add(type.cast(orphanElement));
            }
        }

        return orphanElementsOfType;
    }

    private IElement getOrphanElementByUuid(UUID uuid)
    {
        if(this.orphanElements.containsKey(uuid))
        {
            return this.orphanElements.get(uuid);
        }

        return this.getAttractionCategoryByUuid(uuid);
    }

    private AttractionCategory getAttractionCategoryByUuid(UUID uuid)
    {
        for(AttractionCategory attractionCategory : this.attractionCategories)
        {
            if(attractionCategory.getUuid().equals(uuid))
            {
                return attractionCategory;
            }
        }

        return null;
    }

    public void addOrphanElement(IElement orphanElement)
    {
        if(OrphanElement.class.isInstance(orphanElement))
        {
            if(!this.orphanElements.containsKey(orphanElement.getUuid()))
            {
                this.orphanElements.put(orphanElement.getUuid(), orphanElement);
                Log.v(Constants.LOG_TAG, String.format("Content.addOrphanElement:: %s added to orphan elements", orphanElement));
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("Content.addOrphanElement:: %s already exists", orphanElement));
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("Content.addOrphanElement:: %s is not of type <OrphanElement>", orphanElement));
        }

    }

    public void removeOrphanElements(List<? extends OrphanElement> orphanElements)
    {
        Log.v(Constants.LOG_TAG,  String.format("Content.removeOrphanElements:: removing [%d] orphan elements", orphanElements.size()));

        for(OrphanElement orphanElement : orphanElements)
        {
            this.removeOrphanElement(orphanElement);
        }
    }

    public void removeOrphanElement(OrphanElement orphanElement)
    {
        if(this.orphanElements.containsValue(orphanElement))
        {
            this.orphanElements.remove(orphanElement.getUuid());
            Log.v(Constants.LOG_TAG,  String.format("Content.removeOrphanElement:: %s removed from orphan elements", orphanElement));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Content.removeOrphanElement:: %s not found in OrphanElements", orphanElement));
        }
    }


    private void flattenContentTree(IElement element)
    {
        this.addElement(element);
        for (IElement child : element.getChildren())
        {
            this.flattenContentTree(child);
        }
    }

    public ArrayList<String> getUuidStringsFromElements(List<IElement> elements)
    {
        ArrayList<String> uuidStrings = new ArrayList<>();
        for(IElement element : elements)
        {
            uuidStrings.add(element.getUuid().toString());
        }
        return uuidStrings;
    }

    public List<IElement> fetchElementsByUuidStrings(List<String> uuidStrings)
    {
        Stopwatch stopwatch = new Stopwatch(true);

        List<IElement> elements = new ArrayList<>();
        for(String uuidString : uuidStrings)
        {
            elements.add(this.getElementByUuid(UUID.fromString(uuidString)));
        }

        Log.v(Constants.LOG_TAG, String.format("Content.fetchElementsByUuidStrings:: fetching [%d] elements took [%d]ms ", uuidStrings.size(), stopwatch.stop()));
        return elements;
    }

    public IElement getElementByUuid(UUID uuid)
    {
        IElement element;

        if(this.elements.containsKey(uuid))
        {
            element = this.elements.get(uuid);
        }
        else
        {
            element = this.getOrphanElementByUuid(uuid);
        }

        if(element != null)
        {
            return element;
        }
        else
        {
            Log.w(Constants.LOG_TAG, String.format("Content.getElementByUuid:: No element found for uuid[%s]", uuid));
            return null;
        }
    }

    public void addElementAndChildren(IElement element)
    {
        for(IElement child : element.getChildren())
        {
            this.addElementAndChildren(child);
        }
        this.addElement(element);
    }

    public void addElements(List<IElement> elements)
    {
        for(IElement element : elements)
        {
            this.addElement(element);
        }
    }

    public void addElement(IElement element)
    {
        if(!OrphanElement.class.isInstance(element))
        {
            Log.v(Constants.LOG_TAG,  String.format("Content.addElement:: %s added", element));
            this.elements.put(element.getUuid(), element);
        }
        else
        {
            String errorMessage = String.format("adding %s requested -- DEPRECATED: use AddOrphanElement!", element);
            Log.e(Constants.LOG_TAG,  "Content.addElement:: )" + errorMessage);

            throw new IllegalStateException(errorMessage);
        }
    }

    public boolean removeElementAndChildren(IElement element)
    {
        for(IElement child : element.getChildren())
        {
            if(!this.removeElementAndChildren(child))
            {
                return false;
            }
        }
        return this.removeElement(element);
    }

    public boolean removeElement(IElement element)
    {
        if(this.elements.containsKey(element.getUuid()))
        {
            Log.v(Constants.LOG_TAG,  String.format("Content.removeElement:: %s removed", element));
            this.elements.remove(element.getUuid());
            return true;
        }
        return false;
    }
}