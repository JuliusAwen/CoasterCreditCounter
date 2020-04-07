package de.juliusawen.coastercreditcounter.persistence.jsonHandler;

import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.application.Content;
import de.juliusawen.coastercreditcounter.application.Preferences;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.Blueprint;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.CustomAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.StockAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.persistence.IDatabaseWrapper;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;
import de.juliusawen.coastercreditcounter.persistence.PersistenceService;
import de.juliusawen.coastercreditcounter.persistence.databaseMock.DatabaseMock;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.Stopwatch;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailType;

public class JsonHandler implements IDatabaseWrapper
{
    private LinkedList<TemporaryJsonElement> temporaryLocations = new LinkedList<>();
    private LinkedList<TemporaryJsonElement> temporaryParks = new LinkedList<>();
    private LinkedList<TemporaryJsonElement> temporaryBlueprints = new LinkedList<>();
    private LinkedList<TemporaryJsonElement> temporaryOnSiteAttractions = new LinkedList<>();
    private LinkedList<TemporaryJsonElement> temporaryVisits = new LinkedList<>();

    public boolean importContent(Content content)
    {
        Log.i(Constants.LOG_TAG, "JsonHandler.importContent:: importing content...");

        content.clear();

        Stopwatch stopwatch = new Stopwatch(true);

        boolean success;
        if(App.config.alwaysLoadFromDatabaseMock())
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.importContent:: App.config.alwaysLoadFromDatabaseMock = true --> loading default content from DatabaseMock");
            success = this.loadDefaultContentFromDatabaseMock(content);
        }
        else
        {
            if(!App.isInitialized && App.config.resetToDefaultContentOnStartup())
            {
                Log.e(Constants.LOG_TAG, "JsonHandler.importContent:: App.config.resetToDefaultContentOnStartup = true --> loading and exporting default content");
                success = this.loadDefaultContentFromDatabaseMock(content) && this.exportContent(content);
            }
            else if(this.readExternalJsonStringAndFetchContent(content))
            {
                success = true;
            }
            else
            {
                if(App.config.createExportFileWithDefaultsIfNotFound())
                {
                    Log.e(Constants.LOG_TAG, "JsonHandler.importContent:: App.config.createExportFileWithDefaultsIfNotFound = true --> loading and exporting default content");
                    success = this.loadDefaultContentFromDatabaseMock(content) && this.exportContent(content);
                }
                else
                {
                    //Todo: implement default content creation for non-debug builds ("use content provided by developer" aka "Julius' Coasters")
                    success = this.initializeForFirstUse(content);
                }
            }
        }

        if(success)
        {
            Log.i(Constants.LOG_TAG, String.format("JsonHandler.importContent:: success - took [%d]ms", stopwatch.stop()));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.importContent:: failed - took [%d]ms", stopwatch.stop()));
        }

