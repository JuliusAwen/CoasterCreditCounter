package de.juliusawen.coastercreditcounter.frontend.elements;

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
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Status;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
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
                if(this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_ATTRACTIONS
                        || this.viewModel.requestCode == Constants.REQUEST_CODE_ASSIGN_CATEGORY_TO_ATTRACTIONS
                        || this.viewModel.requestCode == Constants.REQUEST_CODE_ASSIGN_MANUFACTURERS_TO_ATTRACTIONS)
                {
                    HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
                    childTypesToExpand.add(Attraction.class);

                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            childTypesToExpand,
                            true,
                            Constants.TYPE_ATTRACTION_CATEGORY);

                    this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(AttractionCategoryHeader.class, Typeface.BOLD);

                    if(this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_ATTRACTIONS)
                    {
                        this.viewModel.contentRecyclerViewAdapter.displayStatus(true);
                    }
                    else if(this.viewModel.requestCode == Constants.REQUEST_CODE_ASSIGN_CATEGORY_TO_ATTRACTIONS
                            || this.viewModel.requestCode == Constants.REQUEST_CODE_ASSIGN_MANUFACTURERS_TO_ATTRACTIONS)
                    {
                        this.viewModel.contentRecyclerViewAdapter.displayManufacturers(true);
                        this.viewModel.contentRecyclerViewAdapter.displayLocations(true);
                    }
                }
                else if(this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_STATUS)
                {
                    this.useSelectOrDeselectAllBar = false;

                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            null,
                            false,
                            Constants.TYPE_NONE);

                    this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(Status.class, Typeface.BOLD);
                }
                else if(this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_VISIT)
                {
                    this.useSelectOrDeselectAllBar = false;

                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            null,
                            false,
                            Constants.TYPE_NONE);

                    this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(Status.class, Typeface.BOLD);
                    this.viewModel.contentRecyclerViewAdapter.setSpecialStringResourceForType(Visit.class, R.string.text_visit_display_full_name);
                }
                else
                {
                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            null,
                            true,
                            Constants.TYPE_NONE);

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
                this.decorateFloatingActionButton();
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
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        if(this.viewModel.requestCode == Constants.REQUEST_CODE_ASSIGN_CATEGORY_TO_ATTRACTIONS
            || this.viewModel.requestCode == Constants.REQUEST_CODE_ASSIGN_MANUFACTURERS_TO_ATTRACTIONS)
        {
            Menu submenuSortBy = menu.addSubMenu(Menu.NONE, Constants.SELECTION_SORT_BY, Menu.NONE, R.string.selection_sort_by);
            Menu submenuSortByManufacturer = submenuSortBy.addSubMenu(Menu.NONE, Constants.SELECTION_SORT_BY_MANUFACTURER, Menu.NONE, R.string.selection_sort_by_manufacturer);
            Menu submenuSortByLocation = submenuSortBy.addSubMenu(Menu.NONE, Constants.SELECTION_SORT_BY_LOCATION, Menu.NONE, R.string.selection_sort_by_location);

            submenuSortByManufacturer.add(Menu.NONE, Constants.SELECTION_SORT_BY_MANUFACTURER_ASCENDING, Menu.NONE, R.string.selection_sort_by_manufacturer_ascending);
            submenuSortByManufacturer.add(Menu.NONE, Constants.SELECTION_SORT_BY_MANUFACTURER_DESCENDING, Menu.NONE, R.string.selection_sort_by_manufacturer_descending);

            submenuSortByLocation.add(Menu.NONE, Constants.SELECTION_SORT_BY_LOCATION_ASCENDING, Menu.NONE, R.string.selection_sort_by_location_ascending);
            submenuSortByLocation.add(Menu.NONE, Constants.SELECTION_SORT_BY_LOCATION_DESCENDING, Menu.NONE, R.string.selection_sort_by_location_descending);
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

        if(id == Constants.SELECTION_SORT_BY_MANUFACTURER_ASCENDING)
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

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableTool.getColoredDrawable(R.drawable.ic_baseline_check, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!viewModel.contentRecyclerViewAdapter.getSelectedItemsInOrderOfSelection().isEmpty())
                {
                    Log.d(Constants.LOG_TAG, "PickElementsActivity.onClickFloatingActionButton:: accepted - return code <OK>");
                    returnResult(RESULT_OK);
                }
                else
                {
                    Log.d(Constants.LOG_TAG, "PickElementsActivity.onClickFloatingActionButton:: no element selected");
                    Toaster.makeToast(PickElementsActivity.this, getString(R.string.error_no_entry_selected));
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
                    returnResult(RESULT_OK);
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

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("PickElementsActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            if(this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_STATUS
                    || this.viewModel.requestCode == Constants.REQUEST_CODE_PICK_VISIT)
            {
                Log.d(Constants.LOG_TAG, String.format("PickElementsActivity.returnResult:: returning %s", this.viewModel.contentRecyclerViewAdapter.getLastSelectedItem()));
                intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.contentRecyclerViewAdapter.getLastSelectedItem().getUuid().toString());
            }
            else
            {
                List<IElement> selectedElementsWithoutOrphanElements = new ArrayList<>();

                for(IElement element : this.viewModel.contentRecyclerViewAdapter.getSelectedItemsInOrderOfSelection())
                {
                    if(!(element instanceof OrphanElement))
                    {
                        selectedElementsWithoutOrphanElements.add(element);
                    }
                }

                Log.d(Constants.LOG_TAG, String.format("PickElementsActivity.returnResult:: returning [%d] elements", selectedElementsWithoutOrphanElements.size()));
                intent.putExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(selectedElementsWithoutOrphanElements));
            }

        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
        finish();
    }
}
