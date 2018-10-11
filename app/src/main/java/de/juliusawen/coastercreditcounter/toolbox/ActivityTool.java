package de.juliusawen.coastercreditcounter.toolbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Attraction;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Location;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.presentation.elements.EditElementActivity;
import de.juliusawen.coastercreditcounter.presentation.elements.PickElementsActivity;
import de.juliusawen.coastercreditcounter.presentation.elements.SortElementsActivity;
import de.juliusawen.coastercreditcounter.presentation.locations.AddLocationActivity;
import de.juliusawen.coastercreditcounter.presentation.locations.ShowLocationsActivity;
import de.juliusawen.coastercreditcounter.presentation.parks.ShowParkActivity;
import de.juliusawen.coastercreditcounter.presentation.visits.AddVisitActivity;
import de.juliusawen.coastercreditcounter.presentation.visits.ShowVisitActivity;

public abstract class ActivityTool
{
    public static void startActivityShow(Context context, Element element)
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
        else if(element.isInstance(Visit.class))
        {
            type = ShowVisitActivity.class;
        }

        if(type != null)
        {
            Intent intent = new Intent(context, type);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, element.getUuid().toString());
            context.startActivity(intent);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityShow:: started activity [%s] for %s",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), element));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityTool.startActivityShow:: unable to start context: unknown type %s", element));
        }
    }

    public static void startAddVisitActivityForResult(Context context, int requestCode, Element park)
    {
        Intent intent = new Intent(context, AddVisitActivity.class);
        intent.putExtra(Constants.EXTRA_ELEMENT_UUID, park.getUuid().toString());
        ((Activity)context).startActivityForResult(intent, requestCode);

        Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityAddForResult:: started activity [%s] for parent %s - add visit",
                StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), park));
    }

    public static void startActivityEditForResult(Context context, int requestCode, Element elementToEdit)
    {
        Class type = null;
        String toolbarSubtitle = null;

        if(elementToEdit.isInstance(Location.class))
        {
            if(elementToEdit.isRootElement())
            {
                type = EditElementActivity.class;
                toolbarSubtitle = context.getString(R.string.subtitle_root_location_edit);
            }
            else
            {
                type = EditElementActivity.class;
                toolbarSubtitle = context.getString(R.string.subtitle_location_edit);
            }
        }
        else if(elementToEdit.isInstance(AttractionCategory.class))
        {
            type = EditElementActivity.class;
            toolbarSubtitle = context.getString(R.string.subtitle_attraction_category_edit);
        }

        if(type != null)
        {
            Intent intent = new Intent(context, type);
            intent.putExtra(Constants.EXTRA_TOOLBAR_SUBTITLE, toolbarSubtitle);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, elementToEdit.getUuid().toString());
            ((Activity)context).startActivityForResult(intent, requestCode);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityEditForResult:: started activity [%s] for %s",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), elementToEdit));
        }
        else
        {
            Log.d(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityTool.startActivityEditForResult:: unable to start activity: unknown type %s", elementToEdit));
        }
    }

    public static void startActivityAddForResult(Context context, int requestCode, Element parent)
    {
        Class type = null;

        if(parent.isInstance(Location.class))
        {
            type = AddLocationActivity.class;
        }
        else if(parent.isInstance(Visit.class))
        {
            type = Visit.class;
        }

        if(type != null)
        {
            Intent intent = new Intent(context, type);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, parent.getUuid().toString());
            ((Activity)context).startActivityForResult(intent, requestCode);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityAddForResult:: started activity [%s] for %s  with requestCode [%d]",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), parent, requestCode));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityTool.startActivityAddForResult:: unable to start context: unknown type %s", parent));
        }
    }

    public static void startActivitySortForResult(Context context, int requestCode, List<Element> elementsToSort)
    {
        Element firstElement = elementsToSort.get(0);
        String toolbarTitle = null;

        if(firstElement.isInstance(Location.class))
        {
            toolbarTitle = context.getString(R.string.title_locations_sort);
        }
        else if(firstElement.isInstance(Park.class))
        {
            toolbarTitle = context.getString(R.string.title_parks_sort);
        }
        else if(firstElement.isInstance(Attraction.class))
        {
            toolbarTitle = context.getString(R.string.title_attractions_sort);
        }
        else if(firstElement.isInstance(AttractionCategory.class))
        {
            toolbarTitle = context.getString(R.string.title_attraction_categories_sort);
        }

        if(toolbarTitle != null)
        {
            Intent intent = new Intent(context, SortElementsActivity.class);
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
            intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(elementsToSort));
            ((Activity)context).startActivityForResult(intent, requestCode);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivitySortForResult:: started activity [%s] for #[%d] elements with requestCode [%d]",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), elementsToSort.size(), requestCode));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityTool.startActivitySortForResult:: unable to start c: unknown type %s", firstElement));
        }
    }

    public static void startActivityPickForResult(Context context, int requestCode, List<Element> elementsToPickFrom)
    {
        Element firstElement = elementsToPickFrom.get(0);
        String toolbarTitle = null;
        String toolbarSubtitle = null;

        if(firstElement.isInstance(Location.class))
        {
            toolbarTitle = context.getString(R.string.title_locations_pick);
            toolbarSubtitle = context.getString(R.string.subtitle_locations_pick_description);
        }
        else if(firstElement.isInstance(Park.class))
        {
            toolbarTitle = context.getString(R.string.title_parks_pick);
            toolbarSubtitle = context.getString(R.string.subtitle_parks_pick_description);
        }
        else if(firstElement.isInstance(Attraction.class) || firstElement.isInstance(AttractionCategory.class))
        {
            toolbarTitle = context.getString(R.string.title_attractions_pick);
            toolbarSubtitle = context.getString(R.string.subtitle_attractions_description_pick);
        }

        if(toolbarTitle != null)
        {
            Intent intent = new Intent(context, PickElementsActivity.class);
            intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(elementsToPickFrom));
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
            intent.putExtra(Constants.EXTRA_TOOLBAR_SUBTITLE, toolbarSubtitle);
            ((Activity)context).startActivityForResult(intent, requestCode);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityPickForResult:: started activity [%s] for #[%d] elements with requestCode [%d]",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), elementsToPickFrom.size(), requestCode));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityTool.startActivityPickForResult:: unable to start context: unknown type %s", firstElement));
        }
    }
}
