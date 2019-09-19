package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.Coaster;
import de.juliusawen.coastercreditcounter.dataModel.elements.CustomAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.CustomCoaster;
import de.juliusawen.coastercreditcounter.dataModel.elements.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.StockAttraction;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Category;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Status;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;

public class CreateOrEditCustomAttractionActivity extends BaseActivity
{
    private CreateOrEditCustomAttractionActivityViewModel viewModel;

    private EditText editTextAttractionName;

    private Spinner spinnerAttractionType;

    private TextView textViewManufacturer;
    private TextView textViewCategory;
    private TextView textViewStatus;

    private EditText editTextUntrackedRideCount;

    private final int attractionType_RollerCoaster = 0;
    private final int attractionType_NonRollerCoaster = 1;
    private Map<String, Integer> attractionTypesById;


    protected void setContentView()
    {
        setContentView(R.layout.activity_create_or_edit_custom_attraction);
    }

    protected void create()
    {
        this.editTextAttractionName = findViewById(R.id.editTextCreateOrEditAttractionName);
        this.spinnerAttractionType = findViewById(R.id.spinnerCreateOrEditAttraction_AttractionType);
        this.textViewManufacturer = findViewById(R.id.textViewCreateOrEditAttraction_Manufacturer);
        this.textViewCategory = findViewById(R.id.textViewCreateOrEditAttraction_Category);
        this.textViewStatus = findViewById(R.id.textViewCreateOrEditAttraction_Status);
        this.editTextUntrackedRideCount = findViewById(R.id.editTextCreateOrEditAttractionUntrackedRideCount);

        this.viewModel = ViewModelProviders.of(this).get(CreateOrEditCustomAttractionActivityViewModel.class);

        if(RequestCode.values()[getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, 0)].equals(RequestCode.EDIT_CUSTOM_ATTRACTION))
        {
            this.viewModel.isEditMode = true;
        }

