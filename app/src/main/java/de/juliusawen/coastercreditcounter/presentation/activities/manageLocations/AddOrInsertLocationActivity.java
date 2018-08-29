package de.juliusawen.coastercreditcounter.presentation.activities.manageLocations;

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

import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.Toolbox.Toaster;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.presentation.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpOverlayFragment;

public class AddOrInsertLocationActivity extends AppCompatActivity implements
        HelpOverlayFragment.HelpOverlayFragmentInteractionListener,
        ConfirmDialogFragment.ConfirmDialogFragmentInteractionListener
{
    private Location locationToAddToOrInsertInto;
    private Location newLocation;
    private int selection;

    private EditText editText;
    private HelpOverlayFragment helpOverlayFragment;
    private ConfirmDialogFragment confirmDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_insert_location);

        this.initializeContent();
        this.initializeViews();
    }

    private void initializeContent()
    {
        this.locationToAddToOrInsertInto = (Location) Content.getInstance().getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        this.selection = getIntent().getIntExtra(Constants.EXTRA_SELECTION, Constants.SELECTION_ADD);
    }

    private void initializeViews()
    {
        FrameLayout frameLayoutActivity = findViewById(R.id.frameLayoutAddOrInsertLocation);
        View addLocationView = getLayoutInflater().inflate(R.layout.layout_add_or_insert_location, frameLayoutActivity, false);
        frameLayoutActivity.addView(addLocationView);

        this.createToolbar(addLocationView);
        this.createEditText(addLocationView);
        this.createConfirmDialogFragment(frameLayoutActivity.getId());
        this.createHelpOverlayFragment(frameLayoutActivity.getId());
    }

    private void createToolbar(View view)
    {
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        String dynamicText = "";
        if(this.selection == Constants.SELECTION_ADD)
        {
            dynamicText = getString(R.string.dynamic_text_add);
        }
        else if(this.selection == Constants.SELECTION_INSERT)
        {
            dynamicText = getString(R.string.dynamic_text_insert);
        }

        toolbar.setTitle(getString(R.string.title_add_or_insert_location, dynamicText));
        toolbar.setSubtitle(this.locationToAddToOrInsertInto.getName());
        setSupportActionBar(toolbar);
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
        this.editText = view.findViewById(R.id.editTextAddOrInsertLocation);
        this.editText.setHint(R.string.edit_text_hint_enter_name);

        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    handleOnEditorActionDone();
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void createConfirmDialogFragment(int frameLayoutId)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        this.confirmDialogFragment = ConfirmDialogFragment.newInstance();
        fragmentTransaction.add(frameLayoutId, this.confirmDialogFragment, Constants.FRAGMENT_TAG_CONFIRM_DIALOG);
        fragmentTransaction.commit();
    }

    private void createHelpOverlayFragment(int frameLayoutId)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        this.helpOverlayFragment = HelpOverlayFragment.newInstance(getText(R.string.help_text_add_or_insert_location), false);
        fragmentTransaction.add(frameLayoutId, this.helpOverlayFragment, Constants.FRAGMENT_TAG_HELP_OVERLAY);
        fragmentTransaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putString(Constants.KEY_ELEMENT, this.locationToAddToOrInsertInto.getUuid().toString());
        outState.putBoolean(Constants.KEY_HELP_VISIBLE, this.helpOverlayFragment.isVisible());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.locationToAddToOrInsertInto = (Location) Content.getInstance().getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_ELEMENT)));
        this.helpOverlayFragment.setVisibility(savedInstanceState.getBoolean(Constants.KEY_HELP_VISIBLE));
        this.confirmDialogFragment.setVisibility(!savedInstanceState.getBoolean(Constants.KEY_HELP_VISIBLE));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == Constants.SELECTION_HELP)
        {
            this.helpOverlayFragment.setVisibility(true);
            this.confirmDialogFragment.setVisibility(false);
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
            this.confirmDialogFragment.setVisibility(true);
        }
    }

    @Override
    public void onConfirmDialogFragmentInteraction(View view)
    {
        if(view.getId() == Constants.BUTTON_OK)
        {
            handleOnEditorActionDone();
        }
        else if(view.getId() == Constants.BUTTON_CANCEL)
        {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    private void handleOnEditorActionDone()
    {
        if(this.handleLocationCreation())
        {
            if(this.selection == Constants.SELECTION_ADD)
            {
                this.locationToAddToOrInsertInto.addChild(this.newLocation);
                this.returnResult();
            }
            else if(this.selection == Constants.SELECTION_INSERT)
            {
                if(this.locationToAddToOrInsertInto.getChildren().size() > 1)
                {
                    Intent intent = new Intent(getApplicationContext(), PickElementsActivity.class);
                    intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.locationToAddToOrInsertInto.getUuid().toString());
                    startActivityForResult(intent, Constants.REQUEST_PICK_ELEMENTS);
                }
                else
                {
                    this.locationToAddToOrInsertInto.insertNode(this.newLocation, this.locationToAddToOrInsertInto.getChildren());
                    returnResult();
                }
            }
        }
        else
        {
            Toaster.makeToast(this, getString(R.string.error_text_location_name_not_valid));
        }
    }

    private boolean handleLocationCreation()
    {
        boolean success = false;
        this.newLocation = Location.createLocation(this.editText.getText().toString());

        if(this.newLocation != null)
        {
            Content.getInstance().addElement(this.newLocation);
            success = true;
        }

        return success;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == Constants.REQUEST_PICK_ELEMENTS)
        {
            if(resultCode == RESULT_OK)
            {
                List<String> uuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
                List<Location> pickedChildren = Content.getInstance().getLocationsFromUuidStrings(uuidStrings);

                this.locationToAddToOrInsertInto.insertNode(this.newLocation, pickedChildren);

                this.returnResult();
            }
        }
    }

    private void returnResult()
    {
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.newLocation.getUuid().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
