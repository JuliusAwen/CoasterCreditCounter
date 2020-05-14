package de.juliusawen.coastercreditcounter.persistence;

import android.net.Uri;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.application.Content;
import de.juliusawen.coastercreditcounter.application.Preferences;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.statistics.StatisticsGlobalTotals;
import de.juliusawen.coastercreditcounter.persistence.databaseMock.DatabaseMock;
import de.juliusawen.coastercreditcounter.persistence.jsonHandler.JsonHandler;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

public class Persistence
{
    private IDatabaseWrapper databaseWrapper;
    private final JsonHandler jsonHandler;

    private static Persistence instance;

    public static Persistence getInstance()
    {
        if(Persistence.instance == null)
        {
            Persistence.instance = new Persistence();
        }

        return instance;
    }

    private Persistence()
    {
        this.jsonHandler = new JsonHandler();

        switch(App.config.getDatabaseWrapper())
        {
            case Constants.DATABASE_WRAPPER_DATABASE_MOCK:
            {
                this.databaseWrapper = DatabaseMock.getInstance();
                break;
            }

            case Constants.DATABASE_WRAPPER_JSON_HANDLER:
            {
                this.databaseWrapper = this.jsonHandler;
                break;
            }
        }

        Log.frame(LogLevel.INFO, "instantiated", '#', true);
    }

    public IDatabaseWrapper getDatabaseWrapper()
    {
        return this.databaseWrapper;
    }

    public boolean loadContent(Content content)
    {
        Log.i("loading content...");
        return this.databaseWrapper.loadContent(content);
    }

    public boolean saveContent(Content content)
    {
        Log.i("saving content...");
        return this.databaseWrapper.saveContent(content);
    }

    public boolean loadPreferences(Preferences preferences)
    {
        Log.i("loading preferences...");

        return this.jsonHandler.loadPreferences(preferences);
    }

    public boolean savePreferences(Preferences preferences)
    {
        Log.i("saving preferences...");
        return this.jsonHandler.savePreferences(preferences);
    }

    public boolean validateImportFileUri(Uri uri, String importFileName)
    {
        Log.i("validating...");
        return this.jsonHandler.validateImportFileUri(uri, importFileName);
    }

    public boolean importContent(Uri uri, String exportFileName)
    {
        Log.i("importing content via JsonHandler...");
        return this.jsonHandler.importContent(App.content, uri, exportFileName);
    }

    public boolean exportFileExists(Uri exportFileDocumentTreeUri, String exportFileName)
    {
        return this.jsonHandler.exportFileExists(exportFileDocumentTreeUri, exportFileName);
    }

    public boolean exportContent(Uri exportFileDocumentTreeUri, String exportFileName)
    {
        Log.i("exporting content via JsonHandler...");
        return this.jsonHandler.exportContent(App.content, exportFileDocumentTreeUri, exportFileName);
    }

    public boolean synchronize(Set<IElement> elementsToCreate, Set<IElement> elementsToUpdate, Set<IElement> elementsToDelete)
    {
        int size;

        Set<IElement> elementsToCreateAndToDelete = new HashSet<>(elementsToCreate);
        elementsToCreateAndToDelete.retainAll(elementsToDelete);


        size = elementsToCreate.size();
        elementsToCreate.removeAll(elementsToDelete);
        if(elementsToCreate.size() != size)
        {
            Log.d(String.format(Locale.getDefault(), "removed [%d] elements from ElementsToCreate since they will be deleted anyway", size - elementsToCreate.size()));
        }

        size = elementsToUpdate.size();
        elementsToUpdate.removeAll(elementsToDelete);
        if(elementsToUpdate.size() != size)
        {
            Log.d(String.format(Locale.getDefault(), "removed [%d] elements from ElementsToUpdate since they will be deleted anyway", size - elementsToUpdate.size()));
        }

        size = elementsToUpdate.size();
        elementsToUpdate.removeAll(elementsToCreate);
        if(elementsToUpdate.size() != size)
        {
            Log.d(String.format(Locale.getDefault(), "removed [%d] elements from ElementsToUpdate since they will be created in updated state anyway", size - elementsToUpdate.size()));
        }

        size = elementsToDelete.size();
        elementsToDelete.removeAll(elementsToCreateAndToDelete);
        if(elementsToDelete.size() != size)
        {
            Log.d(String.format(Locale.getDefault(), "removed [%d] elements from ElementsToDelete since they won't be created anyway", size - elementsToDelete.size()));
        }

        Log.i(String.format(Locale.getDefault(), "creating [%d], updating [%d], deleting [%d] elements...", elementsToCreate.size(), elementsToUpdate.size(), elementsToDelete.size()));


        for(IElement element : elementsToCreate)
        {
            Log.d(String.format("CREATE %s", element));
        }

        for(IElement element : elementsToUpdate)
        {
            Log.d(String.format("UPDATE %s", element));
        }

        for(IElement element : elementsToDelete)
        {
            Log.d(String.format("DELETE %s", element));
        }

        return this.databaseWrapper.synchronize(elementsToCreate, elementsToUpdate, elementsToDelete);
    }

    public StatisticsGlobalTotals fetchStatisticsGlobalTotals()
    {
        return this.databaseWrapper.fetchStatisticsGlobalTotals();
    }

    public List<IElement> fetchCurrentVisits()
    {
        return this.databaseWrapper.fetchCurrentVisits();
    }
}