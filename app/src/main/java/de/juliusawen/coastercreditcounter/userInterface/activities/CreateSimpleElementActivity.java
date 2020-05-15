package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.Note;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

public class CreateSimpleElementActivity extends BaseActivity
{
    private CreateSimpleElementViewModel viewModel;

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

        this.viewModel = new ViewModelProvider(this).get(CreateSimpleElementViewModel.class);

        if(this.viewModel.requestCode == null)
        {
            this.viewModel.requestCode = RequestCode.values()[getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, 0)];
            this.viewModel.maxCharacterCount = this.viewModel.requestCode == RequestCode.CREATE_NOTE ? App.config.maxCharacterCountForNote : App.config.maxCharacterCountForSimpleElementName;
        }

        this.createTextInput(getIntent().getStringExtra(Constants.EXTRA_HINT));

        super.createHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_HELP_TITLE)), getIntent().getStringExtra(Constants.EXTRA_HELP_TEXT));
        super.createToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE));

        super.createFloatingActionButton();
        this.decorateFloatingActionButton();
    }

    private void createTextInput(String hint)
    {
        this.textInputLayout.setHint(hint);
        this.textInputLayout.setError(null);
        this.textInputLayout.setCounterMaxLength(this.viewModel.maxCharacterCount);

        this.textInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                Log.i(String.format(Locale.getDefault(), "actionId[%d]", actionId));

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

        if(this.viewModel.requestCode == RequestCode.CREATE_NOTE)
        {
            this.textInputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            this.textInputEditText.setLines(App.config.minLinesForNote);
            this.textInputEditText.setMinLines(App.config.minLinesForNote);
            this.textInputEditText.setMaxLines(App.config.maxLinesForNote);
        }
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
        if(this.textInputLayout.getError() == null)
        {
            if(this.tryCreateElement(this.textInputEditText.getText().toString().trim()))
            {
                returnResult(RESULT_OK);
            }
            else
            {
                Log.i("input is invalid");
                this.textInputLayout.setError(getString(R.string.error_name_invalid));
            }
        }
    }

    private boolean tryCreateElement(String input)
    {
        IElement createdElement = null;

        switch(this.viewModel.requestCode)
        {
            case CREATE_CREDIT_TYPE:
                Log.d(String.format("creating CreditType [%s]", input));
                createdElement = CreditType.create(input);
                break;

            case CREATE_CATEGORY:
                Log.d(String.format("creating Category [%s]", input));
                createdElement = Category.create(input);
                break;

            case CREATE_MANUFACTURER:
                Log.d(String.format("creating Manufacturer [%s]", input));
                createdElement = Manufacturer.create(input);
                break;

            case CREATE_STATUS:
                Log.d(String.format("creating Status [%s]", input));
                createdElement = Status.create(input);
                break;

            case CREATE_NOTE:
                Log.d(String.format("creating Note with text [%s]", input));
                createdElement = Note.create(input);
                break;
        }

        if(createdElement != null)
        {
            this.viewModel.createdElement = createdElement;
            return true;
        }

        return false;
    }

    private void returnResult(int resultCode)
    {
        Log.i(String.format(Locale.getDefault(), "resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            super.markForCreation(this.viewModel.createdElement);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.createdElement.getUuid().toString());
        }

        setResult(resultCode, intent);
        Log.frame(LogLevel.INFO, String.format("finishing [%s]", this.getClass().getSimpleName()), '+', true);
        finish();
    }
}
