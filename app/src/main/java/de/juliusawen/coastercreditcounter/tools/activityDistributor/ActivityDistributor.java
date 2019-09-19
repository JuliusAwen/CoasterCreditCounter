package de.juliusawen.coastercreditcounter.tools.activityDistributor;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.OrphanElementType;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.userInterface.activities.CreateLocationActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.CreateOrEditCustomAttractionActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.CreateParkActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.CreateSimpleElementActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.CreateVisitActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.EditElementActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.ManageOrphanElementsActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.NavigationHubActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.PickElementsActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.ShowLocationsActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.ShowParkActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.ShowVisitActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.SortElementsActivity;

public abstract class ActivityDistributor
{
    public static void startActivityShow(Context context, RequestCode requestCode, IElement element)
    {
        Class type = null;

        switch(requestCode)
        {
            case SHOW_LOCATION:
                type = ShowLocationsActivity.class;
                break;

            case SHOW_PARK:
                type = ShowParkActivity.class;
                break;

            case SHOW_VISIT:
                type = ShowVisitActivity.class;
                break;
        }

        if(type != null)
        {
            Intent intent = new Intent(context, type);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, element.getUuid().toString());
            context.startActivity(intent);

            Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivityShow:: started [%s] for %s from [%s]",
                    StringTool.parseActivityName(intent.getComponent().getShortClassName()), element, context.getClass().getSimpleName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(),"ActivityDistributor.startActivityShow:: unable to start activity: unknown RequestCode [%s] for type %s", requestCode, element));
        }
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + context.getClass().getSimpleName());
    }

    public static  void startActivityManageForResult(Context context, RequestCode requestCode)
    {
        OrphanElementType orphanElementType = OrphanElementType.NONE;
        String toolbarTitle;
        String helpTitle;
        String helpText;

        switch(requestCode)
        {
            case MANAGE_CREDIT_TYPES:
                orphanElementType = OrphanElementType.CREDIT_TYPE;
                toolbarTitle = context.getString(R.string.title_credit_types);
                helpTitle = context.getString(R.string.title_credit_types);
                helpText = context.getString(R.string.help_text_manage_credit_types);
                break;

            case MANAGE_CATEGORIES:
                orphanElementType = OrphanElementType.CATEGORY;
                toolbarTitle = context.getString(R.string.title_categories);
                helpTitle = context.getString(R.string.title_categories);
                helpText = context.getString(R.string.help_text_manage_categories);
                break;

            case MANAGE_MANUFACTURERS:
                orphanElementType = OrphanElementType.MANUFACTURER;
                toolbarTitle = context.getString(R.string.title_manufacturers);
                helpTitle = context.getString(R.string.title_manufacturers);
                helpText = context.getString(R.string.help_text_manage_manufacturers);
                break;

            case MANAGE_STATUSES:
                orphanElementType = OrphanElementType.STATUS;
                toolbarTitle = context.getString(R.string.title_statuses);
                helpTitle = context.getString(R.string.title_statuses);
                helpText = context.getString(R.string.help_text_manage_statuses);
                break;

            default:
                toolbarTitle = context.getString(R.string.error_missing_text);
                helpTitle = context.getString(R.string.error_missing_text);
                helpText = context.getString(R.string.error_missing_text);
                break;
        }

        if(!orphanElementType.equals(OrphanElementType.NONE))
        {
            Intent intent = new Intent(context, ManageOrphanElementsActivity.class);
            intent.putExtra(Constants.EXTRA_TYPE_TO_MANAGE, orphanElementType.ordinal());
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
            intent.putExtra(Constants.EXTRA_HELP_TITLE, helpTitle);
            intent.putExtra(Constants.EXTRA_HELP_TEXT, helpText);

            ((Activity)context).startActivityForResult(intent, requestCode.ordinal());

            Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivityManageForResult:: started [%s] from [%s]",
                    StringTool.parseActivityName(intent.getComponent().getShortClassName()), context.getClass().getSimpleName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityDistributor.startActivityManageForResult:: unable to start activity: unknown RequestCode [%s]", requestCode));
        }
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + context.getClass().getSimpleName());
    }

    public static void startActivityEditForResult(Context context, RequestCode requestCode, IElement element)
    {
        Class type = null;
        String toolbarTitle = context.getString(R.string.error_missing_text);

        switch(requestCode)
        {
            case EDIT_LOCATION:
                type = EditElementActivity.class;
                toolbarTitle = context.getString(R.string.title_location_edit);
                break;

            case EDIT_PARK:
                type = EditElementActivity.class;
                toolbarTitle = context.getString(R.string.title_park_edit);
                break;

            case EDIT_CUSTOM_ATTRACTION:
                type = CreateOrEditCustomAttractionActivity.class;
                toolbarTitle = context.getString(R.string.title_custom_attraction_edit);
                break;

            case EDIT_CREDIT_TYPE:
                type = EditElementActivity.class;
                toolbarTitle = context.getString(R.string.title_credit_type_edit);
                break;

            case EDIT_CATEGORY:
                type = EditElementActivity.class;
                toolbarTitle = context.getString(R.string.title_category_edit);
                break;

            case EDIT_MANUFACTURER:
                type = EditElementActivity.class;
                toolbarTitle = context.getString(R.string.title_manufacturer_edit);
                break;

            case EDIT_STATUS:
                type = EditElementActivity.class;
                toolbarTitle = context.getString(R.string.title_status_edit);
                break;
        }

        if(type != null)
        {
            Intent intent = new Intent(context, type);
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, element.getUuid().toString());
            intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode.ordinal());
            ((Activity)context).startActivityForResult(intent, requestCode.ordinal());

            Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivityEditForResult:: started [%s] for %s from [%s]",
                    StringTool.parseActivityName(intent.getComponent().getShortClassName()), element, context.getClass().getSimpleName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityDistributor.startActivityEditForResult:: unable to start activity: unknown RequestCode [%s] for type [%s] from [%s]",
                    requestCode, element, context.getClass().getSimpleName()));
        }
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + context.getClass().getSimpleName());
    }

    public static void startActivityCreateForResult(Context context, RequestCode requestCode, IElement parentElement)
    {
        Class type;

        switch(requestCode)
        {
            case CREATE_LOCATION:
                type = CreateLocationActivity.class;
                break;

            case CREATE_PARK:
                type = CreateParkActivity.class;
                break;

            case CREATE_VISIT:
                type = CreateVisitActivity.class;
                break;

            case CREATE_CUSTOM_ATTRACTION:
                type = CreateOrEditCustomAttractionActivity.class;
                break;

            default:
                type = CreateSimpleElementActivity.class;
                break;
        }

        Intent intent = new Intent(context, type);

        if(parentElement != null)
        {
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, parentElement.getUuid().toString());
            ((Activity)context).startActivityForResult(intent, requestCode.ordinal());

            Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivityCreateForResult:: started [%s] for %s  with RequestCode [%s] from [%s]",
                    StringTool.parseActivityName(intent.getComponent().getShortClassName()), parentElement, requestCode, context.getClass().getSimpleName()));
        }
        else
        {
            switch(requestCode)
            {
                case CREATE_CREDIT_TYPE:
                    intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, context.getString(R.string.title_credit_type_create));
                    intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_credit_type_create));
                    intent.putExtra(Constants.EXTRA_HELP_TEXT, context.getString(R.string.help_text_create_credit_type));
                    intent.putExtra(Constants.EXTRA_HINT, context.getString(R.string.hint_enter_credit_type_name));
                    break;

                case CREATE_CATEGORY:
                    intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, context.getString(R.string.title_category_create));
                    intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_category_create));
                    intent.putExtra(Constants.EXTRA_HELP_TEXT, context.getString(R.string.help_text_create_category));
                    intent.putExtra(Constants.EXTRA_HINT, context.getString(R.string.hint_enter_category_name));
                    break;

                case CREATE_MANUFACTURER:
                    intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, context.getString(R.string.title_manufacturer_create));
                    intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_manufacturer_create));
                    intent.putExtra(Constants.EXTRA_HELP_TEXT, context.getString(R.string.help_text_create_manufacturer));
                    intent.putExtra(Constants.EXTRA_HINT, context.getString(R.string.hint_enter_manufacturer_name));
                    break;

                case CREATE_STATUS:
                    intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, context.getString(R.string.title_status_create));
                    intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_status_create));
                    intent.putExtra(Constants.EXTRA_HELP_TEXT, context.getString(R.string.help_text_create_status));
                    intent.putExtra(Constants.EXTRA_HINT, context.getString(R.string.hint_enter_status_name));
                    break;
            }

            intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode.ordinal());

            ((Activity)context).startActivityForResult(intent, requestCode.ordinal());

            Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivityCreateForResult:: started [%s] for OrphanElement with RequestCode [%s] from [%s]",
                    StringTool.parseActivityName(intent.getComponent().getShortClassName()), requestCode, context.getClass().getSimpleName()));
        }

        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + context.getClass().getSimpleName());
    }

    public static void startActivitySortForResult(Context context, RequestCode requestCode, List<IElement> elementsToSort)
    {
        String toolbarTitle;

        switch(requestCode)
        {
            case SORT_LOCATIONS:
                toolbarTitle = context.getString(R.string.title_locations_sort);
                break;

            case SORT_PARKS:
                toolbarTitle = context.getString(R.string.title_parks_sort);
                break;

            case SORT_ATTRACTIONS:
                toolbarTitle = context.getString(R.string.title_attractions_sort);
                break;

            case SORT_CREDIT_TYPES:
                toolbarTitle = context.getString(R.string.title_credit_type_sort);
                break;

            case SORT_CATEGORIES:
                toolbarTitle = context.getString(R.string.title_categories_sort);
                break;

            case SORT_MANUFACTURERS:
                toolbarTitle = context.getString(R.string.title_manufacturers_sort);
                break;

            case SORT_STATUSES:
                toolbarTitle = context.getString(R.string.title_statuses_sort);
                break;

                default:
                    toolbarTitle = context.getString(R.string.error_missing_text);

        }

        if(!toolbarTitle.equals(context.getString(R.string.error_missing_text)))
        {
            Intent intent = new Intent(context, SortElementsActivity.class);
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
            intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(elementsToSort));
            ((Activity)context).startActivityForResult(intent, requestCode.ordinal());

            Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivitySortForResult:: started [%s] for [%d] elements with RequestCode [%s] from [%s]",
                    StringTool.parseActivityName(intent.getComponent().getShortClassName()), elementsToSort.size(), requestCode, context.getClass().getSimpleName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityDistributor.startActivitySortForResult:: unable to start activity: unknown request code [%s]", requestCode));
        }
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + context.getClass().getSimpleName());
    }

    public static void startActivityPickForResult(Context context, RequestCode requestCode, List<IElement> elementsToPickFrom)
    {
        String toolbarTitle = null;
        String toolbarSubtitle = null;

        Intent intent = new Intent(context, PickElementsActivity.class);

        switch(requestCode)
        {
            case PICK_LOCATIONS:
                toolbarTitle = context.getString(R.string.title_locations_pick);
                toolbarSubtitle = context.getString(R.string.subtitle_locations_pick_description_to_add_to_new_location);
                break;

            case PICK_PARKS:
                toolbarTitle = context.getString(R.string.title_parks_pick);
                toolbarSubtitle = context.getString(R.string.subtitle_parks_pick_description_to_add_to_new_location);
                break;

            case PICK_VISIT:
                toolbarTitle = context.getString(R.string.title_visit_pick);
                toolbarSubtitle = context.getString(R.string.subtitle_visit_pick_to_open);
                intent.putExtra(Constants.EXTRA_SIMPLE_PICK, true);
                break;

            case PICK_ATTRACTIONS:
                toolbarTitle = context.getString(R.string.title_attractions_pick);
                toolbarSubtitle = context.getString(R.string.subtitle_attractions_description_pick_to_add_to_visit);
                break;

            case PICK_CREDIT_TYPE:
                toolbarTitle = context.getString(R.string.title_credit_type_pick);
                toolbarSubtitle = context.getString(R.string.subtitle_credit_type_to_assign_to_attraction);
                intent.putExtra(Constants.EXTRA_SIMPLE_PICK, true);
                break;

            case PICK_CATEGORY:
                toolbarTitle = context.getString(R.string.title_category_pick);
                toolbarSubtitle = context.getString(R.string.subtitle_category_to_assign_to_attraction);
                intent.putExtra(Constants.EXTRA_SIMPLE_PICK, true);
                break;

            case PICK_MANUFACTURER:
                toolbarTitle = context.getString(R.string.title_manufacturer_pick);
                toolbarSubtitle = context.getString(R.string.subtitle_manufacturer_to_assign_to_attraction);
                intent.putExtra(Constants.EXTRA_SIMPLE_PICK, true);
                break;

            case PICK_STATUS:
                toolbarTitle = context.getString(R.string.title_status_pick);
                toolbarSubtitle = context.getString(R.string.subtitle_status_to_assign_to_attraction);
                intent.putExtra(Constants.EXTRA_SIMPLE_PICK, true);
                break;

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
                toolbarTitle = context.getString(R.string.title_credit_type_pick);
                toolbarSubtitle = context.getString(R.string.subtitle_credit_type_to_assign_credit_type_to);
                break;

            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
                toolbarTitle = context.getString(R.string.title_attractions_pick);
                toolbarSubtitle = context.getString(R.string.subtitle_category_to_assign_category_to);
                break;

            case ASSIGN_MANUFACTURERS_TO_ATTRACTIONS:
                toolbarTitle = context.getString(R.string.title_attractions_pick);
                toolbarSubtitle = context.getString(R.string.subtitle_manufacturer_to_assign_manufacturer_to);
                break;

            case ASSIGN_STATUS_TO_ATTRACTIONS:
                toolbarTitle = context.getString(R.string.title_status_pick);
                toolbarSubtitle = context.getString(R.string.subtitle_status_to_assign_status_to);
                break;
        }

        if(toolbarTitle != null)
        {
            intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode.ordinal());
            intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(elementsToPickFrom));
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
            intent.putExtra(Constants.EXTRA_TOOLBAR_SUBTITLE, toolbarSubtitle);
            ((Activity)context).startActivityForResult(intent, requestCode.ordinal());

            Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivityPickForResult:: started [%s] for [%d] elements with RequestCode [%s] from [%s]",
                    StringTool.parseActivityName(intent.getComponent().getShortClassName()), elementsToPickFrom.size(), requestCode, context.getClass().getSimpleName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format(Locale.getDefault(), "ActivityDistributor.startActivityPickForResult:: unable to start activity: unknown request code [%s]", requestCode));
        }
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + context.getClass().getSimpleName());
    }

    public static void startActivityViaIntent(Context context, Intent intent)
    {
        Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivityViaIntent:: starting [%s] from [%s] via given intent...",
                StringTool.parseActivityName(intent.getComponent().getShortClassName()), context.getClass().getSimpleName()));

        context.startActivity(intent);
    }

    public static void startActivityViaClass(Context context, Class type)
    {
        Intent intent = new Intent(context, type);

        Log.i(Constants.LOG_TAG, String.format("ActivityDistributor.startActivityViaClass:: starting [%s] from [%s] via given class type...",
                StringTool.parseActivityName(intent.getComponent().getShortClassName()), context.getClass().getSimpleName()));

        context.startActivity(intent);
    }

    public static void goToCurrentVisit(Context context, Visit currentVisit)
    {
        Intent navigationHubActivity = new Intent(context, NavigationHubActivity.class);

        Intent showLocationIntent = new Intent(context, ShowLocationsActivity.class);

        Intent showParkIntent = new Intent(context, ShowParkActivity.class);
        showParkIntent.putExtra(Constants.EXTRA_ELEMENT_UUID, currentVisit.getParent().getUuid().toString());

        Intent showVisitIntent = new Intent(context, ShowVisitActivity.class);
        showVisitIntent.putExtra(Constants.EXTRA_ELEMENT_UUID, currentVisit.getUuid().toString());

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntent(navigationHubActivity);
        taskStackBuilder.addNextIntent(showLocationIntent);
        taskStackBuilder.addNextIntent(showParkIntent);
        taskStackBuilder.addNextIntent(showVisitIntent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        try
        {
            pendingIntent.send();
        }
        catch(PendingIntent.CanceledException e)
        {
            e.printStackTrace();
        }
    }
}
