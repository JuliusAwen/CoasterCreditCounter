package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.Blueprint;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.CustomAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.StockAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;

public class CreateAttractionActivity extends BaseActivity
{
    private CreateAttractionActivityViewModel viewModel;

    private TextInputLayout textInputLayoutAttractionName;
    private TextInputEditText textInputEditTextAttractionName;

    private LinearLayout layoutBlueprint;
    private TextView textViewBlueprint;
    private ImageView imageViewPickBlueprint;

    private LinearLayout layoutCreditType;
    private TextView textViewCreditType;
    private ImageView imageViewPickCreditType;

    private LinearLayout layoutCategory;
    private TextView textViewCategory;
    private ImageView imageViewPickCategory;

    private LinearLayout layoutManufacturer;
    private TextView textViewManufacturer;
    private ImageView imageViewPickManufacturer;

    private LinearLayout layoutStatus;
    private TextView textViewStatus;
    private ImageView imageViewPickStatus;

    private LinearLayout layoutUntrackedRideCount;
    private TextInputLayout textInputLayoutUntrackedRideCount;
    private TextInputEditText textInputEditTextUntrackedRideCount;

    private Drawable pickIconBlack;
    private Drawable pickIconGrey;

    private Drawable closeIcon;

    protected void setContentView()
    {
        setContentView(R.layout.activity_create_attraction);
    }

    protected void create()
    {
        this.textInputLayoutAttractionName = findViewById(R.id.textInputLayoutCreateOrEditAttraction_AttractionName);
        this.textInputEditTextAttractionName = findViewById(R.id.textInputEditTextCreateOrEditAttraction_AttractionName);

        this.layoutBlueprint = findViewById(R.id.linearLayoutCreateOrEditAttraction_Blueprint);
        this.textViewBlueprint = findViewById(R.id.textViewCreateOrEditAttraction_Blueprint);
        this.imageViewPickBlueprint = findViewById(R.id.imageViewCreateOrEditAttraction_PickBlueprint);

        this.layoutCreditType = findViewById(R.id.linearLayoutCreateOrEditAttraction_CreditType);
        this.textViewCreditType = findViewById(R.id.textViewCreateOrEditAttraction_CreditType);
        this.imageViewPickCreditType = findViewById(R.id.imageViewCreateOrEditAttraction_PickCreditType);

        this.layoutCategory = findViewById(R.id.linearLayoutCreateOrEditAttraction_Category);
        this.textViewCategory = findViewById(R.id.textViewCreateOrEditAttraction_Category);
        this.imageViewPickCategory = findViewById(R.id.imageViewCreateOrEditAttraction_PickCategory);

        this.layoutManufacturer = findViewById(R.id.linearLayoutCreateOrEditAttraction_Manufacturer);
        this.textViewManufacturer = findViewById(R.id.textViewCreateOrEditAttraction_Manufacturer);
        this.imageViewPickManufacturer = findViewById(R.id.imageViewCreateOrEditAttraction_PickManufacturer);

        this.layoutStatus = findViewById(R.id.linearLayoutCreateOrEditAttraction_Status);
        this.textViewStatus = findViewById(R.id.textViewCreateOrEditAttraction_Status);
        this.imageViewPickStatus = findViewById(R.id.imageViewCreateOrEditAttraction_PickStatus);

        this.layoutUntrackedRideCount = findViewById(R.id.linearLayoutCreateOrEditAttraction_UntrackedRideCount);
        this.textInputLayoutUntrackedRideCount = findViewById(R.id.textInputLayoutCreateOrEditAttractionName_UntrackedRideCount);
        this.textInputEditTextUntrackedRideCount = findViewById(R.id.textInputEditTextCreateOrEditAttraction_UntrackedRideCount);

        this.pickIconBlack = DrawableProvider.getColoredDrawableMutation(R.drawable.ic_baseline_arrow_drop_down, R.color.black);
        this.pickIconGrey = DrawableProvider.getColoredDrawableMutation(R.drawable.ic_baseline_arrow_drop_down, R.color.grey);
        this.closeIcon = DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_close, R.color.black);


        this.viewModel = new ViewModelProvider(this).get(CreateAttractionActivityViewModel.class);

