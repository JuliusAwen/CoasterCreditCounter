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

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.Toolbox.Toaster;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.presentation.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpOverlayFragment;

public class AddOrInsertLocationActivity extends AppCompatActivity implements
        HelpOverlayFragment.HelpOverlayFragmentInteractionListener,
        ConfirmDialogFragment.ConfirmDialogFragmentInteractionListener
{
    private Element currentElement;
    private int selection;

    private EditText editText;
    private HelpOverlayFragment helpOverlayFragment;
    private ConfirmDialogFragment confirmDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_or_insert_location_activity);

        this.initializeContent();
        this.initializeViews();
    }

    private void initializeContent()
    {
        Intent intent = getIntent();

        this.currentElement = Content.getInstance().getElementByUuid(UUID.fromString(intent.getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        this.selection = intent.getIntExtra(Constants.EXTRA_SELECTION, 0);
    }

    private void initializeViews()
    {
        FrameLayout frameLayoutActivity = findViewById(R.id.frameLayoutAddOrInsertLocation);
        View addLocationView = getLayoutInflater().inflate(R.layout.add_or_insert_location_layout, frameLayoutActivity, false);
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
        toolbar.setSubtitle(this.currentElement.getName());
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

        outState.putString(Constants.KEY_CURRENT_ELEMENT, this.currentElement.getUuid().toString());
        outState.putBoolean(Constants.KEY_HELP_VISIBLE, this.helpOverlayFragment.isVisible());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.currentElement = Content.getInstance().getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_CURRENT_ELEMENT)));
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
        Location newLocation = Location.createLocation(this.editText.getText().toString());

        if(newLocation != null)
        {
            if(this.selection == Constants.SELECTION_ADD)
            {
                ((Location) this.currentElement).addChild(newLocation);
            }
            else if(this.selection == Constants.SELECTION_INSERT)
            {
                ((Location) this.currentElement).insertNode(newLocation);
            }

            Content.getInstance().addElement(newLocation);

            Intent intent = new Intent();
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, newLocation.getUuid().toString());
            setResult(RESULT_OK, intent);
            finish();
        }
        else
        {
            Toaster.makeToast(this, getString(R.string.error_text_location_name_not_valid));
        }
    }
}
