package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.graphics.Typeface;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.GroupHeader;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Model;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public abstract class ContentRecyclerViewDecorationPresetProvider
{
    public static ContentRecyclerViewDecoration applyPreset(ContentRecyclerViewDecoration decoration, RequestCode requestCode)
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

        return decoration;
    }
}
