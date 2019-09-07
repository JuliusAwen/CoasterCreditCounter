package de.juliusawen.coastercreditcounter.frontend.elements;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Status;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.DrawableProvider;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class CreateSimpleElementActivity extends BaseActivity
{
    private CreateSimpleElementActivityViewModel viewModel;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "EditElementActivity.CreateSimpleElementActivity:: creating activity...");

        setContentView(R.layout.activity_create_simple_element);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            this.editText = findViewById(R.id.editTextCreateSimpleElement);
            this.editText.setOnEditorActionListener(this.getOnEditorActionListener());

            Intent intent = getIntent();
            String toolbarTitle = intent.getStringExtra(Constants.EXTRA_TOOLBAR_TITLE);
            String helpTitle = intent.getStringExtra(Constants.EXTRA_HELP_TITLE);
            String helpText = intent.getStringExtra(Constants.EXTRA_HELP_TEXT);
            String hint = intent.getStringExtra(Constants.EXTRA_HINT);

            this.editText.setHint(hint);

            this.viewModel = ViewModelProviders.of(this).get(CreateSimpleElementActivityViewModel.class);

            super.addHelpOverlayFragment(getString(R.string.title_help, helpTitle), helpText);
            super.addToolbar();
            super.addToolbarHomeButton();
            super.setToolbarTitleAndSubtitle(toolbarTitle, null);
            super.addFloatingActionButton();

            this.decorateFloatingActionButton();
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

    private TextView.OnEditorActionListener getOnEditorActionListener()
    {
        return new TextView.OnEditorActionListener()
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
        };
    }

    private void handleOnEditorActionDone()
    {
        this.viewModel.createdString = this.editText.getText().toString();
        if(!this.viewModel.createdString.trim().isEmpty())
        {
            returnResult(RESULT_OK);
        }
        else
        {
            returnResult(RESULT_CANCELED);
        }
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("CreateSimpleElementActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            IElement createdElement = null;
            int requestCode = getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, -1);

            if(requestCode == Constants.REQUEST_CODE_CREATE_ATTRACTION_CATEGORY)
            {
                Log.d(Constants.LOG_TAG, String.format("CreateSimpleElementActivity.returnResult<CREATE_ATTRACTION_CATEGORY>:: creating AttractionCategory [%s]", viewModel.createdString));
                createdElement = AttractionCategory.create(viewModel.createdString, null);
            }
            else if(requestCode == Constants.REQUEST_CODE_CREATE_MANUFACTURER)
            {
                Log.d(Constants.LOG_TAG, String.format("CreateSimpleElementActivity.returnResult<CREATE_MANUFACTURER>:: creating Manufacturer [%s]", viewModel.createdString));
                createdElement = Manufacturer.create(viewModel.createdString, null);
            }
            else if(requestCode == Constants.REQUEST_CODE_CREATE_STATUS)
            {
                Log.d(Constants.LOG_TAG, String.format("CreateSimpleElementActivity.returnResult<CREATE_STATUS>:: creating Status [%s]", viewModel.createdString));

                createdElement = Status.create(viewModel.createdString, null);
            }

            if(createdElement != null)
            {
                this.markForCreation(createdElement);
                intent.putExtra(Constants.EXTRA_ELEMENT_UUID, createdElement.getUuid().toString());
            }
            else
            {
                Toaster.makeToast(this, getString(R.string.error_creation_failed));
                Log.e(Constants.LOG_TAG, String.format("CreateSimpleElementActivity.returnResult<CREATE>:: not able to create [%s]", viewModel.createdString));
            }
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
        finish();
    }
}
