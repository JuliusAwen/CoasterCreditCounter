package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.util.LinkedHashSet;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

abstract class ConfigurationPresetProvider
{
    static void applyConfigurationPreset(AdapterConfiguration adapterConfiguration, RequestCode requestCode)
    {
        Log.d(String.format("applying for RequestCode[%s]...", requestCode));

        RecyclerViewDecorationPresetProvider.applyDecorationPreset(adapterConfiguration.getRecyclerViewDecoration(), requestCode);

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

        adapterConfiguration.addchildTypesToExpandInSortOrder(relevantChildTypesInSortOrder);

        adapterConfiguration.isDecorable = configurationPreset.isDecorable;
        adapterConfiguration.isExpandable = configurationPreset.isExpandable;
        adapterConfiguration.isSelectable = configurationPreset.isSelectable;
        adapterConfiguration.isCountable = configurationPreset.isCountable;
    }
}
