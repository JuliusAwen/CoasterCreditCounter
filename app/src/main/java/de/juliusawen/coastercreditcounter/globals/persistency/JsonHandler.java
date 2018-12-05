package de.juliusawen.coastercreditcounter.globals.persistency;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.attractions.AttractionBlueprint;
import de.juliusawen.coastercreditcounter.data.attractions.CoasterBlueprint;
import de.juliusawen.coastercreditcounter.data.attractions.CustomAttraction;
import de.juliusawen.coastercreditcounter.data.attractions.CustomCoaster;
import de.juliusawen.coastercreditcounter.data.attractions.IBlueprint;
import de.juliusawen.coastercreditcounter.data.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.data.attractions.StockAttraction;
import de.juliusawen.coastercreditcounter.data.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.data.elements.IElement;
import de.juliusawen.coastercreditcounter.data.elements.Location;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Content;
import de.juliusawen.coastercreditcounter.toolbox.Stopwatch;

public class JsonHandler
{
    private List<TemporaryElement> temporaryLocations = new ArrayList<>();
    private List<TemporaryElement> temporaryParks = new ArrayList<>();
    private List<TemporaryElement> temporaryCoasterBlueprints = new ArrayList<>();
    private List<TemporaryElement> temporaryStockAttractions = new ArrayList<>();
    private List<TemporaryElement> temporaryCustomAttractions = new ArrayList<>();
    private List<TemporaryElement> temporaryCustomCoasters = new ArrayList<>();
    private List<TemporaryElement> temporaryVisits = new ArrayList<>();

    public void fetchContentFromJsonString(String jsonString, Content content)
    {
        Stopwatch stopwatch = new Stopwatch(true);
        content.clear();

        this.addChildlessOrphanElements(jsonString, content);

        this.entangleElements(this.temporaryLocations, content);
        this.entangleElements(this.temporaryParks, content);
        this.entangleElements(this.temporaryCoasterBlueprints, content);
        this.entangleElements(this.temporaryStockAttractions, content);
        this.entangleElements(this.temporaryCustomAttractions, content);
        this.entangleElements(this.temporaryCustomCoasters, content);

        this.addVisitedAttractions(this.temporaryVisits, content);

        content.setRootLocation();
        Log.i(Constants.LOG_TAG, String.format("JsonHandler.fetchContentFromJsonString:: fetching content from json string took [%d]ms", stopwatch.stop()));
    }

