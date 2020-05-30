package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.util.LinkedHashSet;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Model;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public abstract class ContentRecyclerViewAdapterConfigurationPresetProvider
{
    public static void applyConfigurationPreset(ContentRecyclerViewAdapterConfiguration configuration, RequestCode requestCode)
    {
        Log.d(String.format("RequestCode[%s]...", requestCode));

        LinkedHashSet<Class<? extends IElement>> relevantChildTypes = new LinkedHashSet<>();

        switch(requestCode)
        {
            case NAVIGATE:
            {
                configuration.setSelectable(true);
                configuration.setMultipleSelection(true);
                relevantChildTypes.add(IAttraction.class);
                break;
            }

            case SHOW_LOCATIONS:
                relevantChildTypes.add(Location.class);
                relevantChildTypes.add(Park.class);
                break;

            case SORT_LOCATIONS:
            case SORT_PARKS:
            case SORT_ATTRACTIONS:
            case SORT_CREDIT_TYPES:
            case SORT_CATEGORIES:
            case SORT_MANUFACTURERS:
            case SORT_MODELS:
            case SORT_STATUSES:
            {
                configuration.setSelectable(true);
                configuration.setMultipleSelection(false);
                relevantChildTypes.add(IElement.class);
                break;
            }

            case PICK_CREDIT_TYPE:
            case PICK_CATEGORY:
            case PICK_MANUFACTURER:
            case PICK_MODEL:
            case PICK_STATUS:
            {
                configuration.setSelectable(true);
                configuration.setMultipleSelection(false);
                relevantChildTypes.add(IProperty.class);
                break;
            }

            case MANAGE_CREDIT_TYPES:
            case MANAGE_CATEGORIES:
            case MANAGE_MANUFACTURERS:
            case MANAGE_MODELS:
            case MANAGE_STATUSES:
            {
                relevantChildTypes.add(Model.class);
                break;
            }

            default:
                break;
        }

        configuration.addRelevantChildTypes(relevantChildTypes);
    }
}
