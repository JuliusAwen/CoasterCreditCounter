package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

abstract class ConfigurationPresetProvider
{
    static Configuration applyPreset(Configuration configuration, RequestCode requestCode)
    {
        Log.d(String.format("presetting Configuration for RequestCode[%s]...", requestCode));

        switch(requestCode)
        {
            case NAVIGATE:
            {
                configuration.isDecorable = true;
                configuration.isSelectable = true;
                configuration.isMultipleSelection = true;
                configuration.isExpandable = true;

                configuration.addChildTypeToExpand(IAttraction.class);
                break;
            }

            case SHOW_LOCATIONS:
                configuration.isDecorable = true;
                configuration.isExpandable = true;

                configuration.addChildTypeToExpand(Location.class);
                configuration.addChildTypeToExpand(Park.class);
                break;

            default:
                break;
        }

        return configuration;
    }
}
