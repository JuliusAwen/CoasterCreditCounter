package de.juliusawen.coastercreditcounter.presentation.activities.Locations;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.enums.ButtonFunction;

public class EditLocationActivity extends BaseActivity implements ConfirmDialogFragment.ConfirmDialogFragmentInteractionListener
{
    private Location locationToEdit;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "EditLocationActivity.onCreate:: creating activity...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);

        this.initializeContent();
        this.initializeViews();
    }

    @Override
    public void onConfirmDialogFragmentInteraction(View view)
    {
        ButtonFunction buttonFunction = ButtonFunction.values()[view.getId()];
        Log.i(Constants.LOG_TAG, String.format("EditLocationsActivity.onConfirmDialogIntercation:: [%S] selected", buttonFunction));

        switch (buttonFunction)
        {
            case OK:
                handleOnEditorActionDone();
                break;

            case CANCEL:
                finish();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.KEY_ELEMENT, this.locationToEdit.getUuid().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        this.locationToEdit = (Location) Content.getInstance().getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_ELEMENT)));
    }

    private void initializeContent()
    {
        this.locationToEdit = (Location) Content.getInstance().getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
    }

    private void initializeViews()
    {
        FrameLayout frameLayoutActivity = findViewById(R.id.frameLayoutEditLocation);
        View editLocationView = getLayoutInflater().inflate(R.layout.layout_edit_location, frameLayoutActivity, false);
        frameLayoutActivity.addView(editLocationView);

        super.createToolbar(getString(R.string.title_edit_location), this.locationToEdit.getName(), false);
        super.createConfirmDialogFragment();
        this.createEditText(editLocationView);
        super.createHelpOverlayFragment(getText(R.string.help_text_edit_location), false);
    }

    private void createEditText(View view)
    {
        this.editText = view.findViewById(R.id.editTextEditLocation);
        Log.i(Constants.LOG_TAG, String.format("EditLocationActivity.createEditText:: edit %s", this.locationToEdit));
        this.editText.append(this.locationToEdit.getName());

        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                boolean handled = false;

                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    Log.i(Constants.LOG_TAG, "EditLocationActivity.createEditText.onEditorAction:: IME_ACTION_DONE");
                    handleOnEditorActionDone();

                    handled = true;
                }
                return handled;
            }
        });
    }

    private void handleOnEditorActionDone()
    {
        Log.i(Constants.LOG_TAG, String.format("EditLocationActivity.createEditText:: changing name of %s to [%s]", this.locationToEdit, this.editText.getText().toString()));
        locationToEdit.setName(this.editText.getText().toString());
        finish();
    }
}
