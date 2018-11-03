package de.juliusawen.coastercreditcounter.toolbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.presentation.elements.EditElementActivity;
import de.juliusawen.coastercreditcounter.presentation.elements.PickElementsActivity;
import de.juliusawen.coastercreditcounter.presentation.elements.SortElementsActivity;
import de.juliusawen.coastercreditcounter.presentation.locations.CreateLocationActivity;
import de.juliusawen.coastercreditcounter.presentation.locations.ShowLocationsActivity;
import de.juliusawen.coastercreditcounter.presentation.orphanElements.CreateAttractionCategoryActivity;
import de.juliusawen.coastercreditcounter.presentation.orphanElements.ShowAttractionCategoriesActivity;
import de.juliusawen.coastercreditcounter.presentation.parks.ShowParkActivity;
import de.juliusawen.coastercreditcounter.presentation.visits.CreateVisitActivity;
import de.juliusawen.coastercreditcounter.presentation.visits.ShowVisitActivity;

public abstract class ActivityTool
{
    public static void startActivityShow(Context context, int requestCode, Element element)
    {
        Class type = null;

        if(requestCode == Constants.REQUEST_SHOW_LOCATION)
        {
            type = ShowLocationsActivity.class;
        }
        else if(requestCode == Constants.REQUEST_SHOW_PARK)
        {
            type = ShowParkActivity.class;
        }
        else if(requestCode == Constants.REQUEST_SHOW_VISIT)
        {
            type = ShowVisitActivity.class;
        }
        else if(requestCode == Constants.REQUEST_SHOW_ATTRACTION_CATEGORIES)
        {
            type = ShowAttractionCategoriesActivity.class;
        }

        if(type != null && element != null)
        {
            Intent intent = new Intent(context, type);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, element.getUuid().toString());
            context.startActivity(intent);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityShow:: started [%s] for %s",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), element));
        }
        else if(type != null)
        {
            Intent intent = new Intent(context, type);
            context.startActivity(intent);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityShow:: started [%s] for OrphanElements",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName())));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityTool.startActivityShow:: unable to start activity: unknown type %s", element));
        }
    }

    public static void startActivityEditForResult(Context context, int requestCode, Element element)
    {
        Class type = null;
        String toolbarSubtitle = null;

        if(requestCode == Constants.REQUEST_EDIT_LOCATION)
        {
            if(element.isRootElement())
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
        else if(requestCode == Constants.REQUEST_EDIT_ATTRACTION_CATEGORY)
        {
            type = EditElementActivity.class;
            toolbarSubtitle = context.getString(R.string.subtitle_attraction_category_edit);
        }

        if(type != null)
        {
            Intent intent = new Intent(context, type);
            intent.putExtra(Constants.EXTRA_TOOLBAR_SUBTITLE, toolbarSubtitle);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, element.getUuid().toString());
            ((Activity)context).startActivityForResult(intent, requestCode);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityEditForResult:: started [%s] for %s",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), element));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityTool.startActivityEditForResult:: unable to start activity: unknown request code [%s]"
                    , requestCode));
        }
    }

    public static void startActivityCreateForResult(Context context, int requestCode, Element parentElement)
    {
        Class type = null;

        if(requestCode == Constants.REQUEST_CREATE_LOCATION)
        {
            type = CreateLocationActivity.class;
        }
        else if(requestCode == Constants.REQUEST_CREATE_VISIT)
        {
            type = CreateVisitActivity.class;
        }
        else if(requestCode == Constants.REQUEST_CREATE_ATTRACTION_CATEGORY)
        {
            type = CreateAttractionCategoryActivity.class;
        }

        if(type != null && parentElement != null)
        {
            Intent intent = new Intent(context, type);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, parentElement.getUuid().toString());
            ((Activity)context).startActivityForResult(intent, requestCode);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityCreateForResult:: started [%s] for %s  with request code [%d]",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), parentElement, requestCode));
        }
        else if(type != null)
        {
            Intent intent = new Intent(context, type);
            ((Activity)context).startActivityForResult(intent, requestCode);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityCreateForResult:: started [%s] for OrphanElement with request code [%d]",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), requestCode));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityTool.startActivityCreateForResult:: unable to start activity: unknown request code [%s]"
                    , requestCode));
        }
    }

    public static void startActivitySortForResult(Context context, int requestCode, List<Element> elementsToSort)
    {
        String toolbarTitle = null;

        if(requestCode == Constants.REQUEST_SORT_LOCATIONS)
        {
            toolbarTitle = context.getString(R.string.title_locations_sort);
        }
        else if(requestCode == Constants.REQUEST_SORT_PARKS)
        {
            toolbarTitle = context.getString(R.string.title_parks_sort);
        }
        else if(requestCode == Constants.REQUEST_SORT_ATTRACTIONS)
        {
            toolbarTitle = context.getString(R.string.title_attractions_sort);
        }
        else if(requestCode == Constants.REQUEST_SORT_ATTRACTION_CATEGORIES)
        {
            toolbarTitle = context.getString(R.string.title_attraction_categories_sort);
        }

        if(toolbarTitle != null)
        {
            Intent intent = new Intent(context, SortElementsActivity.class);
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
            intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(elementsToSort));
            ((Activity)context).startActivityForResult(intent, requestCode);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivitySortForResult:: started [%s] for [%d] elements with request code [%d]",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), elementsToSort.size(), requestCode));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityTool.startActivitySortForResult:: unable to start activity: unknown request code [%s]"
                    , requestCode));
        }
    }

    public static void startActivityPickForResult(Context context, int requestCode, List<Element> elementsToPickFrom)
    {
        String toolbarTitle = null;
        String toolbarSubtitle = null;

        if(requestCode == Constants.REQUEST_PICK_LOCATIONS)
        {
            toolbarTitle = context.getString(R.string.title_locations_pick);
            toolbarSubtitle = context.getString(R.string.subtitle_locations_pick_description);
        }
        else if(requestCode == Constants.REQUEST_PICK_PARKS)
        {
            toolbarTitle = context.getString(R.string.title_parks_pick);
            toolbarSubtitle = context.getString(R.string.subtitle_parks_pick_description);
        }
        else if(requestCode == Constants.REQUEST_PICK_ATTRACTIONS)
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

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityPickForResult:: started [%s] for [%d] elements with requestCode [%d]",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), elementsToPickFrom.size(), requestCode));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityTool.startActivityPickForResult:: unable to start activity: unknown request code [%s]"
                    , requestCode));
        }
    }
}
