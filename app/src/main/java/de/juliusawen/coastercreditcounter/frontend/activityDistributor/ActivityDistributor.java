package de.juliusawen.coastercreditcounter.frontend.activityDistributor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.elements.ElementType;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.frontend.attractions.CreateOrEditCustomAttractionActivity;
import de.juliusawen.coastercreditcounter.frontend.elements.CreateSimpleElementActivity;
import de.juliusawen.coastercreditcounter.frontend.elements.EditElementActivity;
import de.juliusawen.coastercreditcounter.frontend.elements.ManageOrphanElementsActivity;
import de.juliusawen.coastercreditcounter.frontend.elements.PickElementsActivity;
import de.juliusawen.coastercreditcounter.frontend.elements.SortElementsActivity;
import de.juliusawen.coastercreditcounter.frontend.locations.CreateLocationActivity;
import de.juliusawen.coastercreditcounter.frontend.locations.ShowLocationsActivity;
import de.juliusawen.coastercreditcounter.frontend.parks.CreateParkActivity;
import de.juliusawen.coastercreditcounter.frontend.parks.ShowParkActivity;
import de.juliusawen.coastercreditcounter.frontend.visits.CreateVisitActivity;
import de.juliusawen.coastercreditcounter.frontend.visits.ShowVisitActivity;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;

