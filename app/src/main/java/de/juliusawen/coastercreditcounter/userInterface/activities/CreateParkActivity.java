package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.Toaster;

public class CreateParkActivity extends BaseActivity
{
    private CreateParkActivityViewModel viewModel;
    private EditText editText;


    protected void setContentView()
    {
        setContentView(R.layout.activity_create_park);
    }

    protected void create()
    {
        this.editText = findViewById(R.id.editTextCreatePark);

        this.viewModel = ViewModelProviders.of(this).get(CreateParkActivityViewModel.class);

        if(this.viewModel.parentLocation == null)
        {
            this.viewModel.parentLocation = (Location) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.subtitle_park_create)), this.getText(R.string.help_text_create_park));
        super.addToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle(this.viewModel.parentLocation.getName(), getString(R.string.subtitle_park_create));
        super.addFloatingActionButton();

        this.decorateFloatingActionButton();
        this.createEditText();
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_check, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handleOnEditorActionDone();
            }
        });
        super.setFloatingActionButtonVisibility(true);
    }

    private void createEditText()
    {
        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                Log.i(Constants.LOG_TAG, String.format("CreateParkActivity.onClickEditorAction:: actionId[%d]", actionId));

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
        if(this.createPark())
        {
            Log.d(Constants.LOG_TAG, String.format("CreateParkActivity.handleOnEditorActionDone:: adding child %s to parent %s",
                    this.viewModel.newPark, this.viewModel.parentLocation));

            this.viewModel.parentLocation.addChildAndSetParent(this.viewModel.newPark);

            Log.v(Constants.LOG_TAG, String.format( "CreateParkActivity.handleOnEditorActionDone:: parent %s has no children<Park> - returning RESULT_OK", this.viewModel.parentLocation));
            this.returnResult(RESULT_OK);
        }
        else
        {
            Toaster.makeToast(this, getString(R.string.error_name_not_valid));
        }
    }

    private boolean createPark()
    {
        boolean success = false;
        Park park = Park.create(this.editText.getText().toString(), null);

        if(park != null)
        {
            this.viewModel.newPark = park;
            success = true;
        }

        Log.d(Constants.LOG_TAG, String.format("CreateParkActivity.createLocation:: show %s success[%S]", this.viewModel.newPark, success));

        return success;
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("CreateParkActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            Log.i(Constants.LOG_TAG, String.format("CreateParkActivity.returnResult:: returning new %s", this.viewModel.newPark));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.newPark.getUuid().toString());
            super.markForCreation(this.viewModel.newPark);
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + this.getClass().getSimpleName());
        finish();
    }
}
