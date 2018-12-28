package de.juliusawen.coastercreditcounter.toolbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Location;
import de.juliusawen.coastercreditcounter.frontend.CreateSimpleStringActivity;
import de.juliusawen.coastercreditcounter.frontend.attractionCategories.ManageAttractionCategoriesActivity;
import de.juliusawen.coastercreditcounter.frontend.elements.EditElementActivity;
import de.juliusawen.coastercreditcounter.frontend.elements.PickElementsActivity;
import de.juliusawen.coastercreditcounter.frontend.elements.SortElementsActivity;
import de.juliusawen.coastercreditcounter.frontend.locations.CreateLocationActivity;
import de.juliusawen.coastercreditcounter.frontend.locations.ShowLocationsActivity;
import de.juliusawen.coastercreditcounter.frontend.parks.ShowParkActivity;
import de.juliusawen.coastercreditcounter.frontend.visits.CreateVisitActivity;
import de.juliusawen.coastercreditcounter.frontend.visits.ShowVisitActivity;
import de.juliusawen.coastercreditcounter.globals.Constants;

public abstract class ActivityTool
{
    public static void startActivityShow(Context context, int requestCode, IElement element)
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

        if(type != null)
        {
            Intent intent = new Intent(context, type);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, element.getUuid().toString());
            context.startActivity(intent);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityShow:: started [%s] for %s",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), element));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(),
                    "ActivityTool.startActivityShow:: unable to start activity: unknown request code [%d] for type %s", requestCode, element));
        }
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
    }

    public static void startActivityManage(Context context, int requestCode)
    {
        Class type = null;

        if(requestCode == Constants.REQUEST_MANAGE_ATTRACTION_CATEGORIES)
        {
            type = ManageAttractionCategoriesActivity.class;
        }

        if(type != null)
        {
            Intent intent = new Intent(context, type);
            context.startActivity(intent);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityManage:: started [%s]",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName())));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityTool.startActivityManage:: unable to start activity: unknown request code [%d]", requestCode));
        }
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
    }

    public static void startActivityEditForResult(Context context, int requestCode, IElement element)
    {
        Class type = null;
        String toolbarTitle = null;

        if(requestCode == Constants.REQUEST_EDIT_LOCATION)
        {
            if(((Location)element).isRootLocation())
            {
                type = EditElementActivity.class;
                toolbarTitle = context.getString(R.string.title_root_location_edit);
            }
            else
            {
                type = EditElementActivity.class;
                toolbarTitle = context.getString(R.string.title_location_edit);
            }
        }
        else if(requestCode == Constants.REQUEST_EDIT_ATTRACTION_CATEGORY)
        {
            type = EditElementActivity.class;
            toolbarTitle = context.getString(R.string.title_attraction_category_edit);
        }

        if(type != null)
        {
            Intent intent = new Intent(context, type);
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, element.getUuid().toString());
            ((Activity)context).startActivityForResult(intent, requestCode);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityEditForResult:: started [%s] for %s",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), element));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(),
                    "ActivityTool.startActivityEditForResult:: unable to start activity: unknown request code [%d] for type [%s]", requestCode, element));
        }
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
    }

    public static void startActivityCreateForResult(Context context, int requestCode, IElement parentElement)
    {
        Class type;

        if(requestCode == Constants.REQUEST_CREATE_LOCATION)
        {
            type = CreateLocationActivity.class;
        }
        else if(requestCode == Constants.REQUEST_CREATE_VISIT)
        {
            type = CreateVisitActivity.class;
        }
        else
        {
            type = CreateSimpleStringActivity.class;
        }

        if(parentElement != null)
        {
            Intent intent = new Intent(context, type);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, parentElement.getUuid().toString());
            ((Activity)context).startActivityForResult(intent, requestCode);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityCreateForResult:: started [%s] for %s  with request code [%d]",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), parentElement, requestCode));
        }
        else
        {
            Intent intent = new Intent(context, type);

            if(requestCode == Constants.REQUEST_CREATE_ATTRACTION_CATEGORY)
            {
                intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_attraction_category_create));
                intent.putExtra(Constants.EXTRA_HELP_TEXT, context.getString(R.string.help_text_create_attraction_category));
            }

            ((Activity)context).startActivityForResult(intent, requestCode);

            Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivityCreateForResult:: started [%s] for OrphanElement with request code [%d]",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), requestCode));
        }

        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
    }

    public static void startActivitySortForResult(Context context, int requestCode, List<IElement> elementsToSort)
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
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
    }

    public static void startActivityPickForResult(Context context, int requestCode, List<IElement> elementsToPickFrom)
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
        else if(requestCode == Constants.REQUEST_APPLY_CATEGORY_TO_ATTRACTIONS)
        {
            toolbarTitle = context.getString(R.string.title_attractions_pick);
            toolbarSubtitle = context.getString(R.string.subtitle_attraction_category_apply_to_attractions);
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
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
    }

    public static void startActivity(Context context, Class type)
    {
        Intent intent = new Intent(context, type);

        Log.i(Constants.LOG_TAG, String.format("ActivityTool.startActivity:: starting [%s]...",
                StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName())));

        context.startActivity(intent);
    }
}
