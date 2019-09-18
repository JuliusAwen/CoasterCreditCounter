package de.juliusawen.coastercreditcounter.globals;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.persistence.DatabaseMock;
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

    public int getSize()
    {
        return this.elementsByUuid.size();
    }

    public boolean initialize()
    {

        Log.i(Constants.LOG_TAG, "Content.initialize:: loading content...");
        Stopwatch stopwatch = new Stopwatch(true);

        if(this.persistence.loadContent(this))
        {
            Log.i(Constants.LOG_TAG, String.format("Content.initialize:: loading content successful - took [%d]ms", stopwatch.stop()));
            return true;
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
            //Todo: implement default content creation for non-debug builds ("use content provided by developer" aka "Julius' Coasters")
            Log.e(Constants.LOG_TAG, "Content.useDefaults:: creating default content for non-debug build not yet implemented");
            throw new IllegalStateException();
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
        this.backupElements = new LinkedHashMap<>(this.elementsByUuid);

        this.isRestoreBackupPossible = true;

        if(this.elementsByUuid.size() > 0)
        {
            Log.i(Constants.LOG_TAG, "Content.backup:: content backup created");
            return true;
        }
        else
        {
            Log.i(Constants.LOG_TAG, "Content.backup:: content is empty");
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
        List<T> content = new LinkedList<>();
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
        List<IElement> content = new LinkedList<>();
        for(IElement element : this.elementsByUuid.values())
        {
            if(type.isInstance(element))
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

    public List<IElement> getContentByUuidStrings(List<String> uuidStrings)
    {
        List<IElement> elements = new LinkedList<>();

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

    public void reorderElements(List<IElement> elements)
    {
        for(IElement element : elements)
        {
            this.removeElement(element);
            this.addElement(element);
        }
    }
}