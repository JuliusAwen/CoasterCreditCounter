package de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.AdapterType;

class GetContentRecyclerViewAdapterRequest
{
    AdapterType adapterType;

    List<IElement> elements = new ArrayList<>();

    Set<Class<? extends IElement>> relevantChildTypes = new HashSet<>();

    boolean selectMultiple = false;

    int groupType = Constants.TYPE_NONE;
}
