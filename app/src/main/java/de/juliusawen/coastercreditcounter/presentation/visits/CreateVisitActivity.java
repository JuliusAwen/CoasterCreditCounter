package de.juliusawen.coastercreditcounter.presentation.visits;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import androidx.lifecycle.ViewModelProviders;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.Utilities.AttractionCategoryHeaderProvider;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.data.elements.attractions.Attraction;
import de.juliusawen.coastercreditcounter.data.elements.attractions.StockAttraction;
import de.juliusawen.coastercreditcounter.data.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.presentation.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.fragments.AlertDialogFragment;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.ResultTool;

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

        this.viewModel = ViewModelProviders.of(this).get(CreateVisitActivityViewModel.class);

        if(this.viewModel.park == null)
        {
            this.viewModel.park = (Park) App.content.getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        if(this.viewModel.attractionCategoryHeaderProvider == null)
        {
            this.viewModel.attractionCategoryHeaderProvider = new AttractionCategoryHeaderProvider();
        }

        super.addToolbar();
        super.addToolbarHomeButton();
        this.decorateToolbar();

        if(!this.viewModel.datePicked)
        {
            this.pickDate();
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
            if(requestCode == Constants.REQUEST_PICK_ATTRACTIONS)
            {
                List<Element> resultElements = ResultTool.fetchResultElements(data);

                for(Element element : resultElements)
                {
                    Element visitedAttraction = VisitedAttraction.create((StockAttraction)element);
                    this.viewModel.visit.addChildAndSetParent(visitedAttraction);
                    App.content.addElement(visitedAttraction);
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

                    if(viewModel.park.getChildCountOfType(Attraction.class) > 0)
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
                getString(R.string.alert_dialog_visit_already_exists_title),
                getString(R.string.alert_dialog_visit_already_exists_message),
                getString(R.string.text_accept),
                getString(R.string.text_cancel),
                ALERT_DIALOG_VISIT_ALREADY_EXISTS
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

        this.viewModel.visit = Visit.create(calendar);
        this.viewModel.park.addChildAndSetParent(this.viewModel.visit);
        App.content.addElement(this.viewModel.visit);

        if(Visit.isSameDay(this.viewModel.visit.getCalendar(), Calendar.getInstance()))
        {
            Log.i(Constants.LOG_TAG, "CreateVisitActivity.pickDate:: created visit is today - set as open visit");
            Visit.setOpenVisit(this.viewModel.visit);
        }

        this.decorateToolbar();
    }

    private void deleteExistingVisit()
    {
        Log.d(Constants.LOG_TAG, String.format("CreateVisitActivity.deleteExistingVisit:: deleting %s", this.viewModel.existingVisit));

        this.viewModel.park.deleteChild(this.viewModel.existingVisit);
        App.content.removeElement(this.viewModel.existingVisit);
        this.viewModel.existingVisit = null;
    }

    private void showPickAttractionsDialog()
    {
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(
                R.drawable.ic_baseline_notification_important,
                getString(R.string.alert_dialog_add_attractions_to_visit_title),
                getString(R.string.alert_dialog_add_attractions_to_visit_message),
                getString(R.string.text_accept),
                getString(R.string.text_cancel), ALERT_DIALOG_PICK_ATTRACTIONS
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
                            Constants.REQUEST_PICK_ATTRACTIONS,
                            this.viewModel.attractionCategoryHeaderProvider.getCategorizedAttractions(viewModel.park.getChildrenAsType(StockAttraction.class)));
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
