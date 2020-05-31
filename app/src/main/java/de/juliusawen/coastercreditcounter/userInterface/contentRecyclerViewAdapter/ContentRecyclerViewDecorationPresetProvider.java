package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.graphics.Typeface;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.GroupHeader;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Model;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
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
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW);
                break;
            }

            case CATEGORY:
            {
                decoration
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW);
                break;
            }

            case MANUFACTURER:
            {
                decoration
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW);
                break;
            }
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
                                .addDetailTypesAndModeForContentType(Model.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(Model.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(Model.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .addTypefaceForContentType(Model.class, Typeface.BOLD);
                        break;

                    case CREDIT_TYPE:
                        decoration
                                .addDetailTypesAndModeForContentType(Model.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(Model.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .addTypefaceForContentType(CreditType.class, Typeface.BOLD);
                        break;

                    case CATEGORY:
                        decoration
                                .addDetailTypesAndModeForContentType(Model.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(Model.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .addTypefaceForContentType(Category.class, Typeface.BOLD);
                        break;

                    case MANUFACTURER:
                        decoration
                                .addDetailTypesAndModeForContentType(Model.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(Model.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .addTypefaceForContentType(Manufacturer.class, Typeface.BOLD);
                        break;
                }

                decoration
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                        .addSpecialStringResourceForContentType(IProperty.class, R.string.substitute_properties_default_postfix);
                break;
            }

            case SHOW_ATTRACTIONS:
            {
                if(groupType == GroupType.CATEGORY)
                {
                    decoration
                            .addTypefaceForContentType(GroupHeader.class, Typeface.BOLD)
                            .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                            .addTypefaceForDetailType(DetailType.STATUS, Typeface.ITALIC)
                            .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.TOTAL_RIDE_COUNT, DetailDisplayMode.BELOW)
                            .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW);
                }
                break;
            }

            default:
            {
                switch(groupType)
                {
                    case NONE:
                        decoration
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                                .addTypefaceForContentType(IAttraction.class, Typeface.BOLD);
                        break;

                    case PARK:
                        decoration
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                                .addTypefaceForContentType(Location.class, Typeface.BOLD);
                        break;

                    case CREDIT_TYPE:
                        decoration
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER ,DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                                .addTypefaceForContentType(CreditType.class, Typeface.BOLD);
                        break;

                    case CATEGORY:
                        decoration
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                                .addTypefaceForContentType(Category.class, Typeface.BOLD);
                        break;

                    case MANUFACTURER:
                        decoration
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                                .addTypefaceForContentType(Manufacturer.class, Typeface.BOLD);
                        break;

                    case MODEL:
                        decoration
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                                .addTypefaceForContentType(Model.class, Typeface.BOLD);
                        break;

                    case STATUS:
                        decoration
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .addTypefaceForContentType(Status.class, Typeface.BOLD);
                        break;
                }

                decoration.addSpecialStringResourceForContentType(IProperty.class, R.string.substitute_properties_default_postfix);
            }
        }
    }

    public static void applyDecorationPreset(ContentRecyclerViewDecoration decoration, RequestCode requestCode)
    {
        Log.d(String.format("RequestCode[%s]...", requestCode));

        decoration.resetStyles();

        switch(requestCode)
        {
            case NAVIGATE:
            {
                decoration
                        .addTypefaceForContentType(Park.class, Typeface.BOLD_ITALIC);
                break;
            }

            case SHOW_LOCATIONS:
            {
                decoration
                        .addTypefaceForContentType(Location.class, Typeface.BOLD);
                break;
            }

            case SORT_LOCATIONS:
            case SORT_PARKS:
            case SORT_ATTRACTIONS:
            case SORT_CREDIT_TYPES:
            case SORT_CATEGORIES:
            case SORT_MANUFACTURERS:
            case SORT_MODELS:
            case SORT_STATUSES:
            {
                decoration
                        .addTypefaceForContentType(IElement.class, Typeface.BOLD)
                        .addDetailTypesAndModeForContentType(Model.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                        .addDetailTypesAndModeForContentType(Model.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                        .addDetailTypesAndModeForContentType(Model.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                        .addSpecialStringResourceForContentType(IProperty.class, R.string.substitute_properties_default_postfix);
                break;
            }

            case PICK_CREDIT_TYPE:
            case PICK_CATEGORY:
            case PICK_MANUFACTURER:
            case PICK_MODEL:
            case PICK_STATUS:
            case MANAGE_CREDIT_TYPES:
            case MANAGE_CATEGORIES:
            case MANAGE_MANUFACTURERS:
            case MANAGE_MODELS:
            case MANAGE_STATUSES:
            {
                decoration
                        .addTypefaceForContentType(IProperty.class, Typeface.BOLD)
                        .addSpecialStringResourceForContentType(IProperty.class, R.string.substitute_properties_default_postfix);
                break;
            }
        }

        decoration
                .addTypefaceForContentType(GroupHeader.class, Typeface.BOLD)
                .addTypefaceForDetailType(DetailType.STATUS, Typeface.ITALIC);
    }
}
