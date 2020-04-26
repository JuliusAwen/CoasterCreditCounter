package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
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
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.Note;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;

public class EditSimpleElementActivity extends BaseActivity
{
    private EditSimpleElementActivityViewModel viewModel;
    private TextInputLayout textInputLayout;
    private TextInputEditText textInputEditText;


    protected void setContentView()
    {
        setContentView(R.layout.activity_edit_simple_element);
    }

    protected void create()
    {
        this.textInputLayout = findViewById(R.id.textInputLayout);
        this.textInputEditText = findViewById(R.id.textInputEditText);

        this.viewModel = new ViewModelProvider(this).get(EditSimpleElementActivityViewModel.class);

        if(this.viewModel.requestCode == null)
        {
            this.viewModel.requestCode = RequestCode.values()[getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, 0)];
            this.viewModel.maxCharacterCount = this.viewModel.requestCode == RequestCode.EDIT_NOTE ? App.config.maxCharacterCountForNote : App.config.maxCharacterCountForSimpleElementName;
        }

        if(this.viewModel.elementToEdit == null)
        {
            this.viewModel.elementToEdit = App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        this.createTextInput(getIntent().getStringExtra(Constants.EXTRA_HINT));

        super.createHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE)), getString(R.string.help_text_edit_simple_element));
        super.createToolbar()
                .addToolbarHomeButton()
                .setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE));

        super.createFloatingActionButton();
        this.decorateFloatingActionButton();
    }

    private void createTextInput(String hint)
    {
        Log.d(Constants.LOG_TAG, String.format("EditSimpleElementActivity.createTextInput:: edit %s", this.viewModel.elementToEdit));

        this.textInputLayout.setHint(hint);
        this.textInputLayout.setError(null);
        this.textInputLayout.setCounterMaxLength(this.viewModel.maxCharacterCount);

        this.textInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                Log.i(Constants.LOG_TAG, String.format("EditSimpleElementActivity.onEditorAction:: actionId[%d]", actionId));

                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    if(!textInputEditText.getText().toString().trim().isEmpty())
                    {
                        handleOnEditorActionDone();
                    }
                    else
                    {
                        decorateTextInput();
                    }

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
                    textInputLayout.setError(EditSimpleElementActivity.this.getString(R.string.error_character_count_exceeded, textInputLayout.getCounterMaxLength()));
                }
                else
                {
                    textInputLayout.setError(null);
                }
            }
        });

        if(this.viewModel.requestCode == RequestCode.EDIT_NOTE)
        {
            this.textInputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            this.textInputEditText.setLines(App.config.minLinesForNote);
            this.textInputEditText.setMinLines(App.config.minLinesForNote);
            this.textInputEditText.setMaxLines(App.config.maxLinesForNote);
        }

        this.decorateTextInput();
    }

    private void decorateTextInput()
    {
        this.textInputEditText.append(this.viewModel.requestCode == RequestCode.EDIT_NOTE ? ((Note)this.viewModel.elementToEdit).getText() : this.viewModel.elementToEdit.getName());
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
            String input = this.textInputEditText.getText().toString().trim();

            if(this.viewModel.requestCode == RequestCode.EDIT_NOTE)
            {
                if(!((Note)this.viewModel.elementToEdit).getText().equals(input))
                {
                    if(((Note)this.viewModel.elementToEdit).setTextAndAdjustName(input))
                    {
                        Log.d(Constants.LOG_TAG, String.format("EditSimpleElementActivity.handleOnEditorActionDone:: text of %s changed to [%s]", this.viewModel.elementToEdit, input));
                        returnResult(RESULT_OK);
                    }
                    else
                    {
                        Log.w(Constants.LOG_TAG, String.format("EditSimpleElementActivity.handleOnEditorActionDone:: input [%s] is invalid", input));
                        this.textInputLayout.setError(getString(R.string.error_name_invalid));
                    }
                }
                else
                {
                    Log.d(Constants.LOG_TAG, "EditSimpleElementActivity.handleOnEditorActionDone:: text has not changed - cancel");
                    returnResult(RESULT_CANCELED);
                }
            }
            else
            {
                if(!this.viewModel.elementToEdit.getName().equals(input))
                {
                    if(this.viewModel.elementToEdit.setName(input))
                    {
                        Log.d(Constants.LOG_TAG, String.format("EditSimpleElementActivity.handleOnEditorActionDone:: name of %s changed to [%s]", this.viewModel.elementToEdit, input));
                        returnResult(RESULT_OK);
                    }
                    else
                    {
                        Log.w(Constants.LOG_TAG, String.format("EditSimpleElementActivity.handleOnEditorActionDone:: input [%s] is invalid", input));
                        this.textInputLayout.setError(getString(R.string.error_name_invalid));
                    }
                }
                else
                {
                    Log.v(Constants.LOG_TAG, "EditSimpleElementActivity.handleOnEditorActionDone:: name has not changed - cancel");
                    returnResult(RESULT_CANCELED);
                }
            }
        }
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("EditSimpleElementActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            super.markForUpdate(this.viewModel.elementToEdit);

            Log.i(Constants.LOG_TAG, String.format("EditSimpleElementActivity.returnResult:: returning edited %s", this.viewModel.elementToEdit));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.elementToEdit.getUuid().toString());
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + this.getClass().getSimpleName());
        finish();
    }
}
