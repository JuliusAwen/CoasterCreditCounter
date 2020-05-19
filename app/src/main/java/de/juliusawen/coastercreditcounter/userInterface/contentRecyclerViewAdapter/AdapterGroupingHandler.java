package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

abstract class AdapterGroupingHandler extends AdapterBaseHandler
{
    private boolean isGroupable;
    private GroupType groupType;

    AdapterGroupingHandler(List<IElement> content, Configuration configuration)
    {
        super(content, configuration);
        this.isGroupable = configuration.isGroupable;
        this.groupType = configuration.getGroupType();

        if(this.isGroupable)
        {
            Log.wrap(LogLevel.VERBOSE, String.format(Locale.getDefault(), "instantiated with GroupType[%s]", this.groupType), '=', false);
        }
    }
}
