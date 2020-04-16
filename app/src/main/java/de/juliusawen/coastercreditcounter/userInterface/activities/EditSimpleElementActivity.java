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
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;

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

        if(this.viewModel.elementToEdit == null)
        {
            this.viewModel.elementToEdit = App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        this.createTextInput(getString(R.string.hint_edit_name, this.viewModel.elementToEdit.getName()));

        super.createHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE)), getString(R.string.help_text_edit_simple_element));
        super.createToolbar()
                .addToolbarHomeButton()
                .setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), null);

        super.createFloatingActionButton();
        this.decorateFloatingActionButton();
    }

    private void createTextInput(String hint)
    {
        Log.d(Constants.LOG_TAG, String.format("EditSimpleElementActivity.createTextInput:: edit %s", this.viewModel.elementToEdit));

        this.textInputLayout.setHint(hint);
        this.textInputLayout.setError(null);
        this.textInputLayout.setCounterMaxLength(App.config.maxCharacterCount);

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

        this.decorateTextInput();
    }

    private void decorateTextInput()
    {
        this.textInputEditText.append(this.viewModel.elementToEdit.getName());
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
        String name = this.textInputEditText.getText().toString();
        if(name.length() <= App.config.maxCharacterCount)
        {
            name = name.trim();
            if(!this.viewModel.elementToEdit.getName().equals(name))
            {
                Log.i(Constants.LOG_TAG, String.format("EditSimpleElementActivity.handleOnEditorActionDone:: name of %s changed to [%s]", this.viewModel.elementToEdit, name));
                if(!this.viewModel.elementToEdit.setName(name))
                {
                    this.textInputLayout.setError(getString(R.string.error_name_invalid));
                    Log.i(Constants.LOG_TAG, String.format("EditSimpleElementActivity.handleOnEditorActionDone:: name [%s] is invalid", name));
                }
            }
            else
            {
                Log.v(Constants.LOG_TAG, "EditSimpleElementActivity.handleOnEditorActionDone:: name has not changed");
                returnResult(RESULT_CANCELED);
            }
        }
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("EditSimpleElementActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            Log.i(Constants.LOG_TAG, String.format("EditSimpleElementActivity.returnResult:: returning edited %s", this.viewModel.elementToEdit));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.elementToEdit.getUuid().toString());

            super.markForUpdate(this.viewModel.elementToEdit);
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + this.getClass().getSimpleName());
        finish();
    }
}
