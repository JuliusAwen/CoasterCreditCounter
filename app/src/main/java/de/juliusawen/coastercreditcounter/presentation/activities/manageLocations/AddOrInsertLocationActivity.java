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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.presentation.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpOverlayFragment;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;
import de.juliusawen.coastercreditcounter.toolbox.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.toolbox.enums.Selection;

public class AddOrInsertLocationActivity extends AppCompatActivity implements
        HelpOverlayFragment.HelpOverlayFragmentInteractionListener,
        ConfirmDialogFragment.ConfirmDialogFragmentInteractionListener
{
    private Location locationToAddToOrInsertInto;
    private Location newLocation;
    private Selection selection;

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
        this.selection = Selection.values()[getIntent().getIntExtra(Constants.EXTRA_SELECTION, Selection.ADD_LOCATION.ordinal())];
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
        if(this.selection == Selection.ADD_LOCATION)
        {
            dynamicText = getString(R.string.dynamic_text_add);
        }
        else if(this.selection == Selection.INSERT_LOCATION)
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
        menu.add(0, Selection.HELP.ordinal(), Menu.NONE, R.string.selection_help);

        return super.onPrepareOptionsMenu(menu);
    }

    private void createEditText(View view)
    {
        this.editText = view.findViewById(R.id.editTextAddOrInsertLocation);
        this.editText.setHint(R.string.hint_enter_name);

        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                boolean handled = false;

                switch (actionId)
                {
                    case EditorInfo.IME_ACTION_DONE:
                        handleOnEditorActionDone();
                        handled = true;
                        break;
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
        Selection selection = Selection.values()[item.getItemId()];
        switch (selection)
        {
            case HELP:
                this.helpOverlayFragment.setVisibility(true);
                this.confirmDialogFragment.setVisibility(false);
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
                this.confirmDialogFragment.setVisibility(true);
                break;
        }
    }

    @Override
    public void onConfirmDialogFragmentInteraction(View view)
    {
        ButtonFunction buttonFunction = ButtonFunction.values()[view.getId()];
        switch (buttonFunction)
        {
            case OK:
                handleOnEditorActionDone();
                break;

            case CANCEL:
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
        }
    }

    private void handleOnEditorActionDone()
    {
        if(this.handleLocationCreation())
        {
            switch (this.selection)
            {
                case ADD_LOCATION:
                    this.locationToAddToOrInsertInto.addChild(this.newLocation);
                    this.returnResult();
                    break;

                case INSERT_LOCATION:
                    if (this.locationToAddToOrInsertInto.getChildren().size() > 1)
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
                    break;
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
                pickedChildren = Content.getInstance().orderLocationListByCompareList(new ArrayList<>(pickedChildren), new ArrayList<>(locationToAddToOrInsertInto.getChildren()));

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
