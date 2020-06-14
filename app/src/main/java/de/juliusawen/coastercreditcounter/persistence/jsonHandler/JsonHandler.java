package de.juliusawen.coastercreditcounter.persistence.jsonHandler;

import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;

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
import java.util.Locale;
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
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailType;

public class JsonHandler implements IDatabaseWrapper
{
    private LinkedList<TemporaryJsonElement> temporaryLocations = new LinkedList<>();
    private LinkedList<TemporaryJsonElement> temporaryParks = new LinkedList<>();
    private LinkedList<TemporaryJsonElement> temporaryAttractions = new LinkedList<>();
    private LinkedList<TemporaryJsonElement> temporaryVisits = new LinkedList<>();

    public boolean importContent(Content content, Uri uri, String importFileName)
    {
        Log.d(String.format("importing content from uri [%s] and file name [%s]", uri, importFileName));
        Stopwatch stopwatch = new Stopwatch(true);

        content.clear();

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
                Log.e("not able to fetch ExportFileUri");
                return false;
            }
            success = this.tryPopulateContentFromExportFile(content, exportFileUri);
        }

        if(success)
        {
            Log.i(String.format(Locale.getDefault(), "success, took [%d]ms - saving content...", stopwatch.stop()));
            this.saveContent(content);
        }
        else
        {
            Log.e(String.format(Locale.getDefault(), "failed, took [%d]ms - restoring backup.", stopwatch.stop()));
            content.restoreBackup(false);
        }

        return success;
    }

    private boolean tryPopulateDefaultContentFromDatabaseMock(Content content)
    {
        Log.w("App.config.alwaysLoadFromDatabaseMock = true --> trying to load default content from DatabaseMock");

        DatabaseMock databaseMock = DatabaseMock.getInstance();
        if(databaseMock.loadContent(content))
        {
            Log.w("success");
            return true;
        }
        else
        {
            Log.e("failed");
            return false;
        }
    }

    private boolean tryPopulateContentFromExportFile(Content content, Uri exportFileUri)
    {
        Log.v("populating...");
        Stopwatch stopwatch = new Stopwatch(true);

        String jsonString = this.readStringFromUri(exportFileUri);
        if(this.tryFetchContentFromJsonString(jsonString, content))
        {
            Log.d(String.format(Locale.getDefault(), "success - took [%d]ms", stopwatch.stop()));
            return true;
        }
        else
        {
            Log.e(String.format(Locale.getDefault(), "failed - took [%d]ms", stopwatch.stop()));
            return false;
        }
    }

    @Override
    public boolean loadContent(Content content)
    {
        Log.d("loading...");
        Stopwatch stopwatchLoad = new Stopwatch(true);

        boolean success;
        String jsonString;
        if(!App.isInitialized && App.config.resetToDefaultContentOnStartup())
        {
            this.tryPopulateDefaultContentFromDatabaseMock(content);
            this.saveContent(content);

            Log.i("reloading content in order to get all defaults...");
        }

        jsonString = this.readStringFromInternalStorageFile(App.config.getExportFileName());
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
            Log.i(String.format(Locale.getDefault(), "success - took [%d]ms", stopwatchLoad.stop()));
        }
        else
        {
            Log.e(String.format(Locale.getDefault(), "failed - took [%d]ms", stopwatchLoad.stop()));
        }

        return success;
    }

    private boolean initializeForFirstUse(Content content)
    {
        Log.i("creating root location and defaults for first use...");

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
        Log.v("try fetching...");
        Stopwatch stopwatch = new Stopwatch(true);

        if(!jsonString.isEmpty())
        {
            if(this.tryCreateElementsAndAddToContent(jsonString, content))
            {
                this.buildNodeTree(this.temporaryLocations, content);
                this.buildNodeTree(this.temporaryParks, content);
                this.buildNodeTree(this.temporaryAttractions, content);

                this.createVisitedAttractionsAndAddToVisits(this.temporaryVisits, content);

                Log.d(String.format(Locale.getDefault(), "success - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(String.format(Locale.getDefault(), "failed: json string invalid - took [%d]ms", stopwatch.stop()));
                return false;
            }
        }
        else
        {
            Log.e(String.format(Locale.getDefault(), "failed: json string is empty - took [%d]ms", stopwatch.stop()));
            return false;
        }
    }

    private boolean tryCreateElementsAndAddToContent(String jsonString, Content content)
    {
        Log.v("creating...");

        try
        {
            JSONObject jsonObjectContent = new JSONObject(jsonString);

            if(jsonObjectContent.isNull(Constants.JSON_STRING_IDENTIFIER) || !jsonObjectContent.getString(Constants.JSON_STRING_IDENTIFIER).equals(Constants.JSON_STRING_ID))
            {
                Log.e("JsonString could not be identified as CoasterCreditCounterExportFile");
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
            Log.e(String.format("failed: JSONException [%s]", e.getMessage()));
            return false;
        }

        Log.v("success");

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
        catch(JSONException je)
        {
            Log.e("failed", je);
            throw je;
        }

        Log.d("success");
        return temporaryJsonElements;
    }

    private LinkedList<IElement> createCreditTypes(List<TemporaryJsonElement> temporaryJsonElements)
    {
        LinkedList<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            CreditType creditType = CreditType.create(temporaryJsonElement.name, temporaryJsonElement.uuid);

            if(temporaryJsonElement.isDefault)
            {
                CreditType.setDefault(creditType);
            }

            elements.add(creditType);
        }

        if(!elements.contains(CreditType.getDefault()))
        {
            Log.e("no default fetched from json - adding created default");
            elements.add(CreditType.getDefault());
        }

        Log.d(String.format(Locale.getDefault(), "success - [%d] created", elements.size()));
        return elements;
    }

    private LinkedList<IElement> createCategories(List<TemporaryJsonElement> temporaryJsonElements)
    {
        LinkedList<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            Category category = Category.create(temporaryJsonElement.name, temporaryJsonElement.uuid);

            if(temporaryJsonElement.isDefault)
            {
                Category.setDefault(category);
            }

            elements.add(category);
        }

        if(!elements.contains(Category.getDefault()))
        {
            Log.e("no default fetched from json - adding created default");
            elements.add(Category.getDefault());
        }

        Log.d(String.format(Locale.getDefault(), "success - [%d] created", elements.size()));
        return elements;
    }

    private LinkedList<IElement> createManufacturers(List<TemporaryJsonElement> temporaryJsonElements)
    {
        LinkedList<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            Manufacturer manufacturer = Manufacturer.create(temporaryJsonElement.name, temporaryJsonElement.uuid);

            if(temporaryJsonElement.isDefault)
            {
                Manufacturer.setDefault(manufacturer);
            }

            elements.add(manufacturer);
        }

        if(!elements.contains(Manufacturer.getDefault()))
        {
            Log.e("no default fetched from json - adding created default");
            elements.add(Manufacturer.getDefault());
        }

        Log.d(String.format(Locale.getDefault(), "success - [%d] created", elements.size()));
        return elements;
    }

    private LinkedList<IElement> createModels(List<TemporaryJsonElement> temporaryJsonElements, Content content)
    {
        LinkedList<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            Model model = Model.create(temporaryJsonElement.name, temporaryJsonElement.uuid);

            if(temporaryJsonElement.creditTypeUuid != null)
            {
                model.setCreditType(this.getCreditTypeFromUuid(temporaryJsonElement.creditTypeUuid, content));
            }

            if(temporaryJsonElement.categoryUuid != null)
            {
                model.setCategory(this.getCategoryFromUuid(temporaryJsonElement.categoryUuid, content));
            }

            if(temporaryJsonElement.manufacturerUuid != null)
            {
                model.setManufacturer(this.getManufacturerFromUuid(temporaryJsonElement.manufacturerUuid, content));
            }

            if(temporaryJsonElement.isDefault)
            {
                Model.setDefault(model);
            }

            elements.add(model);
        }

        if(!elements.contains(Model.getDefault()))
        {
            Log.e("no default fetched from json - adding created default");
            elements.add(Model.getDefault());
        }

        Log.d(String.format(Locale.getDefault(), "success - [%d] created", elements.size()));
        return elements;
    }

    private LinkedList<IElement> createStatuses(List<TemporaryJsonElement> temporaryJsonElements)
    {
        LinkedList<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            Status status = Status.create(temporaryJsonElement.name, temporaryJsonElement.uuid);

            if(temporaryJsonElement.isDefault)
            {
                Status.setDefault(status);
            }

            elements.add(status);
        }

        if(!elements.contains(Status.getDefault()))
        {
            Log.e("no default fetched from json - adding created default");
            elements.add(Status.getDefault());
        }

        Log.d(String.format(Locale.getDefault(), "success - [%d] created", elements.size()));
        return elements;
    }

    private LinkedList<IElement> createLocations(LinkedList<TemporaryJsonElement> temporaryJsonElements)
    {
        LinkedList<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            Location element = Location.create(temporaryJsonElement.name, temporaryJsonElement.uuid);
            elements.add(element);
        }

        Log.d(String.format(Locale.getDefault(), "success - [%d] created", elements.size()));
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

        Log.d(String.format(Locale.getDefault(), "success - [%d] created", elements.size()));
        return elements;
    }

    private LinkedList<IElement> createAttractions(LinkedList<TemporaryJsonElement> temporaryJsonElements, Content content)
    {
        LinkedList<IElement> elements = new LinkedList<>();
        for(TemporaryJsonElement temporaryJsonElement : temporaryJsonElements)
        {
            OnSiteAttraction element = OnSiteAttraction.create(temporaryJsonElement.name, temporaryJsonElement.untrackedRideCount, temporaryJsonElement.uuid);

            Model model = this.getModelFromUuid(temporaryJsonElement.modelUuid, content);

            if(!model.isCreditTypeSet())
            {
                element.setCreditType(this.getCreditTypeFromUuid(temporaryJsonElement.creditTypeUuid, content));
            }

            if(!model.isCategorySet())
            {
                element.setCategory(this.getCategoryFromUuid(temporaryJsonElement.categoryUuid, content));
            }

            if(!model.isManufacturerSet())
            {
                element.setManufacturer(this.getManufacturerFromUuid(temporaryJsonElement.manufacturerUuid, content));
            }

            element.setModel(model);
            element.setStatus(this.getStatusFromUuid(temporaryJsonElement.statusUuid, content));

            elements.add(element);
        }

        Log.d(String.format(Locale.getDefault(), "success - [%d] created", elements.size()));
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

        Log.d(String.format(Locale.getDefault(), "success - [%d] created", elements.size()));
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

        Log.d(String.format(Locale.getDefault(), "success - [%d] created", elements.size()));
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
            Log.e(String.format("fetched Element for UUID [%s] is not a CreditType - returning default", uuid));
            return CreditType.getDefault();
        }
    }

    private Category getCategoryFromUuid(UUID uuid, Content content)
    {
        IElement element = content.getContentByUuid(uuid);
        if(element instanceof Category)
        {
            return (Category) element;
        }
        else
        {
            Log.e(String.format("fetched Element for UUID [%s] is not a Category - returning default", uuid));
            return Category.getDefault();
        }
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
            Log.e(String.format("fetched Element for UUID [%s] is not a Manufacturer - returning default", uuid));
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
            Log.e(String.format("fetched Element for UUID [%s] is not a Model - returning default", uuid));
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
            Log.e(String.format("fetched Element for UUID [%s] is not a Status - returning default", uuid));
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

        Log.d("success");
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

        Log.d("success");
    }

    public boolean exportContent(Content content, Uri exportFileDocumentTreeUri, String exportFileName)
    {
        Log.d(String.format("exporting content to uri [%s] under file name [%s]...", exportFileDocumentTreeUri, exportFileName));

        Stopwatch stopwatch = new Stopwatch(true);

        JSONObject jsonObject = this.createContentJsonObject(content);
        if(jsonObject != null)
        {
            Uri exportFileUri = this.fetchExportFileUri(exportFileDocumentTreeUri, exportFileName);

            if(this.writeStringToExternalStorageFile(exportFileUri, jsonObject.toString()))
            {
                Log.i(String.format(Locale.getDefault(), "success - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(String.format(Locale.getDefault(), "failed - took [%d]ms", stopwatch.stop()));
                return false;
            }
        }
        else
        {
            Log.e(String.format(Locale.getDefault(), "failed: json object is null - took [%d]ms", stopwatch.stop()));
            return false;
        }
    }

    @Override
    public boolean saveContent(Content content)
    {
        Log.d("saving...");

        Stopwatch stopwatch = new Stopwatch(true);
        JSONObject jsonObject = this.createContentJsonObject(content);
        if(jsonObject != null)
        {
            if(this.writeStringToInternalStorageFile(App.config.getExportFileName(), jsonObject.toString()))
            {
                Log.i(String.format(Locale.getDefault(), "success - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(String.format(Locale.getDefault(), "failed - took [%d]ms", stopwatch.stop()));
            }
        }
        else
        {
            Log.e(String.format(Locale.getDefault(), "failed: json object is null - took [%d]ms", stopwatch.stop()));
        }

        return false;
    }

    private JSONObject createContentJsonObject(Content content)
    {
        Log.d("creating...");
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

            Log.d(String.format(Locale.getDefault(), "success - took [%d]ms", stopwatch.stop()));
            return jsonObject;
        }
        catch(JSONException je)
        {
            Log.e(String.format(Locale.getDefault(), "failed - took [%d]ms", stopwatch.stop()), je);
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

        Log.v("success");
        return jsonArray;
    }

    public boolean tryLoadPreferences(Preferences preferences)
    {
        if(!App.config.resetToDefaultPreferencesOnStartup())
        {
            Log.d("loading...");
            Stopwatch stopwatch = new Stopwatch(true);

            String jsonString = this.readStringFromInternalStorageFile(App.config.getPreferencesFileName());
            if(this.tryFetchPreferencesFromJsonString(jsonString, preferences))
            {
                Log.i(String.format(Locale.getDefault(), "success - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(String.format(Locale.getDefault(), "failed - took [%d]ms", stopwatch.stop()));
                return false;
            }
        }
        else
        {
            Log.w("overridden by App.config: ResetToDefaultPreferencesOnStartup[TRUE]");
            return false;
        }
    }

    private boolean tryFetchPreferencesFromJsonString(String jsonString, Preferences preferences)
    {
        Log.v("fetching...");

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

                Log.d(String.format(Locale.getDefault(), "success - took [%d]ms", stopwatch.stop()));
                return true;
            }
            catch(JSONException e)
            {
                Log.e(String.format(Locale.getDefault(), "failed - took [%d]ms", stopwatch.stop()), e);
                return false;
            }
        }
        else
        {
            Log.e("failed: json string is empty");
            return false;
        }
    }

    public boolean savePreferences(Preferences preferences)
    {
        Log.d("saving...");

        Stopwatch stopwatch = new Stopwatch(true);

        JSONObject jsonObject = preferences.toJson();

        if(jsonObject != null)
        {
            if(this.writeStringToInternalStorageFile(App.config.getPreferencesFileName(), jsonObject.toString()))
            {
                Log.i(String.format(Locale.getDefault(), "success - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(String.format(Locale.getDefault(), "failed: could not write to json file - took [%d]ms", stopwatch.stop()));
                return false;
            }
        }
        else
        {
            Log.e(String.format(Locale.getDefault(), "failed: json object is null - took [%d]ms", stopwatch.stop()));
            return false;
        }
    }

    public boolean validateImportFileUri(Uri uri, String importFileName)
    {
        if(this.fetchImportFileUri(uri, importFileName) != null)
        {
            Log.d(String.format("uri [%s] with importFileName [%s] is valid", uri, importFileName));
            return true;
        }
        else
        {
            Log.e(String.format("uri [%s] with importFileName [%s] invalid", uri, importFileName));
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
                Log.v(String.format("passed uri [%s] is most likely ImportFileUri", uri));
                importFileUri = uri;
            }
            else
            {
                Log.e(String.format("passed uri [%s] is neither Directory nor .json file", uri));
            }
        }
        else
        {
            if(importFileName != null)
            {
                DocumentFile importFile = DocumentFile.fromTreeUri(App.getContext(), uri).findFile(importFileName);

                if(importFile != null)
                {
                    Log.i(String.format("fetched ExportFileUri [%s]", importFile.getUri()));
                    importFileUri = importFile.getUri();
                }
                else
                {
                    Log.e(String.format("ImportFile [%s] not found in Directory [%s]", importFileName, uri));
                }
            }
            else
            {
                Log.e(String.format("unable to fetch uri from [%s]- no ExportFileName passed", uri));
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
        Log.d(String.format("reading from [%s]...", uri));
        InputStream inputStream = null;

        try
        {
            inputStream = App.getContext().getContentResolver().openInputStream(uri);

        }
        catch (FileNotFoundException e)
        {
            Log.e(String.format("FileNotFoundException: file at [%s] does not exist: [%s]", uri, e.getMessage()));
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
                Log.e(String.format("IOException: [%s]", e.getMessage()));
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
            Log.e("InputStream is NULL");
        }

        return output;
    }

    public String readStringFromInternalStorageFile(String fileName)
    {
        Log.d(String.format("reading from [%s]", fileName));

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
                Log.e(String.format("FileNotFoundException: [%s] does not exist!\n[%s]", fileName, e.getMessage()));
            }
            catch(IOException e)
            {
                Log.e(String.format("IOException: reading FileInputStream failed!\n[%s] ", e.getMessage()));
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
                    Log.e(String.format("IOException: closing FileInputStream failed!\n[%s] ", e.getMessage()));
                }
            }
        }
        else
        {
            Log.e(String.format("file [%s] does not exist - returning empty output", fileName));
        }

        return output;
    }

    public boolean writeStringToInternalStorageFile(String fileName, String input)
    {
        Log.d(String.format("writing to [%s]", fileName));

        File file = new File(App.getContext().getFilesDir(), fileName);

        FileOutputStream fileOutputStream = null;
        try
        {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(input.getBytes());
        }
        catch(FileNotFoundException e)
        {
            Log.e(String.format("FileNotFoundException: [%s] does not exist!\n[%s]", fileName, e.getMessage()));
        }
        catch(IOException e)
        {
            Log.e(String.format("IOException: writing FileOutputStream failed!\n[%s]", e.getMessage()));
        }
        finally
        {
            try
            {
                fileOutputStream.close();
            }
            catch(IOException e)
            {
                Log.e(String.format("IOException: closing FileOutputStream failed!\n[%s]", e.getMessage()));
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
            Log.e(String.format("Exception [%s]", e.getMessage()));
            return false;
        }

        return true;
    }

    @Override
    public boolean create(Set<IElement> elements)
    {
        Log.e("empty implementation to satisfy interface");
        return false;
    }

    @Override
    public boolean update(Set<IElement> elements)
    {
        Log.e("empty implementation to satisfy interface");
        return false;
    }

    @Override
    public boolean delete(Set<IElement> elements)
    {
        Log.e("empty implementation to satisfy interface");
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
    public StatisticsGlobalTotals fetchStatisticsGlobalTotals()
    {
        StatisticsGlobalTotals statisticsGlobalTotals = new StatisticsGlobalTotals();

        statisticsGlobalTotals.totalCredits = this.fetchTotalCreditsCount();
        statisticsGlobalTotals.totalRides = this.fetchTotalRideCount();
        statisticsGlobalTotals.totalVisits = this.fetchTotalVisits();
        statisticsGlobalTotals.totalParksVisited = this.fetchTotalVisitedParksCount();

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

        Log.d(String.format(Locale.getDefault(), "[%d] found - took [%d]ms", totalCreditsCount, stopwatch.stop()));

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

        Log.d(String.format(Locale.getDefault(), "[%d] found - took [%d]ms", totalCreditsRideCount, stopwatch.stop()));

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

        Log.d(String.format(Locale.getDefault(), "[%d] found - took [%d]ms", totalVisits, stopwatch.stop()));

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

        Log.d(String.format(Locale.getDefault(), "[%d] found - took [%d]ms", totalVisitedParksCount, stopwatch.stop()));

        return totalVisitedParksCount;
    }

    @Override
    public List<IElement> fetchCurrentVisits()
    {
        Stopwatch stopwatch = new Stopwatch(true);

        List<IElement> currentVisits = Visit.fetchVisitsForYearAndDay(Calendar.getInstance(), App.content.getContentAsType(Visit.class));

        Log.d(String.format(Locale.getDefault(), "[%d] found - took [%d]ms", currentVisits.size(), stopwatch.stop()));

        return currentVisits;
    }
}