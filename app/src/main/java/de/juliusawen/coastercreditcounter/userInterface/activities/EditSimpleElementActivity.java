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
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.Note;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

public class EditSimpleElementActivity extends BaseActivity
{
    private EditSimpleElementViewModel viewModel;
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

        this.viewModel = new ViewModelProvider(this).get(EditSimpleElementViewModel.class);

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
        super.createToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE));

        super.createFloatingActionButton();
        this.decorateFloatingActionButton();
    }

    private void createTextInput(String hint)
    {
        Log.d(String.format("edit %s", this.viewModel.elementToEdit));

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
                String text = ((Note)this.viewModel.elementToEdit).getText();
                if(!text.equals(input))
                {
                    Log.d(String.format("%s's text [%s] changed to [%s]", this.viewModel.elementToEdit, text, input));

                    if(((Note)this.viewModel.elementToEdit).setTextAndAdjustName(input))
                    {
                        returnResult(RESULT_OK);
                    }
                    else
                    {
                        Log.w(String.format("input [%s] is invalid - text not changed", input));
                        this.textInputLayout.setError(getString(R.string.error_name_invalid));
                    }
                }
                else
                {
                    Log.d("text has not changed - cancel");
                    returnResult(RESULT_CANCELED);
                }
            }
            else
            {
                if(!this.viewModel.elementToEdit.getName().equals(input))
                {
                    Log.d(String.format("trying to change name of %s to [%s]", this.viewModel.elementToEdit, input));

                    if(this.viewModel.elementToEdit.setName(input))
                    {
                        returnResult(RESULT_OK);
                    }
                    else
                    {
                        Log.w(String.format("input [%s] is invalid - name not changed", input));
                        this.textInputLayout.setError(getString(R.string.error_name_invalid));
                    }
                }
                else
                {
                    Log.v("name has not changed - cancel");
                    returnResult(RESULT_CANCELED);
                }
            }
        }
    }

    private void returnResult(int resultCode)
    {
        Log.i(String.format("resultCode[%s]", StringTool.resultCodeToString(resultCode)));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            super.markForUpdate(this.viewModel.elementToEdit);

            Log.i(String.format("returning edited %s", this.viewModel.elementToEdit));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.elementToEdit.getUuid().toString());
        }

        setResult(resultCode, intent);
        Log.frame(LogLevel.INFO, String.format("finishing [%s]", this.getClass().getSimpleName()), '+', false);
        finish();
    }
}
