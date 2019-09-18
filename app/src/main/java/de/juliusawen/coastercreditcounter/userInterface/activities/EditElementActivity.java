package de.juliusawen.coastercreditcounter.userInterface.activities;

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
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.Toaster;

public class EditElementActivity extends BaseActivity
{
    private EditElementActivityViewModel viewModel;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_edit_element);
        super.onCreate(savedInstanceState);
    }

    protected void create()
    {
        this.editText = findViewById(R.id.editTextEditElement);

        this.viewModel = ViewModelProviders.of(this).get(EditElementActivityViewModel.class);

        if(this.viewModel.elementToEdit == null)
        {
            this.viewModel.elementToEdit = App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        super.addHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE)), getText(R.string.help_text_edit));
        super.addToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), this.viewModel.elementToEdit.getName());
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
                if(handleOnEditorActionDone())
                {
                    returnResult(RESULT_OK);
                }
            }
        });
        super.setFloatingActionButtonVisibility(true);
    }

    private void createEditText()
    {
        Log.d(Constants.LOG_TAG, String.format("EditElementActivity.createEditText:: edit %s", this.viewModel.elementToEdit));
        this.editText.append(this.viewModel.elementToEdit.getName());

        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    Log.d(Constants.LOG_TAG, "EditElementActivity.onEditorAction<IME_ACTION_DONE>:: ");
                    if(handleOnEditorActionDone())
                    {
                        returnResult(RESULT_OK);
                    }
                }

                return true;
            }
        });
    }

    private boolean handleOnEditorActionDone()
    {
        String editText = this.editText.getText().toString();
        if(!this.viewModel.elementToEdit.getName().equals(editText))
        {
            Log.i(Constants.LOG_TAG, String.format("EditElementActivity.handleOnEditorActionDone:: name of %s changed to [%s]", this.viewModel.elementToEdit, editText));
            if(!this.viewModel.elementToEdit.setName(editText))
            {
                Toaster.makeToast(this, getString(R.string.error_name_not_valid));
                return false;
            }
        }
        else
        {
            Log.v(Constants.LOG_TAG, "EditElementActivity.createEditText:: name has not changed");
        }

        return true;
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("AddElementsActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            Log.i(Constants.LOG_TAG, String.format("AddElementsActivity.returnResult:: returning edited %s", this.viewModel.elementToEdit));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.elementToEdit.getUuid().toString());

            super.markForUpdate(this.viewModel.elementToEdit);
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
        finish();
    }
}
