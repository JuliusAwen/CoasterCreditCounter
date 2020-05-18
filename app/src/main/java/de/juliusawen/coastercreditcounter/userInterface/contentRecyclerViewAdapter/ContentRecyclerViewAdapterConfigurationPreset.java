package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

enum ContentRecyclerViewAdapterConfigurationPreset
{
    PLAIN(false, false, false, false, false),
    DECORABLE(true, false, false, false, false),
    EXPANDABLE(false, true, false, false, false),
    SELECTABLE(false, false, true, false, false),
    COUNTABLE(false, false, false, true, false),

    DECORABLE_EXPANDABLE(true, true, false, false, false);

    boolean isDecorable;
    boolean isExpandable;
    boolean isSelectable;
    boolean isCountable;
    boolean useDedicatedExpansionToggleOnClickListener;

    ContentRecyclerViewAdapterConfigurationPreset(
            boolean isDecorable,
            boolean isExpandable,
            boolean isSelectable,
            boolean isCountable,
            boolean useDedicatedExpansionToggleOnClickListener)
    {
        this.isDecorable = isDecorable;
        this.isExpandable = isExpandable;
        this.isSelectable = isSelectable;
        this.isCountable = isCountable;
        this.useDedicatedExpansionToggleOnClickListener = useDedicatedExpansionToggleOnClickListener;
    }
}
