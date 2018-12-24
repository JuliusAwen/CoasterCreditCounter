package de.juliusawen.coastercreditcounter.globals;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.elements.IElement;
import de.juliusawen.coastercreditcounter.data.elements.Location;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.data.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.globals.persistency.Persistency;
import de.juliusawen.coastercreditcounter.toolbox.Stopwatch;

public class Content
{
    private Map<UUID, IElement> elementsByUuid = new HashMap<>();
    private List<AttractionCategory> attractionCategories = new ArrayList<>();
    private Location rootLocation;

    private Map<UUID, IElement> backupElements = null;
    private List<AttractionCategory> backupAttractionCategories = null;
    private Location backupRootLocation = null;

    private Persistency persistency;

    private static Content instance;

    public static Content getInstance(Persistency persistency)
    {
        if(Content.instance == null)
        {
            Content.instance = new Content(persistency);
        }
        return Content.instance;
    }

    private Content(Persistency persistency)
    {
        this.persistency = persistency;
        Log.i(Constants.LOG_TAG,"Content.Constructor:: <Content> instantiated");
    }

    public boolean initialize()
    {
        boolean success;
        Log.i(Constants.LOG_TAG, "Content.initialize:: loading content...");
        Stopwatch stopwatchFetchContent = new Stopwatch(true);
        success = this.persistency.loadContent(this);
        Log.i(Constants.LOG_TAG,  String.format("Content.initialize:: loading content took [%d]ms", stopwatchFetchContent.stop()));

        if(success)
        {
            success = this.validate();
        }
        return success;
    }

    public boolean validate()
    {
        Log.e(Constants.LOG_TAG, "Content.validate:: validation not yet implemented");
        //Todo: implement

        return true;
    }

    public void clear()
    {
        if(this.backup())
        {
            this.rootLocation = null;
            this.elementsByUuid.clear();
            this.attractionCategories.clear();

            Log.i(Constants.LOG_TAG, "Content.clear:: content cleared");
        }
        else
        {
            Log.e(Constants.LOG_TAG, "Content.clear:: content not cleared!");
        }
    }

    private boolean backup()
    {
        this.backupElements = new LinkedHashMap<>(this.elementsByUuid);
        this.backupAttractionCategories = new ArrayList<>(this.attractionCategories);
        this.backupRootLocation = this.rootLocation;

        Log.i(Constants.LOG_TAG, "Content.backup:: content backup created");
        return true;
    }

    public boolean restoreBackup()
    {
        if(this.backupElements != null && this.backupAttractionCategories != null && this.backupRootLocation != null)
        {
            this.elementsByUuid = new LinkedHashMap<>(this.backupElements);
            this.attractionCategories = new ArrayList<>(backupAttractionCategories);
            this.rootLocation = this.backupRootLocation;

            this.backupElements = null;
            this.backupAttractionCategories = null;
            this.backupRootLocation = null;

            Log.i(Constants.LOG_TAG, "Content.restoreBackup:: content backup restored");
            return true;
        }
        else
        {
            Log.e(Constants.LOG_TAG, "Content.restoreBackup:: restore content backup not possible!");
            return false;
        }
    }

    public Location getRootLocation()
    {
        if(this.rootLocation == null)
        {
            this.setRootLocation();
        }

        return this.rootLocation;
    }

    private void setRootLocation()
    {
        Location rootLocation = this.getContentAsType(Location.class).get(0).getRootLocation();
        this.rootLocation = rootLocation;
        Log.i(Constants.LOG_TAG,  String.format("Content.setRootLocation:: %s set as root", rootLocation));
    }

    public boolean containsElement(IElement element)
    {
        return this.elementsByUuid.values().contains(element);
    }

    public <T extends IElement> List<T> getContentAsType(Class<T> type)
    {
        List<T> content = new ArrayList<>();
        for(IElement element : this.elementsByUuid.values())
        {
            if(type.isInstance(element))
            {
                content.add(type.cast(element));
            }
        }
        return content;
    }

    public <T extends IElement> List<IElement> getContentOfType(Class<T> type)
    {
        List<IElement> content = new ArrayList<>();
        for(IElement element : this.elementsByUuid.values())
        {
            if(type.isInstance(element))
            {
                content.add(element);
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
        return this.getContentAsType(type);
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
            elements.add(this.getContentByUuid(UUID.fromString(uuidString)));
        }

        Log.v(Constants.LOG_TAG, String.format("Content.fetchElementsByUuidStrings:: fetching [%d] elements took [%d]ms ", uuidStrings.size(), stopwatch.stop()));
        return elements;
    }

    public IElement getContentByUuid(UUID uuid)
    {
        if(this.elementsByUuid.containsKey(uuid))
        {
            return this.elementsByUuid.get(uuid);
        }
        else
        {
            AttractionCategory attractionCategory = this.getAttractionCategoryByUuid(uuid);
            if(attractionCategory != null)
            {
                return attractionCategory;
            }
            else
            {
                Log.w(Constants.LOG_TAG, String.format("Content.getContentByUuid:: No element found for uuid[%s]", uuid));
                return null;
            }
        }
    }

    public AttractionCategory getAttractionCategoryByUuid(UUID uuid)
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
        Log.v(Constants.LOG_TAG,  String.format("Content.addElement:: %s added", element));
        this.elementsByUuid.put(element.getUuid(), element);
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
        if(this.elementsByUuid.containsKey(element.getUuid()))
        {
            Log.v(Constants.LOG_TAG,  String.format("Content.removeElement:: %s removed", element));
            this.elementsByUuid.remove(element.getUuid());
            return true;
        }
        return false;
    }
}