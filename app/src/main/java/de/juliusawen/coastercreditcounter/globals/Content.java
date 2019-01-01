package de.juliusawen.coastercreditcounter.globals;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Location;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.IOrphanElement;
import de.juliusawen.coastercreditcounter.backend.persistency.DatabaseMock;
import de.juliusawen.coastercreditcounter.backend.persistency.Persistency;
import de.juliusawen.coastercreditcounter.toolbox.Stopwatch;

public class Content
{
    private Map<UUID, IElement> elementsByUuid = new HashMap<>();
    private List<AttractionCategory> attractionCategories = new ArrayList<>();
    private Location rootLocation;

    private boolean isRestoreBackupPossible;
    private Map<UUID, IElement> backupElements = null;
    private List<AttractionCategory> backupAttractionCategories = null;
    private Location backupRootLocation = null;

    private final Persistency persistency;

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

        Log.i(Constants.LOG_TAG, "Content.initialize:: loading content...");
        Stopwatch stopwatch = new Stopwatch(true);

        if(this.persistency.loadContent(this))
        {
            if(App.DEBUG && App.config.validateContent() && this.validate())
            {
                Log.i(Constants.LOG_TAG, String.format("Content.initialize:: loading content successful - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("Content.initialize:: validation failed - took [%d]ms", stopwatch.stop()));
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("Content.initialize:: loading content failed - took [%d]ms", stopwatch.stop()));
        }

        return false;
    }

    public void useDefaults()
    {
        Log.i(Constants.LOG_TAG, "Content.useDefaults:: creating default content...");

        if(App.DEBUG)
        {
            DatabaseMock databaseMock = DatabaseMock.getInstance();
            databaseMock.loadContent(this);
        }
        else
        {
            //Todo: implement default content creation for non-debug builds
            Log.e(Constants.LOG_TAG, "Content.useDefaults:: creating default content for non-debug build not yet implemented");
            throw new IllegalStateException();
        }

    }

    public boolean validate()
    {
        //Todo: extend validation
        Stopwatch stopwatch = new Stopwatch(true);
        Log.i(Constants.LOG_TAG, "Content.validate:: validating content...");

        if(this.validateParentChildRelations(new ArrayList<>(this.elementsByUuid.values()))

                )
        {
            Log.i(Constants.LOG_TAG, String.format("Content.validate:: validation successful - took [%d]ms", stopwatch.stop()));
            return true;
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("Content.validate:: validation failed - took [%d]ms", stopwatch.stop()));
            return false;
        }
    }

    private boolean validateParentChildRelations(List<IElement> elements)
    {
        for(IElement element : elements)
        {
            if(element.getParent() == null)
            {
                if(!(element instanceof IOrphanElement) && !element.equals(this.getRootLocation()))
                {
                    Log.e(Constants.LOG_TAG, String.format("Content.validateParentChildRelations:: FAILED: %s missing parent", element));
                    return false;
                }
            }
            else
            {
                if(!element.getParent().getChildren().contains(element))
                {
                    Log.e(Constants.LOG_TAG, String.format("Content.validateParentChildRelations:: FAILED: parent %s does not have child %s", element.getParent(), element));
                    return false;
                }
            }

            for(Location child : element.getChildrenAsType(Location.class))
            {
                if(child.getParent() != element)
                {
                    Log.e(Constants.LOG_TAG, String.format("Content.validateParentChildRelations:: FAILED: child %s has unexpected parent %s - expected parent is %s",
                            child, child.getParent(), element));
                    return false;
                }
            }
        }

        Log.d(Constants.LOG_TAG, "Content.validateParentChildRelations:: SUCCESS");
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

        this.isRestoreBackupPossible = true;

        Log.i(Constants.LOG_TAG, "Content.backup:: content backup created");
        return true;
    }

    public boolean restoreBackup()
    {
        if(this.isRestoreBackupPossible)
        {
            if(this.backupElements != null && this.backupAttractionCategories != null && this.backupRootLocation != null)
            {
                this.elementsByUuid = new LinkedHashMap<>(this.backupElements);
                this.attractionCategories = new ArrayList<>(backupAttractionCategories);
                this.rootLocation = this.backupRootLocation;

                this.isRestoreBackupPossible = false;

                this.backupElements = null;
                this.backupAttractionCategories = null;
                this.backupRootLocation = null;

                Log.i(Constants.LOG_TAG, "Content.restoreBackup:: content backup restored");
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG, "Content.restoreBackup:: restore content backup not possible: backup data is null");
                return false;
            }
        }
        else
        {
            Log.d(Constants.LOG_TAG, "Content.restoreBackup:: restore content backup not possible");
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
        Log.v(Constants.LOG_TAG, String.format("Content.setAttractionCategories:: [%d] AttractionCategories set", attractionCategories.size()));
    }

    public void addAttractionCategory(AttractionCategory attractionCategory)
    {
        this.addAttractionCategory(this.attractionCategories.size(), attractionCategory);
    }

    public void addAttractionCategory(int index, AttractionCategory attractionCategory)
    {
        this.attractionCategories.add(index, attractionCategory);
        Log.v(Constants.LOG_TAG, String.format("Content.addAttractionCategory:: %s added at index [%d] of [%d]", attractionCategory, index, this.attractionCategories.size() - 1));
    }

    public void removeAttractionCategory(AttractionCategory attractionCategory)
    {
        this.attractionCategories.remove(attractionCategory);
        Log.d(Constants.LOG_TAG, String.format("Content.removeAttractionCategory:: %s removed", attractionCategory));
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

    public List<IElement> getContentByUuidStrings(List<String> uuidStrings)
    {
        List<IElement> elements = new ArrayList<>();

        Stopwatch stopwatch = new Stopwatch(true);

        for(String uuidString : uuidStrings)
        {
            elements.add(this.getContentByUuidString(UUID.fromString(uuidString)));
        }

        Log.v(Constants.LOG_TAG, String.format("Content.getContentByUuidStrings:: fetching [%d] elements took [%d]ms ", uuidStrings.size(), stopwatch.stop()));

        return elements;
    }

    public IElement getContentByUuidString(UUID uuid)
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
                Log.w(Constants.LOG_TAG, String.format("Content.getContentByUuidString:: No element found for uuid[%s]", uuid));
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

    public void removeElementAndChildren(IElement element)
    {
        for(IElement child : element.getChildren())
        {
            this.removeElementAndChildren(child);
        }
        this.removeElement(element);
    }

    public void removeElement(IElement element)
    {
        if(this.elementsByUuid.containsKey(element.getUuid()))
        {
            Log.v(Constants.LOG_TAG,  String.format("Content.removeElement:: %s removed", element));
            this.elementsByUuid.remove(element.getUuid());
        }
    }
}