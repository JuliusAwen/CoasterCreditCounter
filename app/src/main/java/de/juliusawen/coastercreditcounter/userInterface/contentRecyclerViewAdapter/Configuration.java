package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.View;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

public class Configuration
{
    private GroupType groupType = GroupType.NONE;

    boolean hasExternalOnClickListeners = false;
    private final Map<Class<? extends IElement>, View.OnClickListener> onClickListenersByType = new HashMap<>();
    private final Map<Class<? extends IElement>, View.OnLongClickListener> onLongClickListenersByType = new HashMap<>();

    boolean isDecorable = false;
    private Decoration decoration;

    boolean isSelectable = false;
    boolean isMultipleSelection = false;

    boolean isExpandable = false;
    private final LinkedHashSet<Class<? extends IElement>> childTypesToExpand = new LinkedHashSet<>();

    boolean isCountable = false;

    Configuration()
    {
        Log.frame(LogLevel.VERBOSE, "instantiated", '=', true);
    }

    GroupType getGroupType()
    {
        return this.groupType;
    }

    public void setGroupType(GroupType groupType)
    {
        this.groupType = groupType;
        Log.v(String.format("set GroupType[%s]", groupType));
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


    LinkedHashSet<Class<? extends IElement>> getChildTypesToExpand()
    {
        return this.childTypesToExpand;
    }

    public void addchildTypesToExpand(LinkedHashSet<Class<? extends IElement>> childTypesToExpandInSortOrder)
    {
        this.isExpandable = true;
        this.childTypesToExpand.addAll(childTypesToExpandInSortOrder);
        Log.v(String.format(Locale.getDefault(), "added [%d] types to expand", childTypesToExpandInSortOrder.size()));
    }

    public void addChildTypeToExpand(Class<? extends IElement> childTypeToExpand)
    {
        this.isExpandable = true;
        this.childTypesToExpand.add(childTypeToExpand);
        Log.v(String.format("added [%s] as type to expand", childTypeToExpand));
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
            Log.e("isDecorable but no Decoration is set");
            if(!tryFix)
            {
                return false;
            }

            Log.w("setting isDecorable to FALSE");
            this.isDecorable = false;
        }

        if(this.isMultipleSelection && !this.isSelectable)
        {
            Log.e("isMultipleSelection but not isSelectable");
            if(!tryFix)
            {
                return false;
            }

            Log.w("setting isMultipleSelection to FALSE");
            this.isMultipleSelection = false;
        }

        if(this.isExpandable && this.childTypesToExpand.isEmpty())
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
                        "  groupType[%s]\n" +
                        "  isDecorable[%S]\n" +
                        "  isSelectable[%S]" + (this.isSelectable ? String.format(" - isMultipleSelection[%S]", this.isMultipleSelection) :  "\n") +
                        "  isExpandable[%S]" + (this.isExpandable ? String.format(Locale.getDefault(), " - [%d] child types\n", this.childTypesToExpand.size()) : "\n") +
                        "  isCountable[%S]\n",

                this.groupType,
                this.isDecorable,
                this.isSelectable,
                this.isExpandable,
                this.isCountable
        );
    }
}