public abstract class ActivityDistributor
{
    public static void startActivityShow(Context context, int requestCode, IElement element)
    {
        Class type = null;
        if(requestCode == Constants.REQUEST_CODE_SHOW_LOCATION)
        {
            type = ShowLocationsActivity.class;
        }
        else if(requestCode == Constants.REQUEST_CODE_SHOW_PARK)
        {
            type = ShowParkActivity.class;
        }
        else if(requestCode == Constants.REQUEST_CODE_SHOW_VISIT)
        {
            type = ShowVisitActivity.class;
        }

        if(type != null)
        {
            Intent intent = new Intent(context, type);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, element.getUuid().toString());
            context.startActivity(intent);

            Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivityShow:: started [%s] for %s",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), element));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(),
                    "ActivityDistributor.startActivityShow:: unable to start activity: unknown request code [%d] for type %s", requestCode, element));
        }
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
    }

    public static  void startActivityManageForResult(Context context, int requestCode)
    {
        ElementType elementType = ElementType.NONE;
        String toolbarTitle;
        String helpTitle;
        String helpText;

        if(requestCode == Constants.REQUEST_CODE_MANAGE_ATTRACTION_CATEGORIES)
        {
            elementType = ElementType.ATTRACTION_CATEGORY;
            toolbarTitle = context.getString(R.string.title_attraction_categories);
            helpTitle = context.getString(R.string.title_attraction_categories);
            helpText = context.getString(R.string.help_text_manage_attraction_category);
        }
        else if(requestCode == Constants.REQUEST_CODE_MANAGE_MANUFACTURERS)
        {
            elementType = ElementType.MANUFACTURER;
            toolbarTitle = context.getString(R.string.title_manufacturers);
            helpTitle = context.getString(R.string.title_manufacturers);
            helpText = context.getString(R.string.help_text_manage_manufacturer);
        }
        else if(requestCode == Constants.REQUEST_CODE_MANAGE_STATUSES)
        {
            elementType = ElementType.STATUS;
            toolbarTitle = context.getString(R.string.title_statuses);
            helpTitle = context.getString(R.string.title_statuses);
            helpText = context.getString(R.string.help_text_manage_status);
        }
        else
        {
            toolbarTitle = context.getString(R.string.error_missing_text);
            helpTitle = context.getString(R.string.error_missing_text);
            helpText = context.getString(R.string.error_missing_text);
        }

        if(!elementType.equals(ElementType.NONE))
        {
            Intent intent = new Intent(context, ManageOrphanElementsActivity.class);
            intent.putExtra(Constants.EXTRA_TYPE_TO_MANAGE, elementType.ordinal());
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
            intent.putExtra(Constants.EXTRA_HELP_TITLE, helpTitle);
            intent.putExtra(Constants.EXTRA_HELP_TEXT, helpText);

            ((Activity)context).startActivityForResult(intent, requestCode);

            Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivityManageForResult:: started [%s]",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName())));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityDistributor.startActivityManageForResult:: unable to start activity: unknown request code [%d]", requestCode));
        }
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
    }

    public static void startActivityEditForResult(Context context, int requestCode, IElement element)
    {
        Class type = null;
        String toolbarTitle = context.getString(R.string.error_missing_text);
        if(requestCode == Constants.REQUEST_CODE_EDIT_LOCATION)
        {
            type = EditElementActivity.class;
            toolbarTitle = context.getString(R.string.title_location_edit);
        }
        else if(requestCode == Constants.REQUEST_CODE_EDIT_PARK)
        {
            type = EditElementActivity.class;
            toolbarTitle = context.getString(R.string.title_park_edit);
        }
        else if(requestCode == Constants.REQUEST_CODE_EDIT_ATTRACTION_CATEGORY)
        {
            type = EditElementActivity.class;
            toolbarTitle = context.getString(R.string.title_attraction_category_edit);
        }
        else if(requestCode == Constants.REQUEST_CODE_EDIT_MANUFACTURER)
        {
            type = EditElementActivity.class;
            toolbarTitle = context.getString(R.string.title_manufacturer_edit);
        }
        else if(requestCode == Constants.REQUEST_CODE_EDIT_STATUS)
        {
            type = EditElementActivity.class;
            toolbarTitle = context.getString(R.string.title_status_edit);
        }
        else if(requestCode == Constants.REQUEST_CODE_EDIT_CUSTOM_ATTRACTION)
        {
            type = CreateOrEditCustomAttractionActivity.class;
            toolbarTitle = context.getString(R.string.title_custom_attraction_edit);
        }

        if(type != null)
        {
            Intent intent = new Intent(context, type);
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, element.getUuid().toString());
            intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode);
            ((Activity)context).startActivityForResult(intent, requestCode);

            Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivityEditForResult:: started [%s] for %s",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), element));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(),
                    "ActivityDistributor.startActivityEditForResult:: unable to start activity: unknown request code [%d] for type [%s]", requestCode, element));
        }
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
    }

    public static void startActivityCreateForResult(Context context, int requestCode, IElement parentElement)
    {
        Class type;
        if(requestCode == Constants.REQUEST_CODE_CREATE_LOCATION)
        {
            type = CreateLocationActivity.class;
        }
        else if(requestCode == Constants.REQUEST_CODE_CREATE_PARK)
        {
            type = CreateParkActivity.class;
        }
        else if(requestCode == Constants.REQUEST_CODE_CREATE_VISIT)
        {
            type = CreateVisitActivity.class;
        }
        else if(requestCode == Constants.REQUEST_CODE_CREATE_CUSTOM_ATTRACTION)
        {
            type = CreateOrEditCustomAttractionActivity.class;
        }
        else
        {
            type = CreateSimpleElementActivity.class;
        }

        Intent intent = new Intent(context, type);

        if(parentElement != null)
        {
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, parentElement.getUuid().toString());
            ((Activity)context).startActivityForResult(intent, requestCode);

            Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivityCreateForResult:: started [%s] for %s  with request code [%d]",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), parentElement, requestCode));
        }
        else
        {
            if(requestCode == Constants.REQUEST_CODE_CREATE_ATTRACTION_CATEGORY)
            {
                intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, context.getString(R.string.title_attraction_category_create));
                intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_attraction_category_create));
                intent.putExtra(Constants.EXTRA_HELP_TEXT, context.getString(R.string.help_text_create_attraction_category));
                intent.putExtra(Constants.EXTRA_HINT, context.getString(R.string.hint_enter_attraction_category_name));
            }
            else if(requestCode == Constants.REQUEST_CODE_CREATE_MANUFACTURER)
            {
                intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, context.getString(R.string.title_manufacturer_create));
                intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_manufacturer_create));
                intent.putExtra(Constants.EXTRA_HELP_TEXT, context.getString(R.string.help_text_create_manufacturer));
                intent.putExtra(Constants.EXTRA_HINT, context.getString(R.string.hint_enter_manufacturer_name));
            }
            else if(requestCode == Constants.REQUEST_CODE_CREATE_STATUS)
            {
                intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, context.getString(R.string.title_status_create));
                intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_status_create));
                intent.putExtra(Constants.EXTRA_HELP_TEXT, context.getString(R.string.help_text_create_status));
                intent.putExtra(Constants.EXTRA_HINT, context.getString(R.string.hint_enter_status_name));
            }

            intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode);

            ((Activity)context).startActivityForResult(intent, requestCode);

            Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivityCreateForResult:: started [%s] for OrphanElement with request code [%d]",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), requestCode));
        }

        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
    }

    public static void startActivitySortForResult(Context context, int requestCode, List<IElement> elementsToSort)
    {
        String toolbarTitle;
        if(requestCode == Constants.REQUEST_CODE_SORT_LOCATIONS)
        {
            toolbarTitle = context.getString(R.string.title_locations_sort);
        }
        else if(requestCode == Constants.REQUEST_CODE_SORT_PARKS)
        {
            toolbarTitle = context.getString(R.string.title_parks_sort);
        }
        else if(requestCode == Constants.REQUEST_CODE_SORT_ATTRACTIONS)
        {
            toolbarTitle = context.getString(R.string.title_attractions_sort);
        }
        else if(requestCode == Constants.REQUEST_CODE_SORT_ATTRACTION_CATEGORIES)
        {
            toolbarTitle = context.getString(R.string.title_attraction_categories_sort);
        }
        else if(requestCode == Constants.REQUEST_CODE_SORT_MANUFACTURERS)
        {
            toolbarTitle = context.getString(R.string.title_manufacturers_sort);
        }
        else if(requestCode == Constants.REQUEST_CODE_SORT_STATUSES)
        {
            toolbarTitle = context.getString(R.string.title_statuses_sort);
        }
        else
        {
            toolbarTitle = context.getString(R.string.error_missing_text);
        }

        if(!toolbarTitle.equals(context.getString(R.string.error_missing_text)))
        {
            Intent intent = new Intent(context, SortElementsActivity.class);
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
            intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(elementsToSort));
            ((Activity)context).startActivityForResult(intent, requestCode);

            Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivitySortForResult:: started [%s] for [%d] elements with request code [%d]",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), elementsToSort.size(), requestCode));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityDistributor.startActivitySortForResult:: unable to start activity: unknown request code [%s]", requestCode));
        }
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
    }

    public static void startActivityPickForResult(Context context, int requestCode, List<IElement> elementsToPickFrom)
    {
        String toolbarTitle = null;
        String toolbarSubtitle = null;

        Intent intent = new Intent(context, PickElementsActivity.class);

        if(requestCode == Constants.REQUEST_CODE_PICK_LOCATIONS)
        {
            toolbarTitle = context.getString(R.string.title_locations_pick);
            toolbarSubtitle = context.getString(R.string.subtitle_locations_pick_description_to_add_to_new_location);
        }
        else if(requestCode == Constants.REQUEST_CODE_PICK_PARKS)
        {
            toolbarTitle = context.getString(R.string.title_parks_pick);
            toolbarSubtitle = context.getString(R.string.subtitle_parks_pick_description_to_add_to_new_location);
        }
        else if(requestCode == Constants.REQUEST_CODE_PICK_ATTRACTIONS)
        {
            toolbarTitle = context.getString(R.string.title_attractions_pick);
            toolbarSubtitle = context.getString(R.string.subtitle_attractions_description_pick_to_add_to_visit);
        }
        else if(requestCode == Constants.REQUEST_CODE_ASSIGN_MANUFACTURERS_TO_ATTRACTIONS)
        {
            toolbarTitle = context.getString(R.string.title_attractions_pick);
            toolbarSubtitle = context.getString(R.string.subtitle_manufacturer_to_assign_manufacturer_to);
        }
        else if(requestCode == Constants.REQUEST_CODE_ASSIGN_CATEGORY_TO_ATTRACTIONS)
        {
            toolbarTitle = context.getString(R.string.title_attractions_pick);
            toolbarSubtitle = context.getString(R.string.subtitle_attraction_category_to_assign_category_to);
        }
        else if(requestCode == Constants.REQUEST_CODE_ASSIGN_STATUS_TO_ATTRACTIONS)
        {
            toolbarTitle = context.getString(R.string.title_status_pick);
            toolbarSubtitle = context.getString(R.string.subtitle_status_to_assign_status_to);
        }
        else if(requestCode == Constants.REQUEST_CODE_PICK_MANUFACTURER)
        {
            toolbarTitle = context.getString(R.string.title_manufacturer_pick);
            toolbarSubtitle = context.getString(R.string.subtitle_manufacturer_to_assign_to_attraction);

            intent.putExtra(Constants.EXTRA_SIMPLE_PICK, true);
        }
        else if(requestCode == Constants.REQUEST_CODE_PICK_ATTRACTION_CATEGORY)
        {
            toolbarTitle = context.getString(R.string.title_attraction_category_pick);
            toolbarSubtitle = context.getString(R.string.subtitle_attraction_category_to_assign_to_attraction);

            intent.putExtra(Constants.EXTRA_SIMPLE_PICK, true);
        }
        else if(requestCode == Constants.REQUEST_CODE_PICK_STATUS)
        {
            toolbarTitle = context.getString(R.string.title_status_pick);
            toolbarSubtitle = context.getString(R.string.subtitle_status_to_assign_to_attraction);

            intent.putExtra(Constants.EXTRA_SIMPLE_PICK, true);
        }
        else if(requestCode == Constants.REQUEST_CODE_PICK_VISIT)
        {
            toolbarTitle = context.getString(R.string.title_visit_pick);
            toolbarSubtitle = context.getString(R.string.subtitle_visit_pick_to_open);

            intent.putExtra(Constants.EXTRA_SIMPLE_PICK, true);
        }


        if(toolbarTitle != null)
        {
            intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode);
            intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(elementsToPickFrom));
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
            intent.putExtra(Constants.EXTRA_TOOLBAR_SUBTITLE, toolbarSubtitle);
            ((Activity)context).startActivityForResult(intent, requestCode);

            Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivityPickForResult:: started [%s] for [%d] elements with requestCode [%d]",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName()), elementsToPickFrom.size(), requestCode));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityDistributor.startActivityPickForResult:: unable to start activity: unknown request code [%s]", requestCode));
        }
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
    }

    public static void startActivityViaIntent(Context context, Intent intent)
    {
        Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivityViaIntent:: starting [%s] via given intent...",
                StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName())));

        context.startActivity(intent);
    }

    public static void startActivityViaClass(Context context, Class type)
    {
        Intent intent = new Intent(context, type);

        Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivityViaClass:: starting [%s] via given class type...",
                StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName())));

        context.startActivity(intent);
    }
}
