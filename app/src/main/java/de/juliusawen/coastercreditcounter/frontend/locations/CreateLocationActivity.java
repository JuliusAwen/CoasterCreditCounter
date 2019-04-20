package de.juliusawen.coastercreditcounter.frontend.locations;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.elements.Location;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class CreateLocationActivity extends BaseActivity implements ConfirmDialogFragment.ConfirmDialogFragmentInteractionListener
{
    private CreateLocationActivityViewModel viewModel;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "CreateLocationsActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_create_location);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            this.editText = findViewById(R.id.editTextCreateLocation);

            this.viewModel = ViewModelProviders.of(this).get(CreateLocationActivityViewModel.class);

            if(this.viewModel.parentLocation == null)
            {
                this.viewModel.parentLocation = (Location) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
            }

            super.addConfirmDialogFragment();

            super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.subtitle_location_create)), this.getText(R.string.help_text_create_location));

            super.addToolbar();
            super.addToolbarHomeButton();
            super.setToolbarTitleAndSubtitle(this.viewModel.parentLocation.getName(), getString(R.string.subtitle_location_create));

            this.createEditText();
        }
    }

    @Override
    public void onConfirmDialogFragmentInteraction(View view)
    {
        ButtonFunction buttonFunction = ButtonFunction.values()[view.getId()];
        Log.i(Constants.LOG_TAG, String.format("CreateLocationsActivity.onConfirmDialogFragment:: [%S] selected", buttonFunction));

        switch (buttonFunction)
        {
            case OK:
                handleOnEditorActionDone();
                break;

            case CANCEL:
                this.returnResult(RESULT_CANCELED);
                break;
        }
    }

    private void createEditText()
    {
        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                Log.i(Constants.LOG_TAG, String.format("CreateLocationsActivity.onClickEditorAction:: actionId[%d]", actionId));

                boolean handled = false;

                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    handleOnEditorActionDone();
                    handled = true;
                }

                return handled;
            }
        });
    }

    private void handleOnEditorActionDone()
    {
        if(this.createLocation())
        {
                Log.d(Constants.LOG_TAG, String.format("CreateLocationsActivity.handleOnEditorActionDone:: adding child %s to parent %s",
                        this.viewModel.newLocation, this.viewModel.parentLocation));

                this.viewModel.parentLocation.addChildAndSetParent(this.viewModel.newLocation);

                Log.v(Constants.LOG_TAG, String.format( "CreateLocationsActivity.handleOnEditorActionDone:: parent %s has no children<Park> - returning RESULT_OK", this.viewModel.parentLocation));
                this.returnResult(RESULT_OK);
        }
        else
        {
            Toaster.makeToast(this, getString(R.string.error_name_not_valid));
        }
    }

    private boolean createLocation()
    {
        boolean success = false;
        Location location = Location.create(this.editText.getText().toString(), null);

        if(location != null)
        {
            this.viewModel.newLocation = location;
            success = true;
        }

        Log.d(Constants.LOG_TAG, String.format("CreateLocationsActivity.createLocation:: create %s success[%S]", this.viewModel.newLocation, success));

        return success;
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("CreateLocationActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            Log.i(Constants.LOG_TAG, String.format("CreateLocationActivity.returnResult:: returning new %s", this.viewModel.newLocation));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.newLocation.getUuid().toString());
            super.markForCreation(this.viewModel.newLocation);
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
        finish();
    }
}
