package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;

public class CreateLocationActivity extends BaseActivity
{
    private CreateLocationActivityViewModel viewModel;

    private TextInputLayout textInputLayout;
    private TextInputEditText textInputEditText;

    protected void setContentView()
    {
        setContentView(R.layout.activity_create_location);
    }

    protected void create()
    {
        this.textInputLayout = findViewById(R.id.textInputLayout);
        this.textInputEditText = findViewById(R.id.textInputEditText);

        this.viewModel = new ViewModelProvider(this).get(CreateLocationActivityViewModel.class);

        if(this.viewModel.parentLocation == null)
        {
            this.viewModel.parentLocation = (Location) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        this.createTextInput();

        super.createHelpOverlayFragment(getString(R.string.title_help, getString(R.string.subtitle_create_location)), this.getString(R.string.help_text_create_location));
        super.createToolbar()
                .addToolbarHomeButton()
                .setToolbarTitleAndSubtitle(this.viewModel.parentLocation.getName(), getString(R.string.subtitle_create_location));
        super.createFloatingActionButton();

        this.decorateFloatingActionButton();
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

    private void createTextInput()
    {
        this.textInputLayout.setHint(getString(R.string.hint_enter_location_name));
        this.textInputLayout.setError(null);
        this.textInputLayout.setCounterMaxLength(App.config.maxCharacterCount);

        this.textInputEditText.requestFocus();
        this.textInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
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

        this.textInputEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (editable.length() > textInputLayout.getCounterMaxLength())
                {
                    textInputLayout.setError(CreateLocationActivity.this.getString(R.string.error_character_count_exceeded, textInputLayout.getCounterMaxLength()));
                }
                else
                {
                    textInputLayout.setError(null);
                }
            }
        });
    }

    private void handleOnEditorActionDone()
    {
        String name = this.textInputEditText.getText().toString();
        if(name.length() <= App.config.maxCharacterCount)
        {
            if(this.tryCreateLocation(name.trim()))
            {
                Log.d(Constants.LOG_TAG, String.format("CreateLocationsActivity.handleOnEditorActionDone:: adding child %s to parent %s",
                        this.viewModel.newLocation, this.viewModel.parentLocation));

                this.viewModel.parentLocation.addChildAndSetParent(this.viewModel.newLocation);

                this.returnResult(RESULT_OK);
            }
            else
            {
                Log.d(Constants.LOG_TAG, "CreateLocationsActivity.handleOnEditorActionDone:: name is invalid");
                this.textInputLayout.setError(getString(R.string.error_name_invalid));
            }
        }
    }

    private boolean tryCreateLocation(String name)
    {
        boolean success = false;

        Location location = Location.create(name.trim());

        if(location != null)
        {
            this.viewModel.newLocation = location;
            success = true;
        }

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
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + this.getClass().getSimpleName());
        finish();
    }
}
