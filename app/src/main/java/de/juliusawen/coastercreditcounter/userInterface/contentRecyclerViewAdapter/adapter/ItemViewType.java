package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter;

import de.juliusawen.coastercreditcounter.tools.logger.Log;

enum ItemViewType
{
    UNDETERMINED,

    ELEMENT,
    VISITED_ATTRACTION,
    BOTTOM_SPACER;

    static ItemViewType getValue(int ordinal)
    {
        if(ItemViewType.values().length >= ordinal)
        {
            return ItemViewType.values()[ordinal];
        }
        else
        {
            Log.e(String.format("ordinal [%s] out of bounds (Enum has [%s] values) - returning [%s]", ordinal, values().length, values()[0]));
            return values()[0];
        }
    }
}
