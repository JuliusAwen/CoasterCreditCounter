package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.ConfirmSnackbar;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.IConfirmSnackbarClient;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.tools.menuAgents.PopupItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.PopupMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.customViews.CoordinatorLayoutWithMaxHeight;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

import static de.juliusawen.coastercreditcounter.application.Constants.LOG_TAG;

public class ShowOnSiteAttractionActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener, IConfirmSnackbarClient
{
    private ShowOnSiteAttractionActivityViewModel viewModel;

    private CoordinatorLayoutWithMaxHeight layoutNote;
    private TextView textViewNote;

    @Override
    protected void setContentView()
    {
        setContentView(R.layout.activity_show_on_site_attraction);
    }

    @Override
    protected void create()
    {
        this.viewModel = new ViewModelProvider(this).get(ShowOnSiteAttractionActivityViewModel.class);

        if(this.viewModel.onSiteAttraction == null)
        {
            this.viewModel.onSiteAttraction = (IOnSiteAttraction) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        if(this.viewModel.optionsMenuAgent == null)
        {
            this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
        }

        super.createHelpOverlayFragment(getString(R.string.title_help, getString(R.string.title_show_on_site_attraction)), getString(R.string.help_text_show_on_site_attraction));
        super.createToolbar()
                .addToolbarHomeButton()
                .setToolbarTitleAndSubtitle(this.viewModel.onSiteAttraction.getName(), this.viewModel.onSiteAttraction.getParent().getName());

        super.createFloatingActionButton();
        this.decorateFloatingActionButton();

        this.createNoteLayout();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(LOG_TAG, String.format("ShowInSiteAttraction.onActivityResult:: requestCode[%s], resultCode[%s]", RequestCode.getValue(requestCode), resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            IElement resultElement = ResultFetcher.fetchResultElement(data);

            switch(RequestCode.values()[requestCode])
            {
                case CREATE_NOTE:
                {
                    this.viewModel.onSiteAttraction.addChildAndSetParent((resultElement));
                    super.markForUpdate(this.viewModel.onSiteAttraction);
                    break;
                }

                case EDIT_NOTE:
                {
                    super.markForUpdate(this.viewModel.onSiteAttraction);
                    break;
                }
            }
        }
    }

    @Override
    protected void resume()
    {
        invalidateOptionsMenu();
        this.handleNoteRelatedViews();
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_comment, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(LOG_TAG, "ShowOnSiteAttractionActivity.onClickFloatingActionButton:: FloatingActionButton pressed");

                if(viewModel.onSiteAttraction.getNote() != null)
                {
                    ActivityDistributor.startActivityEditForResult(ShowOnSiteAttractionActivity.this, RequestCode.EDIT_NOTE, viewModel.onSiteAttraction.getNote());
                }
                else
                {
                    ActivityDistributor.startActivityCreateForResult(ShowOnSiteAttractionActivity.this, RequestCode.CREATE_NOTE, ShowOnSiteAttractionActivity.this.viewModel.onSiteAttraction);
                }
            }
        });
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
        ActivityDistributor.startActivityEditForResult(this, RequestCode.EDIT_NOTE, this.viewModel.onSiteAttraction.getNote());
    }

    private boolean onNoteLongClick()
    {
        PopupMenuAgent.getMenu()
                .add(PopupItem.DELETE_ELEMENT)
                .show(this, this.layoutNote);

        return true;
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
                            getString(R.string.alert_dialog_message_confirm_delete, this.viewModel.onSiteAttraction.getNote().getName()),
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
                                getString(R.string.action_confirm_delete_text, this.viewModel.onSiteAttraction.getNote().getName()),
                                Snackbar.LENGTH_LONG),
                        requestCode,
                        this);
            }
        }
    }

    @Override
    public void handleActionConfirmed(RequestCode requestCode)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowParkOverviewFragment.handleActionConfirmed:: handling confirmed action [%s]", requestCode));

        if(requestCode == RequestCode.DELETE)
        {
            Log.i(Constants.LOG_TAG, String.format("ShowParkOverviewFragment.handleActionConfirmed:: deleting %s...", this.viewModel.onSiteAttraction.getNote()));

            super.markForUpdate(this.viewModel.onSiteAttraction);
            super.markForDeletion(this.viewModel.onSiteAttraction.getNote(), false);

            this.handleNoteRelatedViews();
        }
    }

    private void handleNoteRelatedViews()
    {
        if(this.viewModel.onSiteAttraction.getNote() != null)
        {
            this.textViewNote.setText(this.viewModel.onSiteAttraction.getNote().getText());
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
}
