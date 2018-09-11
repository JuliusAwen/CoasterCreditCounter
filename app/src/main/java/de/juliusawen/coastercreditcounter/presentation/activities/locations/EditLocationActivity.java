package de.juliusawen.coastercreditcounter.presentation.activities.locations;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class EditLocationActivity extends BaseActivity implements ConfirmDialogFragment.ConfirmDialogFragmentInteractionListener
{
    private Location locationToEdit;
    private EditText editText;

    //region @OVERRIDE
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "EditLocationActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_edit_location);
        super.onCreate(savedInstanceState);

        this.initializeContent();

        super.addConfirmDialog();

        super.addHelpOverlay(getString(R.string.title_help, getString(R.string.subtitle_edit_location)), getText(R.string.help_text_edit_location));

        super.addToolbar();
        this.decorateToolbar();

        this.createEditText();

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
                finish();
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
        this.locationToEdit = (Location) App.content.getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_ELEMENT)));
    }
    //endregion

    private void initializeContent()
    {
        this.locationToEdit = (Location) App.content.getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
    }

    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle(this.locationToEdit.getName(), getString(R.string.subtitle_edit_location));
    }

    //region EDIT TEXT
    private void createEditText()
    {
        this.editText = this.findViewById(android.R.id.content).findViewById(R.id.editTextEditLocation);

        Log.d(Constants.LOG_TAG, String.format("EditLocationActivity.createEditText:: edit %s", this.locationToEdit));
        this.editText.append(this.locationToEdit.getName());

        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                boolean handled = onClickEditorAction(actionId);
                finish();
                return handled;
            }
        });
    }

    private boolean onClickEditorAction(int actionId)
    {
        boolean handled = false;

        if (actionId == EditorInfo.IME_ACTION_DONE)
        {
            Log.d(Constants.LOG_TAG, "EditLocationActivity.onClickEditorAction<IME_ACTION_DONE>:: ");
            this.handleOnEditorActionDone();
            handled = true;
        }
        return handled;
    }

    private void handleOnEditorActionDone()
    {
        String editText = this.editText.getText().toString();
        if(!this.locationToEdit.getName().equals(editText))
        {
            if(!this.locationToEdit.setName(editText))
            {
                Toaster.makeToast(this, getString(R.string.error_text_name_not_valid));
            }
            Log.i(Constants.LOG_TAG, String.format("EditLocationActivity.createEditText:: name of %s changed to [%s]", this.locationToEdit, editText));
        }
        Log.v(Constants.LOG_TAG, "EditLocationActivity.createEditText:: name has not changed");
    }
    //endregion
}
