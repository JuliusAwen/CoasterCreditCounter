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
        switch(requestCode)
        {
            case NAVIGATE:
            {
                configuration.isDecorable = true;
                configuration.isExpandable = true;
                configuration.isSelectable = false;
                configuration.isCountable = false;
                configuration.useDedicatedExpansionToggleOnClickListener = true;

                relevantChildTypesInSortOrder.add(IAttraction.class);
                configuration.setRelevantChildTypesInSortOrder(relevantChildTypesInSortOrder);
                break;
            }

            default:
                configuration.isDecorable = false;
                configuration.isExpandable = false;
                configuration.isSelectable = false;
                configuration.isCountable = false;
                configuration.useDedicatedExpansionToggleOnClickListener = false;
                break;
        }
    }
}
