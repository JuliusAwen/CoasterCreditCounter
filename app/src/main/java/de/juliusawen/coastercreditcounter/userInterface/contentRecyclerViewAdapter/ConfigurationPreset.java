package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

enum ConfigurationPreset
{
    PLAIN(false, false, false, false, false),
    GROUPABLE(true, false, false, false, false),
    DECORABLE(false, true, false, false, false),
    EXPANDABLE(false, false, true, false, false),
    SELECTABLE(false, false, false, true, false),
    COUNTABLE(false, false, false, false, true),

    DECORABLE_EXPANDABLE(false, true, true, false, false),
    GROUPABLE_DECORABLE_EXPANDABLE(true, true, true, false, false);

    boolean isGroupable;
    boolean isDecorable;
    boolean isExpandable;
    boolean isSelectable;
    boolean isCountable;

    ConfigurationPreset(
            boolean isGroupable,
            boolean isDecorable,
            boolean isExpandable,
            boolean isSelectable,
            boolean isCountable)
    {
        this.isGroupable = isGroupable;
        this.isDecorable = isDecorable;
        this.isExpandable = isExpandable;
        this.isSelectable = isSelectable;
        this.isCountable = isCountable;
    }
}