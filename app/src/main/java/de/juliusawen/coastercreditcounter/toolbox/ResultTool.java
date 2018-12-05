package de.juliusawen.coastercreditcounter.toolbox;

import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.elements.IElement;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;

public abstract class ResultTool
{
    public static IElement fetchSelectedElement(Intent data)
    {
        String selectedElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
        IElement selectedElement = null;
        if(selectedElementUuidString != null)
        {
            selectedElement = App.content.getContentByUuid(UUID.fromString(selectedElementUuidString));
            Log.d(Constants.LOG_TAG, String.format("ResultTool.fetchSelectedElement:: selected element %s fetched", selectedElement));
        }
        else
        {
            Log.v(Constants.LOG_TAG, "ResultTool.fetchSelectedElement:: no selected element fetched");
        }

        return selectedElement;
    }

    public static List<IElement> fetchResultElements(Intent data)
    {
        List<String> resultElementsUuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
        List<IElement> resultElements = App.content.fetchElementsByUuidStrings(resultElementsUuidStrings);

        Log.d(Constants.LOG_TAG, String.format("ResultTool.fetchSelectedElement:: [%d] result elements fetched", resultElements.size()));

        return resultElements;
    }
}
