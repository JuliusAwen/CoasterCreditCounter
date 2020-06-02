package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Model;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

public class EditAttractionActivity extends BaseActivity
{
    private EditAttractionViewModel viewModel;

    private TextInputLayout textInputLayoutAttractionName;
    private TextInputEditText textInputEditTextAttractionName;

    private LinearLayout layoutCreditType;
    private TextView textViewCreditType;
    private ImageView imageViewPickCreditType;

    private LinearLayout layoutCategory;
    private TextView textViewCategory;
    private ImageView imageViewPickCategory;

    private LinearLayout layoutManufacturer;
    private TextView textViewManufacturer;
    private ImageView imageViewPickManufacturer;

    private LinearLayout layoutModel;
    private TextView textViewBlueprint;
    private ImageView imageViewPickBlueprint;

    private LinearLayout layoutStatus;
    private TextView textViewStatus;
    private ImageView imageViewPickStatus;

    private LinearLayout layoutUntrackedRideCount;
    private TextInputLayout textInputLayoutUntrackedRideCount;
    private TextInputEditText textInputEditTextUntrackedRideCount;

    private Drawable pickIconBlack;
    private Drawable pickIconGrey;

    protected void setContentView()
    {
        setContentView(R.layout.activity_edit_attraction);
    }

    protected void create()
    {
        this.textInputLayoutAttractionName = findViewById(R.id.textInputLayoutCreateOrEditAttraction_AttractionName);
        this.textInputEditTextAttractionName = findViewById(R.id.textInputEditTextCreateOrEditAttraction_AttractionName);

        this.layoutCreditType = findViewById(R.id.linearLayoutCreateOrEditAttraction_CreditType);
        this.textViewCreditType = findViewById(R.id.textViewCreateOrEditAttraction_CreditType);
        this.imageViewPickCreditType = findViewById(R.id.imageViewCreateOrEditAttraction_PickCreditType);

        this.layoutCategory = findViewById(R.id.linearLayoutCreateOrEditAttraction_Category);
        this.textViewCategory = findViewById(R.id.textViewCreateOrEditAttraction_Category);
        this.imageViewPickCategory = findViewById(R.id.imageViewCreateOrEditAttraction_PickCategory);

        this.layoutManufacturer = findViewById(R.id.linearLayoutCreateOrEditAttraction_Manufacturer);
        this.textViewManufacturer = findViewById(R.id.textViewCreateOrEditAttraction_Manufacturer);
        this.imageViewPickManufacturer = findViewById(R.id.imageViewCreateOrEditAttraction_PickManufacturer);

        this.layoutModel = findViewById(R.id.linearLayoutCreateOrEditAttraction_Model);
        this.textViewBlueprint = findViewById(R.id.textViewCreateOrEditAttraction_Model);
        this.imageViewPickBlueprint = findViewById(R.id.imageViewCreateOrEditAttraction_PickModel);

        this.layoutStatus = findViewById(R.id.linearLayoutCreateOrEditAttraction_Status);
        this.textViewStatus = findViewById(R.id.textViewCreateOrEditAttraction_Status);
        this.imageViewPickStatus = findViewById(R.id.imageViewCreateOrEditAttraction_PickStatus);

        this.layoutUntrackedRideCount = findViewById(R.id.linearLayoutCreateOrEditAttraction_UntrackedRideCount);
        this.textInputLayoutUntrackedRideCount = findViewById(R.id.textInputLayoutCreateOrEditAttractionName_UntrackedRideCount);
        this.textInputEditTextUntrackedRideCount = findViewById(R.id.textInputEditTextCreateOrEditAttraction_UntrackedRideCount);


        this.pickIconBlack = DrawableProvider.getColoredDrawableMutation(R.drawable.arrow_drop_down, R.color.black);
        this.pickIconGrey = DrawableProvider.getColoredDrawableMutation(R.drawable.arrow_drop_down, R.color.grey);


        this.viewModel = new ViewModelProvider(this).get(EditAttractionViewModel.class);

        if(this.viewModel.attraction == null)
        {
            this.viewModel.attraction = (IAttraction) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        this.createTextInputAttractionName(getString(R.string.hint_edit_name, this.viewModel.attraction.getName()));
        this.decorateLayoutCreditType();
        this.decorateLayoutCategory();
        this.decorateLayoutManufacturer();
        this.decorateLayoutModel();
        this.decorateLayoutStatus();
        this.createTextInputUntrackedRideCount();

        super.createHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_HELP_TITLE)), getIntent().getStringExtra(Constants.EXTRA_HELP_TEXT));
        super.createToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), null);

        super.createFloatingActionButton();
        this.decorateFloatingActionButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(String.format("%s, %s", RequestCode.getValue(requestCode), StringTool.resultCodeToString(resultCode)));

        if(resultCode == RESULT_OK) // Element was picked in PickElementsActivity
        {
            IElement pickedElement = ResultFetcher.fetchResultElement(data);
            if(pickedElement != null)
            {
                switch(RequestCode.getValue(requestCode))
                {
                    case PICK_CREDIT_TYPE:
                    {
                        this.updateLayoutCreditType((CreditType) pickedElement);
                        break;
                    }

                    case PICK_CATEGORY:
                    {
                        this.updateLayoutCategory((Category) pickedElement);
                        break;
                    }

                    case PICK_MANUFACTURER:
                    {
                        this.updateLayoutManufacturer((Manufacturer) pickedElement);
                        break;
                    }

                    case PICK_MODEL:
                    {
                        this.updateLayoutModel((Model) pickedElement);
                        break;
                    }

                    case PICK_STATUS:
                    {
                        this.updateLayoutStatus((Status)pickedElement);
                        break;
                    }
                }

                Log.i(String.format("picked %s", pickedElement));
            }
            else
            {
                switch(RequestCode.getValue(requestCode)) //it is possible that element was deleted, so it has to be evaluated if it is still existing
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

                    case PICK_MODEL:
                    {
                        if(!App.content.containsElement(this.viewModel.model))
                        {
                            this.updateLayoutModel(Model.getDefault());
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
        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.check, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handleEditAttraction();
            }
        });

        super.setFloatingActionButtonVisibility(true);
    }

    private void handleEditAttraction()
    {
        if(this.textInputLayoutAttractionName.getError() != null || this.textInputLayoutUntrackedRideCount.getError() != null)
        {
            Log.w("some input is invalid");
            return;
        }

        //this has to happen before name is set (otherwise if something is wrong, the name would already be changed)
        int untrackedRideCount = this.fetchUntrackedRideCountFromTextInput();
        if(untrackedRideCount < 0)
        {
            Log.w("entered untracked ride count is invalid");
            this.textInputLayoutUntrackedRideCount.setError(getString(R.string.error_number_invalid));
            return;
        }
        else
        {
            this.viewModel.untrackedRideCount = untrackedRideCount;
        }


        boolean somethingChanged = false;

        String name = this.textInputEditTextAttractionName.getText().toString();
        Log.v(String.format("attraction name entered [%s]", name));
        if(!this.viewModel.attraction.getName().equals(name.trim()))
        {
            if(this.viewModel.attraction.setName(name))
            {
                Log.i(String.format("changed name to [%s]", this.viewModel.attraction.getName()));
                somethingChanged = true;
            }
            else
            {
                Log.w(String.format("name [%s] is invalid", name));
                this.textInputLayoutAttractionName.setError(getString(R.string.error_name_invalid));
                return;
            }
        }


        if(this.viewModel.attraction.getUntracktedRideCount() != this.viewModel.untrackedRideCount)
        {
            this.viewModel.attraction.setUntracktedRideCount(this.viewModel.untrackedRideCount);

            Log.d(String.format(Locale.getDefault(), "untracked ride count set to [%d]", this.viewModel.attraction.getUntracktedRideCount()));
            somethingChanged = true;
        }

        if(this.viewModel.creditType != null)
        {
            if(this.trySetCreditType(this.viewModel.creditType))
            {
                somethingChanged = true;
            }
        }

        if(this.viewModel.category != null)
        {
            if(this.trySetCategory(this.viewModel.category))
            {
                somethingChanged = true;
            }
        }

        if(this.viewModel.manufacturer != null)
        {
            if(this.trySetManufacturer(this.viewModel.manufacturer))
            {
                somethingChanged = true;
            }
        }

        if(!this.viewModel.attraction.getModel().equals(this.viewModel.model))
        {
            this.viewModel.attraction.setModel(this.viewModel.model);
            Log.d(String.format("changed %s's Model to %s", this.viewModel.attraction, this.viewModel.attraction.getModel()));
            somethingChanged = true;
        }

        if(!this.viewModel.attraction.getStatus().equals(this.viewModel.status))
        {
            this.viewModel.attraction.setStatus(this.viewModel.status);
            Log.d(String.format("changed %s's Status to %s",
                    this.viewModel.attraction, this.viewModel.attraction.getStatus()));
            somethingChanged = true;
        }

        if(somethingChanged)
        {
            super.markForUpdate(this.viewModel.attraction);
            this.returnResult(RESULT_OK);
        }
        else
        {
            Log.d("nothing changed - cancel");
            this.returnResult(RESULT_CANCELED);
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
                Log.e(String.format("catched NumberFormatException parsing untracked ride count: [%s]", nfe));
            }
        }

        return untrackedRideCount;
    }

    private void createTextInputAttractionName(String hint)
    {
        this.textInputLayoutAttractionName.setHint(hint);
        this.textInputLayoutAttractionName.setError(null);
        this.textInputLayoutAttractionName.setCounterMaxLength(App.config.maxCharacterCountForSimpleElementName);

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
                    textInputLayoutAttractionName.setError(EditAttractionActivity.this.getString(R.string.error_character_count_exceeded,
                            textInputLayoutAttractionName.getCounterMaxLength()));
                }
                else
                {
                    textInputLayoutAttractionName.setError(null);
                }
            }
        });

        this.textInputEditTextAttractionName.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean hasFocus)
            {
                if(!hasFocus)
                {
                    if(textInputEditTextAttractionName.getText().toString().trim().isEmpty())
                    {
                        decorateTextInputAttractionName();
                    }
                }
            }
        });

        this.decorateTextInputAttractionName();
    }

    private boolean trySetCreditType(CreditType creditType)
    {
        if(this.viewModel.attraction.getCreditType().equals(creditType))
        {
            return false;
        }

        this.viewModel.attraction.setCreditType(creditType);
        Log.d(String.format("changed %s's CreditType to %s", this.viewModel.attraction, this.viewModel.attraction.getCreditType()));

        return true;
    }

    private boolean trySetCategory(Category category)
    {
        if(this.viewModel.attraction.getCategory().equals(category))
        {
            return false;
        }

        this.viewModel.attraction.setCategory(category);
        Log.d(String.format("changed %s's Category to %s", this.viewModel.attraction, this.viewModel.attraction.getCategory()));

        return true;
    }

    private boolean trySetManufacturer(Manufacturer manufacturer)
    {
        if(this.viewModel.attraction.getManufacturer().equals(manufacturer))
        {
            return false;
        }

        this.viewModel.attraction.setManufacturer(manufacturer);
        Log.d(String.format("changed %s's Manufacturer to %s", this.viewModel.attraction, this.viewModel.attraction.getManufacturer()));

        return true;
    }

    private void decorateTextInputAttractionName()
    {
        this.textInputEditTextAttractionName.setText(this.viewModel.attraction.getName());
        this.textInputEditTextAttractionName.setSelection(this.viewModel.attraction.getName().length());
    }

    private void decorateLayoutCreditType()
    {
        this.layoutCreditType.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d("<PickCreditType> selected");

                if(viewModel.model.isCreditTypeSet())
                {
                    Toaster.makeShortToast(EditAttractionActivity.this, getString(R.string.error_property_is_tied_to_model, getString(R.string.credit_type)));
                }
                else
                {
                    ActivityDistributor.startActivityPickForResult(EditAttractionActivity.this, RequestCode.PICK_CREDIT_TYPE, App.content.getContentOfType(CreditType.class));
                }
            }
        });

        this.updateLayoutCreditType(this.viewModel.attraction.getCreditType());
    }

    private void updateLayoutCreditType(CreditType creditType)
    {
        if(creditType == null)
        {
            creditType = CreditType.getDefault();
        }

        Log.v(String.format("setting CreditType %s...", creditType));

        this.viewModel.creditType = creditType;

        this.textViewCreditType.setText(this.viewModel.creditType.getName());
        this.textViewCreditType.setTextColor(getColor(R.color.black));
        this.imageViewPickCreditType.setImageDrawable(App.content.getContentOfType(CreditType.class).size() > 1 ? this.pickIconBlack : this.pickIconGrey);
    }

    private void decorateLayoutCategory()
    {
        this.layoutCategory.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("<PickCategory> selected");

                if(viewModel.model.isCategorySet())
                {
                    Toaster.makeShortToast(EditAttractionActivity.this, getString(R.string.error_property_is_tied_to_model, getString(R.string.category)));
                }
                else
                {
                    ActivityDistributor.startActivityPickForResult(EditAttractionActivity.this, RequestCode.PICK_CATEGORY, App.content.getContentOfType(Category.class));
                }
            }
        });

        this.updateLayoutCategory(this.viewModel.attraction.getCategory());
    }

    private void updateLayoutCategory(Category category)
    {
        if(category == null)
        {
            category = Category.getDefault();
        }

        Log.v(String.format("setting Category %s", category));

        this.viewModel.category = category;

        this.textViewCategory.setText(this.viewModel.category.getName());
        this.textViewCategory.setTextColor(getColor(R.color.black));
        this.imageViewPickCategory.setImageDrawable(App.content.getContentOfType(Category.class).size() > 1 ? this.pickIconBlack : this.pickIconGrey);
    }

    private void decorateLayoutManufacturer()
    {
        this.layoutManufacturer.setOnClickListener((new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("<PickManufacturer> selected");

                if(viewModel.model.isManufacturerSet())
                {
                    Toaster.makeShortToast(EditAttractionActivity.this, getString(R.string.error_property_is_tied_to_model, getString(R.string.manufacturer)));
                }
                else
                {
                    ActivityDistributor.startActivityPickForResult(EditAttractionActivity.this, RequestCode.PICK_MANUFACTURER, App.content.getContentOfType(Manufacturer.class));
                }
            }
        }));

        this.updateLayoutManufacturer(this.viewModel.attraction.getManufacturer());
    }

    private void updateLayoutManufacturer(Manufacturer manufacturer)
    {
        if(manufacturer == null)
        {
            manufacturer = Manufacturer.getDefault();
        }

        Log.v(String.format("setting Manufacturer %s", manufacturer));

        this.viewModel.manufacturer = manufacturer;

        this.textViewManufacturer.setText(this.viewModel.manufacturer.getName());
        this.textViewManufacturer.setTextColor(getColor(R.color.black));
        this.imageViewPickManufacturer.setImageDrawable(App.content.getContentOfType(Manufacturer.class).size() > 1 ? this.pickIconBlack : this.pickIconGrey);
    }

    private void decorateLayoutModel()
    {
        this.layoutModel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d("<PickModel> selected");
                ActivityDistributor.startActivityPickForResult(EditAttractionActivity.this, RequestCode.PICK_MODEL, App.content.getContentOfType(Model.class));
            }
        });

        this.updateLayoutModel(this.viewModel.attraction.getModel());
    }

    private void updateLayoutModel(Model model)
    {
        Log.v(String.format("setting Model %s", model));

        this.viewModel.model = model;

        this.textViewBlueprint.setText(model.getName());

        if(this.viewModel.model.isCreditTypeSet())
        {
            this.viewModel.creditType = null;
            this.textViewCreditType.setText(model.getCreditType().getName());
            this.textViewCreditType.setTextColor(getColor(R.color.grey));
            this.imageViewPickCreditType.setImageDrawable(this.pickIconGrey);
        }
        else
        {
            this.updateLayoutCreditType(this.viewModel.creditType);
        }

        if(this.viewModel.model.isCategorySet())
        {
            this.viewModel.category = null;
            this.textViewCategory.setText(model.getCategory().getName());
            this.textViewCategory.setTextColor(getColor(R.color.grey));
            this.imageViewPickCategory.setImageDrawable(this.pickIconGrey);
        }
        else
        {
            this.updateLayoutCategory(this.viewModel.category);
        }

        if(this.viewModel.model.isManufacturerSet())
        {
            this.viewModel.manufacturer = null;
            this.textViewManufacturer.setText(model.getManufacturer().getName());
            this.textViewManufacturer.setTextColor(getColor(R.color.grey));
            this.imageViewPickManufacturer.setImageDrawable(this.pickIconGrey);
        }
        else
        {
            this.updateLayoutManufacturer(this.viewModel.manufacturer);
        }

        this.imageViewPickBlueprint.setImageDrawable(App.content.getContentOfType(Model.class).size() > 1 ? this.pickIconBlack : this.pickIconGrey);
    }

    private void decorateLayoutStatus()
    {
        this.layoutStatus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("<PickStatus> selected");
                ActivityDistributor.startActivityPickForResult(EditAttractionActivity.this, RequestCode.PICK_STATUS, App.content.getContentOfType(Status.class));
            }
        });

        this.updateLayoutStatus(this.viewModel.attraction.getStatus());
        this.layoutStatus.setVisibility(View.VISIBLE);
    }

    private void updateLayoutStatus(Status status)
    {
        Log.v(String.format("setting Status %s", status));

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
                if(editable.length() > textInputLayoutUntrackedRideCount.getCounterMaxLength())
                {
                    textInputLayoutUntrackedRideCount.setError(EditAttractionActivity.this.getString(R.string.error_digit_count_exceeded, 
                            textInputLayoutUntrackedRideCount.getCounterMaxLength()));
                }
                else if(editable.length() == 0)
                {
                    textInputLayoutUntrackedRideCount.setHint(getString(R.string.hint_edit_untracked_ride_count, viewModel.attraction.getUntracktedRideCount()));
                }
                else if(!editable.toString().trim().isEmpty() && Integer.parseInt(editable.toString()) < 0)
                {
                    textInputLayoutUntrackedRideCount.setError(EditAttractionActivity.this.getString(R.string.error_number_invalid));
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
        this.textInputEditTextUntrackedRideCount.setText(String.valueOf(this.viewModel.attraction.getUntracktedRideCount()));
    }

    private TextView.OnEditorActionListener getOnEditorActionListener()
    {
        return new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                Log.i(String.format(Locale.getDefault(), "actionId[%d]", actionId));

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
        Log.i(String.format("%s", StringTool.resultCodeToString(resultCode)));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            Log.i(String.format("returning %s", this.viewModel.attraction));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.attraction.getUuid().toString());
        }
        else
        {
            Log.i("no changes - returning no element");
        }

        setResult(resultCode, intent);
        Log.frame(LogLevel.INFO, String.format("finishing [%s]", this.getClass().getSimpleName()), '+', false);
        finish();
    }
}