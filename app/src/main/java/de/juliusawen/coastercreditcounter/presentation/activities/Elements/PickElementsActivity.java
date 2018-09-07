package de.juliusawen.coastercreditcounter.presentation.activities.Elements;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.SelectableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class PickElementsActivity extends BaseActivity
{
    private Element element;
    private List<Element> elementsToPickFrom;
    private RadioButton radioButtonSelectOrDeselectAll;
    private TextView textViewSelectOrDeselectAll;
    private SelectableRecyclerAdapter selectableRecyclerAdapter;

    //region @OVERRIDE
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e(Constants.LOG_TAG, "onCreate()");
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "PickElementsActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_pick_elements);
        FrameLayout frameLayoutActivity = findViewById(R.id.frameLayoutPickElements);
        frameLayoutActivity.addView(getLayoutInflater().inflate(R.layout.layout_pick_elements, frameLayoutActivity, false));

        super.onCreate(savedInstanceState);
        super.addToolbar();
        super.addFloatingActionButton();
        super.addHelpOverlay(null, getText(R.string.help_text_pick_elements));

        this.initializeContent();

        this.decorateToolbar();
        this.decorateFloatingActionButton();
        this.addSelectOrDeselectAllBar();
        this.addSelectableRecyclerView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putString(Constants.KEY_ELEMENT, this.element.getUuid().toString());
        outState.putStringArrayList(Constants.KEY_ELEMENTS, Content.getUuidStringsFromElements(this.elementsToPickFrom));

        if(this.selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
        {
            outState.putStringArrayList(Constants.KEY_SELECTED_ELEMENTS, new ArrayList<String>());
        }
        else
        {
            outState.putStringArrayList(Constants.KEY_SELECTED_ELEMENTS, Content.getUuidStringsFromElements(selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection()));
        }

        outState.putString(Constants.EXTRA_RADIO_BUTTON_STATE, this.textViewSelectOrDeselectAll.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.element = super.content.getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_ELEMENT)));

        this.elementsToPickFrom = super.content.fetchElementsFromUuidStrings(savedInstanceState.getStringArrayList(Constants.KEY_ELEMENTS));
        this.selectableRecyclerAdapter.updateList(this.elementsToPickFrom);

        List<String> selectedElementStrings = savedInstanceState.getStringArrayList(Constants.KEY_SELECTED_ELEMENTS);
        if(selectedElementStrings != null && selectedElementStrings.isEmpty())
        {
            this.selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().clear();
        }
        else
        {
            List<Element> selectedElements = super.content.fetchElementsFromUuidStrings(selectedElementStrings);
            this.selectableRecyclerAdapter.selectElements(selectedElements);
        }

        this.textViewSelectOrDeselectAll.setText(savedInstanceState.getString(Constants.EXTRA_RADIO_BUTTON_STATE));
    }
    //endregion

    private void initializeContent()
    {
        this.element = super.content.getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));

        if(this.element.isInstance(Location.class))
        {
            this.elementsToPickFrom = new ArrayList<>(this.element.getChildrenOfInstance(Location.class));
        }
    }

    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle(getString(R.string.title_pick_elements), getString(R.string.subtitle_pick_add_children_to_new_location));
        super.addToolbarHomeButton();
    }

    //region FLOATING ACTION BUTTON
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
        if(!this.selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
        {
            Log.d(Constants.LOG_TAG, "PickElementsActivity.onClickFloatingActionButton:: accepted - return code <OK>");
            returnResult(RESULT_OK);
        }
        else
        {
            Log.d(Constants.LOG_TAG, "PickElementsActivity.onClickFloatingActionButton:: no element selected");
            Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_no_entry_selected));
        }
    }
    //endregion

    //region SELECTABLE RECYCLER VIEW
    private void addSelectableRecyclerView()
    {
        this.selectableRecyclerAdapter = new SelectableRecyclerAdapter(this.elementsToPickFrom, true, new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                onClickSelectableRecyclerView(view);
            }

            @Override
            public void onLongClick(View view, int position) {}
        });

        RecyclerView recyclerView = this.findViewById(android.R.id.content).findViewById(R.id.recyclerViewPickElements);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(this.selectableRecyclerAdapter);
    }

    private void onClickSelectableRecyclerView(View view)
    {
        if(view.isSelected() && selectableRecyclerAdapter.isAllSelected())
        {
            this.changeRadioButtonToDeselectAll();
        }
        else
        {
            this.changeRadioButtonToSelectAll();
        }
    }
    //endregion

    //region SELECT OR DESELECT ALL BAR
    private void addSelectOrDeselectAllBar()
    {
        LinearLayout linearLayoutSelectAll = this.findViewById(android.R.id.content).findViewById(R.id.linearLayoutPickElements_SelectAll);
        linearLayoutSelectAll.setVisibility(View.VISIBLE);

        this.textViewSelectOrDeselectAll = linearLayoutSelectAll.findViewById(R.id.textViewPickElements_SelectAll);
        this.textViewSelectOrDeselectAll.setText(R.string.text_select_all);

        this.radioButtonSelectOrDeselectAll = linearLayoutSelectAll.findViewById(R.id.radioButtonPickElements_SelectAll);
        this.radioButtonSelectOrDeselectAll.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onClickSelectOrDeselectAllBar();
            }
        });
    }

    private void onClickSelectOrDeselectAllBar()
    {
        Log.d(Constants.LOG_TAG, "PickElementsActivity.onClickSelectOrDeselectAllBar:: RadioButton clicked");

        if(this.textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_select_all)))
        {
            this.selectableRecyclerAdapter.selectAllElements();
            changeRadioButtonToDeselectAll();
        }
        else if(this.textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_deselect_all)))
        {
            this.selectableRecyclerAdapter.deselectAllElements();
            changeRadioButtonToSelectAll();
        }
    }

    private void changeRadioButtonToSelectAll()
    {
        Log.d(Constants.LOG_TAG, "PickElementsActivity.changeRadioButtonToSelectAll:: changing RadioButton");

        this.textViewSelectOrDeselectAll.setText(R.string.text_select_all);
        this.radioButtonSelectOrDeselectAll.setChecked(false);
    }

    private void changeRadioButtonToDeselectAll()
    {
        Log.d(Constants.LOG_TAG, "PickElementsActivity.changeRadioButtonToDeselectAll:: changing RadioButton");

        this.textViewSelectOrDeselectAll.setText(R.string.text_deselect_all);
        this.radioButtonSelectOrDeselectAll.setChecked(false);
    }
    //endregion

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("PickElementsActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            intent.putExtra(Constants.EXTRA_ELEMENTS_UUIDS, Content.getUuidStringsFromElements(selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection()));
        }

        setResult(resultCode, intent);
        finish();
    }
}
