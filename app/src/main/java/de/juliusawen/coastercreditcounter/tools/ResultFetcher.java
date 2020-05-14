package de.juliusawen.coastercreditcounter.tools;

import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public abstract class ResultFetcher
{
    public static IElement fetchResultElement(Intent data)
    {
        String resultElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
        IElement resultElement = null;
        if(resultElementUuidString != null)
        {
            resultElement = App.content.getContentByUuid(UUID.fromString(resultElementUuidString));
            Log.d(Constants.LOG_TAG, String.format("ResultFetcher.fetchResultElement:: result element %s fetched", resultElement));
        }
        else
        {
            Log.d(Constants.LOG_TAG, "ResultFetcher.fetchResultElement:: no result element fetched");
        }

        return resultElement;
    }

    public static ArrayList<IElement> fetchResultElements(Intent data)
    {
        ArrayList<String> resultElementsUuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
        ArrayList<IElement> resultElements = App.content.getContentByUuidStrings(resultElementsUuidStrings);

        Log.d(Constants.LOG_TAG, String.format("ResultFetcher.fetchResultElements:: [%d] result elements fetched", resultElements.size()));

        return resultElements;
    }
}
