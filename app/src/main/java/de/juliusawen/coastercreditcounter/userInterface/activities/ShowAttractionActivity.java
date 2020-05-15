package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.OnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.ConfirmSnackbar;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.IConfirmSnackbarClient;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;
import de.juliusawen.coastercreditcounter.tools.menuTools.PopupItem;
import de.juliusawen.coastercreditcounter.tools.menuTools.PopupMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.customViews.FrameLayoutWithMaxHeight;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

public class ShowAttractionActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener, IConfirmSnackbarClient
{
    private ShowAttractionActivityViewModel viewModel;

    private TextView textViewAttractionDetailCreditType;
    private TextView textViewAttractionDetailCategory;
    private TextView textViewAttractionDetailManufacturer;
    private TextView textViewAttractionDetailModel;
    private TextView textViewAttractionDetailStatus;
    private TextView textViewAttractionDetailTotalRideCount;

    private FrameLayoutWithMaxHeight layoutNote;
    private TextView textViewNote;

    @Override
    protected void setContentView()
    {
        setContentView(R.layout.activity_show_attraction);
    }

    @Override
    protected void create()
    {
        this.viewModel = new ViewModelProvider(this).get(ShowAttractionActivityViewModel.class);

        if(this.viewModel.attraction == null)
        {
            this.viewModel.attraction = (OnSiteAttraction) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        super.createHelpOverlayFragment(getString(R.string.title_help, getString(R.string.help_title_show_attraction)), getString(R.string.help_text_show_attraction));
        super.createToolbar();
        super.addToolbarHomeButton();

        super.createFloatingActionButton();
        this.decorateFloatingActionButton();

        this.createAttractionDetailsLayout();
        this.createNoteLayout();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(String.format("requestCode[%s], resultCode[%s]", RequestCode.getValue(requestCode), resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            IElement resultElement = ResultFetcher.fetchResultElement(data);

            switch(RequestCode.values()[requestCode])
            {
                case EDIT_ATTRACTION:
                {
                    if(!resultElement.getName().equals(this.viewModel.formerAttractionName))
                    {
                        Log.d(String.format("<EditAttraction> %s's name has changed'", this.viewModel.formerAttractionName));

                        for(IElement visit : resultElement.getParent().getChildrenOfType(Visit.class))
                        {
                            for(IElement visitedAttraction : visit.getChildrenOfType(VisitedAttraction.class))
                            {
                                if(visitedAttraction.getName().equals(this.viewModel.formerAttractionName))
                                {
                                    visitedAttraction.setName(resultElement.getName());
                                    Log.i(String.format("<EditAttraction>:: renamed VisitedAttraction %s to %s", this.viewModel.formerAttractionName, visitedAttraction));
                                }
                            }
                        }

                        super.markForUpdate(resultElement.getParent());
                    }
                    this.viewModel.formerAttractionName = null;
                    this.decorateAttractionDetailsLayout();

                    break;
                }


                case CREATE_NOTE:
                    this.viewModel.attraction.addChildAndSetParent((resultElement));
                    super.markForUpdate(this.viewModel.attraction);
                    break;

                case EDIT_NOTE:
                    super.markForUpdate(this.viewModel.attraction);
                    break;
            }
        }
    }

    @Override
    protected void resume()
    {
        super.setToolbarTitleAndSubtitle(this.viewModel.attraction.getName(), this.viewModel.attraction.getParent().getName());

        invalidateOptionsMenu();

        this.decorateAttractionDetailsLayout();
        this.handleNoteRelatedViews();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            this.returnResult(RESULT_OK);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_comment, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i("FloatingActionButton pressed");

                if(viewModel.attraction.getNote() != null)
                {
                    ActivityDistributor.startActivityEditForResult(ShowAttractionActivity.this, RequestCode.EDIT_NOTE, viewModel.attraction.getNote());
                }
                else
                {
                    ActivityDistributor.startActivityCreateForResult(ShowAttractionActivity.this, RequestCode.CREATE_NOTE, ShowAttractionActivity.this.viewModel.attraction);
                }
            }
        });
    }

    @Override
    public void handlePopupItemClicked(PopupItem item)
    {
        if(item == PopupItem.DELETE_ELEMENT)
        {
            AlertDialogFragment alertDialogFragmentDelete =
                    AlertDialogFragment.newInstance(
                            R.drawable.ic_baseline_warning,
                            getString(R.string.alert_dialog_title_delete),
                            getString(R.string.alert_dialog_message_confirm_delete, this.viewModel.attraction.getNote().getName()),
                            getString(R.string.text_accept),
                            getString(R.string.text_cancel),
                            RequestCode.DELETE,
                            false);

            alertDialogFragmentDelete.setCancelable(false);
            alertDialogFragmentDelete.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
        }
    }

    @Override
    public void handleAlertDialogClick(RequestCode requestCode, int which)
    {
        if(which == DialogInterface.BUTTON_POSITIVE)
        {
            if(requestCode == RequestCode.DELETE)
            {
                super.setFloatingActionButtonVisibility(false);

                ConfirmSnackbar.Show(
                        Snackbar.make(
                                findViewById(android.R.id.content),
                                getString(R.string.action_confirm_delete_text, this.viewModel.attraction.getNote().getName()),
                                Snackbar.LENGTH_LONG),
                        requestCode,
                        this);
            }
        }
    }

    @Override
    public void handleActionConfirmed(RequestCode requestCode)
    {
        Log.i(String.format("handling confirmed action [%s]", requestCode));

        if(requestCode == RequestCode.DELETE)
        {
            Log.i(String.format("deleting %s...", this.viewModel.attraction.getNote()));

            super.markForUpdate(this.viewModel.attraction);
            super.markForDeletion(this.viewModel.attraction.getNote(), false);

            this.handleNoteRelatedViews();
        }
    }

    private void createAttractionDetailsLayout()
    {
        this.textViewAttractionDetailCreditType = findViewById(R.id.textViewAttractionDetails_CreditType);
        this.textViewAttractionDetailCategory = findViewById(R.id.textViewAttractionDetails_Category);
        this.textViewAttractionDetailManufacturer = findViewById(R.id.textViewAttractionDetails_Manufacturer);
        this.textViewAttractionDetailModel = findViewById(R.id.textViewAttractionDetails_Model);
        this.textViewAttractionDetailStatus = findViewById(R.id.textViewAttractionDetails_Status);
        this.textViewAttractionDetailTotalRideCount = findViewById(R.id.textViewAttractionDetails_TotalRideCount);

        findViewById(R.id.onClickViewAttractionDetail).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                viewModel.formerAttractionName = viewModel.attraction.getName();
                ActivityDistributor.startActivityEditForResult(ShowAttractionActivity.this, RequestCode.EDIT_ATTRACTION, viewModel.attraction);
            }
        });
    }

    private void decorateAttractionDetailsLayout()
    {
        this.textViewAttractionDetailCreditType.setText(StringTool.buildSpannableStringWithTypeface(
                String.format("%s %s", getString(R.string.header_credit_type), this.viewModel.attraction.getCreditType().getName()),
                getString(R.string.header_credit_type),
                Typeface.BOLD));

        this.textViewAttractionDetailCategory.setText(StringTool.buildSpannableStringWithTypeface(
                String.format("%s %s", getString(R.string.header_category), this.viewModel.attraction.getCategory().getName()),
                getString(R.string.header_category),
                Typeface.BOLD));

        this.textViewAttractionDetailManufacturer.setText(StringTool.buildSpannableStringWithTypeface(
                String.format("%s %s", getString(R.string.header_manufacturer), this.viewModel.attraction.getManufacturer().getName()),
                getString(R.string.header_manufacturer),
                Typeface.BOLD));

        this.textViewAttractionDetailModel.setText(StringTool.buildSpannableStringWithTypeface(
                String.format("%s %s", getString(R.string.header_model), this.viewModel.attraction.getModel().getName()),
                getString(R.string.header_model),
                Typeface.BOLD));

        this.textViewAttractionDetailStatus.setText(StringTool.buildSpannableStringWithTypeface(
                String.format("%s %s", getString(R.string.header_status), this.viewModel.attraction.getStatus().getName()),
                getString(R.string.header_status),
                Typeface.BOLD));

        this.textViewAttractionDetailTotalRideCount.setText(StringTool.buildSpannableStringWithTypeface(
                String.format(Locale.getDefault(), "%s %d", getString(R.string.header_total_ride_count), this.viewModel.attraction.fetchTotalRideCount()),
                getString(R.string.header_total_ride_count),
                Typeface.BOLD));
    }

    private void createNoteLayout()
    {
        this.layoutNote = findViewById(R.id.frameLayoutWithMaxHeightNote);
        this.layoutNote.setMaxHeight(ConvertTool.convertDpToPx(App.config.maxHeightForNoteInDP));

        this.textViewNote = findViewById(R.id.textViewNote);
        this.textViewNote.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onNoteClick();
            }
        });

        this.textViewNote.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                return onNoteLongClick();
            }
        });

        this.handleNoteRelatedViews();
    }

    private void onNoteClick()
    {
        ActivityDistributor.startActivityEditForResult(this, RequestCode.EDIT_NOTE, this.viewModel.attraction.getNote());
    }

    private boolean onNoteLongClick()
    {
        PopupMenuAgent.getMenu()
                .add(PopupItem.DELETE_ELEMENT)
                .show(this, this.layoutNote);

        return true;
    }

    private void handleNoteRelatedViews()
    {
        if(this.viewModel.attraction.getNote() != null)
        {
            this.textViewNote.setText(this.viewModel.attraction.getNote().getText());
            super.setFloatingActionButtonVisibility(false);
            this.layoutNote.setVisibility(View.VISIBLE);
        }
        else
        {
            this.textViewNote.setText("");
            super.setFloatingActionButtonVisibility(true);
            this.layoutNote.setVisibility(View.GONE);
        }
    }

    private void returnResult(int resultCode)
    {
        Log.i(String.format(Locale.getDefault(), "returning %s with ResultCode[%d]", this.viewModel.attraction, resultCode));

        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.attraction.getUuid().toString());

        setResult(resultCode, intent);
        Log.frame(LogLevel.INFO, String.format("finishing [%s]", this.getClass().getSimpleName()), '+', true);
        finish();
    }
}
