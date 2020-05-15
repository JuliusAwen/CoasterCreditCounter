package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.DatePicker;

import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.Attraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.OnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

public class CreateVisitActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener
{
    private CreateVisitViewModel viewModel;


    protected void setContentView()
    {
        setContentView(R.layout.activity_create_visit);
    }

    protected void create()
    {
        this.viewModel = new ViewModelProvider(this).get(CreateVisitViewModel.class);

        if(this.viewModel.park == null)
        {
            this.viewModel.park = (Park) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        super.createToolbar();
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

        Log.i(String.format("requestCode[%s], resultCode[%s]", RequestCode.getValue(requestCode), StringTool.resultCodeToString(resultCode)));

        if(resultCode == RESULT_OK)
        {
            if(requestCode == RequestCode.PICK_ATTRACTIONS.ordinal())
            {
                List<IElement> resultElements = ResultFetcher.fetchResultElements(data);

                for(IElement element : resultElements)
                {
                    VisitedAttraction visitedAttraction = VisitedAttraction.create((OnSiteAttraction) element);
                    this.viewModel.visit.addChildAndSetParent(visitedAttraction);
                    super.markForCreation(visitedAttraction);
                }

                super.markForUpdate(this.viewModel.visit);

                this.returnResult(RESULT_OK);
            }
        }
        else if(resultCode == RESULT_CANCELED)
        {
            returnResult(RESULT_OK);
        }
    }

    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle(this.viewModel.visit != null ? this.viewModel.visit.getName() : getString(R.string.title_create_visit), this.viewModel.park.getName());
    }

    private void pickDate()
    {
        Log.i(String.format("picking date for visit in %s", this.viewModel.park));

        this.viewModel.calendar = Calendar.getInstance();
        int year = this.viewModel.calendar.get(Calendar.YEAR);
        int month = this.viewModel.calendar.get(Calendar.MONTH);
        int day = this.viewModel.calendar.get(Calendar.DAY_OF_MONTH);

        this.viewModel.datePickerDialog = new DatePickerDialog(CreateVisitActivity.this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day)
            {
                Log.v(String.format(Locale.getDefault(), "picked date: year[%d], month[%d], day[%d]", year, month, day));
                viewModel.calendar.set(year, month, day);
                viewModel.datePicked = true;

                List<IElement> existingVisits = Visit.fetchVisitsForYearAndDay(viewModel.calendar, viewModel.park.getChildrenAsType(Visit.class));
                viewModel.existingVisit = existingVisits.isEmpty() ? null : (Visit) existingVisits.get(0);
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
                        returnResult(RESULT_OK);
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
                    returnResult(RESULT_CANCELED);
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
        Log.d(String.format("creating visit for %s", this.viewModel.park));

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
        Log.d(String.format("deleting %s", this.viewModel.existingVisit.getFullName()));

        super.markForUpdate(this.viewModel.park);
        super.markForDeletion(this.viewModel.existingVisit, true);
        this.viewModel.existingVisit = null;
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
                            new ArrayList<IElement>(viewModel.park.getChildrenAsType(OnSiteAttraction.class)));
                }
                else if(which == DialogInterface.BUTTON_NEGATIVE)
                {
                    returnResult(RESULT_OK);
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
                        returnResult(RESULT_OK);
                    }
                }
                else if(which == DialogInterface.BUTTON_NEGATIVE)
                {
                    returnResult(RESULT_CANCELED);
                }
                break;
            }
        }
    }

    private void returnResult(int resultCode)
    {
        Log.i(String.format("resultCode[%s]", StringTool.resultCodeToString(resultCode)));

        Intent intent = new Intent();
        if(resultCode == RESULT_OK)
        {
            Log.i(String.format("returning %s", this.viewModel.visit));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.visit.getUuid().toString());
        }

        setResult(resultCode, intent);
        super.synchronizePersistency(); // has to be called manually because after calling finish() BaseActivity.onPause() is not called for some strange reason...

        Log.frame(LogLevel.INFO, String.format("finishing [%s]", this.getClass().getSimpleName()), '+', false);
        finish();
    }
}