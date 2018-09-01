package de.juliusawen.coastercreditcounter.presentation.activities.Locations;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.SelectableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpOverlayFragment;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;
import de.juliusawen.coastercreditcounter.toolbox.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.toolbox.enums.Selection;

public class PickElementsActivity extends AppCompatActivity implements HelpOverlayFragment.HelpOverlayFragmentInteractionListener
{
    private Element element;
    private List<Element> elementsToPickFrom;
    private RadioButton radioButtonSelectOrDeselectAll;
    private TextView textViewSelectOrDeselectAll;
    private SelectableRecyclerAdapter selectableRecyclerAdapter;
    private HelpOverlayFragment helpOverlayFragment;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_elements);

        this.savedInstanceState = savedInstanceState;

        this.initializeContent();
        this.initializeViews();
    }

    private void initializeContent()
    {
        this.element = Content.getInstance().getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));

        if(this.element.getClass().equals(Location.class))
        {
            this.elementsToPickFrom = new ArrayList<Element>(((Location)this.element).getChildren());
        }
    }

    private void initializeViews()
    {
        FrameLayout frameLayoutActivity = findViewById(R.id.frameLayoutPickElements);
        View pickElementsView = getLayoutInflater().inflate(R.layout.layout_pick_elements, frameLayoutActivity, false);
        frameLayoutActivity.addView(pickElementsView);

        this.createToolbar(pickElementsView);
        this.createSelectOrDeselectAllBar(pickElementsView);
        this.createContentRecyclerView(pickElementsView);
        this.createFloatingActionButton();
        this.createHelpOverlayFragment(frameLayoutActivity.getId());
    }

    private void createToolbar(View view)
    {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_pick_elements));
        toolbar.setSubtitle(getString(R.string.subtitle_pick_add_children));
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();
        menu.add(0, Selection.HELP.ordinal(), Menu.NONE, R.string.selection_help);

        return super.onPrepareOptionsMenu(menu);
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

    private void createFloatingActionButton()
    {
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);

        Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_check));
        floatingActionButton.setImageDrawable(drawable);

        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent();
                if(!selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
                {
                    intent.putExtra(Constants.EXTRA_ELEMENTS_UUIDS, Content.getInstance().getUuidStringsFromElements(selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection()));
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else
                {
                    Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_no_entry_selected));
                }
            }
        });
    }

    private void setFloatingActionButtonVisibility(boolean isVisible)
    {
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    private void createHelpOverlayFragment(int frameLayoutId)
    {
        if(this.savedInstanceState == null)
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            this.helpOverlayFragment = HelpOverlayFragment.newInstance(getText(R.string.help_text_pick_elements), false);
            fragmentTransaction.add(frameLayoutId, this.helpOverlayFragment, Constants.FRAGMENT_TAG_HELP_OVERLAY);
            fragmentTransaction.commit();
        }
        else
        {
            this.helpOverlayFragment = (HelpOverlayFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP_OVERLAY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putString(Constants.KEY_ELEMENT, this.element.getUuid().toString());
        outState.putStringArrayList(Constants.KEY_ELEMENTS, Content.getInstance().getUuidStringsFromElements(this.elementsToPickFrom));

        if(this.selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
        {
            outState.putStringArrayList(Constants.KEY_SELECTED_ELEMENTS, new ArrayList<String>());
        }
        else
        {
            outState.putStringArrayList(Constants.KEY_SELECTED_ELEMENTS, Content.getInstance().getUuidStringsFromElements(selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection()));
        }

        outState.putString(Constants.EXTRA_RADIO_BUTTON_STATE, this.textViewSelectOrDeselectAll.getText().toString());

        outState.putBoolean(Constants.KEY_HELP_VISIBLE, this.helpOverlayFragment.isVisible());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.element = Content.getInstance().getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_ELEMENT)));

        this.elementsToPickFrom = Content.getInstance().getElementsFromUuidStrings(savedInstanceState.getStringArrayList(Constants.KEY_ELEMENTS));
        this.selectableRecyclerAdapter.updateList(this.elementsToPickFrom);

        List<String> selectedElementStrings = savedInstanceState.getStringArrayList(Constants.KEY_SELECTED_ELEMENTS);
        if(selectedElementStrings != null && selectedElementStrings.isEmpty())
        {
            this.selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().clear();
        }
        else
        {
            List<Element> selectedElements = Content.getInstance().getElementsFromUuidStrings(selectedElementStrings);
            this.selectableRecyclerAdapter.selectElements(selectedElements);
        }

        this.textViewSelectOrDeselectAll.setText(savedInstanceState.getString(Constants.EXTRA_RADIO_BUTTON_STATE));

        this.helpOverlayFragment.setVisibility(savedInstanceState.getBoolean(Constants.KEY_HELP_VISIBLE));
        this.setFloatingActionButtonVisibility(!savedInstanceState.getBoolean(Constants.KEY_HELP_VISIBLE));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        switch (selection)
        {
            case HELP:
                this.helpOverlayFragment.setVisibility(true);
                this.setFloatingActionButtonVisibility(false);
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onHelpOverlayFragmentInteraction(View view)
    {
        ButtonFunction buttonFunction = ButtonFunction.values()[view.getId()];
        switch (buttonFunction)
        {
            case CLOSE:
                this.helpOverlayFragment.setVisibility(false);
                this.setFloatingActionButtonVisibility(true);
                break;
        }
    }

}
