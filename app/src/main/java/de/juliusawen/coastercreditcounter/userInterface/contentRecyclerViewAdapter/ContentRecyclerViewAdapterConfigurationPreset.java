package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

enum ContentRecyclerViewAdapterConfigurationPreset
{
    PLAIN(false, false, false, false),
    DECORABLE(true, false, false, false),
    EXPANDABLE(false, true, false, false),
    SELECTABLE(false, false, true, false),
    COUNTABLE(false, false, false, true),

    DECORABLE_EXPANDABLE(true, true, false, false);

    boolean isDecorable;
    boolean isExpandable;
    boolean isSelectable;
    boolean isCountable;

    ContentRecyclerViewAdapterConfigurationPreset(
            boolean isDecorable,
            boolean isExpandable,
            boolean isSelectable,
            boolean isCountable)
    {
        this.isDecorable = isDecorable;
        this.isExpandable = isExpandable;
        this.isSelectable = isSelectable;
        this.isCountable = isCountable;
    }
}
