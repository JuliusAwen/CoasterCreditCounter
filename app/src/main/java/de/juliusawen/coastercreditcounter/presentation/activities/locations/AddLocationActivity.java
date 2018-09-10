package de.juliusawen.coastercreditcounter.presentation.activities.locations;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.content.Park;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Content;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.activities.elements.PickElementsActivity;
import de.juliusawen.coastercreditcounter.presentation.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class AddLocationActivity extends BaseActivity implements ConfirmDialogFragment.ConfirmDialogFragmentInteractionListener
{
    private Location parentLocation;
    private Location newLocation;

    private EditText editText;
    private CheckBox checkBoxAddChildren;

    //region @OVERRIDE
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "AddLocationsActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_add_location);
        super.onCreate(savedInstanceState);

        this.initializeContent();

        super.addConfirmDialog();

        super.addHelpOverlay(getString(R.string.title_help, getString(R.string.subtitle_add_location)), this.getText(R.string.help_text_add_location));

        super.addToolbar();
        this.decorateToolbar();

        this.createEditText();
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
                    if(content.deleteElement(this.newLocation))
                    {
                        Log.d(Constants.LOG_TAG, String.format("AddLocationActivity.onConfirmDialogFragmentInteraction:: canceled -> removed %s", this.newLocation));
                    }
                    else
                    {
                        Log.d(Constants.LOG_TAG, String.format("AddLocationActivity.onConfirmDialogFragmentInteraction:: canceled -> not able to remove remove %s", this.newLocation));
                    }
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
        this.parentLocation = (Location) content.getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_ELEMENT)));
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("AddLocationsActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(requestCode == Constants.REQUEST_PICK_ELEMENTS)
        {
            if(resultCode == RESULT_OK)
            {
                List<String> uuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
                List<Element> pickedChildren = content.fetchElementsFromUuidStrings(uuidStrings);
                Log.v(Constants.LOG_TAG, String.format("AddLocationsActivity.onActivityResult<PickElements>:: #[%d] elements returned", pickedChildren.size()));

                if(pickedChildren.size() > 1)
                {
                    Log.v(Constants.LOG_TAG, "AddLocationsActivity.onActivityResult<PickElements>:: sorting list...");
                    pickedChildren = Content.sortElementListByCompareList(new ArrayList<>(pickedChildren), new ArrayList<>(parentLocation.getChildren()));
                }

                Log.d(Constants.LOG_TAG, String.format("AddLocationsActivity.onActivityResult<PickElements>:: inserting #[%d] elements...", pickedChildren.size()));
                this.parentLocation.insertElements(this.newLocation, new ArrayList<>(pickedChildren));

                if(this.parentLocation.hasChildrenOfInstance(Park.class))
                {
                    Log.v(Constants.LOG_TAG, String.format( "AddLocationsActivity.onActivityResult<PickElements>:: parent element %s has #[%d] children parks - asking to relocate...",
                            this.parentLocation, this.parentLocation.getChildCountOfInstance(Park.class)));

                    this.showAlertDialogRelocateChildrenParks();
                }
                else
                {
                    Log.v(Constants.LOG_TAG, String.format( "AddLocationsActivity.onActivityResult<PickElements>:: parent %s has no children<Park> - returning result[%d]",
                            this.parentLocation, resultCode));
                    this.returnResult(resultCode);
                }
            }
         }
    }
    //endregion

    private void initializeContent()
    {
        this.parentLocation = (Location) content.getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
    }

    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle(this.parentLocation.getName(), getString(R.string.subtitle_add_location));
    }

    //region EDIT TEXT
    private void createEditText()
    {
        View contentView = this.findViewById(android.R.id.content);

        if(this.parentLocation.hasChildrenOfInstance(Location.class))
        {
            Log.v(Constants.LOG_TAG, String.format("AddLocationsActivity.createEditText: parent %s has #[%d] children<Park> - offering add location option...",
                    this.parentLocation,
                    this.parentLocation.getChildCountOfInstance(Park.class)));

            LinearLayout linearLayoutAddChildren = contentView.findViewById(R.id.linearLayoutAddLocation_AddChildren);
            linearLayoutAddChildren.setVisibility(View.VISIBLE);

            TextView textViewAddChildren = linearLayoutAddChildren.findViewById(R.id.textViewAddLocation_AddChildren);
            textViewAddChildren.setText(R.string.add_children);

            this.checkBoxAddChildren = linearLayoutAddChildren.findViewById(R.id.checkBoxAddLocation_AddChildren);
        }

        this.editText = contentView.findViewById(R.id.editTextAddLocation);
        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                return onClickEditorAction(actionId);
            }
        });
    }

    private boolean onClickEditorAction(int actionId)
    {
        Log.i(Constants.LOG_TAG, String.format("AddLocationsActivity.onClickEditorAction:: actionId[%d]", actionId));

        boolean handled = false;

        switch (actionId)
        {
            case EditorInfo.IME_ACTION_DONE:
                handleOnEditorActionDone();
                handled = true;
                break;
        }

        return handled;
    }

    private void handleOnEditorActionDone()
    {
        Log.d(Constants.LOG_TAG, String.format(
                "AddLocationsActivity.handleOnEditorActionDone:: %s has #[%d] children<Location> and #[%d] children<Park>",
                this.parentLocation, this.parentLocation.getChildCountOfInstance(Location.class), this.parentLocation.getChildCountOfInstance(Park.class)));

        if(this.handleLocationCreation())
        {
            if(this.checkBoxAddChildren != null && this.checkBoxAddChildren.isChecked())
            {
                Log.d(Constants.LOG_TAG, String .format("AddLocationsActivity.handleOnEditorActionDone:: checkboxAddChildren.isChecked[%S] - parent %s has #[%d] children<Location>",
                        this.checkBoxAddChildren.isChecked(), this.parentLocation, this.parentLocation.getChildCountOfInstance(Location.class)));


                if (this.parentLocation.getChildCountOfInstance(Location.class) > 1)
                {
                    Log.i(Constants.LOG_TAG, String.format("AddLocationsActivity.handleOnEditorActionDone:: add children chosen - starting PickElementsActivity for %s...", this.parentLocation));
                    Intent intent = new Intent(getApplicationContext(), PickElementsActivity.class);
                    intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.parentLocation.getUuid().toString());
                    startActivityForResult(intent, Constants.REQUEST_PICK_ELEMENTS);
                }
                else
                {
                    Log.d(Constants.LOG_TAG, String.format("AddLocationsActivity.handleOnEditorActionDone:: parent %s has only one child<Location> -> inserting in new %s",
                            this.parentLocation, this.newLocation));
                    this.parentLocation.insertElements(this.newLocation, this.parentLocation.getChildrenOfInstance(Location.class));

                    if(this.parentLocation.hasChildrenOfInstance(Park.class))
                    {
                        Log.v(Constants.LOG_TAG, String.format( "AddLocationsActivity.handleOnEditorActionDone:: parent %s has #[%d] children<Park> - asking to relocate...",
                                this.parentLocation, this.parentLocation.getChildCountOfInstance(Park.class)));
                        this.showAlertDialogRelocateChildrenParks();
                    }
                    else
                    {
                        Log.v(Constants.LOG_TAG, String.format( "AddLocationsActivity.handleOnEditorActionDone:: parent %s has no children<Park> - returning RESULT_OK", this.parentLocation));
                        this.returnResult(RESULT_OK);
                    }
                }
            }
            else
            {
                Log.d(Constants.LOG_TAG, String.format("AddLocationsActivity.handleOnEditorActionDone:: parent %s has no children<Location> - adding child %s",
                        this.parentLocation, this.newLocation));
                this.parentLocation.addChild(this.newLocation);

                if(this.parentLocation.hasChildrenOfInstance(Park.class))
                {
                    Log.v(Constants.LOG_TAG, String.format("AddLocationsActivity.handleOnEditorActionDone:: parent %s has no children<Location> and #[%d] children<Park> - asking to relocate...",
                            this.parentLocation, this.parentLocation.getChildCountOfInstance(Park.class)));

                    this.showAlertDialogRelocateChildrenParks();
                }
                else
                {
                    Log.v(Constants.LOG_TAG, String.format( "AddLocationsActivity.handleOnEditorActionDone:: parent %s has no children<Park> - returning RESULT_OK", this.parentLocation));
                    this.returnResult(RESULT_OK);
                }
            }
        }
        else
        {
            Toaster.makeToast(this, getString(R.string.error_text_name_not_valid));
        }
    }

    private void showAlertDialogRelocateChildrenParks()
    {
        Log.v(Constants.LOG_TAG, "AddLocationsActivity.showAlertDialogRelocateChildrenParks:: creating alert dialog<relocate locations parks>");

        AlertDialog.Builder builder;
        AlertDialog alertDialog;

        builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.alert_dialog_relocate_locations_parks_title);
        builder.setMessage(getString(R.string.alert_dialog_relocate_locations_parks_message, this.parentLocation.getName(), this.newLocation.getName()));
        builder.setPositiveButton(R.string.text_accept, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                onClickAlertDialogPositiveButtonRelocateChildrenParks(dialog);
            }
        });

        builder.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                onClickAlertDialogNegativeButton(dialog);
            }
        });

        alertDialog = builder.create();
        alertDialog.setIcon(R.drawable.ic_baseline_notification_important);

        alertDialog.show();
    }

    private void onClickAlertDialogPositiveButtonRelocateChildrenParks(DialogInterface dialog)
    {
        Log.i(Constants.LOG_TAG, "AddLocationsActivity.onClickAlertDialogPositiveButtonRelocateChildrenParks:: accepted");
        dialog.dismiss();
        this.relocateChildrenParks();
        this.returnResult(RESULT_OK);

    }
    private void onClickAlertDialogNegativeButton(DialogInterface dialog)
    {
        Log.i(Constants.LOG_TAG, "AddLocationsActivity.onClickAlertDialogNegativeButton:: canceled");
        dialog.dismiss();
        this.returnResult(RESULT_OK);
    }

    private void relocateChildrenParks()
    {
        Log.d(Constants.LOG_TAG, String.format("AddLocationsActivity.relocateChildrenParks:: relocating children<Park> of parent %s to new %s...", this.parentLocation, this.newLocation));

        for(Element park : this.parentLocation.getChildrenOfInstance(Park.class))
        {
            park.relocateElement(this.newLocation);
        }
    }


    private boolean handleLocationCreation()
    {
        boolean success = false;
        this.newLocation = Location.createLocation(this.editText.getText().toString());

        if(this.newLocation != null)
        {
            content.addElement(this.newLocation);
            success = true;
        }

        Log.d(Constants.LOG_TAG, String.format("AddLocationsActivity.handleLocationCreation:: create %s success[%S]", this.editText.getText().toString(), success));

        return success;
    }
    //endregion

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("AddElementsActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            Log.i(Constants.LOG_TAG, String.format("AddElementsActivity.returnResult:: returning new %s", this.newLocation));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.newLocation.getUuid().toString());
        }

        setResult(resultCode, intent);
        finish();
    }
}
