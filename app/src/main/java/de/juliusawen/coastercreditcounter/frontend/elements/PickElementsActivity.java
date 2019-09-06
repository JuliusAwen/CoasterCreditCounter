package de.juliusawen.coastercreditcounter.frontend.elements;

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
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.backend.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Status;
import de.juliusawen.coastercreditcounter.backend.temporaryElements.GroupHeader.GroupHeader;
import de.juliusawen.coastercreditcounter.backend.temporaryElements.GroupHeader.GroupHeaderProvider;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.ActivityDistributor;
import de.juliusawen.coastercreditcounter.toolbox.DrawableProvider;
import de.juliusawen.coastercreditcounter.toolbox.MenuAgent;
import de.juliusawen.coastercreditcounter.toolbox.ResultFetcher;
import de.juliusawen.coastercreditcounter.toolbox.SortTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

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

            if(this.viewModel.requestCode == -1)
            {
                this.viewModel.requestCode = getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, -1);
            }

            this.viewModel.isSimplePick = getIntent().getBooleanExtra(Constants.EXTRA_SIMPLE_PICK, false);

            if(this.viewModel.elementsToPickFrom == null)
            {
                this.viewModel.elementsToPickFrom = App.content.getContentByUuidStrings(getIntent().getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS));
            }

            if(this.viewModel.optionsMenuAgent == null)
            {
                this.viewModel.optionsMenuAgent = new MenuAgent(MenuAgent.MenuType.OPTIONS_MENU);
            }

            if(this.viewModel.contentRecyclerViewAdapter == null)
            {
                if(this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_ATTRACTIONS)
                {
                    HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
                    childTypesToExpand.add(Attraction.class);

                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            childTypesToExpand,
                            true)
                            .setTypefaceForType(GroupHeader.class, Typeface.BOLD)
                            .setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.STATUS, ContentRecyclerViewAdapter.DisplayMode.BELOW)
                            .groupItemsByType(GroupHeaderProvider.GroupType.ATTRACTION_CATEGORY);
                }
                else if(this.viewModel.requestCode == Constants.REQUEST_CODE_ASSIGN_MANUFACTURERS_TO_ATTRACTIONS
                    || this.viewModel.requestCode == Constants.REQUEST_CODE_ASSIGN_CATEGORY_TO_ATTRACTIONS
                    || this.viewModel.requestCode == Constants.REQUEST_CODE_ASSIGN_STATUS_TO_ATTRACTIONS)
                {
                    HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
                    childTypesToExpand.add(Attraction.class);

                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            childTypesToExpand,
                            true)
                            .setTypefaceForType(GroupHeader.class, Typeface.BOLD)
                            .setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.MANUFACTURER, ContentRecyclerViewAdapter.DisplayMode.ABOVE)
                            .setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.LOCATION, ContentRecyclerViewAdapter.DisplayMode.BELOW)
                            .groupItemsByType(GroupHeaderProvider.GroupType.ATTRACTION_CATEGORY);
                }
                else if(this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_STATUS
                    || this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_MANUFACTURER
                    || this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_ATTRACTION_CATEGORY)
                {
                    this.useSelectOrDeselectAllBar = false;

                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            null,
                            false)
                            .setTypefaceForType(Status.class, Typeface.BOLD)
                            .setTypefaceForType(Manufacturer.class, Typeface.BOLD)
                            .setTypefaceForType(AttractionCategory.class, Typeface.BOLD)
                            .groupItemsByType(GroupHeaderProvider.GroupType.ATTRACTION_CATEGORY);

                    super.addFloatingActionButton();
                    this.decorateFloatingActionButtonAdd();
                }
                else if(this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_VISIT)
                {
                    this.useSelectOrDeselectAllBar = false;

                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            null,
                            false)
                            .setTypefaceForType(Status.class, Typeface.BOLD)
                            .setSpecialStringResourceForType(Visit.class, R.string.text_visit_display_full_name);
                }
                else
                {
                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            null,
                            true)
                            .setTypefaceForType(this.viewModel.elementsToPickFrom.get(0).getClass(), Typeface.BOLD);
                }
            }
            this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewOnClickListener());
            this.recyclerView = findViewById(R.id.recyclerViewPickElements);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            this.recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

            super.addHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE)), getText(R.string.help_text_pick_elements))
                    .addToolbar()
                    .addToolbarHomeButton()
                    .setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE));

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
        Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode != Activity.RESULT_OK)
        {
            return;
        }

        if(requestCode == Constants.REQUEST_CODE_CREATE_ATTRACTION_CATEGORY
            || requestCode == Constants.REQUEST_CODE_CREATE_MANUFACTURER
            || requestCode == Constants.REQUEST_CODE_CREATE_STATUS)
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
        }
    }

    //region OPTIONS MENU

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(this.viewModel.requestCode == Constants.REQUEST_CODE_ASSIGN_CATEGORY_TO_ATTRACTIONS
                || this.viewModel.requestCode == Constants.REQUEST_CODE_ASSIGN_MANUFACTURERS_TO_ATTRACTIONS
                || this.viewModel.requestCode == Constants.REQUEST_CODE_ASSIGN_STATUS_TO_ATTRACTIONS)
        {
            this.viewModel.optionsMenuAgent
                    .addMenuItem(MenuAgent.SORT_BY_NAME)
                    .addMenuItem(MenuAgent.SORT_BY_LOCATION, this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupHeaderProvider.GroupType.LOCATION)
//                    .addMenuItem(MenuAgent.SORT_BY_ATTRACTION_CATEGORY, this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupHeaderProvider.GroupType.ATTRACTION_CATEGORY)
                    .addMenuItem(MenuAgent.SORT_BY_MANUFACTURER, this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupHeaderProvider.GroupType.MANUFACTURER)
                    .addMenuItem(MenuAgent.GROUP_BY_LOCATION, this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupHeaderProvider.GroupType.LOCATION)
                    .addMenuItem(MenuAgent.GROUP_BY_ATTRACTION_CATEGORY, this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupHeaderProvider.GroupType.ATTRACTION_CATEGORY)
                    .addMenuItem(MenuAgent.GROUP_BY_MANUFACTURER, this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupHeaderProvider.GroupType.MANUFACTURER)
                    .addMenuItem(MenuAgent.GROUP_BY_STATUS, this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupHeaderProvider.GroupType.STATUS)
                    .addMenuItem(MenuAgent.EXPAND_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllExpanded())
                    .addMenuItem(MenuAgent.COLLAPSE_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllCollapsed())
                    .addMenuItem(MenuAgent.HELP)
                    .create(menu);
        }
        else if(this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_ATTRACTIONS)
        {
            this.viewModel.optionsMenuAgent
                    .addMenuItem(MenuAgent.EXPAND_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllExpanded())
                    .addMenuItem(MenuAgent.COLLAPSE_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllCollapsed())
                    .create(menu);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return this.viewModel.optionsMenuAgent.handleMenuItemSelected(item, this);
    }


    @Override
    public boolean handleOptionsMenuItemExpandAllSelected()
    {
        this.viewModel.contentRecyclerViewAdapter.expandAll();
        return true;
    }

    @Override
    public boolean handleOptionsMenuItemCollapseAllSelected()
    {
        this.viewModel.contentRecyclerViewAdapter.collapseAll();
        return true;
    }

    @Override
    public boolean handleOptionsMenuItemGroupByLocationSelected()
    {
        this.groupElementsByType(GroupHeaderProvider.GroupType.LOCATION);
        return true;
    }

    @Override
    public boolean handleOptionsMenuItemGroupByAttractionCategorySelected()
    {
        this.groupElementsByType(GroupHeaderProvider.GroupType.ATTRACTION_CATEGORY);
        return true;
    }

    @Override
    public boolean handleOptionsMenuItemGroupByManufacturerSelected()
    {
        this.groupElementsByType(GroupHeaderProvider.GroupType.MANUFACTURER);
        return true;
    }

    @Override
    public boolean handleOptionsMenuItemGroupByStatusSelected()
    {
        this.groupElementsByType(GroupHeaderProvider.GroupType.STATUS);
        return true;
    }

    @Override
    public boolean handleOptionsMenuItemSortByNameAscendingSelected()
    {
        this.viewModel.elementsToPickFrom = SortTool.sortElementsByNameAscending(viewModel.elementsToPickFrom);
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
        return true;
    }

    @Override
    public boolean handleOptionsMenuItemSortByNameDescendingSelected()
    {
        this.viewModel.elementsToPickFrom = SortTool.sortElementsByNameDescending(viewModel.elementsToPickFrom);
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
        return true;
    }

    @Override
    public boolean handleOptionsMenuItemSortByLocationAscendingSelected()
    {
        this.viewModel.elementsToPickFrom = SortTool.sortAttractionsByLocationAscending(viewModel.elementsToPickFrom);
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
        return true;
    }

    @Override
    public boolean handleOptionsMenuItemSortByLocationDescendingSelected()
    {
        this.viewModel.elementsToPickFrom = SortTool.sortAttractionsByLocationDescending(viewModel.elementsToPickFrom);
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
        return true;
    }

