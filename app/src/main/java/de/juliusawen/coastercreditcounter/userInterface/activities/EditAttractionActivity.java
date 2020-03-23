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

public class EditAttractionActivity extends BaseActivity
{
    private EditAttractionActivityViewModel viewModel;

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

    private EditText editTextUntrackedRideCount;

    private Drawable pickIconBlack;
    private  Drawable pickIconGrey;

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

        this.editTextUntrackedRideCount = findViewById(R.id.editTextCreateOrEditAttractionUntrackedRideCount);


        this.pickIconBlack = DrawableProvider.getColoredDrawableMutation(R.drawable.ic_baseline_arrow_drop_down, R.color.black);
        this.pickIconGrey = DrawableProvider.getColoredDrawableMutation(R.drawable.ic_baseline_arrow_drop_down, R.color.grey);


        this.viewModel = new ViewModelProvider(this).get(EditAttractionActivityViewModel.class);

        if(this.viewModel.attraction == null)
        {
            this.viewModel.attraction = (IAttraction) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        if(this.viewModel.attraction != null)
        {
            this.decorateEditTextAttractionName();

            if(this.viewModel.attraction.isCustomAttraction())
            {
                this.decorateLayoutCreditType();
                this.decorateLayoutCategory();
                this.decorateLayoutManufacturer();
            }
            else if(this.viewModel.attraction.isStockAttraction())
            {
                this.decorateLayoutBlueprint();
            }
            else if(this.viewModel.attraction.isBlueprint())
            {
                //handle blueprint
            }
            else
            {
                //log error
            }
            this.decorateLayoutStatus();
        }

        this.decorateEditTextUntrackedRideCount();

        super.createHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE)), getText(R.string.help_text_edit_attraction));
        super.createToolbar()
                .addToolbarHomeButton()
                .setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), this.viewModel.attraction.getName());
        super.createFloatingActionButton();

        this.decorateFloatingActionButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(Constants.LOG_TAG, String.format("EditAttractionActivity.onActivityResult:: requestCode[%s], resultCode[%s]", RequestCode.getValue(requestCode), resultCode));

        if(resultCode != RESULT_OK) // no Property was selected in PickPropertyActivity
        { // as it is possible that former Property was deleted via Pick-->ManageProperty, it's existence has to be validated
            switch(RequestCode.getValue(requestCode))
            {
                case PICK_CREDIT_TYPE:
                    this.updateLayoutCreditType(App.content.containsElement(this.viewModel.creditType)
                            ? this.viewModel.creditType
                            : CreditType.getDefault());
                    break;

                case PICK_CATEGORY:
                    this.updateLayoutCategory(App.content.containsElement(this.viewModel.category)
                            ? this.viewModel.category
                            : Category.getDefault());
                    break;

                case PICK_MANUFACTURER:
                    this.updateLayoutManufacturer(App.content.containsElement(this.viewModel.manufacturer)
                            ? this.viewModel.manufacturer
                            : Manufacturer.getDefault());
                    break;

                case PICK_STATUS:
                    this.updateLayoutStatus(App.content.containsElement(this.viewModel.status)
                            ? this.viewModel.status
                            : Status.getDefault());
                    break;
            }
        }
        else if(resultCode == RESULT_OK) // a Property was selected in PickPropertyActivity
        {
            IElement pickedElement = ResultFetcher.fetchResultElement(data);

            if(App.config.isDebugBuild() && pickedElement == null)
            {
                Toaster.makeShortToast(this, "RESULT OK - NO PROPERTY FETCHED!");
            }

            switch(RequestCode.getValue(requestCode))
            {
                case PICK_CREDIT_TYPE:
                    this.updateLayoutCreditType(pickedElement != null
                            ? (CreditType)pickedElement
                            : this.viewModel.creditType);
                    break;

                case PICK_CATEGORY:
                    this.updateLayoutCategory(pickedElement != null
                            ? (Category)pickedElement
                            : this.viewModel.category);
                    break;

                case PICK_MANUFACTURER:
                    this.updateLayoutManufacturer(pickedElement != null
                            ? (Manufacturer)pickedElement
                            : this.viewModel.manufacturer);
                    break;

                case PICK_STATUS:
                    this.updateLayoutStatus(pickedElement != null
                            ? (Status)pickedElement
                            : this.viewModel.status);
                    break;
            }
            Log.i(Constants.LOG_TAG, String.format("EditAttractionActivity.onActivityResult:: picked %s", pickedElement));
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
                boolean somethingWentWrong = false;

                viewModel.name = editTextAttractionName.getText().toString();
                Log.v(Constants.LOG_TAG, String.format("EditAttractionActivity.onClickFab:: attraction name entered [%s]", viewModel.name));

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
                    Log.v(Constants.LOG_TAG, String.format("EditAttractionActivity.onClickFab:: untracked ride count set to [%d]", viewModel.untrackedRideCount));
                }
                catch(NumberFormatException nfe)
                {
                    Log.w(Constants.LOG_TAG, String.format("EditAttractionActivity.onClickFab:: catched NumberFormatException parsing untracked ride count: [%s]", nfe));

                    somethingWentWrong = true;
                    Toaster.makeShortToast(EditAttractionActivity.this, getString(R.string.error_number_not_valid));
                }


                if(!somethingWentWrong)
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
                            Toaster.makeShortToast(EditAttractionActivity.this, getString(R.string.error_name_not_valid));
                        }
                    }

                    if(viewModel.attraction.hasCreditType())
                    {
                        if(!viewModel.attraction.getCreditType().equals(viewModel.creditType))
                        {
                            viewModel.attraction.setCreditType(viewModel.creditType);
                            somethingChanged = true;
                        }
                    }

                    if(viewModel.attraction.hasCategory())
                    {
                        if(!viewModel.attraction.getCategory().equals(viewModel.category))
                        {
                            viewModel.attraction.setCategory(viewModel.category);
                            somethingChanged = true;
                        }
                    }

                    if(viewModel.attraction.hasManufacturer())
                    {
                        if(!viewModel.attraction.getManufacturer().equals(viewModel.manufacturer))
                        {
                            viewModel.attraction.setManufacturer(viewModel.manufacturer);
                            somethingChanged = true;
                        }
                    }

                    if(viewModel.attraction.hasStatus())
                    {
                        if(!viewModel.attraction.getStatus().equals(viewModel.status))
                        {
                            viewModel.attraction.setStatus(viewModel.status);
                            somethingChanged = true;
                        }
                    }

                    if(viewModel.attraction.getUntracktedRideCount() != viewModel.untrackedRideCount)
                    {
                        viewModel.attraction.setUntracktedRideCount(viewModel.untrackedRideCount);
                        somethingChanged = true;
                    }

                    if(!somethingWentWrong && somethingChanged)
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
            }
        });

        super.setFloatingActionButtonVisibility(true);
    }

    private void decorateEditTextAttractionName()
    {
        this.editTextAttractionName.setOnEditorActionListener(this.getOnEditorActionListener());
        this.editTextAttractionName.setText(this.viewModel.attraction.getName());
        this.editTextAttractionName.setSelection(this.viewModel.attraction.getName().length());
    }

    private void decorateLayoutBlueprint()
    {
        this.layoutBlueprint.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(Constants.LOG_TAG, "EditAttractionActivity.onClick:: <PickBlueprint> selected");

                List<IElement> elements = App.content.getContentOfType(Blueprint.class);

                ActivityDistributor.startActivityPickForResult(EditAttractionActivity.this, RequestCode.PICK_BLUEPRINT, elements);
            }
        });

        this.textViewBlueprint.setText(((StockAttraction)this.viewModel.attraction).getBlueprint().getName());

        this.imageViewPickBlueprint.setImageDrawable(App.content.getContentOfType(Blueprint.class).size() > 1 ? this.pickIconBlack : this.pickIconGrey);

        this.layoutBlueprint.setVisibility(View.VISIBLE);
    }

    private void decorateLayoutCreditType()
    {
        this.layoutCreditType.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(Constants.LOG_TAG, "EditAttractionActivity.onClick:: <PickCreditType> selected");
                ActivityDistributor.startActivityPickForResult(EditAttractionActivity.this, RequestCode.PICK_CREDIT_TYPE, App.content.getContentOfType(CreditType.class));
            }
        });

        this.updateLayoutCreditType(this.viewModel.attraction.getCreditType());

        this.layoutCreditType.setVisibility(View.VISIBLE);
    }

    private void updateLayoutCreditType(CreditType creditType)
    {
        Log.d(Constants.LOG_TAG, String.format("EditAttractionActivity.updateLayoutCreditType:: setting CreditType %s...", creditType));

        this.textViewCreditType.setText(creditType.getName());
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
                Log.d(Constants.LOG_TAG, "EditAttractionActivity.onClick:: <PickCategory> selected");
                ActivityDistributor.startActivityPickForResult(EditAttractionActivity.this, RequestCode.PICK_CATEGORY, App.content.getContentOfType(Category.class));
            }
        });

        this.updateLayoutCategory(this.viewModel.attraction.getCategory());

        this.layoutCategory.setVisibility(View.VISIBLE);
    }

    private void updateLayoutCategory(Category category)
    {
        Log.d(Constants.LOG_TAG, String.format("EditAttractionActivity.updateLayoutCategory:: setting Category %s", category));

        this.textViewCategory.setText(category.getName());
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
                Log.d(Constants.LOG_TAG, "EditAttractionActivity.onClick:: <PickManufacturer> selected");
                ActivityDistributor.startActivityPickForResult(EditAttractionActivity.this, RequestCode.PICK_MANUFACTURER, App.content.getContentOfType(Manufacturer.class));
            }
        }));

        this.updateLayoutManufacturer(this.viewModel.attraction.getManufacturer());

        this.layoutManufacturer.setVisibility(View.VISIBLE);
    }

    private void updateLayoutManufacturer(Manufacturer manufacturer)
    {
        Log.d(Constants.LOG_TAG, String.format("EditAttractionActivity.updateLayoutManufacturer:: setting Manufacturer %s", manufacturer));

        this.textViewManufacturer.setText(manufacturer.getName());
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
                Log.d(Constants.LOG_TAG, "EditAttractionActivity.onClick:: <PickStatus> selected");
                ActivityDistributor.startActivityPickForResult(EditAttractionActivity.this, RequestCode.PICK_STATUS, App.content.getContentOfType(Status.class));
            }
        });

        this.updateLayoutStatus(this.viewModel.attraction.getStatus());

        this.layoutStatus.setVisibility(View.VISIBLE);
    }

    private void updateLayoutStatus(Status status)
    {
        Log.d(Constants.LOG_TAG, String.format("EditAttractionActivity.updateLayoutStatus:: setting Status %s", status));

        this.textViewStatus.setText(status.getName());
        this.viewModel.status = status;

        this.imageViewPickStatus.setImageDrawable(App.content.getContentOfType(Status.class).size() > 1 ? this.pickIconBlack : this.pickIconGrey);
    }

    private void decorateEditTextUntrackedRideCount()
    {
        this.editTextUntrackedRideCount.setOnEditorActionListener(this.getOnEditorActionListener());
        this.editTextUntrackedRideCount.setText(String.valueOf(this.viewModel.attraction.getUntracktedRideCount()));
    }

    private TextView.OnEditorActionListener getOnEditorActionListener()
    {
        return new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                Log.i(Constants.LOG_TAG, String.format("EditAttractionActivity.getOnEditorActionListener.onClickEditorAction:: actionId[%d]", actionId));

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

    private boolean createAttraction() //adjust to make it work for Blueprints
    {
        boolean success = false;
        IAttraction attraction = CustomAttraction.create(this.editTextAttractionName.getText().toString(), this.viewModel.untrackedRideCount);

        //        IAttraction attraction = Blueprint.create(this.editTextAttractionName.getText().toString());
        //
        //        IAttraction attraction = StockAttraction.create(this.editTextAttractionName.getText().toString(), Blueprint.create("unnamed"));

        if(attraction != null)
        {
            this.viewModel.attraction = attraction;
            this.viewModel.attraction.setCreditType(this.viewModel.creditType);
            this.viewModel.attraction.setCategory(this.viewModel.category);
            this.viewModel.attraction.setManufacturer(this.viewModel.manufacturer);
            this.viewModel.attraction.setStatus(this.viewModel.status);
            this.viewModel.attraction.setUntracktedRideCount(this.viewModel.untrackedRideCount);

            Log.d(Constants.LOG_TAG, String.format("EditAttractionActivity.createAttraction:: created %s", this.viewModel.attraction.getFullName()));

            success = true;
        }

        Log.d(Constants.LOG_TAG, String.format("EditAttractionActivity.createAttraction:: created successfuly [%S]", success));
        return success;
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("EditAttractionActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            Log.i(Constants.LOG_TAG, String.format("EditAttractionActivity.returnResult:: returning %s", this.viewModel.attraction));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.attraction.getUuid().toString());
        }
        else
        {
            Log.i(Constants.LOG_TAG, "EditAttractionActivity.returnResult:: no changes - returning no element");
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + this.getClass().getSimpleName());
        finish();
    }
}
