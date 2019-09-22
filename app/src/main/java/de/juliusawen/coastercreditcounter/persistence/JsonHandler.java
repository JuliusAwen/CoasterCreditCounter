package de.juliusawen.coastercreditcounter.persistence;

import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Settings;
import de.juliusawen.coastercreditcounter.dataModel.elements.Blueprint;
import de.juliusawen.coastercreditcounter.dataModel.elements.CustomAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.StockAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.VisitedAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attributes.IPersistable;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Content;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.Stopwatch;

public class JsonHandler implements IDatabaseWrapper
{
    private class TemporaryJsonElement
    {
        public String name;
        public UUID uuid;
        public final List<UUID> childrenUuids = new LinkedList<>();
        public int day;
        public int month;
        public int year;
        public final Map<UUID, Integer> rideCountsByAttraction = new LinkedHashMap<>();
        public UUID blueprintUuid;
        public int untrackedRideCount;
        public UUID creditTypeUuid;
        public UUID categoryUuid;
        public UUID manufacturerUuid;
        public UUID statusUuid;
        public boolean isDefault;
    }

    private List<TemporaryJsonElement> temporaryLocations;
    private List<TemporaryJsonElement> temporaryParks;
    private List<TemporaryJsonElement> temporaryBlueprints;
    private List<TemporaryJsonElement> temporaryStockAttractions;
    private List<TemporaryJsonElement> temporaryCustomAttractions;
    private List<TemporaryJsonElement> temporaryVisits;

