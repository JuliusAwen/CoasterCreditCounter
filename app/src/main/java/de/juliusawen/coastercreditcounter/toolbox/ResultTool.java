package de.juliusawen.coastercreditcounter.toolbox;

import android.content.Intent;
import android.util.Log;

import java.util.List;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;

public abstract class ResultTool
{
    public static Element fetchSelectedElement(Intent data)
    {
        String selectedElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
        Element selectedElement = null;
        if(selectedElementUuidString != null)
        {
            selectedElement = App.content.fetchElementByUuidString(selectedElementUuidString);
            Log.d(Constants.LOG_TAG, String.format("ResultTool.fetchSelectedElement:: selected element %s fetched", selectedElement));
        }
        else
        {
            Log.v(Constants.LOG_TAG, "ResultTool.fetchSelectedElement:: no selected element fetched");
        }

        return selectedElement;
    }

    public static List<Element> fetchResultElements(Intent data)
    {
        List<String> resultElementsUuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
        List<Element> resultElements = App.content.fetchElementsByUuidStrings(resultElementsUuidStrings);

        Log.d(Constants.LOG_TAG, String.format("ResultTool.fetchSelectedElement:: [%d] result elements fetched", resultElements.size()));

        return resultElements;
    }
}
