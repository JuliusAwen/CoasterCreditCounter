package de.juliusawen.coastercreditcounter.presentation.activities.manageLocations;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.Toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.Toolbox.Toaster;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.SelectableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpOverlayFragment;

public class PickElementsActivity extends AppCompatActivity implements HelpOverlayFragment.HelpOverlayFragmentInteractionListener
{
    private Element element;
    private List<Element> elementsToPickFrom;
    private SelectableRecyclerAdapter selectableRecyclerAdapter;
    private HelpOverlayFragment helpOverlayFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_elements);

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
        this.createContentRecyclerView(pickElementsView);
        this.createFloatingActionButton();
        this.createHelpOverlayFragment(frameLayoutActivity.getId());
    }

    private void createToolbar(View view)
    {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_pick_elements));
        toolbar.setSubtitle(getString(R.string.subtitle_pick_children_insert));
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
        menu.add(0, Constants.SELECTION_HELP, Menu.NONE, R.string.selection_help);

        return super.onPrepareOptionsMenu(menu);
    }

    private void createContentRecyclerView(View view)
    {
        this.selectableRecyclerAdapter = new SelectableRecyclerAdapter(this.elementsToPickFrom, true);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewPickElements);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(this.selectableRecyclerAdapter);
    }

    private void createFloatingActionButton()
    {
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButtonPickElements);

        Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_check));
        floatingActionButton.setImageDrawable(drawable);

        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent();
                if(!selectableRecyclerAdapter.getSelectedElements().isEmpty())
                {
                    intent.putExtra(Constants.EXTRA_ELEMENTS_UUIDS, Content.getInstance().getUuidStringsFromElements(selectableRecyclerAdapter.getSelectedElements()));
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
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButtonPickElements);
        floatingActionButton.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    private void createHelpOverlayFragment(int frameLayoutId)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        this.helpOverlayFragment = HelpOverlayFragment.newInstance(getText(R.string.help_text_pick_elements), false);
        fragmentTransaction.add(frameLayoutId, this.helpOverlayFragment, Constants.FRAGMENT_TAG_HELP_OVERLAY);
        fragmentTransaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.KEY_ELEMENTS, Content.getInstance().getUuidStringsFromElements(this.elementsToPickFrom));
        outState.putString(Constants.KEY_ELEMENT, this.element.getUuid().toString());

        if(this.selectableRecyclerAdapter.getSelectedElements().isEmpty())
        {
            outState.putStringArrayList(Constants.KEY_SELECTED_ELEMENTS, new ArrayList<String>());
        }
        else
        {
            outState.putStringArrayList(Constants.KEY_SELECTED_ELEMENTS, Content.getInstance().getUuidStringsFromElements(selectableRecyclerAdapter.getSelectedElements()));
        }

        outState.putBoolean(Constants.KEY_HELP_VISIBLE, this.helpOverlayFragment.isVisible());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.elementsToPickFrom = Content.getInstance().getElementsFromUuidStrings(savedInstanceState.getStringArrayList(Constants.KEY_ELEMENTS));
        this.element = Content.getInstance().getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_ELEMENT)));

        List<String> selectedElementStrings = savedInstanceState.getStringArrayList(Constants.KEY_SELECTED_ELEMENTS);

        if(selectedElementStrings != null && selectedElementStrings.isEmpty())
        {
            this.selectableRecyclerAdapter.getSelectedElements().clear();
        }
        else
        {
            List<Element> selectedElements = Content.getInstance().getElementsFromUuidStrings(selectedElementStrings);
            this.selectableRecyclerAdapter.selectElements(selectedElements);
        }

        this.helpOverlayFragment.setVisibility(savedInstanceState.getBoolean(Constants.KEY_HELP_VISIBLE));
        this.setFloatingActionButtonVisibility(!savedInstanceState.getBoolean(Constants.KEY_HELP_VISIBLE));

        this.selectableRecyclerAdapter.updateList(this.elementsToPickFrom);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == Constants.SELECTION_HELP)
        {
            this.helpOverlayFragment.setVisibility(true);
            this.setFloatingActionButtonVisibility(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onHelpOverlayFragmentInteraction(View view)
    {
        if(view.getId() == Constants.BUTTON_CLOSE)
        {
            this.helpOverlayFragment.setVisibility(false);
            this.setFloatingActionButtonVisibility(true);
        }
    }
}
