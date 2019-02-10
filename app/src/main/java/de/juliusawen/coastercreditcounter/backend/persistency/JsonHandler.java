package de.juliusawen.coastercreditcounter.backend.persistency;

import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.application.Settings;
import de.juliusawen.coastercreditcounter.backend.attractions.AttractionBlueprint;
import de.juliusawen.coastercreditcounter.backend.attractions.CoasterBlueprint;
import de.juliusawen.coastercreditcounter.backend.attractions.CustomAttraction;
import de.juliusawen.coastercreditcounter.backend.attractions.CustomCoaster;
import de.juliusawen.coastercreditcounter.backend.attractions.IBlueprint;
import de.juliusawen.coastercreditcounter.backend.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.backend.attractions.StockAttraction;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Location;
import de.juliusawen.coastercreditcounter.backend.elements.Park;
import de.juliusawen.coastercreditcounter.backend.elements.Ride;
import de.juliusawen.coastercreditcounter.backend.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.backend.temporaryElements.VisitedAttraction;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Content;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.toolbox.ConvertTool;
import de.juliusawen.coastercreditcounter.toolbox.Stopwatch;

public class JsonHandler implements IDatabaseWrapper
{
    private class TemporaryElement
    {
        public String name;
        public UUID uuid;
        public final List<UUID> childrenUuids = new ArrayList<>();
        public int day;
        public int month;
        public int year;
        public int hour;
        public int minute;
        public final Map<UUID, List<UUID>> ridesByAttractions = new LinkedHashMap<>();
        public UUID blueprintUuid;
        public UUID attractionCategoryUuid;
        public UUID manufacturerUuid;
        public int untrackedRideCount;
        public boolean isDefault;
    }

    private List<TemporaryElement> temporaryLocations;
    private List<TemporaryElement> temporaryParks;
    private List<TemporaryElement> temporaryCoasterBlueprints;
    private List<TemporaryElement> temporaryStockAttractions;
    private List<TemporaryElement> temporaryCustomAttractions;
    private List<TemporaryElement> temporaryCustomCoasters;
    private List<TemporaryElement> temporaryVisits;

