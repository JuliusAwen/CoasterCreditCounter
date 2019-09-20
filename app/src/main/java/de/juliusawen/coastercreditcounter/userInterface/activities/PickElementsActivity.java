package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Category;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Status;
import de.juliusawen.coastercreditcounter.dataModel.temporaryElements.GroupHeader;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.globals.enums.SortType;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.SortTool;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailDisplayMode;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailType;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.GroupType;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.RecyclerOnClickListener;

public class PickElementsActivity extends BaseActivity
{
    private PickElementsActivityViewModel viewModel;
    private RecyclerView recyclerView;
    private TextView textViewSelectOrDeselectAll;
    private RadioButton radioButtonSelectOrDeselectAll;
    private boolean useSelectOrDeselectAllBar = true;


    protected void setContentView()
    {
        setContentView(R.layout.activity_pick_elements);
    }

    protected void create()
    {
        this.viewModel = ViewModelProviders.of(this).get(PickElementsActivityViewModel.class);

        if(this.viewModel.requestCode == null)
        {
            this.viewModel.requestCode = RequestCode.values()[getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, 0)];
        }

        this.viewModel.isSimplePick = getIntent().getBooleanExtra(Constants.EXTRA_SIMPLE_PICK, false);

        if(this.viewModel.elementsToPickFrom == null)
        {
            this.viewModel.elementsToPickFrom = App.content.getContentByUuidStrings(getIntent().getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS));
        }

        if(this.viewModel.optionsMenuAgent == null)
        {
            this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
        }

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            switch(this.viewModel.requestCode)
            {
                case PICK_ATTRACTIONS:
                case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
                case ASSIGN_CATEGORY_TO_ATTRACTIONS:
                case ASSIGN_MANUFACTURERS_TO_ATTRACTIONS:
                case ASSIGN_STATUS_TO_ATTRACTIONS:
                {
                    Set<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
                    childTypesToExpand.add(IAttraction.class);

                    if(this.viewModel.requestCode != RequestCode.PICK_ATTRACTIONS)
                    {
                        this.viewModel.elementsToPickFrom = SortTool.sortElements(this.viewModel.elementsToPickFrom, SortType.BY_NAME, SortOrder.ASCENDING);
                    }

                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            childTypesToExpand,
                            true)
                            .setTypefaceForType(GroupHeader.class, Typeface.BOLD);
                            this.setDetailModesAndGroupElements(GroupType.CATEGORY);
                    break;
                }

                case PICK_CREDIT_TYPE:
                case PICK_CATEGORY:
                case PICK_MANUFACTURER:
                case PICK_STATUS:
                {
                    this.useSelectOrDeselectAllBar = false;

                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            null,
                            false)
                            .setTypefaceForType(CreditType.class, Typeface.BOLD)
                            .setTypefaceForType(Category.class, Typeface.BOLD)
                            .setTypefaceForType(Manufacturer.class, Typeface.BOLD)
                            .setTypefaceForType(Status.class, Typeface.BOLD);

                    super.addFloatingActionButton();
                    this.decorateFloatingActionButtonAdd();
                    break;
                }

                case PICK_VISIT:
                {
                    this.useSelectOrDeselectAllBar = false;

                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            null,
                            false)
                            .setTypefaceForType(Status.class, Typeface.BOLD)
                            .setSpecialStringResourceForType(Visit.class, R.string.text_visit_display_full_name);
                    break;
                }

                default:
                {
                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            null,
                            true)
                            .setTypefaceForType(this.viewModel.elementsToPickFrom.get(0).getClass(), Typeface.BOLD);
                    break;
                }

            }
        }
        this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewOnClickListener());
        this.recyclerView = findViewById(R.id.recyclerViewPickElements);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

        super.addHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE)), getText(R.string.help_text_pick_elements));
        super.addToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE));

        if(!this.viewModel.isSimplePick)
        {
            super.addFloatingActionButton();
            this.decorateFloatingActionButtonCheck();
        }

        this.addSelectOrDeselectAllBar();
    }

    @Override
    protected void onDestroy()
    {
        this.recyclerView.setAdapter(null);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode != Activity.RESULT_OK)
        {
            return;
        }

        switch(RequestCode.getValue(requestCode))
        {
            case CREATE_CREDIT_TYPE:
            case CREATE_CATEGORY:
            case CREATE_MANUFACTURER:
            case CREATE_STATUS:
            {
                IElement returnElement = ResultFetcher.fetchResultElement(data);

                if(returnElement != null)
                {
                    returnResult(RESULT_OK, returnElement);
                }
                else
                {
                    Log.d(Constants.LOG_TAG, "PickElementsActivity.onActivityResult:: no element returned");
                }

                break;
            }
        }

    }

    @Override
    protected Menu createOptionsMenu(Menu menu)
    {
        switch(this.viewModel.requestCode)
        {
            case PICK_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURERS_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                this.viewModel.optionsMenuAgent
                        .add(OptionsItem.SORT_BY)
                            .addToGroup(OptionsItem.SORT_BY_NAME, OptionsItem.SORT_BY)
                                .addToGroup(OptionsItem.SORT_BY_NAME_ASCENDING, OptionsItem.SORT_BY_NAME)
                                .addToGroup(OptionsItem.SORT_BY_NAME_DESCENDING, OptionsItem.SORT_BY_NAME)
                            .addToGroup(OptionsItem.SORT_BY_LOCATION, OptionsItem.SORT_BY)
                                .addToGroup(OptionsItem.SORT_BY_LOCATION_ASCENDING, OptionsItem.SORT_BY_LOCATION)
                                .addToGroup(OptionsItem.SORT_BY_LOCATION_DESCENDING, OptionsItem.SORT_BY_LOCATION)
                            .addToGroup(OptionsItem.SORT_BY_CATEGORY, OptionsItem.SORT_BY)
                                .addToGroup(OptionsItem.SORT_BY_CATEGORY_ASCENDING, OptionsItem.SORT_BY_CATEGORY)
                                .addToGroup(OptionsItem.SORT_BY_CATEGORY_DESCENDING, OptionsItem.SORT_BY_CATEGORY)
                            .addToGroup(OptionsItem.SORT_BY_MANUFACTURER, OptionsItem.SORT_BY)
                                .addToGroup(OptionsItem.SORT_BY_MANUFACTURER_ASCENDING, OptionsItem.SORT_BY_MANUFACTURER)
                                .addToGroup(OptionsItem.SORT_BY_MANUFACTURER_DESCENDING, OptionsItem.SORT_BY_MANUFACTURER)
                        .add(OptionsItem.GROUP_BY)
                            .addToGroup(OptionsItem.GROUP_BY_LOCATION, OptionsItem.GROUP_BY)
                            .addToGroup(OptionsItem.GROUP_BY_CREDIT_TYPE, OptionsItem.GROUP_BY)
                            .addToGroup(OptionsItem.GROUP_BY_CATEGORY, OptionsItem.GROUP_BY)
                            .addToGroup(OptionsItem.GROUP_BY_MANUFACTURER, OptionsItem.GROUP_BY)
                            .addToGroup(OptionsItem.GROUP_BY_STATUS, OptionsItem.GROUP_BY)
                        .add(OptionsItem.EXPAND_ALL)
                        .add(OptionsItem.COLLAPSE_ALL);
                break;

            case PICK_CATEGORY:
            case PICK_MANUFACTURER:
            case PICK_STATUS:
                this.viewModel.optionsMenuAgent
                        .add(OptionsItem.SORT)
                            .addToGroup(OptionsItem.SORT_ASCENDING, OptionsItem.SORT)
                            .addToGroup(OptionsItem.SORT_DESCENDING, OptionsItem.SORT);
                break;
        }

        return this.viewModel.optionsMenuAgent.create(menu);
    }

    @Override
    protected Menu prepareOptionsMenu(Menu menu)
    {
        boolean sortByLocationVisible = this.viewModel.requestCode != RequestCode.PICK_ATTRACTIONS;

        boolean sortByLocationEnabled = this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupType.LOCATION;
        boolean sortByCategoryEnabled = this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupType.CATEGORY;
        boolean sortByManufacturerEnabled = this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupType.MANUFACTURER;

        boolean groupByLocationVisible = this.viewModel.requestCode != RequestCode.PICK_ATTRACTIONS;

        boolean groupByLocationEnabled = this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupType.LOCATION;
        boolean groupByCreditTypeEnabled = this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupType.CREDIT_TYPE;
        boolean groupByCategoryEnabled = this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupType.CATEGORY;
        boolean groupByManufacturerEnabled = this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupType.MANUFACTURER;
        boolean groupByStatusEnabled = this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupType.STATUS;

        switch(this.viewModel.requestCode)
        {
            case PICK_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURERS_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                this.viewModel.optionsMenuAgent
                        .setEnabled(OptionsItem.SORT_BY, sortByLocationEnabled || sortByCategoryEnabled || sortByManufacturerEnabled)
                            .setVisible(OptionsItem.SORT_BY_LOCATION, sortByLocationVisible)
                            .setEnabled(OptionsItem.SORT_BY_LOCATION, sortByLocationEnabled)
                            .setEnabled(OptionsItem.SORT_BY_CATEGORY, sortByCategoryEnabled)
                            .setEnabled(OptionsItem.SORT_BY_MANUFACTURER, sortByManufacturerEnabled)
                        .setEnabled(OptionsItem.GROUP_BY, groupByLocationEnabled || groupByCategoryEnabled || groupByManufacturerEnabled || groupByStatusEnabled)
                            .setVisible(OptionsItem.GROUP_BY_LOCATION, groupByLocationVisible)
                            .setEnabled(OptionsItem.GROUP_BY_LOCATION, groupByLocationEnabled)
                            .setEnabled(OptionsItem.GROUP_BY_CREDIT_TYPE, groupByCreditTypeEnabled)
                            .setEnabled(OptionsItem.GROUP_BY_CATEGORY, groupByCategoryEnabled)
                            .setEnabled(OptionsItem.GROUP_BY_MANUFACTURER, groupByManufacturerEnabled)
                            .setEnabled(OptionsItem.GROUP_BY_STATUS, groupByStatusEnabled);
                break;
        }

        return this.viewModel.optionsMenuAgent.prepare(menu);
    }

    @Override
    public boolean handleOptionsItemSelected(OptionsItem item)
    {
        switch(item)
        {
            case SORT_ASCENDING:
            case SORT_BY_NAME_ASCENDING:
                this.viewModel.elementsToPickFrom = SortTool.sortElements(viewModel.elementsToPickFrom, SortType.BY_NAME, SortOrder.ASCENDING);
                this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
                return true;

            case SORT_DESCENDING:
            case SORT_BY_NAME_DESCENDING:
                this.viewModel.elementsToPickFrom = SortTool.sortElements(viewModel.elementsToPickFrom, SortType.BY_NAME, SortOrder.DESCENDING);
                this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
                return true;

            case SORT_BY_LOCATION_ASCENDING:
                this.viewModel.elementsToPickFrom = SortTool.sortElements(viewModel.elementsToPickFrom, SortType.BY_LOCATION, SortOrder.ASCENDING);
                this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
                return true;

            case SORT_BY_LOCATION_DESCENDING:
                this.viewModel.elementsToPickFrom = SortTool.sortElements(viewModel.elementsToPickFrom, SortType.BY_LOCATION, SortOrder.DESCENDING);
                this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
                return true;

            case SORT_BY_CATEGORY_ASCENDING:
                this.viewModel.elementsToPickFrom = SortTool.sortElements(viewModel.elementsToPickFrom, SortType.BY_CATEGORY, SortOrder.ASCENDING);
                this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
                return true;

            case SORT_BY_CATEGORY_DESCENDING:
                this.viewModel.elementsToPickFrom = SortTool.sortElements(viewModel.elementsToPickFrom, SortType.BY_CATEGORY, SortOrder.DESCENDING);
                this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
                return true;

            case SORT_BY_MANUFACTURER_ASCENDING:
                this.viewModel.elementsToPickFrom = SortTool.sortElements(viewModel.elementsToPickFrom, SortType.BY_MANUFACTURER, SortOrder.ASCENDING);
                this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
                return true;

            case SORT_BY_MANUFACTURER_DESCENDING:
                this.viewModel.elementsToPickFrom = SortTool.sortElements(viewModel.elementsToPickFrom, SortType.BY_MANUFACTURER, SortOrder.DESCENDING);
                this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
                return true;

            case GROUP_BY_LOCATION:
                this.setDetailModesAndGroupElements(GroupType.LOCATION);
                return true;

            case GROUP_BY_CREDIT_TYPE:
                this.setDetailModesAndGroupElements(GroupType.CREDIT_TYPE);
                return true;

            case GROUP_BY_CATEGORY:
                this.setDetailModesAndGroupElements(GroupType.CATEGORY);
                return true;

            case GROUP_BY_MANUFACTURER:
                this.setDetailModesAndGroupElements(GroupType.MANUFACTURER);
                return true;

            case GROUP_BY_STATUS:
                this.setDetailModesAndGroupElements(GroupType.STATUS);
                return true;

            case EXPAND_ALL:
                this.viewModel.contentRecyclerViewAdapter.expandAll();
                return true;

            case COLLAPSE_ALL:
                this.viewModel.contentRecyclerViewAdapter.collapseAll();
                return true;

            default:
                return false;
        }
    }

    private void setDetailModesAndGroupElements(GroupType groupType)
    {
        this.viewModel.contentRecyclerViewAdapter
                .clearTypefaceForTypes()
                .setTypefaceForType(GroupHeader.class, Typeface.BOLD)
                .clearDisplayModesForDetails();

        if(this.viewModel.requestCode == RequestCode.PICK_ATTRACTIONS)
        {
            switch(groupType)
            {
                case CREDIT_TYPE:
                    this.viewModel.contentRecyclerViewAdapter
                            .setDisplayModeForDetail(DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDisplayModeForDetail(DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .setDisplayModeForDetail(DetailType.STATUS, DetailDisplayMode.BELOW)
                            .setDisplayModeForDetail(DetailType.TOTAL_RIDE_COUNT, DetailDisplayMode.BELOW)
                            .groupItems(GroupType.CREDIT_TYPE);
                    break;

                case CATEGORY:
                    this.viewModel.contentRecyclerViewAdapter
                            .setDisplayModeForDetail(DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDisplayModeForDetail(DetailType.STATUS, DetailDisplayMode.BELOW)
                            .setDisplayModeForDetail(DetailType.TOTAL_RIDE_COUNT, DetailDisplayMode.BELOW)
                            .groupItems(GroupType.CATEGORY);
                    break;

                case MANUFACTURER:
                    this.viewModel.contentRecyclerViewAdapter
                            .setDisplayModeForDetail(DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .setDisplayModeForDetail(DetailType.STATUS, DetailDisplayMode.BELOW)
                            .setDisplayModeForDetail(DetailType.TOTAL_RIDE_COUNT, DetailDisplayMode.BELOW)
                            .groupItems(GroupType.MANUFACTURER);
                    break;

                case STATUS:
                    this.viewModel.contentRecyclerViewAdapter
                            .setDisplayModeForDetail(DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDisplayModeForDetail(DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .setDisplayModeForDetail(DetailType.STATUS, DetailDisplayMode.BELOW)
                            .setDisplayModeForDetail(DetailType.TOTAL_RIDE_COUNT, DetailDisplayMode.BELOW)
                            .groupItems(GroupType.STATUS);
                    break;
            }
        }
        else
        {
            switch(groupType)
            {
                case LOCATION:
                    this.viewModel.contentRecyclerViewAdapter
                            .setDisplayModeForDetail(DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDisplayModeForDetail(DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .groupItems(GroupType.LOCATION);
                    break;

                case CREDIT_TYPE:
                    this.viewModel.contentRecyclerViewAdapter
                            .setDisplayModeForDetail(DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDisplayModeForDetail(DetailType.LOCATION, DetailDisplayMode.BELOW)
                            .setDisplayModeForDetail(DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .groupItems(GroupType.CREDIT_TYPE);
                    break;

                case CATEGORY:
                    this.viewModel.contentRecyclerViewAdapter
                            .setDisplayModeForDetail(DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDisplayModeForDetail(DetailType.LOCATION, DetailDisplayMode.BELOW)
                            .setDisplayModeForDetail(DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .groupItems(GroupType.CATEGORY);
                    break;

                case MANUFACTURER:
                    this.viewModel.contentRecyclerViewAdapter
                            .setDisplayModeForDetail(DetailType.LOCATION, DetailDisplayMode.BELOW)
                            .setDisplayModeForDetail(DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .groupItems(GroupType.MANUFACTURER);
                    break;

                case STATUS:
                    this.viewModel.contentRecyclerViewAdapter
                            .setDisplayModeForDetail(DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDisplayModeForDetail(DetailType.LOCATION, DetailDisplayMode.BELOW)
                            .setDisplayModeForDetail(DetailType.CATEGORY, DetailDisplayMode.BELOW)
                            .groupItems(GroupType.STATUS);
                    break;
            }
        }
    }

    private void decorateFloatingActionButtonCheck()
    {
        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_check, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!viewModel.contentRecyclerViewAdapter.getSelectedItemsInOrderOfSelection().isEmpty())
                {
                    Log.d(Constants.LOG_TAG, "PickElementsActivity.onClickFloatingActionButtonCheck:: accepted - return code <OK>");
                    returnResult(RESULT_OK, null);
                }
                else
                {
                    Log.d(Constants.LOG_TAG, "PickElementsActivity.onClickFloatingActionButton<Check>:: no element selected");
                    Toaster.makeToast(PickElementsActivity.this, getString(R.string.error_no_entry_selected));
                }
            }
        });
        super.setFloatingActionButtonVisibility(true);
    }

    private void decorateFloatingActionButtonAdd()
    {
        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_add, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onClickFloatingActionButton<Add>:: FloatingActionButton pressed - RequestCode [%s]", viewModel.requestCode));

                switch(viewModel.requestCode)
                {
                    case PICK_CREDIT_TYPE:
                        ActivityDistributor.startActivityCreateForResult(PickElementsActivity.this, RequestCode.CREATE_CREDIT_TYPE, null);
                        break;

                    case PICK_CATEGORY:
                        ActivityDistributor.startActivityCreateForResult(PickElementsActivity.this, RequestCode.CREATE_CATEGORY, null);
                        break;

                    case PICK_MANUFACTURER:
                        ActivityDistributor.startActivityCreateForResult(PickElementsActivity.this, RequestCode.CREATE_MANUFACTURER, null);
                        break;

                    case PICK_STATUS:
                        ActivityDistributor.startActivityCreateForResult(PickElementsActivity.this, RequestCode.CREATE_STATUS, null);
                        break;

                        default:
                            break;
                }
            }
        });
        super.setFloatingActionButtonVisibility(true);
    }

    private void addSelectOrDeselectAllBar()
    {
        LinearLayout linearLayoutSelectAll = this.findViewById(android.R.id.content).findViewById(R.id.linearLayoutPickElements_SelectAll);
        linearLayoutSelectAll.setVisibility(this.useSelectOrDeselectAllBar ? View.VISIBLE : View.GONE);

        this.textViewSelectOrDeselectAll = linearLayoutSelectAll.findViewById(R.id.textViewPickElements_SelectAll);

        if(this.viewModel.contentRecyclerViewAdapter.isAllSelected())
        {
            this.textViewSelectOrDeselectAll.setText(R.string.text_deselect_all);
        }
        else
        {
            this.textViewSelectOrDeselectAll.setText(R.string.text_select_all);
        }

        this.radioButtonSelectOrDeselectAll = linearLayoutSelectAll.findViewById(R.id.radioButtonPickElements_SelectAll);
        this.radioButtonSelectOrDeselectAll.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(Constants.LOG_TAG, "PickElementsActivity.onClickSelectOrDeselectAllBar:: RadioButton clicked");

                if(textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_select_all)))
                {
                    viewModel.contentRecyclerViewAdapter.selectAllItems();
                    changeRadioButtonToDeselectAll();
                }
                else if(textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_deselect_all)))
                {
                    viewModel.contentRecyclerViewAdapter.deselectAllItems();
                    changeRadioButtonToSelectAll();
                }
            }
        });
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(PickElementsActivity.this.viewModel.isSimplePick)
                {
                    Log.d(Constants.LOG_TAG, "PickElementsActivity.onClickItem:: simple pick - return code <OK>");
                    returnResult(RESULT_OK, null);
                }
                else
                {
                    if(!view.isSelected() && viewModel.contentRecyclerViewAdapter.isAllSelected())
                    {
                        changeRadioButtonToDeselectAll();
                    }
                    else
                    {
                        if(textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_deselect_all)))
                        {
                            changeRadioButtonToSelectAll();
                        }
                    }
                }
            }

            @Override
            public boolean onLongClick(View view)
            {
                return true;
            }
        };
    }

    private void changeRadioButtonToSelectAll()
    {
        this.textViewSelectOrDeselectAll.setText(R.string.text_select_all);
        this.radioButtonSelectOrDeselectAll.setChecked(false);

        Log.v(Constants.LOG_TAG, String.format("PickElementsActivity.changeRadioButtonToSelectAll:: changed RadioButtonText to [%s]", this.textViewSelectOrDeselectAll.getText()));
    }

    private void changeRadioButtonToDeselectAll()
    {
        this.textViewSelectOrDeselectAll.setText(R.string.text_deselect_all);
        this.radioButtonSelectOrDeselectAll.setChecked(false);

        Log.v(Constants.LOG_TAG, String.format("PickElementsActivity.changeRadioButtonToDeselectAll:: changed RadioButtonText to [%s]", this.textViewSelectOrDeselectAll.getText()));
    }

    private void returnResult(int resultCode, IElement element)
    {
        Log.i(Constants.LOG_TAG, String.format("PickElementsActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            if(element == null)
            {
                switch(this.viewModel.requestCode)
                {
                    case PICK_VISIT:
                    case PICK_CREDIT_TYPE:
                    case PICK_CATEGORY:
                    case PICK_MANUFACTURER:
                    case PICK_STATUS:
                    {
                        Log.d(Constants.LOG_TAG, String.format("PickElementsActivity.returnResult:: returning %s", this.viewModel.contentRecyclerViewAdapter.getLastSelectedItem()));
                        intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.contentRecyclerViewAdapter.getLastSelectedItem().getUuid().toString());
                        break;
                    }

                    default:
                    {
                        List<IElement> selectedElementsWithoutOrphanElements = new ArrayList<>();

                        for(IElement selectedElement : this.viewModel.contentRecyclerViewAdapter.getSelectedItemsInOrderOfSelection())
                        {
                            if(!(selectedElement instanceof OrphanElement))
                            {
                                selectedElementsWithoutOrphanElements.add(selectedElement);
                            }
                        }

                        Log.d(Constants.LOG_TAG, String.format("PickElementsActivity.returnResult:: returning [%d] elements", selectedElementsWithoutOrphanElements.size()));
                        intent.putExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(selectedElementsWithoutOrphanElements));
                        break;
                    }
                }
            }
            else
            {
                Log.d(Constants.LOG_TAG, String.format("PickElementsActivity.returnResult:: returning created %s", element));
                intent.putExtra(Constants.EXTRA_ELEMENT_UUID, element.getUuid().toString());
            }
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + this.getClass().getSimpleName());
        finish();
    }
}
