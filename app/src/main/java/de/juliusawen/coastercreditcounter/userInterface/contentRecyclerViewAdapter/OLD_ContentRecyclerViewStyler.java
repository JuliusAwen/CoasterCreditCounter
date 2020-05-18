package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

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

public abstract class OLD_ContentRecyclerViewStyler
{
    public static void groupElementsAndSetDetailModes(OLD_ContentRecyclerViewAdapter oldContentRecyclerViewAdapter, RequestCode requestCode, GroupType groupType)
    {
        oldContentRecyclerViewAdapter
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
                        oldContentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(Model.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(Model.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(Model.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .setTypefaceForContentType(Model.class, Typeface.BOLD)
                                .groupItems(GroupType.NONE);
                        break;

                    case CREDIT_TYPE:
                        oldContentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(Model.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(Model.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setTypefaceForContentType(CreditType.class, Typeface.BOLD)
                            .groupItems(GroupType.CREDIT_TYPE);
                        break;

                    case CATEGORY:
                        oldContentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(Model.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(Model.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setTypefaceForContentType(Category.class, Typeface.BOLD)
                            .groupItems(GroupType.CATEGORY);
                        break;

                    case MANUFACTURER:
                        oldContentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(Model.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(Model.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .setTypefaceForContentType(Manufacturer.class, Typeface.BOLD)
                            .groupItems(GroupType.MANUFACTURER);
                        break;
                }

                oldContentRecyclerViewAdapter
                    .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                    .setSpecialStringResourceForType(IProperty.class, R.string.substitute_properties_default_postfix);
                break;
            }

            case SHOW_ATTRACTIONS:
            {
                if(groupType == GroupType.CATEGORY)
                {
                    oldContentRecyclerViewAdapter
                            .setTypefaceForContentType(GroupHeader.class, Typeface.BOLD)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                            .setTypefaceForDetailType(DetailType.STATUS, Typeface.ITALIC)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.TOTAL_RIDE_COUNT, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                            .groupItems(GroupType.CATEGORY);
                }
                break;
            }

            default:
            {
                switch(groupType)
                {
                    case NONE:
                        oldContentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                            .setTypefaceForContentType(IAttraction.class, Typeface.BOLD)
                            .groupItems(GroupType.NONE);
                        break;

                    case PARK:
                        oldContentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                            .setTypefaceForContentType(Location.class, Typeface.BOLD)
                            .groupItems(GroupType.PARK);
                        break;

                    case CREDIT_TYPE:
                        oldContentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER ,DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                            .setTypefaceForContentType(CreditType.class, Typeface.BOLD)
                            .groupItems(GroupType.CREDIT_TYPE);
                        break;

                    case CATEGORY:
                        oldContentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                            .setTypefaceForContentType(Category.class, Typeface.BOLD)
                            .groupItems(GroupType.CATEGORY);
                        break;

                    case MANUFACTURER:
                        oldContentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                            .setTypefaceForContentType(Manufacturer.class, Typeface.BOLD)
                            .groupItems(GroupType.MANUFACTURER);
                        break;

                    case MODEL:
                        oldContentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                            .setTypefaceForContentType(Model.class, Typeface.BOLD)
                            .groupItems(GroupType.MODEL);
                        break;

                    case STATUS:
                        oldContentRecyclerViewAdapter
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                            .setTypefaceForContentType(Status.class, Typeface.BOLD)
                            .groupItems(GroupType.STATUS);
                        break;
                }

                oldContentRecyclerViewAdapter.setSpecialStringResourceForType(IProperty.class, R.string.substitute_properties_default_postfix);
            }
        }
    }
}
