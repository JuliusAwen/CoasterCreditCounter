package de.juliusawen.coastercreditcounter.frontend.parks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.elements.Location;
import de.juliusawen.coastercreditcounter.backend.elements.Park;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.toolbox.ConvertTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class CreateParkActivity extends BaseActivity implements ConfirmDialogFragment.ConfirmDialogFragmentInteractionListener
{
    private CreateParkActivityViewModel viewModel;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "CreateParkActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_create_park);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            this.editText = findViewById(R.id.editTextCreatePark);

            this.viewModel = ViewModelProviders.of(this).get(CreateParkActivityViewModel.class);

            if(this.viewModel.parentLocation == null)
            {
                this.viewModel.parentLocation = (Location) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
            }

            super.addConfirmDialogFragment();

            super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.subtitle_park_create)), this.getText(R.string.help_text_create_park));

            super.addToolbar();
            super.addToolbarHomeButton();
            super.setToolbarTitleAndSubtitle(this.viewModel.parentLocation.getName(), getString(R.string.subtitle_park_create));

            this.createEditText();
            this.setKeyboardDetector();
        }
    }

    @Override
    public void onConfirmDialogFragmentInteraction(View view)
    {
        ButtonFunction buttonFunction = ButtonFunction.values()[view.getId()];
        Log.i(Constants.LOG_TAG, String.format("CreateParkActivity.onConfirmDialogFragment:: [%S] selected", buttonFunction));

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

        Log.d(Constants.LOG_TAG, String.format("CreateParkActivity.createLocation:: create %s success[%S]", this.viewModel.newPark, success));

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
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
        finish();
    }

    private void setKeyboardDetector()
    {
        final View activityRootView = findViewById(android.R.id.content);

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                int heightDifference = activityRootView.getRootView().getHeight() - activityRootView.getHeight();

                if(heightDifference > ConvertTool.convertDpToPx(150))
                {
                    CreateParkActivity.super.setConfirmDialogVisibilityWithoutFade(false);
                }
                else
                {
                    CreateParkActivity.super.setConfirmDialogVisibilityWithoutFade(true);
                }
            }
        });
    }
}