//    @Override
//    public boolean handleOptionsMenuItemSortByAttractionCategoryAscendingSelected()
//    {
//        this.viewModel.elementsToPickFrom = SortTool.sortAttractionsByAttractionsCatgeoryAscending(viewModel.elementsToPickFrom);
//        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
//        return true;
//    }
//
//    @Override
//    public boolean handleOptionsMenuItemSortByAttractionCategoryDescendingSelected()
//    {
//        this.viewModel.elementsToPickFrom = SortTool.sortAttractionsByAttractionsCatgeoryDescending(viewModel.elementsToPickFrom);
//        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
//        return true;
//    }

    @Override
    public boolean handleOptionsMenuItemSortByManufacturerAscendingSelected()
    {
        this.viewModel.elementsToPickFrom = SortTool.sortAttractionsByManufacturerAscending(viewModel.elementsToPickFrom);
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
        return true;
    }

    @Override
    public boolean handleOptionsMenuItemSortByManufacturerDescendingSelected()
    {
        this.viewModel.elementsToPickFrom = SortTool.sortAttractionsByManufacturerDescending(viewModel.elementsToPickFrom);
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
        return true;
    }

    // endregion OPTIONS MENU


    private void groupElementsByType(GroupHeaderProvider.GroupType groupType)
    {
        this.viewModel.contentRecyclerViewAdapter.clearTypefaceForTypes()
                .setTypefaceForType(GroupHeader.class, Typeface.BOLD)
                .clearDisplayModeForDetails();

        switch(groupType)
        {
            case LOCATION:
                this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.MANUFACTURER, ContentRecyclerViewAdapter.DisplayMode.ABOVE)
                        .setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.ATTRACTION_CATEGORY, ContentRecyclerViewAdapter.DisplayMode.BELOW)
                        .setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.STATUS, ContentRecyclerViewAdapter.DisplayMode.BELOW)
                        .groupItemsByType(GroupHeaderProvider.GroupType.LOCATION);
                break;

            case ATTRACTION_CATEGORY:
                this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.MANUFACTURER, ContentRecyclerViewAdapter.DisplayMode.ABOVE)
                        .setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.LOCATION, ContentRecyclerViewAdapter.DisplayMode.BELOW)
                        .setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.STATUS, ContentRecyclerViewAdapter.DisplayMode.BELOW)
                        .groupItemsByType(GroupHeaderProvider.GroupType.ATTRACTION_CATEGORY);
                break;

            case MANUFACTURER:
                this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.ATTRACTION_CATEGORY, ContentRecyclerViewAdapter.DisplayMode.BELOW)
                        .setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.LOCATION, ContentRecyclerViewAdapter.DisplayMode.BELOW)
                        .setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.STATUS, ContentRecyclerViewAdapter.DisplayMode.BELOW)
                        .groupItemsByType(GroupHeaderProvider.GroupType.MANUFACTURER);
                break;

            case STATUS:
                this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.MANUFACTURER, ContentRecyclerViewAdapter.DisplayMode.ABOVE)
                        .setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.LOCATION, ContentRecyclerViewAdapter.DisplayMode.BELOW)
                        .setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.ATTRACTION_CATEGORY, ContentRecyclerViewAdapter.DisplayMode.BELOW)
                        .groupItemsByType(GroupHeaderProvider.GroupType.STATUS);
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

                if(viewModel.requestCode == Constants.REQUEST_CODE_PICK_ATTRACTION_CATEGORY)
                {
                    ActivityDistributor.startActivityCreateForResult(PickElementsActivity.this, Constants.REQUEST_CODE_CREATE_ATTRACTION_CATEGORY, null);
                }
                else if(viewModel.requestCode == Constants.REQUEST_CODE_PICK_MANUFACTURER)
                {
                    ActivityDistributor.startActivityCreateForResult(PickElementsActivity.this, Constants.REQUEST_CODE_CREATE_MANUFACTURER, null);
                }
                else if(viewModel.requestCode == Constants.REQUEST_CODE_PICK_STATUS)
                {
                    ActivityDistributor.startActivityCreateForResult(PickElementsActivity.this, Constants.REQUEST_CODE_CREATE_STATUS, null);
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
                if(this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_STATUS
                    || this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_VISIT
                    || this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_MANUFACTURER
                    || this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_ATTRACTION_CATEGORY)
                {
                    Log.d(Constants.LOG_TAG, String.format("PickElementsActivity.returnResult:: returning %s", this.viewModel.contentRecyclerViewAdapter.getLastSelectedItem()));
                    intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.contentRecyclerViewAdapter.getLastSelectedItem().getUuid().toString());
                }
                else
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
