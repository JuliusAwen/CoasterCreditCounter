package de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Element;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.AdapterType;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;

public class ContentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private RecyclerView recyclerView;

    private final AdapterType adapterType;
    private final Class<? extends IElement> childType;

    private final View.OnClickListener expansionOnClickListener;
    private final View.OnClickListener selectionOnClickListener;
    private final View.OnClickListener increaseOnClickListener;
    private final View.OnClickListener decreaseOnClickListener;

    private final boolean selectMultiple;

    private final List<IElement> items = new ArrayList<>();

    private final List<IElement> selectedItemsInOrderOfSelection = new ArrayList<>();

    private Set<IElement> expandedParents = new HashSet<>();

    private RecyclerOnClickListener.OnClickListener recyclerOnClickListener;


    enum ViewType
    {
        PARENT,
        CHILD,
        VISITED_ATTRACTION,
        ITEM_DIVIDER,
        BOTTOM_SPACER
    }

    static class ViewHolderParent extends RecyclerView.ViewHolder
    {
        final ImageView imageViewExpandToggle;
        final TextView textViewName;

        ViewHolderParent(View view)
        {
            super(view);
            this.imageViewExpandToggle = view.findViewById(R.id.imageViewRecyclerViewItemExpandableParent);
            this.textViewName = view.findViewById(R.id.textViewRecyclerViewItemExpandableParent);
        }
    }

    static class ViewHolderChild extends RecyclerView.ViewHolder
    {
        final TextView textViewName;

        ViewHolderChild(View view)
        {
            super(view);
            this.textViewName = view.findViewById(R.id.textViewRecyclerViewItemChild);
        }
    }

    static class ViewHolderVisitedAttraction extends RecyclerView.ViewHolder
    {
        final LinearLayout linearLayoutCounter;
        final TextView textViewName;
        final TextView textViewCount;
        final ImageView imageViewDecrease;
        final ImageView imageViewIncrease;
        final FrameLayout frameLayoutDecrease;
        final FrameLayout frameLayoutIncrease;

        ViewHolderVisitedAttraction(View view)
        {
            super(view);

            this.linearLayoutCounter = view.findViewById(R.id.linearLayoutRecyclerViewVisitedAttraction_Counter);
            this.textViewName = view.findViewById(R.id.textViewRecyclerViewItemVisitedAttraction_Name);
            this.textViewCount = view.findViewById(R.id.textViewRecyclerViewItemVisitedAttraction_Count);
            this.imageViewDecrease = view.findViewById(R.id.imageViewRecyclerViewItemVisitedAttraction_Decrease);
            this.imageViewIncrease = view.findViewById(R.id.imageViewRecyclerViewItemVisitedAttraction_Increase);
            this.frameLayoutDecrease = view.findViewById(R.id.FrameLayoutRecyclerViewItemVisitedAttraction_Decrease);
            this.frameLayoutIncrease = view.findViewById(R.id.FrameLayoutRecyclerViewItemVisitedAttraction_Increase);

            this.imageViewDecrease.setImageDrawable(App.getContext().getDrawable(R.drawable.ic_baseline_remove_circle_outline));
            this.imageViewIncrease.setImageDrawable(App.getContext().getDrawable(R.drawable.ic_baseline_add_circle_outline));
        }
    }

    static class ViewHolderItemDivider extends RecyclerView.ViewHolder
    {
        ViewHolderItemDivider(View view)
        {
            super(view);
            view.setClickable(false);
        }
    }

    private class ItemDivider extends OrphanElement
    {
        private ItemDivider()
        {
            super("ItemDivider", UUID.randomUUID());
        }
    }

    static class ViewHolderBottomSpacer extends RecyclerView.ViewHolder
    {
        ViewHolderBottomSpacer(View view)
        {
            super(view);
            view.setClickable(false);
        }
    }

    private class BottomSpacer extends OrphanElement
    {
        private BottomSpacer()
        {
            super("BottomSpacer", UUID.randomUUID());
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

            case VISITED_ATTRACTION:
                view = layoutInflater.inflate(R.layout.recycler_view_item_visited_attraction, viewGroup, false);
                viewHolder = new ViewHolderVisitedAttraction(view);
                break;

            case ITEM_DIVIDER:
                view = layoutInflater.inflate(R.layout.recycler_view_item_divider, viewGroup, false);
                viewHolder = new ViewHolderItemDivider(view);
                break;

            case BOTTOM_SPACER:
                view = layoutInflater.inflate(R.layout.recycler_view_item_bottom_spacer, viewGroup, false);
                viewHolder = new ViewHolderBottomSpacer(view);
                break;

                default:
                    throw new IllegalStateException();
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position)
    {
        IElement item = this.items.get(position);

        if(this.isParent(item))
        {
            return ViewType.PARENT.ordinal();
        }
        else if(this.isChild(item) && !this.adapterType.equals(AdapterType.COUNTABLE))
        {
            return ViewType.CHILD.ordinal();
        }
        else if(this.isChild(item) && this.adapterType.equals(AdapterType.COUNTABLE))
        {
            return ViewType.VISITED_ATTRACTION.ordinal();
        }
        else if(item instanceof ItemDivider)
        {
            return ViewType.ITEM_DIVIDER.ordinal();
        }
        else if(item instanceof BottomSpacer)
        {
            return ViewType.BOTTOM_SPACER.ordinal();
        }

        return -1;
    }

    private boolean isParent(IElement item)
    {
        return !(item instanceof ItemDivider) && (this.childType == null || !this.childType.isInstance(item) && !(item instanceof BottomSpacer));
    }

    private boolean isChild(IElement item)
    {
        return !(item instanceof ItemDivider) && (this.childType != null && (this.childType.isInstance(item) || !(item instanceof BottomSpacer)));
    }

    @Override
    public int getItemCount()
    {
        return this.items.size();
    }

    @Override
    public long getItemId(int position)
    {
        return this.items.get(position).getItemId();
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

            case VISITED_ATTRACTION:
                ViewHolderVisitedAttraction viewHolderVisitedAttraction = (ViewHolderVisitedAttraction) viewHolder;
                this.bindViewHolderVisitedAttraction(viewHolderVisitedAttraction, position);
                break;

            case BOTTOM_SPACER:
            case ITEM_DIVIDER:
                break;

            default:
                throw new IllegalStateException();
        }
    }





    ContentRecyclerViewAdapter(GetContentRecyclerViewAdapterRequest request)
    {
        this.adapterType = request.adapterType;
        this.childType = request.childType;
        this.selectMultiple = request.selectMultiple;

        if(request.initiallyExpandedElements != null)
        {
            this.expandedParents = new HashSet<>(request.initiallyExpandedElements);
        }

        this.initializeItems(request.elements);

        this.expansionOnClickListener = this.getExpansionOnClickListener();
        this.selectionOnClickListener = this.getSelectionOnClickListener();
        this.increaseOnClickListener = this.getIncreaseOnClickListener();
        this.decreaseOnClickListener = this.getDecreaseOnClickListener();
    }

    private void initializeItems(List<IElement> parents)
    {
        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.initializeItems:: initializing [%d] parents...", parents.size()));
        this.items.clear();

        for(IElement parent : parents)
        {
            this.items.add(parent);
            if(this.childType != null && this.expandedParents.contains(parent))
            {
                this.items.addAll(this.items.indexOf(parent) + 1, parent.getChildrenOfType(this.childType));
                Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.initializeItems:: parent %s is expanded - [%d] children added",
                        parent, parent.getChildCountOfType(this.childType)));
            }
            this.items.add(new ItemDivider());
        }
    }

    private View.OnClickListener getExpansionOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final Element parent = (Element) view.getTag();

                if(!expandedParents.contains(parent))
                {
                    expandParent(parent);
                }
                else
                {
                    collapseParent(parent);
                }
            }
        };
    }

    private View.OnClickListener getSelectionOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final IElement selectedItem = (IElement) view.getTag();

                if(!selectedItemsInOrderOfSelection.contains(selectedItem))
                {
                    if(selectMultiple)
                    {
                        selectedItemsInOrderOfSelection.add(selectedItem);
                        notifyItemChanged(items.indexOf(selectedItem));
                        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSelectionOnClickListener.onClick:: %s selected", selectedItem));

                        if(childType != null && isParent(selectedItem))
                        {
                            selectAllChildren(selectedItem);
                        }

                        if(childType != null && isChild(selectedItem) && (selectedItem instanceof Attraction))
                        {
                            AttractionCategoryHeader attractionCategoryHeader =
                                    getAttractionCategoryHeaderForAttractionCategoryFromItems(((Attraction)selectedItem).getAttractionCategory());

                            if(attractionCategoryHeader != null)
                            {
                                List<IElement> children = new ArrayList<>(attractionCategoryHeader.getChildren());

                                for(IElement child : attractionCategoryHeader.getChildren())
                                {
                                    if(selectedItemsInOrderOfSelection.contains(child))
                                    {
                                        children.remove(child);
                                    }
                                }

                                if(children.size() <= 0)
                                {
                                    selectedItemsInOrderOfSelection.add(attractionCategoryHeader);
                                    notifyItemChanged(items.indexOf(attractionCategoryHeader));
                                    Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSelectionOnClickListener.onClick:: %s selected", attractionCategoryHeader));
                                }
                            }
                        }
                    }
                    else
                    {
                        IElement previouslySelectedElement = getLastSelectedItem();

                        if(previouslySelectedElement != null)
                        {
                            selectedItemsInOrderOfSelection.remove(previouslySelectedElement);
                            notifyItemChanged(items.indexOf(previouslySelectedElement));
                            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSelectionOnClickListener.onClick:: %s deselected", previouslySelectedElement));
                        }

                        selectedItemsInOrderOfSelection.add(selectedItem);
                        notifyItemChanged(items.indexOf(selectedItem));
                        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSelectionOnClickListener.onClick:: %s selected", selectedItem));
                    }
                }
                else
                {
                    selectedItemsInOrderOfSelection.remove(selectedItem);
                    notifyItemChanged(items.indexOf(selectedItem));

                    Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSelectionOnClickListener.onClick:: %s deselected", selectedItem));

                    if(childType != null && isParent(selectedItem))
                    {
                        deselectAllChildren(selectedItem);
                    }
                    else if(childType != null && isChild(selectedItem) && Attraction.class.isInstance(selectedItem))
                    {
                        AttractionCategoryHeader attractionCategoryHeader =
                                getAttractionCategoryHeaderForAttractionCategoryFromItems(((Attraction)selectedItem).getAttractionCategory());

                        selectedItemsInOrderOfSelection.remove(attractionCategoryHeader);
                        notifyItemChanged(items.indexOf(attractionCategoryHeader));
                        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSelectionOnClickListener.onClick:: %s deselected", attractionCategoryHeader));
                    }
                }

                if(recyclerOnClickListener != null)
                {
                    recyclerOnClickListener.onClick(view);
                }
            }
        };
    }

    private View.OnClickListener getIncreaseOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                VisitedAttraction visitedAttraction = (VisitedAttraction) view.getTag();

                Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getIncreaseOnClickListener.onClick:: increasing %s's ride count for %s",
                        visitedAttraction.getOnSiteAttraction(), visitedAttraction.getParent()));

                visitedAttraction.increaseRideCount(App.settings.getDefaultIncrement());
                notifyItemChanged(items.indexOf(visitedAttraction));
            }
        };
    }

    private View.OnClickListener getDecreaseOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                VisitedAttraction visitedAttraction = (VisitedAttraction) view.getTag();

                Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getDecreaseOnClickListener.onClick:: decreasing %s's ride count for %s",
                        visitedAttraction.getOnSiteAttraction(), visitedAttraction.getParent()));

                if(visitedAttraction.decreaseRideCount(App.settings.getDefaultIncrement()))
                {
                    notifyItemChanged(items.indexOf(visitedAttraction));
                }
            }
        };
    }

    private AttractionCategoryHeader getAttractionCategoryHeaderForAttractionCategoryFromItems(AttractionCategory attractionCategory)
    {
        for(IElement item : this.items)
        {
            if(item instanceof AttractionCategoryHeader)
            {
                if(((AttractionCategoryHeader)item).getAttractionCategory().equals(attractionCategory))
                {
                    return (AttractionCategoryHeader) item;
                }
            }
        }

        return null;
    }



    private void selectAllChildren(IElement item)
    {
        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.selectAllChildren:: selecting [%d] children of item %s", item.getChildCountOfType(this.childType), item));

        for(IElement child : item.getChildrenOfType(this.childType))
        {
            if(!this.selectedItemsInOrderOfSelection.contains(child))
            {
                this.selectedItemsInOrderOfSelection.add(child);
                if(this.items.contains(child))
                {
                    Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.selectAllChildren:: [%s] selected", child));
                    notifyItemChanged(this.items.indexOf(child));
                }
            }
        }
    }

    private void deselectAllChildren(IElement item)
    {
        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.deselectAllChildren:: deselecting [%d] children of item %s", item.getChildCountOfType(this.childType), item));

        for(IElement child : item.getChildrenOfType(this.childType))
        {
            if(this.selectedItemsInOrderOfSelection.contains(child))
            {
                this.selectedItemsInOrderOfSelection.remove(child);
                if(this.items.contains(child))
                {
                    notifyItemChanged(this.items.indexOf(child));
                    Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.deselectAllChildren:: [%s] deselected", child));
                }
            }
        }
    }






    private void bindViewHolderParent(final ViewHolderParent viewHolder, int position)
    {
        IElement parent = this.items.get(position);
        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.bindViewHolderParent:: binding %s for position [%d]...", parent, position));

        this.decorateExpandToggle(viewHolder, parent);

        String name = parent.getName();
        if(this.childType != null && !this.expandedParents.contains(parent) && parent.hasChildrenOfType(this.childType))
        {
            name += String.format(Locale.getDefault(), " (%d)", parent.getChildCountOfType(this.childType));
        }

        viewHolder.textViewName.setText(name);
        viewHolder.textViewName.setTag(parent);

        viewHolder.itemView.setTag(parent);

        if(this.selectedItemsInOrderOfSelection.contains(parent))
        {
            viewHolder.itemView.setSelected(true);
        }
        else
        {
            viewHolder.itemView.setSelected(false);
        }

        if(this.adapterType == AdapterType.SELECTABLE)
        {
            viewHolder.itemView.setOnClickListener(this.selectionOnClickListener);
        }
        else
        {
            if(this.recyclerOnClickListener != null)
            {
                viewHolder.textViewName.setOnClickListener(new RecyclerOnClickListener(this.recyclerOnClickListener));
                viewHolder.textViewName.setOnLongClickListener(new RecyclerOnClickListener(this.recyclerOnClickListener));
            }
        }
    }

    private void decorateExpandToggle(ViewHolderParent viewHolder, IElement parent)
    {
        viewHolder.imageViewExpandToggle.setTag(parent);

        if(this.childType != null && parent.getChildCountOfType(this.childType) > 0)
        {
            if(this.expandedParents.contains(parent))
            {
                viewHolder.imageViewExpandToggle.setImageDrawable(App.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_down));
            }
            else
            {
                viewHolder.imageViewExpandToggle.setImageDrawable(App.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_right));
            }

            viewHolder.imageViewExpandToggle.setOnClickListener(this.expansionOnClickListener);
        }
        else
        {
            this.setImagePlaceholder(viewHolder.imageViewExpandToggle);
        }
    }

    private void bindViewHolderChild(ViewHolderChild viewHolder, int position)
    {
        IElement child = this.items.get(position);
        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.bindViewHolderChild:: binding %s for position [%d]", child, position));

        viewHolder.textViewName.setText(child.getName());

        if(this.adapterType == AdapterType.SELECTABLE)
        {
            if(this.selectedItemsInOrderOfSelection.contains(child))
            {
                viewHolder.itemView.setSelected(true);
            }
            else
            {
                viewHolder.itemView.setSelected(false);
            }

            viewHolder.itemView.setOnClickListener(this.selectionOnClickListener);
        }
        else
        {
            if(this.recyclerOnClickListener != null)
            {
                viewHolder.itemView.setOnClickListener(new RecyclerOnClickListener(this.recyclerOnClickListener));
                viewHolder.itemView.setOnLongClickListener(new RecyclerOnClickListener(this.recyclerOnClickListener));
            }
        }

        viewHolder.itemView.setTag(child);
    }

    private void bindViewHolderVisitedAttraction(ViewHolderVisitedAttraction viewHolder, int position)
    {
        VisitedAttraction child = (VisitedAttraction) this.items.get(position);
        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.bindViewHolderVisitedAttraction:: binding %s for position [%d]", child, position));

        viewHolder.linearLayoutCounter.setTag(child);
        if(this.recyclerOnClickListener != null)
        {
            viewHolder.linearLayoutCounter.setOnClickListener(new RecyclerOnClickListener(this.recyclerOnClickListener));
            viewHolder.linearLayoutCounter.setOnLongClickListener(new RecyclerOnClickListener(this.recyclerOnClickListener));
        }

        viewHolder.textViewName.setText(child.getName());
        viewHolder.textViewCount.setText(String.valueOf(child.getRideCount()));

        viewHolder.frameLayoutDecrease.setTag(child);
        viewHolder.frameLayoutDecrease.setOnClickListener(this.decreaseOnClickListener);

        viewHolder.frameLayoutIncrease.setTag(child);
        viewHolder.frameLayoutIncrease.setOnClickListener(this.increaseOnClickListener);
    }

    private void setImagePlaceholder(ImageView imageView)
    {
        imageView.setImageDrawable(DrawableTool.getColoredDrawable(R.drawable.ic_baseline_error_outline, R.color.default_color));
    }

    public void expandParent(IElement parent)
    {
        if(!this.expandedParents.contains(parent))
        {
            if(this.childType != null && this.items.contains(parent))
            {
                this.expandedParents.add(parent);
                notifyItemChanged(this.items.indexOf(parent));

                this.items.addAll(this.items.indexOf(parent) + 1, parent.getChildrenOfType(this.childType));
                notifyItemRangeInserted(this.items.indexOf(parent) + 1, parent.getChildCountOfType(this.childType));

                ((LinearLayoutManager)ContentRecyclerViewAdapter.this.getLayoutManager()).scrollToPositionWithOffset(this.items.indexOf(parent), 0);

                Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.expandParent:: " +
                        "expanded parent %s with [%d] children", parent, parent.getChildCountOfType(this.childType)));
            }
        }
        else
        {
            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.expandParent:: parent %s already expanded", parent));
        }
    }

    private void collapseParent(IElement parent)
    {
        if(this.childType != null && this.expandedParents.contains(parent))
        {
            this.expandedParents.remove(parent);
            notifyItemChanged(items.indexOf(parent));

            this.items.removeAll(parent.getChildrenOfType(this.childType));
            notifyItemRangeRemoved(items.indexOf(parent) + 1, parent.getChildCountOfType(this.childType));

            this.recyclerView.scrollToPosition(items.indexOf(parent));

            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.collapseParent:: collapsed parent %s", parent));
        }
    }

    public void toggleExpansion(IElement parent)
    {
        if(this.childType != null)
        {
            if(!this.expandedParents.contains(parent))
            {
                this.expandParent(parent);
            }
            else
            {
                this.collapseParent(parent);
            }
        }
    }

    public List<IElement> getItems()
    {
        List<IElement> content = new ArrayList<>();
        for(IElement item : this.items)
        {
            if(!(item instanceof ItemDivider) && !(item instanceof BottomSpacer))
            {
                content.add(item);
            }
        }

        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getItems:: returning [%d] elements", content.size()));
        return content;
    }

    public void swapItems(IElement item1, IElement item2)
    {
        int index1 = this.items.indexOf(item1);
        int index2 = this.items.indexOf(item2);

        Collections.swap(this.items, index1, index2);
        notifyItemMoved(index1, index2);

        this.scrollToItem(item1);
    }

    public boolean isAllSelected()
    {
        List<IElement> content = new ArrayList<>(this.getItems());
        content.removeAll(this.selectedItemsInOrderOfSelection);
        return content.size() <= 0;
    }

    public void selectAllItems()
    {
        Log.i(Constants.LOG_TAG, "ContentRecyclerViewAdapter.selectAllItems:: selecting all elements...");

        this.selectedItemsInOrderOfSelection.clear();

        for(IElement item : this.getItems())
        {
            this.selectedItemsInOrderOfSelection.add(item);
            notifyItemChanged(this.items.indexOf(item));
            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.selectAllItems:: %s parent selected", item));

            if(this.childType != null && this.isParent(item) && !this.expandedParents.contains(item))
            {
                this.selectedItemsInOrderOfSelection.addAll(item.getChildrenOfType(childType));
                Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.selectAllItems:: selected [%d] children of %s", item.getChildCountOfType(childType), item));
            }
        }
    }

    public void deselectAllItems()
    {
        Log.i(Constants.LOG_TAG, "ContentRecyclerViewAdapter.deselectAllItems:: deselecting all elements...");
        this.selectedItemsInOrderOfSelection.clear();
        notifyDataSetChanged();
    }

    public List<IElement> getSelectedItemsInOrderOfSelection()
    {
        return this.selectedItemsInOrderOfSelection;
    }

    public IElement getLastSelectedItem()
    {
        if(!this.selectedItemsInOrderOfSelection.isEmpty())
        {
            return this.selectedItemsInOrderOfSelection.get(0);
        }

        return null;
    }

    public void updateItems(List<IElement> items)
    {
        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.updateItems:: updating with [%d] items...", items.size()));
        this.initializeItems(items);
    }


    public void setOnClickListener(RecyclerOnClickListener.OnClickListener onClickListener)
    {
        this.recyclerOnClickListener = onClickListener;
    }

    public void scrollToItem(IElement item)
    {
        if(item instanceof AttractionCategory)
        {
            for(IElement element : this.items)
            {
                if(element instanceof AttractionCategoryHeader)
                {
                    if(((AttractionCategoryHeader) element).getAttractionCategory().equals(item))
                    {
                        item = element;
                        break;
                    }
                }
            }
        }

        if(this.items.contains(item) && this.recyclerView != null)
        {
            recyclerView.scrollToPosition(items.indexOf(item));
            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.scrollToItem:: scrolled to [%s]", item));
        }
    }

    public void addBottomSpacer()
    {
        if(!this.items.isEmpty() && !(this.items.get(this.items.size() - 1) instanceof BottomSpacer))
        {
            this.items.add(new BottomSpacer());
            notifyItemInserted(this.items.size() - 1);
            Log.v(Constants.LOG_TAG, "ContentRecyclerViewAdapter.addBottomSpacer:: added BottomSpacer");
        }
    }

    public RecyclerView.LayoutManager getLayoutManager()
    {
        return this.recyclerView.getLayoutManager();
    }
}