        if(this.viewModel.isEditMode)
        {
            if(this.viewModel.attraction == null)
            {
                this.viewModel.attraction = (IAttraction) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
            }

            if(this.viewModel.parentPark == null)
            {
                this.viewModel.parentPark = (Park) this.viewModel.attraction.getParent();
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
        super.addFloatingActionButton();

        this.decorateFloatingActionButton();
        this.createEditTextAttractionName();

        if(viewModel.isEditMode)
        {
            findViewById(R.id.linearLayoutCreateOrEditAttraction_AttractionType).setVisibility(View.GONE);
            Log.i(Constants.LOG_TAG, "CreateOrEditCustomAttractionActivity.onCreate:: Activity is in edit mode - hiding option to change attraction type");
        }
        else
        {
            this.createAttractionTypesDictionary();
            this.createLayoutAttractionType();
        }

        if(!(this.viewModel.attraction instanceof StockAttraction))
        {
            this.createLayoutManufacturer();
            this.createLayoutCategory();
        }
        //Todo: implement goto blueprint on else

        this.createLayoutStatus();
        this.createEditTextUntrackedRideCount();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode != RESULT_OK)
        {
            return;
        }

        IElement pickedElement = ResultFetcher.fetchResultElement(data);

        switch(RequestCode.values()[requestCode])
        {
            case PICK_MANUFACTURER:
                this.setText((Manufacturer)pickedElement);
                break;

            case PICK_CATEGORY:
                this.setText((Category) pickedElement);
                break;

            case PICK_STATUS:
                this.setText((Status)pickedElement);
                break;
        }

        Log.i(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.onActivityResult:: picked %s", pickedElement));
    }

    private void setText(Manufacturer element)
    {
        this.textViewManufacturer.setText(element.getName());
        this.viewModel.manufacturer = element;
    }

    private void setText(Category element)
    {
        this.textViewCategory.setText(element.getName());
        this.viewModel.category = element;
    }

    private void setText(Status element)
    {
        this.textViewStatus.setText(element.getName());
        this.viewModel.status = element;
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_check, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                boolean somethingWentWrong = false;

                viewModel.name = editTextAttractionName.getText().toString();
                Log.v(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.onClickFab:: attraction name entered [%s]", viewModel.name));

                String untrackedRideCountString = editTextUntrackedRideCount.getText().toString();
                try
                {
                    if(!untrackedRideCountString.trim().isEmpty())
                    {
                        viewModel.untrackedRideCount = Integer.parseInt(untrackedRideCountString);
                    }
                    else
                    {
                        viewModel.untrackedRideCount = 0;
                    }
                    Log.v(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.onClickFab:: untracked ride count set to [%d]", viewModel.untrackedRideCount));
                }
                catch(NumberFormatException nfe)
                {
                    Log.w(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.onClickFab:: catched NumberFormatException parsing untracked ride count: [%s]", nfe));

                    somethingWentWrong = true;
                    Toaster.makeToast(CreateOrEditCustomAttractionActivity.this, getString(R.string.error_number_not_valid));
                }


                if(!somethingWentWrong)
                {
                    if(viewModel.isEditMode)
                    {
                        boolean somethingChanged = false;

                        if(!viewModel.attraction.getName().equals(viewModel.name))
                        {
                            if(viewModel.attraction.setName(viewModel.name))
                            {
                                somethingChanged = true;
                            }
                            else
                            {
                                somethingWentWrong = true;
                                Toaster.makeToast(CreateOrEditCustomAttractionActivity.this, getString(R.string.error_name_not_valid));
                            }
                        }

                        if(!(viewModel.attraction instanceof StockAttraction))
                        {
                            if(!viewModel.attraction.getManufacturer().equals(viewModel.manufacturer))
                            {
                                viewModel.attraction.setManufacturer(viewModel.manufacturer);
                                somethingChanged = true;
                            }

                            if(!viewModel.attraction.getCategory().equals(viewModel.category))
                            {
                                viewModel.attraction.setCategory(viewModel.category);
                                somethingChanged = true;
                            }
                        }

                        if(!viewModel.attraction.getStatus().equals(viewModel.status))
                        {
                            viewModel.attraction.setStatus(viewModel.status);
                            somethingChanged = true;
                        }

                        if(viewModel.attraction.getUntracktedRideCount() != viewModel.untrackedRideCount)
                        {
                            viewModel.attraction.setUntracktedRideCount(viewModel.untrackedRideCount);
                            somethingChanged = true;
                        }

                        if(somethingChanged && !somethingWentWrong)
                        {
                            markForUpdate(viewModel.attraction);
                        }

                        if(!somethingWentWrong)
                        {
                            if(somethingChanged)
                            {
                                returnResult(RESULT_OK);
                            }
                            else
                            {
                                returnResult(RESULT_CANCELED);
                            }
                        }
                    }
                    else
                    {
                        if(createAttraction())
                        {
                            Log.d(Constants.LOG_TAG, String.format("CreateLocationsActivity.onClickFab:: adding child %s to parent %s", viewModel.attraction, viewModel.parentPark));

                            viewModel.parentPark.addChildAndSetParent(viewModel.attraction);


                            CreateOrEditCustomAttractionActivity.super.markForCreation(viewModel.attraction);
                            CreateOrEditCustomAttractionActivity.super.markForUpdate(viewModel.parentPark);

                            returnResult(RESULT_OK);
                        }
                        else
                        {
                            Toaster.makeToast(CreateOrEditCustomAttractionActivity.this, getString(R.string.error_name_not_valid));
                        }
                    }
                }
            }
        });

        super.setFloatingActionButtonVisibility(true);
    }

    private void createEditTextAttractionName()
    {
        this.editTextAttractionName.setOnEditorActionListener(this.getOnEditorActionListener());

        if(this.viewModel.isEditMode)
        {
            this.editTextAttractionName.setText(this.viewModel.attraction.getName());
            this.editTextAttractionName.setSelection(this.viewModel.attraction.getName().length());
        }
    }

    private void createAttractionTypesDictionary()
    {
        this.attractionTypesById = new HashMap<>();
        this.attractionTypesById.put(getString(R.string.attraction_type_roller_coaster), this.attractionType_RollerCoaster);
        this.attractionTypesById.put(getString(R.string.attraction_type_non_roller_coaster), this.attractionType_NonRollerCoaster);
    }

    private void createLayoutAttractionType()
    {
        this.spinnerAttractionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String item = parent.getItemAtPosition(position).toString();
                viewModel.attractionType = attractionTypesById.get(item);
                Log.d(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.onItemSelected:: attraction type set to [%s]", item));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, new ArrayList<>(this.attractionTypesById.keySet()));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerAttractionType.setAdapter(arrayAdapter);

        if(this.viewModel.isEditMode)
        {
            if(this.viewModel.attraction instanceof Coaster)
            {
                this.spinnerAttractionType.setSelection(attractionType_RollerCoaster);
            }
            else
            {
                this.spinnerAttractionType.setSelection(attractionType_NonRollerCoaster);
            }
        }
    }

    private void createLayoutManufacturer()
    {
        LinearLayout linearLayout = findViewById(R.id.linearLayoutCreateOrEditAttraction_Manufacturer);
        linearLayout.setOnClickListener((new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(Constants.LOG_TAG, "CreateOrEditCustomAttractionActivity.onClick:: <PickManufacturer> selected");

                List<IElement> elements = App.content.getContentOfType(Manufacturer.class);

                if(elements.size() == 1)
                {
                    Log.d(Constants.LOG_TAG, "CreateOrEditCustomAttractionActivity.onClick:: only one element found - picked!");
                    setText((Manufacturer)elements.get(0));
                }
                else
                {

                    ActivityDistributor.startActivityPickForResult(CreateOrEditCustomAttractionActivity.this, RequestCode.PICK_MANUFACTURER, elements);
                }
            }
        }));

        Manufacturer manufacturer = this.viewModel.isEditMode ? this.viewModel.attraction.getManufacturer() : Manufacturer.getDefault();
        this.textViewManufacturer.setText(manufacturer.getName());
        linearLayout.setVisibility(View.VISIBLE);

        this.viewModel.manufacturer = manufacturer;
    }

