package de.juliusawen.coastercreditcounter.presentation.activities.elements;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class EditElementActivity extends BaseActivity implements ConfirmDialogFragment.ConfirmDialogFragmentInteractionListener
{
    private EditElementViewModel viewModel;

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "EditElementActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_edit_location);
        super.onCreate(savedInstanceState);

        this.viewModel = ViewModelProviders.of(this).get(EditElementViewModel.class);

        if(this.viewModel.elementToEdit == null)
        {
            this.viewModel.elementToEdit = App.content.getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }
        
        if(this.viewModel.toolbarSubtitle == null)
        {
            this.viewModel.toolbarSubtitle = getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE);
        }
        
        super.addConfirmDialog();

        super.addHelpOverlay(getString(R.string.title_help, this.viewModel.toolbarSubtitle), getText(R.string.help_text_edit_location));

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
                finish();
                break;

            case CANCEL:
                finish();
                break;
        }
    }

    private void createEditText()
    {
        this.editText = this.findViewById(android.R.id.content).findViewById(R.id.editTextEditLocation);

        Log.d(Constants.LOG_TAG, String.format("EditElementActivity.createEditText:: edit %s", this.viewModel.elementToEdit));
        this.editText.append(this.viewModel.elementToEdit.getName());

        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                boolean handled = onClickEditorAction(actionId);
                finish();
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
            if(!this.viewModel.elementToEdit.setName(editText))
            {
                Toaster.makeToast(this, getString(R.string.error_text_name_not_valid));
            }
            Log.i(Constants.LOG_TAG, String.format("EditElementActivity.createEditText:: name of %s changed to [%s]", this.viewModel.elementToEdit, editText));
        }
        else
        {
            Log.v(Constants.LOG_TAG, "EditElementActivity.createEditText:: name has not changed");
        }
    }
}