    public boolean importContent(Content content)
    {
        Stopwatch stopwatchImport = new Stopwatch(true);

        content.clear();

        if((!App.isInitialized && App.config.useDefaultContentFromDatabaseMockOnStartup()) || (App.isInitialized && App.config.alwaysImportFromDatabaseMock()))
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.importContent:: importing default content from DatabaseMock");
            boolean success = this.useAndExportDefaultContent(content);
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.importContent:: importing default content from DatabaseMock successful[%S]- took [%d]ms", success, stopwatchImport.stop()));
            return success;
        }
        else
        {
            Log.d(Constants.LOG_TAG, ("JsonHandler.importContent:: reading external json string..."));
            Stopwatch stopwatchRead = new Stopwatch(true);

            File file = new File(App.persistence.getExternalStorageDocumentsDirectory().getAbsolutePath(), App.config.getContentFileName());
            String jsonString = App.persistence.readStringFromExternalFile(file);
            Log.v(Constants.LOG_TAG, String.format("JsonHandler.importContent:: reading external json string took [%d]ms", stopwatchRead.stop()));

            if(this.fetchContent(jsonString, content))
            {
                Log.i(Constants.LOG_TAG, String.format("JsonHandler.importContent:: importing content successful - took [%d]ms", stopwatchImport.stop()));
                return true;
            }
            else if(App.config.createExportFileIfNotExists())
            {
                Log.e(Constants.LOG_TAG, "JsonHandler.importContent:: export file not viable: using default content");
                boolean success = this.useAndExportDefaultContent(content);
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.importContent:: export file not viable: using default content successful[%S] - took [%d]ms",
                        success, stopwatchImport.stop()));
                return success;
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.importContent:: importing content failed - took [%d]ms", stopwatchImport.stop()));
                return false;
            }
        }
    }

    private boolean useAndExportDefaultContent(Content content)
    {
        Log.e(Constants.LOG_TAG, "JsonHandler.useAndExportDefaultContent:: creating default content and exporting to external json...");
        content.useDefaults();
        return this.exportContent(content);
    }

    @Override
    public boolean loadContent(Content content)
    {
        if(App.config.useExternalStorage())
        {
            Log.e(Constants.LOG_TAG, ("JsonHandler.loadContent:: running DEBUG build - using external json..."));
            return this.importContent(content);
        }

        Stopwatch stopwatchLoad = new Stopwatch(true);

        Log.d(Constants.LOG_TAG, ("JsonHandler.loadContent:: reading internal json string..."));
        Stopwatch stopwatchRead = new Stopwatch(true);
        String jsonString = App.persistence.readStringFromInternalFile(App.config.getContentFileName());
        Log.v(Constants.LOG_TAG, String.format("JsonHandler.loadContent:: reading internal json string took [%d]ms", stopwatchRead.stop()));

        if(this.fetchContent(jsonString, content))
        {
            Log.i(Constants.LOG_TAG, String.format("JsonHandler.loadContent:: loading content successful - took [%d]ms", stopwatchLoad.stop()));
            return true;
        }
        else
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.loadContent:: creating default content and saving to internal json...");
            content.useDefaults();
            boolean success = this.saveContent(content);

            Log.e(Constants.LOG_TAG, String.format("JsonHandler.loadContent:: loading content failed: using default content - took [%d]ms", stopwatchLoad.stop()));
            return success;
        }
    }

    private boolean fetchContent(String jsonString, Content content)
    {
        Log.d(Constants.LOG_TAG, ("JsonHandler.fetchContent:: fetching content from json string..."));
        Stopwatch stopwatch = new Stopwatch(true);

        if(!jsonString.isEmpty())
        {
            if(this.addChildlessOrphanElements(jsonString, content))
            {
                this.entangleElements(this.temporaryLocations, content);
                this.entangleElements(this.temporaryParks, content);
                this.entangleElements(this.temporaryCoasterBlueprints, content);
                this.entangleElements(this.temporaryStockAttractions, content);
                this.entangleElements(this.temporaryCustomAttractions, content);
                this.entangleElements(this.temporaryCustomCoasters, content);

                this.addVisitedAttractions(this.temporaryVisits, content);

                Log.i(Constants.LOG_TAG, String.format("JsonHandler.fetchContent:: fetching content from json string successful - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG, "JsonHandler.fetchContent:: json string is corrupt - restoring content backup");
                App.content.restoreBackup();

                Log.e(Constants.LOG_TAG, String.format("JsonHandler.fetchContent:: fetching content from json string failed - took [%d]ms", stopwatch.stop()));
                return false;
            }
        }

        Log.e(Constants.LOG_TAG, String.format("JsonHandler.fetchContent:: fetching content from json string failed: json string is empty - took [%d]ms", stopwatch.stop()));
        return false;
    }

    private boolean addChildlessOrphanElements(String jsonString, Content content)
    {
        try
        {
            JSONObject jsonObjectContent = new JSONObject(jsonString);

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_MANUFACTURER))
            {
                List<TemporaryElement> temporaryManufacturers =
                        this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_MANUFACTURER));
                content.addElements(ConvertTool.convertElementsToType(this.createManufacturers(temporaryManufacturers), IElement.class));
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_ATTRACTION_CATEGORIES))
            {
                List<TemporaryElement> temporaryAttractionCategories =
                        this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_ATTRACTION_CATEGORIES));
                content.addElements(ConvertTool.convertElementsToType(this.createAttractionCategories(temporaryAttractionCategories), IElement.class));
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_LOCATIONS))
            {
                this.temporaryLocations =
                        this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_LOCATIONS));
                content.addElements(this.createLocations(temporaryLocations));

            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_PARKS))
            {
                this.temporaryParks =
                        this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_PARKS));
                content.addElements(this.createParks(this.temporaryParks));
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_ATTRACTIONS))
            {
                JSONObject jsonObjectAttractions = jsonObjectContent.getJSONObject(Constants.JSON_STRING_ATTRACTIONS);

                if(!jsonObjectAttractions.isNull(Constants.JSON_STRING_ATTRACTION_BLUEPRINTS))
                {
                    List<TemporaryElement> temporaryAttractionBlueprints =
                            this.createTemporaryElements(jsonObjectAttractions.getJSONArray(Constants.JSON_STRING_ATTRACTION_BLUEPRINTS));
                    content.addElements(this.createAttractionBlueprints(temporaryAttractionBlueprints, content));
                }

                if(!jsonObjectAttractions.isNull(Constants.JSON_STRING_COASTER_BLUEPRINTS))
                {
                    this.temporaryCoasterBlueprints =
                            this.createTemporaryElements(jsonObjectAttractions.getJSONArray(Constants.JSON_STRING_COASTER_BLUEPRINTS));
                    content.addElements(this.createCoasterBlueprints(this.temporaryCoasterBlueprints, content));
                }

                if(!jsonObjectAttractions.isNull(Constants.JSON_STRING_STOCK_ATTRACTIONS))
                {
                    this.temporaryStockAttractions =
                            this.createTemporaryElements(jsonObjectAttractions.getJSONArray(Constants.JSON_STRING_STOCK_ATTRACTIONS));
                    content.addElements(this.createStockAttractions(this.temporaryStockAttractions, content));
                }

                if(!jsonObjectAttractions.isNull(Constants.JSON_STRING_CUSTOM_ATTRACTIONS))
                {
                    this.temporaryCustomAttractions =
                            this.createTemporaryElements(jsonObjectAttractions.getJSONArray(Constants.JSON_STRING_CUSTOM_ATTRACTIONS));
                    content.addElements(this.createCustomAttractions(this.temporaryCustomAttractions, content));
                }

                if(!jsonObjectAttractions.isNull(Constants.JSON_STRING_CUSTOM_COASTERS))
                {
                    this.temporaryCustomCoasters =
                            this.createTemporaryElements(jsonObjectAttractions.getJSONArray(Constants.JSON_STRING_CUSTOM_COASTERS));
                    content.addElements(this.createCustomCoasters(this.temporaryCustomCoasters, content));
                }
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_VISITS))
            {
                this.temporaryVisits =
                        this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_VISITS));
                content.addElements(this.createVisits(this.temporaryVisits));
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_RIDES))
            {
                List<TemporaryElement> temporaryRides = this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_RIDES));
                content.addElements(this.createRides(temporaryRides));
            }
        }
        catch(JSONException e)
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.setChildlessOrphanElements:: JSONException [%s]", e.getMessage()));
            return false;
        }

        return true;
    }

    private List<TemporaryElement> createTemporaryElements(JSONArray jsonArray) throws JSONException
    {
        List<TemporaryElement> temporaryElements = new ArrayList<>();
        try
        {
            for(int i = 0; i < jsonArray.length(); i++)
            {
                TemporaryElement temporaryElement = new TemporaryElement();

                JSONObject jsonObjectItem = jsonArray.getJSONObject(i);

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_NAME))
                {
                    temporaryElement.name = jsonObjectItem.getString(Constants.JSON_STRING_NAME);
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_UUID))
                {
                    temporaryElement.uuid = UUID.fromString(jsonObjectItem.getString(Constants.JSON_STRING_UUID));
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_CHILDREN))
                {
                    JSONArray jsonArrayChildren = jsonObjectItem.getJSONArray(Constants.JSON_STRING_CHILDREN);
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

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_HOUR) && !jsonObjectItem.isNull(Constants.JSON_STRING_MINUTE))
                {
                    temporaryElement.hour = jsonObjectItem.getInt(Constants.JSON_STRING_HOUR);
                    temporaryElement.minute = jsonObjectItem.getInt(Constants.JSON_STRING_MINUTE);
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_RIDES_BY_ATTRACTIONS))
                {
                    JSONArray jsonArrayRidesByAttractions = jsonObjectItem.getJSONArray(Constants.JSON_STRING_RIDES_BY_ATTRACTIONS);

                    for(int j = 0; j < jsonArrayRidesByAttractions.length(); j++)
                    {
                        JSONObject jsonObjectRidesByAttraction = jsonArrayRidesByAttractions.getJSONObject(j);
                        String key = jsonObjectRidesByAttraction.names().getString(0);

                        UUID attractionUuid = UUID.fromString(key);
                        temporaryElement.ridesByAttractions.put(attractionUuid, new LinkedList<UUID>());

                        if(!jsonObjectRidesByAttraction.isNull(key))
                        {
                            JSONArray jsonArrayRides = jsonObjectRidesByAttraction.getJSONArray(key);

                            for(int k = 0; k < jsonArrayRides.length(); k++)
                            {
                                Objects.requireNonNull(temporaryElement.ridesByAttractions.get(attractionUuid)).add(UUID.fromString(jsonArrayRides.getString(k)));
                            }
                        }
                    }
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_BLUEPRINT))
                {
                    temporaryElement.blueprintUuid = UUID.fromString(jsonObjectItem.getString(Constants.JSON_STRING_BLUEPRINT));
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_MANUFACTURER))
                {
                    temporaryElement.manufacturerUuid = UUID.fromString(jsonObjectItem.getString(Constants.JSON_STRING_MANUFACTURER));
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_ATTRACTION_CATEGORY))
                {
                    temporaryElement.attractionCategoryUuid = UUID.fromString(jsonObjectItem.getString(Constants.JSON_STRING_ATTRACTION_CATEGORY));
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_UNTRACKED_RIDE_COUNT))
                {
                    temporaryElement.untrackedRideCount = jsonObjectItem.getInt(Constants.JSON_STRING_UNTRACKED_RIDE_COUNT);
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_IS_DEFAULT))
                {
                    temporaryElement.isDefault = jsonObjectItem.getBoolean(Constants.JSON_STRING_IS_DEFAULT);
                }

                temporaryElements.add(temporaryElement);
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.createTemporaryElements:: JSONException [%s]", e.getMessage()));
            throw e;
        }

        return temporaryElements;
    }

    private List<Manufacturer> createManufacturers(List<TemporaryElement> temporaryElements)
    {
        List<Manufacturer> manufacturers = new ArrayList<>();
        for(TemporaryElement temporaryElement : temporaryElements)
        {
            Manufacturer manufacturer = Manufacturer.create(temporaryElement.name, temporaryElement.uuid);

            if(temporaryElement.isDefault)
            {
                Manufacturer.setDefault(manufacturer);
            }

            manufacturers.add(manufacturer);
        }

        if(Manufacturer.getDefault() == null)
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.createManufacturers:: no default MANUFACTURER found - using default as fallback");

            Manufacturer.createAndSetDefault();
            manufacturers.add(Manufacturer.getDefault());
        }

        return manufacturers;
    }

    private List<AttractionCategory> createAttractionCategories(List<TemporaryElement> temporaryElements)
    {
        List<AttractionCategory> attractionCategories = new ArrayList<>();
        for(TemporaryElement temporaryElement : temporaryElements)
        {
            AttractionCategory attractionCategory = AttractionCategory.create(temporaryElement.name, temporaryElement.uuid);

            if(temporaryElement.isDefault)
            {
                AttractionCategory.setDefault(attractionCategory);
            }

            attractionCategories.add(attractionCategory);
        }

        if(AttractionCategory.getDefault() == null)
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.createAttractionCategories:: no default AttractionCategory found - using default as fallback");

            AttractionCategory.createAndSetDefault();
            attractionCategories.add(AttractionCategory.getDefault());
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
            AttractionBlueprint element = AttractionBlueprint.create(temporaryElement.name, temporaryElement.untrackedRideCount, temporaryElement.uuid);
            element.setManufacturer(this.getManufacturerFromUuid(temporaryElement.manufacturerUuid, content));
            element.setAttractionCategory(this.getAttractionCategoryFromUuid(temporaryElement.attractionCategoryUuid, content));
            elements.add(element);
        }
        return elements;
    }

    private List<IElement> createCoasterBlueprints(List<TemporaryElement> temporaryElements, Content content)
    {
        List<IElement> elements = new ArrayList<>();
        for(TemporaryElement temporaryElement : temporaryElements)
        {
            CoasterBlueprint element = CoasterBlueprint.create(temporaryElement.name, temporaryElement.untrackedRideCount, temporaryElement.uuid);
            element.setManufacturer(this.getManufacturerFromUuid(temporaryElement.manufacturerUuid, content));
            element.setAttractionCategory(this.getAttractionCategoryFromUuid(temporaryElement.attractionCategoryUuid, content));
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
                    StockAttraction.create(
                            temporaryElement.name,
                            (IBlueprint)content.getContentByUuid(temporaryElement.blueprintUuid),
                            temporaryElement.untrackedRideCount,
                            temporaryElement.uuid);
            elements.add(element);
        }
        return elements;
    }

    private List<IElement> createCustomAttractions(List<TemporaryElement> temporaryElements, Content content)
    {
        List<IElement> elements = new ArrayList<>();
        for(TemporaryElement temporaryElement : temporaryElements)
        {
            CustomAttraction element = CustomAttraction.create(temporaryElement.name, temporaryElement.untrackedRideCount, temporaryElement.uuid);
            element.setManufacturer(this.getManufacturerFromUuid(temporaryElement.manufacturerUuid, content));
            element.setAttractionCategory(this.getAttractionCategoryFromUuid(temporaryElement.attractionCategoryUuid, content));
            elements.add(element);
        }
        return elements;
    }

    private List<IElement> createCustomCoasters(List<TemporaryElement> temporaryElements, Content content)
    {
        List<IElement> elements = new ArrayList<>();
        for(TemporaryElement temporaryElement : temporaryElements)
        {
            CustomCoaster element = CustomCoaster.create(temporaryElement.name, temporaryElement.untrackedRideCount, temporaryElement.uuid);
            element.setManufacturer(this.getManufacturerFromUuid(temporaryElement.manufacturerUuid, content));
            element.setAttractionCategory(this.getAttractionCategoryFromUuid(temporaryElement.attractionCategoryUuid, content));
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

    private List<IElement> createRides(List<TemporaryElement> temporaryElements)
    {
        List<IElement> elements = new ArrayList<>();
        for(TemporaryElement temporaryElement : temporaryElements)
        {
            Ride element = Ride.create(temporaryElement.hour, temporaryElement.minute, temporaryElement.uuid);
            elements.add(element);
        }
        return elements;
    }

    private Manufacturer getManufacturerFromUuid(UUID uuid, Content content)
    {
        IElement element = content.getContentByUuid(uuid);
        if(element instanceof Manufacturer)
        {
            return (Manufacturer) element;
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.getManufacturerFromUuid:: fetched Element for UUID [%s] is not a MANUFACTURER", uuid));
            return null;
        }
    }

    private AttractionCategory getAttractionCategoryFromUuid(UUID uuid, Content content)
    {
        IElement element = content.getContentByUuid(uuid);
        if(element instanceof AttractionCategory)
        {
            return (AttractionCategory) element;
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.getAttractionCategoryFromUuid:: fetched Element for UUID [%s] is not an AttractionCategory", uuid));
            return null;
        }
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
            for(Map.Entry<UUID, List<UUID>> ridesByAttractionUuid : temporaryVisit.ridesByAttractions.entrySet())
            {
                VisitedAttraction visitedAttraction = VisitedAttraction.create((IOnSiteAttraction)content.getContentByUuid(ridesByAttractionUuid.getKey()));

                List<UUID> rideUuids = ridesByAttractionUuid.getValue();
                for(UUID rideUuid : rideUuids)
                {
                    visitedAttraction.addChildAndSetParent(content.getContentByUuid(rideUuid));
                }

                if(rideUuids.size() > 0)
                {
                    visitedAttraction.getOnSiteAttraction().increaseTotalRideCount(rideUuids.size());
                }

                visit.addChildAndSetParent(visitedAttraction);
                content.addElement(visitedAttraction);
            }
        }
    }

    public boolean exportContent(Content content)
    {
        Stopwatch stopwatch = new Stopwatch(true);

        JSONObject jsonObject = this.createContentJsonObject(content);

        File file = new File(App.persistence.getExternalStorageDocumentsDirectory(), App.config.getContentFileName());
        if(jsonObject != null)
        {
            if(App.persistence.writeStringToExternalFile(file , jsonObject.toString()))
            {
                Log.i(Constants.LOG_TAG,  String.format("Content.export:: exporting content to external json successful - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG,  String.format("Content.export:: exporting content failed: could not write to external json - took [%d]ms", stopwatch.stop()));
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Content.export:: exporting content failed: json object is null - took [%d]ms", stopwatch.stop()));
        }

        return false;
    }

    @Override
    public boolean saveContent(Content content)
    {
        if(App.config.useExternalStorage())
        {
            Log.e(Constants.LOG_TAG, ("JsonHandler.saveContent:: running DEBUG build - using external json..."));
            return this.exportContent(content);
        }

        Stopwatch stopwatch = new Stopwatch(true);

        JSONObject jsonObject = this.createContentJsonObject(content);

        if(jsonObject != null)
        {
            if(App.persistence.writeStringToInternalFile(App.config.getContentFileName(), jsonObject.toString()))
            {
                Log.i(Constants.LOG_TAG,  String.format("Content.saveContent:: saving content to internal json file successful - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG,  String.format("Content.saveContent:: saving content failed: could not write to internal json file - took [%d]ms", stopwatch.stop()));
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Content.saveContent:: saving content failed: json object is null - took [%d]ms", stopwatch.stop()));
        }

        return false;
    }

    private JSONObject createContentJsonObject(Content content)
    {
        Log.d(Constants.LOG_TAG, ("JsonHandler.createContentJsonObject:: creating json object from content..."));
        Stopwatch stopwatch = new Stopwatch(true);

        try
        {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(Constants.JSON_STRING_LOCATIONS,
                    content.getContentOfType(Location.class).isEmpty() ? JSONObject.NULL : this.createJsonArray(content.getContentOfType(Location.class)));

            jsonObject.put(Constants.JSON_STRING_PARKS,
                    content.getContentOfType(Park.class).isEmpty() ? JSONObject.NULL : this.createJsonArray(content.getContentOfType(Park.class)));

            jsonObject.put(Constants.JSON_STRING_VISITS,
                    content.getContentOfType(Visit.class).isEmpty() ? JSONObject.NULL : this.createJsonArray(content.getContentOfType(Visit.class)));

            jsonObject.put(Constants.JSON_STRING_RIDES,
                    content.getContentOfType(Ride.class).isEmpty() ? JSONObject.NULL : this.createJsonArray(content.getContentOfType(Ride.class)));


            JSONObject jsonObjectAttractions = new JSONObject();
            jsonObjectAttractions.put(Constants.JSON_STRING_ATTRACTION_BLUEPRINTS,
                    content.getContentOfType(AttractionBlueprint.class).isEmpty() ? JSONObject.NULL : this.createJsonArray(content.getContentOfType(AttractionBlueprint.class)));

            jsonObjectAttractions.put(Constants.JSON_STRING_COASTER_BLUEPRINTS,
                    content.getContentOfType(CoasterBlueprint.class).isEmpty() ? JSONObject.NULL : this.createJsonArray(content.getContentOfType(CoasterBlueprint.class)));

            jsonObjectAttractions.put(Constants.JSON_STRING_CUSTOM_ATTRACTIONS,
                    content.getContentOfType(CustomAttraction.class).isEmpty() ? JSONObject.NULL : this.createJsonArray(content.getContentOfType(CustomAttraction.class)));

            jsonObjectAttractions.put(Constants.JSON_STRING_CUSTOM_COASTERS,
                    content.getContentOfType(CustomCoaster.class).isEmpty() ? JSONObject.NULL : this.createJsonArray(content.getContentOfType(CustomCoaster.class)));

            jsonObjectAttractions.put(Constants.JSON_STRING_STOCK_ATTRACTIONS,
                    content.getContentOfType(StockAttraction.class).isEmpty() ? JSONObject.NULL : this.createJsonArray(content.getContentOfType(StockAttraction.class)));
            jsonObject.put(Constants.JSON_STRING_ATTRACTIONS, jsonObjectAttractions);


            jsonObject.put(Constants.JSON_STRING_MANUFACTURER,
                    content.getContentOfType(Manufacturer.class).isEmpty() ? JSONObject.NULL : this.createJsonArray(content.getContentOfType(Manufacturer.class)));

            jsonObject.put(Constants.JSON_STRING_ATTRACTION_CATEGORIES,
                    content.getContentOfType(AttractionCategory.class).isEmpty() ? JSONObject.NULL : this.createJsonArray(content.getContentOfType(AttractionCategory.class)));

            Log.v(Constants.LOG_TAG, String.format("Content.createContentJsonObject:: creating json object from content - took [%d]ms", stopwatch.stop()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.createContentJsonObject:: creating json object from content failed: JSONException [%s]" +
                    " - took [%d]ms", e.getMessage(), stopwatch.stop()));
        }

        return null;
    }

    private JSONArray createJsonArray(List<IElement> elements) throws JSONException
    {
        JSONArray jsonArray = new JSONArray();

        if(!elements.isEmpty())
        {
            try
            {
                for(IElement element : elements)
                {
                    jsonArray.put(element.toJson());
                }
            }
            catch(JSONException e)
            {
                e.printStackTrace();
                throw e;
            }
        }
        else
        {
            jsonArray.put(JSONObject.NULL);
        }

        return jsonArray;
    }

    public boolean loadSettings(Settings settings)
    {
        Log.d(Constants.LOG_TAG, ("JsonHandler.loadSettings:: reading internal json string..."));
        Stopwatch stopwatchLoad = new Stopwatch(true);

        Stopwatch stopwatchRead = new Stopwatch(true);
        String jsonString = App.persistence.readStringFromInternalFile(App.config.getSettingsFileName());
        Log.v(Constants.LOG_TAG, String.format("JsonHandler.loadSettings:: reading internal json string took [%d]ms", stopwatchRead.stop()));

        if(this.fetchSettings(jsonString, settings))
        {
            Log.i(Constants.LOG_TAG, String.format("JsonHandler.loadSettings:: loading settings successful - took [%d]ms", stopwatchLoad.stop()));
            return true;
        }

        Log.e(Constants.LOG_TAG, ("JsonHandler.loadSettings:: creating and saving default settings"));
        settings.useDefaults();
        boolean success = this.saveSettings(settings);

        Log.e(Constants.LOG_TAG, String.format("JsonHandler.loadSettings:: loading settings failed - using default settings. Took [%d]ms", stopwatchLoad.stop()));
        return success;
    }

    private boolean fetchSettings(String jsonString, Settings settings)
    {
        Log.d(Constants.LOG_TAG, ("JsonHandler.fetchSettings:: fetching settings from json string..."));
        Stopwatch stopwatch = new Stopwatch(true);

        if(!jsonString.isEmpty())
        {
            try
            {
                JSONObject jsonObjectSettings = new JSONObject(jsonString);
                if(!jsonObjectSettings.isNull(Constants.JSON_STRING_DEFAULT_SORT_ORDER))
                {
                    settings.setDefaultSortOrderParkVisits(SortOrder.values()[jsonObjectSettings.getInt(Constants.JSON_STRING_DEFAULT_SORT_ORDER)]);
                }

                if(!jsonObjectSettings.isNull(Constants.JSON_STRING_EXPAND_LATEST_YEAR_HEADER))
                {
                    settings.setExpandLatestYearInListByDefault(jsonObjectSettings.getBoolean(Constants.JSON_STRING_EXPAND_LATEST_YEAR_HEADER));
                }

                if(!jsonObjectSettings.isNull(Constants.JSON_STRING_FIRST_DAY_OF_THE_WEEK))
                {
                    settings.setFirstDayOfTheWeek(jsonObjectSettings.getInt(Constants.JSON_STRING_FIRST_DAY_OF_THE_WEEK));
                }

                if(!jsonObjectSettings.isNull(Constants.JSON_STRING_DEFAULT_INCREMENT))
                {
                    settings.setDefaultIncrement(jsonObjectSettings.getInt(Constants.JSON_STRING_DEFAULT_INCREMENT));
                }

                Log.v(Constants.LOG_TAG, String.format("JsonHandler.fetchSettings:: fetching settings from json string successful - took [%d]ms", stopwatch.stop()));
                return true;
            }
            catch(JSONException e)
            {
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.fetchSettings:: json string is corrupt: JSONException [%s]", e.getMessage()));
                return false;
            }
        }

        Log.e(Constants.LOG_TAG, String.format("JsonHandler.fetchContent:: fetching settings from json string failed: json string is empty - took [%d]ms", stopwatch.stop()));
        return false;
    }

    public boolean saveSettings(Settings settings)
    {
        Stopwatch stopwatch = new Stopwatch(true);

        JSONObject jsonObject = settings.toJson();

        if(jsonObject != null)
        {
            if(App.persistence.writeStringToInternalFile(App.config.getSettingsFileName(), jsonObject.toString()))
            {
                Log.i(Constants.LOG_TAG,  String.format("JsonHandler.saveSettings:: saving settings successful - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG,  String.format("JsonHandler.saveSettings:: saving settings failed: could not write to json file - took [%d]ms", stopwatch.stop()));
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("JsonHandler.saveSettings:: saving settings failed: json object is null - took [%d]ms", stopwatch.stop()));
        }

        return false;
    }

    @Override
    public boolean synchronize(Set<IElement> elementsToCreate, Set<IElement> elementsToUpdate, Set<IElement> elementsToDelete)
    {
        Intent intent = new Intent(App.getContext(), PersistenceService.class);
        intent.setAction(Constants.ACTION_SAVE);
        App.getContext().startService(intent);

        return true;
    }

    @Override
    public boolean create(Set<IElement> elements)
    {
        Log.e(Constants.LOG_TAG,  "JsonHandler.create:: empty implementation to satisfy interface");
        return false;
    }

    @Override
    public boolean update(Set<IElement> elements)
    {
        Log.e(Constants.LOG_TAG,  "JsonHandler.update:: empty implementation to satisfy interface");
        return false;
    }

    @Override
    public boolean delete(Set<IElement> elements)
    {
        Log.e(Constants.LOG_TAG,  "JsonHandler.delete:: empty implementation to satisfy interface");
        return false;
    }
}