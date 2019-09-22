package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;

public class CreateSimpleElementActivity extends BaseActivity
{
    private CreateSimpleElementActivityViewModel viewModel;
    private EditText editText;


    protected void setContentView()
    {
        setContentView(R.layout.activity_create_simple_element);
    }

    protected void create()
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
                Toaster.makeToast(this, getString(R.string.error_creation_failed));
                Log.e(Constants.LOG_TAG, String.format("CreateSimpleElementActivity.returnResult<CREATE>:: not able to create [%s]", viewModel.createdString));
            }
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + this.getClass().getSimpleName());
        finish();
    }
}
