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

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;

public class CreateSimpleElementActivity extends BaseActivity
{
    private CreateSimpleElementActivityViewModel viewModel;
    private TextInputLayout textInputLayout;
    private TextInputEditText textInputEditText;


    protected void setContentView()
    {
        setContentView(R.layout.activity_create_simple_element);
    }

    protected void create()
    {
        this.textInputLayout = findViewById(R.id.textInputLayout);
        this.textInputEditText = findViewById(R.id.textInputEditText);

        this.viewModel = new ViewModelProvider(this).get(CreateSimpleElementActivityViewModel.class);

        this.createTextInput(getIntent().getStringExtra(Constants.EXTRA_HINT));

        super.createHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_HELP_TITLE)), getIntent().getStringExtra(Constants.EXTRA_HELP_TEXT));
        super.createToolbar()
                .addToolbarHomeButton()
                .setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), null);

        super.createFloatingActionButton();
        this.decorateFloatingActionButton();
    }

    private void createTextInput(String hint)
    {
        this.textInputLayout.setHint(hint);
        this.textInputLayout.setError(null);
        this.textInputLayout.setCounterMaxLength(App.config.maxCharacterCount);

        this.textInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                Log.i(Constants.LOG_TAG, String.format("CreateSimpleElementActivity.onEditorAction:: actionId[%d]", actionId));

                boolean handled = false;

                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    handleOnEditorActionDone();
                    handled = true;
                }

                return handled;
            }
        });

        this.textInputEditText.requestFocus();
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
                    textInputLayout.setError(CreateSimpleElementActivity.this.getString(R.string.error_character_count_exceeded, textInputLayout.getCounterMaxLength()));
                }
                else
                {
                    textInputLayout.setError(null);
                }
            }
        });
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

    private void handleOnEditorActionDone()
    {
        this.viewModel.createdString = this.textInputEditText.getText().toString();
        if(!this.viewModel.createdString.trim().isEmpty())
        {
            if(this.viewModel.createdString.length() <= App.config.maxCharacterCount)
            {
                returnResult(RESULT_OK);
            }
        }
        else
        {
            this.textInputLayout.setError(getString(R.string.error_name_invalid));
        }
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("CreateSimpleElementActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            IElement createdElement = null;
            RequestCode requestCode = RequestCode.values()[getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, 0)];

            switch(requestCode)
            {
                case CREATE_CREDIT_TYPE:
                    Log.d(Constants.LOG_TAG, String.format("CreateSimpleElementActivity.returnResult:: creating CreditType [%s]", viewModel.createdString));
                    createdElement = CreditType.create(viewModel.createdString, null);
                    break;

                case CREATE_CATEGORY:
                    Log.d(Constants.LOG_TAG, String.format("CreateSimpleElementActivity.returnResult:: creating Category [%s]", viewModel.createdString));
                    createdElement = Category.create(viewModel.createdString, null);
                    break;

                case CREATE_MANUFACTURER:
                    Log.d(Constants.LOG_TAG, String.format("CreateSimpleElementActivity.returnResult:: creating Manufacturer [%s]", viewModel.createdString));
                    createdElement = Manufacturer.create(viewModel.createdString, null);
                    break;

                case CREATE_STATUS:
                    Log.d(Constants.LOG_TAG, String.format("CreateSimpleElementActivity.returnResult:: creating Status [%s]", viewModel.createdString));
                    createdElement = Status.create(viewModel.createdString, null);
                    break;
            }

            if(createdElement != null)
            {
                this.markForCreation(createdElement);
                intent.putExtra(Constants.EXTRA_ELEMENT_UUID, createdElement.getUuid().toString());
            }
            else
            {
                Toaster.makeShortToast(this, getString(R.string.error_creation_failed));
                Log.e(Constants.LOG_TAG, String.format("CreateSimpleElementActivity.returnResult<CREATE>:: not able to create [%s]", viewModel.createdString));
            }
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + this.getClass().getSimpleName());
        finish();
    }
}
