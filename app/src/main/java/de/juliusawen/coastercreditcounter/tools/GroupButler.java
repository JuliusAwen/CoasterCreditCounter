package de.juliusawen.coastercreditcounter.tools;

import android.graphics.Typeface;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.GroupHeader;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Model;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailDisplayMode;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailType;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.GroupType;

public abstract class GroupButler
{
    public static void groupElementsAndSetDetailModes(ContentRecyclerViewAdapter contentRecyclerViewAdapter, RequestCode requestCode, GroupType groupType)
    {
        contentRecyclerViewAdapter
                .clearTypefacesForContentType()
                .setTypefaceForContentType(GroupHeader.class, Typeface.BOLD)
                .clearTypefacesForDetailType()
                .setTypefaceForDetailType(DetailType.STATUS, Typeface.ITALIC)
                .clearDetailTypesAndModeForContentType();

        switch(requestCode)
        {
            case PICK_MODEL:
            case MANAGE_MODELS:
            {
                switch(groupType)
                {
                    case NONE:
                        contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(Model.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(Model.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(Model.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .setTypefaceForContentType(Model.class, Typeface.BOLD)
                                .groupItems(GroupType.NONE);
                        break;

                    case CREDIT_TYPE:
                        contentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(Model.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(Model.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setTypefaceForContentType(CreditType.class, Typeface.BOLD)
                            .groupItems(GroupType.CREDIT_TYPE);
                        break;

                    case CATEGORY:
                        contentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(Model.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(Model.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setTypefaceForContentType(Category.class, Typeface.BOLD)
                            .groupItems(GroupType.CATEGORY);
                        break;

                    case MANUFACTURER:
                        contentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(Model.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(Model.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .setTypefaceForContentType(Manufacturer.class, Typeface.BOLD)
                            .groupItems(GroupType.MANUFACTURER);
                        break;
                }

                contentRecyclerViewAdapter
                    .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                    .setSpecialStringResourceForType(IProperty.class, R.string.substitute_properties_default_postfix);
                break;
            }

            default:
            {
                switch(groupType)
                {
                    case NONE:
                        contentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                            .setTypefaceForContentType(IAttraction.class, Typeface.BOLD)
                            .groupItems(GroupType.NONE);
                        break;

                    case PARK:
                        contentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                            .setTypefaceForContentType(Location.class, Typeface.BOLD)
                            .groupItems(GroupType.PARK);
                        break;

                    case CREDIT_TYPE:
                        contentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER ,DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                            .setTypefaceForContentType(CreditType.class, Typeface.BOLD)
                            .groupItems(GroupType.CREDIT_TYPE);
                        break;

                    case CATEGORY:
                        contentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                            .setTypefaceForContentType(Category.class, Typeface.BOLD)
                            .groupItems(GroupType.CATEGORY);
                        break;

                    case MANUFACTURER:
                        contentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                            .setTypefaceForContentType(Manufacturer.class, Typeface.BOLD)
                            .groupItems(GroupType.MANUFACTURER);
                        break;

                    case MODEL:
                        contentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                            .setTypefaceForContentType(Model.class, Typeface.BOLD)
                            .groupItems(GroupType.MODEL);
                        break;

                    case STATUS:
                        contentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                            .setTypefaceForContentType(Status.class, Typeface.BOLD)
                            .groupItems(GroupType.STATUS);
                        break;
                }

                contentRecyclerViewAdapter.setSpecialStringResourceForType(IProperty.class, R.string.substitute_properties_default_postfix);
            }
        }
    }
}