    private void createLayoutCategory()
    {
        LinearLayout linearLayout = findViewById(R.id.linearLayoutCreateOrEditAttraction_Category);
        linearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(Constants.LOG_TAG, "CreateOrEditCustomAttractionActivity.onClick:: <PickCategory> selected");

                List<IElement> elements = App.content.getContentOfType(Category.class);

                if(elements.size() == 1)
                {
                    Log.d(Constants.LOG_TAG, "CreateOrEditCustomAttractionActivity.onClick:: only one element found - picked!");
                    setText((Category)elements.get(0));
                }
                else
                {

                    ActivityDistributor.startActivityPickForResult(CreateOrEditCustomAttractionActivity.this, RequestCode.PICK_CATEGORY, elements);
                }
            }
        });

        Category category = this.viewModel.isEditMode ? this.viewModel.attraction.getCategory() : Category.getDefault();
        this.textViewCategory.setText(category.getName());
        linearLayout.setVisibility(View.VISIBLE);

        this.viewModel.category = category;
    }

    private void createLayoutStatus()
    {
        findViewById(R.id.linearLayoutCreateOrEditAttraction_Status).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(Constants.LOG_TAG, "CreateOrEditCustomAttractionActivity.onClick:: <PickStatus> selected");

                List<IElement> elements = App.content.getContentOfType(Status.class);

                if(elements.size() == 1)
                {
                    Log.d(Constants.LOG_TAG, "CreateOrEditCustomAttractionActivity.onClick:: only one element found - picked!");
                    setText((Status)elements.get(0));
                }
                else
                {
                    ActivityDistributor.startActivityPickForResult(CreateOrEditCustomAttractionActivity.this, RequestCode.PICK_STATUS, elements);
                }
            }
        });

        Status status = this.viewModel.isEditMode ? this.viewModel.attraction.getStatus() : Status.getDefault();
        this.textViewStatus.setText(status.getName());
        this.viewModel.status = status;
    }

    private void createEditTextUntrackedRideCount()
    {
        this.editTextUntrackedRideCount.setOnEditorActionListener(this.getOnEditorActionListener());

        if(this.viewModel.isEditMode)
        {
            this.editTextUntrackedRideCount.setText(String.valueOf(this.viewModel.attraction.getUntracktedRideCount()));
        }
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
                    InputMethodManager inputMethodManager = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    textView.clearFocus();
                    handled = true;
                }

                return handled;
            }
        };
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
            this.viewModel.attraction.setCategory(this.viewModel.category);
            this.viewModel.attraction.setStatus(this.viewModel.status);
            this.viewModel.attraction.setUntracktedRideCount(this.viewModel.untrackedRideCount);

            Log.d(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.createAttraction:: created %s", this.viewModel.attraction.getFullName()));

            success = true;
        }

        Log.d(Constants.LOG_TAG, String.format("CreateOrEditCustomAttractionActivity.createAttraction:: show - success[%S]", success));

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
        else
        {
            Log.i(Constants.LOG_TAG, "CreateOrEditCustomAttractionActivity.returnResult:: no changes - returning no element");
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + this.getClass().getSimpleName());
        finish();
    }
}
