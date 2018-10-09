package de.juliusawen.coastercreditcounter.presentation.activities.elements;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.Collections;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;

public class SortElementsActivity extends BaseActivity
{
    private SortElementsActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "SortElementsActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_sort_elements);
        super.onCreate(savedInstanceState);

        this.viewModel = ViewModelProviders.of(this).get(SortElementsActivityViewModel.class);
        
        if(this.viewModel.elementsToSort == null)
        {
            this.viewModel.elementsToSort = App.content.fetchElementsByUuidStrings(getIntent().getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS));
        }
        
        if(this.viewModel.toolbarTitle == null)
        {
            this.viewModel.toolbarTitle = getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE);
        }
        
        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(this.viewModel.elementsToSort, null, false);
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerViewSortElements);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

        super.addHelpOverlayFragment(getString(R.string.title_help, this.viewModel.toolbarTitle), getText(R.string.help_text_sort_elements));

        super.addToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle(this.viewModel.toolbarTitle, null);

        super.addFloatingActionButton();
        this.decorateFloatingActionButton();

        this.createActionDialog();
    }

    @Override
    protected void onResume()
    {
        this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();
        menu.add(Menu.NONE, Selection.SORT_ASCENDING.ordinal(), Menu.NONE, R.string.selection_sort_ascending);
        menu.add(Menu.NONE, Selection.SORT_DESCENDING.ordinal(), Menu.NONE, R.string.selection_sort_descending);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("SortElementsActivity.onOptionItemSelected:: [%S] selected", selection));
        switch (selection)
        {
            case SORT_ASCENDING:
                Element.sortElementsByNameAscending(this.viewModel.elementsToSort);
                this.viewModel.contentRecyclerViewAdapter.updateContent(this.viewModel.elementsToSort);
                this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
                return true;

            case SORT_DESCENDING:
                Element.sortElementsByNameDescending(this.viewModel.elementsToSort);
                this.viewModel.contentRecyclerViewAdapter.updateContent(this.viewModel.elementsToSort);
                this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onToolbarHomeButtonBackClicked()
    {
        Log.d(Constants.LOG_TAG, "ShowLocationsActivity.onToolbarHomeButtonBackClicked:: result canceled");
        returnResult(RESULT_CANCELED);
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_check)));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(Constants.LOG_TAG, "ShowLocationsActivity.onClickFloatingActionButton:: accepted - return code <OK>...");
                returnResult(RESULT_OK);
            }
        });
        super.setFloatingActionButtonVisibility(true);
    }

    private void createActionDialog()
    {
        ImageButton buttonDown = findViewById(R.id.buttonActionDialogUpDown_Down);
        buttonDown.setImageDrawable(DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_arrow_downward)));
        buttonDown.setId(ButtonFunction.MOVE_SELECTION_DOWN.ordinal());
        findViewById(R.id.frameLayoutDialogUpDown_Down).setOnClickListener(this.getActionDialogOnClickListenerDown());

        ImageButton buttonUp = findViewById(R.id.buttonActionDialogUpDown_Up);
        buttonUp.setImageDrawable(DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_arrow_upward)));
        buttonUp.setId(ButtonFunction.MOVE_SELECTION_UP.ordinal());
        findViewById(R.id.frameLayoutDialogUpDown_Up).setOnClickListener(this.getActionDialogOnClickListenerUp());
    }

    private View.OnClickListener getActionDialogOnClickListenerDown()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.v(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonDown:: button<DOWN> clicked");
                if(!viewModel.contentRecyclerViewAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
                {
                    int position = viewModel.elementsToSort.indexOf(viewModel.contentRecyclerViewAdapter.getLastSelectedElement());

                    if(position < viewModel.elementsToSort.size() - 1)
                    {
                        Log.i(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonDown:: swapping elements");
                        viewModel.contentRecyclerViewAdapter.swapElements(viewModel.elementsToSort.get(position), viewModel.elementsToSort.get(position +1));
                        Collections.swap(viewModel.elementsToSort, position, position +1);
                    }
                    else
                    {
                        Log.d(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonDown:: end of list - not swapping elements");
                    }
                }
                else
                {
                    Log.i(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonDown:: no element selected");
                }
            }
        };
    }

    private View.OnClickListener getActionDialogOnClickListenerUp()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.v(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonUp:: button<UP> clicked");

                if(!viewModel.contentRecyclerViewAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
                {
                    int position = viewModel.elementsToSort.indexOf(viewModel.contentRecyclerViewAdapter.getLastSelectedElement());

                    if(position > 0)
                    {
                        Log.d(Constants.LOG_TAG, "SortElementsActivity.createActionDialog.onClick:: swapping elements");

                        viewModel.contentRecyclerViewAdapter.swapElements(viewModel.elementsToSort.get(position), viewModel.elementsToSort.get(position -1));
                        Collections.swap(viewModel.elementsToSort, position, position -1);
                    }
                    else
                    {
                        Log.d(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonUp:: end of list - not swapping elements");
                    }
                }
                else
                {
                    Log.i(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonUp:: no element selected");
                }
            }
        };
    }

    private void returnResult(int resultCode)
    {
        Log.d(Constants.LOG_TAG, String.format("SortElementsActivity.returnResult:: resultCode[%d]", resultCode));
        Intent intent = new Intent();
        if(resultCode == RESULT_OK)
        {
            Log.d(Constants.LOG_TAG, String.format("SortElementsActivity.returnResult:: returning [%d] elements as result", this.viewModel.elementsToSort.size()));

            intent.putExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(this.viewModel.elementsToSort));

            if(!this.viewModel.contentRecyclerViewAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
            {
                Element lastSelectedElement = this.viewModel.contentRecyclerViewAdapter.getLastSelectedElement();
                intent.putExtra(Constants.EXTRA_ELEMENT_UUID, lastSelectedElement.getUuid().toString());
            }
        }
        setResult(resultCode, intent);
        finish();
    }
}
