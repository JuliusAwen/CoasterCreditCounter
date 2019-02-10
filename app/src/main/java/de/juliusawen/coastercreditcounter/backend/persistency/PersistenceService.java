package de.juliusawen.coastercreditcounter.backend.persistency;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import androidx.annotation.Nullable;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class PersistenceService extends IntentService
{
    IDatabaseWrapper databaseWrapper;

    public PersistenceService()
    {
        super(Constants.PERSISTENCY_SERVICE_NAME);
        this.databaseWrapper = App.persistence.getDatabaseWrapper();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        String action = Objects.requireNonNull(intent).getAction();

        if(action != null)
        {
            Log.d(Constants.LOG_TAG, String.format("PersistenceService.onHandleIntent:: action is [%s]", action));

            switch(action)
            {
                case Constants.ACTION_CREATE:

                    this.create(intent);
                    break;

                case Constants.ACTION_DELETE:

                    this.delete(intent);
                    break;

                case Constants.ACTION_UPDATE:

                    this.update(intent);
                    break;

                case Constants.ACTION_SAVE:

                    this.save();
                    break;
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, "PersistenceService.onHandleIntent:: DataString is empty");
            throw new IllegalStateException();
        }
    }

    private void create(Intent intent)
    {
        Set<IElement> elementsToCreate = new HashSet<>(App.content.getContentByUuidStrings(intent.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_TO_CREATE_UUIDS)));
        Log.v(Constants.LOG_TAG, String.format("PersistenceService.create:: creating [%d] elements...", elementsToCreate.size()));

        if(this.databaseWrapper.create(elementsToCreate))
        {
            Log.d(Constants.LOG_TAG, "PersistenceService.synchronize:: elements created successfully");
        }
        else
        {
            Log.e(Constants.LOG_TAG, "PersistenceService.synchronize:: create elements failed");
        }
    }

    private void delete(Intent intent)
    {
        Set<IElement> elementsToDelete = new HashSet<>(App.content.getContentByUuidStrings(intent.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_TO_DELETE_UUIDS)));

        Log.v(Constants.LOG_TAG, String.format("PersistenceService.delete:: deleting [%d] elements...", elementsToDelete.size()));

        if(databaseWrapper.delete(elementsToDelete))
        {
            Log.d(Constants.LOG_TAG, "PersistenceService.delete:: elements deleted successfully");
        }
        else
        {
            Log.e(Constants.LOG_TAG, "PersistenceService.delete:: deleting elements failed");
        }
    }

    private void update(Intent intent)
    {
        Set<IElement> elementsToUpdate = new HashSet<>(App.content.getContentByUuidStrings(intent.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_TO_UPDATE_UUIDS)));
        Log.v(Constants.LOG_TAG, String.format("PersistenceService.update:: updating [%d] elements...", elementsToUpdate.size()));

        if(databaseWrapper.update(elementsToUpdate))
        {
            Log.d(Constants.LOG_TAG, "PersistenceService.update:: elements updated successfully");
        }
        else
        {
            Log.e(Constants.LOG_TAG, "PersistenceService.update:: updating elements failed");
        }
    }

    private void save()
    {
        Log.v(Constants.LOG_TAG, "PersistenceService.save:: saving content...");

        if(databaseWrapper.saveContent(App.content))
        {
            Log.d(Constants.LOG_TAG, "PersistenceService.save:: elements saved successfully");
        }
        else
        {
            Log.e(Constants.LOG_TAG, "PersistenceService.save:: saving elements failed");
        }
    }
}
