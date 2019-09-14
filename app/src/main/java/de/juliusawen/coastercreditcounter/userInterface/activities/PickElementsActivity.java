package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.Attraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Status;
import de.juliusawen.coastercreditcounter.dataModel.temporaryElements.GroupHeader;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.SortTool;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuAgent.OptionsMenuAgent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "PickElementsActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_pick_elements);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
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
                    {
                        HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
                        childTypesToExpand.add(Attraction.class);

                        this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(this.viewModel.elementsToPickFrom, childTypesToExpand, true).setTypefaceForType(GroupHeader.class, Typeface.BOLD).setDisplayModeForDetail(DetailType.MANUFACTURER, DetailDisplayMode.ABOVE).setDisplayModeForDetail(DetailType.STATUS, DetailDisplayMode.BELOW).groupItemsByType(GroupType.ATTRACTION_CATEGORY);
                        break;
                    }

                    case ASSIGN_CATEGORY_TO_ATTRACTIONS:
                    case ASSIGN_MANUFACTURERS_TO_ATTRACTIONS:
                    case ASSIGN_STATUS_TO_ATTRACTIONS:
                    {
                        HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
                        childTypesToExpand.add(Attraction.class);

                        this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                                this.viewModel.elementsToPickFrom,
                                childTypesToExpand,
                                true)
                                .setTypefaceForType(GroupHeader.class, Typeface.BOLD)
                                .setDisplayModeForDetail(DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .setDisplayModeForDetail(DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .groupItemsByType(GroupType.ATTRACTION_CATEGORY);
                        break;
                    }

                    case PICK_ATTRACTION_CATEGORY:
                    case PICK_MANUFACTURER:
                    case PICK_STATUS:
                    {
                        this.useSelectOrDeselectAllBar = false;

                        this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                                this.viewModel.elementsToPickFrom,
                                null,
                                false)
                                .setTypefaceForType(Status.class, Typeface.BOLD)
                                .setTypefaceForType(Manufacturer.class, Typeface.BOLD)
                                .setTypefaceForType(AttractionCategory.class, Typeface.BOLD);

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

        switch(RequestCode.values()[requestCode])
        {
            case CREATE_ATTRACTION_CATEGORY:
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

    //region OPTIONS MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(App.isInitialized)
        {
            switch(this.viewModel.requestCode)
            {
                case ASSIGN_CATEGORY_TO_ATTRACTIONS:
                case ASSIGN_MANUFACTURERS_TO_ATTRACTIONS:
                case ASSIGN_STATUS_TO_ATTRACTIONS:
                    this.viewModel.optionsMenuAgent
                            .add(OptionsMenuAgent.SORT_BY_NAME)
                            .add(OptionsMenuAgent.SORT_BY_LOCATION)
                            //                    .add(OptionsMenuAgent.SORT_BY_ATTRACTION_CATEGORY)
                            .add(OptionsMenuAgent.SORT_BY_MANUFACTURER)
                            .add(OptionsMenuAgent.GROUP_BY_LOCATION)
                            .add(OptionsMenuAgent.GROUP_BY_ATTRACTION_CATEGORY)
                            .add(OptionsMenuAgent.GROUP_BY_MANUFACTURER)
                            .add(OptionsMenuAgent.GROUP_BY_STATUS)
                            .add(OptionsMenuAgent.EXPAND_ALL)
                            .add(OptionsMenuAgent.COLLAPSE_ALL);
                    break;

                case PICK_ATTRACTIONS:
                    this.viewModel.optionsMenuAgent
                            .add(OptionsMenuAgent.EXPAND_ALL)
                            .add(OptionsMenuAgent.COLLAPSE_ALL);
                    break;

                case PICK_ATTRACTION_CATEGORY:
                case PICK_MANUFACTURER:
                case PICK_STATUS:
                    this.viewModel.optionsMenuAgent
                            .add(OptionsMenuAgent.SORT);
                    break;
            }

            this.viewModel.optionsMenuAgent.create(menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(App.isInitialized)
        {
            switch(this.viewModel.requestCode)
            {
                case ASSIGN_CATEGORY_TO_ATTRACTIONS:
                case ASSIGN_MANUFACTURERS_TO_ATTRACTIONS:
                case ASSIGN_STATUS_TO_ATTRACTIONS:
                    this.viewModel.optionsMenuAgent
                            .setEnabled(OptionsMenuAgent.SORT_BY_LOCATION, this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupType.LOCATION)
                            //                    .setEnabled(OptionsMenuAgent.SORT_BY_ATTRACTION_CATEGORY, this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupHeaderProvider.GroupType.ATTRACTION_CATEGORY)
                            .setEnabled(OptionsMenuAgent.SORT_BY_MANUFACTURER, this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupType.MANUFACTURER)
                            .setEnabled(OptionsMenuAgent.GROUP_BY_LOCATION, this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupType.LOCATION)
                            .setEnabled(OptionsMenuAgent.GROUP_BY_ATTRACTION_CATEGORY, this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupType.ATTRACTION_CATEGORY)
                            .setEnabled(OptionsMenuAgent.GROUP_BY_MANUFACTURER, this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupType.MANUFACTURER)
                            .setEnabled(OptionsMenuAgent.GROUP_BY_STATUS, this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupType.STATUS)
                            .setEnabled(OptionsMenuAgent.EXPAND_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllExpanded())
                            .setEnabled(OptionsMenuAgent.COLLAPSE_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllCollapsed());
                    break;

                case PICK_ATTRACTIONS:
                    this.viewModel.optionsMenuAgent
                            .setEnabled(OptionsMenuAgent.EXPAND_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllExpanded())
                            .setEnabled(OptionsMenuAgent.COLLAPSE_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllCollapsed());
                    break;
            }

            this.viewModel.optionsMenuAgent.prepare(menu);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(this.viewModel.optionsMenuAgent.handleOptionsItemSelected(item, this))
        {
            return true;
        }

        return  super.onOptionsItemSelected(item);
    }


    @Override
    public void handleExpandAllSelected()
    {
        this.viewModel.contentRecyclerViewAdapter.expandAll();
    }

    @Override
    public void handleCollapseAllSelected()
    {
        this.viewModel.contentRecyclerViewAdapter.collapseAll();
    }

    @Override
    public void handleGroupByLocationSelected()
    {
        this.groupElementsByType(GroupType.LOCATION);
    }

    @Override
    public void handleGroupByAttractionCategorySelected()
    {
        this.groupElementsByType(GroupType.ATTRACTION_CATEGORY);
    }

    @Override
    public void handleGroupByManufacturerSelected()
    {
        this.groupElementsByType(GroupType.MANUFACTURER);
    }

    @Override
    public void handleGroupByStatusSelected()
    {
        this.groupElementsByType(GroupType.STATUS);
    }


    @Override
    public void handleSortAscendingSelected()
    {
        this.viewModel.elementsToPickFrom = SortTool.sortElementsByNameAscending(viewModel.elementsToPickFrom);
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
    }

    @Override
    public void handleSortDescendingSelected()
    {
        this.viewModel.elementsToPickFrom = SortTool.sortElementsByNameDescending(viewModel.elementsToPickFrom);
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
    }

    @Override
    public void handleSortByNameAscendingSelected()
    {
        this.viewModel.elementsToPickFrom = SortTool.sortElementsByNameAscending(viewModel.elementsToPickFrom);
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
    }

    @Override
    public void handleSortByNameDescendingSelected()
    {
        this.viewModel.elementsToPickFrom = SortTool.sortElementsByNameDescending(viewModel.elementsToPickFrom);
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
    }

    @Override
    public void handleSortByLocationAscendingSelected()
    {
        this.viewModel.elementsToPickFrom = SortTool.sortAttractionsByLocationAscending(viewModel.elementsToPickFrom);
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
    }

    @Override
    public void handleSortByLocationDescendingSelected()
    {
        this.viewModel.elementsToPickFrom = SortTool.sortAttractionsByLocationDescending(viewModel.elementsToPickFrom);
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
    }

//    @Override
//    public void handleSortByAttractionCategoryAscendingSelected()
//    {
//        this.viewModel.elementsToPickFrom = SortTool.sortAttractionsByAttractionsCatgeoryAscending(viewModel.elementsToPickFrom);
//        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
//    }
//
//    @Override
//    public void handleSortByAttractionCategoryDescendingSelected()
//    {
//        this.viewModel.elementsToPickFrom = SortTool.sortAttractionsByAttractionsCatgeoryDescending(viewModel.elementsToPickFrom);
//        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
//    }

    @Override
    public void handleSortByManufacturerAscendingSelected()
    {
        this.viewModel.elementsToPickFrom = SortTool.sortAttractionsByManufacturerAscending(viewModel.elementsToPickFrom);
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
    }

    @Override
    public void handleSortByManufacturerDescendingSelected()
    {
        this.viewModel.elementsToPickFrom = SortTool.sortAttractionsByManufacturerDescending(viewModel.elementsToPickFrom);
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
    }

    // endregion OPTIONS MENU


    private void groupElementsByType(GroupType groupType)
    {
        this.viewModel.contentRecyclerViewAdapter.clearTypefaceForTypes()
                .setTypefaceForType(GroupHeader.class, Typeface.BOLD)
                .clearDisplayModeForDetails();

        switch(groupType)
        {
            case LOCATION:
                this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                        .setDisplayModeForDetail(DetailType.ATTRACTION_CATEGORY, DetailDisplayMode.BELOW)
                        .setDisplayModeForDetail(DetailType.STATUS, DetailDisplayMode.BELOW)
                        .groupItemsByType(GroupType.LOCATION);
                break;

            case ATTRACTION_CATEGORY:
                this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                        .setDisplayModeForDetail(DetailType.LOCATION, DetailDisplayMode.BELOW)
                        .setDisplayModeForDetail(DetailType.STATUS, DetailDisplayMode.BELOW)
                        .groupItemsByType(GroupType.ATTRACTION_CATEGORY);
                break;

            case MANUFACTURER:
                this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(DetailType.ATTRACTION_CATEGORY, DetailDisplayMode.BELOW)
                        .setDisplayModeForDetail(DetailType.LOCATION, DetailDisplayMode.BELOW)
                        .setDisplayModeForDetail(DetailType.STATUS, DetailDisplayMode.BELOW)
                        .groupItemsByType(GroupType.MANUFACTURER);
                break;

            case STATUS:
                this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                        .setDisplayModeForDetail(DetailType.LOCATION, DetailDisplayMode.BELOW)
                        .setDisplayModeForDetail(DetailType.ATTRACTION_CATEGORY, DetailDisplayMode.BELOW)
                        .groupItemsByType(GroupType.STATUS);
                break;
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
                Log.i(Constants.LOG_TAG, "ManageOrphanElementsActivity.onClickFloatingActionButton<Add>:: FloatingActionButton pressed");

                if(viewModel.requestCode == RequestCode.PICK_ATTRACTION_CATEGORY)
                {
                    ActivityDistributor.startActivityCreateForResult(PickElementsActivity.this, RequestCode.CREATE_ATTRACTION_CATEGORY, null);
                }
                else if(viewModel.requestCode == RequestCode.PICK_MANUFACTURER)
                {
                    ActivityDistributor.startActivityCreateForResult(PickElementsActivity.this, RequestCode.CREATE_MANUFACTURER, null);
                }
                else if(viewModel.requestCode == RequestCode.PICK_STATUS)
                {
                    ActivityDistributor.startActivityCreateForResult(PickElementsActivity.this, RequestCode.CREATE_STATUS, null);
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
                    case PICK_STATUS:
                    case PICK_VISIT:
                    case PICK_MANUFACTURER:
                    case PICK_ATTRACTION_CATEGORY:
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
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
        finish();
    }
}
