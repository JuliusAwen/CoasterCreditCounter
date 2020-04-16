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
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;

public class CreateParkActivity extends BaseActivity
{
    private CreateParkActivityViewModel viewModel;

    private TextInputLayout textInputLayout;
    private TextInputEditText textInputEditText;

    protected void setContentView()
    {
        setContentView(R.layout.activity_create_park);
    }

    protected void create()
    {
        this.textInputLayout = findViewById(R.id.textInputLayout);
        this.textInputEditText = findViewById(R.id.textInputEditText);

        this.viewModel = new ViewModelProvider(this).get(CreateParkActivityViewModel.class);

        if(this.viewModel.parentLocation == null)
        {
            this.viewModel.parentLocation = (Location) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        super.createHelpOverlayFragment(getString(R.string.title_help, getString(R.string.subtitle_create_park)), this.getText(R.string.help_text_create_park));
        super.createToolbar()
                .addToolbarHomeButton()
                .setToolbarTitleAndSubtitle(this.viewModel.parentLocation.getName(), getString(R.string.subtitle_create_park));
        super.createFloatingActionButton();

        this.decorateFloatingActionButton();
        this.createTextInput();
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
        this.textInputLayout.setHint(getString(R.string.hint_enter_park_name));
        this.textInputLayout.setError(null);
        this.textInputLayout.setCounterMaxLength(App.config.maxCharacterCount);

        this.textInputEditText.requestFocus();
        this.textInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
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
                    textInputLayout.setError(CreateParkActivity.this.getString(R.string.error_character_count_exceeded, textInputLayout.getCounterMaxLength()));
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
            if(this.tryCreatePark(name.trim()))
            {
                Log.d(Constants.LOG_TAG, String.format("CreateParkActivity.handleOnEditorActionDone:: adding child %s to parent %s",
                        this.viewModel.newPark, this.viewModel.parentLocation));

                this.viewModel.parentLocation.addChildAndSetParent(this.viewModel.newPark);

                Log.v(Constants.LOG_TAG, String.format( "CreateParkActivity.handleOnEditorActionDone:: parent %s has no children<Park> - returning RESULT_OK", this.viewModel.parentLocation));
                this.returnResult(RESULT_OK);
            }
            else
            {
                Log.d(Constants.LOG_TAG, "CreateParkActivity.handleOnEditorActionDone:: name is invalid");
                this.textInputLayout.setError(getString(R.string.error_name_invalid));
            }
        }
    }

    private boolean tryCreatePark(String name)
    {
        boolean success = false;

        Park park = Park.create(name);

        if(park != null)
        {
            this.viewModel.newPark = park;
            success = true;
        }

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
