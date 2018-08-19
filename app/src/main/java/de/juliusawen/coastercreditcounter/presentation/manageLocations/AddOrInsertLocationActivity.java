package de.juliusawen.coastercreditcounter.presentation.manageLocations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Objects;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpOverlayFragment;

public class AddOrInsertLocationActivity extends AppCompatActivity implements HelpOverlayFragment.OnFragmentInteractionListener
{
    private Element currentElement;
    private String subtitle;

    private int selection;

    private Boolean helpOverlayVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        this.initializeContent();
        this.initializeViews();
    }

    private void initializeContent()
    {
        Intent intent = getIntent();

        this.currentElement = Content.getInstance().getElementByUuid(UUID.fromString(intent.getStringExtra(Constants.EXTRA_UUID)));
        this.selection = intent.getIntExtra(Constants.EXTRA_SELECTION, 0);
        this.subtitle = currentElement.getName();
    }

    private void initializeViews()
    {
        FrameLayout frameLayoutActivity = findViewById(R.id.frameLayout_addLocation);
        View addLocationView = getLayoutInflater().inflate(R.layout.layout_add_location, frameLayoutActivity, false);
        frameLayoutActivity.addView(addLocationView);

        this.createToolbar(addLocationView);
        this.createEditText(addLocationView);

        this.createHelpOverlay();
    }

    private void createToolbar(View view)
    {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_add_location));
        toolbar.setSubtitle(this.subtitle);
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

    private void createEditText(View view)
    {
        EditText editText = view.findViewById(R.id.editTextAddLocation);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    Location currentLocation = (Location) currentElement;
                    Location newLocation = currentLocation.createLocation(textView.getText().toString());

                    if(selection == Constants.SELECTION_ADD)
                    {
                        currentLocation.addChild(newLocation);
                    }
                    else if(selection == Constants.SELECTION_INSERT)
                    {
                        currentLocation.insertNode(newLocation);
                    }

                    Content.getInstance().addElement(newLocation);

                    finish();
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void createHelpOverlay()
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        HelpOverlayFragment helpOverlayFragment = HelpOverlayFragment.newInstance(getText(R.string.help_text_add_location), false);
        fragmentTransaction.add(R.id.frameLayout_addLocation, helpOverlayFragment, Constants.FRAGMENT_TAG_HELP);
        fragmentTransaction.commit();

        this.helpOverlayVisible = false;
    }

    private void setHelpOverlayFragmentVisibility(boolean isVisible)
    {
        HelpOverlayFragment helpOverlayFragment = (HelpOverlayFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP);

        if(isVisible)
        {
            helpOverlayFragment.fragmentView.setVisibility(View.VISIBLE);
        }
        else
        {
            helpOverlayFragment.fragmentView.setVisibility(View.INVISIBLE);
        }

        this.helpOverlayVisible = isVisible;
    }

    @Override
    public void onFragmentInteraction(View view)
    {
        if(view.getId() == Constants.BUTTON_CLOSE)
        {
            this.setHelpOverlayFragmentVisibility(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putString(Constants.KEY_CURRENT_ELEMENT, this.currentElement.getUuid().toString());

        outState.putBoolean(Constants.KEY_HELP_ACTIVE, this.helpOverlayVisible);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.currentElement = Content.getInstance().getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_CURRENT_ELEMENT)));
        this.setHelpOverlayFragmentVisibility(savedInstanceState.getBoolean(Constants.KEY_HELP_ACTIVE));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == Constants.SELECTION_HELP)
        {
            this.setHelpOverlayFragmentVisibility(true);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
