package de.juliusawen.coastercreditcounter.presentation.activities.elements;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.Element;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.presentation.recycler.SelectableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class PickElementsActivity extends BaseActivity
{
    private List<Element> elementsToPickFrom;
    private String toolbarTitle;
    private String toolbarSubtitle;

    private RadioButton radioButtonSelectOrDeselectAll;
    private TextView textViewSelectOrDeselectAll;
    private SelectableRecyclerAdapter contentRecyclerAdapter;

    //region @OVERRIDE
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "PickElementsActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_pick_elements);
        super.onCreate(savedInstanceState);

        this.initializeContent();

        super.addHelpOverlay(getString(R.string.title_help, this.toolbarTitle), getText(R.string.help_text_pick_elements));

        super.addToolbar();
        super.addToolbarHomeButton();
        this.decorateToolbar();

        super.addFloatingActionButton();
        this.decorateFloatingActionButton();

        this.addSelectOrDeselectAllBar();
        this.createContentRecyclerView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.contentRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.KEY_ELEMENTS, App.content.getUuidStringsFromElements(this.elementsToPickFrom));

        if(this.contentRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
        {
            outState.putStringArrayList(Constants.KEY_SELECTED_ELEMENTS, new ArrayList<String>());
        }
        else
        {
            outState.putStringArrayList(Constants.KEY_SELECTED_ELEMENTS, App.content.getUuidStringsFromElements(contentRecyclerAdapter.getSelectedElementsInOrderOfSelection()));
        }

        outState.putString(Constants.KEY_RADIO_BUTTON_STATE, this.textViewSelectOrDeselectAll.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.elementsToPickFrom = App.content.fetchElementsByUuidStrings(savedInstanceState.getStringArrayList(Constants.KEY_ELEMENTS));
        this.contentRecyclerAdapter.updateElements(this.elementsToPickFrom);

        List<String> selectedElementStrings = savedInstanceState.getStringArrayList(Constants.KEY_SELECTED_ELEMENTS);
        if(selectedElementStrings != null && selectedElementStrings.isEmpty())
        {
            this.contentRecyclerAdapter.getSelectedElementsInOrderOfSelection().clear();
        }
        else
        {
            List<Element> selectedElements = App.content.fetchElementsByUuidStrings(selectedElementStrings);
            this.contentRecyclerAdapter.selectElements(selectedElements);
        }

        this.textViewSelectOrDeselectAll.setText(savedInstanceState.getString(Constants.KEY_RADIO_BUTTON_STATE));

    }
    //endregion

    private void initializeContent()
    {
        this.elementsToPickFrom = App.content.fetchElementsByUuidStrings(getIntent().getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS));
        this.toolbarTitle = getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE);
        this.toolbarSubtitle = getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE);
        Log.v(Constants.LOG_TAG, String.format("PickElementsActivity.initializeContent:: initialized with #[%d] elements", this.elementsToPickFrom.size()));
    }

    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle(this.toolbarTitle, this.toolbarSubtitle);
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
        if(!this.contentRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
        {
            Log.d(Constants.LOG_TAG, "PickElementsActivity.onClickFloatingActionButton:: accepted - return code <OK>");
            returnResult(RESULT_OK);
        }
        else
        {
            Log.d(Constants.LOG_TAG, "PickElementsActivity.onClickFloatingActionButton:: no parentElement selected");
            Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_no_entry_selected));
        }
    }
    //endregion

    //region SELECTABLE RECYCLER VIEW
    private void createContentRecyclerView()
    {
        this.contentRecyclerAdapter = new SelectableRecyclerAdapter(this.elementsToPickFrom, true, new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                onClickContentRecyclerView(view);
            }

            @Override
            public boolean onLongClick(View view, int position)
            {
                onLongClickContentRecyclerView(view);
                return true;
            }
        });

        RecyclerView recyclerView = this.findViewById(android.R.id.content).findViewById(R.id.recyclerViewPickElements);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(this.contentRecyclerAdapter);
    }

    private void onClickContentRecyclerView(View view)
    {
        if(view.isSelected() && contentRecyclerAdapter.isAllSelected())
        {
            this.changeRadioButtonToDeselectAll();
        }
        else
        {
            if(this.textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_deselect_all)))
            {
                this.changeRadioButtonToSelectAll();
            }
        }
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
                onMenuItemClickLongClickContentRecyclerView(longClickedElement);
                return true;
            }
        });
        popupMenu.show();
    }

    private void onMenuItemClickLongClickContentRecyclerView(Element longClickedElement)
    {
        ActivityTool.startActivityEdit(this, longClickedElement);
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
            this.contentRecyclerAdapter.selectAllElements();
            changeRadioButtonToDeselectAll();
        }
        else if(this.textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_deselect_all)))
        {
            this.contentRecyclerAdapter.deselectAllElements();
            changeRadioButtonToSelectAll();
        }
    }

    private void changeRadioButtonToSelectAll()
    {
        Log.v(Constants.LOG_TAG, "PickElementsActivity.changeRadioButtonToSelectAll:: changing RadioButton");

        this.textViewSelectOrDeselectAll.setText(R.string.text_select_all);
        this.radioButtonSelectOrDeselectAll.setChecked(false);
    }

    private void changeRadioButtonToDeselectAll()
    {
        Log.v(Constants.LOG_TAG, "PickElementsActivity.changeRadioButtonToDeselectAll:: changing RadioButton");

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
            intent.putExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(contentRecyclerAdapter.getSelectedElementsInOrderOfSelection()));
        }

        setResult(resultCode, intent);
        finish();
    }
}
