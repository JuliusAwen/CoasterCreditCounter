package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.View;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public class Configuration
{
    boolean isGroupable = false;
    private GroupType groupType = GroupType.NONE;

    boolean hasExternalOnClickListeners = false;
    private final Map<Class<? extends IElement>, View.OnClickListener> onClickListenersByType = new HashMap<>();
    private final Map<Class<? extends IElement>, View.OnLongClickListener> onLongClickListenersByType = new HashMap<>();

    boolean isDecorable = false;
    private Decoration decoration;


    boolean isExpandable = false;
    private final LinkedHashSet<Class<? extends IElement>> childTypesToExpandInSortOrder = new LinkedHashSet<>();


    boolean isSelectable = false;
    boolean isCountable = false;


    GroupType getGroupType()
    {
        return this.groupType;
    }

    public void setGroupType(GroupType groupType)
    {
        this.isGroupable = true;
        this.groupType = groupType;
        Log.v(String.format("GroupType set:\n%s", groupType));
    }


    public Decoration getDecoration()
    {
        return this.decoration != null ? this.decoration : new Decoration();
    }

    public void setDecoration(Decoration decoration)
    {
        this.isDecorable = true;
        this.decoration = decoration;
        Log.v(String.format("\n%s", decoration));
    }


    LinkedHashSet<Class<? extends IElement>> getChildTypesToExpandInSortOrder()
    {
        return this.childTypesToExpandInSortOrder;
    }

    public void addchildTypesToExpandInSortOrder(LinkedHashSet<Class<? extends IElement>> childTypesToExpandInSortOrder)
    {
        this.isExpandable = true;
        this.childTypesToExpandInSortOrder.addAll(childTypesToExpandInSortOrder);
        Log.v(String.format(Locale.getDefault(), "added [%d] Elements", childTypesToExpandInSortOrder.size()));
    }

    public void addChildTypeToExpand(Class<? extends IElement> childTypeToExpand)
    {
        this.isExpandable = true;
        this.childTypesToExpandInSortOrder.add(childTypeToExpand);
        Log.v(String.format("added [%s]", childTypeToExpand));
    }

    Map<Class<? extends IElement>, View.OnClickListener> getOnClickListenersByType()
    {
        return this.onClickListenersByType;
    }

    Map<Class<? extends IElement>, View.OnLongClickListener> getOnLongClickListenersByType()
    {
        return this.onLongClickListenersByType;
    }

    public void addOnClickListenerByType(Class<? extends IElement> type, View.OnClickListener onClickListener)
    {
        this.hasExternalOnClickListeners = true;
        this.onClickListenersByType.put(type, onClickListener);
        Log.v(String.format("for [%s]", type.getSimpleName()));
    }

    public void addOnLongClickListenerByType(Class<? extends IElement> type, View.OnLongClickListener onLongClickListener)
    {
        this.hasExternalOnClickListeners = true;
        this.onLongClickListenersByType.put(type, onLongClickListener);
        Log.v(String.format("for [%s]", type.getSimpleName()));
    }

    public boolean validate(boolean tryFix)
    {
        Log.v("validating...");

        if(this.isDecorable && this.decoration == null)
        {
            Log.e("isDecorable but now Decoration is set");
            if(!tryFix)
            {
                return false;
            }

            Log.w("setting isDecorable to FALSE");
            this.isExpandable = false;
        }

        if(this.isExpandable && this.childTypesToExpandInSortOrder.isEmpty())
        {
            Log.e("isExpandable but no child types to expand are set");
            if(!tryFix)
            {
                return false;
            }

            Log.w("setting isExpandable to FALSE");
            this.isExpandable = false;
        }

        Log.d("valid");
        return true;
    }

    @Override
    public String toString()
    {
        //Todo: add relevantChildTypes and CustomOnClickListeners
        return String.format(Locale.getDefault(),
                "ContentRecyclerViewConfiguration:\n" +
                        "  isGroupable[%S]" + (this.isDecorable ? String.format(" - GroupType[%s]\n", this.groupType) : "\n") +
                        "  isDecorable[%S]" +
                        "  isExpandable[%S]" + (this.isExpandable ? String.format(Locale.getDefault(), " - [%d] child types\n", this.childTypesToExpandInSortOrder.size()) : "\n") +
                        "  isSelectable[%S]\n" +
                        "  isCountable[%S]\n",

                this.isGroupable,
                this.isDecorable,
                this.isExpandable,
                this.isSelectable,
                this.isCountable
        );
    }
}

