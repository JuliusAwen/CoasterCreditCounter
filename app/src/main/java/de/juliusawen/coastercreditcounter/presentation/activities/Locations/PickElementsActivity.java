package de.juliusawen.coastercreditcounter.presentation.activities.Locations;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER);
        Log.d(Constants.LOG_TAG, "PickElementsActivity.onCreate:: creating activity...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_elements);


        this.initializeContent();
        this.initializeViews();

        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putString(Constants.KEY_ELEMENT, this.element.getUuid().toString());
        outState.putStringArrayList(Constants.KEY_ELEMENTS, Content.createUuidStringsFromElements(this.elementsToPickFrom));

        if(this.selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
        {
            outState.putStringArrayList(Constants.KEY_SELECTED_ELEMENTS, new ArrayList<String>());
        }
        else
        {
            outState.putStringArrayList(Constants.KEY_SELECTED_ELEMENTS, Content.createUuidStringsFromElements(selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection()));
        }

        outState.putString(Constants.EXTRA_RADIO_BUTTON_STATE, this.textViewSelectOrDeselectAll.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.element = Content.getInstance().getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_ELEMENT)));

        this.elementsToPickFrom = Content.getInstance().fetchElementsFromUuidStrings(savedInstanceState.getStringArrayList(Constants.KEY_ELEMENTS));
        this.selectableRecyclerAdapter.updateList(this.elementsToPickFrom);

        List<String> selectedElementStrings = savedInstanceState.getStringArrayList(Constants.KEY_SELECTED_ELEMENTS);
        if(selectedElementStrings != null && selectedElementStrings.isEmpty())
        {
            this.selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().clear();
        }
        else
        {
            List<Element> selectedElements = Content.getInstance().fetchElementsFromUuidStrings(selectedElementStrings);
            this.selectableRecyclerAdapter.selectElements(selectedElements);
        }

        this.textViewSelectOrDeselectAll.setText(savedInstanceState.getString(Constants.EXTRA_RADIO_BUTTON_STATE));
    }

    private void initializeContent()
    {
        this.element = Content.getInstance().getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));

        if(this.element.getClass().equals(Location.class))
        {
            this.elementsToPickFrom = new ArrayList<>(this.element.getChildren());
        }
    }

    private void initializeViews()
    {
        FrameLayout frameLayoutActivity = findViewById(R.id.frameLayoutPickElements);
        View pickElementsView = getLayoutInflater().inflate(R.layout.layout_pick_elements, frameLayoutActivity, false);
        frameLayoutActivity.addView(pickElementsView);

        super.createToolbar(pickElementsView, getString(R.string.title_pick_elements), getString(R.string.subtitle_pick_add_children_to_new_location), true);
        this.createFloatingActionButton();
        this.createSelectOrDeselectAllBar(pickElementsView);
        this.createContentRecyclerView(pickElementsView);
        super.createHelpOverlayFragment(frameLayoutActivity, getText(R.string.help_text_pick_elements), false);
    }

    private void createFloatingActionButton()
    {
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent();
                if(!selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
                {
                    intent.putExtra(Constants.EXTRA_ELEMENTS_UUIDS, Content.createUuidStringsFromElements(selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection()));
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else
                {
                    Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_no_entry_selected));
                }
            }
        });

        super.createFloatingActionButton(floatingActionButton, DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_check)));
    }

    private void createSelectOrDeselectAllBar(View view)
    {
        LinearLayout linearLayoutSelectAll = view.findViewById(R.id.linearLayoutPickElements_SelectAll);
        linearLayoutSelectAll.setVisibility(View.VISIBLE);

        this.textViewSelectOrDeselectAll = linearLayoutSelectAll.findViewById(R.id.textViewPickElements_SelectAll);
        this.textViewSelectOrDeselectAll.setText(R.string.text_select_all);

        this.radioButtonSelectOrDeselectAll = linearLayoutSelectAll.findViewById(R.id.radioButtonPickElements_SelectAll);
        this.radioButtonSelectOrDeselectAll.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_select_all)))
                {
                    selectableRecyclerAdapter.selectAllElements();
                    changeToDeselectAll();
                }
                else if(textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_deselect_all)))
                {
                    selectableRecyclerAdapter.deselectAllElements();
                    changeToSelectAll();
                }
            }
        });
    }

    private void changeToSelectAll()
    {
        textViewSelectOrDeselectAll.setText(R.string.text_select_all);
        radioButtonSelectOrDeselectAll.setChecked(false);
    }

    private void changeToDeselectAll()
    {
        textViewSelectOrDeselectAll.setText(R.string.text_deselect_all);
        radioButtonSelectOrDeselectAll.setChecked(false);
    }

    private void createContentRecyclerView(View view)
    {
        this.selectableRecyclerAdapter = new SelectableRecyclerAdapter(this.elementsToPickFrom, true, new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                if(view.isSelected() && selectableRecyclerAdapter.isAllSelected())
                {
                    changeToDeselectAll();
                }
                else
                {
                    changeToSelectAll();
                }
            }

            @Override
            public void onLongClick(View view, int position) {}
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewPickElements);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(this.selectableRecyclerAdapter);
    }
}
