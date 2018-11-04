package de.juliusawen.coastercreditcounter.presentation.elements;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;

import androidx.lifecycle.ViewModelProviders;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.presentation.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class EditElementActivity extends BaseActivity implements ConfirmDialogFragment.ConfirmDialogFragmentInteractionListener
{
    private EditElementActivityViewModel viewModel;

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "EditElementActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_edit_location);
        super.onCreate(savedInstanceState);

        this.editText = this.findViewById(android.R.id.content).findViewById(R.id.editTextEditLocation);

        this.viewModel = ViewModelProviders.of(this).get(EditElementActivityViewModel.class);

        if(this.viewModel.elementToEdit == null)
        {
            this.viewModel.elementToEdit = App.content.getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }
        
        if(this.viewModel.toolbarSubtitle == null)
        {
            this.viewModel.toolbarSubtitle = getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE);
        }
        
        super.addConfirmDialogFragment();

        super.addHelpOverlayFragment(getString(R.string.title_help, this.viewModel.toolbarSubtitle), getText(R.string.help_text_edit_location));

        super.addToolbar();
        super.setToolbarTitleAndSubtitle(this.viewModel.elementToEdit.getName(), this.viewModel.toolbarSubtitle);

        this.createEditText();
    }

    @Override
    public void onConfirmDialogFragmentInteraction(View view)
    {
        ButtonFunction buttonFunction = ButtonFunction.values()[view.getId()];
        Log.i(Constants.LOG_TAG, String.format("EditElementActivity.onConfirmDialogIntercation:: [%S] selected", buttonFunction));

        switch (buttonFunction)
        {
            case OK:
                handleOnEditorActionDone();
                returnResult(RESULT_OK);
                break;

            case CANCEL:
                returnResult(RESULT_CANCELED);
                break;
        }
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
                boolean handled = onClickEditorAction(actionId);
                returnResult(RESULT_OK);
                return handled;
            }
        });
    }

    private boolean onClickEditorAction(int actionId)
    {
        boolean handled = false;

        if (actionId == EditorInfo.IME_ACTION_DONE)
        {
            Log.d(Constants.LOG_TAG, "EditElementActivity.onClickEditorAction<IME_ACTION_DONE>:: ");
            this.handleOnEditorActionDone();
            handled = true;
        }
        return handled;
    }

    private void handleOnEditorActionDone()
    {
        String editText = this.editText.getText().toString();
        if(!this.viewModel.elementToEdit.getName().equals(editText))
        {
            Log.i(Constants.LOG_TAG, String.format("EditElementActivity.createEditText:: name of %s changed to [%s]", this.viewModel.elementToEdit, editText));
            if(!this.viewModel.elementToEdit.setName(editText))
            {
                Toaster.makeToast(this, getString(R.string.error_text_name_not_valid));
            }
        }
        else
        {
            Log.v(Constants.LOG_TAG, "EditElementActivity.createEditText:: name has not changed");
        }
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("AddElementsActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            Log.i(Constants.LOG_TAG, String.format("AddElementsActivity.returnResult:: returning edited %s", this.viewModel.elementToEdit));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.elementToEdit.getUuid().toString());
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
        finish();
    }
}
