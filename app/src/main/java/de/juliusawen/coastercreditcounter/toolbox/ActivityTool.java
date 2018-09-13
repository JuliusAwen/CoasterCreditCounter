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
import de.juliusawen.coastercreditcounter.presentation.activities.locations.AddLocationActivity;
import de.juliusawen.coastercreditcounter.presentation.activities.parks.ShowParkActivity;

public abstract class ActivityTool
{
    public static void startActivityShowLocations(Activity activity, Element location)
    {
        Intent intent = activity.getIntent();

        Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityShowLocations:: starting activty [%s]...",
                StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName())));

        intent.putExtra(Constants.EXTRA_ELEMENT_UUID, location.getUuid().toString());
        activity.startActivity(intent);
    }

    public static void startActivityAddLocation(Activity activity, int requestId, Element parentLocation)
    {
        Intent intent = new Intent(activity.getApplicationContext(), AddLocationActivity.class);

        Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityAddLocation:: starting activty [%s]...",
                StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName())));

        intent.putExtra(Constants.EXTRA_ELEMENT_UUID, parentLocation.getUuid().toString());
        activity.startActivityForResult(intent, requestId);
    }

    public static void startActivityShowPark(Activity activity, Element park)
    {
        Intent intent = new Intent(activity, ShowParkActivity.class);

        Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityEditElement:: starting activty [%s]...",
                StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName())));

        intent.putExtra(Constants.EXTRA_ELEMENT_UUID, park.getUuid().toString());
        activity.startActivity(intent);
    }

    public static void startActivitySortElements(Activity activity, int requestId, List<Element> elementsToSort, String toolbarTitle)
    {
        Intent intent = new Intent(activity.getApplicationContext(), SortElementsActivity.class);

        Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityEditElement:: starting activty [%s]...",
                StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName())));

        intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, Content.getUuidStringsFromElements(elementsToSort));
        intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
        activity.startActivityForResult(intent, requestId);
    }

    public static void startActivityEditElement(Activity activity, Element elementToEdit, String toolbarTitle)
    {
        Intent intent = new Intent(activity.getApplicationContext(), EditElementActivity.class);

        Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivitySortElements:: starting activty [%s]...",
                StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName())));

        intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
        intent.putExtra(Constants.EXTRA_ELEMENT_UUID, elementToEdit.getUuid().toString());
        activity.startActivity(intent);
    }
}
