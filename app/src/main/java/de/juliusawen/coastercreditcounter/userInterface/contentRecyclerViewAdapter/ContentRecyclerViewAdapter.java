package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.IGroupHeader;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.SpecialGroupHeader;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.temporary.BottomSpacer;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.StringTool;

public class ContentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private enum ViewType
    {
        ITEM,
        VISITED_ATTRACTION,
        BOTTOM_SPACER
    }

    private RecyclerView recyclerView;
    private final GroupHeaderProvider groupHeaderProvider;

    private ArrayList<IElement> originalItems;
    private ArrayList<IElement> items = new ArrayList<>();

    private final HashMap<IElement, Integer> generationByItem = new HashMap<>();

    private final ContentRecyclerViewAdapterType contentRecyclerViewAdapterType;
    private GroupType groupType;
    private boolean formatAsPrettyPrint;

    private final boolean selectMultipleItems;
    private final Set<Class<? extends IElement>> relevantChildTypes = new HashSet<>();

    private boolean useDedicatedExpansionOnClickListener;
    private final View.OnClickListener expansionOnClickListener;
    private final View.OnClickListener selectionOnClickListener;
    private RecyclerOnClickListener.OnClickListener recyclerOnClickListener;
    private View.OnClickListener increaseRideCountOnClickListener;
    private View.OnClickListener decreaseRideCountOnClickListener;


    private final LinkedList<IElement> selectedItemsInOrderOfSelection = new LinkedList<>();
    private final HashSet<IElement> expandedItems = new HashSet<>();

    private final HashMap<Class<? extends IElement>, Integer> specialStringResourcesByType = new HashMap<>();

    private final HashMap<Class<? extends IElement>, Integer> typefacesByContentType = new HashMap<>();
    private final HashMap<DetailType, Integer> typefacesByDetailType = new HashMap<>();

    private HashMap<DetailType, HashMap<DetailDisplayMode, Set<Class<? extends IAttraction>>>> contentTypesByDetailDisplayModeByDetailType = new HashMap<>();

    ContentRecyclerViewAdapter(GetContentRecyclerViewAdapterRequest request)
    {
        this.contentRecyclerViewAdapterType = request.contentRecyclerViewAdapterType;
        this.selectMultipleItems = request.selectMultiple;

        this.initializeContentTypesByDetailDisplayModeByDetailType();

        this.groupHeaderProvider = new GroupHeaderProvider();
        this.groupType = GroupType.NONE;

        if(request.relevantChildTypes != null)
        {
            this.relevantChildTypes.addAll(request.relevantChildTypes);
        }

        this.setItems(request.elements);

        this.expansionOnClickListener = this.getExpansionOnClickListener();
        this.selectionOnClickListener = this.getSelectionOnClickListener();
    }

    public ContentRecyclerViewAdapter setItems(List<IElement> items)
    {
        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.setItems:: setting [%d] items...", items.size()));

        this.originalItems = new ArrayList<>(items);
        this.generationByItem.clear();
        this.items.clear();

        this.groupItems(this.groupType);

        return this;
    }

    public ContentRecyclerViewAdapter groupItems(GroupType groupType)
    {
        this.groupType = groupType;
        this.selectedItemsInOrderOfSelection.clear();

        List<IElement> groupedItems = new LinkedList<>();

        switch(groupType)
        {
            case NONE:
                groupedItems = new LinkedList<>(this.originalItems);
                break;

            case LOCATION:
                groupedItems = this.groupHeaderProvider.groupElements(this.originalItems, GroupType.LOCATION);
                break;

            case CREDIT_TYPE:
                groupedItems = this.groupHeaderProvider.groupElements(this.originalItems, GroupType.CREDIT_TYPE);
                break;

            case CATEGORY:
                groupedItems = this.groupHeaderProvider.groupElements(this.originalItems, GroupType.CATEGORY);
                break;

            case MANUFACTURER:
                groupedItems = this.groupHeaderProvider.groupElements(this.originalItems, GroupType.MANUFACTURER);
                break;

            case STATUS:
                groupedItems = this.groupHeaderProvider.groupElements(this.originalItems, GroupType.STATUS);
                break;

            case YEAR:
                groupedItems = this.groupHeaderProvider.groupElements(this.originalItems, GroupType.YEAR);

                if(App.preferences.expandLatestYearHeaderByDefault())
                {
                    SpecialGroupHeader latestSpecialGroupHeader = this.groupHeaderProvider.getSpecialGroupHeaderForLatestYear(groupedItems);
                    this.expandedItems.add(latestSpecialGroupHeader);
                }
                break;
        }

        this.items = this.initializeItems(groupedItems, 0);
        notifyDataSetChanged();

        return this;
    }

    private ArrayList<IElement> initializeItems(List<IElement> items, int generation)
    {
        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.initializeItems:: initializing [%d] items - generation [%d]...", items.size(), generation));

        ArrayList<IElement> initializedItems = new ArrayList<>();
        for(IElement item : items)
        {
            initializedItems.add(item);
            this.generationByItem.put(item, generation);

            if(this.expandedItems.contains(item))
            {
                ArrayList<IElement> relevantChildren = this.getRelevantChildren(item);
                Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.initializeItems:: item %s is expanded - adding [%d] children", item, relevantChildren.size()));
                initializedItems.addAll(this.initializeItems(relevantChildren, generation + 1));
            }
        }

        return initializedItems;
    }

    private void initializeContentTypesByDetailDisplayModeByDetailType()
    {
        HashMap<DetailDisplayMode, Set<Class<? extends IAttraction>>> contentTypesByDetailDisplayModeLocation = new HashMap<>();
        contentTypesByDetailDisplayModeLocation.put(DetailDisplayMode.ABOVE, new HashSet<Class<? extends IAttraction>>());
        contentTypesByDetailDisplayModeLocation.put(DetailDisplayMode.BELOW, new HashSet<Class<? extends IAttraction>>());
        this.contentTypesByDetailDisplayModeByDetailType.put(DetailType.LOCATION, contentTypesByDetailDisplayModeLocation);

        HashMap<DetailDisplayMode, Set<Class<? extends IAttraction>>> contentTypesByDetailDisplayModeCreditType = new HashMap<>();
        contentTypesByDetailDisplayModeCreditType.put(DetailDisplayMode.ABOVE, new HashSet<Class<? extends IAttraction>>());
        contentTypesByDetailDisplayModeCreditType.put(DetailDisplayMode.BELOW, new HashSet<Class<? extends IAttraction>>());
        this.contentTypesByDetailDisplayModeByDetailType.put(DetailType.CREDIT_TYPE, contentTypesByDetailDisplayModeCreditType);

        HashMap<DetailDisplayMode, Set<Class<? extends IAttraction>>> contentTypesByDetailDisplayModeCategory = new HashMap<>();
        contentTypesByDetailDisplayModeCategory.put(DetailDisplayMode.ABOVE, new HashSet<Class<? extends IAttraction>>());
        contentTypesByDetailDisplayModeCategory.put(DetailDisplayMode.BELOW, new HashSet<Class<? extends IAttraction>>());
        this.contentTypesByDetailDisplayModeByDetailType.put(DetailType.CATEGORY, contentTypesByDetailDisplayModeCategory);

        HashMap<DetailDisplayMode, Set<Class<? extends IAttraction>>> contentTypesByDetailDisplayManufacturer = new HashMap<>();
        contentTypesByDetailDisplayManufacturer.put(DetailDisplayMode.ABOVE, new HashSet<Class<? extends IAttraction>>());
        contentTypesByDetailDisplayManufacturer.put(DetailDisplayMode.BELOW, new HashSet<Class<? extends IAttraction>>());
        this.contentTypesByDetailDisplayModeByDetailType.put(DetailType.MANUFACTURER, contentTypesByDetailDisplayManufacturer);

        HashMap<DetailDisplayMode, Set<Class<? extends IAttraction>>> contentTypesByDetailDisplayStatus = new HashMap<>();
        contentTypesByDetailDisplayStatus.put(DetailDisplayMode.ABOVE, new HashSet<Class<? extends IAttraction>>());
        contentTypesByDetailDisplayStatus.put(DetailDisplayMode.BELOW, new HashSet<Class<? extends IAttraction>>());
        this.contentTypesByDetailDisplayModeByDetailType.put(DetailType.STATUS, contentTypesByDetailDisplayStatus);

        HashMap<DetailDisplayMode, Set<Class<? extends IAttraction>>> contentTypesByDetailDisplayTotalRideCount = new HashMap<>();
        contentTypesByDetailDisplayTotalRideCount.put(DetailDisplayMode.ABOVE, new HashSet<Class<? extends IAttraction>>());
        contentTypesByDetailDisplayTotalRideCount.put(DetailDisplayMode.BELOW, new HashSet<Class<? extends IAttraction>>());
        this.contentTypesByDetailDisplayModeByDetailType.put(DetailType.TOTAL_RIDE_COUNT, contentTypesByDetailDisplayTotalRideCount);
    }

    private static class ViewHolderItem extends RecyclerView.ViewHolder
    {
        final LinearLayout linearLayoutItem;
        final ImageView imageViewExpandToggle;
        final TextView textViewDetailAbove;
        final TextView textViewName;
        final TextView textViewDetailBelow;
        final LinearLayout linearLayoutPrettyPrint;
        final TextView textViewPrettyPrint;


        ViewHolderItem(View view)
        {
            super(view);
            this.linearLayoutItem = view.findViewById(R.id.linearLayoutRecyclerViewItem);
            this.imageViewExpandToggle = view.findViewById(R.id.imageViewRecyclerViewItem);
            this.textViewDetailAbove = view.findViewById(R.id.textViewRecyclerViewItem_UpperDetail);
            this.textViewName = view.findViewById(R.id.textViewRecyclerViewItem_Name);
            this.textViewDetailBelow = view.findViewById(R.id.textViewRecyclerViewItem_LowerDetail);
            this.linearLayoutPrettyPrint = view.findViewById(R.id.linearLayoutRecyclerViewItem_PrettyPrint);
            this.textViewPrettyPrint = view.findViewById(R.id.textViewRecyclerViewItem_PrettyPrint);
        }
    }

    private static class ViewHolderVisitedAttraction extends RecyclerView.ViewHolder
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

    private static class ViewHolderBottomSpacer extends RecyclerView.ViewHolder
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

        if(this.contentRecyclerViewAdapterType == ContentRecyclerViewAdapterType.COUNTABLE && (item.isVisitedAttraction()))
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int typeOfView)
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
                view = layoutInflater.inflate(R.layout.bottom_spacer, viewGroup, false);
                viewHolder = new ViewHolderBottomSpacer(view);
                break;

            default:
                throw new IllegalStateException();
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
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
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        this.recyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView)
    {
        this.recyclerView = null;
        super.onDetachedFromRecyclerView(recyclerView);
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

        if(!this.getRelevantChildren(item).isEmpty())
        {
            viewHolder.imageViewExpandToggle.setVisibility(View.VISIBLE);

            if(this.expandedItems.contains(item))
            {
                viewHolder.imageViewExpandToggle.setImageDrawable(App.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_down));
            }
            else
            {
                viewHolder.imageViewExpandToggle.setImageDrawable(App.getContext().getDrawable(R.drawable.ic_baseline_arrow_drop_right));
            }
        }
        else
        {
            viewHolder.imageViewExpandToggle.setImageDrawable(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_error_outline, R.color.default_color));
        }

        if(this.useDedicatedExpansionOnClickListener)
        {
            viewHolder.imageViewExpandToggle.setOnClickListener(this.expansionOnClickListener);
        }


        if(!this.formatAsPrettyPrint)
        {
            //set typeface
            boolean typefaceSet = false;
            for(Class<? extends IElement> type : typefacesByContentType.keySet())
            {
                if(type.isAssignableFrom(item.getClass()))
                {
                    viewHolder.textViewName.setTypeface(null, typefacesByContentType.get(type));
                    typefaceSet = true;
                    break;
                }
            }

            if(!typefaceSet)
            {
                viewHolder.textViewName.setTypeface(null, Typeface.NORMAL);
            }



            //set special string resource
            boolean specialStringResourceSet = false;
            for(Class<? extends IElement> type : this.specialStringResourcesByType.keySet())
            {
                if(type.isAssignableFrom(item.getClass()))
                {
                    if(item.isVisit())
                    {
                        if(this.specialStringResourcesByType.containsKey(Visit.class))
                        {
                            viewHolder.textViewName.setText(App.getContext().getString(this.specialStringResourcesByType.get(Visit.class), item.getName(), item.getParent().getName()));
                            specialStringResourceSet = true;
                        }
                        break;
                    }
                    else if(item.isProperty() && ((IProperty)item).isDefault())
                    {
                        if(this.specialStringResourcesByType.containsKey(IProperty.class))
                        {
                            viewHolder.textViewName.setText(App.getContext().getString(this.specialStringResourcesByType.get(IProperty.class), item.getName()));
                            specialStringResourceSet = true;
                        }
                        break;
                    }
                }
            }


            //settext
            if(!specialStringResourceSet)
            {
                viewHolder.textViewName.setText(item.getName());
            }



            viewHolder.textViewDetailAbove.setVisibility(View.GONE);
            viewHolder.textViewDetailBelow.setVisibility(View.GONE);

            if(item.isAttraction())
            {
                //decorate details

                Set<DetailType> detailTypesToDiplayAbove = new HashSet<>();
                Set<DetailType> detailTypesToDisplayBelow = new HashSet<>();

                for(DetailType detailType : this.contentTypesByDetailDisplayModeByDetailType.keySet())
                {
                    for(Class<? extends IAttraction> contentType : this.contentTypesByDetailDisplayModeByDetailType.get(detailType).get(DetailDisplayMode.ABOVE))
                    {
                        if(contentType.isInstance(item))
                        {
                            detailTypesToDiplayAbove.add(detailType);
                            break;
                        }
                    }

                    for(Class<? extends IAttraction> contentType : this.contentTypesByDetailDisplayModeByDetailType.get(detailType).get(DetailDisplayMode.BELOW))
                    {
                        if(contentType.isInstance(item))
                        {
                            detailTypesToDisplayBelow.add(detailType);
                            break;
                        }
                    }
                }

                if(detailTypesToDiplayAbove.size() > 0)
                {
                    viewHolder.textViewDetailAbove.setText(this.getSpannableDetailString(item, detailTypesToDiplayAbove));
                    viewHolder.textViewDetailAbove.setVisibility(View.VISIBLE);
                }

                if(detailTypesToDisplayBelow.size() > 0)
                {
                    viewHolder.textViewDetailBelow.setText(this.getSpannableDetailString(item, detailTypesToDisplayBelow));
                    viewHolder.textViewDetailBelow.setVisibility(View.VISIBLE);
                }
            }



            //set tag
            viewHolder.itemView.setTag(item);



            //set onClickListeners
            if(!viewHolder.itemView.hasOnClickListeners())
            {
                if(this.contentRecyclerViewAdapterType == ContentRecyclerViewAdapterType.SELECTABLE)
                {
                    viewHolder.itemView.setOnClickListener(this.selectionOnClickListener);
                    viewHolder.itemView.setOnLongClickListener(new RecyclerOnClickListener(this.recyclerOnClickListener));
                }
                else if(this.recyclerOnClickListener != null)
                {
                    viewHolder.itemView.setOnClickListener(new RecyclerOnClickListener(this.recyclerOnClickListener));
                    viewHolder.itemView.setOnLongClickListener(new RecyclerOnClickListener(this.recyclerOnClickListener));
                }
            }



            //set background color
            if(this.selectedItemsInOrderOfSelection.contains(item))
            {
                viewHolder.itemView.setBackgroundColor(App.getContext().getColor(R.color.selected_color));
            }
            else
            {
                viewHolder.itemView.setBackgroundColor(App.getContext().getColor(R.color.default_color));
            }



            //indentLayout based on generation
            int padding = ConvertTool.convertDpToPx(
                    (int)(App.getContext().getResources().getDimension(R.dimen.expand_toggle_padding_factor) / App.getContext().getResources().getDisplayMetrics().density))
                    * generation;

            viewHolder.linearLayoutItem.setPadding(padding, 0, padding, 0);



            //set visibility
            viewHolder.linearLayoutPrettyPrint.setVisibility(View.GONE);
            viewHolder.linearLayoutItem.setVisibility(View.VISIBLE);
        }
        else //format as pretty print
        {
            //set visibility for pretty print
            viewHolder.linearLayoutItem.setVisibility(View.GONE);

            viewHolder.textViewPrettyPrint.setText(item.getName());
            viewHolder.linearLayoutPrettyPrint.setVisibility(View.VISIBLE);
        }
    }

    private SpannableString getSpannableDetailString(IElement item, Set<DetailType> detailTypes)
    {
        HashMap<String, Integer> typefacesByDetailSubString = new HashMap<>();
        HashMap<DetailType, String> detailSubStringsByDetailType = new HashMap<>();

        for(DetailType detailType : detailTypes)
        {
            switch(detailType)
            {
                case LOCATION:
                {
                    String locationDetail;
                    if(item.isBlueprint())
                    {
                        // as blueprints are not on site attractions, they have no park and "blueprint" is displayed instead
                        locationDetail = App.getContext().getString(R.string.substitute_blueprint);
                        detailSubStringsByDetailType.put(DetailType.LOCATION, locationDetail);
                        typefacesByDetailSubString.put(locationDetail, Typeface.BOLD_ITALIC);
                    }
                    else
                    {
                        locationDetail = item.getParent().getName();
                        detailSubStringsByDetailType.put(DetailType.LOCATION, locationDetail);
                        typefacesByDetailSubString.put(locationDetail, this.typefacesByDetailType.containsKey(DetailType.LOCATION)
                                ? this.typefacesByDetailType.get(DetailType.LOCATION)
                                : Typeface.NORMAL);
                    }
                    break;
                }
                case CREDIT_TYPE:
                {
                    if(item.hasCreditType())
                    {
                        String creditTypeDetail = ((IAttraction)item).getCreditType().getName();
                        detailSubStringsByDetailType.put(DetailType.CREDIT_TYPE, creditTypeDetail);
                        typefacesByDetailSubString.put(creditTypeDetail, this.typefacesByDetailType.containsKey(DetailType.CREDIT_TYPE)
                                ? this.typefacesByDetailType.get(DetailType.CREDIT_TYPE)
                                : Typeface.NORMAL);
                    }
                    else
                    {
                        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSpannableDetailString:: %s has no CreditType", item));
                    }
                    break;
                }
                case CATEGORY:
                {
                    if(item.hasCategory())
                    {
                        String categoryDetail = ((IAttraction)item).getCategory().getName();
                        detailSubStringsByDetailType.put(DetailType.CATEGORY, categoryDetail);
                        typefacesByDetailSubString.put(categoryDetail, this.typefacesByDetailType.containsKey(DetailType.CATEGORY)
                                ? this.typefacesByDetailType.get(DetailType.CATEGORY)
                                : Typeface.NORMAL);
                    }
                    else
                    {
                        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSpannableDetailString:: %s has no Category", item));
                    }
                    break;
                }
                case MANUFACTURER:
                {
                    if(item.hasManufacturer())
                    {
                        String manufacturerDetail = ((IAttraction)item).getManufacturer().getName();
                        detailSubStringsByDetailType.put(DetailType.MANUFACTURER, manufacturerDetail);
                        typefacesByDetailSubString.put(manufacturerDetail, this.typefacesByDetailType.containsKey(DetailType.MANUFACTURER)
                                ? this.typefacesByDetailType.get(DetailType.MANUFACTURER)
                                : Typeface.NORMAL);
                    }
                    else
                    {
                        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSpannableDetailString:: %s has no Manufacturer", item));
                    }
                    break;
                }
                case STATUS:
                {
                    if(item.hasStatus())
                    {
                        String statusDetail = ((IAttraction)item).getStatus().getName();
                        detailSubStringsByDetailType.put(DetailType.STATUS, statusDetail);
                        typefacesByDetailSubString.put(statusDetail, this.typefacesByDetailType.containsKey(DetailType.STATUS)
                                ? this.typefacesByDetailType.get(DetailType.STATUS)
                                : Typeface.NORMAL);
                    }
                    else
                    {
                        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSpannableDetailString:: %s has no Status", item));
                    }
                    break;
                }
                case TOTAL_RIDE_COUNT:
                {
                    String totalRideCountDetail = App.getContext().getString(R.string.text_total_rides, ((IAttraction)item).getTotalRideCount());
                    detailSubStringsByDetailType.put(DetailType.TOTAL_RIDE_COUNT, totalRideCountDetail);
                    typefacesByDetailSubString.put(totalRideCountDetail, this.typefacesByDetailType.containsKey(DetailType.TOTAL_RIDE_COUNT)
                            ? this.typefacesByDetailType.get(DetailType.TOTAL_RIDE_COUNT)
                            : Typeface.NORMAL);
                    break;
                }
                default:
                    Log.e(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSpannableDetailString:: DetailType [%s] for %s not found",
                            DetailType.getValue(detailType.ordinal()), item));
                    break;
            }
        }

        return StringTool.buildSpannableString(this.getOrderedDetailString(detailSubStringsByDetailType), typefacesByDetailSubString);
    }

    private String getOrderedDetailString(HashMap<DetailType, String> detailSubsStringsByDetailType)
    {
        StringBuilder stringBuilder = new StringBuilder();

        for(DetailType detailType : App.preferences.getDetailsOrder())
        {
            if(detailSubsStringsByDetailType.containsKey(detailType))
            {
                if(stringBuilder.length() != 0)
                {
                    stringBuilder.append(" - ");
                }
                stringBuilder.append(detailSubsStringsByDetailType.get(detailType));
            }
        }
        return stringBuilder.toString();
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
            viewHolder.textViewCount.setText(String.valueOf(visitedAttraction.getTotalRideCount()));

            viewHolder.imageViewIncrease.setTag(visitedAttraction);
            if(!viewHolder.imageViewIncrease.hasOnClickListeners() && this.increaseRideCountOnClickListener != null)
            {
                viewHolder.imageViewIncrease.setOnClickListener(this.increaseRideCountOnClickListener);
            }

            viewHolder.imageViewDecrease.setTag(visitedAttraction);
            if(!viewHolder.imageViewDecrease.hasOnClickListeners() && this.decreaseRideCountOnClickListener != null)
            {
                viewHolder.imageViewDecrease.setOnClickListener(this.decreaseRideCountOnClickListener);
            }
        }
        else
        {
            viewHolder.linearLayoutEditable.setVisibility(View.GONE);

            viewHolder.textViewPrettyPrint.setText(App.getContext().getString(R.string.text_visited_attraction_pretty_print, visitedAttraction.getTotalRideCount(), visitedAttraction.getName()));
            viewHolder.textViewPrettyPrint.setVisibility(View.VISIBLE);
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

    private ArrayList<IElement> getRelevantChildren(IElement item)
    {
        ArrayList<IElement> distinctRelevantChildren = new ArrayList<>();

        for(Class<? extends IElement> childType : this.relevantChildTypes)
        {
            ArrayList<IElement> allRelevantChildren = new ArrayList<>(item.fetchChildrenOfType(childType));
            for(IElement child : allRelevantChildren)
            {
                if(!distinctRelevantChildren.contains(child))
                {
                    distinctRelevantChildren.add(child);
                }
            }
        }

        return distinctRelevantChildren;
    }

    private IElement getParentOfRelevantChild(IElement item)
    {
        if(item.isAttraction())
        {
            return this.getGroupHeaderForItem(item);
        }
        else if(!item.isOrphan())
        {
            Log.e(Constants.LOG_TAG, String.format("********** IT HAPPENED! CRVA.getParentOfRelevantChild for item not being OrphanElement or Attraction was called! Class [%s]",
                    item.getClass().getSimpleName()));
            return this.items.get(this.items.indexOf(item.getParent()));
        }
        else
        {
            return null;
        }
    }

    private IGroupHeader getGroupHeaderForItem(IElement groupElement)
    {
        for(IElement item : this.items)
        {
            if(item.isGroupHeader())
            {
                if(item.getChildren().contains(groupElement))
                {
                    return (IGroupHeader)item;
                }
            }
        }
        return null;
    }

    public ContentRecyclerViewAdapter setOnClickListener(RecyclerOnClickListener.OnClickListener onClickListener)
    {
        this.recyclerOnClickListener = onClickListener;
        return this;
    }

    public ContentRecyclerViewAdapter addIncreaseRideCountOnClickListener(View.OnClickListener increaseOnClickListener)
    {
        this.increaseRideCountOnClickListener = increaseOnClickListener;
        return this;
    }

    public ContentRecyclerViewAdapter addDecreaseRideCountOnClickListener(View.OnClickListener decreaseOnClickListener)
    {
        this.decreaseRideCountOnClickListener = decreaseOnClickListener;
        return this;
    }

    public ContentRecyclerViewAdapter toggleExpansion(IElement item)
    {
        if(!this.relevantChildTypes.isEmpty())
        {
            if(!this.expandedItems.contains(item))
            {
                this.expandItem(item);
            }
            else
            {
                this.collapseItem(item, true);
            }
        }

        return this;
    }

    public ContentRecyclerViewAdapter expandAll()
    {
        if(!this.items.isEmpty() && !this.isAllExpanded())
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

            Log.v(Constants.LOG_TAG, "ContentRecyclerViewAdapter.expandAll:: all items expanded");
        }
        else
        {
            Log.v(Constants.LOG_TAG, "ContentRecyclerViewAdapter.expandAll:: no items to expand");
        }

        return this;
    }

    public ContentRecyclerViewAdapter expandGroupHeaderOfElement(IElement element)
    {
        for(IElement item : this.items)
        {
            if(item.isGroupHeader() && item.getChildren().contains(element))
            {
                this.expandItem(item);
                break;
            }
        }

        return this;
    }


    public ContentRecyclerViewAdapter expandItem(IElement item)
    {
        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.expandItem:: expanding item %s...", item));

        if(!this.expandedItems.contains(item))
        {
            ArrayList<IElement> relevantChildren = this.getRelevantChildren(item);
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

        return this;
    }

    public boolean isAllExpanded()
    {
        for(IElement item : this.items)
        {
            for(IElement relevantChild : this.getRelevantChildren(item))
            {
                if(relevantChild.isLocation())
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
                else if(relevantChild.isAttraction() || relevantChild.isVisitedAttraction() || relevantChild.isVisit())
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

    public ContentRecyclerViewAdapter collapseAll()
    {
        if(!this.items.isEmpty() && !this.isAllCollapsed())
        {
            List<IElement> itemsList = new ArrayList<>(this.expandedItems);

            for(IElement item : itemsList)
            {
                if(this.expandedItems.contains(item))
                {
                    this.collapseItem(item, false);
                }
            }
            Log.v(Constants.LOG_TAG, "ContentRecyclerViewAdapter.collapseAll:: all items collapsed");
        }
        else
        {
            Log.v(Constants.LOG_TAG, "ContentRecyclerViewAdapter.collapseAll:: no items to collapse");
        }

        return this;
    }

    private void collapseItem(IElement item, boolean scrollToItem)
    {
        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.collapseItem:: collapsing item %s...", item));

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
                        this.collapseItem(child, false);
                    }

                    int index = this.items.indexOf(child);
                    this.items.remove(child);
                    notifyItemRemoved(index);

                    this.generationByItem.remove(child);

                    Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.collapseItem:: removed child %s at index [%d]", child, index));
                }

                if(scrollToItem)
                {
                    this.scrollToItem(item);
                }

                Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.collapseItem:: collapsed item %s", item));
            }
        }
        else
        {
            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.collapseItem:: %s not expanded", item));
        }
    }

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
                    collapseItem(item, true);
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
                    if(selectMultipleItems)
                    {
                        setItemSelected(selectedItem);

                        if(!relevantChildTypes.isEmpty())
                        {
                            setItemsSelected(getRelevantChildren(selectedItem));
                            selectParentIfAllRelevantChildrenAreSelected(getParentOfRelevantChild(selectedItem));
                        }
                    }
                    else
                    {
                        if(!selectedItem.isGroupHeader())
                        {
                            setItemDeselected(getLastSelectedItem());
                            setItemSelected(selectedItem);
                        }
                        else
                        {
                            Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.getSelectionOnClickListener.onClick:: %s clicked - GroupHeaders are ignored.", selectedItem));
                        }
                    }
                }
                else
                {
                    setItemDeselected(selectedItem);

                    if(selectMultipleItems)
                    {
                        if(!relevantChildTypes.isEmpty())
                        {
                            setItemsDeselected(getRelevantChildren(selectedItem));
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

    private void selectParentIfAllRelevantChildrenAreSelected(IElement parent)
    {
        if(parent != null && this.allRelevantChildrenAreSelected(parent))
        {
            this.setItemSelected(parent);
        }
    }

    private void deselectParentIfNotAllRelevantChildrenAreSelected(IElement parent)
    {
        if(parent != null && !allRelevantChildrenAreSelected(parent))
        {
            this.setItemDeselected(parent);
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

    public void setAllItemsSelected()
    {
        Log.d(Constants.LOG_TAG, "ContentRecyclerViewAdapter.setAllItemsSelected:: selecting all items...");

        this.selectedItemsInOrderOfSelection.clear();

        for(IElement item : this.items)
        {
            this.setItemSelected(item);

            if(!this.expandedItems.contains(item))
            {
                List<IElement> relevantChildren = this.getRelevantChildren(item);
                if(!relevantChildren.isEmpty())
                {
                    this.setItemsSelected(relevantChildren);
                }
            }
        }
    }

    private void setItemsSelected(List<IElement> elements)
    {
        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.setItemsSelected:: selecting [%d] elements", elements.size()));

        for(IElement element : elements)
        {
            this.setItemSelected(element);
        }
    }

    public void setItemSelected(IElement element)
    {
        if(element != null)
        {
            if(!this.selectedItemsInOrderOfSelection.contains(element))
            {
                this.selectedItemsInOrderOfSelection.add(element);
                Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.setItemSelected:: %s selected", element));
            }

            if(this.items.contains(element))
            {
                notifyItemChanged(this.items.indexOf(element));
            }
        }
    }

    public void setAllItemsDeselected()
    {
        Log.i(Constants.LOG_TAG, "ContentRecyclerViewAdapter.setAllItemsDeselected:: deselecting all elements...");

        LinkedList<IElement> selectedItems = new LinkedList<>(this.selectedItemsInOrderOfSelection);
        this.setItemsDeselected(selectedItems);
    }

    private void setItemsDeselected(List<IElement> elements)
    {
        Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.setItemsDeselected:: deselecting [%d] elements", elements.size()));

        for(IElement element : elements)
        {
            this.setItemDeselected(element);
        }
    }

    public void setItemDeselected(IElement element)
    {
        if(element != null)
        {
            if(this.selectedItemsInOrderOfSelection.contains(element))
            {
                selectedItemsInOrderOfSelection.remove(element);
                Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.setItemDeselected:: %s deselected", element));
            }

            if(this.items.contains(element))
            {
                notifyItemChanged(this.items.indexOf(element));
            }
        }
    }

    public boolean isAllSelected()
    {
        List<IElement> items = new ArrayList<>(this.items);
        items.removeAll(this.selectedItemsInOrderOfSelection);

        return items.isEmpty();
    }

    public LinkedList<IElement> getSelectedItemsInOrderOfSelection()
    {
        LinkedList<IElement> selectedItems = new LinkedList<>();

        for(IElement item : this.selectedItemsInOrderOfSelection)
        {
            if(!(item.isGroupHeader()))
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
        if(item != null && this.items.contains(item) && this.recyclerView != null)
        {
            Log.d(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.scrollToItem:: scrolling to %s", item));
            recyclerView.scrollToPosition(items.indexOf(item));
        }
    }

    public ContentRecyclerViewAdapter addBottomSpacer()
    {
        if(!this.items.isEmpty() && !(this.items.get(this.items.size() - 1) instanceof BottomSpacer))
        {
            this.items.add(new BottomSpacer());
            notifyItemInserted(this.items.size() - 1);
            Log.v(Constants.LOG_TAG, "ContentRecyclerViewAdapter.addBottomSpacer:: added BottomSpacer");
        }

        return this;
    }

    public ContentRecyclerViewAdapter setTypefaceForContentType(Class<? extends IElement> type, int typeface)
    {
        if(typeface <= 3)
        {
            this.typefacesByContentType.put(type, typeface);
            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.setTypefaceForContentType:: [%d] set for [%s]", typeface, type.getSimpleName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG, "ContentRecyclerViewAdapter.setTypefaceForContentType:: unknown typeface");
        }

        return this;
    }

    public ContentRecyclerViewAdapter clearTypefacesForContentType()
    {
        this.typefacesByContentType.clear();
        return this;
    }

    public ContentRecyclerViewAdapter setTypefaceForDetailType(DetailType type, int typeface)
    {
        if(typeface <= 3)
        {
            this.typefacesByDetailType.put(type, typeface);
            Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.setTypefaceForDetailType:: [%d] set for [%s]", typeface, type));
        }
        else
        {
            Log.e(Constants.LOG_TAG, "ContentRecyclerViewAdapter.setTypefaceForDetailType:: unknown typeface");
        }

        return this;
    }

    public ContentRecyclerViewAdapter clearTypefacesForDetailType()
    {
        this.typefacesByDetailType.clear();
        return this;
    }

    /**
     *  set a string resource which replaces the element.getName() string for the given class
     */
    public ContentRecyclerViewAdapter setSpecialStringResourceForType(Class<? extends IElement> type,  int stringResource)
    {
        this.specialStringResourcesByType.put(type, stringResource);
        return this;
    }

    public ContentRecyclerViewAdapter clearDetailTypesAndModeForContentType()
    {
        this.contentTypesByDetailDisplayModeByDetailType.clear();
        this.initializeContentTypesByDetailDisplayModeByDetailType();
        return this;
    }

    public ContentRecyclerViewAdapter setDetailTypesAndModeForContentType(Class<? extends IAttraction> contentType, DetailType detailType, DetailDisplayMode detailDisplayMode)
    {
        this.contentTypesByDetailDisplayModeByDetailType.get(detailType).get(detailDisplayMode).add(contentType);
        return this;
    }

    public void setFormatAsPrettyPrint(boolean formatAsPrettyPrint)
    {
        this.formatAsPrettyPrint = formatAsPrettyPrint;
        this.expandAll();

        notifyDataSetChanged();
    }

    public GroupType getGroupType()
    {
        return this.groupType;
    }

    public ContentRecyclerViewAdapter setUseDedicatedExpansionOnClickListener(boolean useDedicatedExpansionOnClickListener)
    {
        this.useDedicatedExpansionOnClickListener = useDedicatedExpansionOnClickListener;
        Log.v(Constants.LOG_TAG, String.format("ContentRecyclerViewAdapter.setUseDedicatedExpansionOnClickListener:: set to [%S]", useDedicatedExpansionOnClickListener));
        return this;
    }
}