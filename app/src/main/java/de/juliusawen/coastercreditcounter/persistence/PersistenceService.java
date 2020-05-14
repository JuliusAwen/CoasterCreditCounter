package de.juliusawen.coastercreditcounter.persistence;

import android.app.IntentService;
import android.content.Intent;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public class PersistenceService extends IntentService
{
    final IDatabaseWrapper databaseWrapper;

    public PersistenceService()
    {
        super(Constants.PERSISTENCY_SERVICE_NAME);
        this.databaseWrapper = App.persistence.getDatabaseWrapper();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        String action = (intent).getAction();

        if(action != null)
        {
            Log.v(String.format("action is [%s]", action));

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
            Log.e("DataString is empty");
            throw new IllegalStateException();
        }
    }

    private void create(Intent intent)
    {
        Set<IElement> elementsToCreate = new HashSet<>(App.content.getContentByUuidStrings(intent.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_TO_CREATE_UUIDS)));
        Log.d(String.format(Locale.getDefault(), "creating [%d] elements...", elementsToCreate.size()));

        if(this.databaseWrapper.create(elementsToCreate))
        {
            Log.i("success");
        }
        else
        {
            Log.e("failed");
        }
    }

    private void delete(Intent intent)
    {
        Set<IElement> elementsToDelete = new HashSet<>(App.content.getContentByUuidStrings(intent.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_TO_DELETE_UUIDS)));
        Log.d(String.format(Locale.getDefault(), "deleting [%d] elements...", elementsToDelete.size()));

        if(databaseWrapper.delete(elementsToDelete))
        {
            Log.i("success");
        }
        else
        {
            Log.e("failed");
        }
    }

    private void update(Intent intent)
    {
        Set<IElement> elementsToUpdate = new HashSet<>(App.content.getContentByUuidStrings(intent.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_TO_UPDATE_UUIDS)));
        Log.d(String.format(Locale.getDefault(), "updating [%d] elements...", elementsToUpdate.size()));

        if(databaseWrapper.update(elementsToUpdate))
        {
            Log.i("success");
        }
        else
        {
            Log.e("failed");
        }
    }

    private void save()
    {
        Log.d("saving content...");

        if(databaseWrapper.saveContent(App.content))
        {
            Log.i("success");
        }
        else
        {
            Log.e("failed");
        }
    }
}
