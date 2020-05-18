package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter;

import android.view.View;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public class ContentRecyclerViewAdapterConfiguration
{
    public boolean isDecorable = false;
    public boolean isExpandable = false;
    public boolean isSelectable = false;
    public boolean isCountable = false;

    private GroupType groupType = GroupType.NONE;

    private ContentRecyclerViewDecoration decoration;

    private final LinkedHashSet<Class<? extends IElement>> childTypesToExpandInSortOrder = new LinkedHashSet<>();

    private final Map<Class<? extends IElement>, View.OnClickListener> onClickListenersByType = new HashMap<>();
    private final Map<Class<? extends IElement>, View.OnLongClickListener> onLongClickListenersByType = new HashMap<>();

    public ContentRecyclerViewAdapterConfiguration(ContentRecyclerViewDecoration decoration)
    {
        this.decoration = decoration;
        Log.v("instantiated");
    }

    public ContentRecyclerViewDecoration getDecoration()
    {
        return this.decoration;
    }

    public void setDecoration(ContentRecyclerViewDecoration decoration)
    {
        this.decoration = decoration;
        Log.v(String.format("Decoration set:\n%s", decoration));
    }

    public Map<Class<? extends IElement>, View.OnClickListener> getOnClickListenersByType()
    {
        return this.onClickListenersByType;
    }

    public Map<Class<? extends IElement>, View.OnLongClickListener> getOnLongClickListenersByType()
    {
        return this.onLongClickListenersByType;
    }

    public void addOnClickListenerByType(Class<? extends IElement> type, View.OnClickListener onClickListener)
    {
        this.onClickListenersByType.put(type, onClickListener);
        Log.v(String.format("for [%s]", type.getSimpleName()));
    }

    public void addOnLongClickListenerByType(Class<? extends IElement> type, View.OnLongClickListener onLongClickListener)
    {
        this.onLongClickListenersByType.put(type, onLongClickListener);
        Log.v(String.format("for [%s]", type.getSimpleName()));
    }

    public void addchildTypesToExpandInSortOrder(LinkedHashSet<Class<? extends IElement>> childTypesToExpandInSortOrder)
    {
        this.childTypesToExpandInSortOrder.addAll(childTypesToExpandInSortOrder);
        Log.v(String.format(Locale.getDefault(), "added [%d] Elements", childTypesToExpandInSortOrder.size()));
    }

    public void addChildTypeToExpand(Class<? extends IElement> childTypeToExpand)
    {
        this.childTypesToExpandInSortOrder.add(childTypeToExpand);
        Log.v(String.format("added [%s]", childTypeToExpand));
    }

    public LinkedHashSet<Class<? extends IElement>> getChildTypesToExpandInSortOrder()
    {
        return this.childTypesToExpandInSortOrder;
    }

    public boolean validate(boolean tryFix)
    {
        Log.v("validating...");

        Log.d("valid");
        return true;
    }

    @Override
    public String toString()
    {
        //Todo: add relevantChildTypes and CustomOnClickListeners
        return String.format(Locale.getDefault(),

                "ContentRecyclerViewConfiguration:\n" +
                        "  isDecorable[%S]\n" +
                        "  isExpandable[%S]\n" +
                        "  isSelectable[%S]\n" +
                        "  isCountable[%S]\n" +
                        "  GroupType[%S]",

                this.isDecorable,
                this.isExpandable,
                this.isSelectable,
                this.isCountable,
                this.groupType
        );
    }
}