    public boolean importContent(Content content)
    {
        content.clear();

        if((!App.isInitialized && App.config.useDefaultContentFromDatabaseMockOnStartup()) || (App.isInitialized && App.config.alwaysImportFromDatabaseMock()))
        {
            Log.w(Constants.LOG_TAG, "JsonHandler.importContent:: importing default content from DatabaseMock");
            Stopwatch stopwatch = new Stopwatch(true);
            boolean success = this.provideDefaultContent(content);
            Log.i(Constants.LOG_TAG, String.format("JsonHandler.importContent:: creating, exporting and importing default content from DatabaseMock successful [%S]- took [%d]ms", success, stopwatch.stop()));
            return success;
        }
        else
        {
            return this.readExternalJsonStringAndFetchContent(content);
        }
    }
    private boolean readExternalJsonStringAndFetchContent(Content content)
    {
        Log.d(Constants.LOG_TAG, ("JsonHandler.readExternalJsonStringAndFetchContent:: reading external json string..."));
        Stopwatch stopwatchImport = new Stopwatch(true);
        Stopwatch stopwatchRead = new Stopwatch(true);

        File file = new File(App.persistence.getExternalStorageDocumentsDirectory().getAbsolutePath(), App.config.getContentFileName());
        String jsonString = App.persistence.readStringFromExternalFile(file);
        Log.i(Constants.LOG_TAG, String.format("JsonHandler.readExternalJsonStringAndFetchContent:: reading external json string took [%d]ms", stopwatchRead.stop()));

        if(this.fetchContent(jsonString, content))
        {
            Log.i(Constants.LOG_TAG, String.format("JsonHandler.readExternalJsonStringAndFetchContent:: importing content from file [%s] successful - took [%d]ms", App.config.getContentFileName(), stopwatchImport.stop()));
            return true;
        }
        else if(App.config.createExportFileIfNotExists())
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.importContent:: export file not viable: using default content");
            boolean success = this.provideDefaultContent(content);
            Log.w(Constants.LOG_TAG, String.format("JsonHandler.importContent:: export file not viable: using default content successful[%S] - took [%d]ms", success, stopwatchImport.stop()));
            return success;
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.importContent:: importing content from file [%s] failed - took [%d]ms", App.config.getContentFileName(), stopwatchImport.stop()));
            return false;
        }
    }

    private boolean provideDefaultContent(Content content)
    {
        Log.i(Constants.LOG_TAG, "JsonHandler.provideDefaultContent:: creating default content and exporting to external json...");
        content.useDatabaseMock();
        this.exportContent(content);
        return this.readExternalJsonStringAndFetchContent(content); //reload to properly attach defaultManufacturers/-Categories/-Statuses (lazy sloppy mock implementation)
    }

    @Override
    public boolean loadContent(Content content)
    {
        if(App.config.useExternalStorage())
        {
            Log.w(Constants.LOG_TAG, ("JsonHandler.loadContent:: running DEBUG build - using external json..."));
            return this.importContent(content);
        }

        Stopwatch stopwatchLoad = new Stopwatch(true);

        Log.d(Constants.LOG_TAG, ("JsonHandler.loadContent:: reading internal json string..."));
        Stopwatch stopwatchRead = new Stopwatch(true);
        String jsonString = App.persistence.readStringFromInternalFile(App.config.getContentFileName());
        Log.i(Constants.LOG_TAG, String.format("JsonHandler.loadContent:: reading internal json string took [%d]ms", stopwatchRead.stop()));

        if(this.fetchContent(jsonString, content))
        {
            Log.i(Constants.LOG_TAG, String.format("JsonHandler.loadContent:: loading content successful - took [%d]ms", stopwatchLoad.stop()));
            return true;
        }
        else
        {
            Log.i(Constants.LOG_TAG, "JsonHandler.loadContent:: creating default content and saving to internal json...");
            content.useDatabaseMock();
            boolean success = this.saveContent(content);

            Log.e(Constants.LOG_TAG, String.format("JsonHandler.loadContent:: loading content failed: using default content - took [%d]ms", stopwatchLoad.stop()));
            return success;
        }
    }


    private boolean fetchContent(String jsonString, Content content)
    {
        Log.i(Constants.LOG_TAG, ("JsonHandler.fetchContent:: fetching content from json string..."));
        Stopwatch stopwatch = new Stopwatch(true);

        if(!jsonString.isEmpty())
        {
            if(this.createElementsAndAddToContent(jsonString, content))
            {
                this.buildNodeTree(this.temporaryLocations, content);
                this.buildNodeTree(this.temporaryParks, content);
                this.buildNodeTree(this.temporaryBlueprints, content);
                this.buildNodeTree(this.temporaryStockAttractions, content);
                this.buildNodeTree(this.temporaryCustomAttractions, content);

                this.createVisitedAttractionsAndAddToVisits(this.temporaryVisits, content);

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

    private boolean createElementsAndAddToContent(String jsonString, Content content)
    {
        try
        {
            JSONObject jsonObjectContent = new JSONObject(jsonString);

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_CREDIT_TYPES))
            {
                List<TemporaryJsonElement> temporaryCategories = this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_CREDIT_TYPES));
                content.addElements(ConvertTool.convertElementsToType(this.createCreditTypes(temporaryCategories), IElement.class));
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_CATEGORIES))
            {
                List<TemporaryJsonElement> temporaryCategories = this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_CATEGORIES));
                content.addElements(ConvertTool.convertElementsToType(this.createCategories(temporaryCategories), IElement.class));
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_MANUFACTURERS))
            {
                List<TemporaryJsonElement> temporaryManufacturers = this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_MANUFACTURERS));
                content.addElements(ConvertTool.convertElementsToType(this.createManufacturers(temporaryManufacturers), IElement.class));
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_STATUSES))
            {
                List<TemporaryJsonElement> temporaryStatuses = this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_STATUSES));
                content.addElements(ConvertTool.convertElementsToType(this.createStatuses(temporaryStatuses), IElement.class));
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_LOCATIONS))
            {
                this.temporaryLocations = this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_LOCATIONS));
                content.addElements(this.createLocations(temporaryLocations));

            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_PARKS))
            {
                this.temporaryParks = this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_PARKS));
                content.addElements(this.createParks(this.temporaryParks));
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_ATTRACTIONS))
            {
                JSONObject jsonObjectAttractions = jsonObjectContent.getJSONObject(Constants.JSON_STRING_ATTRACTIONS);

                if(!jsonObjectAttractions.isNull(Constants.JSON_STRING_BLUEPRINTS))
                {
                    this.temporaryBlueprints = this.createTemporaryElements(jsonObjectAttractions.getJSONArray(Constants.JSON_STRING_BLUEPRINTS));
                    content.addElements(this.createBlueprints(this.temporaryBlueprints, content));
                }

                if(!jsonObjectAttractions.isNull(Constants.JSON_STRING_STOCK_ATTRACTIONS))
                {
                    this.temporaryStockAttractions = this.createTemporaryElements(jsonObjectAttractions.getJSONArray(Constants.JSON_STRING_STOCK_ATTRACTIONS));
                    content.addElements(this.createStockAttractions(this.temporaryStockAttractions, content));
                }

                if(!jsonObjectAttractions.isNull(Constants.JSON_STRING_CUSTOM_ATTRACTIONS))
                {
                    this.temporaryCustomAttractions = this.createTemporaryElements(jsonObjectAttractions.getJSONArray(Constants.JSON_STRING_CUSTOM_ATTRACTIONS));
                    content.addElements(this.createCustomAttractions(this.temporaryCustomAttractions, content));
                }
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_VISITS))
            {
                this.temporaryVisits = this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_VISITS));
                content.addElements(this.createVisits(this.temporaryVisits));
            }
        }
        catch(JSONException e)
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.setChildlessOrphanElements:: JSONException [%s]", e.getMessage()));
            return false;
        }

        return true;
    }

    private List<TemporaryJsonElement> createTemporaryElements(JSONArray jsonArray) throws JSONException
    {
        List<TemporaryJsonElement> temporaryJsonElements = new ArrayList<>();
        try
        {
            for(int i = 0; i < jsonArray.length(); i++)
            {
                TemporaryJsonElement temporaryJsonElement = new TemporaryJsonElement();

                JSONObject jsonObjectItem = jsonArray.getJSONObject(i);

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_NAME))
                {
                    temporaryJsonElement.name = jsonObjectItem.getString(Constants.JSON_STRING_NAME);
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_UUID))
                {
                    temporaryJsonElement.uuid = UUID.fromString(jsonObjectItem.getString(Constants.JSON_STRING_UUID));
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_CHILDREN))
                {
                    JSONArray jsonArrayChildren = jsonObjectItem.getJSONArray(Constants.JSON_STRING_CHILDREN);
                    for(int j = 0; j < jsonArrayChildren.length(); j++)
                    {
                        temporaryJsonElement.childrenUuids.add(UUID.fromString(jsonArrayChildren.getString(j)));
                    }
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_DAY) && !jsonObjectItem.isNull(Constants.JSON_STRING_MONTH) && !jsonObjectItem.isNull(Constants.JSON_STRING_YEAR))
                {
                    temporaryJsonElement.day = jsonObjectItem.getInt(Constants.JSON_STRING_DAY);
                    temporaryJsonElement.month = jsonObjectItem.getInt(Constants.JSON_STRING_MONTH);
                    temporaryJsonElement.year = jsonObjectItem.getInt(Constants.JSON_STRING_YEAR);
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_RIDE_COUNTS_BY_ATTRACTION))
                {
                    JSONArray jsonArrayRideCountsByAttractions = jsonObjectItem.getJSONArray(Constants.JSON_STRING_RIDE_COUNTS_BY_ATTRACTION);

                    for(int j = 0; j < jsonArrayRideCountsByAttractions.length(); j++)
                    {
                        JSONObject jsonObjectRideCountByAttraction = jsonArrayRideCountsByAttractions.getJSONObject(j);
                        String key = jsonObjectRideCountByAttraction.names().getString(0);
                        temporaryJsonElement.rideCountsByAttraction.put(UUID.fromString(key), jsonObjectRideCountByAttraction.getInt(key));
                    }
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_BLUEPRINT))
                {
                    temporaryJsonElement.blueprintUuid = UUID.fromString(jsonObjectItem.getString(Constants.JSON_STRING_BLUEPRINT));
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_CREDIT_TYPE))
                {
                    temporaryJsonElement.creditTypeUuid = UUID.fromString(jsonObjectItem.getString(Constants.JSON_STRING_CREDIT_TYPE));
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_MANUFACTURER))
                {
                    temporaryJsonElement.manufacturerUuid = UUID.fromString(jsonObjectItem.getString(Constants.JSON_STRING_MANUFACTURER));
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_CATEGORY))
                {
                    temporaryJsonElement.categoryUuid = UUID.fromString(jsonObjectItem.getString(Constants.JSON_STRING_CATEGORY));
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_STATUS))
                {
                    temporaryJsonElement.statusUuid = UUID.fromString(jsonObjectItem.getString(Constants.JSON_STRING_STATUS));
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_UNTRACKED_RIDE_COUNT))
                {
                    temporaryJsonElement.untrackedRideCount = jsonObjectItem.getInt(Constants.JSON_STRING_UNTRACKED_RIDE_COUNT);
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_IS_DEFAULT))
                {
                    temporaryJsonElement.isDefault = jsonObjectItem.getBoolean(Constants.JSON_STRING_IS_DEFAULT);
                }

                temporaryJsonElements.add(temporaryJsonElement);
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.createTemporaryElements:: JSONException [%s]", e.getMessage()));
            throw e;
        }

        return temporaryJsonElements;
    }


    private List<CreditType> createCreditTypes(List<TemporaryJsonElement> temporaryJsonElements)
    {
        List<CreditType> creditTypes = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            CreditType creditType = CreditType.create(temporaryJsonElement.name, temporaryJsonElement.uuid);

            if(temporaryJsonElement.isDefault)
            {
                App.settings.setDefaultCreditType(creditType);
            }
            creditTypes.add(creditType);
        }

        if(App.settings.getDefaultCreditType() == null)
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.createCreditTypes:: no default CreditType found - using default as fallback");
            creditTypes.add(App.settings.getDefaultCreditType());
        }

        return creditTypes;
    }

    private List<Category> createCategories(List<TemporaryJsonElement> temporaryJsonElements)
    {
        List<Category> categories = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            Category category = Category.create(temporaryJsonElement.name, temporaryJsonElement.uuid);

            if(temporaryJsonElement.isDefault)
            {
                App.settings.setDefaultCategory(category);
            }
            categories.add(category);
        }

        if(App.settings.getDefaultCategory() == null)
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.createCategories:: no default Category found - using default as fallback");
            categories.add(App.settings.getDefaultCategory());
        }

        return categories;
    }

    private List<Manufacturer> createManufacturers(List<TemporaryJsonElement> temporaryJsonElements)
    {
        List<Manufacturer> manufacturers = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            Manufacturer manufacturer = Manufacturer.create(temporaryJsonElement.name, temporaryJsonElement.uuid);

            if(temporaryJsonElement.isDefault)
            {
                App.settings.setDefaultManufacturer(manufacturer);
            }
            manufacturers.add(manufacturer);
        }

        if(App.settings.getDefaultManufacturer() == null)
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.createManufacturers:: no default Manufacturer found - using default as fallback");
            manufacturers.add(App.settings.getDefaultManufacturer());
        }

        return manufacturers;
    }

    private List<Status> createStatuses(List<TemporaryJsonElement> temporaryJsonElements)
    {
        List<Status> statuses = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            Status status = Status.create(temporaryJsonElement.name, temporaryJsonElement.uuid);

            if(temporaryJsonElement.isDefault)
            {
                App.settings.setDefaultStatus(status);
            }
            statuses.add(status);
        }

        if(App.settings.getDefaultStatus() == null)
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.createStatuses:: no default Status found - using default as fallback");
            statuses.add(App.settings.getDefaultStatus());
        }

        return statuses;
    }

    private List<IElement> createLocations(List<TemporaryJsonElement> temporaryJsonElements)
    {
        List<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            Location element = Location.create(temporaryJsonElement.name, temporaryJsonElement.uuid);
            elements.add(element);
        }
        return elements;
    }

    private List<IElement> createParks(List<TemporaryJsonElement> temporaryJsonElements)
    {
        List<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            Park element  = Park.create(temporaryJsonElement.name, temporaryJsonElement.uuid);
            elements.add(element);
        }
        return elements;
    }
    
    private List<IElement> createBlueprints(List<TemporaryJsonElement> temporaryJsonElements, Content content)
    {
        List<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            Blueprint element = Blueprint.create(temporaryJsonElement.name, temporaryJsonElement.uuid);
            element.setCreditType(this.getCreditTypeFromUuid(temporaryJsonElement.creditTypeUuid, content));
            element.setCategory(this.getCategoryFromUuid(temporaryJsonElement.categoryUuid, content));
            element.setManufacturer(this.getManufacturerFromUuid(temporaryJsonElement.manufacturerUuid, content));
            elements.add(element);
        }
        return elements;
    }

    private List<IElement> createStockAttractions(List<TemporaryJsonElement> temporaryJsonElements, Content content)
    {
        List<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            StockAttraction element = StockAttraction.create(
                            temporaryJsonElement.name,
                            (Blueprint)content.getContentByUuid(temporaryJsonElement.blueprintUuid),
                            temporaryJsonElement.untrackedRideCount,
                            temporaryJsonElement.uuid);
            element.setStatus(this.getStatusFromUuid(temporaryJsonElement.statusUuid, content));
            elements.add(element);
        }
        return elements;
    }

    private List<IElement> createCustomAttractions(List<TemporaryJsonElement> temporaryJsonElements, Content content)
    {
        List<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            CustomAttraction element = CustomAttraction.create(temporaryJsonElement.name, temporaryJsonElement.untrackedRideCount, temporaryJsonElement.uuid);
            element.setCreditType(this.getCreditTypeFromUuid(temporaryJsonElement.creditTypeUuid, content));
            element.setCategory(this.getCategoryFromUuid(temporaryJsonElement.categoryUuid, content));
            element.setManufacturer(this.getManufacturerFromUuid(temporaryJsonElement.manufacturerUuid, content));
            element.setStatus(this.getStatusFromUuid(temporaryJsonElement.statusUuid, content));
            elements.add(element);
        }
        return elements;
    }

    private List<IElement> createVisits(List<TemporaryJsonElement> temporaryJsonElements)
    {
        List<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            Visit element = Visit.create(temporaryJsonElement.year, temporaryJsonElement.month, temporaryJsonElement.day, temporaryJsonElement.uuid);
            elements.add(element);
        }
        return elements;
    }


    private CreditType getCreditTypeFromUuid(UUID uuid, Content content)
    {
        IElement element = content.getContentByUuid(uuid);
        if(element instanceof CreditType)
        {
            return (CreditType) element;
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.getCategoryFromUuid:: fetched Element for UUID [%s] is not a CreditType - using default", uuid));
            return App.settings.getDefaultCreditType();
        }
    }

    private Category getCategoryFromUuid(UUID uuid, Content content)
    {
        IElement element = content.getContentByUuid(uuid);
        if(element instanceof Category)
        {
            return (Category)element;
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.getCategoryFromUuid:: fetched Element for UUID [%s] is not a Category - using default", uuid));
            return App.settings.getDefaultCategory();
        }
    }

    private Manufacturer getManufacturerFromUuid(UUID uuid, Content content)
    {
        IElement element = content.getContentByUuid(uuid);
        if(element instanceof Manufacturer)
        {
            return (Manufacturer)element;
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.getManufacturerFromUuid:: fetched Element for UUID [%s] is not a Manufacturer - using default", uuid));
            return App.settings.getDefaultManufacturer();
        }
    }

    private Status getStatusFromUuid(UUID uuid, Content content)
    {
        IElement element = content.getContentByUuid(uuid);
        if(element instanceof Status)
        {
            return (Status)element;
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.getStatusFromUuid:: fetched Element for UUID [%s] is not a Status - using default", uuid));
            return App.settings.getDefaultStatus();
        }
    }


    private void buildNodeTree(List<TemporaryJsonElement> elements, Content content)
    {
        for(TemporaryJsonElement temporaryJsonElement : elements)
        {
            IElement element = content.getContentByUuid(temporaryJsonElement.uuid);
            element.addChildrenAndSetParent(temporaryJsonElement.childrenUuids);
        }
    }

    private void createVisitedAttractionsAndAddToVisits(List<TemporaryJsonElement> temporaryVisits, Content content)
    {
        for(TemporaryJsonElement temporaryVisit : temporaryVisits)
        {
            Visit visit = (Visit)content.getContentByUuid(temporaryVisit.uuid);
            for(Map.Entry<UUID, Integer> rideCountsByAttractionUuid : temporaryVisit.rideCountsByAttraction.entrySet())
            {
                VisitedAttraction visitedAttraction = VisitedAttraction.create((IOnSiteAttraction)content.getContentByUuid(rideCountsByAttractionUuid.getKey()));
                visitedAttraction.increaseTotalRideCount(rideCountsByAttractionUuid.getValue());

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
                Log.i(Constants.LOG_TAG, String.format("Content.export:: exporting content to external json [%s] successful - took [%d]ms", App.config.getContentFileName(), stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("Content.export:: exporting content failed: could not write to external json [%s] - took [%d]ms", App.config.getContentFileName(), stopwatch.stop()));
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("Content.export:: exporting content failed: json object is null - took [%d]ms", stopwatch.stop()));
        }

        return false;
    }

    @Override
    public boolean saveContent(Content content)
    {
        if(App.config.useExternalStorage())
        {
            Log.w(Constants.LOG_TAG, ("JsonHandler.saveContent:: running DEBUG build - using external json..."));
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
        Log.i(Constants.LOG_TAG, ("JsonHandler.createContentJsonObject:: creating json object from content..."));
        Stopwatch stopwatch = new Stopwatch(true);

        try
        {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(Constants.JSON_STRING_LOCATIONS, content.getContentOfType(Location.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(Location.class)));

            jsonObject.put(Constants.JSON_STRING_PARKS, content.getContentOfType(Park.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(Park.class)));

            jsonObject.put(Constants.JSON_STRING_VISITS, content.getContentOfType(Visit.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(Visit.class)));


            JSONObject jsonObjectAttractions = new JSONObject();
            jsonObjectAttractions.put(Constants.JSON_STRING_BLUEPRINTS, content.getContentOfType(Blueprint.class).isEmpty()
                            ? JSONObject.NULL
                            : this.createJsonArray(content.getContentAsType(Blueprint.class)));

            jsonObjectAttractions.put(Constants.JSON_STRING_CUSTOM_ATTRACTIONS, content.getContentOfType(CustomAttraction.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(CustomAttraction.class)));

            jsonObjectAttractions.put(Constants.JSON_STRING_STOCK_ATTRACTIONS, content.getContentOfType(StockAttraction.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(StockAttraction.class)));

            jsonObject.put(Constants.JSON_STRING_ATTRACTIONS, jsonObjectAttractions);


            jsonObject.put(Constants.JSON_STRING_CREDIT_TYPES, content.getContentOfType(CreditType.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(CreditType.class)));

            jsonObject.put(Constants.JSON_STRING_CATEGORIES, content.getContentOfType(Category.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(Category.class)));

            jsonObject.put(Constants.JSON_STRING_MANUFACTURERS, content.getContentOfType(Manufacturer.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(Manufacturer.class)));

            jsonObject.put(Constants.JSON_STRING_STATUSES, content.getContentOfType(Status.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(Status.class)));

            Log.i(Constants.LOG_TAG, String.format("Content.createContentJsonObject:: creating json object from content - took [%d]ms", stopwatch.stop()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.createContentJsonObject:: creating json object from content failed: JSONException [%s] - took [%d]ms", e.getMessage(), stopwatch.stop()));
        }

        return null;
    }

    private JSONArray createJsonArray(List<? extends IPersistable> elements) throws JSONException
    {
        JSONArray jsonArray = new JSONArray();

        if(!elements.isEmpty())
        {
            try
            {
                for(IPersistable element : elements)
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
        Log.i(Constants.LOG_TAG, ("JsonHandler.loadSettings:: reading internal json string..."));
        Stopwatch stopwatchLoad = new Stopwatch(true);

        Stopwatch stopwatchRead = new Stopwatch(true);
        String jsonString = App.persistence.readStringFromInternalFile(App.config.getSettingsFileName());
        Log.i(Constants.LOG_TAG, String.format("JsonHandler.loadSettings:: reading internal json string took [%d]ms", stopwatchRead.stop()));

        if(this.fetchSettings(jsonString, settings))
        {
            Log.i(Constants.LOG_TAG, String.format("JsonHandler.loadSettings:: loading settings successful - took [%d]ms", stopwatchLoad.stop()));
            return true;
        }

        Log.i(Constants.LOG_TAG, ("JsonHandler.loadSettings:: creating and saving default settings"));
        settings.useDefaults();
        boolean success = this.saveSettings(settings);

        Log.e(Constants.LOG_TAG, String.format("JsonHandler.loadSettings:: loading settings failed - using default settings. Took [%d]ms", stopwatchLoad.stop()));
        return success;
    }

    private boolean fetchSettings(String jsonString, Settings settings)
    {
        Log.i(Constants.LOG_TAG, ("JsonHandler.fetchSettings:: fetching settings from json string..."));
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

                Log.i(Constants.LOG_TAG, String.format("JsonHandler.fetchSettings:: fetching settings from json string successful - took [%d]ms", stopwatch.stop()));
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
                Log.i(Constants.LOG_TAG, String.format("JsonHandler.saveSettings:: saving settings successful - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.saveSettings:: saving settings failed: could not write to json file - took [%d]ms", stopwatch.stop()));
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.saveSettings:: saving settings failed: json object is null - took [%d]ms", stopwatch.stop()));
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
        Log.e(Constants.LOG_TAG, "JsonHandler.create:: empty implementation to satisfy interface");
        return false;
    }

    @Override
    public boolean update(Set<IElement> elements)
    {
        Log.e(Constants.LOG_TAG, "JsonHandler.update:: empty implementation to satisfy interface");
        return false;
    }

    @Override
    public boolean delete(Set<IElement> elements)
    {
        Log.e(Constants.LOG_TAG, "JsonHandler.delete:: empty implementation to satisfy interface");
        return false;
    }

    @Override
    public int fetchTotalCreditsCount()
    {
        Stopwatch stopwatch = new Stopwatch(true);

        int totalCreditsCount = 0;

        for(IOnSiteAttraction attraction : this.getAllCreditableAttractions())
        {
            if((attraction).getTotalRideCount() > 0)
            {
                totalCreditsCount ++;
            }
        }

        Log.i(Constants.LOG_TAG, String.format("JsonHandler.fetchTotalCreditsCount:: [%d] credits found - took [%d]ms", totalCreditsCount, stopwatch.stop()));

        return totalCreditsCount;
    }

    @Override
    public int fetchTotalCreditsRideCount()
    {
        Stopwatch stopwatch = new Stopwatch(true);

        int totalCreditsRideCount = 0;

        for(IOnSiteAttraction attraction : this.getAllCreditableAttractions())
        {
            totalCreditsRideCount += attraction.getTotalRideCount();
        }

        Log.i(Constants.LOG_TAG, String.format("JsonHandler.fetchTotalCreditsRideCount:: [%d] rides on creditable attractions found - took [%d]ms", totalCreditsRideCount, stopwatch.stop()));

        return totalCreditsRideCount;
    }

    @Override
    public int fetchTotalVisitedParksCount()
    {
        Stopwatch stopwatch = new Stopwatch(true);

        int totalVisitedParksCount = 0;

        for(Park park : App.content.getContentAsType(Park.class))
        {
            if(park.hasChildrenOfType(Visit.class))
            {
                totalVisitedParksCount += 1;
            }
            else
            {
                for(IOnSiteAttraction attraction : park.getChildrenAsType(IOnSiteAttraction.class))
                {
                    if(attraction.getTotalRideCount() > 0)
                    {
                        totalVisitedParksCount += 1;
                        break;
                    }
                }
            }
        }

        Log.i(Constants.LOG_TAG, String.format("JsonHandler.fetchTotalVisitedParksCount:: [%d] visited parks found - took [%d]ms", totalVisitedParksCount, stopwatch.stop()));

        return totalVisitedParksCount;
    }

    @Override
    public List<Visit> fetchCurrentVisits()
    {
        Stopwatch stopwatch = new Stopwatch(true);

        Calendar todaysCalendar = Calendar.getInstance();
        List<Visit> currentVisits = Visit.fetchVisitsForYearAndDay(todaysCalendar, App.content.getContentAsType(Visit.class));

        Log.i(Constants.LOG_TAG, String.format("JsonHandler.fetchCurrentVisits:: fetching current visits took [%d]ms", stopwatch.stop()));

        return currentVisits;
    }

    private List<IOnSiteAttraction> getAllCreditableAttractions()
    {
        List<IOnSiteAttraction> creditableAttractions = new ArrayList<>();

        for(IOnSiteAttraction attraction : App.content.getContentAsType(IOnSiteAttraction.class))
        {
            if(!attraction.getCreditType().equals(App.settings.getDefaultCreditType()))
            {
                creditableAttractions.add(attraction);
            }
        }

        return creditableAttractions;
    }
}