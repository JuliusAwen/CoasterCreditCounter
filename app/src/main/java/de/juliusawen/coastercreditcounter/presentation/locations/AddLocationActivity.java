package de.juliusawen.coastercreditcounter.presentation.locations;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.lifecycle.ViewModelProviders;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Location;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.presentation.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.fragments.AlertDialogFragment;
import de.juliusawen.coastercreditcounter.presentation.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class AddLocationActivity extends BaseActivity implements ConfirmDialogFragment.ConfirmDialogFragmentInteractionListener, AlertDialogFragment.AlertDialogListener
{
    AddLocationActivityViewModel viewModel;

    private LinearLayout linearLayoutAddChildren;
    private TextView textViewAddChildren;
    private EditText editText;
    private CheckBox checkBoxAddChildren;

    private static final int ALERT_DIALOG_RELOCATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "AddLocationsActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_add_location);
        super.onCreate(savedInstanceState);

        this.linearLayoutAddChildren = findViewById(R.id.linearLayoutAddLocation_AddChildren);
        this.textViewAddChildren = this.linearLayoutAddChildren.findViewById(R.id.textViewAddLocation_AddChildren);
        this.editText = findViewById(R.id.editTextAddLocation);
        this.checkBoxAddChildren = this.linearLayoutAddChildren.findViewById(R.id.checkBoxAddLocation_AddChildren);

        this.viewModel = ViewModelProviders.of(this).get(AddLocationActivityViewModel.class);

        if(this.viewModel.parentLocation == null)
        {
            this.viewModel.parentLocation = (Location) App.content.getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        super.addConfirmDialogFragment();

        super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.subtitle_location_add)), this.getText(R.string.help_text_add_location));

        super.addToolbar();
        super.setToolbarTitleAndSubtitle(this.viewModel.parentLocation.getName(), getString(R.string.subtitle_location_add));

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

                if(this.viewModel.newLocation != null)
                {
                    if(App.content.deleteElement(this.viewModel.newLocation))
                    {
                        Log.d(Constants.LOG_TAG, String.format("AddLocationActivity.onConfirmDialogFragmentInteraction:: canceled -> removed %s", this.viewModel.newLocation));
                    }
                    else
                    {
                        Log.d(Constants.LOG_TAG, String.format("AddLocationActivity.onConfirmDialogFragmentInteraction:: canceled -> not able to remove remove %s", this.viewModel.newLocation));
                    }
                }

                setResult(RESULT_CANCELED, intent);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("AddLocationsActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == RESULT_OK)
        {
            List<String> uuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
            List<Element> pickedElements = App.content.fetchElementsByUuidStrings(uuidStrings);
            Log.v(Constants.LOG_TAG, String.format("AddLocationsActivity.onActivityResult<OK>:: #[%d] elements returned", pickedElements.size()));

            if(pickedElements.size() > 1)
            {
                Log.v(Constants.LOG_TAG, "AddLocationsActivity.onActivityResult<OK>:: sorting list...");
                pickedElements = Element.sortElementsBasedOnComparisonList(new ArrayList<>(pickedElements), new ArrayList<>(viewModel.parentLocation.getChildren()));
            }

            if(requestCode == Constants.REQUEST_PICK_LOCATIONS)
            {
                Log.d(Constants.LOG_TAG, String.format("AddLocationsActivity.onActivityResult<PickLocations>:: inserting #[%d] elements...", pickedElements.size()));
                this.viewModel.parentLocation.insertElements(this.viewModel.newLocation, new ArrayList<>(pickedElements));

                if(this.viewModel.parentLocation.hasChildrenOfInstance(Park.class))
                {
                    Log.v(Constants.LOG_TAG, String.format( "AddLocationsActivity.onActivityResult<PickLocations>:: parent element %s has #[%d] children parks - asking to relocate...",
                            this.viewModel.parentLocation, this.viewModel.parentLocation.getChildCountOfType(Park.class)));

                    this.showAlertDialogRelocateChildrenParks();
                }
                else
                {
                    Log.v(Constants.LOG_TAG, String.format( "AddLocationsActivity.onActivityResult<PickLocations>:: parent %s has no children<Park> - returning result[%d]",
                            this.viewModel.parentLocation, resultCode));
                    this.returnResult(resultCode);
                }
            }
            else if(requestCode == Constants.REQUEST_PICK_PARKS)
            {
                Log.d(Constants.LOG_TAG, String.format("AddLocationsActivity.onActivityResult<PickParks>:: relocating #[%d] elements...", pickedElements.size()));
                this.relocateChildrenParks(pickedElements);
                this.returnResult(resultCode);
            }
         }
    }

    private void createEditText()
    {
        if(this.viewModel.parentLocation.hasChildrenOfInstance(Location.class))
        {
            Log.v(Constants.LOG_TAG, String.format("AddLocationsActivity.createEditText: parent %s has #[%d] children<Park> - offering add location option...",
                    this.viewModel.parentLocation,
                    this.viewModel.parentLocation.getChildCountOfType(Park.class)));

            this.linearLayoutAddChildren.setVisibility(View.VISIBLE);
            this.textViewAddChildren.setText(R.string.add_children);
        }

        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
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
        });
    }

    private void handleOnEditorActionDone()
    {
        Log.d(Constants.LOG_TAG, String.format(
                "AddLocationsActivity.handleOnEditorActionDone:: %s has #[%d] children<Location> and #[%d] children<Park>",
                this.viewModel.parentLocation, this.viewModel.parentLocation.getChildCountOfType(Location.class), this.viewModel.parentLocation.getChildCountOfType(Park.class)));

        if(this.createLocation())
        {
            if(this.checkBoxAddChildren != null && this.checkBoxAddChildren.isChecked())
            {
                Log.d(Constants.LOG_TAG, String .format("AddLocationsActivity.handleOnEditorActionDone:: checkboxAddChildren.isChecked[%S] - parent %s has #[%d] children<Location>",
                        this.checkBoxAddChildren.isChecked(), this.viewModel.parentLocation, this.viewModel.parentLocation.getChildCountOfType(Location.class)));


                if (this.viewModel.parentLocation.getChildCountOfType(Location.class) > 1)
                {
                    Log.i(Constants.LOG_TAG, String.format(
                            "AddLocationsActivity.handleOnEditorActionDone:: add children chosen - starting PickElementsActivity for %s...", this.viewModel.parentLocation));

                    ActivityTool.startActivityPickForResult(this, Constants.REQUEST_PICK_LOCATIONS, this.viewModel.parentLocation.getChildrenOfType(Location.class));
                }
                else
                {
                    Log.d(Constants.LOG_TAG, String.format("AddLocationsActivity.handleOnEditorActionDone:: parent %s has only one child<Location> -> inserting in new %s",
                            this.viewModel.parentLocation, this.viewModel.newLocation));
                    this.viewModel.parentLocation.insertElements(this.viewModel.newLocation, this.viewModel.parentLocation.getChildrenOfType(Location.class));

                    if(this.viewModel.parentLocation.hasChildrenOfInstance(Park.class))
                    {
                        Log.v(Constants.LOG_TAG, String.format("AddLocationsActivity.handleOnEditorActionDone:: parent %s has #[%d] children<Park> - asking to relocate...",
                                this.viewModel.parentLocation, this.viewModel.parentLocation.getChildCountOfType(Park.class)));
                        this.showAlertDialogRelocateChildrenParks();
                    }
                    else
                    {
                        Log.v(Constants.LOG_TAG, String.format(
                                "AddLocationsActivity.handleOnEditorActionDone:: parent %s has no children<Park> - returning RESULT_OK", this.viewModel.parentLocation));

                        this.returnResult(RESULT_OK);
                    }
                }
            }
            else
            {
                Log.d(Constants.LOG_TAG, String.format("AddLocationsActivity.handleOnEditorActionDone:: parent %s has no children<Location> - adding child %s",
                        this.viewModel.parentLocation, this.viewModel.newLocation));
                this.viewModel.parentLocation.addChild(this.viewModel.newLocation);

                if(this.viewModel.parentLocation.hasChildrenOfInstance(Park.class))
                {
                    Log.v(Constants.LOG_TAG, String.format("AddLocationsActivity.handleOnEditorActionDone:: parent %s has no children<Location> and #[%d] children<Park> - asking to relocate...",
                            this.viewModel.parentLocation, this.viewModel.parentLocation.getChildCountOfType(Park.class)));

                    this.showAlertDialogRelocateChildrenParks();
                }
                else
                {
                    Log.v(Constants.LOG_TAG, String.format( "AddLocationsActivity.handleOnEditorActionDone:: parent %s has no children<Park> - returning RESULT_OK", this.viewModel.parentLocation));
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

        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(
                R.drawable.ic_baseline_notification_important,
                getString(R.string.alert_dialog_relocate_children_parks_title),
                getString(R.string.alert_dialog_relocate_children_parks_message, this.viewModel.parentLocation.getName(), viewModel.newLocation.getName()),
                getString(R.string.text_accept),
                getString(R.string.text_cancel),
                this.ALERT_DIALOG_RELOCATE
        );
        alertDialogFragment.setCancelable(false);
        alertDialogFragment.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
    }

    @Override
    public void onAlertDialogClick(int requestCode, DialogInterface dialog, int which)
    {
        dialog.dismiss();

        switch(requestCode)
        {
            case ALERT_DIALOG_RELOCATE:
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    Log.i(Constants.LOG_TAG, "AddLocationsActivity.onAlertDialogClick:: accepted");
                    if(viewModel.parentLocation.getChildCountOfType(Park.class) > 1)
                    {
                        ActivityTool.startActivityPickForResult(AddLocationActivity.this, Constants.REQUEST_PICK_PARKS, viewModel.parentLocation.getChildrenOfType(Park.class));
                    }
                    else
                    {
                        relocateChildrenParks(viewModel.parentLocation.getChildrenOfType(Park.class));
                        returnResult(RESULT_OK);
                    }
                }
                else if(which == DialogInterface.BUTTON_NEGATIVE)
                {
                    Log.i(Constants.LOG_TAG, "AddLocationsActivity.onClickAlertDialogNegativeButton:: canceled");
                    returnResult(RESULT_OK);
                }
                break;
        }
    }

    private void relocateChildrenParks(List<Element> parks)
    {
        for(Element park : parks)
        {
            Log.d(Constants.LOG_TAG, String.format(
                    "AddLocationsActivity.relocateChildrenParks:: relocating children<Park> of parent %s to new %s...", this.viewModel.parentLocation, this.viewModel.newLocation));
            park.relocateElement(this.viewModel.newLocation);
        }
    }

    private boolean createLocation()
    {
        boolean success = false;
        this.viewModel.newLocation = Location.create(this.editText.getText().toString());

        if(this.viewModel.newLocation != null)
        {
            App.content.addElement(this.viewModel.newLocation);
            success = true;
        }

        Log.d(Constants.LOG_TAG, String.format("AddLocationsActivity.createLocation:: create %s success[%S]", this.viewModel.newLocation, success));

        return success;
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("AddElementsActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            Log.i(Constants.LOG_TAG, String.format("AddElementsActivity.returnResult:: returning new %s", this.viewModel.newLocation));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.newLocation.getUuid().toString());
        }

        setResult(resultCode, intent);
        finish();
    }
}
