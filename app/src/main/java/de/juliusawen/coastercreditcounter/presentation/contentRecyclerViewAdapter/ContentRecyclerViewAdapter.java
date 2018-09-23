package de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.AdapterType;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;

import static de.juliusawen.coastercreditcounter.globals.enums.AdapterType.BASIC;
import static de.juliusawen.coastercreditcounter.globals.enums.AdapterType.COUNTABLE;

public class ContentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private RecyclerView recyclerView;

    private AdapterType adapterType;

    private List<Element> content = new ArrayList<>();
    private Set<Element> expandedParents = new HashSet<>();

    private Class<? extends Element> childType;

    private RecyclerOnClickListener.OnClickListener recyclerOnClickListener;
    private boolean selectMultiple;


    enum ViewType
    {
        PARENT,
        CHILD,
        COUNTABLE_CHILD,
        ITEM_DIVIDER,
    }

    static class ViewHolderParent extends RecyclerView.ViewHolder
    {
        ImageView imageViewExpandToggle;
        TextView textViewName;

        ViewHolderParent(View view)
        {
            super(view);
            this.imageViewExpandToggle = view.findViewById(R.id.imageViewRecyclerViewItemExpandableParent);
            this.textViewName = view.findViewById(R.id.textViewRecyclerViewItemExpandableParent);
        }
    }

    static class ViewHolderChild extends RecyclerView.ViewHolder
    {
        TextView textViewName;

        ViewHolderChild(View view)
        {
            super(view);
            this.textViewName = view.findViewById(R.id.textViewRecyclerViewItemChild);
        }
    }

    static class ViewHolderCountableChild extends RecyclerView.ViewHolder
    {
        ViewHolderCountableChild(View view)
        {
            super(view);
        }
    }

    static class ViewHolderItemDivider extends RecyclerView.ViewHolder
    {
        ViewHolderItemDivider(View view)
        {
            super(view);
        }
    }

    private class ItemDivider extends OrphanElement
    {
        private ItemDivider()
        {
            super("ItemDivider", UUID.randomUUID());
        }
    }



    ContentRecyclerViewAdapter(GetContentRecyclerViewAdapterRequest request)
    {
        this.adapterType = request.adapterType;
        this.childType = request.childType;
        this.recyclerOnClickListener = request.onClickListener;
        this.selectMultiple = request.selectMultiple;
        this.expandedParents = request.initiallyExpandedElements;
        this.initializeParents(request.elements);
    }

    private void initializeParents(List<Element> parents)
    {
        Log.e(Constants.LOG_TAG, Constants.LOG_DIVIDER + String.format("ContentRecyclerViewAdapter.initializeParents:: initializing [%d] parents...", parents.size()));

        for(Element parent : parents)
        {
            this.content.add(parent);
            if(this.expandedParents.contains(parent))
            {
                this.expandParent(parent);
            }
            this.content.add(new ItemDivider());
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int typeOfView)
    {
        RecyclerView.ViewHolder viewHolder;

        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());

        ViewType viewType = ViewType.values()[typeOfView];
        switch (viewType)
        {
            case PARENT:
                view = layoutInflater.inflate(R.layout.recycler_view_item_parent, viewGroup, false);
                viewHolder = new ViewHolderParent(view);
                break;

            case CHILD:
                view = layoutInflater.inflate(R.layout.recycler_view_item_child, viewGroup, false);
                viewHolder = new ViewHolderChild(view);
                break;

//            case COUNTABLE_CHILD:
//                view = layoutInflater.inflate(R.layout.recycler_view_content_item_holder, viewGroup, false);
//                viewHolder = new ViewHolderCountableChild(view);
//                break;

            case ITEM_DIVIDER:
                view = layoutInflater.inflate(R.layout.recycler_view_item_divider, viewGroup, false);
                viewHolder = new ViewHolderItemDivider(view);
                break;

                default:
                    throw new IllegalStateException();
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position)
    {
        Element element = this.content.get(position);

        if(this.isParent(element))
        {
            return ViewType.PARENT.ordinal();
        }
        else if(this.isChild(element) && !this.adapterType.equals(COUNTABLE))
        {
            return ViewType.CHILD.ordinal();
        }
        else if(this.isChild(element) && this.adapterType.equals(COUNTABLE))
        {
            return ViewType.COUNTABLE_CHILD.ordinal();
        }
        else if(element.isInstance(ItemDivider.class))
        {
            return ViewType.ITEM_DIVIDER.ordinal();
        }

        return -1;
    }

    private boolean isParent(Element element)
    {
        return !element.isInstance(ItemDivider.class) && (this.childType == null || !element.isInstance(this.childType));
    }

    private boolean isChild(Element element)
    {
        return !element.isInstance(ItemDivider.class) && (this.childType != null && element.isInstance(this.childType));
    }

    @Override
    public int getItemCount()
    {
        return this.content.size();
    }

    @Override
    public long getItemId(int position)
    {
        return this.content.get(position).getItemId();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position)
    {
        ViewType viewType = ViewType.values()[viewHolder.getItemViewType()];
        switch (viewType)
        {
            case PARENT:
                ViewHolderParent viewHolderParent = (ViewHolderParent) viewHolder;
                this.bindViewHolderParent(viewHolderParent, position);
                break;

            case CHILD:
                ViewHolderChild viewHolderChild = (ViewHolderChild) viewHolder;
                this.bindViewHolderChild(viewHolderChild, position);
                break;

            case COUNTABLE_CHILD:
                ViewHolderCountableChild viewHolderCountableChild = (ViewHolderCountableChild) viewHolder;
                this.bindViewHolderCountableChild(viewHolderCountableChild, position);
                break;

            case ITEM_DIVIDER:
                break;

            default:
                throw new IllegalStateException();
        }
    }





    private void bindViewHolderParent(final ViewHolderParent viewHolder, int position)
    {
        Element parent = this.content.get(position);

        this.decorateExpandToggle(viewHolder, parent);

        viewHolder.textViewName.setText(parent.getName());
        viewHolder.textViewName.setTag(parent);
        viewHolder.textViewName.setOnClickListener(new RecyclerOnClickListener(viewHolder, this.recyclerOnClickListener));
        viewHolder.textViewName.setOnLongClickListener(new RecyclerOnClickListener(viewHolder, this.recyclerOnClickListener));
    }

    private void decorateExpandToggle(ViewHolderParent viewHolder,  Element parent)
    {
        viewHolder.imageViewExpandToggle.setTag(parent);

        if(!this.adapterType.equals(BASIC) && parent.getChildCountOfType(this.childType) > 0)
        {
            if(this.expandedParents.contains(parent))
            {
                viewHolder.imageViewExpandToggle.setImageDrawable(viewHolder.imageViewExpandToggle.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_down));
            }
            else
            {
                viewHolder.imageViewExpandToggle.setImageDrawable(viewHolder.imageViewExpandToggle.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_right));
            }

            viewHolder.imageViewExpandToggle.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    final Element parent = (Element) view.getTag();

                    if(!expandedParents.contains(parent))
                    {
                        expandParent(parent);
//                        recyclerView.smoothScrollToPosition(content.indexOf(parent) + parent.getChildCountOfType(childType) + 1);
                    }
                    else
                    {
                        collapseParent(parent);
//                        smoothScrollToElement(parent);
                    }
                }
            });
        }
        else
        {
            this.setImagePlaceholder(viewHolder.imageViewExpandToggle);
        }
    }

    private void expandParent(Element parent)
    {
        if(this.content.contains(parent))
        {
            this.expandedParents.add(parent);
            notifyItemChanged(this.content.indexOf(parent));

            this.content.addAll(this.content.indexOf(parent) + 1, parent.getChildrenOfType(this.childType));
            notifyItemRangeChanged(this.content.indexOf(parent) + 1, parent.getChildCountOfType(this.childType));
        }
    }

    private void collapseParent(Element parent)
    {
        if(this.expandedParents.contains(parent))
        {
            this.expandedParents.remove(parent);
            notifyItemChanged(content.indexOf(parent));

            this.content.removeAll(parent.getChildrenOfType(this.childType));
            notifyItemRangeRemoved(content.indexOf(parent) + 1, parent.getChildCountOfType(this.childType));
        }
    }

    private void bindViewHolderChild(ViewHolderChild viewHolder, int position)
    {
        Element child = this.content.get(position);

        viewHolder.textViewName.setText(child.getName());

        viewHolder.itemView.setOnClickListener(new RecyclerOnClickListener(viewHolder, this.recyclerOnClickListener));
        viewHolder.itemView.setOnLongClickListener(new RecyclerOnClickListener(viewHolder, this.recyclerOnClickListener));
        viewHolder.itemView.setTag(child);
    }

    private void bindViewHolderCountableChild(ViewHolderCountableChild viewHolder, int position)
    {

    }

    private void setImagePlaceholder(ImageView imageView)
    {
        imageView.setImageDrawable(DrawableTool.setTintToColor(imageView.getContext(), imageView.getContext().getDrawable(R.drawable.ic_baseline_error_outline), R.color.default_color));
    }




    public List<Element> getContent()
    {
        List<Element> content = new ArrayList<>();
        for(Element element : this.content)
        {
            if(!element.isInstance(ItemDivider.class))
            {
                content.add(element);
            }
        }

        return content;
    }

    public void updateContent(List<Element> elements)
    {
        Log.e(Constants.LOG_TAG, Constants.LOG_DIVIDER + String.format("ContentRecyclerViewAdapter.updateContent:: updating with [%d] elements...", elements.size()));

        this.content.clear();
        this.initializeParents(elements);
    }

    public Set<Element> getExpandedElements()
    {
        return this.expandedParents;
    }

    public void expandElements(List<Element> elements)
    {
        for(Element element : elements)
        {
            this.expandElement(element);
        }
    }

    public void expandElement(Element element)
    {
        if(!this.expandedParents.contains(element))
        {
            this.expandParent(element);
        }
    }

    public void smoothScrollToElement(Element element)
    {
        if(this.content.contains(element) && this.recyclerView != null)
        {
            recyclerView.smoothScrollToPosition(content.indexOf(element));
        }
    }

    public RecyclerView.LayoutManager getLayoutManager()
    {
        return this.recyclerView.getLayoutManager();
    }
}
