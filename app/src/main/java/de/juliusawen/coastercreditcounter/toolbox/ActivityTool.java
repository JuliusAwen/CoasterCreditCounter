package de.juliusawen.coastercreditcounter.toolbox;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Objects;

import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Content;
import de.juliusawen.coastercreditcounter.presentation.activities.elements.EditElementActivity;
import de.juliusawen.coastercreditcounter.presentation.activities.elements.SortElementsActivity;

public abstract class ActivityTool
{
    public static void startSortElementsActivity(Activity activity, int requestId, List<Element> elementsToSort, String toolbarTitle)
    {
        Intent intent = new Intent(activity.getApplicationContext(), SortElementsActivity.class);

        Log.i(Constants.LOG_TAG, String.format("ActivityTool.startEditElementActivity:: starting activty [%s]...",
                StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName())));

        intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, Content.getUuidStringsFromElements(elementsToSort));
        intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
        activity.startActivityForResult(intent, requestId);
    }

    public static void startEditElementActivity(Activity activity, Element elementToEdit, String toolbarTitle)
    {
        Intent intent = new Intent(activity.getApplicationContext(), EditElementActivity.class);

        Log.i(Constants.LOG_TAG, String.format("ActivityTool.startSortElementsActivity:: starting activty [%s]...",
                StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName())));

        intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
        intent.putExtra(Constants.EXTRA_ELEMENT_UUID, elementToEdit.getUuid().toString());
        activity.startActivity(intent);
    }
}
