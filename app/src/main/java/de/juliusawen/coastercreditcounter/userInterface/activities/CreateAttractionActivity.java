package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import java.util.List;
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

    private EditText editTextAttractionName;

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
    private EditText editTextUntrackedRideCount;

    private Drawable pickIconBlack;
    private Drawable pickIconGrey;

    private Drawable closeIcon;

    protected void setContentView()
    {
        setContentView(R.layout.activity_create_attraction);
    }

    protected void create()
    {
        this.editTextAttractionName = findViewById(R.id.editTextCreateOrEditAttractionName);

        this.layoutBlueprint = findViewById(R.id.linearLayoutCreateOrEditAttraction_Blueprint);
        this.layoutBlueprint.setVisibility(View.GONE);
        this.textViewBlueprint = findViewById(R.id.textViewCreateOrEditAttraction_Blueprint);
        this.imageViewPickBlueprint = findViewById(R.id.imageViewCreateOrEditAttraction_PickBlueprint);

        this.layoutCreditType = findViewById(R.id.linearLayoutCreateOrEditAttraction_CreditType);
        this.layoutCreditType.setVisibility(View.GONE);
        this.textViewCreditType = findViewById(R.id.textViewCreateOrEditAttraction_CreditType);
        this.imageViewPickCreditType = findViewById(R.id.imageViewCreateOrEditAttraction_PickCreditType);

        this.layoutCategory = findViewById(R.id.linearLayoutCreateOrEditAttraction_Category);
        this.layoutCategory.setVisibility(View.GONE);
        this.textViewCategory = findViewById(R.id.textViewCreateOrEditAttraction_Category);
        this.imageViewPickCategory = findViewById(R.id.imageViewCreateOrEditAttraction_PickCategory);

        this.layoutManufacturer = findViewById(R.id.linearLayoutCreateOrEditAttraction_Manufacturer);
        this.layoutManufacturer.setVisibility(View.GONE);
        this.textViewManufacturer = findViewById(R.id.textViewCreateOrEditAttraction_Manufacturer);
        this.imageViewPickManufacturer = findViewById(R.id.imageViewCreateOrEditAttraction_PickManufacturer);

        this.layoutStatus = findViewById(R.id.linearLayoutCreateOrEditAttraction_Status);
        this.layoutStatus.setVisibility(View.GONE);
        this.textViewStatus = findViewById(R.id.textViewCreateOrEditAttraction_Status);
        this.imageViewPickStatus = findViewById(R.id.imageViewCreateOrEditAttraction_PickStatus);

        this.layoutUntrackedRideCount = findViewById(R.id.linearLayoutCreateOrEditAttraction_UntrackedRideCount);
        this.editTextUntrackedRideCount = findViewById(R.id.editTextCreateOrEditAttraction_UntrackedRideCount);


        this.pickIconBlack = DrawableProvider.getColoredDrawableMutation(R.drawable.ic_baseline_arrow_drop_down, R.color.black);
        this.pickIconGrey = DrawableProvider.getColoredDrawableMutation(R.drawable.ic_baseline_arrow_drop_down, R.color.grey);

        this.closeIcon = DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_close, R.color.black);


        this.viewModel = new ViewModelProvider(this).get(CreateAttractionActivityViewModel.class);

        if(this.viewModel.requestCode == null)
        {
            this.viewModel.requestCode = RequestCode.values()[getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, 0)];
        }

        if(this.viewModel.hint == null)
        {
            this.viewModel.hint = getIntent().getStringExtra(Constants.EXTRA_HINT);
        }

        if(this.viewModel.requestCode == RequestCode.CREATE_ON_SITE_ATTRACTION && this.viewModel.parentPark == null)
        {
            this.viewModel.parentPark = (Park) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }


        this.layoutBlueprint.setVisibility(View.GONE);
        this.layoutCreditType.setVisibility(View.GONE);
        this.layoutCategory.setVisibility(View.GONE);
        this.layoutManufacturer.setVisibility(View.GONE);
        this.layoutStatus.setVisibility(View.GONE);
        this.layoutUntrackedRideCount.setVisibility(View.GONE);

        this.decorateEditTextAttractionName();
        this.decorateLayoutCreditType();
        this.decorateLayoutCategory();
        this.decorateLayoutManufacturer();

        if(this.viewModel.requestCode == RequestCode.CREATE_ON_SITE_ATTRACTION)
        {
            this.decorateLayoutBlueprint();
            this.decorateLayoutStatus();
            this.decorateEditTextUntrackedRideCount();
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

        if(resultCode != RESULT_OK) // no Property was selected in PickPropertyActivity
        { // as it is possible that previously set Property was deleted via Pick-->ManageProperty, it's existence has to be validated and removed from layout if non existant
            switch(RequestCode.getValue(requestCode))
            {
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
        else if(resultCode == RESULT_OK) // a Element was selected in PickElements
        {
            IElement pickedElement = ResultFetcher.fetchResultElement(data);
            if(pickedElement != null)
            {
                switch(RequestCode.getValue(requestCode))
                {
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

                    case PICK_BLUEPRINT:
                    {
                        this.updateLayoutBlueprint((Blueprint)pickedElement);
                        break;
                    }
                }

                Log.i(Constants.LOG_TAG, String.format("CreateAttractionActivity.onActivityResult:: picked %s", pickedElement));
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("CreateAttractionActivity.onActivityResult:: result OK but no picked Element returned for RequestCode [%s]",
                        RequestCode.getValue(requestCode)));

                if(App.config.isDebugBuild())
                {
                    Toaster.makeLongToast(this, "result OK but no picked Element returned");
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
                viewModel.name = editTextAttractionName.getText().toString();
                Log.v(Constants.LOG_TAG, String.format("CreateAttractionActivity.onClickFab:: attraction name entered [%s]", viewModel.name));

                boolean somethingWentWrong = false;
                if(viewModel.requestCode == RequestCode.CREATE_ON_SITE_ATTRACTION)
                {
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
                        Log.v(Constants.LOG_TAG, String.format("CreateAttractionActivity.onClickFab:: untracked ride count set to [%d]", viewModel.untrackedRideCount));
                    }
                    catch(NumberFormatException nfe)
                    {
                        Log.w(Constants.LOG_TAG, String.format("CreateAttractionActivity.onClickFab:: catched NumberFormatException parsing untracked ride count: [%s]", nfe));

                        somethingWentWrong = true;
                        Toaster.makeShortToast(CreateAttractionActivity.this, getString(R.string.error_number_not_valid));
                    }
                }

                if(!somethingWentWrong)
                {
                    if(createAttraction())
                    {
                        Log.d(Constants.LOG_TAG, String.format("CreateAttractionActivity.onClickFab:: adding child %s to parent %s", viewModel.attraction, viewModel.parentPark));

                        if(viewModel.requestCode == RequestCode.CREATE_ON_SITE_ATTRACTION)
                        {
                            viewModel.parentPark.addChildAndSetParent(viewModel.attraction);
                            CreateAttractionActivity.super.markForUpdate(viewModel.parentPark);
                        }

                        CreateAttractionActivity.super.markForCreation(viewModel.attraction);

                        returnResult(RESULT_OK);
                    }
                    else
                    {
                        Toaster.makeShortToast(CreateAttractionActivity.this, getString(R.string.error_name_not_valid));
                    }
                }
            }
        });

        super.setFloatingActionButtonVisibility(true);
    }

    private void decorateEditTextAttractionName()
    {
        this.editTextAttractionName.setHint(this.viewModel.hint);
        this.editTextAttractionName.setOnEditorActionListener(this.getOnEditorActionListener());
        this.editTextAttractionName.requestFocus();
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
                    List<IElement> elements = App.content.getContentOfType(Blueprint.class);
                    ActivityDistributor.startActivityPickForResult(CreateAttractionActivity.this, RequestCode.PICK_BLUEPRINT, elements);
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
            this.textViewBlueprint.setText(R.string.blueprint_not_selected);
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

    private void decorateEditTextUntrackedRideCount()
    {
        this.editTextUntrackedRideCount.setOnEditorActionListener(this.getOnEditorActionListener());
        this.editTextUntrackedRideCount.setText(String.valueOf(0));

        this.layoutUntrackedRideCount.setVisibility(View.VISIBLE);
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

    private boolean createAttraction()
    {
        boolean success = false;

        if(this.viewModel.requestCode == RequestCode.CREATE_ON_SITE_ATTRACTION)
        {
            if(this.viewModel.blueprint != null)
            {
                IAttraction attraction = StockAttraction.create(this.editTextAttractionName.getText().toString(), this.viewModel.blueprint, this.viewModel.untrackedRideCount);

                if(attraction != null)
                {
                    attraction.setStatus(this.viewModel.status);

                    this.viewModel.attraction = attraction;

                    Log.d(Constants.LOG_TAG, String.format("CreateAttractionActivity.createAttraction:: created %s", this.viewModel.attraction.getFullName()));

                    success = true;
                }
            }
            else
            {
                IAttraction attraction = CustomAttraction.create(this.editTextAttractionName.getText().toString(), this.viewModel.untrackedRideCount);
                if(attraction != null)
                {
                    attraction.setCreditType(this.viewModel.creditType);
                    attraction.setCategory(this.viewModel.category);
                    attraction.setManufacturer(this.viewModel.manufacturer);
                    attraction.setStatus(this.viewModel.status);
                    attraction.setUntracktedRideCount(this.viewModel.untrackedRideCount);

                    this.viewModel.attraction = attraction;

                    Log.d(Constants.LOG_TAG, String.format("CreateAttractionActivity.createAttraction:: created %s", attraction.getFullName()));

                    success = true;
                }
            }
        }
        else
        {
            IAttraction attraction = Blueprint.create(this.editTextAttractionName.getText().toString());
            if(attraction != null)
            {
                attraction.setCreditType(this.viewModel.creditType);
                attraction.setCategory(this.viewModel.category);
                attraction.setManufacturer(this.viewModel.manufacturer);

                this.viewModel.attraction = attraction;

                Log.d(Constants.LOG_TAG, String.format("CreateAttractionActivity.createAttraction:: created %s", attraction.getFullName()));

                success = true;
            }
        }

        Log.d(Constants.LOG_TAG, String.format("CreateAttractionActivity.createAttraction:: created successfuly [%S]", success));
        return success;
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
