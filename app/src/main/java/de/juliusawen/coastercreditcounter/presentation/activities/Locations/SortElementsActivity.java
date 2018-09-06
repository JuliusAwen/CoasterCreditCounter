package de.juliusawen.coastercreditcounter.presentation.activities.Locations;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.SelectableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.ViewTool;
import de.juliusawen.coastercreditcounter.toolbox.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.toolbox.enums.Selection;

public class SortElementsActivity extends BaseActivity
{
    private List<Element> elementsToSort;
    private SelectableRecyclerAdapter selectableRecyclerAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "SortElementsActivity.onCreate:: creating activity...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_elements);

        this.initializeContent();
        this.initializeViews();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();
        menu.add(0, Selection.SORT_A_TO_Z.ordinal(), Menu.NONE, R.string.selection_sort_a_to_z);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("SortElementsActivity.onOptionItemSelected:: [%S] selected", selection));

        switch (selection)
        {
            case SORT_A_TO_Z:
                Collections.sort(this.elementsToSort, new Comparator<Element>()
                {
                    @Override
                    public int compare(Element element1, Element element2)
                    {
                        return element1.getName().compareToIgnoreCase(element2.getName());
                    }
                });

                this.selectableRecyclerAdapter.updateList(this.elementsToSort);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.KEY_ELEMENTS, Content.getUuidStringsFromElements(this.elementsToSort));
        if(this.selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
        {
            outState.putString(Constants.KEY_SELECTED_ELEMENT, "");
        }
        else
        {
            outState.putString(Constants.KEY_SELECTED_ELEMENT, selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().get(0).getUuid().toString());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.elementsToSort = Content.getInstance().fetchElementsFromUuidStrings(savedInstanceState.getStringArrayList(Constants.KEY_ELEMENTS));

        String selectedElementString = savedInstanceState.getString(Constants.KEY_SELECTED_ELEMENT);
        if(selectedElementString != null && selectedElementString.isEmpty())
        {
            this.selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().clear();
        }
        else
        {
            Element element = Content.getInstance().getElementByUuid(UUID.fromString(selectedElementString));
            this.selectableRecyclerAdapter.selectElement(element);
            this.recyclerView.smoothScrollToPosition(elementsToSort.indexOf(element));
        }

        this.selectableRecyclerAdapter.updateList(this.elementsToSort);
    }

    @Override
    protected void onHomeButtonBackClicked()
    {
        Log.d(Constants.LOG_TAG, "ShowLocationsActivity.onHomeButtonBackClicked:: result canceled");
        returnResult(RESULT_CANCELED);
    }

    private void initializeContent()
    {
        this.elementsToSort = Content.getInstance().fetchElementsFromUuidStrings(getIntent().getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS));
        Log.v(Constants.LOG_TAG, String.format("SortElementsActivity.initializeContent:: fetched #[%d] elements from extra", this.elementsToSort.size()));
    }

    private void initializeViews()
    {
        FrameLayout frameLayoutActivity = findViewById(R.id.frameLayoutSortElements);
        View sortElementsView = getLayoutInflater().inflate(R.layout.layout_sort_elements, frameLayoutActivity, false);
        frameLayoutActivity.addView(sortElementsView);

        super.createToolbar(getString(R.string.title_sort_elements), elementsToSort.get(0).getParent().getName(), true);
        this.createFloatingActionButton();
        this.createActionDialog(sortElementsView);
        this.createContentRecyclerView(sortElementsView);
        this.createHelpOverlayFragment(getText(R.string.help_text_sort_elements), false);
    }

    private void createFloatingActionButton()
    {
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(Constants.LOG_TAG, "ShowLocationsActivity.createFloatingActionButton.onMenuItemClick:: accepted - result ok");
                returnResult(RESULT_OK);
            }
        });

        super.createFloatingActionButton(floatingActionButton, DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_check)));
    }

    private void createActionDialog(View view)
    {
        ImageButton buttonDown = view.findViewById(R.id.buttonActionDialogUpDown_MoveSelectionDown);
        Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_arrow_downward));
        buttonDown.setImageDrawable(drawable);
        buttonDown.setId(ButtonFunction.MOVE_SELECTION_DOWN.ordinal());
        buttonDown.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(Constants.LOG_TAG, "SortElementsActivity.createActionDialog.onClick:: button<DOWN> clicked");

                if(!selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
                {
                    int position = elementsToSort.indexOf(selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().get(0));

                    if(position < elementsToSort.size() - 1)
                    {
                        Log.i(Constants.LOG_TAG, "SortElementsActivity.createActionDialog.onClick:: swapping elements");

                        Collections.swap(elementsToSort, position, position + 1);
                        selectableRecyclerAdapter.notifyDataSetChanged();

                        int scrollMargin = ViewTool.getScrollMarginForRecyclerView(recyclerView);
                        if(elementsToSort.size() > position + 1 + scrollMargin)
                        {
                            recyclerView.smoothScrollToPosition(position + 1 + scrollMargin);
                        }
                        else
                        {
                            recyclerView.smoothScrollToPosition(elementsToSort.size() - 1);
                        }
                    }
                    else
                    {
                        Log.i(Constants.LOG_TAG, "SortElementsActivity.createActionDialog.onClick:: end of list - not swapping elements");
                    }
                }
                else
                {
                    Log.i(Constants.LOG_TAG, "SortElementsActivity.createActionDialog.onClick:: no element selected");
                }
            }
        });

        ImageButton buttonUp = view.findViewById(R.id.buttonActionDialogUpDown_MoveSelectionUp);
        drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_arrow_upward));
        buttonUp.setImageDrawable(drawable);
        buttonUp.setId(ButtonFunction.MOVE_SELECTION_UP.ordinal());
        buttonUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(Constants.LOG_TAG, "SortElementsActivity.createActionDialog.onClick:: button<UP> clicked");

                if(!selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
                {
                    int position = elementsToSort.indexOf(selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().get(0));

                    if(position > 0)
                    {
                        Log.i(Constants.LOG_TAG, "SortElementsActivity.createActionDialog.onClick:: swapping elements");

                        Collections.swap(elementsToSort, position, position - 1);
                        selectableRecyclerAdapter.notifyDataSetChanged();

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
                        Log.i(Constants.LOG_TAG, "SortElementsActivity.createActionDialog.onClick:: end of list - not swapping elements");
                    }
                }
                else
                {
                    Log.i(Constants.LOG_TAG, "SortElementsActivity.createActionDialog.onClick:: no element selected");
                }
            }
        });
    }

    private void createContentRecyclerView(View view)
    {
        this.selectableRecyclerAdapter = new SelectableRecyclerAdapter(this.elementsToSort, false);

        this.recyclerView = view.findViewById(R.id.recyclerViewSortElements);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setAdapter(this.selectableRecyclerAdapter);
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("SortElementsActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            Log.d(Constants.LOG_TAG, String.format("SortElementsActivity.returnResult:: returning #[%d] elements as result", this.selectableRecyclerAdapter.getElementsToSelectFrom().size()));

            intent.putExtra(Constants.EXTRA_ELEMENTS_UUIDS, Content.getUuidStringsFromElements(this.selectableRecyclerAdapter.getElementsToSelectFrom()));

            if(!this.selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
            {
                Element lastSelectedElement = this.selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().get(this.selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().size() - 1);
                intent.putExtra(Constants.EXTRA_ELEMENT_UUID, lastSelectedElement.getUuid().toString());
            }
        }

        setResult(resultCode, intent);

        finish();
    }
}
