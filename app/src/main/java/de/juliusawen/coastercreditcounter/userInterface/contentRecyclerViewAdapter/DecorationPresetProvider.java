package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.graphics.Typeface;

import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.GroupHeader;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

abstract class DecorationPresetProvider
{
    static Decoration createPresetDecoration(RequestCode requestCode)
    {
        Log.d(String.format("creating for RequestCode[%s]...", requestCode));

        Decoration decoration = new Decoration();

        decoration
                .addTypefaceForContentType(GroupHeader.class, Typeface.BOLD)
                .addTypefaceForDetailType(DetailType.STATUS, Typeface.ITALIC);


        switch(requestCode)
        {
            case NAVIGATE:
            {
                decoration
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                        .addDetailTypesAndModeForContentType(IAttraction.class, DetailType.TOTAL_RIDE_COUNT, DetailDisplayMode.BELOW)
                        .addTypefaceForContentType(Park.class, Typeface.BOLD)
                        .addTypefaceForDetailType(DetailType.STATUS, Typeface.ITALIC);
            }
        }

        return decoration;
    }
}
