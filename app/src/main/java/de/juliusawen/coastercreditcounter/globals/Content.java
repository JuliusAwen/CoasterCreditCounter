package de.juliusawen.coastercreditcounter.globals;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.attractions.AttractionBlueprint;
import de.juliusawen.coastercreditcounter.data.attractions.CoasterBlueprint;
import de.juliusawen.coastercreditcounter.data.attractions.CustomAttraction;
import de.juliusawen.coastercreditcounter.data.attractions.CustomCoaster;
import de.juliusawen.coastercreditcounter.data.attractions.StockAttraction;
import de.juliusawen.coastercreditcounter.data.elements.IElement;
import de.juliusawen.coastercreditcounter.data.elements.Location;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.data.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.toolbox.FileTool;
import de.juliusawen.coastercreditcounter.toolbox.Stopwatch;

public class Content
{
    private Map<UUID, IElement> elements = new HashMap<>();
    private Map<UUID, IElement> orphanElements = new HashMap<>();
    private List<AttractionCategory> attractionCategories = new ArrayList<>();

    public Location rootLocation;

    private static Content instance;

    private Persistency persistency;

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
        Stopwatch stopwatchInitializeContent = new Stopwatch(true);

        Log.i(Constants.LOG_TAG, "Content.Constructor:: fetching content...");
        Stopwatch stopwatchFetchContent = new Stopwatch(true);
        this.persistency.fetchContent(this);

        Log.i(Constants.LOG_TAG, "Content.Constructor:: setting root location...");
        Stopwatch stopwatchSetRootLocation = new Stopwatch(true);
        this.setRootLocation();
        Log.i(Constants.LOG_TAG,  String.format("Content.Constructor:: fetching root location took [%d]ms", stopwatchSetRootLocation.stop()));

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

    private void setRootLocation()
    {
        Location rootLocation = this.getContentAsType(Location.class).get(0).getRootLocation();
        this.rootLocation = rootLocation;
        Log.v(Constants.LOG_TAG,  String.format("Content.setRootLocation:: %s set as root", rootLocation));
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

    public <T extends IElement> List<IElement> getContentOfType(Class<T> type)
    {
        List<IElement> content = new ArrayList<>();
        for(IElement element : this.elements.values())
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

    public boolean export(Context context)
    {
        Stopwatch stopwatch = new Stopwatch(true);

        try
        {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("locations", this.fetchJsonArray(this.getContentOfType(Location.class)));
            jsonObject.put("parks", this.fetchJsonArray(this.getContentOfType(Park.class)));
            jsonObject.put("visits", this.fetchJsonArray(this.getContentOfType(Visit.class)));
            jsonObject.put("attractions", this.fetchJsonObjectAttractions());
            jsonObject.put("attraction categories", this.fetchJsonArray(new ArrayList<IElement>(this.getAttractionCategories())));

            if(FileTool.writeStringToFile(App.settings.getExportFileName(), jsonObject.toString(), context))
            {
                Log.v(Constants.LOG_TAG,  String.format("Content.export:: export took [%d]ms", stopwatch.stop()));
                return true;
            }
        }
        catch(JSONException exception)
        {
            exception.printStackTrace();
        }

        Log.v(Constants.LOG_TAG,  String.format("Content.export:: export failed - took [%d]ms", stopwatch.stop()));
        return false;
    }

    private JSONObject fetchJsonObjectAttractions()
    {
        try
        {
            JSONObject jsonObjectAttractions = new JSONObject();

            jsonObjectAttractions.put("attraction blueprints", this.fetchJsonArray(this.getContentOfType(AttractionBlueprint.class)));
            jsonObjectAttractions.put("coaster blueprints", this.fetchJsonArray(this.getContentOfType(CoasterBlueprint.class)));
            jsonObjectAttractions.put("custom attractions", this.fetchJsonArray(this.getContentOfType(CustomAttraction.class)));
            jsonObjectAttractions.put("custom coasters", this.fetchJsonArray(this.getContentOfType(CustomCoaster.class)));
            jsonObjectAttractions.put("stock attractions", this.fetchJsonArray(this.getContentOfType(StockAttraction.class)));

            return jsonObjectAttractions;

        }
        catch(JSONException exception)
        {
            exception.printStackTrace();
            return null;
        }


    }


    private JSONArray fetchJsonArray(List<IElement> elements)
    {
        JSONArray jsonArray = new JSONArray();

        if(!elements.isEmpty())
        {
            for(IElement element : elements)
            {
                jsonArray.put(element.toJson());
            }
        }
        else
        {
            jsonArray.put(JSONObject.NULL);
        }

        return jsonArray;
    }
}