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

    private final Map<Class<? extends IElement>, View.OnClickListener> onClickListenersByType = new HashMap<>();
    private final Map<Class<? extends IElement>, View.OnLongClickListener> onLongClickListenersByType = new HashMap<>();

    private Decoration decoration;

    boolean isSelecetable = false;
    boolean isMultipleSelection = false;

    private final LinkedHashSet<Class<? extends IElement>> relevantChildTypes = new LinkedHashSet<>();

    Configuration(Decoration decoration)
    {
        this.decoration = decoration;
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
        this.onClickListenersByType.put(type, onClickListener);
        Log.v(String.format("for [%s]", type.getSimpleName()));
    }

    public void addOnLongClickListenerByType(Class<? extends IElement> type, View.OnLongClickListener onLongClickListener)
    {
        this.onLongClickListenersByType.put(type, onLongClickListener);
        Log.v(String.format("for [%s]", type.getSimpleName()));
    }


    public Decoration getDecoration()
    {
        return this.decoration;
    }

    public void setDecoration(Decoration decoration)
    {
        this.decoration = decoration;
    }


    LinkedHashSet<Class<? extends IElement>> getRelevantChildTypes()
    {
        return this.relevantChildTypes;
    }

    public void addRelevantChildTypes(LinkedHashSet<Class<? extends IElement>> relevantChildTypes)
    {
        this.relevantChildTypes.addAll(relevantChildTypes);
        Log.v(String.format(Locale.getDefault(), "added [%d] relevant child types", relevantChildTypes.size()));
    }

    public void addRelevantChildType(Class<? extends IElement> relevantChildType)
    {
        this.relevantChildTypes.add(relevantChildType);
        Log.v(String.format("added [%s] as relevant child type", relevantChildType));
    }

    @Override
    public String toString()
    {
        StringBuilder childTypesString = new StringBuilder();
        for(Class<? extends IElement> type : this.getRelevantChildTypes())
        {
            childTypesString.append(String.format("        [%s]\n", type.getSimpleName()));
        }

        StringBuilder onClickListenerTypesString = new StringBuilder();
        for(Class<? extends IElement> type : this.onClickListenersByType.keySet())
        {
            onClickListenerTypesString.append(String.format("        [%s]\n", type.getSimpleName()));
        }

        StringBuilder onLongClickListenerTypesString = new StringBuilder();
        for(Class<? extends IElement> type : this.onLongClickListenersByType.keySet())
        {
            onLongClickListenerTypesString.append(String.format("        [%s]\n", type.getSimpleName()));
        }

        return String.format(Locale.getDefault(),
                        "ContentRecyclerViewConfiguration:\n" +
                        "    groupType[%s]\n" +
                        "    isSelectable[%S], isMultipleSelection[%S]\n" +
                        "    [%d] relevant child types\n%s"+
                        "    [%d] types with OnClickListeners\n%s"+
                        "    [%d] types with OnLongClickListeners\n%s",

                this.groupType,
                this.isSelecetable, this.isMultipleSelection,
                this.relevantChildTypes.size(), childTypesString,
                this.onClickListenersByType.size(), onClickListenerTypesString,
                this.onLongClickListenersByType.size(), onLongClickListenerTypesString
        );
    }
}

