package de.juliusawen.coastercreditcounter.frontend.visits;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import androidx.lifecycle.ViewModelProviders;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Park;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.objects.temporaryElements.VisitedAttraction;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.fragments.AlertDialogFragment;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.ResultTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class CreateVisitActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener
{
    private CreateVisitActivityViewModel viewModel;

    private static final int ALERT_DIALOG_PICK_ATTRACTIONS = 0;
    private static final int ALERT_DIALOG_VISIT_ALREADY_EXISTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "CreateVisitActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_create_visit);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            this.viewModel = ViewModelProviders.of(this).get(CreateVisitActivityViewModel.class);

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
    }

    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle(this.viewModel.visit != null ? this.viewModel.visit.getName() : getString(R.string.title_visit_create), this.viewModel.park.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("CreateVisitActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == Constants.REQUEST_CODE_PICK_ATTRACTIONS)
            {
                List<IElement> resultElements = ResultTool.fetchResultElements(data);

                for(IElement element : resultElements)
                {
                    VisitedAttraction visitedAttraction = VisitedAttraction.create((IOnSiteAttraction) element);
                    this.viewModel.visit.addChildAndSetParent(visitedAttraction);
                    App.content.addElement(visitedAttraction); //cannot mark for creation --> is TemporaryElement
                }

                this.returnResult(Activity.RESULT_OK);
            }
        }
        else if(resultCode == Activity.RESULT_CANCELED)
        {
            returnResult(Activity.RESULT_OK);
        }
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

                viewModel.existingVisit = getExistingVisit(viewModel.calendar);
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

        this.viewModel.datePickerDialog.getDatePicker().setFirstDayOfWeek(App.settings.getFirstDayOfTheWeek());
        this.viewModel.datePickerDialog.setCancelable(false);
        this.viewModel.datePickerDialog.setCanceledOnTouchOutside(false);
        this.viewModel.datePickerDialog.show();
    }

    private Visit getExistingVisit(Calendar calendar)
    {
        for(Visit exisingVisit : viewModel.park.getChildrenAsType(Visit.class))
        {
            if(Visit.isSameDay(exisingVisit.getCalendar(), calendar))
            {
                Log.v(Constants.LOG_TAG, String.format("CreateVisitActivity.getExistingVisit:: %s already exists", exisingVisit));
                return exisingVisit;
            }
        }
        return null;
    }

    private void showVisitAlreadyExistsDialog()
    {
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(
                R.drawable.ic_baseline_warning,
                getString(R.string.alert_dialog_title_visit_already_exists),
                getString(R.string.alert_dialog_message_visit_already_exists),
                getString(R.string.text_accept),
                getString(R.string.text_cancel),
                ALERT_DIALOG_VISIT_ALREADY_EXISTS,
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

//        if(Visit.isSameDay(this.viewModel.visit.getCalendar(), Calendar.getInstance()))
//        {
//            Log.i(Constants.LOG_TAG, "CreateVisitActivity.pickDate:: created visit is today - set as open visit");
//            Visit.setOpenVisit(this.viewModel.visit);
//        }

        this.decorateToolbar();
    }

    private void deleteExistingVisit()
    {
        Log.d(Constants.LOG_TAG, String.format("CreateVisitActivity.deleteExistingVisit:: deleting %s", this.viewModel.existingVisit.getFullName()));

        int counter = 0;
        for(VisitedAttraction visitedAttraction : this.viewModel.existingVisit.getChildrenAsType(VisitedAttraction.class))
        {
            IOnSiteAttraction onSiteAttraction = visitedAttraction.getOnSiteAttraction();
            if(onSiteAttraction.getTotalRideCount() > 0 && visitedAttraction.getRideCount() > 0)
            {
                onSiteAttraction.decreaseTotalRideCount(visitedAttraction.getRideCount());

                super.markForUpdate(onSiteAttraction);

                counter ++;
            }
        }
        if(counter > 0)
        {
            Toaster.makeToast(this, getString(R.string.information_decreased_ride_count, counter));
        }

        super.markForDeletion(this.viewModel.existingVisit, true);
        super.markForUpdate(this.viewModel.park);

        this.viewModel.park.deleteChild(this.viewModel.existingVisit);
        this.viewModel.existingVisit = null;
    }

    private void showPickAttractionsDialog()
    {
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(
                R.drawable.ic_baseline_notification_important,
                getString(R.string.alert_dialog_title_add_attractions_to_visit),
                getString(R.string.alert_dialog_message_add_attractions_to_visit),
                getString(R.string.text_accept),
                getString(R.string.text_cancel), ALERT_DIALOG_PICK_ATTRACTIONS,
                false
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
            case ALERT_DIALOG_PICK_ATTRACTIONS:
            {
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    ActivityTool.startActivityPickForResult(
                            CreateVisitActivity.this,
                            Constants.REQUEST_CODE_PICK_ATTRACTIONS,
                            new ArrayList<IElement>(viewModel.park.getChildrenAsType(IOnSiteAttraction.class)));
                }
                else if(which == DialogInterface.BUTTON_NEGATIVE)
                {
                    returnResult(Activity.RESULT_OK);
                }
                break;
            }
            case ALERT_DIALOG_VISIT_ALREADY_EXISTS:
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
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
        finish();
    }
}
