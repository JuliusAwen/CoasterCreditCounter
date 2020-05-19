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
        Log.d(String.format("creating for RequestCode[%s]...", requestCode));

        Configuration configuration = new Configuration();
        LinkedHashSet<Class<? extends IElement>> relevantChildTypesInSortOrder = new LinkedHashSet<>();
        ConfigurationPreset configurationPreset;

        switch(requestCode)
        {
            case NAVIGATE:
            {
                configurationPreset = ConfigurationPreset.DECORABLE_EXPANDABLE;

                relevantChildTypesInSortOrder.add(IAttraction.class);
                break;
            }

            default:
                configurationPreset = ConfigurationPreset.PLAIN;
                break;
        }

        configuration.addchildTypesToExpandInSortOrder(relevantChildTypesInSortOrder);

        configuration.isGroupable = configurationPreset.isGroupable;
        configuration.isDecorable = configurationPreset.isDecorable;
        configuration.isExpandable = configurationPreset.isExpandable;
        configuration.isSelectable = configurationPreset.isSelectable;
        configuration.isCountable = configurationPreset.isCountable;

        return configuration;
    }
}
