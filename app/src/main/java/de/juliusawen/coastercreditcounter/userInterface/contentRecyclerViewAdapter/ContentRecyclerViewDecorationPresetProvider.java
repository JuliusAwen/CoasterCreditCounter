package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.graphics.Typeface;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public abstract class ContentRecyclerViewDecorationPresetProvider
{
    public static void applyDecorationPreset(ContentRecyclerViewDecoration decoration, RequestCode requestCode, ElementType elementType)
    {
        ContentRecyclerViewDecorationPresetProvider.applyDecorationPreset(decoration, requestCode);

        switch(elementType)
        {
            case CREDIT_TYPE:
            case STATUS:
            {
                decoration
                        .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                        .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MODEL, DetailDisplayMode.ABOVE)
                        .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.LOCATION, DetailDisplayMode.BELOW)
                        .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.CATEGORY, DetailDisplayMode.BELOW);
                break;
            }

            case CATEGORY:
            {
                decoration
                        .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                        .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MODEL, DetailDisplayMode.ABOVE)
                        .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.LOCATION, DetailDisplayMode.BELOW);
                break;
            }

            case MANUFACTURER:
            {
                decoration
                        .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MODEL, DetailDisplayMode.ABOVE)
                        .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.LOCATION, DetailDisplayMode.BELOW)
                        .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.CATEGORY, DetailDisplayMode.BELOW);
                break;
            }

            default:
                Log.v(String.format("no preset found for %s and %s", requestCode, elementType));
                break;
        }
    }

    public static void applyDecorationPreset(ContentRecyclerViewDecoration decoration, RequestCode requestCode, GroupType groupType)
    {
        ContentRecyclerViewDecorationPresetProvider.applyDecorationPreset(decoration, requestCode);

        switch(requestCode)
        {
            case PICK_MODEL:
            case MANAGE_MODELS:
            {
                switch(groupType)
                {
                    case NONE:
                        decoration
                                .addDetailTypesAndModeForContentType(ElementType.MODEL, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.MODEL, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.MODEL, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .addTypefaceForElementType(ElementType.CREDIT_TYPE, Typeface.BOLD);
                        break;

                    case CREDIT_TYPE:
                        decoration
                                .addDetailTypesAndModeForContentType(ElementType.MODEL, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.MODEL, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE);
                        break;

                    case CATEGORY:
                        decoration
                                .addDetailTypesAndModeForContentType(ElementType.MODEL, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.MODEL, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE);
                        break;

                    case MANUFACTURER:
                        decoration
                                .addDetailTypesAndModeForContentType(ElementType.MODEL, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.MODEL, DetailType.CATEGORY, DetailDisplayMode.BELOW);
                        break;
                }

                decoration
                        .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.LOCATION, DetailDisplayMode.BELOW)
                        .addSpecialStringResourceForElementType(ElementType.IPROPERTY, R.string.substitute_properties_default_postfix);
                break;
            }

            case SHOW_ATTRACTIONS:
            {
                if(groupType == GroupType.CATEGORY)
                {
                    decoration
                            .addTypefaceForElementType(ElementType.IGROUP_HEADER, Typeface.BOLD)
                            .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MODEL, DetailDisplayMode.ABOVE)
                            .addTypefaceForDetailType(DetailType.STATUS, Typeface.ITALIC)
                            .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.TOTAL_RIDE_COUNT, DetailDisplayMode.BELOW)
                            .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.STATUS, DetailDisplayMode.BELOW);
                }
                break;
            }

            default:
            {
                switch(groupType)
                {
                    case NONE:
                        decoration
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.STATUS, DetailDisplayMode.BELOW)
                                .addTypefaceForElementType(ElementType.IATTRACTION, Typeface.BOLD);
                        break;

                    case PARK:
                        decoration
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.STATUS, DetailDisplayMode.BELOW)
                                .addTypefaceForElementType(ElementType.LOCATION, Typeface.BOLD);
                        break;

                    case CREDIT_TYPE:
                        decoration
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MANUFACTURER ,DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.STATUS, DetailDisplayMode.BELOW)
                                .addTypefaceForElementType(ElementType.CREDIT_TYPE, Typeface.BOLD);
                        break;

                    case CATEGORY:
                        decoration
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.STATUS, DetailDisplayMode.BELOW)
                                .addTypefaceForElementType(ElementType.CATEGORY, Typeface.BOLD);
                        break;

                    case MANUFACTURER:
                        decoration
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.CREDIT_TYPE, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.STATUS, DetailDisplayMode.BELOW)
                                .addTypefaceForElementType(ElementType.MANUFACTURER, Typeface.BOLD);
                        break;

                    case MODEL:
                        decoration
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.CATEGORY, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.STATUS, DetailDisplayMode.BELOW)
                                .addTypefaceForElementType(ElementType.MODEL, Typeface.BOLD);
                        break;

                    case STATUS:
                        decoration
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(ElementType.IATTRACTION, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .addTypefaceForElementType(ElementType.STATUS, Typeface.BOLD);
                        break;

                    default:
                        Log.v(String.format("no preset found for %s and %s", requestCode, groupType));
                        break;
                }

                decoration.addSpecialStringResourceForElementType(ElementType.IPROPERTY, R.string.substitute_properties_default_postfix);
            }
        }
    }

    public static void applyDecorationPreset(ContentRecyclerViewDecoration decoration, RequestCode requestCode)
    {
        Log.d(String.format("%s...", requestCode));

        decoration.resetStyles();

        switch(requestCode)
        {
            case NAVIGATE:
            {
                decoration
                        .addTypefaceForElementType(ElementType.PARK, Typeface.BOLD_ITALIC);
                break;
            }

            case SHOW_LOCATIONS:
            {
                decoration
                        .addTypefaceForElementType(ElementType.LOCATION, Typeface.BOLD);
                break;
            }

            case SHOW_VISIT:
            {
                decoration.addSpecialStringResourceForElementType(ElementType.VISITED_ATTRACTION, R.string.text_visited_attraction_pretty_print);
            }

            case SORT_LOCATIONS:
            case SORT_PARKS:
            {
                decoration
                        .addTypefaceForElementType(ElementType.IELEMENT, Typeface.BOLD);
                break;
            }

            case MANAGE_CREDIT_TYPES:
            case MANAGE_CATEGORIES:
            case MANAGE_MANUFACTURERS:
            case MANAGE_MODELS:
            case MANAGE_STATUSES:

            case PICK_CREDIT_TYPE:
            case PICK_CATEGORY:
            case PICK_MANUFACTURER:
            case PICK_MODEL:
            case PICK_STATUS:

            case SORT_ATTRACTIONS:
            case SORT_CREDIT_TYPES:
            case SORT_CATEGORIES:
            case SORT_MANUFACTURERS:
            case SORT_MODELS:
            case SORT_STATUSES:
            {
                decoration
                        .addSpecialStringResourceForElementType(ElementType.IPROPERTY, R.string.substitute_properties_default_postfix);
                break;
            }

            case PICK_VISIT:
            {
                decoration
                    .addTypefaceForElementType(ElementType.VISIT, Typeface.BOLD)
                    .addSpecialStringResourceForElementType(ElementType.VISIT, R.string.text_visit_display_full_name);
                break;
            }

            default:
                Log.v(String.format("no preset found for %s", requestCode));
                break;
        }

        decoration
                .addTypefaceForElementType(ElementType.IGROUP_HEADER, Typeface.BOLD)
                .addTypefaceForDetailType(DetailType.STATUS, Typeface.ITALIC);
    }
}
