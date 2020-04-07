package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.Blueprint;
import de.juliusawen.coastercreditcounter.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.enums.SortType;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.SortTool;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailDisplayMode;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailType;

public class SortElementsActivity extends BaseActivity
{
    private SortElementsActivityViewModel viewModel;
    private RecyclerView recyclerView;

    private View frameLayoutDialogDown;
    private View frameLayoutDialogUp;


    protected void setContentView()
    {
        setContentView(R.layout.activity_sort_elements);
    }

    protected void create()
    {
        this.viewModel = new ViewModelProvider(this).get(SortElementsActivityViewModel.class);

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
            this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(this.viewModel.elementsToSort, null, false)
                    .setDetailTypesAndModeForContentType(Blueprint.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                    .setDetailTypesAndModeForContentType(Blueprint.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                    .setDetailTypesAndModeForContentType(Blueprint.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW);

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

        super.createHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE)), getText(R.string.help_text_sort_elements));
        super.createToolbar()
                .addToolbarHomeButton()
                .setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE));
        super.createFloatingActionButton();

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
        this.frameLayoutDialogDown = findViewById(R.id.frameLayoutDialogUpDown_Down);
        this.frameLayoutDialogDown.setOnClickListener(this.getActionDialogOnClickListener());
        this.frameLayoutDialogDown.setOnTouchListener(this.getActionDialogOnTouchListener());

        ImageButton buttonUp = findViewById(R.id.buttonActionDialogUpDown_Up);
        buttonUp.setImageDrawable(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_arrow_upward, R.color.white));
        buttonUp.setId(ButtonFunction.MOVE_SELECTION_UP.ordinal());
        this.frameLayoutDialogUp = findViewById(R.id.frameLayoutDialogUpDown_Up);
        this.frameLayoutDialogUp.setOnClickListener(this.getActionDialogOnClickListener());
        this.frameLayoutDialogUp.setOnTouchListener(this.getActionDialogOnTouchListener());
    }

    private View.OnClickListener getActionDialogOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(view.equals(frameLayoutDialogDown))
                {
                    Log.v(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonClicked:: button<DOWN> clicked");
                }
                else if(view.equals(frameLayoutDialogUp))
                {
                    Log.v(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonClicked:: button<UP> clicked");
                }
            }
        };
    }

    private View.OnTouchListener getActionDialogOnTouchListener()
    {
        return (new View.OnTouchListener()
        {
            private final long DELAY_IN_MS = 200;
            private Handler handler;

            @Override public boolean onTouch(View view, MotionEvent event)
            {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        view.performClick();
                        if (handler != null)
                        {
                            return true;
                        }
                        handler = new Handler();
                        if(view.equals(frameLayoutDialogDown))
                        {
                            handler.post(actionSortDown);
                        }
                        else if(view.equals(frameLayoutDialogUp))
                        {
                            handler.post(actionSortUp);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (handler == null)
                        {
                            return true;
                        }
                        if(view.equals(frameLayoutDialogDown))
                        {
                            handler.removeCallbacks(actionSortDown);
                        }
                        else if(view.equals(frameLayoutDialogUp))
                        {
                            handler.removeCallbacks(actionSortUp);
                        }
                        handler = null;
                        break;
                }
                return false;
            }

            Runnable actionSortDown = new Runnable()
            {
                @Override public void run()
                {
                    sortDown();
                    handler.postDelayed(this, DELAY_IN_MS);
                }
            };

            Runnable actionSortUp = new Runnable()
            {
                @Override public void run()
                {
                    sortUp();
                    handler.postDelayed(this, DELAY_IN_MS);
                }
            };
        });
    }

    private void sortDown()
    {
        if(!viewModel.contentRecyclerViewAdapter.getSelectedItemsInOrderOfSelection().isEmpty())
        {
            int position = viewModel.elementsToSort.indexOf(viewModel.contentRecyclerViewAdapter.getLastSelectedItem());

            if(position < viewModel.elementsToSort.size() - 1)
            {
                Log.v(Constants.LOG_TAG, "SortElementsActivity.sortDown:: swapping elements");
                viewModel.contentRecyclerViewAdapter.swapItems(viewModel.elementsToSort.get(position), viewModel.elementsToSort.get(position + 1));
                Collections.swap(viewModel.elementsToSort, position, position + 1);
            }
            else
            {
                Log.v(Constants.LOG_TAG, "SortElementsActivity.sortDown:: end of list - not swapping elements");
            }
        }
        else
        {
            Log.v(Constants.LOG_TAG, "SortElementsActivity.sortDown:: no element selected");
        }
    }

    private void sortUp()
    {
        if(!viewModel.contentRecyclerViewAdapter.getSelectedItemsInOrderOfSelection().isEmpty())
        {
            int position = viewModel.elementsToSort.indexOf(viewModel.contentRecyclerViewAdapter.getLastSelectedItem());

            if(position > 0)
            {
                Log.v(Constants.LOG_TAG, "SortElementsActivity.sortUp:: swapping elements");

                viewModel.contentRecyclerViewAdapter.swapItems(viewModel.elementsToSort.get(position), viewModel.elementsToSort.get(position - 1));
                Collections.swap(viewModel.elementsToSort, position, position - 1);
            }
            else
            {
                Log.v(Constants.LOG_TAG, "SortElementsActivity.sortUp:: end of list - not swapping elements");
            }
        }
        else
        {
            Log.v(Constants.LOG_TAG, "SortElementsActivity.sortUp:: no element selected");
        }
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