package de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;

public class ContentRecyclerAdapter extends RecyclerView.Adapter<ContentRecyclerAdapter.ViewHolder>
{
    private RecyclerView recyclerView;

    private Map<Element, List<Element>> childrenByParents;
    private RecyclerOnClickListener.OnClickListener onParentClickListener;
    private RecyclerOnClickListener.OnClickListener onChildClickListener;

    private boolean parentsAreExpandable;

    private Map<Element, View> selectedParentViewsByElement = new HashMap<>();
    private Set<Element> selectedChildren = new HashSet<>();
    private boolean parentsAreSelectable;
    private boolean childrenAreSelectable;
    private boolean selectMultipleParentsIsPossible;
    private boolean selectMultipleChildrenIsPossible;

    private List<Element> expandedParents = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        private boolean isExpanded = false;
        private List<View> childViews = new ArrayList<>();

        ViewHolder(LinearLayout rootLinearLayout)
        {
            super(rootLinearLayout);
        }
    }

    public ContentRecyclerAdapter(GetContentRecyclerAdapterRequest request)
    {
        Log.d(Constants.LOG_TAG, String.format(
                "ContentRecyclerAdapter.Constructor:: instantiating RecyclerAdapter with #[%d] parent elements: " +
                        "has onParentClickListener [%S], has onChildClickListener [%S], " +
                        "parentsAreExpandable [%S], parentsAreSelectable [%S], selectMultipleParentsIsPossible [%S], " +
                        "childrenAreSelectable [%S], selectMultipleChildrenIsPossible [%S]",
                request.childrenByParents.size(),
                request.onParentClickListener != null, request.onChildClickListener != null,
                request.parentsAreExpandable, request.parentsAreSelectable, request.selectMultipleParentsIsPossible,
                request.childrenAreSelectable, request.selectMultipleChildrenIsPossible));

        this.childrenByParents = request.childrenByParents;
        this.onParentClickListener = request.onParentClickListener;
        this.onChildClickListener = request.onChildClickListener;
        this.parentsAreExpandable = request.parentsAreExpandable;
        this.parentsAreSelectable = request.parentsAreSelectable;
        this.childrenAreSelectable = request.childrenAreSelectable;
        this.selectMultipleParentsIsPossible = request.selectMultipleParentsIsPossible;
        this.selectMultipleChildrenIsPossible = request.selectMultipleChildrenIsPossible;

    }

    public void updateDataSet(LinkedHashMap<Element, List<Element>> childrenByParents)
    {
        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.updateDataSet:: updating with #[%d] parents...", childrenByParents.size()));
        this.childrenByParents = childrenByParents;
        notifyDataSetChanged();
        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.updateDataSet:: updated with #[%d] parents", childrenByParents.size()));
    }

    public void expandParent(Element parent)
    {
        if(!expandedParents.contains(parent))
        {
            Log.i(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.expandParent:: expanding %s...", parent));
            this.expandedParents.add(parent);
            notifyDataSetChanged();
        }
        else
        {
            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.expandParent:: %s already expanded", parent));
        }
    }

    public void collapseParent(Element parent)
    {
        if(expandedParents.contains(parent))
        {
            Log.i(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.collapseParent:: collapsing %s...", parent));
            this.expandedParents.remove(parent);
            notifyDataSetChanged();
        }
        else
        {
            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.collapseParent:: %s already collapsed", parent));
        }
    }

    public void smoothScrollToParent(Element parent)
    {
        if(this.childrenByParents.keySet().contains(parent))
        {
            Log.d(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.smoothScrollToElement:: scrolling to %s", parent));
            int position = this.getParentPositioninChildrenByParents(parent);
            this.recyclerView.smoothScrollToPosition(position);
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.smoothScrollToElement:: %s not found", parent));
        }
    }

    public boolean isAllSelected()
    {
        List<Element> compareList = new ArrayList<>();
        for(Element parent : childrenByParents.keySet())
        {
            compareList.add(parent);
            compareList.addAll(this.childrenByParents.get(parent));
        }

        compareList.removeAll(selectedParentViewsByElement.keySet());
        return compareList.isEmpty();
    }

    public void selectAllElements()
    {
        Log.i(Constants.LOG_TAG, "ContentRecyclerAdapter.selectAllParentsAndChildren:: selecting all elements...");
        this.selectedParentViewsByElement.clear();
        for(Element parent : this.childrenByParents.keySet())
        {
            this.selectElement(parent);
            this.selectElements(this.childrenByParents.get(parent));
        }
        notifyDataSetChanged();
    }

    public void selectElements(List<Element> elements)
    {
        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.selectElement:: selecting [%d] elements", elements.size()));

        for(Element element : elements)
        {
            this.selectElement(element);
        }
    }

    public void selectElement(Element element)
    {
        this.selectedParentViewsByElement.put(element, null);

        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.selectElement:: %s selected", element));
    }

    public void deselectAllElements()
    {
        Log.i(Constants.LOG_TAG, "ContentRecyclerAdapter.deselectAllElements:: deselecting all elements...");

        this.selectedParentViewsByElement.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ContentRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LinearLayout rootLinearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_content_item_holder, parent, false);
        return new ViewHolder(rootLinearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position)
    {
        Element parent = (Element) this.childrenByParents.keySet().toArray()[position];
        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.onBindViewHolder:: binding ViewHolder for %s)", parent));

        this.decorateViewHolderParent(viewHolder, parent);
    }

    private void decorateViewHolderParent(ViewHolder viewHolder, Element parent)
    {
        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.decorateViewHolderParent:: decorating ViewHolder for %s)", parent));

        this.removeChildViews(viewHolder);
        this.handleSetParentSelected(viewHolder, parent);
        this.decorateExpandToggle(viewHolder, parent);
        this.createChildViews(viewHolder, parent);
        this.decorateTextViewParent(viewHolder, parent);
    }

    private void removeChildViews(ViewHolder viewHolder)
    {
        LinearLayout linearLayout =  viewHolder.itemView.findViewById(R.id.linearLayoutRecyclerViewContentItemHolder);
        for(View childView : viewHolder.childViews)
        {
            linearLayout.removeView(childView);
        }
    }

    private void handleSetParentSelected(ViewHolder viewHolder, Element parent)
    {
        if(this.selectedParentViewsByElement.containsKey(parent))
        {
            viewHolder.itemView.setSelected(true);
            this.selectedParentViewsByElement.put(parent, viewHolder.itemView);
        }
        else
        {
            viewHolder.itemView.setSelected(false);
        }
    }

    private void decorateTextViewParent(ViewHolder viewHolder, Element parent)
    {
        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.decorateTextViewParent:: decorating TextView for %s...)", parent));

        TextView textViewParent = viewHolder.itemView.findViewById(R.id.textViewRecyclerViewContentParent);

        textViewParent.setText(StringTool.getSpannableString(parent.getName(), Typeface.BOLD));
        textViewParent.setTag(parent);

        RecyclerOnClickListener onParentClickListener = getOnParentClickListener(viewHolder);
        textViewParent.setOnClickListener(onParentClickListener);
        textViewParent.setOnLongClickListener(onParentClickListener);
    }

    private RecyclerOnClickListener getOnParentClickListener(final ViewHolder viewHolder)
    {
        return new RecyclerOnClickListener(viewHolder, new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Element element = (Element) view.getTag();

                if(parentsAreSelectable)
                {
                    handleParentSelection(view, element);
                }

                if(onParentClickListener != null)
                {
                    Log.e(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.getOnParentClickListener:: Parent %s clicked", element));
                    onParentClickListener.onClick(view, viewHolder.getAdapterPosition());
                }
            }

            @Override
            public boolean onLongClick(View view, int position)
            {
                if(onParentClickListener != null)
                {
                    Log.e(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.getOnParentClickListener:: Parent %s long clicked", (Element) view.getTag()));

                    return onParentClickListener.onLongClick(view, viewHolder.getAdapterPosition());
                }
                return true;
            }
        });
    }

    private void decorateExpandToggle(final ViewHolder viewHolder, final Element parent)
    {
        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.decorateExpandToggle:: decorating ExpandToggle for %s...", parent));

        ImageView imageViewExpandToggle = viewHolder.itemView.findViewById(R.id.imageViewRecyclerViewContentItemHolder_ExpandToggle);

        if(this.parentsAreExpandable && !this.childrenByParents.get(parent).isEmpty())
        {
            if(expandedParents.contains(parent))
            {
                viewHolder.isExpanded = true;
                imageViewExpandToggle.setImageDrawable(viewHolder.itemView.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_down));

                Log.d(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.decorateExpandToggle:: %s is <EXPANDED>", parent));
            }
            else
            {
                viewHolder.isExpanded = false;
                imageViewExpandToggle.setImageDrawable(viewHolder.itemView.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_right));

                Log.v(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.decorateExpandToggle:: %s is <COLLAPSED>", parent));
            }

            imageViewExpandToggle.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    handleExpandToggle(viewHolder, parent);
                }
            });
        }
        else
        {
            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.decorateExpandToggle:: hiding ExpandToggle for %s", parent));
            Context context = viewHolder.itemView.getContext();
            imageViewExpandToggle.setImageDrawable(DrawableTool.setTintToColor(context, context.getDrawable(R.drawable.ic_baseline_error_outline), R.color.default_color));

            RecyclerOnClickListener onParentClickListener = this.getOnParentClickListener(viewHolder);
            imageViewExpandToggle.setOnClickListener(onParentClickListener);
            imageViewExpandToggle.setOnLongClickListener(onParentClickListener);

            imageViewExpandToggle.setTag(parent);
        }

        imageViewExpandToggle.setVisibility(View.VISIBLE);
    }

    private void handleExpandToggle(ViewHolder viewHolder, Element parent)
    {
        if(viewHolder.isExpanded)
        {
            this.collapseParent(parent);
        }
        else
        {
            this.expandParent(parent);
            this.smoothScrollToParent(parent);
        }
    }

    private void handleParentSelection(View view, Element parent)
    {
        if(view.isSelected())
        {
            this.selectedParentViewsByElement.remove(parent);
            Log.i(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.handleParentSelection:: parent %s deselected", parent));
        }
        else
        {
            if(this.selectedParentViewsByElement.get(parent) != null)
            {
                this.selectedParentViewsByElement.get(parent).setSelected(false);
            }

            if(!this.selectMultipleParentsIsPossible)
            {
                this.selectedParentViewsByElement.clear();
            }

            this.selectedParentViewsByElement.put(parent, view);
            Log.i(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.handleParentSelection:: parent %s selected", parent));
        }
        view.setSelected(!view.isSelected());
        notifyDataSetChanged();
    }

    private void createChildViews(ViewHolder viewHolder, Element parent)
    {
        if(this.expandedParents.contains(parent))
        {
            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.createChildViews:: creating ChildViews for %s...", parent));

            LinearLayout linearLayout = viewHolder.itemView.findViewById(R.id.linearLayoutRecyclerViewContentItemHolder);
            for(Element child : this.childrenByParents.get(parent))
            {
                Log.v(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.createChildViews:: creating ChildView %s...", child));

                LayoutInflater layoutInflater = (LayoutInflater) viewHolder.itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                RecyclerOnClickListener onChildClickListener = this.getOnChildClickListener(viewHolder);

                TextView textViewChild = (TextView) Objects.requireNonNull(layoutInflater).inflate(R.layout.recycler_view_content_item, linearLayout, false);
                textViewChild.setText(child.getName());
                textViewChild.setTag(child);
                textViewChild.setOnClickListener(onChildClickListener);
                textViewChild.setOnLongClickListener(onChildClickListener);

                if(this.childrenAreSelectable)
                {
                    if(this.selectedChildren.contains(child))
                    {
                        textViewChild.setSelected(true);
                    }
                    else
                    {
                        textViewChild.setSelected(false);
                    }
                }

                linearLayout.addView(textViewChild);
                viewHolder.childViews.add(textViewChild);
            }
        }
    }

    private RecyclerOnClickListener getOnChildClickListener(final ViewHolder viewHolder)
    {

        return new RecyclerOnClickListener(viewHolder, new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Element element = (Element) view.getTag();

                if(childrenAreSelectable)
                {
                    handleChildrenSelection(view, element);
                }

                if(onChildClickListener != null)
                {
                    Log.e(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.getOnChildClickListener:: Child %s clicked", element));
                    onChildClickListener.onClick(view, viewHolder.getAdapterPosition());
                }
            }

            @Override
            public boolean onLongClick(View view, int position)
            {
                if(onChildClickListener != null)
                {
                    Log.e(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.getOnChildClickListener:: Child %s long clicked", (Element) view.getTag()));

                    return onChildClickListener.onLongClick(view, viewHolder.getAdapterPosition());
                }
                return true;
            }
        });
    }

    private void handleChildrenSelection(View view, Element child)
    {
        if(view.isSelected())
        {
            this.selectedChildren.remove(child);
            Log.i(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.handleChildrenSelection:: child %s deselected", child));
        }
        else
        {
            if(!this.selectMultipleChildrenIsPossible)
            {
                this.selectedChildren.clear();
            }

            this.selectedChildren.add(child);
            Log.i(Constants.LOG_TAG, String.format("ContentRecyclerAdapter.handleParentSelection:: child %s selected", child));
        }
        view.setSelected(!view.isSelected());
        notifyDataSetChanged();
    }

    private int getParentPositioninChildrenByParents(Element parent)
    {
        int increment = 0;
        for(Element element : this.childrenByParents.keySet())
        {
            if(element.equals(parent))
            {
                return increment;
            }
            else
            {
                increment++;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount()
    {
        return this.childrenByParents.size();
    }
}