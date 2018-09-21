package de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter;

import java.util.LinkedHashMap;
import java.util.List;

import de.juliusawen.coastercreditcounter.data.elements.Element;

public class GetContentRecyclerAdapterRequest
{
    public LinkedHashMap<Element, List<Element>> childrenByParents;

    public RecyclerOnClickListener.OnClickListener onParentClickListener;
    public RecyclerOnClickListener.OnClickListener onChildClickListener;

    public boolean parentsAreExpandable;
    public boolean parentsAreSelectable;
    public boolean selectMultipleParentsIsPossible;

    public boolean childrenAreSelectable;
    public boolean selectMultipleChildrenIsPossible;
}
