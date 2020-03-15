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
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.PropertyType;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.userInterface.activities.CreateLocationActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.CreateOrEditAttractionActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.CreateParkActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.CreateSimpleElementActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.CreateVisitActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.EditElementActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.ManagePropertiesActivity;
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
        ActivityDistributor.startActivityShow(context, requestCode, element, false);
    }

    public static void startActivityShow(Context context, RequestCode requestCode, IElement element, boolean flagActivityClearTop)
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
            if(flagActivityClearTop)
            {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
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
        PropertyType propertyType = null;
        String toolbarTitle;
        String helpTitle;
        String helpText;

        switch(requestCode)
        {
            case MANAGE_CREDIT_TYPES:
                propertyType = PropertyType.CREDIT_TYPE;
                toolbarTitle = context.getString(R.string.credit_type);
                helpTitle = context.getString(R.string.credit_type);
                helpText = context.getString(R.string.help_text_manage_credit_types);
                break;

            case MANAGE_CATEGORIES:
                propertyType = PropertyType.CATEGORY;
                toolbarTitle = context.getString(R.string.category);
                helpTitle = context.getString(R.string.category);
                helpText = context.getString(R.string.help_text_manage_categories);
                break;

            case MANAGE_MANUFACTURERS:
                propertyType = PropertyType.MANUFACTURER;
                toolbarTitle = context.getString(R.string.manufacturer);
                helpTitle = context.getString(R.string.manufacturer);
                helpText = context.getString(R.string.help_text_manage_manufacturers);
                break;

            case MANAGE_STATUSES:
                propertyType = PropertyType.STATUS;
                toolbarTitle = context.getString(R.string.status);
                helpTitle = context.getString(R.string.status);
                helpText = context.getString(R.string.help_text_manage_statuses);
                break;

            default:
                toolbarTitle = context.getString(R.string.error_missing_text);
                helpTitle = context.getString(R.string.error_missing_text);
                helpText = context.getString(R.string.error_missing_text);
                break;
        }

        if(propertyType != null)
        {
            Intent intent = new Intent(context, ManagePropertiesActivity.class);
            intent.putExtra(Constants.EXTRA_TYPE_TO_MANAGE, propertyType.ordinal());
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
                toolbarTitle = context.getString(R.string.title_edit_location);
                break;

            case EDIT_PARK:
                type = EditElementActivity.class;
                toolbarTitle = context.getString(R.string.title_edit_park);
                break;

            case EDIT_ATTRACTION:
                type = CreateOrEditAttractionActivity.class;
                toolbarTitle = context.getString(R.string.title_edit_attraction);
                break;

            case EDIT_CREDIT_TYPE:
                type = EditElementActivity.class;
                toolbarTitle = context.getString(R.string.title_edit_credit_type);
                break;

            case EDIT_CATEGORY:
                type = EditElementActivity.class;
                toolbarTitle = context.getString(R.string.title_edit_category);
                break;

            case EDIT_MANUFACTURER:
                type = EditElementActivity.class;
                toolbarTitle = context.getString(R.string.title_edit_manufacturer);
                break;

            case EDIT_STATUS:
                type = EditElementActivity.class;
                toolbarTitle = context.getString(R.string.title_edit_status);
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

            case CREATE_ATTRACTION:
                type = CreateOrEditAttractionActivity.class;
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
                    intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, context.getString(R.string.title_create_credit_type));
                    intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_create_credit_type));
                    intent.putExtra(Constants.EXTRA_HELP_TEXT, context.getString(R.string.help_text_create_credit_type));
                    intent.putExtra(Constants.EXTRA_HINT, context.getString(R.string.hint_enter_credit_type_name));
                    break;

                case CREATE_CATEGORY:
                    intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, context.getString(R.string.title_create_category));
                    intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_create_category));
                    intent.putExtra(Constants.EXTRA_HELP_TEXT, context.getString(R.string.help_text_create_category));
                    intent.putExtra(Constants.EXTRA_HINT, context.getString(R.string.hint_enter_category_name));
                    break;

                case CREATE_MANUFACTURER:
                    intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, context.getString(R.string.title_create_manufacturer));
                    intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_create_manufacturer));
                    intent.putExtra(Constants.EXTRA_HELP_TEXT, context.getString(R.string.help_text_create_manufacturer));
                    intent.putExtra(Constants.EXTRA_HINT, context.getString(R.string.hint_enter_manufacturer_name));
                    break;

                case CREATE_STATUS:
                    intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, context.getString(R.string.title_create_status));
                    intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_create_status));
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
        String toolbarSubtitle;

        switch(requestCode)
        {
            case SORT_LOCATIONS:
                toolbarSubtitle = context.getString(R.string.locations);
                break;

            case SORT_PARKS:
                toolbarSubtitle = context.getString(R.string.parks);
                break;

            case SORT_ATTRACTIONS:
                toolbarSubtitle = context.getString(R.string.attractions);
                break;

            case SORT_CREDIT_TYPES:
                toolbarSubtitle = context.getString(R.string.credit_types);
                break;

            case SORT_CATEGORIES:
                toolbarSubtitle = context.getString(R.string.categories);
                break;

            case SORT_MANUFACTURERS:
                toolbarSubtitle = context.getString(R.string.manufacturers);
                break;

            case SORT_STATUSES:
                toolbarSubtitle = context.getString(R.string.statuses);
                break;

                default:
                    toolbarSubtitle = context.getString(R.string.error_missing_text);

        }

        if(!toolbarSubtitle.equals(context.getString(R.string.error_missing_text)))
        {
            Intent intent = new Intent(context, SortElementsActivity.class);
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, context.getString(R.string.title_sort));
            intent.putExtra(Constants.EXTRA_TOOLBAR_SUBTITLE, toolbarSubtitle);
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
            case PICK_VISIT:
                toolbarTitle = context.getString(R.string.title_pick_visit);
                toolbarSubtitle = context.getString(R.string.subtitle_to_open);
                intent.putExtra(Constants.EXTRA_SIMPLE_PICK, true);
                break;

            case PICK_ATTRACTIONS:
                toolbarTitle = context.getString(R.string.title_pick_attractions);
                toolbarSubtitle = context.getString(R.string.subtitle_attractions_to_add_to_visit);
                break;

            case PICK_CREDIT_TYPE:
            case PICK_CATEGORY:
            case PICK_MANUFACTURER:
            case PICK_STATUS:
                toolbarTitle = context.getString(R.string.title_pick_attractions);
                toolbarSubtitle = context.getString(R.string.subtitle_to_assign_to_attraction);
                intent.putExtra(Constants.EXTRA_SIMPLE_PICK, true);
                break;

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
                toolbarTitle = context.getString(R.string.title_pick_attractions);
                toolbarSubtitle = context.getString(R.string.subtitle_to_assign_credit_type_to);
                break;

            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
                toolbarTitle = context.getString(R.string.title_pick_attractions);
                toolbarSubtitle = context.getString(R.string.subtitle_to_assign_category_to);
                break;

            case ASSIGN_MANUFACTURERS_TO_ATTRACTIONS:
                toolbarTitle = context.getString(R.string.title_pick_attractions);
                toolbarSubtitle = context.getString(R.string.subtitle_to_assign_manufacturer_to);
                break;

            case ASSIGN_STATUS_TO_ATTRACTIONS:
                toolbarTitle = context.getString(R.string.title_pick_attractions);
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
