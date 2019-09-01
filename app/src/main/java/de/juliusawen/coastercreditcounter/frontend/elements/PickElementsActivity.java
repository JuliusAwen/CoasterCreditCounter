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
import de.juliusawen.coastercreditcounter.backend.GroupHeader.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.backend.GroupHeader.GroupHeaderProvider;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Park;
import de.juliusawen.coastercreditcounter.backend.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.backend.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Status;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.ResultTool;
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

            if(this.viewModel.contentRecyclerViewAdapter == null)
            {
                if(this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_ATTRACTIONS)
                {
                    HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
                    childTypesToExpand.add(Attraction.class);

                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            childTypesToExpand,
                            true,
                            GroupHeaderProvider.GroupType.ATTRACTION_CATEGORY);

                    this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(AttractionCategoryHeader.class, Typeface.BOLD);
                    this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.STATUS, ContentRecyclerViewAdapter.DisplayMode.BELOW);
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
                            true,
                            GroupHeaderProvider.GroupType.ATTRACTION_CATEGORY);

                    this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(AttractionCategoryHeader.class, Typeface.BOLD);
                    this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.MANUFACTURER, ContentRecyclerViewAdapter.DisplayMode.ABOVE);
                    this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.LOCATION, ContentRecyclerViewAdapter.DisplayMode.BELOW);
                }
                else if(this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_STATUS
                    || this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_MANUFACTURER
                    || this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_ATTRACTION_CATEGORY)
                {
                    this.useSelectOrDeselectAllBar = false;

                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            null,
                            false,
                            GroupHeaderProvider.GroupType.ATTRACTION_CATEGORY);

                    this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(Status.class, Typeface.BOLD);
                    this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(Manufacturer.class, Typeface.BOLD);
                    this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(AttractionCategory.class, Typeface.BOLD);

                    super.addFloatingActionButton();
                    this.decorateFloatingActionButtonAdd();
                }
                else if(this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_VISIT)
                {
                    this.useSelectOrDeselectAllBar = false;

                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            null,
                            false,
                            GroupHeaderProvider.GroupType.NONE);

                    this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(Status.class, Typeface.BOLD);
                    this.viewModel.contentRecyclerViewAdapter.setSpecialStringResourceForType(Visit.class, R.string.text_visit_display_full_name);
                }
                else
                {
                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                        this.viewModel.elementsToPickFrom,
                        null,
                        true,
                        GroupHeaderProvider.GroupType.NONE);

                    this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(this.viewModel.elementsToPickFrom.get(0).getClass(), Typeface.BOLD);
                }
            }
            this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewOnClickListener());
            this.recyclerView = findViewById(R.id.recyclerViewPickElements);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            this.recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

            super.addToolbar();
            super.addToolbarHomeButton();
            super.setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE));

            if(!this.viewModel.isSimplePick)
            {
                super.addFloatingActionButton();
                this.decorateFloatingActionButtonCheck();
            }

            super.addHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE)), getText(R.string.help_text_pick_elements));

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
            IElement returnElement = ResultTool.fetchResultElement(data);

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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        if(this.viewModel.requestCode == Constants.REQUEST_CODE_ASSIGN_CATEGORY_TO_ATTRACTIONS
                || this.viewModel.requestCode == Constants.REQUEST_CODE_ASSIGN_MANUFACTURERS_TO_ATTRACTIONS
                || this.viewModel.requestCode == Constants.REQUEST_CODE_ASSIGN_STATUS_TO_ATTRACTIONS)
        {
            Menu submenuSortBy = menu.addSubMenu(Menu.NONE, Constants.SELECTION_SORT_BY, Menu.NONE, R.string.selection_sort_by);

            Menu submenuSortByName = submenuSortBy.addSubMenu(
                    Constants.SELECTION_SORT_BY_NAME, Constants.SELECTION_SORT_BY_NAME, Menu.NONE, R.string.selection_sort_by_name);

            Menu submenuSortByLocation = submenuSortBy.addSubMenu(
                    Constants.SELECTION_SORT_BY_LOCATION, Constants.SELECTION_SORT_BY_LOCATION, Menu.NONE, R.string.selection_sort_by_location);

            submenuSortBy.addSubMenu(
                    Constants.SELECTION_SORT_BY_ATTRACTION_CATEGORY, Constants.SELECTION_SORT_BY_ATTRACTION_CATEGORY, Menu.NONE, R.string.selection_sort_by_attraction_category);

            Menu submenuSortByManufacturer = submenuSortBy.addSubMenu(
                    Constants.SELECTION_SORT_BY_MANUFACTURER, Constants.SELECTION_SORT_BY_MANUFACTURER, Menu.NONE, R.string.selection_sort_by_manufacturer);


            submenuSortByName.add(Menu.NONE, Constants.SELECTION_SORT_BY_NAME_ASCENDING, Menu.NONE, R.string.selection_sort_ascending);
            submenuSortByName.add(Menu.NONE, Constants.SELECTION_SORT_BY_NAME_DESCENDING, Menu.NONE, R.string.selection_sort_descending);

            submenuSortByManufacturer.add(Menu.NONE, Constants.SELECTION_SORT_BY_MANUFACTURER_ASCENDING, Menu.NONE, R.string.selection_sort_ascending);
            submenuSortByManufacturer.add(Menu.NONE, Constants.SELECTION_SORT_BY_MANUFACTURER_DESCENDING, Menu.NONE, R.string.selection_sort_descending);

            submenuSortByLocation.add(Menu.NONE, Constants.SELECTION_SORT_BY_LOCATION_ASCENDING, Menu.NONE, R.string.selection_sort_ascending);
            submenuSortByLocation.add(Menu.NONE, Constants.SELECTION_SORT_BY_LOCATION_DESCENDING, Menu.NONE, R.string.selection_sort_descending);

            submenuSortBy.setGroupEnabled(Constants.SELECTION_SORT_BY_LOCATION, this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupHeaderProvider.GroupType.LOCATION);
            submenuSortBy.setGroupEnabled(Constants.SELECTION_SORT_BY_MANUFACTURER, this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupHeaderProvider.GroupType.MANUFACTURER);
            submenuSortBy.setGroupEnabled(Constants.SELECTION_SORT_BY_ATTRACTION_CATEGORY, this.viewModel.contentRecyclerViewAdapter.getGroupType() != GroupHeaderProvider.GroupType.ATTRACTION_CATEGORY);


            menu.add(Menu.NONE, Constants.SELECTION_EXPAND_ALL, Menu.NONE, R.string.selection_expand_all).setEnabled(!this.viewModel.contentRecyclerViewAdapter.isAllExpanded());
            menu.add(Menu.NONE, Constants.SELECTION_COLLAPSE_ALL, Menu.NONE, R.string.selection_collapse_all).setEnabled(!this.viewModel.contentRecyclerViewAdapter.isAllCollapsed());
        }
        else if(this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_ATTRACTIONS)
        {
            menu.add(Menu.NONE, Constants.SELECTION_EXPAND_ALL, Menu.NONE, R.string.selection_expand_all).setEnabled(!this.viewModel.contentRecyclerViewAdapter.isAllExpanded());
            menu.add(Menu.NONE, Constants.SELECTION_COLLAPSE_ALL, Menu.NONE, R.string.selection_collapse_all).setEnabled(!this.viewModel.contentRecyclerViewAdapter.isAllCollapsed());
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.i(Constants.LOG_TAG, String.format("PickElementsActivity.onOptionItemSelected:: [%S] selected", item.getItemId()));

        int id = item.getItemId();

        if(id == Constants.SELECTION_SORT_BY_NAME_ASCENDING)
        {
            this.viewModel.elementsToPickFrom = SortTool.sortElementsByNameAscending(viewModel.elementsToPickFrom);
            this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
        }
        else if(id == Constants.SELECTION_SORT_BY_NAME_DESCENDING)
        {
            this.viewModel.elementsToPickFrom = SortTool.sortElementsByNameDescending(viewModel.elementsToPickFrom);
            this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
        }
        else if(id == Constants.SELECTION_SORT_BY_MANUFACTURER_ASCENDING)
        {
            this.viewModel.elementsToPickFrom = SortTool.sortAttractionsByManufacturerAscending(viewModel.elementsToPickFrom);
            this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
        }
        else if(id == Constants.SELECTION_SORT_BY_MANUFACTURER_DESCENDING)
        {
            this.viewModel.elementsToPickFrom = SortTool.sortAttractionsByManufacturerDescending(viewModel.elementsToPickFrom);
            this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
        }
        else if(id == Constants.SELECTION_SORT_BY_LOCATION_ASCENDING)
        {
            this.viewModel.elementsToPickFrom = SortTool.sortAttractionsByLocationAscending(viewModel.elementsToPickFrom);
            this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
        }
        else if(id == Constants.SELECTION_SORT_BY_LOCATION_DESCENDING)
        {
            this.viewModel.elementsToPickFrom = SortTool.sortAttractionsByLocationDescending(viewModel.elementsToPickFrom);
            this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToPickFrom);
        }
        else if(id == Constants.SELECTION_EXPAND_ALL)
        {
            this.viewModel.contentRecyclerViewAdapter.expandAll();
        }
        else if(id == Constants.SELECTION_COLLAPSE_ALL)
        {
            this.viewModel.contentRecyclerViewAdapter.collapseAll();
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void decorateFloatingActionButtonCheck()
    {
        super.setFloatingActionButtonIcon(DrawableTool.getColoredDrawable(R.drawable.ic_baseline_check, R.color.white));
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
        super.setFloatingActionButtonIcon(DrawableTool.getColoredDrawable(R.drawable.ic_baseline_add, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(Constants.LOG_TAG, "ManageOrphanElementsActivity.onClickFloatingActionButton<Add>:: FloatingActionButton pressed");

                if(viewModel.requestCode == Constants.REQUEST_CODE_PICK_ATTRACTION_CATEGORY)
                {
                    ActivityTool.startActivityCreateForResult(PickElementsActivity.this, Constants.REQUEST_CODE_CREATE_ATTRACTION_CATEGORY, null);
                }
                else if(viewModel.requestCode == Constants.REQUEST_CODE_PICK_MANUFACTURER)
                {
                    ActivityTool.startActivityCreateForResult(PickElementsActivity.this, Constants.REQUEST_CODE_CREATE_MANUFACTURER, null);
                }
                else if(viewModel.requestCode == Constants.REQUEST_CODE_PICK_STATUS)
                {
                    ActivityTool.startActivityCreateForResult(PickElementsActivity.this, Constants.REQUEST_CODE_CREATE_STATUS, null);
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

    private void groupContentByType(int type)
    {
        List<IElement> groupedContent = new ArrayList<>();
        for(IElement element : this.viewModel.elementsToPickFrom)
        {
            for(IAttraction attraction : new HashSet<>(element.getChildrenAsType(IAttraction.class)))
            {
                if(type == Constants.TYPE_LOCATION)
                {
                    groupedContent.add(attraction.getParent());
                }
                else if(type == Constants.TYPE_MANUFACTURER)
                {
                    groupedContent.add(attraction.getManufacturer());
                }
                else if(type == Constants.TYPE_ATTRACTION_CATEGORY)
                {
                    groupedContent.add(attraction.getAttractionCategory());
                }
                else if(type == Constants.TYPE_STATUS)
                {
                    groupedContent.add(attraction.getStatus());
                }
            }
        }

        this.viewModel.contentRecyclerViewAdapter.clearTypefaceForTypes();
        this.viewModel.contentRecyclerViewAdapter.clearDisplayModeForDetails();

        if(type == Constants.TYPE_LOCATION)
        {
            this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(Park.class, Typeface.BOLD);
            this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.MANUFACTURER, ContentRecyclerViewAdapter.DisplayMode.ABOVE);
            this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.STATUS, ContentRecyclerViewAdapter.DisplayMode.BELOW);
        }
        else if(type == Constants.TYPE_MANUFACTURER)
        {
            this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(Manufacturer.class, Typeface.BOLD);
            this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.LOCATION, ContentRecyclerViewAdapter.DisplayMode.ABOVE);
            this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.ATTRACTION_CATEGORY, ContentRecyclerViewAdapter.DisplayMode.BELOW);
        }
        else if(type == Constants.TYPE_ATTRACTION_CATEGORY)
        {
            this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(AttractionCategory.class, Typeface.BOLD);
            this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.MANUFACTURER, ContentRecyclerViewAdapter.DisplayMode.ABOVE);
            this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.LOCATION, ContentRecyclerViewAdapter.DisplayMode.BELOW);
        }
        else if(type == Constants.TYPE_STATUS)
        {
            this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(Status.class, Typeface.BOLD);
            this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.LOCATION, ContentRecyclerViewAdapter.DisplayMode.ABOVE);
            this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.STATUS, ContentRecyclerViewAdapter.DisplayMode.BELOW);
        }

        this.viewModel.contentRecyclerViewAdapter.setItems(groupedContent);
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
