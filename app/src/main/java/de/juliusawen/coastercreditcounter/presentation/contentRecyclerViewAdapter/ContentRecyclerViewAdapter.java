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
    private List<Element> content;
    private Class<? extends Element> childType;

    private RecyclerOnClickListener.OnClickListener recyclerOnClickListener;
    private boolean selectMultiple;

    private Set<Element> expandedElements;

    enum ViewType
    {
        PARENT,
        CHILD,
        COUNTABLE_CHILD,
        ITEM_DIVIDER,
    }

    static class ViewHolderParent extends RecyclerView.ViewHolder
    {
        ViewHolderParent(View view)
        {
            super(view);
        }
    }

    static class ViewHolderChild extends RecyclerView.ViewHolder
    {
        ViewHolderChild(View view)
        {
            super(view);
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
        this.initializeExpandedParents(request.initiallyExpandedElements);
        this.initializeParents(request.elements);
    }

    private void initializeExpandedParents(List<Element> initiallyExpandedParents)
    {
        if(this.expandedElements == null)
        {
            this.expandedElements = new HashSet<>();
        }

        if(initiallyExpandedParents != null)
        {
            this.expandedElements.addAll(initiallyExpandedParents);
        }
    }

    private void initializeParents(List<Element> parents)
    {
        Log.e(Constants.LOG_TAG, Constants.LOG_DIVIDER + String.format("ContentRecyclerViewAdapter.initializeParents:: initializing [%d] parents...", parents.size()));

        if(this.content == null)
        {
            this.content = new ArrayList<>();
        }

        for(Element parent : parents)
        {
            this.content.add(parent);
            if(this.expandedElements.contains(parent))
            {
                Log.e(Constants.LOG_TAG, Constants.LOG_DIVIDER + String.format("ContentRecyclerViewAdapter.initializeParents:: expanding parent %s...", parent));
                this.expandElement(parent);
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

        ImageView imageViewExpandToggle = viewHolder.itemView.findViewById(R.id.imageViewRecyclerViewItemExpandableParent);
        this.decorateExpandToggle(imageViewExpandToggle, parent);

        final TextView textView = viewHolder.itemView.findViewById(R.id.textViewRecyclerViewItemExpandableParent);
        textView.setText(parent.getName());
        textView.setTag(parent);
        textView.setOnClickListener(new RecyclerOnClickListener(viewHolder, this.recyclerOnClickListener));
        textView.setOnLongClickListener(new RecyclerOnClickListener(viewHolder, this.recyclerOnClickListener));
    }

    private void decorateExpandToggle(ImageView imageViewExpandToggle, Element parent)
    {
        imageViewExpandToggle.setTag(parent);

        if(!this.adapterType.equals(BASIC) && parent.getChildCountOfType(this.childType) > 0)
        {
            if(this.expandedElements.contains(parent))
            {
                imageViewExpandToggle.setImageDrawable(imageViewExpandToggle.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_down));
            }
            else
            {
                imageViewExpandToggle.setImageDrawable(imageViewExpandToggle.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_right));
            }

            imageViewExpandToggle.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    final Element parent = (Element) view.getTag();

                    if(!expandedElements.contains(parent))
                    {
                        expandElement(parent);
//                        smoothScrollToElement(content.get(content.indexOf(parent) + parent.getChildCountOfType(childType)));

                        view.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                smoothScrollToElement(content.get(content.indexOf(parent) + parent.getChildCountOfType(childType)));
                            }
                        }, 500);
                    }
                    else
                    {
                        collapseElement(parent);
//                        smoothScrollToElement(parent);
                    }
                }
            });
        }
        else
        {
            this.setImagePlaceholder(imageViewExpandToggle);
        }
    }

    private void expandElement(Element parent)
    {
        this.expandedElements.add(parent);
        notifyItemChanged(content.indexOf(parent));

        this.content.addAll(this.content.indexOf(parent) + 1, parent.getChildrenOfType(this.childType));
        notifyItemRangeInserted(content.indexOf(parent) + 1, parent.getChildCountOfType(this.childType));
    }

    private void collapseElement(Element parent)
    {
        this.expandedElements.remove(parent);
        notifyItemChanged(content.indexOf(parent));

        this.content.removeAll(parent.getChildrenOfType(this.childType));
        notifyItemRangeRemoved(content.indexOf(parent) + 1, parent.getChildCountOfType(this.childType));

        this.smoothScrollToElement(parent);
    }

    private void bindViewHolderChild(ViewHolderChild viewHolder, int position)
    {
        Element child = this.content.get(position);

        TextView textView = viewHolder.itemView.findViewById(R.id.textViewRecyclerViewItemChild);
        textView.setText(child.getName());

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






    public void updateDataSet(List<Element> elements)
    {
        this.content.clear();
        this.initializeParents(elements);
        notifyDataSetChanged();
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
