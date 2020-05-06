package de.juliusawen.coastercreditcounter.persistence.jsonHandler;

import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.Note;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.OnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Model;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
import de.juliusawen.coastercreditcounter.dataModel.statistics.StatisticsGlobalTotals;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.persistence.IDatabaseWrapper;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;
import de.juliusawen.coastercreditcounter.persistence.PersistenceService;
import de.juliusawen.coastercreditcounter.persistence.databaseMock.DatabaseMock;
import de.juliusawen.coastercreditcounter.tools.Stopwatch;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailType;

public class JsonHandler implements IDatabaseWrapper
{
    private LinkedList<TemporaryJsonElement> temporaryLocations = new LinkedList<>();
    private LinkedList<TemporaryJsonElement> temporaryParks = new LinkedList<>();
    private LinkedList<TemporaryJsonElement> temporaryAttractions = new LinkedList<>();
    private LinkedList<TemporaryJsonElement> temporaryVisits = new LinkedList<>();

    public boolean importContent(Content content, Uri uri, String importFileName)
    {
        Log.i(Constants.LOG_TAG, String.format("JsonHandler.importContent:: importing content from uri [%s] and file name [%s]", uri, importFileName));

        content.clear();

        Stopwatch stopwatch = new Stopwatch(true);

        boolean success;
        if(App.config.alwaysImportFromDatabaseMock())
        {
            success = this.tryPopulateDefaultContentFromDatabaseMock(content);
        }
        else
        {
            Uri exportFileUri = this.fetchImportFileUri(uri, importFileName);
            if(exportFileUri == null)
            {
                Log.e(Constants.LOG_TAG, "JsonHandler.importContent:: not able to fetch ExportFileUri");
                return false;
            }
            success = this.tryPopulateContentFromExportFile(content, exportFileUri);
        }

        if(success)
        {
            Log.i(Constants.LOG_TAG, String.format("JsonHandler.importContent:: success, took [%d]ms - saving content", stopwatch.stop()));
            this.saveContent(content);
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.importContent:: failed, took [%d]ms - restoring backup.", stopwatch.stop()));
            content.restoreBackup(false);
        }

        return success;
    }