        if(this.viewModel.requestCode == null)
        {
            this.viewModel.requestCode = RequestCode.values()[getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, 0)];
        }

        if(this.viewModel.requestCode == RequestCode.CREATE_ON_SITE_ATTRACTION && this.viewModel.parentPark == null)
        {
            this.viewModel.parentPark = (Park) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        this.layoutBlueprint.setVisibility(View.GONE);
        this.layoutStatus.setVisibility(View.GONE);
        this.layoutUntrackedRideCount.setVisibility(View.GONE);

        this.createTextInputAttractionName(getIntent().getStringExtra(Constants.EXTRA_HINT));
        this.decorateLayoutCreditType();
        this.decorateLayoutCategory();
        this.decorateLayoutManufacturer();

        if(this.viewModel.requestCode == RequestCode.CREATE_ON_SITE_ATTRACTION)
        {
            this.decorateLayoutBlueprint();
            this.decorateLayoutStatus();
            this.createTextInputUntrackedRideCount();
        }

        
        super.createHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_HELP_TITLE)), getIntent().getStringExtra(Constants.EXTRA_HELP_TEXT));
        super.createToolbar()
                .addToolbarHomeButton()
                .setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE));

        super.createFloatingActionButton();
        this.decorateFloatingActionButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(Constants.LOG_TAG, String.format("CreateAttractionActivity.onActivityResult:: requestCode[%s], resultCode[%s]", RequestCode.getValue(requestCode), resultCode));

        if(resultCode == RESULT_OK)
        {
            IElement pickedElement = ResultFetcher.fetchResultElement(data);
            if(pickedElement != null)
            {
                switch(RequestCode.getValue(requestCode))
                {
                    case PICK_BLUEPRINT:
                    {
                        this.updateLayoutBlueprint((Blueprint)pickedElement);
                        break;
                    }
                    case PICK_CREDIT_TYPE:
                    {
                        this.updateLayoutCreditType((CreditType)pickedElement);
                        break;
                    }
                    case PICK_CATEGORY:
                    {
                        this.updateLayoutCategory((Category)pickedElement);
                        break;
                    }
                    case PICK_MANUFACTURER:
                    {
                        this.updateLayoutManufacturer((Manufacturer)pickedElement);
                        break;
                    }
                    case PICK_STATUS:
                    {
                        this.updateLayoutStatus((Status)pickedElement);
                        break;
                    }
                }

                Log.i(Constants.LOG_TAG, String.format("CreateAttractionActivity.onActivityResult:: picked %s", pickedElement));
            }
            else
            {
                switch(RequestCode.getValue(requestCode)) //it is possible that element was deleted, so it has to be evaluated if it is still existing
                {
                    case PICK_BLUEPRINT:
                    {
                        if(!App.content.containsElement(this.viewModel.blueprint))
                        {
                            this.updateLayoutBlueprint(null);
                        }
                        break;
                    }
                    case PICK_CREDIT_TYPE:
                    {
                        if(!App.content.containsElement(this.viewModel.creditType))
                        {
                            this.updateLayoutCreditType(CreditType.getDefault());
                        }
                        break;
                    }
                    case PICK_CATEGORY:
                    {
                        if(!App.content.containsElement(this.viewModel.category))
                        {
                            this.updateLayoutCategory(Category.getDefault());
                        }
                        break;
                    }
                    case PICK_MANUFACTURER:
                    {
                        if(!App.content.containsElement(this.viewModel.manufacturer))
                        {
                            this.updateLayoutManufacturer(Manufacturer.getDefault());
                        }
                        break;
                    }
                    case PICK_STATUS:
                    {
                        if(!App.content.containsElement(this.viewModel.status))
                        {
                            this.updateLayoutStatus(Status.getDefault());
                        }
                        break;
                    }
                }
            }
        }
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_check, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handleCreateAttraction();
            }
        });

        super.setFloatingActionButtonVisibility(true);
    }

    private void handleCreateAttraction()
    {
        if(this.textInputLayoutAttractionName.getError() != null || this.textInputLayoutUntrackedRideCount.getError() != null)
        {
            Log.w(Constants.LOG_TAG, "CreateAttractionActivity.handleEditAttraction:: some input is invalid");
            return;
        }

        if(this.viewModel.requestCode == RequestCode.CREATE_ON_SITE_ATTRACTION)
        {
            //this has to happen before name is set (otherwise if something is wrong, the name would already be changed)
            int untrackedRideCount = this.fetchUntrackedRideCountFromTextInput();
            if(untrackedRideCount < 0)
            {
                Log.w(Constants.LOG_TAG, "CreateAttractionActivity.handleEditAttraction:: entered untracked ride count is invalid");
                this.textInputLayoutUntrackedRideCount.setError(getString(R.string.error_number_invalid));
                return;
            }
            else
            {
                this.viewModel.untrackedRideCount = untrackedRideCount;
            }
        }


        this.viewModel.name = this.textInputEditTextAttractionName.getText().toString();
        if(this.tryCreateAttraction())
        {
            if(this.viewModel.requestCode == RequestCode.CREATE_ON_SITE_ATTRACTION)
            {
                Log.d(Constants.LOG_TAG, String.format("CreateAttractionActivity.handleEditAttraction:: adding child %s to parent %s", this.viewModel.attraction, this.viewModel.parentPark));
                this.viewModel.parentPark.addChildAndSetParent(viewModel.attraction);
                super.markForUpdate(viewModel.parentPark);
            }

            super.markForCreation(this.viewModel.attraction);
            returnResult(RESULT_OK);
        }
        else
        {
            Log.w(Constants.LOG_TAG, String.format("CreateAttractionActivity.handleEditAttraction:: entered name [%s] is invalid", this.viewModel.name));
            this.textInputLayoutAttractionName.setError(getString(R.string.error_name_invalid));
        }
    }

    private int fetchUntrackedRideCountFromTextInput()
    {
        int untrackedRideCount = -1;
        String untrackedRideCountString = this.textInputEditTextUntrackedRideCount.getText().toString().trim();

        if(!untrackedRideCountString.isEmpty())
        {
            try
            {
                untrackedRideCount = Integer.parseInt(untrackedRideCountString);
            }
            catch(NumberFormatException nfe)
            {
                Log.e(Constants.LOG_TAG, String.format("EditAttractionActivity.fetchUntrackedRideCountFromTextInput:: catched NumberFormatException parsing untracked ride count: [%s]", nfe));
            }
        }
        else
        {
            Log.w(Constants.LOG_TAG, "CreateAttractionActivity.handleEditAttraction:: no untracked ride count was entered - setting to 0");
            this.decorateTextInputUntrackedRideCount();
            untrackedRideCount = 0;
        }

        return untrackedRideCount;
    }

    private boolean tryCreateAttraction()
    {
        boolean success = false;

        if(this.viewModel.requestCode == RequestCode.CREATE_ON_SITE_ATTRACTION)
        {
            if(this.viewModel.blueprint != null)
            {
                IAttraction attraction = StockAttraction.create(this.viewModel.name, this.viewModel.blueprint, this.viewModel.untrackedRideCount);

                if(attraction != null)
                {
                    attraction.setStatus(this.viewModel.status);

                    this.viewModel.attraction = attraction;

                    success = true;
                }
            }
            else
            {
                IAttraction attraction = CustomAttraction.create(this.viewModel.name, this.viewModel.untrackedRideCount);
                if(attraction != null)
                {
                    attraction.setCreditType(this.viewModel.creditType);
                    attraction.setCategory(this.viewModel.category);
                    attraction.setManufacturer(this.viewModel.manufacturer);
                    attraction.setStatus(this.viewModel.status);
                    attraction.setUntracktedRideCount(this.viewModel.untrackedRideCount);

                    this.viewModel.attraction = attraction;

                    success = true;
                }
            }
        }
        else
        {
            IAttraction attraction = Blueprint.create(this.viewModel.name);
            if(attraction != null)
            {
                attraction.setCreditType(this.viewModel.creditType);
                attraction.setCategory(this.viewModel.category);
                attraction.setManufacturer(this.viewModel.manufacturer);

                this.viewModel.attraction = attraction;

                success = true;
            }
        }

        Log.d(Constants.LOG_TAG, String.format("CreateAttractionActivity.tryCreateAttraction:: creation sucessful[%S]", success));
        return success;
    }

    private void createTextInputAttractionName(String hint)
    {
        this.textInputLayoutAttractionName.setHint(hint);
        this.textInputLayoutAttractionName.setError(null);
        this.textInputLayoutAttractionName.setCounterMaxLength(App.config.maxCharacterCount);

        this.textInputEditTextAttractionName.setOnEditorActionListener(this.getOnEditorActionListener());
        this.textInputEditTextAttractionName.requestFocus();
        this.textInputEditTextAttractionName.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (editable.length() > textInputLayoutAttractionName.getCounterMaxLength())
                {
                    textInputLayoutAttractionName.setError(CreateAttractionActivity.this.getString(R.string.error_character_count_exceeded,
                            textInputLayoutAttractionName.getCounterMaxLength()));
                }
                else
                {
                    textInputLayoutAttractionName.setError(null);
                }
            }
        });
    }

    private void decorateLayoutBlueprint()
    {
        this.layoutBlueprint.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(Constants.LOG_TAG, "CreateAttractionActivity.onClick:: <PickBlueprint> selected");

                if(viewModel.blueprint != null)
                {
                    updateLayoutBlueprint(null);
                    updateLayoutCreditType(viewModel.creditType);
                    updateLayoutCategory(viewModel.category);
                    updateLayoutManufacturer(viewModel.manufacturer);
                }
                else
                {
                    ActivityDistributor.startActivityPickForResult(CreateAttractionActivity.this, RequestCode.PICK_BLUEPRINT, App.content.getContentOfType(Blueprint.class));
                }
            }
        });

        this.updateLayoutBlueprint(null);
        this.layoutBlueprint.setVisibility(View.VISIBLE);
    }

    private void updateLayoutBlueprint(Blueprint blueprint)
    {
        this.viewModel.blueprint = blueprint;

        Drawable icon;
        if(this.viewModel.blueprint != null)
        {
            this.textViewBlueprint.setText(blueprint.getName());

            this.textViewCreditType.setText(blueprint.getCreditType().getName());
            this.textViewCreditType.setTextColor(getColor(R.color.grey));
            this.imageViewPickCreditType.setImageDrawable(this.pickIconGrey);

            this.textViewCategory.setText(blueprint.getCategory().getName());
            this.textViewCategory.setTextColor(getColor(R.color.grey));
            this.imageViewPickCategory.setImageDrawable(this.pickIconGrey);

            this.textViewManufacturer.setText(blueprint.getManufacturer().getName());
            this.textViewManufacturer.setTextColor(getColor(R.color.grey));
            this.imageViewPickManufacturer.setImageDrawable(this.pickIconGrey);

            icon = this.closeIcon;
        }
        else
        {
            this.textViewBlueprint.setText(R.string.blueprint_not_applicable);
            icon = !App.content.getContentOfType(Blueprint.class).isEmpty() ? this.pickIconBlack : this.pickIconGrey;
        }

        this.imageViewPickBlueprint.setImageDrawable(icon);
    }

    private void decorateLayoutCreditType()
    {
        this.layoutCreditType.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(Constants.LOG_TAG, "CreateAttractionActivity.onClick:: <PickCreditType> selected");

                if(viewModel.blueprint != null)
                {
                    Toaster.makeShortToast(CreateAttractionActivity.this, getString(R.string.error_property_is_tied_to_blueprint, "CreditType"));
                }
                else
                {
                    ActivityDistributor.startActivityPickForResult(CreateAttractionActivity.this, RequestCode.PICK_CREDIT_TYPE, App.content.getContentOfType(CreditType.class));
                }
            }
        });

        this.updateLayoutCreditType(CreditType.getDefault());
        this.layoutCreditType.setVisibility(View.VISIBLE);
    }

    private void updateLayoutCreditType(CreditType creditType)
    {
        Log.d(Constants.LOG_TAG, String.format("CreateAttractionActivity.updateLayoutCreditType:: setting CreditType %s...", creditType));

        this.textViewCreditType.setText(creditType.getName());
        this.textViewCreditType.setTextColor(getColor(R.color.black));

        this.viewModel.creditType = creditType;

        this.imageViewPickCreditType.setImageDrawable(App.content.getContentOfType(CreditType.class).size() > 1 ? this.pickIconBlack : this.pickIconGrey);
    }

    private void decorateLayoutCategory()
    {
        this.layoutCategory.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(Constants.LOG_TAG, "CreateAttractionActivity.onClick:: <PickCategory> selected");

                if(viewModel.blueprint != null)
                {
                    Toaster.makeShortToast(CreateAttractionActivity.this, getString(R.string.error_property_is_tied_to_blueprint, "Category"));
                }
                else
                {
                    ActivityDistributor.startActivityPickForResult(CreateAttractionActivity.this, RequestCode.PICK_CATEGORY, App.content.getContentOfType(Category.class));
                }
            }
        });

        this.updateLayoutCategory(Category.getDefault());
        this.layoutCategory.setVisibility(View.VISIBLE);
    }

    private void updateLayoutCategory(Category category)
    {
        Log.d(Constants.LOG_TAG, String.format("CreateAttractionActivity.updateLayoutCategory:: setting Category %s", category));

        this.textViewCategory.setText(category.getName());
        this.textViewCategory.setTextColor(getColor(R.color.black));

        this.viewModel.category = category;

        this.imageViewPickCategory.setImageDrawable(App.content.getContentOfType(Category.class).size() > 1 ? this.pickIconBlack : this.pickIconGrey);
    }

    private void decorateLayoutManufacturer()
    {
        this.layoutManufacturer.setOnClickListener((new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(Constants.LOG_TAG, "CreateAttractionActivity.onClick:: <PickManufacturer> selected");

                if(viewModel.blueprint != null)
                {
                    Toaster.makeShortToast(CreateAttractionActivity.this, getString(R.string.error_property_is_tied_to_blueprint, "Manufacturer"));
                }
                else
                {
                    ActivityDistributor.startActivityPickForResult(CreateAttractionActivity.this, RequestCode.PICK_MANUFACTURER, App.content.getContentOfType(Manufacturer.class));
                }
            }
        }));

        this.updateLayoutManufacturer(Manufacturer.getDefault());
        this.layoutManufacturer.setVisibility(View.VISIBLE);
    }

    private void updateLayoutManufacturer(Manufacturer manufacturer)
    {
        Log.d(Constants.LOG_TAG, String.format("CreateAttractionActivity.updateLayoutManufacturer:: setting Manufacturer %s", manufacturer));

        this.textViewManufacturer.setText(manufacturer.getName());
        this.textViewManufacturer.setTextColor(getColor(R.color.black));

        this.viewModel.manufacturer = manufacturer;

        this.imageViewPickManufacturer.setImageDrawable(App.content.getContentOfType(Manufacturer.class).size() > 1 ? this.pickIconBlack : this.pickIconGrey);
    }

    private void decorateLayoutStatus()
    {
        this.layoutStatus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(Constants.LOG_TAG, "CreateAttractionActivity.onClick:: <PickStatus> selected");
                ActivityDistributor.startActivityPickForResult(CreateAttractionActivity.this, RequestCode.PICK_STATUS, App.content.getContentOfType(Status.class));
            }
        });

        this.updateLayoutStatus(Status.getDefault());
        this.layoutStatus.setVisibility(View.VISIBLE);
    }

    private void updateLayoutStatus(Status status)
    {
        Log.d(Constants.LOG_TAG, String.format("CreateAttractionActivity.updateLayoutStatus:: setting Status %s", status));

        this.textViewStatus.setText(status.getName());
        this.viewModel.status = status;

        this.imageViewPickStatus.setImageDrawable(App.content.getContentOfType(Status.class).size() > 1 ? this.pickIconBlack : this.pickIconGrey);
    }

    private void createTextInputUntrackedRideCount()
    {
        this.textInputLayoutUntrackedRideCount.setError(null);
        this.textInputLayoutUntrackedRideCount.setCounterMaxLength(App.config.maxDigitCount);

        this.textInputEditTextUntrackedRideCount.setOnEditorActionListener(this.getOnEditorActionListener());
        this.textInputEditTextUntrackedRideCount.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (editable.length() > textInputLayoutUntrackedRideCount.getCounterMaxLength())
                {
                    textInputLayoutUntrackedRideCount.setError(CreateAttractionActivity.this.getString(R.string.error_digit_count_exceeded,
                            textInputLayoutUntrackedRideCount.getCounterMaxLength()));
                }
                else if (editable.length() == 0)
                {
                    textInputLayoutUntrackedRideCount.setHint(getString(R.string.hint_enter_untracked_ride_count));
                }
                else
                {
                    textInputLayoutUntrackedRideCount.setError(null);
                    textInputLayoutUntrackedRideCount.setHint(null);
                }
            }
        });

        this.textInputEditTextUntrackedRideCount.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean hasFocus)
            {
                if(!hasFocus)
                {
                    if(textInputEditTextUntrackedRideCount.getText().toString().trim().isEmpty())
                    {
                        decorateTextInputUntrackedRideCount();
                    }
                }
            }
        });

        this.layoutUntrackedRideCount.setVisibility(View.VISIBLE);
        this.decorateTextInputUntrackedRideCount();
    }

    private void decorateTextInputUntrackedRideCount()
    {
        this.textInputEditTextUntrackedRideCount.setText(String.valueOf(0));
        this.textInputEditTextUntrackedRideCount.setSelection(1);
    }

    private TextView.OnEditorActionListener getOnEditorActionListener()
    {
        return new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                Log.i(Constants.LOG_TAG, String.format("CreateAttractionActivity.getOnEditorActionListener.onClickEditorAction:: actionId[%d]", actionId));

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

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("CreateAttractionActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            Log.i(Constants.LOG_TAG, String.format("CreateAttractionActivity.returnResult:: returning %s", this.viewModel.attraction));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.attraction.getUuid().toString());
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + this.getClass().getSimpleName());
        finish();
    }
}