        return success;
    }

    private boolean loadDefaultContentFromDatabaseMock(Content content)
    {
        DatabaseMock databaseMock = DatabaseMock.getInstance();
        if(databaseMock.loadContent(content))
        {
            Log.i(Constants.LOG_TAG, "JsonHandler.loadDefaultContentFromDatabaseMock:: success");
            return true;
        }
        else
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.loadDefaultContentFromDatabaseMock:: failed");
            return false;
        }
    }

    private boolean readExternalJsonStringAndFetchContent(Content content)
    {
        Log.d(Constants.LOG_TAG, ("JsonHandler.readExternalJsonStringAndFetchContent:: reading external json string..."));
        Stopwatch stopwatchFetch = new Stopwatch(true);

        File file = new File(App.persistence.getExternalStorageDocumentsDirectory().getAbsolutePath(), App.config.getContentExportFileName());
        if(file.exists())
        {
            Stopwatch stopwatchRead = new Stopwatch(true);
            String jsonString = App.persistence.readStringFromExternalFile(file);
            Log.i(Constants.LOG_TAG, String.format("JsonHandler.readExternalJsonStringAndFetchContent:: reading external json string took [%d]ms", stopwatchRead.stop()));

            if(this.fetchContentFromJsonString(jsonString, content))
            {
                Log.i(Constants.LOG_TAG, String.format("JsonHandler.readExternalJsonStringAndFetchContent:: success - took [%d]ms", stopwatchFetch.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.readExternalJsonStringAndFetchContent:: failed - took [%d]ms", stopwatchFetch.stop()));
                return false;
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.readExternalJsonStringAndFetchContent:: failed: file [%s] not found - took [%d]ms",
                    App.config.getContentExportFileName(), stopwatchFetch.stop()));
            return false;
        }
    }

    @Override
    public boolean loadContent(Content content)
    {
        Log.i(Constants.LOG_TAG, ("JsonHandler.loadContent:: loading content..."));

        if(App.config.useExternalStorage())
        {
            Log.e(Constants.LOG_TAG, ("JsonHandler.loadContent:: App.config.useExternalStorage = true --> trying to import content from external storage"));
            return this.importContent(content);
        }

        Stopwatch stopwatchLoad = new Stopwatch(true);
        Stopwatch stopwatchRead = new Stopwatch(true);
        String jsonString = App.persistence.readStringFromInternalFile(App.config.getContentExportFileName());
        Log.i(Constants.LOG_TAG, String.format("JsonHandler.loadContent:: reading internal json string took [%d]ms", stopwatchRead.stop()));

        boolean success;

        if(!jsonString.isEmpty())
        {
            success = this.fetchContentFromJsonString(jsonString, content);
        }
        else
        {
            success = this.initializeForFirstUse(content);
        }

        if(success)
        {
            Log.i(Constants.LOG_TAG, String.format("JsonHandler.loadContent:: success - took [%d]ms", stopwatchLoad.stop()));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.loadContent:: failed - took [%d]ms", stopwatchLoad.stop()));
        }

        return success;
    }

    private boolean initializeForFirstUse(Content content)
    {
        Log.i(Constants.LOG_TAG, "JsonHandler.initializeForFirstUse:: creating root location and defaults for first use...");
        content.addElement(Location.create("root"));
        content.addElement(CreditType.getDefault());
        content.addElement(Category.getDefault());
        content.addElement(Manufacturer.getDefault());
        content.addElement(Status.getDefault());
        return this.saveContent(content);
    }

    private boolean fetchContentFromJsonString(String jsonString, Content content)
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
                this.buildNodeTree(this.temporaryOnSiteAttractions, content);

                this.createVisitedAttractionsAndAddToVisits(this.temporaryVisits, content);

                Log.i(Constants.LOG_TAG, String.format("JsonHandler.fetchContent:: success  - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG, "JsonHandler.fetchContent:: failed: json string corrupt - trying to restore content backup");
                App.content.restoreBackup();

                Log.e(Constants.LOG_TAG, String.format("JsonHandler.fetchContent:: failed - took [%d]ms", stopwatch.stop()));
                return false;
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.fetchContent:: failed: json string is empty - took [%d]ms", stopwatch.stop()));
            return false;
        }
    }

    private boolean createElementsAndAddToContent(String jsonString, Content content)
    {
        Log.d(Constants.LOG_TAG, "JsonHandler.createElementsAndAddToContent:: creating elements and populating content");

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

                if(!jsonObjectAttractions.isNull(Constants.JSON_STRING_ON_SITE_ATTRACTIONS))
                {
                    this.temporaryOnSiteAttractions = this.createTemporaryElements(jsonObjectAttractions.getJSONArray(Constants.JSON_STRING_ON_SITE_ATTRACTIONS));
                    content.addElements(this.createOnSiteAttractions(this.temporaryOnSiteAttractions, content));
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
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.createElementsAndAddToContent:: failed: JSONException [%s]", e.getMessage()));
            return false;
        }

        return true;
    }

    private LinkedList<TemporaryJsonElement> createTemporaryElements(JSONArray jsonArray) throws JSONException
    {
        LinkedList<TemporaryJsonElement> temporaryJsonElements = new LinkedList<>();
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
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.createTemporaryElements:: failed: JSONException [%s]", e.getMessage()));
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
                CreditType.setDefault(creditType);
            }
            creditTypes.add(creditType);
        }

        if(!creditTypes.contains(CreditType.getDefault()))
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.createCreditTypes:: no default CreditType found - using default as fallback");
            creditTypes.add(CreditType.getDefault());
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
                Category.setDefault(category);
            }
            categories.add(category);
        }

        if(!categories.contains(Category.getDefault()))
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.createCategories:: no default Category found - using default as fallback");
            categories.add(Category.getDefault());
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
                Manufacturer.setDefault(manufacturer);
            }
            manufacturers.add(manufacturer);
        }

        if(!manufacturers.contains(Manufacturer.getDefault()))
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.createManufacturers:: no default Manufacturer found - using default as fallback");
            manufacturers.add(Manufacturer.getDefault());
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
                Status.setDefault(status);
            }
            statuses.add(status);
        }

        if(!statuses.contains(Status.getDefault()))
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.createStatuses:: no default Status found - using default as fallback");
            statuses.add(Status.getDefault());
        }

        return statuses;
    }

    private LinkedList<IElement> createLocations(LinkedList<TemporaryJsonElement> temporaryJsonElements)
    {
        LinkedList<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            Location element = Location.create(temporaryJsonElement.name, temporaryJsonElement.uuid);
            elements.add(element);
        }
        return elements;
    }

    private LinkedList<IElement> createParks(LinkedList<TemporaryJsonElement> temporaryJsonElements)
    {
        LinkedList<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            Park element  = Park.create(temporaryJsonElement.name, temporaryJsonElement.uuid);
            elements.add(element);
        }
        return elements;
    }
    
    private LinkedList<IElement> createBlueprints(LinkedList<TemporaryJsonElement> temporaryJsonElements, Content content)
    {
        LinkedList<IElement> elements = new LinkedList<>();
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

    private LinkedList<IElement> createOnSiteAttractions(LinkedList<TemporaryJsonElement> temporaryJsonElements, Content content)
    {
        LinkedList<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            if(temporaryJsonElement.blueprintUuid != null)
            {
                StockAttraction element = StockAttraction.create(
                        temporaryJsonElement.name,
                        (Blueprint)content.getContentByUuid(temporaryJsonElement.blueprintUuid),
                        temporaryJsonElement.untrackedRideCount,
                        temporaryJsonElement.uuid);
                element.setStatus(this.getStatusFromUuid(temporaryJsonElement.statusUuid, content));
                elements.add(element);
            }
            else
            {
                CustomAttraction element = CustomAttraction.create(temporaryJsonElement.name, temporaryJsonElement.untrackedRideCount, temporaryJsonElement.uuid);
                element.setCreditType(this.getCreditTypeFromUuid(temporaryJsonElement.creditTypeUuid, content));
                element.setCategory(this.getCategoryFromUuid(temporaryJsonElement.categoryUuid, content));
                element.setManufacturer(this.getManufacturerFromUuid(temporaryJsonElement.manufacturerUuid, content));
                element.setStatus(this.getStatusFromUuid(temporaryJsonElement.statusUuid, content));
                elements.add(element);
            }
        }
        return elements;
    }

    private LinkedList<IElement> createVisits(LinkedList<TemporaryJsonElement> temporaryJsonElements)
    {
        LinkedList<IElement> elements = new LinkedList<>();
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
            return (CreditType)element;
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.getCategoryFromUuid:: fetched Element for UUID [%s] is not a CreditType - using default", uuid));
            return CreditType.getDefault();
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
            return Category.getDefault();
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
            return Manufacturer.getDefault();
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
            return Status.getDefault();
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
        Log.i(Constants.LOG_TAG, "JsonHandler.exportContent:: exporting content to external json...");

        Stopwatch stopwatch = new Stopwatch(true);

        JSONObject jsonObject = this.createContentJsonObject(content);
        if(jsonObject != null)
        {
            File file = new File(App.persistence.getExternalStorageDocumentsDirectory(), App.config.getContentExportFileName());
            if(App.persistence.writeStringToExternalFile(file , jsonObject.toString()))
            {
                Log.i(Constants.LOG_TAG, String.format("JsonHandler.exportContent:: success - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.exportContent:: failed - took [%d]ms", stopwatch.stop()));
                return false;
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.exportContent:: failed: json object is null - took [%d]ms", stopwatch.stop()));
            return false;
        }
    }

    @Override
    public boolean saveContent(Content content)
    {
        Log.i(Constants.LOG_TAG, ("JsonHandler.saveContent:: saving content..."));

        if(App.config.useExternalStorage())
        {
            Log.e(Constants.LOG_TAG, ("JsonHandler.saveContent:: App.config.useExternalStorage = true --> exporting content to external storage"));
            return this.exportContent(content);
        }

        Stopwatch stopwatch = new Stopwatch(true);

        JSONObject jsonObject = this.createContentJsonObject(content);
        if(jsonObject != null)
        {
            if(App.persistence.writeStringToInternalFile(App.config.getContentExportFileName(), jsonObject.toString()))
            {
                Log.i(Constants.LOG_TAG,  String.format("Content.saveContent:: success - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG,  String.format("Content.saveContent:: failed - took [%d]ms", stopwatch.stop()));
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Content.saveContent:: failed: json object is null - took [%d]ms", stopwatch.stop()));
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

            jsonObjectAttractions.put(Constants.JSON_STRING_ON_SITE_ATTRACTIONS, content.getContentOfType(IOnSiteAttraction.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(IOnSiteAttraction.class)));

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

            Log.i(Constants.LOG_TAG, String.format("Content.createContentJsonObject:: success - took [%d]ms", stopwatch.stop()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.createContentJsonObject:: failed: JSONException [%s] - took [%d]ms",
                    e.getMessage(), stopwatch.stop()));
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

    public boolean loadPreferences(Preferences preferences)
    {
        Log.i(Constants.LOG_TAG, ("JsonHandler.loadPreferencess:: trying to read internal json string..."));

        if(!App.config.resetToDefaultPreferencesOnStartup())
        {
            Stopwatch stopwatchLoad = new Stopwatch(true);
            Stopwatch stopwatchRead = new Stopwatch(true);
            String jsonString = App.persistence.readStringFromInternalFile(App.config.getPreferencesFileName());
            Log.i(Constants.LOG_TAG, String.format("JsonHandler.loadPreferences:: reading internal json string took [%d]ms", stopwatchRead.stop()));

            if(this.fetchPreferencesFromJsonString(jsonString, preferences))
            {
                Log.i(Constants.LOG_TAG, String.format("JsonHandler.loadPreferences:: success - took [%d]ms", stopwatchLoad.stop()));
                return true;
            }
            else
            {
                Log.i(Constants.LOG_TAG, String.format("JsonHandler.loadPreferences:: failed - took [%d]ms", stopwatchLoad.stop()));
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean fetchPreferencesFromJsonString(String jsonString, Preferences preferences)
    {
        Log.i(Constants.LOG_TAG, ("JsonHandler.fetchPreferences:: fetching preferences from json string..."));

        if(!jsonString.isEmpty())
        {
            Stopwatch stopwatch = new Stopwatch(true);

            try
            {
                JSONObject jsonObjectPreferences = new JSONObject(jsonString);

                if(!jsonObjectPreferences.isNull(Constants.JSON_STRING_DETAIL_ORDER))
                {
                    JSONArray jsonArrayDetailType = jsonObjectPreferences.getJSONArray(Constants.JSON_STRING_DETAIL_ORDER);

                    ArrayList<DetailType> detailTypes = new ArrayList<>();
                    for(int i = 0; i < jsonArrayDetailType.length(); i++)
                    {
                        detailTypes.add(DetailType.getValue(jsonArrayDetailType.getInt(i)));
                    }
                    preferences.setDetailsOrder(detailTypes);
                }

                if(!jsonObjectPreferences.isNull(Constants.JSON_STRING_DEFAULT_SORT_ORDER))
                {
                    preferences.setDefaultSortOrder(SortOrder.values()[jsonObjectPreferences.getInt(Constants.JSON_STRING_DEFAULT_SORT_ORDER)]);
                }

                if(!jsonObjectPreferences.isNull(Constants.JSON_STRING_EXPAND_LATEST_YEAR_HEADER))
                {
                    preferences.setExpandLatestYearHeaderByDefault(jsonObjectPreferences.getBoolean(Constants.JSON_STRING_EXPAND_LATEST_YEAR_HEADER));
                }

                if(!jsonObjectPreferences.isNull(Constants.JSON_STRING_FIRST_DAY_OF_THE_WEEK))
                {
                    preferences.setFirstDayOfTheWeek(jsonObjectPreferences.getInt(Constants.JSON_STRING_FIRST_DAY_OF_THE_WEEK));
                }

                if(!jsonObjectPreferences.isNull(Constants.JSON_STRING_INCREMENT))
                {
                    preferences.setIncrement(jsonObjectPreferences.getInt(Constants.JSON_STRING_INCREMENT));
                }

                Log.i(Constants.LOG_TAG, String.format("JsonHandler.fetchPreferencesFromJsonString:: success - took [%d]ms", stopwatch.stop()));
                return true;
            }
            catch(JSONException e)
            {
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.fetchPreferencesFromJsonString:: failed: JSONException [%s] - took [%d]ms", e.getMessage(), stopwatch.stop()));
                return false;
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.fetchPreferencesFromJsonString:: failed: json string is empty");
            return false;
        }
    }

    public boolean savePreferences(Preferences preferences)
    {
        Stopwatch stopwatch = new Stopwatch(true);

        JSONObject jsonObject = preferences.toJson();

        if(jsonObject != null)
        {
            if(App.persistence.writeStringToInternalFile(App.config.getPreferencesFileName(), jsonObject.toString()))
            {
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.savePreferences:: success - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.savePreferences:: failed: could not write to json file - took [%d]ms", stopwatch.stop()));
                return false;
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.savePreferences:: failed: json object is null - took [%d]ms", stopwatch.stop()));
            return false;
        }
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
    public int fetchTotalVisits()
    {
        Stopwatch stopwatch = new Stopwatch(true);

        int totalVisits = 0;

        for(IElement visit : App.content.getContentOfType(Visit.class))
        {
            totalVisits += 1;
        }

        Log.i(Constants.LOG_TAG, String.format("JsonHandler.fetchTotalVisits:: [%d] visits found - took [%d]ms", totalVisits, stopwatch.stop()));

        return totalVisits;
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
                for(IOnSiteAttraction attraction : park.fetchChildrenAsType(IOnSiteAttraction.class))
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

        List<Visit> currentVisits = Visit.fetchVisitsForYearAndDay(Calendar.getInstance(), App.content.getContentAsType(Visit.class));

        Log.i(Constants.LOG_TAG, String.format("JsonHandler.fetchCurrentVisits:: fetching current visits took [%d]ms", stopwatch.stop()));

        return currentVisits;
    }

    private List<IOnSiteAttraction> getAllCreditableAttractions()
    {
        List<IOnSiteAttraction> creditableAttractions = new ArrayList<>();

        for(IOnSiteAttraction attraction : App.content.getContentAsType(IOnSiteAttraction.class))
        {
            if(!attraction.getCreditType().isDefault())
            {
                creditableAttractions.add(attraction);
            }
        }

        return creditableAttractions;
    }
}