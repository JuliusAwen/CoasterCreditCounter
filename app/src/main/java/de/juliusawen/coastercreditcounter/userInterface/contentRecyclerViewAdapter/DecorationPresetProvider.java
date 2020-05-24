package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.graphics.Typeface;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.GroupHeader;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Model;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

abstract class DecorationPresetProvider
{
    static void applyPreset(Decoration decoration, RequestCode requestCode)
    {
        Log.v(String.format("creating for RequestCode[%s]...", requestCode));

        switch(requestCode)
        {
            case NAVIGATE:
            {
                decoration
                        .addTypefaceForContentType(Park.class, Typeface.BOLD)
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.TOTAL_RIDE_COUNT, DetailDisplayMode.BELOW)
                        .addTypefaceForDetailType(DetailType.STATUS, Typeface.ITALIC);
                break;
            }

            case SHOW_LOCATIONS:
                decoration
                        .addTypefaceForContentType(Location.class, Typeface.BOLD);
                break;

            case SORT_LOCATIONS:
            case SORT_PARKS:
            case SORT_ATTRACTIONS:
            case SORT_CREDIT_TYPES:
            case SORT_CATEGORIES:
            case SORT_MANUFACTURERS:
            case SORT_MODELS:
            case SORT_STATUSES:
                decoration
                        .addTypefaceForContentType(IElement.class, Typeface.BOLD)
                        .addDetailTypesAndModeForContentType(Model.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                        .addDetailTypesAndModeForContentType(Model.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                        .addDetailTypesAndModeForContentType(Model.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                        .addSpecialStringResourceForType(IProperty.class, R.string.substitute_properties_default_postfix);
                break;
        }

        decoration
                .addTypefaceForContentType(GroupHeader.class, Typeface.BOLD)
                .addTypefaceForDetailType(DetailType.STATUS, Typeface.ITALIC);
    }
}
