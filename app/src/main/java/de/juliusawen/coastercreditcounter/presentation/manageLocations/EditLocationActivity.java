package de.juliusawen.coastercreditcounter.presentation.manageLocations;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpOverlayFragment;

public class EditLocationActivity extends AppCompatActivity implements HelpOverlayFragment.OnFragmentInteractionListener
{
    private Element currentElement;

    private EditText editText;

    private Boolean helpOverlayVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);

        this.initializeContent();
        this.initializeViews();
    }

    private void initializeContent()
    {
        this.currentElement = Content.getInstance().getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_UUID)));
    }

    private void initializeViews()
    {
        FrameLayout frameLayoutActivity = findViewById(R.id.frameLayoutEditLocation);
        View addLocationView = getLayoutInflater().inflate(R.layout.layout_edit_location, frameLayoutActivity, false);
        frameLayoutActivity.addView(addLocationView);

        this.createToolbar(addLocationView);
        this.createEditText(addLocationView);
        this.createConfirmDialog(addLocationView);

        this.createHelpOverlay();
    }

    private void createToolbar(View view)
    {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_edit_location));
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
        this.editText = view.findViewById(R.id.editTextEditLocation);
        this.editText.setText(this.currentElement.getName());

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

    private void createConfirmDialog(View view)
    {
        Button buttonCancel = view.findViewById(R.id.buttonActionDialogTwoImageButtonsBottomLeft);
        buttonCancel.setId(Constants.BUTTON_CANCEL);
        buttonCancel.setText(R.string.button_text_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        Button buttonOk = view.findViewById(R.id.buttonActionDialogTwoImageButtonsBottomRight);
        buttonOk.setId(Constants.BUTTON_OK);
        buttonOk.setText(R.string.button_text_ok);
        buttonOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handleOnEditorActionDone();
            }
        });
    }

    private void createHelpOverlay()
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        HelpOverlayFragment helpOverlayFragment = HelpOverlayFragment.newInstance(getText(R.string.help_text_edit_location), false);
        fragmentTransaction.add(R.id.frameLayoutEditLocation, helpOverlayFragment, Constants.FRAGMENT_TAG_HELP);
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

    private void handleOnEditorActionDone()
    {
        currentElement.setName(this.editText.getText().toString());
        finish();
    }

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
