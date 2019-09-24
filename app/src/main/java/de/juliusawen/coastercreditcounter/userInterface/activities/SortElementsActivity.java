package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.enums.SortType;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.SortTool;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;

public class SortElementsActivity extends BaseActivity
{
    private SortElementsActivityViewModel viewModel;
    private RecyclerView recyclerView;


    protected void setContentView()
    {
        setContentView(R.layout.activity_sort_elements);
    }

    protected void create()
    {
        this.viewModel = ViewModelProviders.of(this).get(SortElementsActivityViewModel.class);

        if(this.viewModel.optionsMenuAgent == null)
        {
            this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
        }

        if(this.viewModel.elementsToSort == null)
        {
            this.viewModel.elementsToSort = App.content.getContentByUuidStrings(getIntent().getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS));
        }

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(this.viewModel.elementsToSort, null, false);

            Set<Class<? extends IElement>> types = new HashSet<>();
            for(IElement elementToSort : this.viewModel.elementsToSort)
            {
                types.add(elementToSort.getClass());
            }

            for(Class<? extends IElement> type : types)
            {
                this.viewModel.contentRecyclerViewAdapter.setTypefaceForContentType(type, Typeface.BOLD);
            }
        }
        this.recyclerView = findViewById(R.id.recyclerViewSortElements);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

        super.addHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE)), getText(R.string.help_text_sort_elements));
        super.addToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE));
        super.addFloatingActionButton();

        this.decorateFloatingActionButton();

        this.createActionDialog();
    }

    @Override
    protected void onDestroy()
    {
        this.recyclerView.setAdapter(null);
        super.onDestroy();
    }

    @Override
    protected Menu createOptionsMenu(Menu menu)
    {
        return this.viewModel.optionsMenuAgent
                .add(OptionsItem.SORT)
                .addToGroup(OptionsItem.SORT_ASCENDING, OptionsItem.SORT)
                .addToGroup(OptionsItem.SORT_DESCENDING, OptionsItem.SORT)
                .create(menu);
    }

    @Override
    public boolean handleOptionsItemSelected(OptionsItem item)
    {
        switch(item)
        {
            case SORT_ASCENDING:
                this.viewModel.elementsToSort = SortTool.sortElements(this.viewModel.elementsToSort, SortType.BY_NAME, SortOrder.ASCENDING);
                this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToSort);
                return true;

            case SORT_DESCENDING:
                this.viewModel.elementsToSort = SortTool.sortElements(this.viewModel.elementsToSort, SortType.BY_NAME, SortOrder.DESCENDING);
                this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.elementsToSort);
                return true;

            default:
                return super.handleOptionsItemSelected(item);
        }
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_check, R.color.white));
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
        buttonDown.setImageDrawable(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_arrow_downward, R.color.white));
        buttonDown.setId(ButtonFunction.MOVE_SELECTION_DOWN.ordinal());
        findViewById(R.id.frameLayoutDialogUpDown_Down).setOnClickListener(this.getActionDialogOnClickListenerDown());

        ImageButton buttonUp = findViewById(R.id.buttonActionDialogUpDown_Up);
        buttonUp.setImageDrawable(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_arrow_upward, R.color.white));
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
                if(!viewModel.contentRecyclerViewAdapter.getSelectedItemsInOrderOfSelection().isEmpty())
                {
                    int position = viewModel.elementsToSort.indexOf(viewModel.contentRecyclerViewAdapter.getLastSelectedItem());

                    if(position < viewModel.elementsToSort.size() - 1)
                    {
                        Log.v(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonDown:: swapping elements");
                        viewModel.contentRecyclerViewAdapter.swapItems(viewModel.elementsToSort.get(position), viewModel.elementsToSort.get(position + 1));
                        Collections.swap(viewModel.elementsToSort, position, position + 1);
                    }
                    else
                    {
                        Log.v(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonDown:: end of list - not swapping elements");
                    }
                }
                else
                {
                    Log.v(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonDown:: no element selected");
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

                if(!viewModel.contentRecyclerViewAdapter.getSelectedItemsInOrderOfSelection().isEmpty())
                {
                    int position = viewModel.elementsToSort.indexOf(viewModel.contentRecyclerViewAdapter.getLastSelectedItem());

                    if(position > 0)
                    {
                        Log.v(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonUp.onClick:: swapping elements");

                        viewModel.contentRecyclerViewAdapter.swapItems(viewModel.elementsToSort.get(position), viewModel.elementsToSort.get(position - 1));
                        Collections.swap(viewModel.elementsToSort, position, position - 1);
                    }
                    else
                    {
                        Log.v(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonUp:: end of list - not swapping elements");
                    }
                }
                else
                {
                    Log.v(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonUp:: no element selected");
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

            if(!this.viewModel.contentRecyclerViewAdapter.getSelectedItemsInOrderOfSelection().isEmpty())
            {
                IElement lastSelectedElement = this.viewModel.contentRecyclerViewAdapter.getLastSelectedItem();
                intent.putExtra(Constants.EXTRA_ELEMENT_UUID, lastSelectedElement.getUuid().toString());
            }
        }
        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + this.getClass().getSimpleName());
        finish();
    }
}
