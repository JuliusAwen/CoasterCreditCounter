package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.DatePicker;

import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.Attraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

public class CreateVisitActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener
{
    private CreateVisitActivityViewModel viewModel;


    protected void setContentView()
    {
        setContentView(R.layout.activity_create_visit);
    }

    protected void create()
    {
        this.viewModel = new ViewModelProvider(this).get(CreateVisitActivityViewModel.class);

        if(this.viewModel.park == null)
        {
            this.viewModel.park = (Park) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        super.addToolbar();
        super.addToolbarHomeButton();

        this.decorateToolbar();

        if(!this.viewModel.datePicked)
        {
            this.pickDate();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(Constants.LOG_TAG, String.format("CreateVisitActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == RequestCode.PICK_ATTRACTIONS.ordinal())
            {
                List<IElement> resultElements = ResultFetcher.fetchResultElements(data);

                for(IElement element : resultElements)
                {
                    VisitedAttraction visitedAttraction = VisitedAttraction.create((IOnSiteAttraction) element);
                    this.viewModel.visit.addChildAndSetParent(visitedAttraction);
                    App.content.addElement(visitedAttraction);
                }

                super.markForUpdate(this.viewModel.visit);

                this.returnResult(Activity.RESULT_OK);
            }

        }
        else if(resultCode == Activity.RESULT_CANCELED)
        {
            returnResult(Activity.RESULT_OK);
        }
    }

    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle(this.viewModel.visit != null ? this.viewModel.visit.getName() : getString(R.string.title_create_visit), this.viewModel.park.getName());
    }

    private void pickDate()
    {
        Log.i(Constants.LOG_TAG, String.format("CreateVisitActivity.pickDate:: picking date for visit in %s", this.viewModel.park));

        this.viewModel.calendar = Calendar.getInstance();
        int year = this.viewModel.calendar.get(Calendar.YEAR);
        int month = this.viewModel.calendar.get(Calendar.MONTH);
        int day = this.viewModel.calendar.get(Calendar.DAY_OF_MONTH);

        this.viewModel.datePickerDialog = new DatePickerDialog(CreateVisitActivity.this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day)
            {
                Log.v(Constants.LOG_TAG, String.format("CreateVisitActivity.onDateSet:: picked date: year[%d], month[%d], day[%d]", year, month, day));
                viewModel.calendar.set(year, month, day);
                viewModel.datePicked = true;


                List<Visit> existingVisits = Visit.fetchVisitsForYearAndDay(viewModel.calendar, viewModel.park.fetchChildrenAsType(Visit.class));
                viewModel.existingVisit = existingVisits.isEmpty() ? null : existingVisits.get(0);
                if(viewModel.existingVisit != null)
                {
                    viewModel.datePickerDialog.dismiss();
                    showVisitAlreadyExistsDialog();
                }
                else
                {
                    viewModel.datePickerDialog.dismiss();
                    createVisit(viewModel.calendar);

                    if(viewModel.park.hasChildrenOfType(Attraction.class))
                    {
                        showPickAttractionsDialog();
                    }
                    else
                    {
                        returnResult(Activity.RESULT_OK);
                    }
                }
            }
        }, year, month, day);

        this.viewModel.datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.text_cancel), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int position)
            {
                viewModel.datePickerDialog.dismiss();
                if (position == DialogInterface.BUTTON_NEGATIVE)
                {
                    returnResult(Activity.RESULT_CANCELED);
                }
            }
        });

        this.viewModel.datePickerDialog.getDatePicker().setFirstDayOfWeek(App.preferences.getFirstDayOfTheWeek());
        this.viewModel.datePickerDialog.setCancelable(false);
        this.viewModel.datePickerDialog.setCanceledOnTouchOutside(false);
        this.viewModel.datePickerDialog.show();
    }

    private void showVisitAlreadyExistsDialog()
    {
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(
                R.drawable.ic_baseline_warning,
                getString(R.string.alert_dialog_title_visit_already_exists),
                getString(R.string.alert_dialog_message_visit_already_exists),
                getString(R.string.text_accept),
                getString(R.string.text_cancel),
                RequestCode.HANDLE_EXISTING_VISIT,
                false
        );
        alertDialogFragment.setCancelable(false);
        alertDialogFragment.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
    }

    private void createVisit(Calendar calendar)
    {
        Log.d(Constants.LOG_TAG, String.format("CreateVisitActivity.createVisit:: creating visit for %s", this.viewModel.park));

        if(this.viewModel.existingVisit != null)
        {
            this.deleteExistingVisit();
        }

        this.viewModel.visit = Visit.create(calendar, null);
        this.viewModel.park.addChildAndSetParent(this.viewModel.visit);

        super.markForCreation(this.viewModel.visit);
        super.markForUpdate(this.viewModel.park);

        this.decorateToolbar();
    }

    private void deleteExistingVisit()
    {
        Log.d(Constants.LOG_TAG, String.format("CreateVisitActivity.deleteExistingVisit:: deleting %s", this.viewModel.existingVisit.getFullName()));

        super.markForDeletion(this.viewModel.existingVisit, true);
        this.viewModel.existingVisit.deleteElementAndDescendants();
        this.viewModel.existingVisit = null;

        super.markForUpdate(this.viewModel.park);
    }

    private void showPickAttractionsDialog()
    {
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(
                R.drawable.ic_baseline_notification_important,
                getString(R.string.alert_dialog_title_add_attractions),
                getString(R.string.alert_dialog_message_confirm_add_attractions_to_visit),
                getString(R.string.text_yes),
                getString(R.string.text_no),
                RequestCode.PICK_ATTRACTIONS,
                false
        );
        alertDialogFragment.setCancelable(false);
        alertDialogFragment.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
    }

    @Override
    public void handleAlertDialogClick(RequestCode requestCode, int which)
    {
        switch(requestCode)
        {
            case PICK_ATTRACTIONS:
            {
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    ActivityDistributor.startActivityPickForResult(
                            CreateVisitActivity.this,
                            RequestCode.PICK_ATTRACTIONS,
                            new ArrayList<IElement>(viewModel.park.fetchChildrenAsType(IOnSiteAttraction.class)));
                }
                else if(which == DialogInterface.BUTTON_NEGATIVE)
                {
                    returnResult(Activity.RESULT_OK);
                }
                break;
            }

            case HANDLE_EXISTING_VISIT:
            {
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    this.createVisit(viewModel.calendar);

                    if(this.viewModel.park.getChildCountOfType(Attraction.class) > 0)
                    {
                        showPickAttractionsDialog();
                    }
                    else
                    {
                        returnResult(Activity.RESULT_OK);
                    }
                }
                else if(which == DialogInterface.BUTTON_NEGATIVE)
                {
                    returnResult(Activity.RESULT_CANCELED);
                }
                break;
            }
        }
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("CreateVisitActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();
        if(resultCode == RESULT_OK)
        {
            Log.i(Constants.LOG_TAG, String.format("CreateVisitActivity.returnResult:: returning %s", this.viewModel.visit));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.visit.getUuid().toString());
        }

        setResult(resultCode, intent);
        super.synchronizePersistency(); // has to be called manually because after calling finish() BaseActivity.onPause() is not called for some strange reason...

        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + this.getClass().getSimpleName());
        finish();
    }
}
