package de.juliusawen.coastercreditcounter.toolbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Attraction;
import de.juliusawen.coastercreditcounter.content.AttractionCategory;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.content.Park;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Content;
import de.juliusawen.coastercreditcounter.presentation.activities.elements.EditElementActivity;
import de.juliusawen.coastercreditcounter.presentation.activities.elements.SortElementsActivity;
import de.juliusawen.coastercreditcounter.presentation.activities.locations.AddLocationActivity;
import de.juliusawen.coastercreditcounter.presentation.activities.locations.ShowLocationsActivity;
import de.juliusawen.coastercreditcounter.presentation.activities.parks.ShowParkActivity;

public abstract class ActivityTool
{
    public static void startActivityShow(Activity activity, Element element)
    {
        Class type = null;

        if(element.isInstance(Location.class))
        {
            type = ShowLocationsActivity.class;
        }
        else if(element.isInstance(Park.class))
        {
            type = ShowParkActivity.class;
        }

        if(type != null)
        {
            Intent intent = new Intent(activity, type);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, element.getUuid().toString());
            activity.startActivity(intent);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityShow:: started activity [%s] for %s...",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), element));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityTool.startActivityShow:: unable to start activity: unknown type %s", element));
        }
    }

    public static void startActivityAdd(Activity activity, int requestId, Element parent)
    {
        Class type = null;

        if(parent.isInstance(Location.class))
        {
            type = AddLocationActivity.class;
        }

        if(type != null)
        {
            Intent intent = new Intent(activity.getApplicationContext(), type);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, parent.getUuid().toString());
            activity.startActivityForResult(intent, requestId);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityAdd:: started activity [%s] for %s...",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), parent));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityTool.startActivityAdd:: unable to start activity: unknown type %s", parent));
        }
    }

    public static void startActivitySort(Activity activity, int requestId, List<Element> elementsToSort)
    {
        Context context = activity.getApplicationContext();
        Element firstElement = elementsToSort.get(0);
        String toolbarTitle = null;

        if(firstElement.isInstance(Location.class))
        {
            toolbarTitle = context.getString(R.string.title_sort_locations);
        }
        else if(firstElement.isInstance(Park.class))
        {
            toolbarTitle = context.getString(R.string.title_sort_parks);
        }
        else if(firstElement.isInstance(Attraction.class))
        {
            toolbarTitle = context.getString(R.string.title_sort_attractions);
        }
        else if(firstElement.isInstance(AttractionCategory.class))
        {
            toolbarTitle = context.getString(R.string.title_sort_attraction_categories);
        }

        if(toolbarTitle != null)
        {
            Intent intent = new Intent(context, SortElementsActivity.class);
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
            intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, Content.getUuidStringsFromElements(elementsToSort));
            activity.startActivityForResult(intent, requestId);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivitySort:: started activity [%s] for #[%d] elements...",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), elementsToSort.size()));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityTool.startActivitySort:: unable to start activity: unknown type %s", firstElement));
        }
    }

    public static void startActivityEdit(Activity activity, Element elementToEdit)
    {
        Context context = activity.getApplicationContext();
        Class type = null;
        String toolbarSubtitle = null;

        if(elementToEdit.isInstance(Location.class))
        {
            if(elementToEdit.isRootElement())
            {
                type = EditElementActivity.class;
                toolbarSubtitle = context.getString(R.string.subtitle_edit_root_location);
            }
            else
            {
                type = EditElementActivity.class;
                toolbarSubtitle = context.getString(R.string.subtitle_edit_location);
            }
        }
        else if(elementToEdit.isInstance(AttractionCategory.class))
        {
            type = EditElementActivity.class;
            toolbarSubtitle = context.getString(R.string.subtitle_edit_attraction_category);
        }

        if(type != null)
        {
            Intent intent = new Intent(activity.getApplicationContext(), type);
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarSubtitle);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, elementToEdit.getUuid().toString());
            activity.startActivity(intent);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityEdit:: started activity [%s] for %s...",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), elementToEdit));
        }
        else
        {
            Log.d(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityTool.startActivityEdit:: unable to start activity: unknown type %s", elementToEdit));
        }
    }
}
