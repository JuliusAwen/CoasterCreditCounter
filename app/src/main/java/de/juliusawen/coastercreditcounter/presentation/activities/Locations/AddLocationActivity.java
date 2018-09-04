package de.juliusawen.coastercreditcounter.presentation.activities.Locations;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;
import de.juliusawen.coastercreditcounter.toolbox.enums.ButtonFunction;

public class AddLocationActivity extends BaseActivity implements ConfirmDialogFragment.ConfirmDialogFragmentInteractionListener
{
    private Location parentLocation;
    private Location newLocation;

    private EditText editText;
    private CheckBox checkBoxAddChildren;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(Constants.LOG_TAG, Constants.LOG_DIVIDER);
        Log.i(Constants.LOG_TAG, "AddLocationsActivity.onCreate:: creating activity...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        this.initializeContent();
        this.initializeViews();
    }

    @Override
    public void onConfirmDialogFragmentInteraction(View view)
    {
        ButtonFunction buttonFunction = ButtonFunction.values()[view.getId()];
        Log.i(Constants.LOG_TAG, String.format("AddLocationsActivity.onConfirmDialogFragment:: [%S] selected", buttonFunction));

        switch (buttonFunction)
        {
            case OK:
                handleOnEditorActionDone();
                break;

            case CANCEL:
                Intent intent = new Intent();

                if(this.newLocation != null)
                {
                    Log.i(Constants.LOG_TAG, String.format("AddLocationActivity.onConfirmDialogFragmentInteraction:: canceled -> removing %s", this.newLocation));
                    Content.getInstance().deleteElementAndChildren(this.newLocation);
                }

                setResult(RESULT_CANCELED, intent);
                finish();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.KEY_ELEMENT, this.parentLocation.getUuid().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        this.parentLocation = (Location) Content.getInstance().getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_ELEMENT)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("AddLocationsActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(requestCode == Constants.REQUEST_PICK_ELEMENTS)
        {
            if(resultCode == RESULT_OK)
            {
                List<String> uuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
                List<Element> pickedChildren = Content.getInstance().fetchElementsFromUuidStrings(uuidStrings);
                pickedChildren = Content.sortElementListByCompareList(new ArrayList<>(pickedChildren), new ArrayList<>(parentLocation.getChildren()));

                this.parentLocation.insertElement(this.newLocation, new ArrayList<>(pickedChildren));
                this.returnResult(resultCode);
            }
         }
    }

    private void initializeContent()
    {
        this.parentLocation = (Location) Content.getInstance().getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
    }

    private void initializeViews()
    {
        FrameLayout frameLayoutActivity = findViewById(R.id.frameLayoutAddLocation);
        View addLocationView = getLayoutInflater().inflate(R.layout.layout_add_location, frameLayoutActivity, false);
        frameLayoutActivity.addView(addLocationView);

        super.createToolbar(addLocationView, getString(R.string.title_add_location), getString(R.string.subtitle_add_location_add_to, this.parentLocation.getName()), false);
        super.createConfirmDialogFragment(frameLayoutActivity);
        this.createEditText(addLocationView);
        super.createHelpOverlayFragment(frameLayoutActivity, getText(R.string.help_text_add_location), false);
    }

    private void createEditText(View view)
    {
        if(this.parentLocation.hasChildren())
        {
            LinearLayout linearLayoutAddChildren = view.findViewById(R.id.linearLayoutAddLocation_AddChildren);
            linearLayoutAddChildren.setVisibility(View.VISIBLE);

            TextView textViewAddChildren = linearLayoutAddChildren.findViewById(R.id.textViewAddLocation_AddChildren);
            textViewAddChildren.setText(R.string.add_children);

            this.checkBoxAddChildren = linearLayoutAddChildren.findViewById(R.id.checkBoxAddLocation_AddChildren);
        }

        this.editText = view.findViewById(R.id.editTextAddLocation);
        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                boolean handled = false;

                switch (actionId)
                {
                    case EditorInfo.IME_ACTION_DONE:
                        Log.i(Constants.LOG_TAG, "AddLocationsActivity.createEditText.onEditorAction:: IME_ACTION_DONE");
                        handleOnEditorActionDone();
                        handled = true;
                        break;
                }

                return handled;
            }
        });
    }

    private void handleOnEditorActionDone()
    {
        if(this.handleLocationCreation())
        {
            if(this.checkBoxAddChildren != null && this.checkBoxAddChildren.isChecked())
            {
                Log.i(Constants.LOG_TAG, String .format("AddLocationsActivity.handleOnEditorActionDone:: checkboxAddChildren[%S]", this.checkBoxAddChildren.isChecked()));

                if (this.parentLocation.getChildCount() > 1)
                {
                    Log.i(Constants.LOG_TAG, "AddLocationsActivity.handleOnEditorActionDone:: starting PickLocationsActivity...");
                    Intent intent = new Intent(getApplicationContext(), PickElementsActivity.class);
                    intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.parentLocation.getUuid().toString());
                    startActivityForResult(intent, Constants.REQUEST_PICK_ELEMENTS);
                }
                else
                {
                    Log.i(Constants.LOG_TAG, String.format("AddLocationsActivity.handleOnEditorActionDone:: %s has only one child -> inserting in %s", this.parentLocation, this.newLocation));
                    this.parentLocation.insertElement(this.newLocation, this.parentLocation.getChildren());
                    returnResult(RESULT_OK);
                }
            }
            else
            {
                this.parentLocation.addChild(this.newLocation);
                this.returnResult(RESULT_OK);
            }
        }
        else
        {
            Toaster.makeToast(this, getString(R.string.error_text_name_not_valid));
        }
    }

    private boolean handleLocationCreation()
    {
        boolean success = false;
        this.newLocation = Location.createLocation(this.editText.getText().toString());

        if(this.newLocation != null)
        {
            Content.getInstance().addElement(this.newLocation);
            success = true;
        }

        Log.i(Constants.LOG_TAG, String.format("AddLocationsActivity.handleLocationCreation:: create location[%s] success[%S]",this.editText.getText().toString(), success));

        return success;
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("AddElementsActivity.returnResult:: result code [%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.newLocation.getUuid().toString());
        }

        setResult(resultCode, intent);
        finish();
    }
}
