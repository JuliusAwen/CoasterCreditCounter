package de.juliusawen.coastercreditcounter.tools.activityDistributor;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.enums.ShowParkTab;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;
import de.juliusawen.coastercreditcounter.userInterface.activities.CreateAttractionActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.CreateChildForLocationActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.CreateSimpleElementActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.CreateVisitActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.EditAttractionActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.EditSimpleElementActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.ManagePropertiesActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.NavigationHubActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.PickElementsActivity;
import de.juliusawen.coastercreditcounter.userInterface.activities.ShowAttractionActivity;
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
        Intent intent = null;
        switch(requestCode)
        {
            case SHOW_LOCATIONS:
                intent = new Intent(context, ShowLocationsActivity.class);
                break;

            case SHOW_PARK:
                intent = new Intent(context, ShowParkActivity.class);
                intent.putExtra(Constants.EXTRA_DEFAULT_TAB, ShowParkTab.SHOW_ATTRACTIONS.ordinal());
                break;

            case SHOW_VISIT:
                intent = new Intent(context, ShowVisitActivity.class);
                break;

            case SHOW_ATTRACTION:
                intent = new Intent(context, ShowAttractionActivity.class);
                break;
        }

        if(intent != null)
        {
            intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode.ordinal());
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, element.getUuid().toString());
            if(flagActivityClearTop)
            {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            Log.i(String.format("starting [%s] for %s with %s from [%s]",
                    StringTool.parseActivityName(intent.getComponent().getShortClassName()), element, requestCode, context.getClass().getSimpleName()));

            Log.frame(LogLevel.INFO, String.format("leaving [%s]", context.getClass().getSimpleName()), '-', false);
            context.startActivity(intent);
        }
        else
        {
            Log.e(String.format("unable to start activity: unknown %s for type %s", requestCode, element));
        }
    }

    public static void startActivityShowForResult(Context context, RequestCode requestCode, IElement element)
    {
        Intent intent = null;
        if(requestCode == RequestCode.SHOW_ATTRACTION)
        {
            intent = new Intent(context, ShowAttractionActivity.class);
        }

        if(intent != null)
        {
            intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode.ordinal());
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, element.getUuid().toString());

            Log.i(String.format("starting [%s] with %s from [%s]",
                            StringTool.parseActivityName(intent.getComponent().getShortClassName()), requestCode, context.getClass().getSimpleName()));

            Log.frame(LogLevel.INFO, String.format("leaving [%s]", context.getClass().getSimpleName()), '-', false);
            ((Activity)context).startActivityForResult(intent, requestCode.ordinal());
        }
        else
        {
            Log.e(String.format("unable to start activity: unknown %s for type %s", requestCode, element));
        }
    }

    public static void startActivityManageForResult(Context context, RequestCode requestCode)
    {
        Intent intent = null;
        String toolbarTitle = context.getString(R.string.error_missing_text);
        String toolbarSubtitle = context.getString(R.string.error_missing_text);
        String helpTitle = context.getString(R.string.error_missing_text);
        String helpText = context.getString(R.string.error_missing_text);

        switch(requestCode)
        {
            case MANAGE_CREDIT_TYPES:
                intent = new Intent(context, ManagePropertiesActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_TO_MANAGE, ElementType.CREDIT_TYPE.ordinal());
                toolbarTitle = context.getString(R.string.credit_type);
                toolbarSubtitle = context.getString(R.string.subtitle_management);
                helpTitle = context.getString(R.string.help_title_manage_property, context.getString(R.string.credit_types));
                helpText = context.getString(R.string.help_text_manage_credit_types);
                break;

            case MANAGE_CATEGORIES:
                intent = new Intent(context, ManagePropertiesActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_TO_MANAGE, ElementType.CATEGORY.ordinal());
                toolbarTitle = context.getString(R.string.category);
                toolbarSubtitle = context.getString(R.string.subtitle_management);
                helpTitle = context.getString(R.string.help_title_manage_property, context.getString(R.string.categories));
                helpText = context.getString(R.string.help_text_manage_categories);
                break;

            case MANAGE_MANUFACTURERS:
                intent = new Intent(context, ManagePropertiesActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_TO_MANAGE, ElementType.MANUFACTURER.ordinal());
                toolbarTitle = context.getString(R.string.manufacturer);
                toolbarSubtitle = context.getString(R.string.subtitle_management);
                helpTitle = context.getString(R.string.help_title_manage_property, context.getString(R.string.manufacturers));
                helpText = context.getString(R.string.help_text_manage_manufacturers);
                break;

            case MANAGE_MODELS:
                intent = new Intent(context, ManagePropertiesActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_TO_MANAGE, ElementType.MODEL.ordinal());
                toolbarTitle = context.getString(R.string.model);
                toolbarSubtitle = context.getString(R.string.subtitle_management);
                helpTitle = context.getString(R.string.help_title_manage_property, context.getString(R.string.models));
                helpText = context.getString(R.string.help_text_manage_models);
                break;

            case MANAGE_STATUSES:
                intent = new Intent(context, ManagePropertiesActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_TO_MANAGE, ElementType.STATUS.ordinal());
                toolbarTitle = context.getString(R.string.status);
                toolbarSubtitle = context.getString(R.string.subtitle_management);
                helpTitle = context.getString(R.string.help_title_manage_property, context.getString(R.string.statuses));
                helpText = context.getString(R.string.help_text_manage_statuses);
                break;
        }

        if(intent != null)
        {
            intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode.ordinal());
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
            intent.putExtra(Constants.EXTRA_TOOLBAR_SUBTITLE, toolbarSubtitle);
            intent.putExtra(Constants.EXTRA_HELP_TITLE, helpTitle);
            intent.putExtra(Constants.EXTRA_HELP_TEXT, helpText);

            Log.i(String.format("starting [%s] with %s from [%s]",
                    StringTool.parseActivityName(intent.getComponent().getShortClassName()), requestCode, context.getClass().getSimpleName()));

            Log.frame(LogLevel.INFO, String.format("leaving [%s]", context.getClass().getSimpleName()), '-', false);
            ((Activity)context).startActivityForResult(intent, requestCode.ordinal());
        }
        else
        {
            Log.e(String.format("unable to start activity: unknown %s from [%s]", requestCode, context.getClass().getSimpleName()));
        }
    }

    public static void startActivityEditForResult(Context context, RequestCode requestCode, IElement element)
    {
        Intent intent = null;
        String toolbarTitle = context.getString(R.string.error_missing_text);

        switch(requestCode)
        {
            case EDIT_LOCATION:
                intent = new Intent(context, EditSimpleElementActivity.class);
                toolbarTitle = context.getString(R.string.title_edit_location);
                intent.putExtra(Constants.EXTRA_HINT, context.getString(R.string.hint_edit_name, element.getName()));
                break;

            case EDIT_PARK:
                intent = new Intent(context, EditSimpleElementActivity.class);
                toolbarTitle = context.getString(R.string.title_edit_park);
                intent.putExtra(Constants.EXTRA_HINT, context.getString(R.string.hint_edit_name, element.getName()));
                break;

            case EDIT_CREDIT_TYPE:
                intent = new Intent(context, EditSimpleElementActivity.class);
                toolbarTitle = context.getString(R.string.title_edit_credit_type);
                intent.putExtra(Constants.EXTRA_HINT, context.getString(R.string.hint_edit_name, element.getName()));
                break;

            case EDIT_CATEGORY:
                intent = new Intent(context, EditSimpleElementActivity.class);
                toolbarTitle = context.getString(R.string.title_edit_category);
                intent.putExtra(Constants.EXTRA_HINT, context.getString(R.string.hint_edit_name, element.getName()));
                break;

            case EDIT_MANUFACTURER:
                intent = new Intent(context, EditSimpleElementActivity.class);
                toolbarTitle = context.getString(R.string.title_edit_manufacturer);
                intent.putExtra(Constants.EXTRA_HINT, context.getString(R.string.hint_edit_name, element.getName()));
                break;

            case EDIT_STATUS:
                intent = new Intent(context, EditSimpleElementActivity.class);
                toolbarTitle = context.getString(R.string.title_edit_status);
                intent.putExtra(Constants.EXTRA_HINT, context.getString(R.string.hint_edit_name, element.getName()));
                break;

            case EDIT_NOTE:
                intent = new Intent(context, EditSimpleElementActivity.class);
                toolbarTitle = context.getString(R.string.title_edit_note);
                intent.putExtra(Constants.EXTRA_HINT, context.getString(R.string.hint_edit_note));
                intent.putExtra(Constants.EXTRA_TOOLBAR_SUBTITLE, context.getString(R.string.subtitle_note, element.getParent().getName()));
                break;

            case EDIT_ATTRACTION:
                intent = new Intent(context, EditAttractionActivity.class);
                intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_edit_on_site_attraction));
                intent.putExtra(Constants.EXTRA_HELP_TEXT, context.getString(R.string.help_text_edit_attraction));
                toolbarTitle = context.getString(R.string.title_edit_on_site_attraction);
                break;
        }

        if(intent != null)
        {
            intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode.ordinal());
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, element.getUuid().toString());

            Log.i(String.format("starting [%s] for %s with %s from [%s]",
                    StringTool.parseActivityName(intent.getComponent().getShortClassName()), element, requestCode, context.getClass().getSimpleName()));

            Log.frame(LogLevel.INFO, String.format("leaving [%s]", context.getClass().getSimpleName()), '-', false);
            ((Activity)context).startActivityForResult(intent, requestCode.ordinal());
        }
        else
        {
            Log.e( String.format("unable to start activity: unknown %s for type [%s] from [%s]", requestCode, element, context.getClass().getSimpleName()));
        }
    }

    public static void startActivityCreateForResult(Context context, RequestCode requestCode)
    {
        ActivityDistributor.startActivityCreateForResult(context, requestCode, null);
    }

    public static void startActivityCreateForResult(Context context, RequestCode requestCode, IElement parentElement)
    {
        Intent intent;
        String toolbarTitle = context.getString(R.string.error_missing_text);
        String helpTitle = context.getString(R.string.error_missing_text);
        String helpText = context.getString(R.string.error_missing_text);
        String hint = context.getString(R.string.error_missing_text);

        switch(requestCode)
        {
            case CREATE_LOCATION:
                intent = new Intent(context, CreateChildForLocationActivity.class);
                intent.putExtra(Constants.EXTRA_TOOLBAR_SUBTITLE, context.getString(R.string.subtitle_create_location));
                helpTitle = context.getString(R.string.subtitle_create_attraction);
                helpText = context.getString(R.string.help_text_create_location);
                hint = context.getString(R.string.hint_enter_location_name);
                break;

            case CREATE_PARK:
                intent = new Intent(context, CreateChildForLocationActivity.class);
                intent.putExtra(Constants.EXTRA_TOOLBAR_SUBTITLE, context.getString(R.string.subtitle_create_park));
                helpTitle = context.getString(R.string.subtitle_create_park);
                helpText = context.getString(R.string.help_text_create_park);
                hint = context.getString(R.string.hint_enter_park_name);
                break;

            case CREATE_VISIT:
                intent = new Intent(context, CreateVisitActivity.class);
                break;

            case CREATE_ATTRACTION:
                intent = new Intent(context, CreateAttractionActivity.class);
                toolbarTitle = context.getString(R.string.title_create_attraction);
                intent.putExtra(Constants.EXTRA_TOOLBAR_SUBTITLE, context.getString(R.string.subtitle_create_attraction, parentElement.getName()));
                helpTitle = context.getString(R.string.title_create_attraction);
                helpText = context.getString(R.string.help_text_create_attraction);
                hint = context.getString(R.string.hint_enter_attraction_name);

                break;

            case CREATE_CREDIT_TYPE:
                intent = new Intent(context, CreateSimpleElementActivity.class);
                toolbarTitle = context.getString(R.string.title_create_credit_type);
                helpTitle = context.getString(R.string.title_create_credit_type);
                helpText = context.getString(R.string.help_text_create_credit_type);
                hint = context.getString(R.string.hint_enter_credit_type_name);
                break;

            case CREATE_CATEGORY:
                intent = new Intent(context, CreateSimpleElementActivity.class);
                toolbarTitle = context.getString(R.string.title_create_category);
                helpTitle = context.getString(R.string.title_create_category);
                helpText = context.getString(R.string.help_text_create_category);
                hint = context.getString(R.string.hint_enter_category_name);
                break;

            case CREATE_MANUFACTURER:
                intent = new Intent(context, CreateSimpleElementActivity.class);
                toolbarTitle = context.getString(R.string.title_create_manufacturer);
                helpTitle = context.getString(R.string.title_create_manufacturer);
                helpText = context.getString(R.string.help_text_create_manufacturer);
                hint = context.getString(R.string.hint_enter_manufacturer_name);
                break;

            case CREATE_STATUS:
                intent = new Intent(context, CreateSimpleElementActivity.class);
                toolbarTitle = context.getString(R.string.title_create_status);
                helpTitle = context.getString(R.string.title_create_status);
                helpText = context.getString(R.string.help_text_create_status);
                hint = context.getString(R.string.hint_enter_status_name);
                break;

            case CREATE_NOTE:
                intent = new Intent(context, CreateSimpleElementActivity.class);
                toolbarTitle = context.getString(R.string.title_create_note);
                intent.putExtra(Constants.EXTRA_TOOLBAR_SUBTITLE, context.getString(R.string.subtitle_note, parentElement.getName()));
                helpTitle = context.getString(R.string.title_create_note);
                helpText = context.getString(R.string.help_text_create_note);
                hint = context.getString(R.string.hint_enter_note);
                break;

            default:
                intent = new Intent(context, CreateSimpleElementActivity.class);
                break;
        }

        if(intent != null)
        {
            intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode.ordinal());
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
            intent.putExtra(Constants.EXTRA_HELP_TITLE, helpTitle);
            intent.putExtra(Constants.EXTRA_HELP_TEXT, helpText);
            intent.putExtra(Constants.EXTRA_HINT, hint);

            if(parentElement != null)
            {
                intent.putExtra(Constants.EXTRA_ELEMENT_UUID, parentElement.getUuid().toString());
            }

            Log.i(String.format("starting [%s] with %s with %s from [%s]",
                    StringTool.parseActivityName(intent.getComponent().getShortClassName()),
                    parentElement != null ? String.format("[%s %s]", "parent", parentElement) : "[no parent]",
                    requestCode,
                    context.getClass().getSimpleName()));

            Log.frame(LogLevel.INFO, String.format("leaving [%s]", context.getClass().getSimpleName()), '-', false);
            ((Activity)context).startActivityForResult(intent, requestCode.ordinal());
        }
        else
        {
            Log.e(String.format("unable to start activity: unknown %s from [%s]", requestCode, context.getClass().getSimpleName()));
        }
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

            case SORT_MODELS:
                toolbarSubtitle = context.getString(R.string.models);
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
            intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode.ordinal());
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, context.getString(R.string.title_sort));
            intent.putExtra(Constants.EXTRA_TOOLBAR_SUBTITLE, toolbarSubtitle);
            intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(elementsToSort));

            Log.i(String.format(Locale.getDefault(), "starting [%s] for [%d] Elements with %s from [%s]",
                    StringTool.parseActivityName(intent.getComponent().getShortClassName()), elementsToSort.size(), requestCode, context.getClass().getSimpleName()));

            Log.frame(LogLevel.INFO, String.format("leaving [%s]", context.getClass().getSimpleName()), '-', false);
            ((Activity)context).startActivityForResult(intent, requestCode.ordinal());
        }
        else
        {
            Log.e(String.format("unable to start activity: unknown %s", requestCode));
        }
    }

    public static void startActivityPickForResult(Context context, RequestCode requestCode, List<IElement> elementsToPickFrom)
    {
        Intent intent = null;
        String toolbarTitle = context.getString(R.string.error_missing_text);
        String toolbarSubtitle = context.getString(R.string.error_missing_text);

        switch(requestCode)
        {
            // SINGLE PICK

            case PICK_VISIT:
                intent = new Intent(context, PickElementsActivity.class);
                intent.putExtra(Constants.EXTRA_SINGLE_PICK, true);
                toolbarTitle = context.getString(R.string.title_pick_visit);
                toolbarSubtitle = context.getString(R.string.subtitle_to_open);
                break;

            case PICK_CREDIT_TYPE:
                intent = new Intent(context, ManagePropertiesActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_TO_MANAGE, ElementType.CREDIT_TYPE.ordinal());
                intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_pick_credit_type));
                intent.putExtra(Constants.EXTRA_HELP_TEXT, context.getString(R.string.help_text_pick_element));
                toolbarTitle = context.getString(R.string.title_pick_credit_type);
                toolbarSubtitle = context.getString(R.string.subtitle_to_assign_to_attraction);
                break;

            case PICK_CATEGORY:
                intent = new Intent(context, ManagePropertiesActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_TO_MANAGE, ElementType.CATEGORY.ordinal());
                intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_pick_category));
                intent.putExtra(Constants.EXTRA_HELP_TEXT, context.getString(R.string.help_text_pick_element));
                toolbarTitle = context.getString(R.string.title_pick_category);
                toolbarSubtitle = context.getString(R.string.subtitle_to_assign_to_attraction);
                break;

            case PICK_MANUFACTURER:
                intent = new Intent(context, ManagePropertiesActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_TO_MANAGE, ElementType.MANUFACTURER.ordinal());
                intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_pick_manufacturer));
                intent.putExtra(Constants.EXTRA_HELP_TEXT, context.getString(R.string.help_text_pick_element));
                toolbarTitle = context.getString(R.string.title_pick_manufacturer);
                toolbarSubtitle = context.getString(R.string.subtitle_to_assign_to_attraction);
                break;

            case PICK_MODEL:
                intent = new Intent(context, ManagePropertiesActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_TO_MANAGE, ElementType.MODEL.ordinal());
                intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_pick_model));
                intent.putExtra(Constants.EXTRA_HELP_TEXT, context.getString(R.string.help_text_pick_element));
                toolbarTitle = context.getString(R.string.title_pick_model);
                toolbarSubtitle = context.getString(R.string.subtitle_to_assign_to_attraction);
                break;

            case PICK_STATUS:
                intent = new Intent(context, ManagePropertiesActivity.class);
                intent.putExtra(Constants.EXTRA_TYPE_TO_MANAGE, ElementType.STATUS.ordinal());
                intent.putExtra(Constants.EXTRA_HELP_TITLE, context.getString(R.string.title_pick_status));
                intent.putExtra(Constants.EXTRA_HELP_TEXT, context.getString(R.string.help_text_pick_element));
                toolbarTitle = context.getString(R.string.title_pick_status);
                toolbarSubtitle = context.getString(R.string.subtitle_to_assign_to_attraction);
                break;


            // MULTIPLE PICK

            case PICK_ATTRACTIONS:
                intent = new Intent(context, PickElementsActivity.class);
                toolbarTitle = context.getString(R.string.title_pick_attractions);
                toolbarSubtitle = context.getString(R.string.subtitle_attractions_to_add_to_visit);
                break;

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
                intent = new Intent(context, PickElementsActivity.class);
                toolbarTitle = context.getString(R.string.title_pick_attractions);
                toolbarSubtitle = context.getString(R.string.subtitle_to_assign_credit_type_to);
                break;

            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
                intent = new Intent(context, PickElementsActivity.class);
                toolbarTitle = context.getString(R.string.title_pick_attractions);
                toolbarSubtitle = context.getString(R.string.subtitle_to_assign_category_to);
                break;

            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
                intent = new Intent(context, PickElementsActivity.class);
                toolbarTitle = context.getString(R.string.title_pick_attractions);
                toolbarSubtitle = context.getString(R.string.subtitle_to_assign_manufacturer_to);
                break;

            case ASSIGN_STATUS_TO_ATTRACTIONS:
                intent = new Intent(context, PickElementsActivity.class);
                toolbarTitle = context.getString(R.string.title_pick_attractions);
                toolbarSubtitle = context.getString(R.string.subtitle_status_to_assign_status_to);
                break;
        }

        if(intent != null)
        {
            intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode.ordinal());
            intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(elementsToPickFrom));
            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, toolbarTitle);
            intent.putExtra(Constants.EXTRA_TOOLBAR_SUBTITLE, toolbarSubtitle);

            Log.i(String.format(Locale.getDefault(), "starting [%s] for [%d] Elements with %s from [%s]",
                    StringTool.parseActivityName(intent.getComponent().getShortClassName()), elementsToPickFrom.size(), requestCode, context.getClass().getSimpleName()));

            Log.frame(LogLevel.INFO, String.format("leaving [%s]", context.getClass().getSimpleName()), '-', false);
            ((Activity)context).startActivityForResult(intent, requestCode.ordinal());
        }
        else
        {
            Log.e(String.format("unable to start activity: unknown %s", requestCode));
        }
    }

    public static void startActivityViaIntent(Context context, Intent intent)
    {
        Log.i(String.format("ActivityDistributor.startActivityViaIntent:: starting [%s] from [%s] via given intent...",
                StringTool.parseActivityName(intent.getComponent().getShortClassName()), context.getClass().getSimpleName()));

        Log.frame(LogLevel.INFO, String.format("leaving [%s]", context.getClass().getSimpleName()), '-', false);
        context.startActivity(intent);
    }

    @SuppressWarnings("rawtypes")
    public static void startActivityViaClass(Context context, Class type)
    {
        Intent intent = new Intent(context, type);

        Log.i(String.format("starting [%s] from [%s] via given class type...", StringTool.parseActivityName(intent.getComponent().getShortClassName()), context.getClass().getSimpleName()));

        Log.frame(LogLevel.INFO, String.format("leaving [%s]", context.getClass().getSimpleName()), '-', false);
        context.startActivity(intent);
    }

    public static void goToCurrentVisit(Context context, IElement currentVisit)
    {
        Intent navigationHubIntent = new Intent(context, NavigationHubActivity.class);
        navigationHubIntent.putExtra(Constants.EXTRA_REQUEST_CODE, RequestCode.NAVIGATE.ordinal());

        Intent showLocationsIntent = new Intent(context, ShowLocationsActivity.class);
        showLocationsIntent.putExtra(Constants.EXTRA_REQUEST_CODE, RequestCode.SHOW_LOCATIONS.ordinal());
        showLocationsIntent.putExtra(Constants.EXTRA_ELEMENT_UUID, App.content.getRootLocation().getUuid().toString());

        Intent showParkIntent = new Intent(context, ShowParkActivity.class);
        showParkIntent.putExtra(Constants.EXTRA_REQUEST_CODE, RequestCode.SHOW_PARK.ordinal());
        showParkIntent.putExtra(Constants.EXTRA_DEFAULT_TAB, ShowParkTab.SHOW_VISITS.ordinal());
        showParkIntent.putExtra(Constants.EXTRA_ELEMENT_UUID, currentVisit.getParent().getUuid().toString());

        Intent showVisitIntent = new Intent(context, ShowVisitActivity.class);
        showVisitIntent.putExtra(Constants.EXTRA_REQUEST_CODE, RequestCode.SHOW_VISIT.ordinal());
        showVisitIntent.putExtra(Constants.EXTRA_ELEMENT_UUID, currentVisit.getUuid().toString());

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntent(navigationHubIntent);
        taskStackBuilder.addNextIntent(showLocationsIntent);
        taskStackBuilder.addNextIntent(showParkIntent);
        taskStackBuilder.addNextIntent(showVisitIntent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.frame(LogLevel.INFO, String.format("leaving [%s]", context.getClass().getSimpleName()), '-', false);
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