package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.util.LinkedHashSet;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter.ContentRecyclerViewAdapterConfiguration;

abstract class ContentRecyclerViewAdapterConfigurationPresetProvider
{
    static void applyConfigurationPreset(ContentRecyclerViewAdapterConfiguration configuration, RequestCode requestCode)
    {
        Log.d(String.format("applying for RequestCode[%s]...", requestCode));

        ContentRecyclerViewDecorationPresetProvider.applyDecorationPreset(configuration.getDecoration(), requestCode);

        LinkedHashSet<Class<? extends IElement>> relevantChildTypesInSortOrder = new LinkedHashSet<>();
        ContentRecyclerViewAdapterConfigurationPreset configurationPreset;
        switch(requestCode)
        {
            case NAVIGATE:
            {
                configurationPreset = ContentRecyclerViewAdapterConfigurationPreset.DECORABLE_EXPANDABLE;

                relevantChildTypesInSortOrder.add(IAttraction.class);
                break;
            }

            default:
                configurationPreset = ContentRecyclerViewAdapterConfigurationPreset.PLAIN;
                break;
        }

        configuration.addchildTypesToExpandInSortOrder(relevantChildTypesInSortOrder);

        configuration.isDecorable = configurationPreset.isDecorable;
        configuration.isExpandable = configurationPreset.isExpandable;
        configuration.isSelectable = configurationPreset.isSelectable;
        configuration.isCountable = configurationPreset.isCountable;
    }
}
