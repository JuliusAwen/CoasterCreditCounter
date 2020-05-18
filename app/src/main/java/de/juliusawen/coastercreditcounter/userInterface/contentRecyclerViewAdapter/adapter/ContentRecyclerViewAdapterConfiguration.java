package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter;

import java.util.LinkedHashSet;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public class ContentRecyclerViewAdapterConfiguration
{
    private ContentRecyclerViewOnClickListener.CustomItemOnClickListener customItemOnClickListener;

    public boolean isDecorable = false;
    public boolean isExpandable = false;
    public boolean isSelectable = false;
    public boolean isCountable = false;
    public boolean useDedicatedExpansionToggleOnClickListener = false;

    private GroupType groupType = GroupType.NONE;

    private ContentRecyclerViewDecoration decoration;

    private LinkedHashSet<Class<? extends IElement>> relevantChildTypesInSortOrder;

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

    public ContentRecyclerViewOnClickListener.CustomItemOnClickListener getCustomItemOnClickListener()
    {
        return this.customItemOnClickListener;
    }

    public void setCustomItemOnClickListener(ContentRecyclerViewOnClickListener.CustomItemOnClickListener customItemOnClickListener)
    {
        this.customItemOnClickListener = customItemOnClickListener;
        Log.v("set");
    }

    public void setRelevantChildTypesInSortOrder(LinkedHashSet<Class<? extends IElement>> relevantChildTypesInSortOrder)
    {
        this.relevantChildTypesInSortOrder = relevantChildTypesInSortOrder;
    }

    public LinkedHashSet<Class<? extends IElement>> getRelevantChildTypesInSortOrder()
    {
        return this.relevantChildTypesInSortOrder;
    }

    public boolean validate(boolean tryFix)
    {
        Log.v("validating...");

        if(this.isSelectable && this.getCustomItemOnClickListener() == null)
        {
            Log.e("CustomItemOnClickListener cannot be NULL when isSelectable");
            if(!tryFix)
            {
                return false;
            }

            this.isSelectable = false;
            Log.w("changed isSelectable to FALSE");
        }

        if(this.useDedicatedExpansionToggleOnClickListener && !this.isExpandable)
        {
            Log.e("isExpandable cannot be FALSE when useDedicatedExpansionOnClickListener");
            if(!tryFix)
            {
                return false;
            }

            this.useDedicatedExpansionToggleOnClickListener = false;
            Log.w("changed useDedicatedExpansionToggleOnClickListener to FALSE");
        }

        if(this.isExpandable && this.getCustomItemOnClickListener() == null && !this.useDedicatedExpansionToggleOnClickListener)
        {
            Log.e("CustomItemOnClickListener cannot be NULL when isExpandable and !useDedicatedExpansionOnClickListener");
            if(!tryFix)
            {
                return false;
            }

            this.useDedicatedExpansionToggleOnClickListener = true;
            Log.w("changed useDedicatedExpansionToggleOnClickListener to TRUE");
        }

        Log.d("valid");
        return true;
    }

    @Override
    public String toString()
    {
        //Todo: add relevantChildTypes
        return String.format(Locale.getDefault(),

                "ContentRecyclerViewConfiguration:\n" +
                        "  isDecorable[%S]\n" +
                        "  isExpandable[%S]\n" +
                        "  isSelectable[%S]\n" +
                        "  isCountable[%S]\n" +
                        "  useDedicatedExpansionOnClickListener[%S]\n" +
                        "  GroupType[%S]\n" +
                        "  %s RecyclerOnClickListener",

                this.isDecorable,
                this.isExpandable,
                this.isSelectable,
                this.isCountable,
                this.useDedicatedExpansionToggleOnClickListener,
                this.groupType,
                this.customItemOnClickListener != null ? "with" : "no"
        );
    }
}
