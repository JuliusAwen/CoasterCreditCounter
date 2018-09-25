package de.juliusawen.coastercreditcounter.presentation.activities.elements;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import java.util.Collections;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.SelectableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.ViewTool;

public class SortElementsActivity extends BaseActivity
{
    private SortElementsActivityViewModel viewModel;
    private RecyclerView recyclerView;
    private ImageButton buttonDown;
    private ImageButton buttonUp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "SortElementsActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_sort_elements);
        super.onCreate(savedInstanceState);

        this.buttonDown = findViewById(R.id.buttonActionDialogUpDown_MoveSelectionDown);
        this.buttonUp = findViewById(R.id.buttonActionDialogUpDown_MoveSelectionUp);
        this.recyclerView = findViewById(R.id.recyclerViewSortElements);

        this.viewModel = ViewModelProviders.of(this).get(SortElementsActivityViewModel.class);
        
        if(this.viewModel.elementsToSort == null)
        {
            this.viewModel.elementsToSort = App.content.fetchElementsByUuidStrings(getIntent().getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS));
        }
        
        if(this.viewModel.toolbarTitle == null)
        {
            this.viewModel.toolbarTitle = getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE);
        }
        
        if(this.viewModel.contentRecyclerAdapter == null)
        {
            this.viewModel.contentRecyclerAdapter = this.createContentRecyclerView();
        }
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setAdapter(this.viewModel.contentRecyclerAdapter);

        super.addHelpOverlay(getString(R.string.title_help, this.viewModel.toolbarTitle), getText(R.string.help_text_sort_elements));

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
        this.viewModel.contentRecyclerAdapter.notifyDataSetChanged();
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
                this.viewModel.contentRecyclerAdapter.updateElements(this.viewModel.elementsToSort);
                return true;

            case SORT_DESCENDING:
                Element.sortElementsByNameDescending(this.viewModel.elementsToSort);
                this.viewModel.contentRecyclerAdapter.updateElements(this.viewModel.elementsToSort);
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
                onClickFloatingActionButton();
            }
        });
        super.setFloatingActionButtonVisibility(true);
    }

    private void onClickFloatingActionButton()
    {
        Log.d(Constants.LOG_TAG, "ShowLocationsActivity.onClickFloatingActionButton:: accepted - return code <OK>...");
        this.returnResult(RESULT_OK);
    }

    private SelectableRecyclerAdapter createContentRecyclerView()
    {
        RecyclerOnClickListener.OnClickListener recyclerOnClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position) {}

            @Override
            public boolean onLongClick(View view, int position)
            {
                onLongClickContentRecyclerView(view);
                return true;
            }
        };
        
        return new SelectableRecyclerAdapter(this.viewModel.elementsToSort, false, recyclerOnClickListener);
    }

    private void onLongClickContentRecyclerView(View view)
    {
        final Element longClickedElement = (Element) view.getTag();

        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenu().add(0, Selection.EDIT_ELEMENT.ordinal(), Menu.NONE, R.string.selection_edit_element);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                return false;
            }
        });
        popupMenu.show();
    }

    private void createActionDialog()
    {

        Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_arrow_downward));
        this.buttonDown.setImageDrawable(drawable);
        this.buttonDown.setId(ButtonFunction.MOVE_SELECTION_DOWN.ordinal());
        this.buttonDown.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.v(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonDown:: button<DOWN> clicked");
                if(!viewModel.contentRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
                {
                    int position = viewModel.elementsToSort.indexOf(viewModel.contentRecyclerAdapter.getSelectedElementsInOrderOfSelection().get(0));

                    if(position < viewModel.elementsToSort.size() - 1)
                    {
                        Log.i(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonDown:: swapping elements");
                        Collections.swap(viewModel.elementsToSort, position, position + 1);
                        viewModel.contentRecyclerAdapter.notifyDataSetChanged();

                        int scrollMargin = ViewTool.getScrollMarginForRecyclerView(recyclerView);
                        if(viewModel.elementsToSort.size() > position + 1 + scrollMargin)
                        {
                            recyclerView.smoothScrollToPosition(position + 1 + scrollMargin);
                        }
                        else
                        {
                            recyclerView.smoothScrollToPosition(viewModel.elementsToSort.size() - 1);
                        }
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
        });

        drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_arrow_upward));
        this.buttonUp.setImageDrawable(drawable);
        this.buttonUp.setId(ButtonFunction.MOVE_SELECTION_UP.ordinal());
        this.buttonUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.v(Constants.LOG_TAG, "SortElementsActivity.onClickActionDialogButtonUp:: button<UP> clicked");

                if(!viewModel.contentRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
                {
                    int position = viewModel.elementsToSort.indexOf(viewModel.contentRecyclerAdapter.getSelectedElementsInOrderOfSelection().get(0));

                    if(position > 0)
                    {
                        Log.d(Constants.LOG_TAG, "SortElementsActivity.createActionDialog.onClick:: swapping elements");

                        Collections.swap(viewModel.elementsToSort, position, position - 1);
                        viewModel.contentRecyclerAdapter.notifyDataSetChanged();

                        int scrollMargin = ViewTool.getScrollMarginForRecyclerView(recyclerView);
                        if(position - 1 - scrollMargin >= 0)
                        {
                            recyclerView.smoothScrollToPosition(position - 1 - scrollMargin);
                        }
                        else
                        {
                            recyclerView.smoothScrollToPosition(0);
                        }
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
        });
    }

    private void returnResult(int resultCode)
    {
        Log.d(Constants.LOG_TAG, String.format("SortElementsActivity.returnResult:: resultCode[%d]", resultCode));
        Intent intent = new Intent();
        if(resultCode == RESULT_OK)
        {
            Log.d(Constants.LOG_TAG, String.format("SortElementsActivity.returnResult:: returning #[%d] elements as result", this.viewModel.contentRecyclerAdapter.getElementsToSelectFrom().size()));

            intent.putExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(this.viewModel.contentRecyclerAdapter.getElementsToSelectFrom()));

            if(!this.viewModel.contentRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
            {
                Element lastSelectedElement = this.viewModel.contentRecyclerAdapter.getSelectedElementsInOrderOfSelection().get(this.viewModel.contentRecyclerAdapter.getSelectedElementsInOrderOfSelection().size() - 1);
                intent.putExtra(Constants.EXTRA_ELEMENT_UUID, lastSelectedElement.getUuid().toString());
            }
        }
        setResult(resultCode, intent);
        finish();
    }
}
