package de.juliusawen.coastercreditcounter.presentation.adapters.expandableLists;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.juliusawen.coastercreditcounter.R;

public class ExpandableListAdapter extends BaseExpandableListAdapter
{

    private Context context;

    private ExpandableListView expandableListView;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListContent;

    public ExpandableListAdapter(
            Context context,
            ExpandableListView expandableListView,
            List<String> expandableListTitle,
            HashMap<String, List<String>> expandableListContent)
    {
        this.context = context;
        this.expandableListView = expandableListView;
        this.expandableListTitle = expandableListTitle;
        this.expandableListContent = expandableListContent;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition)
    {
        return this.expandableListContent.get(this.expandableListTitle.get(listPosition)).get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition)
    {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);

        if (convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(layoutInflater).inflate(R.layout.content_expandable_list_item, this.expandableListView);
        }

        TextView expandedListTextView = convertView.findViewById(R.id.expandedListContentTextViewItem);
        expandedListTextView.setText(expandedListText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition)
    {
        return this.expandableListContent.get(this.expandableListTitle.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition)
    {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount()
    {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition)
    {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        String listTitle = (String) getGroup(listPosition);

        if (convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(layoutInflater).inflate(R.layout.content_expandable_list_group, this.expandableListView);
        }

        TextView listTitleTextView = convertView.findViewById(R.id.expandableListContentTextViewTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition)
    {
        return true;
    }
}