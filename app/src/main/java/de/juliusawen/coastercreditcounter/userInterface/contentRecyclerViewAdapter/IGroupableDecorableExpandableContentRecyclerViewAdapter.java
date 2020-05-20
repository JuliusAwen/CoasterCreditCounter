package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

public interface IGroupableDecorableExpandableContentRecyclerViewAdapter extends IContentRecyclerViewAdapter, IExpandableContentRecyclerViewAdapter
{
    void groupContent(GroupType groupType);
}
