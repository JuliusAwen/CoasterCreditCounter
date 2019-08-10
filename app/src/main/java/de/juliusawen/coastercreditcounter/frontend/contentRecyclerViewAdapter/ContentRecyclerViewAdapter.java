package de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.GroupHeader.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.backend.GroupHeader.GroupHeaderProvider;
import de.juliusawen.coastercreditcounter.backend.GroupHeader.YearHeader;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.attractions.AttractionBlueprint;
import de.juliusawen.coastercreditcounter.backend.attractions.CoasterBlueprint;
import de.juliusawen.coastercreditcounter.backend.attractions.CustomAttraction;
import de.juliusawen.coastercreditcounter.backend.attractions.CustomCoaster;
import de.juliusawen.coastercreditcounter.backend.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.attractions.StockAttraction;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Location;
import de.juliusawen.coastercreditcounter.backend.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.backend.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.backend.temporaryElements.VisitedAttraction;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.AdapterType;
import de.juliusawen.coastercreditcounter.toolbox.ConvertTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;

public class ContentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private RecyclerView recyclerView;
    private final GroupHeaderProvider groupHeaderProvider;

    private List<IElement> originalItems;
    private List<IElement> items = new ArrayList<>();

    private final Map<IElement, Integer> generationByItem = new LinkedHashMap<>();

    private final AdapterType adapterType;
    private int groupType;
    private boolean formatAsPrettyPrint;

    private final boolean selectMultipleItems;
    private final Set<Class<? extends IElement>> relevantChildTypes = new HashSet<>();

    private RecyclerOnClickListener.OnClickListener recyclerOnClickListener;
    private View.OnClickListener addRideOnClickListener;
    private View.OnClickListener deleteRideOnClickListener;
    private final View.OnClickListener expansionOnClickListener;
    private final View.OnClickListener selectionOnClickListener;

    private final List<IElement> selectedItemsInOrderOfSelection = new ArrayList<>();
    private final Set<IElement> expandedItems = new HashSet<>();

    private boolean displayManufacturers;
    private boolean displayAttractionCategories;
    private boolean displayLocations;
    private boolean displayStatus;
    private boolean displayTotalRideCount;

    private final Set<Class<? extends IAttraction>> typesToDisplayManufacturer = new HashSet<>();
    private final Set<Class<? extends IAttraction>> typesToDisplayAttractionCategory = new HashSet<>();
    private final Set<Class<? extends IAttraction>> typesToDisplayLocation = new HashSet<>();
    private final Set<Class<? extends IAttraction>> typesToDisplayStatus = new HashSet<>();
    private final Set<Class<? extends IAttraction>> typesToDisplayTotalRideCount = new HashSet<>();

    private Map<Class<? extends IElement>, Integer> specialStringResourcesByType = new HashMap<>();

    @SuppressLint("UseSparseArrays")
    private final Map<Integer, Set<Class<? extends IElement>>> typesByTypeface = new HashMap<>();

    enum ViewType
    {
        ITEM,
        VISITED_ATTRACTION,
        BOTTOM_SPACER
    }

    ContentRecyclerViewAdapter(GetContentRecyclerViewAdapterRequest request)
    {
        this.adapterType = request.adapterType;
        this.groupType = request.groupType;
        this.selectMultipleItems = request.selectMultiple;

        this.initializeTypesToDisplay();
        this.initializeTypesByTypeface();

        this.groupHeaderProvider = new GroupHeaderProvider();

        if(request.relevantChildTypes != null)
        {
            this.relevantChildTypes.addAll(request.relevantChildTypes);
        }

        this.setItems(request.elements);

        this.expansionOnClickListener = this.getExpansionOnClickListener();
        this.selectionOnClickListener = this.getSelectionOnClickListener();
    }


    public void setItems(List<IElement> items)
    {
        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.setItems:: setting [%d] items...", items.size()));

        this.originalItems = items;
        this.generationByItem.clear();
        this.items.clear();

        this.groupItemsByType(this.groupType);
    }

    public void groupItemsByType(int groupType)
    {
        this.groupType = groupType;

        List<IElement> groupedItems = new ArrayList<>();

        if(groupType == Constants.TYPE_NONE)
        {
            groupedItems = this.originalItems;
        }
        else if(groupType == Constants.TYPE_ATTRACTION_CATEGORY)
        {
            groupedItems = this.groupHeaderProvider.groupByAttractionCategories(ConvertTool.convertElementsToType(this.originalItems, IAttraction.class));
        }
        else if(groupType == Constants.TYPE_YEAR)
        {
            groupedItems = this.groupHeaderProvider.groupByYear(ConvertTool.convertElementsToType(this.originalItems, Visit.class));

            if(App.settings.expandLatestYearInListByDefault())
            {
                YearHeader latestYearHeader = this.groupHeaderProvider.getLatestYearHeader(groupedItems);
                this.expandedItems.add(latestYearHeader);
            }
        }

        this.items = this.initializeItems(groupedItems, 0);

        notifyDataSetChanged();
    }

    private List<IElement> initializeItems(List<IElement> items, int generation)
    {
        List<IElement> initializedItems = new ArrayList<>();

        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.initializeItems:: initializing [%d] items - generation [%d]...", items.size(), generation));

        for(IElement item : items)
        {
            initializedItems.add(item);
            this.generationByItem.put(item, generation);

            if(this.expandedItems.contains(item))
            {
                List<IElement> relevantChildren = this.getRelevantChildren(item);
                Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.initializeItems:: item %s is expanded - adding [%d] children", item, relevantChildren.size()));
                initializedItems.addAll(this.initializeItems(relevantChildren, generation + 1));
            }
        }

        return initializedItems;
    }

    private void initializeTypesToDisplay()
    {
        this.typesToDisplayLocation.add(CustomCoaster.class);
        this.typesToDisplayLocation.add(CustomAttraction.class);
        this.typesToDisplayLocation.add(StockAttraction.class);
        this.typesToDisplayLocation.add(CoasterBlueprint.class); // as blueprints are not on site attractions, they have no location and "blueprint" is displayed instead
        this.typesToDisplayLocation.add(AttractionBlueprint.class); // as blueprints are not on site attractions, they have no location and "blueprint" is displayed instead

        this.typesToDisplayManufacturer.add(CustomCoaster.class);
        this.typesToDisplayManufacturer.add(CustomAttraction.class);
        this.typesToDisplayManufacturer.add(CoasterBlueprint.class);
        this.typesToDisplayManufacturer.add(AttractionBlueprint.class);
        this.typesToDisplayManufacturer.add(StockAttraction.class);

        this.typesToDisplayAttractionCategory.add(CustomCoaster.class);
        this.typesToDisplayAttractionCategory.add(CustomAttraction.class);
        this.typesToDisplayAttractionCategory.add(CoasterBlueprint.class);
        this.typesToDisplayAttractionCategory.add(AttractionBlueprint.class);

        this.typesToDisplayStatus.add(CustomCoaster.class);
        this.typesToDisplayStatus.add(CustomAttraction.class);
        this.typesToDisplayStatus.add(StockAttraction.class);

        this.typesToDisplayTotalRideCount.add(CustomCoaster.class);
        this.typesToDisplayTotalRideCount.add(CustomAttraction.class);
        this.typesToDisplayTotalRideCount.add(StockAttraction.class);
    }

    private void initializeTypesByTypeface()
    {
        this.typesByTypeface.put(Typeface.BOLD, new HashSet<Class<? extends IElement>>());
        this.typesByTypeface.put(Typeface.ITALIC, new HashSet<Class<? extends IElement>>());
        this.typesByTypeface.put(Typeface.BOLD_ITALIC, new HashSet<Class<? extends IElement>>());
    }

    static class ViewHolderItem extends RecyclerView.ViewHolder
    {
        final LinearLayout linearLayout;
        final View viewSeperator;
        final ImageView imageViewExpandToggle;
        final TextView textViewDetailAbove;
        final TextView textViewName;
        final TextView textViewDetailBelow;


        ViewHolderItem(View view)
        {
            super(view);
            this.linearLayout = view.findViewById(R.id.linearLayoutRecyclerViewItem);
            this.viewSeperator = view.findViewById(R.id.viewRecyclerViewItem_Seperator);
            this.imageViewExpandToggle = view.findViewById(R.id.imageViewRecyclerViewItem);
            this.textViewDetailAbove = view.findViewById(R.id.textViewRecyclerViewItem_UpperDetail);
            this.textViewName = view.findViewById(R.id.textViewRecyclerViewItem_Name);
            this.textViewDetailBelow = view.findViewById(R.id.textViewRecyclerViewItem_LowerDetail);
        }
    }

    static class ViewHolderVisitedAttraction extends RecyclerView.ViewHolder
    {
        final LinearLayout linearLayoutEditable;
        final LinearLayout linearLayoutCounter;
        final TextView textViewName;
        final TextView textViewCount;
        final ImageView imageViewDecrease;
        final ImageView imageViewIncrease;
        final TextView textViewPrettyPrint;

        ViewHolderVisitedAttraction(View view)
        {
            super(view);

            this.linearLayoutEditable = view.findViewById(R.id.linearLayoutRecyclerViewVisitedAttraction_OpenForEditing);

            this.linearLayoutCounter = view.findViewById(R.id.linearLayoutRecyclerViewVisitedAttraction_Counter);

            this.textViewName = view.findViewById(R.id.textViewRecyclerViewItemVisitedAttraction_Name);
            this.textViewCount = view.findViewById(R.id.textViewRecyclerViewItemVisitedAttraction_Count);

            this.imageViewIncrease = view.findViewById(R.id.imageViewRecyclerViewItemVisitedAttraction_Increase);
            this.imageViewIncrease.setImageDrawable(App.getContext().getDrawable(R.drawable.ic_baseline_add_circle_outline));

            this.imageViewDecrease = view.findViewById(R.id.imageViewRecyclerViewItemVisitedAttraction_Decrease);
            this.imageViewDecrease.setImageDrawable(App.getContext().getDrawable(R.drawable.ic_baseline_remove_circle_outline));

            this.textViewPrettyPrint = view.findViewById(R.id.textViewRecyclerViewItemVisitedAttraction_PrettyPrint);
        }
    }

    private class BottomSpacer extends OrphanElement
    {
        private BottomSpacer()
        {
            super("BottomSpacer", UUID.randomUUID());
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

    @Override
    public int getItemViewType(int position)
    {
        IElement item = this.items.get(position);

        if(this.adapterType.equals(AdapterType.COUNTABLE) && (item instanceof VisitedAttraction))
        {
            return ViewType.VISITED_ATTRACTION.ordinal();
        }
        else if(item instanceof BottomSpacer)
        {
            return ViewType.BOTTOM_SPACER.ordinal();
        }
        else
        {
            return ViewType.ITEM.ordinal();
        }
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
            case ITEM:
                view = layoutInflater.inflate(R.layout.recycler_view_item, viewGroup, false);
                viewHolder = new ViewHolderItem(view);
                break;

            case VISITED_ATTRACTION:
                view = layoutInflater.inflate(R.layout.recycler_view_item_visited_attraction, viewGroup, false);
                viewHolder = new ViewHolderVisitedAttraction(view);
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position)
    {
        ViewType viewType = ViewType.values()[viewHolder.getItemViewType()];
        switch (viewType)
        {
            case ITEM:
                ViewHolderItem viewHolderItem = (ViewHolderItem) viewHolder;
                this.bindViewHolderItem(viewHolderItem, position);
                break;

            case VISITED_ATTRACTION:
                ViewHolderVisitedAttraction viewHolderVisitedAttraction = (ViewHolderVisitedAttraction) viewHolder;
                this.bindViewHolderVisitedAttraction(viewHolderVisitedAttraction, position);
                break;

            case BOTTOM_SPACER:
                break;

            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public int getItemCount()
    {
        return this.items.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    private RecyclerView.LayoutManager getLayoutManager()
    {
        return this.recyclerView.getLayoutManager();
    }

    private void bindViewHolderItem(final ViewHolderItem viewHolder, int position)
    {
        IElement item = this.items.get(position);
        int generation = this.getGeneration(item);

        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.bindViewHolderItem:: binding %s for position [%d] - generation [%d]...", item, position, generation));


        //setExpandToggle
        viewHolder.imageViewExpandToggle.setTag(item);
        if(!this.getRelevantChildren(item).isEmpty() && !this.formatAsPrettyPrint)
        {
            if(this.expandedItems.contains(item))
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
            viewHolder.imageViewExpandToggle.setImageDrawable(DrawableTool.getColoredDrawable(R.drawable.ic_baseline_error_outline, R.color.default_color));
        }

        if(!this.formatAsPrettyPrint)
        {
            viewHolder.viewSeperator.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.viewSeperator.setVisibility(View.GONE);
        }



        //decorate item

        viewHolder.textViewName.setTypeface(null, Typeface.NORMAL);

        //set typeface
        for(Map.Entry<Integer, Set<Class<? extends IElement>>> typeByTypeface : this.typesByTypeface.entrySet())
        {
            if(typeByTypeface.getValue().contains(item.getClass()))
            {
                viewHolder.textViewName.setTypeface(null, typeByTypeface.getKey());
                break;
            }
        }

        if(this.specialStringResourcesByType.containsKey(item.getClass()))
        {
            if(item instanceof Visit)
            {
                viewHolder.textViewName.setText(App.getContext().getString(this.specialStringResourcesByType.get(Visit.class), item.getName(), item.getParent().getName()));
            }
        }
        else
        {
            viewHolder.textViewName.setText(item.getName());
        }

        //decorate detail above
        Class type = item.getClass();

        if(this.displayManufacturers && this.typesToDisplayManufacturer.contains(type))
        {
            viewHolder.textViewDetailAbove.setVisibility(View.VISIBLE);
            viewHolder.textViewDetailAbove.setText(((Attraction)item).getManufacturer().getName());
        }
        else if(this.displayAttractionCategories && typesToDisplayAttractionCategory.contains(type))
        {
            viewHolder.textViewDetailAbove.setVisibility(View.VISIBLE);
            viewHolder.textViewDetailAbove.setText(((IAttraction)item).getAttractionCategory().getName());
        }
        else
        {
            viewHolder.textViewDetailAbove.setVisibility(View.GONE);
        }

        //decorate detail below
        if(this.displayLocations && this.typesToDisplayLocation.contains(type))
        {
            viewHolder.textViewDetailBelow.setVisibility(View.VISIBLE);

            String locationName;

            if(item instanceof CoasterBlueprint || item instanceof AttractionBlueprint)
            {
                // as blueprints are not on site attractions, they have no location and "blueprint" is displayed instead
                locationName = App.getContext().getString(R.string.text_blueprint_substitute);
                viewHolder.textViewDetailBelow.setTypeface(null, Typeface.ITALIC);
            }
            else
            {
                locationName = item.getParent().getName();
                viewHolder.textViewDetailBelow.setTypeface(null, Typeface.NORMAL);
            }

            viewHolder.textViewDetailBelow.setText(locationName);
        }
        else if((this.displayStatus && this.typesToDisplayStatus.contains(type) || (this.displayTotalRideCount && this.typesToDisplayTotalRideCount.contains(type))))
        {
            viewHolder.textViewDetailBelow.setVisibility(View.VISIBLE);
            viewHolder.textViewDetailBelow.setTypeface(null, Typeface.ITALIC);

            String text = "";

            if(this.displayStatus)
            {
                text += ((IAttraction)item).getStatus().getName();
            }

            if(this.displayStatus && this.displayTotalRideCount)
            {
                text += App.getContext().getString(R.string.text_lower_detail_seperator);
            }

            if(this.displayTotalRideCount)
            {
                text += App.getContext().getString(R.string.text_total_rides, ((IAttraction)item).getTotalRideCount());
            }


            viewHolder.textViewDetailBelow.setText(text);
        }
        else
        {
            viewHolder.textViewDetailBelow.setVisibility(View.GONE);
        }

        viewHolder.itemView.setTag(item);

        if(this.selectedItemsInOrderOfSelection.contains(item))
        {
            viewHolder.itemView.setBackgroundColor(App.getContext().getColor(R.color.selected_color));
        }
        else
        {
            viewHolder.itemView.setBackgroundColor(App.getContext().getColor(R.color.default_color));
        }

        if(!viewHolder.itemView.hasOnClickListeners())
        {
            if(this.adapterType == AdapterType.SELECTABLE)
            {
                viewHolder.itemView.setOnClickListener(this.selectionOnClickListener);
            }
            else if(this.recyclerOnClickListener != null)
            {
                viewHolder.itemView.setOnClickListener(new RecyclerOnClickListener(this.recyclerOnClickListener));
                viewHolder.itemView.setOnLongClickListener(new RecyclerOnClickListener(this.recyclerOnClickListener));
            }
        }


        //indentLayout based on generation
        int padding = ConvertTool.convertDpToPx(
                (int)(App.getContext().getResources().getDimension(R.dimen.expand_toggle_padding_factor) / App.getContext().getResources().getDisplayMetrics().density)) * generation;
        viewHolder.linearLayout.setPadding(padding, 0, padding, 0);
    }

    private void bindViewHolderVisitedAttraction(ViewHolderVisitedAttraction viewHolder, int position)
    {
        VisitedAttraction visitedAttraction = (VisitedAttraction) this.items.get(position);
        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.bindViewHolderVisitedAttraction:: binding %s for position [%d]", visitedAttraction, position));

        if(!this.formatAsPrettyPrint)
        {
            viewHolder.textViewPrettyPrint.setVisibility(View.GONE);
            viewHolder.linearLayoutEditable.setVisibility(View.VISIBLE);

            viewHolder.linearLayoutCounter.setTag(visitedAttraction);
            if(!viewHolder.linearLayoutCounter.hasOnClickListeners() && this.recyclerOnClickListener != null)
            {
                viewHolder.linearLayoutCounter.setOnClickListener(new RecyclerOnClickListener(this.recyclerOnClickListener));
                viewHolder.linearLayoutCounter.setOnLongClickListener(new RecyclerOnClickListener(this.recyclerOnClickListener));
            }

            viewHolder.textViewName.setText(visitedAttraction.getName());
            viewHolder.textViewCount.setText(String.valueOf(visitedAttraction.getRideCount()));

            viewHolder.imageViewIncrease.setTag(visitedAttraction);
            if(!viewHolder.imageViewIncrease.hasOnClickListeners() && this.addRideOnClickListener != null)
            {
                viewHolder.imageViewIncrease.setOnClickListener(this.addRideOnClickListener);
            }

            //Todo: refactor --> ShowRides/ShowRide
            viewHolder.imageViewDecrease.setTag(visitedAttraction);
            if(!viewHolder.imageViewDecrease.hasOnClickListeners() && this.deleteRideOnClickListener != null)
            {
                viewHolder.imageViewDecrease.setOnClickListener(this.deleteRideOnClickListener);
            }
        }
        else
        {
            viewHolder.linearLayoutEditable.setVisibility(View.GONE);
            viewHolder.textViewPrettyPrint.setVisibility(View.VISIBLE);

            viewHolder.textViewPrettyPrint.setText(App.getContext().getString(R.string.text_visited_attraction_pretty_print, visitedAttraction.getRideCount(), visitedAttraction.getName()));
        }
    }

    private int getGeneration(IElement item)
    {
        Integer generation = this.generationByItem.get(item);

        if(generation == null)
        {
            Log.e(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getGeneration:: could not determine generation for %s - returning -1...", item));
            generation = -1;
        }

        return generation;
    }

    private List<IElement> getRelevantChildren(IElement item)
    {
        List<IElement> relevantChildren = new ArrayList<>();

        for(Class<? extends IElement> childType : this.relevantChildTypes)
        {
            relevantChildren.addAll(item.getChildrenOfType(childType));
        }

        return relevantChildren;
    }

    private IElement getParentOfRelevantChild(IElement item)
    {
        if(item instanceof Attraction)
        {
            AttractionCategoryHeader attractionCategoryHeader = this.getAttractionCategoryHeaderForAttractionCategoryFromItem(((Attraction) item).getAttractionCategory());
            return this.items.get(this.items.indexOf(attractionCategoryHeader));
        }
        else if(!(item instanceof OrphanElement))
        {
            return this.items.get(this.items.indexOf(item.getParent()));
        }
        else
        {
            return null;
        }
    }

    private AttractionCategoryHeader getAttractionCategoryHeaderForAttractionCategoryFromItem(AttractionCategory attractionCategory)
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

    public void setOnClickListener(RecyclerOnClickListener.OnClickListener onClickListener)
    {
        this.recyclerOnClickListener = onClickListener;
    }

    public void addRideOnClickListener(View.OnClickListener increaseOnClickListener)
    {
        this.addRideOnClickListener = increaseOnClickListener;
    }

    public void deleteRideOnClickListener(View.OnClickListener decreaseOnClickListener)
    {
        this.deleteRideOnClickListener = decreaseOnClickListener;
    }

    private View.OnClickListener getExpansionOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final IElement item = (IElement) view.getTag();

                if(!expandedItems.contains(item))
                {
                    expandItem(item);
                }
                else
                {
                    collapseItem(item);
                }
            }
        };
    }

    public void toggleExpansion(IElement item)
    {
        if(!this.relevantChildTypes.isEmpty())
        {
            if(!this.expandedItems.contains(item))
            {
                this.expandItem(item);
            }
            else
            {
                this.collapseItem(item);
            }
        }
    }

    public void expandAll()
    {
        int itemsCount;
        do
        {
            Log.v(Constants.LOG_TAG, "ContentRecyclerViewAdapter.expandAll:: expanding next generation");

            List<IElement> itemsList = new ArrayList<>(this.items);
            itemsCount = itemsList.size();

            for(IElement item : itemsList)
            {
                if(!this.expandedItems.contains(item))
                {
                    this.expandItem(item);
                }
            }
        }
        while(itemsCount != this.items.size());

        scrollToItem(this.items.get(0));

        Log.v(Constants.LOG_TAG, "ContentRecyclerViewAdapter.expandAll:: all expanded");
    }

    public void expandItem(IElement item)
    {
        if(!this.expandedItems.contains(item))
        {
            List<IElement> relevantChildren = this.getRelevantChildren(item);
            if(!relevantChildren.isEmpty())
            {
                this.expandedItems.add(item);
                notifyItemChanged(this.items.indexOf(item));

                int generation = this.getGeneration(item) + 1;
                int index = this.items.indexOf(item);
                for(IElement child : relevantChildren)
                {
                    this.generationByItem.put(child, generation);

                    index ++;
                    this.items.add(index, child);
                    notifyItemInserted(index);

                    Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.expandItem:: added child %s at index [%d] - generation [%d]", child, index, generation));
                }


                //scroll to item above expanded item (if any)
                index = this.items.indexOf(item);
                if(index > 0)
                {
                    index--;
                }
                ((LinearLayoutManager)ContentRecyclerViewAdapter.this.getLayoutManager()).scrollToPositionWithOffset(index, 0);
            }
        }
        else
        {
            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.expandItem:: %s already expanded", item));
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isAllExpanded()
    {
        for(IElement item : this.items)
        {
            for(IElement relevantChild : this.getRelevantChildren(item))
            {
                if(relevantChild instanceof Location)
                {
                    List<IElement> relevantGrandchildren = this.getRelevantChildren(relevantChild);
                    if(!relevantGrandchildren.isEmpty())
                    {
                        if(!this.expandedItems.contains(relevantChild))
                        {
                            return false;
                        }
                    }
                }
                else if(relevantChild instanceof Attraction || relevantChild instanceof VisitedAttraction)
                {
                    if(!this.expandedItems.contains(item))
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void collapseAll()
    {
        Log.v(Constants.LOG_TAG, "ContentRecyclerViewAdapter.collapseAll:: collapsing all");

        List<IElement> itemsList = new ArrayList<>(this.expandedItems);

        for(IElement item : itemsList)
        {
            if(this.expandedItems.contains(item))
            {
                this.collapseItem(item);
            }
        }

        Log.v(Constants.LOG_TAG, "ContentRecyclerViewAdapter.collapseAll:: all collapsed");
    }

    private void collapseItem(IElement item)
    {
        if(this.expandedItems.contains(item))
        {
            List<IElement> relevantChildren = this.getRelevantChildren(item);
            if(!relevantChildren.isEmpty())
            {
                this.expandedItems.remove(item);
                notifyItemChanged(items.indexOf(item));

                for(IElement child : relevantChildren)
                {
                    if(this.expandedItems.contains(child))
                    {
                        this.collapseItem(child);
                    }

                    int index = this.items.indexOf(child);
                    this.items.remove(child);
                    notifyItemRemoved(index);

                    this.generationByItem.remove(child);

                    Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.collapseItem:: removed child %s at index [%d]", child, index));
                }

                this.scrollToItem(item);

                Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.collapseItem:: collapsed item %s", item));
            }
        }
        else
        {
            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.collapseItem:: %s not expanded", item));
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isAllCollapsed()
    {
        for(IElement item : this.items)
        {
            List<IElement> relevantChildren = this.getRelevantChildren(item);
            int relevantChildCount = relevantChildren.size();

            relevantChildren.removeAll(this.items);

            if(relevantChildCount != relevantChildren.size())
            {
                return false;
            }
        }

        return true;
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
                    if(selectMultipleItems)
                    {
                        selectedItemsInOrderOfSelection.add(selectedItem);
                        notifyItemChanged(items.indexOf(selectedItem));
                        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSelectionOnClickListener.onClick:: %s selected", selectedItem));

                        if(!relevantChildTypes.isEmpty())
                        {
                            List<IElement> relevantChildren = getRelevantChildren(selectedItem);

                            selectAllRelevantChildren(relevantChildren);
                            selectParentIfAllRelevantChildrenAreSelected(getParentOfRelevantChild(selectedItem));
                        }
                    }
                    else
                    {
                        IElement previouslySelectedItem = getLastSelectedItem();

                        if(previouslySelectedItem != null)
                        {
                            selectedItemsInOrderOfSelection.remove(previouslySelectedItem);
                            notifyItemChanged(items.indexOf(previouslySelectedItem));
                            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSelectionOnClickListener.onClick:: %s deselected", previouslySelectedItem));
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

                    if(selectMultipleItems)
                    {
                        if(!relevantChildTypes.isEmpty())
                        {
                            List<IElement> relevantChildren = getRelevantChildren(selectedItem);
                            deselectAllRelevantChildren(relevantChildren);
                            deselectParentIfNotAllRelevantChildrenAreSelected(getParentOfRelevantChild(selectedItem));
                        }
                    }
                }

                if(recyclerOnClickListener != null)
                {
                    recyclerOnClickListener.onClick(view);
                }
            }
        };
    }

    private void selectAllRelevantChildren(List<IElement> relevantChildren)
    {
        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.selectAllRelevantChildren:: selecting [%d] children", relevantChildren.size()));

        for(IElement child : relevantChildren)
        {
            if(!this.selectedItemsInOrderOfSelection.contains(child))
            {
                this.selectedItemsInOrderOfSelection.add(child);
                if(this.items.contains(child))
                {
                    Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.selectAllRelevantChildren:: child [%s] selected", child));
                    notifyItemChanged(this.items.indexOf(child));
                }
            }
        }
    }

    private void deselectAllRelevantChildren(List<IElement> relevantChildren)
    {
        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.deselectAllRelevantChildren:: deselecting [%d] children", relevantChildren.size()));

        for(IElement child : relevantChildren)
        {
            if(this.selectedItemsInOrderOfSelection.contains(child))
            {
                this.selectedItemsInOrderOfSelection.remove(child);
                if(this.items.contains(child))
                {
                    notifyItemChanged(this.items.indexOf(child));
                    Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.deselectAllRelevantChildren:: child [%s] deselected", child));
                }
            }
        }
    }

    private void selectParentIfAllRelevantChildrenAreSelected(IElement parent)
    {
        if(parent != null && this.allRelevantChildrenAreSelected(parent))
        {
            this.selectedItemsInOrderOfSelection.add(parent);
            notifyItemChanged(items.indexOf(parent));
            Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.selectParentIfAllRelevantChildrenAreSelected:: parent %s selected", parent));
        }
    }
    private void deselectParentIfNotAllRelevantChildrenAreSelected(IElement parent)
    {
        if(parent != null && !allRelevantChildrenAreSelected(parent))
        {
            this.selectedItemsInOrderOfSelection.remove(parent);
            notifyItemChanged(items.indexOf(parent));
            Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.deselectParentIfNotAllRelevantChildrenAreSelected:: parent %s deselected", parent));
        }
    }

    private boolean allRelevantChildrenAreSelected(IElement parent)
    {
        boolean allRelevantChildrenAreSelected = false;
        List<IElement> relevantChildren = this.getRelevantChildren(parent);
        if(parent != null && !relevantChildren.isEmpty())
        {
            relevantChildren.removeAll(this.selectedItemsInOrderOfSelection);
            allRelevantChildrenAreSelected = relevantChildren.isEmpty();
        }
        return allRelevantChildrenAreSelected;
    }

    public boolean isAllSelected()
    {
        List<IElement> items = new ArrayList<>(this.items);
        items.removeAll(this.selectedItemsInOrderOfSelection);
        return items.isEmpty();
    }

    public void selectAllItems()
    {
        Log.i(Constants.LOG_TAG, "ContentRecyclerViewAdapter.selectAllItems:: selecting all items...");

        this.selectedItemsInOrderOfSelection.clear();

        for(IElement item : this.items)
        {
            this.selectedItemsInOrderOfSelection.add(item);
            notifyItemChanged(this.items.indexOf(item));
            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.selectAllItems:: %s selected", item));


            if(!this.expandedItems.contains(item))
            {
                List<IElement> relevantChildren = this.getRelevantChildren(item);
                if(!relevantChildren.isEmpty())
                {
                    this.selectedItemsInOrderOfSelection.addAll(relevantChildren);
                    Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.selectAllItems:: selected [%d] children for not expanded %s", relevantChildren.size(), item));
                }
            }
        }
    }

    public void deselectAllItems()
    {
        Log.i(Constants.LOG_TAG, "ContentRecyclerViewAdapter.deselectAllItems:: deselecting all elements...");

        List<IElement> selectedItems = new ArrayList<>(this.selectedItemsInOrderOfSelection);
        this.selectedItemsInOrderOfSelection.clear();

        for(IElement selectedItem : selectedItems)
        {
            notifyItemChanged(this.items.indexOf(selectedItem));
        }
    }

    public List<IElement> getSelectedItemsInOrderOfSelection()
    {
        List<IElement> selectedItems = new ArrayList<>();

        for(IElement item : this.selectedItemsInOrderOfSelection)
        {
            if(!(item instanceof AttractionCategoryHeader))
            {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    public IElement getLastSelectedItem()
    {
        if(!this.selectedItemsInOrderOfSelection.isEmpty())
        {
            return this.selectedItemsInOrderOfSelection.get(0);
        }
        return null;
    }

    public void swapItems(IElement item1, IElement item2)
    {
        int index1 = this.items.indexOf(item1);
        int index2 = this.items.indexOf(item2);

        Collections.swap(this.items, index1, index2);
        notifyItemMoved(index1, index2);

        this.scrollToItem(item1);
    }

    public void scrollToItem(IElement item)
    {
        if(item instanceof AttractionCategory)
        {
            for(IElement element : this.items)
            {
                if(element instanceof AttractionCategoryHeader)
                {
                    if(((AttractionCategoryHeader)element).getAttractionCategory().equals(item))
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
            Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.scrollToItem:: scrolled to %s", item));
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

    public void setTypefaceForType(Class<? extends IElement> type, int typeface)
    {
        if(typeface <= 3)
        {
            Objects.requireNonNull(this.typesByTypeface.get(typeface)).add(type);
        }
        else
        {
            Log.e(Constants.LOG_TAG, "ContentRecyclerViewAdapter.setTypefaceForType:: unknown typeface");
        }
    }

    public void setSpecialStringResourceForType(Class<? extends IElement> type,  int stringResource)
    {
        this.specialStringResourcesByType.put(type, stringResource);
    }

    public void displayManufacturers(boolean display)
    {
        this.displayManufacturers = display;
    }

    public void displayAttractionCategories(boolean display)
    {
        this.displayAttractionCategories = display;
    }

    public void displayLocations(boolean display)
    {
        this.displayLocations = display;
    }

    public void displayStatus(boolean display)
    {
        this.displayStatus = display;
    }

    public void displayTotalRideCount(boolean display)
    {
        this.displayTotalRideCount = display;
    }

    public void formatAsPrettyPrint(boolean formatAsPrettyPrint)
    {
        this.formatAsPrettyPrint = formatAsPrettyPrint;

        if(formatAsPrettyPrint)
        {
            this.expandAll();
        }
        else
        {
            this.collapseAll();
        }

        notifyDataSetChanged();
    }
}
