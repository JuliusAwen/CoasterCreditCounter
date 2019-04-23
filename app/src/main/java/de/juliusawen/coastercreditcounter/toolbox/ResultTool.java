package de.juliusawen.coastercreditcounter.toolbox;

import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.globals.Constants;

public abstract class ResultTool
{
    public static IElement fetchResultElement(Intent data)
    {
        String resultElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
        IElement selectedElement = null;
        if(resultElementUuidString != null)
        {
            selectedElement = App.content.getContentByUuid(UUID.fromString(resultElementUuidString));
            Log.d(Constants.LOG_TAG, String.format("ResultTool.fetchResultElement:: selected element %s fetched", selectedElement));
        }
        else
        {
            Log.d(Constants.LOG_TAG, "ResultTool.fetchResultElement:: no selected element fetched");
        }

        return selectedElement;
    }

    public static List<IElement> fetchResultElements(Intent data)
    {
        List<String> resultElementsUuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
        List<IElement> resultElements = App.content.getContentByUuidStrings(resultElementsUuidStrings);

        Log.d(Constants.LOG_TAG, String.format("ResultTool.fetchResultElements:: [%d] result elements fetched", resultElements.size()));

        return resultElements;
    }
}