    private boolean tryPopulateDefaultContentFromDatabaseMock(Content content)
    {
        Log.e(Constants.LOG_TAG, "JsonHandler.tryPopulateDefaultContentFromDatabaseMock:: App.config.alwaysLoadFromDatabaseMock = true --> loading default content from DatabaseMock");

        DatabaseMock databaseMock = DatabaseMock.getInstance();
        if(databaseMock.loadContent(content))
        {
            Log.i(Constants.LOG_TAG, "JsonHandler.tryPopulateDefaultContentFromDatabaseMock:: success");
            return true;
        }
        else
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.tryPopulateDefaultContentFromDatabaseMock:: failed");
            return false;
        }
    }

    private boolean tryPopulateContentFromExportFile(Content content, Uri exportFileUri)
    {
        Log.d(Constants.LOG_TAG, ("JsonHandler.tryPopulateContentFromExportFile:: reading json string from export file..."));

        Stopwatch stopwatchFetch = new Stopwatch(true);
        Stopwatch stopwatchRead = new Stopwatch(true);
        String jsonString = this.readStringFromUri(exportFileUri);

        Log.d(Constants.LOG_TAG, String.format("JsonHandler.tryPopulateContentFromExportFile:: reading json string took [%d]ms", stopwatchRead.stop()));

        if(this.tryFetchContentFromJsonString(jsonString, content))
        {
            Log.d(Constants.LOG_TAG, String.format("JsonHandler.tryPopulateContentFromExportFile:: success - took [%d]ms", stopwatchFetch.stop()));
            return true;
        }
        else
        {
            Log.d(Constants.LOG_TAG, String.format("JsonHandler.tryPopulateContentFromExportFile:: failed - took [%d]ms", stopwatchFetch.stop()));
            return false;
        }
    }

    @Override
    public boolean loadContent(Content content)
    {
        Log.i(Constants.LOG_TAG, ("JsonHandler.loadContent:: loading content..."));

        boolean success;
        String jsonString;
        if(!App.isInitialized && App.config.resetToDefaultContentOnStartup())
        {
            success = this.tryPopulateDefaultContentFromDatabaseMock(content) && this.saveContent(content);
        }
        else
        {
            Stopwatch stopwatchLoad = new Stopwatch(true);

            Stopwatch stopwatchRead = new Stopwatch(true);
            jsonString = this.readStringFromInternalStorageFile(App.config.getExportFileName());
            Log.d(Constants.LOG_TAG, String.format("JsonHandler.loadContent:: reading json string took [%d]ms", stopwatchRead.stop()));

            if(!jsonString.isEmpty())
            {
                success = this.tryFetchContentFromJsonString(jsonString, content);
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
        content.addElement(Model.getDefault());
        content.addElement(Status.getDefault());
        return this.saveContent(content);
    }

    private boolean tryFetchContentFromJsonString(String jsonString, Content content)
    {
        Log.d(Constants.LOG_TAG, ("JsonHandler.fetchContentFromJsonString:: fetching content from json string..."));
        Stopwatch stopwatch = new Stopwatch(true);

        if(!jsonString.isEmpty())
        {
            if(this.tryCreateElementsAndAddToContent(jsonString, content))
            {
                this.buildNodeTree(this.temporaryLocations, content);
                this.buildNodeTree(this.temporaryParks, content);
                this.buildNodeTree(this.temporaryAttractions, content);

                this.createVisitedAttractionsAndAddToVisits(this.temporaryVisits, content);

                Log.d(Constants.LOG_TAG, String.format("JsonHandler.fetchContentFromJsonString:: success  - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.fetchContentFromJsonString:: failed: json string invalid - took [%d]ms", stopwatch.stop()));
                return false;
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.fetchContentFromJsonString:: failed: json string is empty - took [%d]ms", stopwatch.stop()));
            return false;
        }
    }

    private boolean tryCreateElementsAndAddToContent(String jsonString, Content content)
    {
        Log.d(Constants.LOG_TAG, "JsonHandler.tryCreateElementsAndAddToContent:: creating elements and populating content");

        try
        {
            JSONObject jsonObjectContent = new JSONObject(jsonString);

            if(jsonObjectContent.isNull(Constants.JSON_STRING_IDENTIFIER) || !jsonObjectContent.getString(Constants.JSON_STRING_IDENTIFIER).equals(Constants.JSON_STRING_ID))
            {
                Log.e(Constants.LOG_TAG, "JsonHandler.tryCreateElementsAndAddToContent:: JsonString could not be identified as CoasterCreditCounterExportFile");
                return false;
            }


            if(!jsonObjectContent.isNull(Constants.JSON_STRING_CREDIT_TYPES))
            {
                List<TemporaryJsonElement> temporaryCategories = this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_CREDIT_TYPES));
                content.addElements(this.createCreditTypes(temporaryCategories));
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_CATEGORIES))
            {
                List<TemporaryJsonElement> temporaryCategories = this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_CATEGORIES));
                content.addElements(this.createCategories(temporaryCategories));
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_MANUFACTURERS))
            {
                List<TemporaryJsonElement> temporaryManufacturers = this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_MANUFACTURERS));
                content.addElements(this.createManufacturers(temporaryManufacturers));
            }

            if(!jsonObjectContent.isNull((Constants.JSON_STRING_MODELS)))
            {
                List<TemporaryJsonElement> temporaryModels = this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_MODELS));
                content.addElements(this.createModels(temporaryModels, content)); // create CreditTypes, Categories and Manufacturers first!
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_STATUSES))
            {
                List<TemporaryJsonElement> temporaryStatuses = this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_STATUSES));
                content.addElements(this.createStatuses(temporaryStatuses));
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_NOTES))
            {
                LinkedList<TemporaryJsonElement> temporaryNotes = this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_NOTES));
                content.addElements(this.createNotes(temporaryNotes));
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
                this.temporaryAttractions = this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_ATTRACTIONS));
                content.addElements(this.createAttractions(this.temporaryAttractions, content));
            }

            if(!jsonObjectContent.isNull(Constants.JSON_STRING_VISITS))
            {
                this.temporaryVisits = this.createTemporaryElements(jsonObjectContent.getJSONArray(Constants.JSON_STRING_VISITS));
                content.addElements(this.createVisits(this.temporaryVisits)); //create all IProperty first!
            }
        }
        catch(JSONException e)
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.tryCreateElementsAndAddToContent:: failed: JSONException [%s]", e.getMessage()));
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

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_UUID))
                {
                    temporaryJsonElement.uuid = UUID.fromString(jsonObjectItem.getString(Constants.JSON_STRING_UUID));
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_NAME))
                {
                    temporaryJsonElement.name = jsonObjectItem.getString(Constants.JSON_STRING_NAME);
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_NOTE_TEXT))
                {
                    temporaryJsonElement.noteText = jsonObjectItem.getString(Constants.JSON_STRING_NOTE_TEXT);
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

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_CREDIT_TYPE))
                {
                    temporaryJsonElement.creditTypeUuid = UUID.fromString(jsonObjectItem.getString(Constants.JSON_STRING_CREDIT_TYPE));
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_CATEGORY))
                {
                    temporaryJsonElement.categoryUuid = UUID.fromString(jsonObjectItem.getString(Constants.JSON_STRING_CATEGORY));
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_MANUFACTURER))
                {
                    temporaryJsonElement.manufacturerUuid = UUID.fromString(jsonObjectItem.getString(Constants.JSON_STRING_MANUFACTURER));
                }

                if(!jsonObjectItem.isNull(Constants.JSON_STRING_MODEL))
                {
                    temporaryJsonElement.modelUuid = UUID.fromString(jsonObjectItem.getString(Constants.JSON_STRING_MODEL));
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

    private LinkedList<IElement> createCreditTypes(List<TemporaryJsonElement> temporaryJsonElements)
    {
        LinkedList<IElement> creditTypes = new LinkedList<>();
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
            Log.e(Constants.LOG_TAG, "JsonHandler.createCreditTypes:: no default CreditType found - creating default as fallback");
            creditTypes.add(CreditType.getDefault());
        }

        return creditTypes;
    }

    private LinkedList<IElement> createCategories(List<TemporaryJsonElement> temporaryJsonElements)
    {
        LinkedList<IElement> categories = new LinkedList<>();
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
            Log.e(Constants.LOG_TAG, "JsonHandler.createCategories:: no default Category found - creating default as fallback");
            categories.add(Category.getDefault());
        }

        return categories;
    }

    private LinkedList<IElement> createManufacturers(List<TemporaryJsonElement> temporaryJsonElements)
    {
        LinkedList<IElement> manufacturers = new LinkedList<>();
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
            Log.e(Constants.LOG_TAG, "JsonHandler.createManufacturers:: no default Manufacturer found - creating default as fallback");
            manufacturers.add(Manufacturer.getDefault());
        }

        return manufacturers;
    }

    private LinkedList<IElement> createModels(List<TemporaryJsonElement> temporaryJsonElements, Content content)
    {
        LinkedList<IElement> models = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            Model model = Model.create(temporaryJsonElement.name, temporaryJsonElement.uuid);
            model.setCreditType(this.getCreditTypeFromUuid(temporaryJsonElement.creditTypeUuid, content));
            model.setCategory(this.getCategoryFromUuid(temporaryJsonElement.categoryUuid, content));
            model.setManufacturer(this.getManufacturerFromUuid(temporaryJsonElement.manufacturerUuid, content));

            if(temporaryJsonElement.isDefault)
            {
                Model.setDefault(model);
            }
            models.add(model);
        }

        if(!models.contains(Model.getDefault()))
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.createModels:: no default Model found - creating default as fallback");
            models.add(Model.getDefault());
        }

        return models;
    }

    private LinkedList<IElement> createStatuses(List<TemporaryJsonElement> temporaryJsonElements)
    {
        LinkedList<IElement> statuses = new LinkedList<>();
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

    private LinkedList<IElement> createAttractions(LinkedList<TemporaryJsonElement> temporaryJsonElements, Content content)
    {
        LinkedList<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            OnSiteAttraction element = OnSiteAttraction.create(temporaryJsonElement.name, temporaryJsonElement.untrackedRideCount, temporaryJsonElement.uuid);

            if(temporaryJsonElement.modelUuid != null)
            {
                element.setModel(this.getModelFromUuid(temporaryJsonElement.modelUuid, content));
            }
            else
            {
                element.setCreditType(this.getCreditTypeFromUuid(temporaryJsonElement.creditTypeUuid, content));
                element.setCategory(this.getCategoryFromUuid(temporaryJsonElement.categoryUuid, content));
                element.setManufacturer(this.getManufacturerFromUuid(temporaryJsonElement.manufacturerUuid, content));
            }
            element.setStatus(this.getStatusFromUuid(temporaryJsonElement.statusUuid, content));
            elements.add(element);
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

    private LinkedList<IElement> createNotes(LinkedList<TemporaryJsonElement> temporaryJsonElements)
    {
        LinkedList<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            Note element = Note.create(temporaryJsonElement.noteText, temporaryJsonElement.uuid);
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

    private Model getModelFromUuid(UUID uuid, Content content)
    {
        IElement element = content.getContentByUuid(uuid);
        if(element instanceof Model)
        {
            return (Model) element;
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.getModelFromUuid:: fetched Element for UUID [%s] is not a Model - using default", uuid));
            return Model.getDefault();
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
                VisitedAttraction visitedAttraction = VisitedAttraction.create((OnSiteAttraction)content.getContentByUuid(rideCountsByAttractionUuid.getKey()));
                visitedAttraction.increaseTrackedRideCount(rideCountsByAttractionUuid.getValue());

                visit.addChildAndSetParent(visitedAttraction);
                content.addElement(visitedAttraction);
            }
        }
    }

    public boolean exportContent(Content content, Uri exportFileDocumentTreeUri, String exportFileName)
    {
        Log.i(Constants.LOG_TAG, String.format("JsonHandler.exportContent:: exporting content to uri [%s] under file name [%s]", exportFileDocumentTreeUri, exportFileName));

        Stopwatch stopwatch = new Stopwatch(true);

        JSONObject jsonObject = this.createContentJsonObject(content);
        if(jsonObject != null)
        {
            Uri exportFileUri = this.fetchExportFileUri(exportFileDocumentTreeUri, exportFileName);

            if(this.writeStringToExternalStorageFile(exportFileUri, jsonObject.toString()))
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
        Log.i(Constants.LOG_TAG, ("JsonHandler.saveContent:: trying to save content to internal storage..."));

        Stopwatch stopwatch = new Stopwatch(true);
        JSONObject jsonObject = this.createContentJsonObject(content);
        if(jsonObject != null)
        {
            if(this.writeStringToInternalStorageFile(App.config.getExportFileName(), jsonObject.toString()))
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
        Log.d(Constants.LOG_TAG, ("JsonHandler.createContentJsonObject:: creating json object from content..."));
        Stopwatch stopwatch = new Stopwatch(true);

        try
        {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(Constants.JSON_STRING_IDENTIFIER, Constants.JSON_STRING_ID);

            jsonObject.put(Constants.JSON_STRING_LOCATIONS, content.getContentOfType(Location.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(Location.class)));

            jsonObject.put(Constants.JSON_STRING_PARKS, content.getContentOfType(Park.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(Park.class)));

            jsonObject.put(Constants.JSON_STRING_VISITS, content.getContentOfType(Visit.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(Visit.class)));

            jsonObject.put(Constants.JSON_STRING_ATTRACTIONS, content.getContentOfType(OnSiteAttraction.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(OnSiteAttraction.class)));

            jsonObject.put(Constants.JSON_STRING_CREDIT_TYPES, content.getContentOfType(CreditType.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(CreditType.class)));

            jsonObject.put(Constants.JSON_STRING_CATEGORIES, content.getContentOfType(Category.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(Category.class)));

            jsonObject.put(Constants.JSON_STRING_MANUFACTURERS, content.getContentOfType(Manufacturer.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(Manufacturer.class)));

            jsonObject.put(Constants.JSON_STRING_MODELS, content.getContentOfType(Model.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(Model.class)));

            jsonObject.put(Constants.JSON_STRING_STATUSES, content.getContentOfType(Status.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(Status.class)));

            jsonObject.put(Constants.JSON_STRING_NOTES, content.getContentOfType(Note.class).isEmpty()
                    ? JSONObject.NULL
                    : this.createJsonArray(content.getContentAsType(Note.class)));

            Log.d(Constants.LOG_TAG, String.format("Content.createContentJsonObject:: success - took [%d]ms", stopwatch.stop()));
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
        Log.i(Constants.LOG_TAG, ("JsonHandler.loadPreferencess:: loading preferences..."));

        if(!App.config.resetToDefaultPreferencesOnStartup())
        {
            Stopwatch stopwatchLoad = new Stopwatch(true);
            Stopwatch stopwatchRead = new Stopwatch(true);
            String jsonString = this.readStringFromInternalStorageFile(App.config.getPreferencesFileName());
            Log.d(Constants.LOG_TAG, String.format("JsonHandler.loadPreferences:: reading json string took [%d]ms", stopwatchRead.stop()));

            if(this.tryFetchPreferencesFromJsonString(jsonString, preferences))
            {
                Log.i(Constants.LOG_TAG, String.format("JsonHandler.loadPreferences:: success - took [%d]ms", stopwatchLoad.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.loadPreferences:: failed - took [%d]ms", stopwatchLoad.stop()));
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean tryFetchPreferencesFromJsonString(String jsonString, Preferences preferences)
    {
        Log.d(Constants.LOG_TAG, ("JsonHandler.tryFetchPreferencesFromJsonString:: fetching preferences from json string..."));

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

                Log.d(Constants.LOG_TAG, String.format("JsonHandler.tryFetchPreferencesFromJsonString:: success - took [%d]ms", stopwatch.stop()));
                return true;
            }
            catch(JSONException e)
            {
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.tryFetchPreferencesFromJsonString:: failed: JSONException [%s] - took [%d]ms", e.getMessage(), stopwatch.stop()));
                return false;
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.tryFetchPreferencesFromJsonString:: failed: json string is empty");
            return false;
        }
    }

    public boolean savePreferences(Preferences preferences)
    {
        Log.i(Constants.LOG_TAG, ("JsonHandler.savePreferences:: saving preferences..."));

        Stopwatch stopwatch = new Stopwatch(true);

        JSONObject jsonObject = preferences.toJson();

        if(jsonObject != null)
        {
            if(this.writeStringToInternalStorageFile(App.config.getPreferencesFileName(), jsonObject.toString()))
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

    public boolean validateImportFileUri(Uri uri, String importFileName)
    {
        if(this.fetchImportFileUri(uri, importFileName) != null)
        {
            Log.w(Constants.LOG_TAG, String.format("JsonHandler.validateImportFileUri:: uri [%s] with importFileName [%s] is valid", uri, importFileName));
            return true;
        }
        else
        {
            Log.w(Constants.LOG_TAG, String.format("JsonHandler.validateImportFileUri:: uri [%s] with importFileName [%s] invalid", uri, importFileName));
            return false;
        }
    }

    private Uri fetchImportFileUri(Uri uri, String importFileName)
    {
        Uri importFileUri = null;

        if(DocumentsContract.isDocumentUri(App.getContext(), uri))
        {
            if(this.hasJsonFileExtension(uri))
            {
                Log.i(Constants.LOG_TAG, String.format("JsonHandler.fetchImportFileUri:: passed uri [%s] is most likely ImportFileUri", uri));
                importFileUri = uri;
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.fetchImportFileUri:: passed uri [%s] is neither Directory nor .json file", uri));
            }
        }
        else
        {
            if(importFileName != null)
            {
                DocumentFile importFile = DocumentFile.fromTreeUri(App.getContext(), uri).findFile(importFileName);

                if(importFile != null)
                {
                    Log.i(Constants.LOG_TAG, String.format("JsonHandler.fetchImportFileUri:: fetched ExportFileUri [%s]", importFile.getUri()));
                    importFileUri = importFile.getUri();
                }
                else
                {
                    Log.e(Constants.LOG_TAG, String.format("JsonHandler.fetchImportFileUri:: ImportFile [%s] not found in Directory [%s]", importFileName, uri));
                }
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.fetchImportFileUri:: unable to fetch uri from [%s]- no ExportFileName passed", uri));
            }
        }

        return importFileUri;
    }

    public boolean hasJsonFileExtension(Uri uri)
    {
        return uri.getPath().contains(".") && uri.getPath().substring(uri.getPath().lastIndexOf(".")).equals(".json");
    }

    public boolean exportFileExists(Uri exportFileDocumentTreeUri, String exportFileName)
    {
        return this.getExportFile(exportFileDocumentTreeUri, exportFileName).exists();
    }

    private Uri fetchExportFileUri(Uri exportFileDocumentTreeUri, String exportFileName)
    {
        DocumentFile documentTreeFile = DocumentFile.fromTreeUri(App.getContext(), exportFileDocumentTreeUri);

        DocumentFile exportFile = this.getExportFile(exportFileDocumentTreeUri, exportFileName);

        if(exportFile == null || !exportFile.exists())
        {
            exportFile = documentTreeFile.createFile("application/octet-stream", exportFileName);
        }

        return exportFile.getUri();
    }

    private DocumentFile getExportFile(Uri exportFileDocumentTreeUri, String exportFileName)
    {
        String id = DocumentsContract.getTreeDocumentId(exportFileDocumentTreeUri) + "/" + exportFileName;
        Uri exportFileUri = DocumentsContract.buildDocumentUriUsingTree(exportFileDocumentTreeUri, id);

        return DocumentFile.fromSingleUri(App.getContext(), exportFileUri);
    }

    public String readStringFromUri(Uri uri)
    {
        Log.d(Constants.LOG_TAG, String.format("JsonHandler.readStringFromUri:: reading string from [%s]...", uri));
        InputStream inputStream = null;

        try
        {
            inputStream = App.getContext().getContentResolver().openInputStream(uri);

        }
        catch (FileNotFoundException e)
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.readStringFromUri:: FileNotFoundException: file at [%s] does not exist: [%s]", uri, e.getMessage()));
            e.printStackTrace();
        }

        return this.readStringFromInputStream(inputStream);
    }

    private String readStringFromInputStream(InputStream inputStream)
    {
        String output = "";

        if(inputStream != null)
        {
            BufferedReader bufferedReader = null;

            try
            {
                StringBuilder stringBuilder = new StringBuilder();
                String receiveString;

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);

                while((receiveString = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(receiveString);
                }

                output = stringBuilder.toString();
            }
            catch (IOException e)
            {
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.readStringFromInputStream:: IOException: [%s]", e.getMessage()));
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    inputStream.close();

                    if(bufferedReader != null)
                    {
                        bufferedReader.close();
                    }
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, "JsonHandler.readStringFromInputStream:: InputStream is NULL");
        }

        return output;
    }

    public String readStringFromInternalStorageFile(String fileName)
    {
        Log.i(Constants.LOG_TAG, String.format("JsonHandler.readStringFromInternalStorageFile:: reading string from file [%s]", fileName));

        String output = "";

        File file = new File(App.getContext().getFilesDir(), fileName);

        if(file.exists())
        {
            int length = (int) file.length();
            byte[] bytes = new byte[length];

            FileInputStream fileInputStream = null;
            try
            {
                fileInputStream = new FileInputStream(file);
                //noinspection ResultOfMethodCallIgnored
                fileInputStream.read(bytes);
            }
            catch(FileNotFoundException e)
            {
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.readStringFromInternalStorageFile:: FileNotFoundException: [%s] does not exist!\n[%s]", fileName, e.getMessage()));
            }
            catch(IOException e)
            {
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.readStringFromInternalStorageFile:: IOException: reading FileInputStream failed!\n[%s] ", e.getMessage()));
            }
            finally
            {
                try
                {
                    fileInputStream.close();
                    output = new String(bytes);
                }
                catch(IOException e)
                {
                    Log.e(Constants.LOG_TAG, String.format("JsonHandler.readStringFromInternalStorageFile:: IOException: closing FileInputStream failed!\n[%s] ", e.getMessage()));
                }
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.readStringFromInternalStorageFile:: file [%s] does not exist - returning empty output", fileName));
        }

        return output;
    }

    public boolean writeStringToInternalStorageFile(String fileName, String input)
    {
        Log.i(Constants.LOG_TAG, String.format("JsonHandler.writeStringToInternalStorageFile:: writing to file [%s]", fileName));

        File file = new File(App.getContext().getFilesDir(), fileName);

        FileOutputStream fileOutputStream = null;
        try
        {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(input.getBytes());
        }
        catch(FileNotFoundException e)
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.writeStringToInternalStorageFile:: FileNotFoundException: [%s] does not exist!\n[%s]", fileName, e.getMessage()));
        }
        catch(IOException e)
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.writeStringToInternalStorageFile:: IOException: writing FileOutputStream failed!\n[%s]", e.getMessage()));
        }
        finally
        {
            try
            {
                fileOutputStream.close();
            }
            catch(IOException e)
            {
                Log.e(Constants.LOG_TAG, String.format("JsonHandler.writeStringToInternalStorageFile:: IOException: closing FileOutputStream failed!\n[%s]", e.getMessage()));
            }
        }

        return true;
    }

    public boolean writeStringToExternalStorageFile(Uri uri, String input)
    {
        try
        {
            ParcelFileDescriptor fileDescriptor = App.getContext().getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream = new FileOutputStream(fileDescriptor.getFileDescriptor());
            fileOutputStream.write(input.getBytes());

            fileOutputStream.close();
            fileDescriptor.close();
        }
        catch (Exception e)
        {
            Log.e(Constants.LOG_TAG, String.format("JsonHandler.writeStringToExternalStorageFile:: Exception [%s]", e.getMessage()));
            return false;
        }

        return true;
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
    public StatisticsGlobalTotals fetchStatisticsGlobalTotals()
    {
        StatisticsGlobalTotals statisticsGlobalTotals = new StatisticsGlobalTotals();

        statisticsGlobalTotals.credits = this.fetchTotalCreditsCount();
        statisticsGlobalTotals.rides = this.fetchTotalRideCount();
        statisticsGlobalTotals.visits = this.fetchTotalVisits();
        statisticsGlobalTotals.parksVisited = this.fetchTotalVisitedParksCount();

        return statisticsGlobalTotals;
    }

    private int fetchTotalCreditsCount()
    {
        Stopwatch stopwatch = new Stopwatch(true);

        int totalCreditsCount = 0;

        for(OnSiteAttraction attraction : this.getAllCreditableAttractions())
        {
            if((attraction).fetchTotalRideCount() > 0)
            {
                totalCreditsCount ++;
            }
        }

        Log.d(Constants.LOG_TAG, String.format("JsonHandler.fetchTotalCreditsCount:: [%d] credits found - took [%d]ms", totalCreditsCount, stopwatch.stop()));

        return totalCreditsCount;
    }


    private int fetchTotalRideCount()
    {
        Stopwatch stopwatch = new Stopwatch(true);

        int totalCreditsRideCount = 0;

        for(OnSiteAttraction attraction : this.getAllCreditableAttractions())
        {
            totalCreditsRideCount += attraction.fetchTotalRideCount();
        }

        Log.d(Constants.LOG_TAG, String.format("JsonHandler.fetchTotalCreditsRideCount:: [%d] rides on creditable attractions found - took [%d]ms", totalCreditsRideCount, stopwatch.stop()));

        return totalCreditsRideCount;
    }

    private List<OnSiteAttraction> getAllCreditableAttractions()
    {
        List<OnSiteAttraction> creditableAttractions = new ArrayList<>();

        for(OnSiteAttraction attraction : App.content.getContentAsType(OnSiteAttraction.class))
        {
            if(!attraction.getCreditType().isDefault())
            {
                creditableAttractions.add(attraction);
            }
        }

        return creditableAttractions;
    }

    private int fetchTotalVisits()
    {
        Stopwatch stopwatch = new Stopwatch(true);

        int totalVisits = App.content.getContentOfType(Visit.class).size();

        Log.d(Constants.LOG_TAG, String.format("JsonHandler.fetchTotalVisits:: [%d] visits found - took [%d]ms", totalVisits, stopwatch.stop()));

        return totalVisits;
    }

    private int fetchTotalVisitedParksCount()
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
                for(OnSiteAttraction attraction : park.getChildrenAsType(OnSiteAttraction.class))
                {
                    if(attraction.fetchTotalRideCount() > 0)
                    {
                        totalVisitedParksCount += 1;
                        break;
                    }
                }
            }
        }

        Log.d(Constants.LOG_TAG, String.format("JsonHandler.fetchTotalVisitedParksCount:: [%d] visited parks found - took [%d]ms", totalVisitedParksCount, stopwatch.stop()));

        return totalVisitedParksCount;
    }

    @Override
    public List<Visit> fetchCurrentVisits()
    {
        Stopwatch stopwatch = new Stopwatch(true);

        List<Visit> currentVisits = Visit.fetchVisitsForYearAndDay(Calendar.getInstance(), App.content.getContentAsType(Visit.class));

        Log.d(Constants.LOG_TAG, String.format("JsonHandler.fetchCurrentVisits:: fetching current visits took [%d]ms", stopwatch.stop()));

        return currentVisits;
    }
}