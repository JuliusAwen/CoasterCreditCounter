package de.juliusawen.coastercreditcounter.application;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.persistence.Persistence;
import de.juliusawen.coastercreditcounter.tools.Stopwatch;

public class Content
{
    private Map<UUID, IElement> elementsByUuid = new LinkedHashMap<>();
    private Location rootLocation;

    private boolean isRestoreBackupPossible;
    private Map<UUID, IElement> backupElements = null;

    private final Persistence persistence;

    private static Content instance;

    public static Content getInstance(Persistence persistence)
    {
        if(Content.instance == null)
        {
            Content.instance = new Content(persistence);
        }
        return Content.instance;
    }

    private Content(Persistence persistence)
    {
        this.persistence = persistence;
        Log.i(Constants.LOG_TAG,"Content.Constructor:: <Content> instantiated");
    }

    public boolean initialize()
    {
        Log.i(Constants.LOG_TAG, "Content.initialize:: initializing content...");
        Stopwatch stopwatch = new Stopwatch(true);

        if(this.persistence.loadContent(this))
        {
            Log.i(Constants.LOG_TAG, String.format("Content.initialize:: content initialization successful - took [%d]ms", stopwatch.stop()));
            return true;
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("Content.initialize:: content initialization failed - took [%d]ms", stopwatch.stop()));
            return false;
        }
    }

    public void clear()
    {
        if(this.backup())
        {
            this.rootLocation = null;
            this.elementsByUuid.clear();
            Log.i(Constants.LOG_TAG, "Content.clear:: content cleared");
        }
    }

    private boolean backup()
    {
        if(this.elementsByUuid.size() > 0)
        {
            this.backupElements = new LinkedHashMap<>(this.elementsByUuid);

            this.isRestoreBackupPossible = true;
            Log.i(Constants.LOG_TAG, "Content.backup:: content backup created");
            return true;
        }
        else
        {
            this.isRestoreBackupPossible = false;
            Log.i(Constants.LOG_TAG, "Content.backup:: no backup created - content is empty");
            return false;
        }
    }

    public boolean restoreBackup()
    {
        if(this.isRestoreBackupPossible)
        {
            if(this.backupElements != null)
            {
                this.elementsByUuid = new LinkedHashMap<>(this.backupElements);
                this.rootLocation = null;
                this.isRestoreBackupPossible = false;
                this.backupElements = null;

                Log.i(Constants.LOG_TAG, "Content.restoreBackup:: content backup restored");
                return true;
            }
            else
            {
                Log.w(Constants.LOG_TAG, "Content.restoreBackup:: content backup is empty");
                return true;
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
        List<Location> locations = this.getContentAsType(Location.class);
        if(!locations.isEmpty())
        {
            this.rootLocation = locations.get(0).getRootLocation();
            Log.i(Constants.LOG_TAG,  String.format("Content.setRootLocation:: %s set as root", rootLocation));
        }
        else
        {
            String message = "not able to set root location: no location located in content - closing app";
            Log.e(Constants.LOG_TAG, "Content.setRootLocation:: " + message);
            throw new IllegalStateException(message);
        }
    }

    public boolean containsElement(IElement element)
    {
        if(this.elementsByUuid.containsValue(element))
        {
            return true;
        }
        else
        {
            Log.w(Constants.LOG_TAG, String.format("Content.containsElement:: Content does not contain %s", element));
            return false;
        }
    }

    public <T extends IElement> List<T> getContentAsType(Class<T> type)
    {
        List<T> content = new ArrayList<>();
        for(IElement element : this.elementsByUuid.values())
        {
            if(type.isAssignableFrom(element.getClass()))
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
            if(type.isAssignableFrom(element.getClass()))
            {
                content.add(element);
            }
        }
        return content;
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

    public ArrayList<IElement> getContentByUuidStrings(List<String> uuidStrings)
    {
        ArrayList<IElement> elements = new ArrayList<>();

        Stopwatch stopwatch = new Stopwatch(true);

        for(String uuidString : uuidStrings)
        {
            elements.add(this.getContentByUuid(UUID.fromString(uuidString)));
        }

        Log.v(Constants.LOG_TAG, String.format("Content.getContentByUuidStrings:: fetching [%d] elements took [%d]ms ", uuidStrings.size(), stopwatch.stop()));

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
            Log.w(Constants.LOG_TAG, String.format("Content.getContentByUuid:: No element found for uuid[%s]", uuid));
            return null;
        }
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
        Log.v(Constants.LOG_TAG,  String.format("Content.addElement:: %s added", element.getFullName()));
        this.elementsByUuid.put(element.getUuid(), element);
    }

    public void removeElement(IElement element)
    {
        if(this.elementsByUuid.containsKey(element.getUuid()))
        {
            Log.v(Constants.LOG_TAG,  String.format("Content.removeElement:: %s removed", element));
            this.elementsByUuid.remove(element.getUuid());
        }
    }

    public void reorderElements(ArrayList<IElement> elements)
    {
        for(IElement element : elements)
        {
            this.removeElement(element);
            this.addElement(element);
        }
    }
}