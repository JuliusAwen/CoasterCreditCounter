package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

abstract class ConfigurationPresetProvider
{
    static void applyPreset(Configuration configuration, RequestCode requestCode)
    {
        Log.d(String.format("presetting Configuration for RequestCode[%s]...", requestCode));

        switch(requestCode)
        {
            case NAVIGATE:
            {
                configuration.isSelecetable = true;
                configuration.isMultipleSelection = true;
                configuration.addRelevantChildType(IAttraction.class);
                break;
            }

            case SHOW_LOCATIONS:
                configuration.addRelevantChildType(Location.class);
                configuration.addRelevantChildType(Park.class);
                break;

            case SORT_LOCATIONS:
            case SORT_PARKS:
            case SORT_ATTRACTIONS:
            case SORT_CREDIT_TYPES:
            case SORT_CATEGORIES:
            case SORT_MANUFACTURERS:
            case SORT_MODELS:
            case SORT_STATUSES:
                configuration.addRelevantChildType(IElement.class);
                configuration.isSelecetable = true;
                break;

            default:
                break;
        }
    }
}
