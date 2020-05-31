package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.util.LinkedHashSet;

import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public abstract class ContentRecyclerViewAdapterConfigurationPresetProvider
{
    public static void applyConfigurationPreset(ContentRecyclerViewAdapterConfiguration configuration, RequestCode requestCode)
    {
        Log.d(String.format("%s...", requestCode));

        LinkedHashSet<ElementType> relevantChildTypes = new LinkedHashSet<>();

        switch(requestCode)
        {
            case NAVIGATE:
            {
                configuration.setSelectable(true);
                configuration.setMultipleSelection(true);
                relevantChildTypes.add(ElementType.IATTRACTION);
                break;
            }

            case SHOW_LOCATIONS:
                relevantChildTypes.add(ElementType.LOCATION);
                relevantChildTypes.add(ElementType.PARK);
                break;

            case SORT_LOCATIONS:
            case SORT_PARKS:
            case SORT_ATTRACTIONS:
            {
                configuration.setSelectable(true);
                configuration.setMultipleSelection(false);
                break;
            }

            case PICK_CREDIT_TYPE:
            case PICK_CATEGORY:
            case PICK_MANUFACTURER:
            case PICK_MODEL:
            case PICK_STATUS:

            case SORT_CREDIT_TYPES:
            case SORT_CATEGORIES:
            case SORT_MANUFACTURERS:
            case SORT_MODELS:
            case SORT_STATUSES:
            {
                configuration.setSelectable(true);
                configuration.setMultipleSelection(false);
                relevantChildTypes.add(ElementType.IPROPERTY);
                break;
            }


            case MANAGE_MODELS:
            {
                relevantChildTypes.add(ElementType.MODEL);
                break;
            }

            case MANAGE_CREDIT_TYPES:
            case MANAGE_CATEGORIES:
            case MANAGE_MANUFACTURERS:
            case MANAGE_STATUSES:
            default:
                Log.v(String.format("no preset found for %s", requestCode));
                break;
        }

        configuration.addRelevantChildTypes(relevantChildTypes);
    }
}
