package de.juliusawen.coastercreditcounter.frontend.attractions;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.attractions.CustomAttraction;
import de.juliusawen.coastercreditcounter.backend.attractions.CustomCoaster;
import de.juliusawen.coastercreditcounter.backend.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Park;
import de.juliusawen.coastercreditcounter.backend.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Status;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.fragments.ConfirmDialogFragment;
import de.juliusawen.coastercreditcounter.frontend.spinnerAdapter.SpinnerAdapter;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.toolbox.ConvertTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class CreateOrEditCustomAttractionActivity extends BaseActivity implements ConfirmDialogFragment.ConfirmDialogFragmentInteractionListener
{
    private CreateOrEditCustomAttractionActivityViewModel viewModel;

    private EditText editTextAttractionName;
    private Spinner spinnerAttractionType;
    private Spinner spinnerManufacturer;
    private Spinner spinnerAttractionCategory;
    private Spinner spinnerStatus;
    private EditText editTextUntrackedRideCount;

    private final int attractionType_RollerCoaster = 1;
    private final int attractionType_NonRollerCoaster = 2;
    private Map<String, Integer> attractionTypesById;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "CreateOrEditCustomAttractionActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_create_or_edit_custom_attraction);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            this.editTextAttractionName = findViewById(R.id.editTextCreateOrEditAttractionName);
            this.spinnerAttractionType = findViewById(R.id.spinnerCreateOrEditAttraction_AttractionType);
            this.spinnerManufacturer = findViewById(R.id.spinnerCreateOrEditAttraction_Manufacturer);
            this.spinnerAttractionCategory = findViewById(R.id.spinnerCreateOrEditAttraction_AttractionCategory);
            this.spinnerStatus = findViewById(R.id.spinnerCreateOrEditAttraction_Status);
            this.editTextUntrackedRideCount = findViewById(R.id.editTextCreateOrEditAttractionUntrackedRideCount);

            this.viewModel = ViewModelProviders.of(this).get(CreateOrEditCustomAttractionActivityViewModel.class);

            if(this.viewModel.requestCode == -1)
            {
                this.viewModel.requestCode = getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, -1);

                if(this.viewModel.requestCode == Constants.REQUEST_CODE_EDIT_CUSTOM_ATTRACTION)
                {
                    this.viewModel.isEditMode = true;
                }
            }

            if(this.viewModel.isEditMode)
            {
                if(this.viewModel.attraction == null)
                {
                    this.viewModel.attraction = (IAttraction) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
                }
            }
            else if(this.viewModel.parentPark == null)
            {
                this.viewModel.parentPark = (Park) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
            }

            if(this.viewModel.toolbarTitle == null)
            {
                this.viewModel.toolbarTitle = this.viewModel.isEditMode ? getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE) : getString(R.string.title_custom_attraction_create);
            }

            if(this.viewModel.toolbarSubtitle == null)
            {
                this.viewModel.toolbarSubtitle = this.viewModel.isEditMode
                                ? this.viewModel.attraction.getName()
                                : getString(R.string.subtitle_custom_attraction_create, this.viewModel.parentPark.getName());
            }

            super.addHelpOverlayFragment(
                    getString(R.string.title_help, this.viewModel.isEditMode
                            ? getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE)
                            : getString(R.string.title_custom_attraction_create)),
                    getText(R.string.help_text_create_or_edit_custom_attraction));

            super.addToolbar();
            super.addToolbarHomeButton();
            super.setToolbarTitleAndSubtitle(this.viewModel.toolbarTitle, this.viewModel.toolbarSubtitle);

            super.addConfirmDialogFragment();

            this.createEditTextAttractionName();
            this.createAttractionTypesDictionary();
            this.createSpinnerAttractionType();
            this.createSpinnerManufacturer();
            this.createSpinnerAttractionCategory();
            this.createSpinnerStatus();
            this.createEditTextUntrackedRideCount();

            this.setKeyboardDetector();
        }
    }

    @Override
    public void onConfirmDialogFragmentInteraction(View view)
    {
        ButtonFunction buttonFunction = ButtonFunction.values()[view.getId()];
        Log.i(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.onConfirmDialogFragment:: [%S] selected", buttonFunction));

        switch (buttonFunction)
        {
            case OK:
                handleOnEditorActionDone();
                break;

            case CANCEL:
                this.returnResult(RESULT_CANCELED);
                break;
        }
    }

    private void createEditTextAttractionName()
    {
        this.editTextAttractionName.setOnEditorActionListener(this.getOnEditorActionListener());
    }

    private void createAttractionTypesDictionary()
    {
        this.attractionTypesById = new HashMap<>();
        this.attractionTypesById.put(getString(R.string.attraction_type_roller_coaster), this.attractionType_RollerCoaster);
        this.attractionTypesById.put(getString(R.string.attraction_type_non_roller_coaster), this.attractionType_NonRollerCoaster);
    }

    private void createSpinnerAttractionType()
    {
        if(this.viewModel.isEditMode)
        {
            this.spinnerAttractionType.setVisibility(View.GONE);
        }
        else
        {
            this.spinnerAttractionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    String item = parent.getItemAtPosition(position).toString();
                    viewModel.attractionType = attractionTypesById.get(item);
                    Log.d(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.createSpinnerAttractionType.onItemSelected:: attraction type set to [%s]", item));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, new ArrayList<>(this.attractionTypesById.keySet()));
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.spinnerAttractionType.setAdapter(arrayAdapter);
        }
    }

    private void createSpinnerManufacturer()
    {
        this.spinnerManufacturer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                viewModel.manufacturer = (Manufacturer)parent.getItemAtPosition(position);
                Log.d(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.createSpinnerManufacturer.onItemSelected:: manufacturer set to %s", viewModel.manufacturer));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        List<IElement> manufacturers = App.content.getContentOfType(Manufacturer.class);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_item, manufacturers);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.spinnerManufacturer.setAdapter(spinnerAdapter);
        this.spinnerManufacturer.setSelection(manufacturers.indexOf(Manufacturer.getDefault()));
    }

    private void createSpinnerAttractionCategory()
    {
        this.spinnerAttractionCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                viewModel.attractionCategory = (AttractionCategory) parent.getItemAtPosition(position);
                Log.d(Constants.LOG_TAG,
                        String.format("CreateOrEditCustomAttractionActivity.createSpinnerAttractionCategory.onItemSelected:: attraction category set to %s", viewModel.attractionCategory));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        List<IElement> attractionCategories = App.content.getContentOfType(AttractionCategory.class);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_item, attractionCategories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.spinnerAttractionCategory.setAdapter(spinnerAdapter);
        this.spinnerAttractionCategory.setSelection(attractionCategories.indexOf(AttractionCategory.getDefault()));
    }

    private void createSpinnerStatus()
    {
        this.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                viewModel.status = (Status) parent.getItemAtPosition(position);
                Log.d(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.createSpinnerStatus.onItemSelected:: status set to %s", viewModel.status));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        List<IElement> statuses = App.content.getContentOfType(Status.class);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_item, statuses);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.spinnerStatus.setAdapter(spinnerAdapter);
        this.spinnerStatus.setSelection(statuses.indexOf(Status.getDefault()));
    }

    private void createEditTextUntrackedRideCount()
    {
        this.editTextUntrackedRideCount.setOnEditorActionListener(this.getOnEditorActionListener());
    }

    private TextView.OnEditorActionListener getOnEditorActionListener()
    {
        return new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                Log.i(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.getOnEditorActionListener.onClickEditorAction:: actionId[%d]", actionId));

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
        boolean somethingWentWrong = false;

        this.viewModel.name = this.editTextAttractionName.getText().toString();
        Log.d(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.handleOnEditorActionDone:: attraction name set to [%s]", this.viewModel.name));

        String untrackedRideCountString = this.editTextUntrackedRideCount.getText().toString();
        try
        {
            if(!untrackedRideCountString.trim().isEmpty())
            {
                this.viewModel.untrackedRideCount = Integer.parseInt(untrackedRideCountString);
            }
            else
            {
                this.viewModel.untrackedRideCount = 0;
            }
            Log.d(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.handleOnEditorActionDone:: untracked ride count set to [%d]", this.viewModel.untrackedRideCount));
        }
        catch(NumberFormatException nfe)
        {
            somethingWentWrong = true;
            Toaster.makeToast(this, getString(R.string.error_number_not_valid));
        }


        if(!somethingWentWrong)
        {
            if(this.viewModel.isEditMode)
            {
                boolean somethingChanged = false;

                if(!this.viewModel.attraction.getName().equals(this.viewModel.name))
                {
                    if(this.viewModel.attraction.setName(this.viewModel.name))
                    {
                        somethingChanged = true;
                    }
                    else
                    {
                        somethingWentWrong = true;
                        Toaster.makeToast(this, getString(R.string.error_name_not_valid));
                    }
                }

                if(!this.viewModel.attraction.getManufacturer().equals(this.viewModel.manufacturer))
                {
                    this.viewModel.attraction.setManufacturer(this.viewModel.manufacturer);
                    somethingChanged = true;
                }

                if(!this.viewModel.attraction.getAttractionCategory().equals(this.viewModel.attractionCategory))
                {
                    this.viewModel.attraction.setAttractionCategory(this.viewModel.attractionCategory);
                    somethingChanged = true;
                }

                if(!this.viewModel.attraction.getStatus().equals(this.viewModel.status))
                {
                    this.viewModel.attraction.setStatus(this.viewModel.status);
                    somethingChanged = true;
                }

                if(this.viewModel.attraction.getUntracktedRideCount() != this.viewModel.untrackedRideCount)
                {
                    this.viewModel.attraction.setUntracktedRideCount(this.viewModel.untrackedRideCount);
                    somethingChanged = true;
                }

                if(somethingChanged && !somethingWentWrong)
                {
                    super.markForUpdate(this.viewModel.attraction);
                }

                if(!somethingWentWrong)
                {
                    this.returnResult(RESULT_OK);
                }
            }
            else
            {
                if(this.createAttraction())
                {
                    Log.d(Constants.LOG_TAG, String.format("CreateLocationsActivity.handleOnEditorActionDone:: adding child %s to parent %s",
                            this.viewModel.attraction, this.viewModel.parentPark));

                    this.viewModel.parentPark.addChildAndSetParent(this.viewModel.attraction);
                    super.markForCreation(this.viewModel.attraction);
                    super.markForUpdate(this.viewModel.parentPark);

                    this.returnResult(RESULT_OK);
                }
                else
                {
                    Toaster.makeToast(this, getString(R.string.error_name_not_valid));
                }
            }
        }
    }

    private boolean createAttraction()
    {
        boolean success = false;
        IAttraction attraction = null;

        if(this.viewModel.attractionType == this.attractionType_RollerCoaster)
        {
             attraction = CustomCoaster.create(this.editTextAttractionName.getText().toString(), this.viewModel.untrackedRideCount, null);
        }
        else if(this.viewModel.attractionType == this.attractionType_NonRollerCoaster)
        {
            attraction = CustomAttraction.create(this.editTextAttractionName.getText().toString(), this.viewModel.untrackedRideCount, null);
        }

        if(attraction != null)
        {
            this.viewModel.attraction = attraction;
            this.viewModel.attraction.setManufacturer(this.viewModel.manufacturer);
            this.viewModel.attraction.setAttractionCategory(this.viewModel.attractionCategory);
            this.viewModel.attraction.setStatus(this.viewModel.status);
            this.viewModel.attraction.setUntracktedRideCount(this.viewModel.untrackedRideCount);

            success = true;
        }

        Log.d(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.createAttraction:: create %s success[%S]", this.viewModel.attraction, success));

        return success;
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            Log.i(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.returnResult:: returning %s", this.viewModel.attraction));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.attraction.getUuid().toString());
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
        finish();
    }

    private void setKeyboardDetector()
    {
        final View activityRootView = findViewById(android.R.id.content);

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                int heightDifference = activityRootView.getRootView().getHeight() - activityRootView.getHeight();

                if(heightDifference > ConvertTool.convertDpToPx(150))
                {
                    CreateOrEditCustomAttractionActivity.super.setConfirmDialogVisibilityWithoutFade(false);
                }
                else
                {
                    CreateOrEditCustomAttractionActivity.super.setConfirmDialogVisibilityWithoutFade(true);
                }
            }
        });
    }
}