    private void addChildlessOrphanElements(String jsonString, Content content)
    {
        try
        {
            JSONObject jsonObjectContent = new JSONObject(jsonString);
            if(!jsonObjectContent.isNull(Constants.JSON_STRING_ATTRACTION_CATEGORIES))
            {
                List<TemporaryElement> temporaryAttractionCategories =
                        this.createTemporary(jsonObjectContent.getJSONArray(Constants.JSON_STRING_ATTRACTION_CATEGORIES));
                content.setAttractionCategories(this.createAttractionCategories(temporaryAttractionCategories));
            }


            if(!jsonObjectContent.isNull(Constants.JSON_STRING_LOCATIONS))
            {
                this.temporaryLocations =
                        this.createTemporary(jsonObjectContent.getJSONArray(Constants.JSON_STRING_LOCATIONS));
                content.addElements(this.createLocations(temporaryLocations));
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_PARKS))
            {
                this.temporaryParks =
                        this.createTemporary(jsonObjectContent.getJSONArray(Constants.JSON_STRING_PARKS));
                content.addElements(this.createParks(this.temporaryParks));
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_ATTRACTIONS))
            {
                JSONObject jsonObjectAttractions = jsonObjectContent.getJSONObject(Constants.JSON_STRING_ATTRACTIONS);

                if(!jsonObjectAttractions.isNull(Constants.JSON_STRING_ATTRACTION_BLUEPRINTS))
                {
                    List<TemporaryElement> temporaryAttractionBlueprints =
                            this.createTemporary(jsonObjectAttractions.getJSONArray(Constants.JSON_STRING_ATTRACTION_BLUEPRINTS));
                    content.addElements(this.createAttractionBlueprints(temporaryAttractionBlueprints, content));
                }

                if(!jsonObjectAttractions.isNull(Constants.JSON_STRING_COASTER_BLUEPRINTS))
                {
                    this.temporaryCoasterBlueprints =
                            this.createTemporary(jsonObjectAttractions.getJSONArray(Constants.JSON_STRING_COASTER_BLUEPRINTS));
                    content.addElements(this.createCoasterBlueprints(this.temporaryCoasterBlueprints, content));
                }

                if(!jsonObjectAttractions.isNull(Constants.JSON_STRING_STOCK_ATTRACTIONS))
                {
                    this.temporaryStockAttractions =
                            this.createTemporary(jsonObjectAttractions.getJSONArray(Constants.JSON_STRING_STOCK_ATTRACTIONS));
                    content.addElements(this.createStockAttractions(this.temporaryStockAttractions, content));
                }

                if(!jsonObjectAttractions.isNull(Constants.JSON_STRING_CUSTOM_ATTRACTIONS))
                {
                    this.temporaryCustomAttractions =
                            this.createTemporary(jsonObjectAttractions.getJSONArray(Constants.JSON_STRING_CUSTOM_ATTRACTIONS));
                    content.addElements(this.createCustomAttractions(this.temporaryCustomAttractions, content));
                }

                if(!jsonObjectAttractions.isNull(Constants.JSON_STRING_CUSTOM_COASTERS))
                {
                    this.temporaryCustomCoasters =
                            this.createTemporary(jsonObjectAttractions.getJSONArray(Constants.JSON_STRING_CUSTOM_COASTERS));
                    content.addElements(this.createCustomCoasters(this.temporaryCustomCoasters, content));
                }
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_VISITS))
            {
                this.temporaryVisits =
                        this.createTemporary(jsonObjectContent.getJSONArray(Constants.JSON_STRING_VISITS));
                content.addElements(this.createVisits(this.temporaryVisits));
            }
        }
        catch(JSONException e)
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.setChildlessOrphanElements:: JSONException [%s]", e.getMessage()));
            throw new IllegalStateException(e);
        }
    }

    private List<AttractionCategory> createAttractionCategories(List<TemporaryElement> temporaryElements)
    {
        List<AttractionCategory> attractionCategories = new ArrayList<>();
        for(TemporaryElement temporaryElement : temporaryElements)
        {
            AttractionCategory attractionCategory = AttractionCategory.create(temporaryElement.name, temporaryElement.uuid);
            attractionCategories.add(attractionCategory);
        }
        return attractionCategories;
    }

    private List<IElement> createLocations(List<TemporaryElement> temporaryElements)
    {
        List<IElement> elements = new ArrayList<>();
        for(TemporaryElement temporaryElement : temporaryElements)
        {
            Location element = Location.create(temporaryElement.name, temporaryElement.uuid);
            elements.add(element);
        }
        return elements;
    }

    private List<IElement> createParks(List<TemporaryElement> temporaryElements)
    {
        List<IElement> elements = new ArrayList<>();
        for(TemporaryElement temporaryElement : temporaryElements)
        {
            Park element  = Park.create(temporaryElement.name, temporaryElement.uuid);
            elements.add(element);
        }
        return elements;
    }
    
    private List<IElement> createAttractionBlueprints(List<TemporaryElement> temporaryElements, Content content)
    {
        List<IElement> elements = new ArrayList<>();
        for(TemporaryElement temporaryElement : temporaryElements)
        {
            AttractionBlueprint element = AttractionBlueprint.create(temporaryElement.name, temporaryElement.uuid);
            element.setAttractionCategory(content.getAttractionCategoryByUuid(temporaryElement.attractionCategoryUuid));
            elements.add(element);
        }
        return elements;
    }

    private List<IElement> createCoasterBlueprints(List<TemporaryElement> temporaryElements, Content content)
    {
        List<IElement> elements = new ArrayList<>();
        for(TemporaryElement temporaryElement : temporaryElements)
        {
            CoasterBlueprint element = CoasterBlueprint.create(temporaryElement.name, temporaryElement.uuid);
            element.setAttractionCategory(content.getAttractionCategoryByUuid(temporaryElement.attractionCategoryUuid));
            elements.add(element);
        }
        return elements;
    }

    private List<IElement> createStockAttractions(List<TemporaryElement> temporaryElements, Content content)
    {
        List<IElement> elements = new ArrayList<>();
        for(TemporaryElement temporaryElement : temporaryElements)
        {
            StockAttraction element =
                    StockAttraction.create(temporaryElement.name, (IBlueprint)content.getContentByUuid(temporaryElement.blueprintUuid), temporaryElement.uuid);
            elements.add(element);
        }
        return elements;
    }

    private List<IElement> createCustomAttractions(List<TemporaryElement> temporaryElements, Content content)
    {
        List<IElement> elements = new ArrayList<>();
        for(TemporaryElement temporaryElement : temporaryElements)
        {
            CustomAttraction element = CustomAttraction.create(temporaryElement.name, temporaryElement.uuid);
            element.setAttractionCategory(content.getAttractionCategoryByUuid(temporaryElement.attractionCategoryUuid));
            elements.add(element);
        }
        return elements;
    }

    private List<IElement> createCustomCoasters(List<TemporaryElement> temporaryElements, Content content)
    {
        List<IElement> elements = new ArrayList<>();
        for(TemporaryElement temporaryElement : temporaryElements)
        {
            CustomCoaster element = CustomCoaster.create(temporaryElement.name, temporaryElement.uuid);
            element.setAttractionCategory(content.getAttractionCategoryByUuid(temporaryElement.attractionCategoryUuid));
            elements.add(element);
        }
        return elements;
    }

    private List<IElement> createVisits(List<TemporaryElement> temporaryElements)
    {
        List<IElement> elements = new ArrayList<>();
        for(TemporaryElement temporaryElement : temporaryElements)
        {
            Visit element = Visit.create(temporaryElement.year, temporaryElement.month, temporaryElement.day, temporaryElement.uuid);
            elements.add(element);
        }
        return elements;
    }

    private List<TemporaryElement> createTemporary(JSONArray jsonArray)
    {
        List<TemporaryElement> temporaryElements = new ArrayList<>();
        try
        {
            for(int i = 0; i < jsonArray.length(); i++)
            {
                TemporaryElement temporaryElement = new TemporaryElement();

                JSONObject jsonObjectItem = jsonArray.getJSONObject(i);

                JSONObject jsonObjectElement = jsonObjectItem.getJSONObject(Constants.JSON_STRING_ELEMENT);

                temporaryElement.name = jsonObjectElement.getString(Constants.JSON_STRING_NAME);
                temporaryElement.uuid = UUID.fromString(jsonObjectElement.getString(Constants.JSON_STRING_UUID));

                if(!jsonObjectElement.isNull(Constants.JSON_STRING_PARENT))
                {
                    temporaryElement.parentUuid = UUID.fromString(jsonObjectElement.getString(Constants.JSON_STRING_PARENT));
                }

                if(!jsonObjectElement.isNull(Constants.JSON_STRING_CHILDREN))
                {
                    JSONArray jsonArrayChildren = jsonObjectElement.getJSONArray(Constants.JSON_STRING_CHILDREN);
                    for(int j = 0; j < jsonArrayChildren.length(); j++)
                    {
                        temporaryElement.childrenUuids.add(UUID.fromString(jsonArrayChildren.getString(j)));
                    }
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_DAY) && !jsonObjectItem.isNull(Constants.JSON_STRING_MONTH) && !jsonObjectItem.isNull(Constants.JSON_STRING_YEAR))
                {
                    temporaryElement.day = jsonObjectItem.getInt(Constants.JSON_STRING_DAY);
                    temporaryElement.month = jsonObjectItem.getInt(Constants.JSON_STRING_MONTH);
                    temporaryElement.year = jsonObjectItem.getInt(Constants.JSON_STRING_YEAR);
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_RIDE_COUNT_BY_ATTRACTIONS))
                {
                    JSONArray jsonArrayRideCountByAttractions = jsonObjectItem.getJSONArray(Constants.JSON_STRING_RIDE_COUNT_BY_ATTRACTIONS);

                    for(int j = 0; j < jsonArrayRideCountByAttractions.length(); j++)
                    {
                        JSONObject jsonObjectRideCountByAttraction = jsonArrayRideCountByAttractions.getJSONObject(j);
                        String key = jsonObjectRideCountByAttraction.names().getString(0);
                        temporaryElement.rideCountByAttractionUuids.put(UUID.fromString(key), jsonObjectRideCountByAttraction.getInt(key));
                    }
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_BLUEPRINT))
                {
                    temporaryElement.blueprintUuid = UUID.fromString(jsonObjectItem.getString(Constants.JSON_STRING_BLUEPRINT));
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_ATTRACTION_CATEGORY))
                {
                    temporaryElement.attractionCategoryUuid = UUID.fromString(jsonObjectItem.getString(Constants.JSON_STRING_ATTRACTION_CATEGORY));
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_TOTAL_RIDE_COUNT))
                {
                    temporaryElement.totalRideCount = jsonObjectItem.getInt(Constants.JSON_STRING_TOTAL_RIDE_COUNT);
                }


                temporaryElements.add(temporaryElement);
            }
        }
        catch(JSONException e)
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.createTemporary:: JSONException [%s]", e.getMessage()));
            throw new IllegalStateException(e);
        }

        return temporaryElements;
    }

    private void entangleElements(List<TemporaryElement> elements, Content content)
    {
        for(TemporaryElement temporaryElement : elements)
        {
            IElement element = content.getContentByUuid(temporaryElement.uuid);
            element.addChildrenAndSetParent(temporaryElement.childrenUuids);
        }
    }

    private void addVisitedAttractions(List<TemporaryElement> temporaryVisits, Content content)
    {
        for(TemporaryElement temporaryVisit : temporaryVisits)
        {
            Visit visit = (Visit)content.getContentByUuid(temporaryVisit.uuid);
            for(Map.Entry<UUID, Integer> rideCountByAttractionUuid : temporaryVisit.rideCountByAttractionUuids.entrySet())
            {
                VisitedAttraction visitedAttraction =
                        VisitedAttraction.create((IOnSiteAttraction) content.getContentByUuid(rideCountByAttractionUuid.getKey()), rideCountByAttractionUuid.getValue());
                visitedAttraction.increaseRideCount(rideCountByAttractionUuid.getValue());
                visit.addChildAndSetParent(visitedAttraction);
                content.addElement(visitedAttraction);
            }
        }
    }
}