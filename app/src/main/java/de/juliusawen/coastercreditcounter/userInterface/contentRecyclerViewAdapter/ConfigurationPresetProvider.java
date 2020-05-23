package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.util.LinkedHashSet;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

abstract class ConfigurationPresetProvider
{
    static Configuration createPresetConfiguration(RequestCode requestCode)
    {
        Log.d(String.format("presetting Configuration for RequestCode[%s]...", requestCode));

        Configuration configuration = new Configuration();
        LinkedHashSet<Class<? extends IElement>> relevantChildTypesInSortOrder = new LinkedHashSet<>();

        switch(requestCode)
        {
            case NAVIGATE:
            {
                configuration.isDecorable = true;
                configuration.isSelectable = true;
                configuration.isMultipleSelection = true;
                configuration.isExpandable = true;

                relevantChildTypesInSortOrder.add(IAttraction.class);
                break;
            }

            default:
                break;
        }

        configuration.addchildTypesToExpand(relevantChildTypesInSortOrder);


        return configuration;
    }
}
