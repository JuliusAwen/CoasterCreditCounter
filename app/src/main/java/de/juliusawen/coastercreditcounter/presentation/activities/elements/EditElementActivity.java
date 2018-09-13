package de.juliusawen.coastercreditcounter.presentation.activities.elements;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.Element;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class EditElementActivity extends BaseActivity implements ConfirmDialogFragment.ConfirmDialogFragmentInteractionListener
{
    private Element elementToEdit;
    private EditText editText;
    private String toolbarSubtitle;

    //region @OVERRIDE
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "EditElementActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_edit_location);
        super.onCreate(savedInstanceState);

        this.initializeContent();

        super.addConfirmDialog();

        super.addHelpOverlay(getString(R.string.title_help, this.toolbarSubtitle), getText(R.string.help_text_edit_location));

        super.addToolbar();
        this.decorateToolbar();

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

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.KEY_ELEMENT, this.elementToEdit.getUuid().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        this.elementToEdit = App.content.getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_ELEMENT)));
    }
    //endregion

    private void initializeContent()
    {
        this.elementToEdit = App.content.getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        this.toolbarSubtitle = getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE);
    }

    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle(this.elementToEdit.getName(), this.toolbarSubtitle);
    }

    //region EDIT TEXT
    private void createEditText()
    {
        this.editText = this.findViewById(android.R.id.content).findViewById(R.id.editTextEditLocation);

        Log.d(Constants.LOG_TAG, String.format("EditElementActivity.createEditText:: edit %s", this.elementToEdit));
        this.editText.append(this.elementToEdit.getName());

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
        if(!this.elementToEdit.getName().equals(editText))
        {
            if(!this.elementToEdit.setName(editText))
            {
                Toaster.makeToast(this, getString(R.string.error_text_name_not_valid));
            }
            Log.i(Constants.LOG_TAG, String.format("EditElementActivity.createEditText:: name of %s changed to [%s]", this.elementToEdit, editText));
        }
        else
        {
            Log.v(Constants.LOG_TAG, "EditElementActivity.createEditText:: name has not changed");
        }
    }
    //endregion
}
