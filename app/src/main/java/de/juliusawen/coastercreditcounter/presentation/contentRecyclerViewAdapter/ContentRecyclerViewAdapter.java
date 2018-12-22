package de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter;

import android.content.Context;
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
import de.juliusawen.coastercreditcounter.data.attractions.Attraction;
import de.juliusawen.coastercreditcounter.data.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.IElement;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.data.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.AdapterType;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;

public class ContentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private RecyclerView recyclerView;

    private AdapterType adapterType;

    private List<IElement> content = new ArrayList<>();

    private Set<IElement> expandedParents = new HashSet<>();
    private View.OnClickListener expansionOnClickListener;

    private List<IElement> selectedElementsInOrderOfSelection = new ArrayList<>();
    private View.OnClickListener selectionOnClickListener;

    private View.OnClickListener increaseOnClickListener;
    private View.OnClickListener decreaseOnClickListener;

    private Class<? extends IElement> childType;

    private RecyclerOnClickListener.OnClickListener recyclerOnClickListener;

    private boolean selectMultiple;


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

    static class ViewHolderVisitedAttraction extends RecyclerView.ViewHolder
    {
        LinearLayout linearLayoutCounter;
        TextView textViewName;
        TextView textViewCount;
        ImageView imageViewDecrease;
        ImageView imageViewIncrease;
        FrameLayout frameLayoutDecrease;
        FrameLayout frameLayoutIncrease;

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
        IElement item = this.content.get(position);

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
        else if(ItemDivider.class.isInstance(item))
        {
            return ViewType.ITEM_DIVIDER.ordinal();
        }
        else if(BottomSpacer.class.isInstance(item))
        {
            return ViewType.BOTTOM_SPACER.ordinal();
        }

        return -1;
    }

    private boolean isParent(IElement element)
    {
        return !ItemDivider.class.isInstance(element) && (this.childType == null || !this.childType.isInstance(element) && !BottomSpacer.class.isInstance(element));
    }

    private boolean isChild(IElement element)
    {
        return !ItemDivider.class.isInstance(element) && (this.childType != null && (this.childType.isInstance(element) || !BottomSpacer.class.isInstance(element)));
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

        this.initializeContent(request.elements);

        this.expansionOnClickListener = this.getExpansionOnClickListener();
        this.selectionOnClickListener = this.getSelectionOnClickListener();
        this.increaseOnClickListener = this.getIncreaseOnClickListener();
        this.decreaseOnClickListener = this.getDecreaseOnClickListener();
    }

    private void initializeContent(List<IElement> parents)
    {
        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.initializeContent:: initializing [%d] parents...", parents.size()));
        this.content.clear();

        for(IElement parent : parents)
        {
            this.content.add(parent);
            if(this.childType != null && this.expandedParents.contains(parent))
            {
                this.content.addAll(this.content.indexOf(parent) + 1, parent.getChildrenOfType(this.childType));
                Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.initializeContent:: parent %s is expanded - [%d] children added",
                        parent, parent.getChildCountOfType(this.childType)));
            }
            this.content.add(new ItemDivider());
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
                final Element selectedElement = (Element) view.getTag();

                if(!selectedElementsInOrderOfSelection.contains(selectedElement))
                {
                    if(selectMultiple)
                    {
                        selectedElementsInOrderOfSelection.add(selectedElement);
                        notifyItemChanged(content.indexOf(selectedElement));
                        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSelectionOnClickListener.onClick:: %s selected", selectedElement));

                        if(childType != null && isParent(selectedElement))
                        {
                            selectAllChildren(selectedElement);
                        }

                        if(childType != null && isChild(selectedElement) && Attraction.class.isInstance(selectedElement))
                        {
                            AttractionCategoryHeader attractionCategoryHeader =
                                    getAttractionCategoryHeaderForAttractionCategoryFromContent(content, ((Attraction)selectedElement).getAttractionCategory());

                            if(attractionCategoryHeader != null)
                            {
                                List<IElement> children = new ArrayList<>(attractionCategoryHeader.getChildren());

                                for(IElement child : attractionCategoryHeader.getChildren())
                                {
                                    if(selectedElementsInOrderOfSelection.contains(child))
                                    {
                                        children.remove(child);
                                    }
                                }

                                if(children.size() <= 0)
                                {
                                    selectedElementsInOrderOfSelection.add(attractionCategoryHeader);
                                    notifyItemChanged(content.indexOf(attractionCategoryHeader));
                                    Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSelectionOnClickListener.onClick:: %s selected", attractionCategoryHeader));
                                }
                            }
                        }
                    }
                    else
                    {
                        IElement previouslySelectedElement = getLastSelectedElement();

                        if(previouslySelectedElement != null)
                        {
                            selectedElementsInOrderOfSelection.remove(previouslySelectedElement);
                            notifyItemChanged(content.indexOf(previouslySelectedElement));
                            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSelectionOnClickListener.onClick:: %s deselected", previouslySelectedElement));
                        }

                        selectedElementsInOrderOfSelection.add(selectedElement);
                        notifyItemChanged(content.indexOf(selectedElement));
                        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSelectionOnClickListener.onClick:: %s selected", selectedElement));
                    }
                }
                else
                {
                    selectedElementsInOrderOfSelection.remove(selectedElement);
                    notifyItemChanged(content.indexOf(selectedElement));

                    Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSelectionOnClickListener.onClick:: %s deselected", selectedElement));

                    if(childType != null && isParent(selectedElement))
                    {
                        deselectAllChildren(selectedElement);
                    }
                    else if(childType != null && isChild(selectedElement) && Attraction.class.isInstance(selectedElement))
                    {
                        AttractionCategoryHeader attractionCategoryHeader =
                                getAttractionCategoryHeaderForAttractionCategoryFromContent(content, ((Attraction)selectedElement).getAttractionCategory());

                        selectedElementsInOrderOfSelection.remove(attractionCategoryHeader);
                        notifyItemChanged(content.indexOf(attractionCategoryHeader));
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

                visitedAttraction.increaseRideCount(App.userSettings.getDefaultIncrement());
                notifyItemChanged(content.indexOf(visitedAttraction));
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

                if(visitedAttraction.decreaseRideCount(App.userSettings.getDefaultIncrement()))
                {
                    notifyItemChanged(content.indexOf(visitedAttraction));
                }
            }
        };
    }

    private AttractionCategoryHeader getAttractionCategoryHeaderForAttractionCategoryFromContent(List<? extends IElement> content, AttractionCategory attractionCategory)
    {
        for(IElement element : content)
        {
            if(AttractionCategoryHeader.class.isInstance(element))
            {
                if(((AttractionCategoryHeader)element).getAttractionCategory().equals(attractionCategory))
                {
                    return (AttractionCategoryHeader) element;
                }
            }
        }

        return null;
    }



    private void selectAllChildren(Element element)
    {
        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.selectAllChildren:: selecting [%d] children of element %s",
                element.getChildCountOfType(this.childType), element));

        for(IElement child : element.getChildrenOfType(this.childType))
        {
            if(!this.selectedElementsInOrderOfSelection.contains(child))
            {
                this.selectedElementsInOrderOfSelection.add(child);
                if(this.content.contains(child))
                {
                    Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.selectAllChildren:: [%s] selected", child));
                    notifyItemChanged(this.content.indexOf(child));
                }
            }
        }
    }

    private void deselectAllChildren(Element element)
    {
        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.deselectAllChildren:: deselecting [%d] children of element %s"
                , element.getChildCountOfType(this.childType), element));

        for(IElement child : element.getChildrenOfType(this.childType))
        {
            if(this.selectedElementsInOrderOfSelection.contains(child))
            {
                this.selectedElementsInOrderOfSelection.remove(child);
                if(this.content.contains(child))
                {
                    notifyItemChanged(this.content.indexOf(child));
                    Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.deselectAllChildren:: [%s] deselected", child));
                }
            }
        }
    }






    private void bindViewHolderParent(final ViewHolderParent viewHolder, int position)
    {
        IElement parent = this.content.get(position);
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

        if(this.selectedElementsInOrderOfSelection.contains(parent))
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
                viewHolder.imageViewExpandToggle.setImageDrawable(viewHolder.imageViewExpandToggle.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_down));
            }
            else
            {
                viewHolder.imageViewExpandToggle.setImageDrawable(viewHolder.imageViewExpandToggle.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_right));
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
        IElement child = this.content.get(position);
        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.bindViewHolderChild:: binding %s for position [%d]", child, position));

        viewHolder.textViewName.setText(child.getName());

        if(this.adapterType == AdapterType.SELECTABLE)
        {
            if(this.selectedElementsInOrderOfSelection.contains(child))
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
        Context context = viewHolder.itemView.getContext();
        VisitedAttraction child = (VisitedAttraction) this.content.get(position);
        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.bindViewHolderVisitedAttraction:: binding %s for position [%d]", child, position));

        viewHolder.linearLayoutCounter.setTag(child);
        if(this.recyclerOnClickListener != null)
        {
            viewHolder.linearLayoutCounter.setOnClickListener(new RecyclerOnClickListener(this.recyclerOnClickListener));
            viewHolder.linearLayoutCounter.setOnLongClickListener(new RecyclerOnClickListener(this.recyclerOnClickListener));
        }

        viewHolder.textViewName.setText(child.getName());
        viewHolder.textViewCount.setText(String.valueOf(child.getRideCount()));

        viewHolder.imageViewDecrease.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_remove_circle_outline));
        viewHolder.imageViewIncrease.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_add_circle_outline));

        viewHolder.frameLayoutDecrease.setTag(child);
        viewHolder.frameLayoutDecrease.setOnClickListener(this.decreaseOnClickListener);

        viewHolder.frameLayoutIncrease.setTag(child);
        viewHolder.frameLayoutIncrease.setOnClickListener(this.increaseOnClickListener);
    }

    private void setImagePlaceholder(ImageView imageView)
    {
        imageView.setImageDrawable(DrawableTool.setTintToColor(imageView.getContext().getDrawable(R.drawable.ic_baseline_error_outline), R.color.default_color, imageView.getContext()));
    }

    public void expandParent(Element parent)
    {
        if(!this.expandedParents.contains(parent))
        {
            if(this.childType != null && this.content.contains(parent))
            {
                this.expandedParents.add(parent);
                notifyItemChanged(this.content.indexOf(parent));

                this.content.addAll(this.content.indexOf(parent) + 1, parent.getChildrenOfType(this.childType));
                notifyItemRangeInserted(this.content.indexOf(parent) + 1, parent.getChildCountOfType(this.childType));

                ((LinearLayoutManager)ContentRecyclerViewAdapter.this.getLayoutManager()).scrollToPositionWithOffset(this.content.indexOf(parent), 0);

                Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.expandParent:: " +
                        "expanded parent %s with [%d] children", parent, parent.getChildCountOfType(this.childType)));
            }
        }
        else
        {
            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.expandParent:: parent %s already expanded", parent));
        }
    }

    private void collapseParent(Element parent)
    {
        if(this.childType != null && this.expandedParents.contains(parent))
        {
            this.expandedParents.remove(parent);
            notifyItemChanged(content.indexOf(parent));

            this.content.removeAll(parent.getChildrenOfType(this.childType));
            notifyItemRangeRemoved(content.indexOf(parent) + 1, parent.getChildCountOfType(this.childType));

            this.recyclerView.scrollToPosition(content.indexOf(parent));

            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.collapseParent:: collapsed parent %s", parent));
        }
    }

    public void toggleExpansion(Element parent)
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

    public List<IElement> getContent()
    {
        List<IElement> content = new ArrayList<>();
        for(IElement element : this.content)
        {
            if(!ItemDivider.class.isInstance(element) && !BottomSpacer.class.isInstance(element))
            {
                content.add(element);
            }
        }

        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getContent:: returning [%d] elements", content.size()));
        return content;
    }

    public void swapElements(IElement element1, IElement element2)
    {
        int index1 = this.content.indexOf(element1);
        int index2 = this.content.indexOf(element2);

        Collections.swap(this.content, index1, index2);
        notifyItemMoved(index1, index2);

        this.scrollToElement(element1);
    }

    public boolean isAllSelected()
    {
        List<IElement> content = new ArrayList<>(this.getContent());
        content.removeAll(this.selectedElementsInOrderOfSelection);
        return content.size() <= 0;
    }

    public void selectAllElements()
    {
        Log.i(Constants.LOG_TAG, "ContentRecyclerViewAdapter.selectAllElements:: selecting all elements...");

        this.selectedElementsInOrderOfSelection.clear();

        for(IElement element : this.getContent())
        {
            this.selectedElementsInOrderOfSelection.add(element);
            notifyItemChanged(this.content.indexOf(element));
            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.selectAllElements:: %s parent selected", element));

            if(this.childType != null && this.isParent(element) && !this.expandedParents.contains(element))
            {
                this.selectedElementsInOrderOfSelection.addAll(element.getChildrenOfType(childType));
                Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.selectAllElements:: selected [%d] children of %s", element.getChildCountOfType(childType), element));
            }
        }
    }

    public void deselectAllElements()
    {
        Log.i(Constants.LOG_TAG, "ContentRecyclerViewAdapter.deselectAllElements:: deselecting all elements...");
        this.selectedElementsInOrderOfSelection.clear();
        notifyDataSetChanged();
    }

    public List<IElement> getSelectedElementsInOrderOfSelection()
    {
        return this.selectedElementsInOrderOfSelection;
    }

    public IElement getLastSelectedElement()
    {
        if(!this.selectedElementsInOrderOfSelection.isEmpty())
        {
            return this.selectedElementsInOrderOfSelection.get(0);
        }

        return null;
    }

    public void updateContent(List<IElement> elements)
    {
        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.updateContent:: updating with [%d] elements...", elements.size()));
        this.initializeContent(elements);
    }


    public void setOnClickListener(RecyclerOnClickListener.OnClickListener onClickListener)
    {
        this.recyclerOnClickListener = onClickListener;
    }

    public void scrollToElement(IElement element)
    {
        if(element instanceof AttractionCategory)
        {
            for(IElement item : this.content)
            {
                if(item instanceof AttractionCategoryHeader)
                {
                    if(((AttractionCategoryHeader) item).getAttractionCategory().equals(element))
                    {
                        element = item;
                        break;
                    }
                }
            }
        }

        if(this.content.contains(element) && this.recyclerView != null)
        {
            recyclerView.scrollToPosition(content.indexOf(element));
            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.scrollToElement:: scrolled to [%s]", element));
        }
    }

    public void addBottomSpacer()
    {
        if(!this.content.isEmpty())
        {
            this.content.add(new BottomSpacer());
            notifyItemInserted(this.content.size() - 1);
            Log.v(Constants.LOG_TAG, "ContentRecyclerViewAdapter.addBottomSpacer:: added BottomSpacer");
        }
    }

    public RecyclerView.LayoutManager getLayoutManager()
    {
        return this.recyclerView.getLayoutManager();
    }
}
