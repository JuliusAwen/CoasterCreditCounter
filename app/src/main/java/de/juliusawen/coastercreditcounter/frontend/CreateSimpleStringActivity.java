package de.juliusawen.coastercreditcounter.frontend;

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
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class CreateSimpleStringActivity extends BaseActivity
{
    private CreateSimpleStringActivityViewModel viewModel;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "EditElementActivity.CreateSimpleStringActivity:: creating activity...");

        setContentView(R.layout.activity_create_simple_string);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            this.editText = findViewById(R.id.editTextCreateSimpleString);
            this.editText.setOnEditorActionListener(this.getOnEditorActionListener());

            Intent intent = getIntent();
            String toolbarTitle = intent.getStringExtra(Constants.EXTRA_TOOLBAR_TITLE);
            String helpTitle = intent.getStringExtra(Constants.EXTRA_HELP_TITLE);
            String helpText = intent.getStringExtra(Constants.EXTRA_HELP_TEXT);
            String hint = intent.getStringExtra(Constants.EXTRA_HINT);

            this.editText.setHint(hint);

            this.viewModel = ViewModelProviders.of(this).get(CreateSimpleStringActivityViewModel.class);

            super.addFloatingActionButton();
            this.decorateFloatingActionButton();

            super.addHelpOverlayFragment(getString(R.string.title_help, helpTitle), helpText);

            super.addToolbar();
            super.addToolbarHomeButton();
            super.setToolbarTitleAndSubtitle(toolbarTitle, null);
        }
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableTool.getColoredDrawable(R.drawable.ic_baseline_check, R.color.white));
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
                Log.i(Constants.LOG_TAG, String.format("CreateSimpleStringActivity.onClickEditorAction:: actionId[%d]", actionId));

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
            Toaster.makeToast(this, getString(R.string.error_name_not_valid));
        }
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("CreateSimpleStringActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            Log.i(Constants.LOG_TAG, String.format("CreateSimpleStringActivity.returnResult:: returning string <%s>", this.viewModel.createdString));
            intent.putExtra(Constants.EXTRA_RESULT_STRING, this.viewModel.createdString);
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
        finish();
    }
}
