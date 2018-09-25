package de.juliusawen.coastercreditcounter.presentation.activities.visits;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Attraction;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.fragments.parks.ShowAttractionsFragment;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;

public class ShowVisitActivity extends BaseActivity
{
    private ShowVisitActivityViewModel viewModel;
    private ShowAttractionsFragment showAttractionsFragment;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowVisitActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_show_visit);
        super.onCreate(savedInstanceState);

        this.viewModel = ViewModelProviders.of(this).get(ShowVisitActivityViewModel.class);

        if(this.viewModel.parentPark == null)
        {
            Element passedElement = App.content.getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
            if(passedElement.isInstance(Visit.class))
            {
                this.viewModel.visit = (Visit) passedElement;
                this.viewModel.parentPark = (Park) this.viewModel.visit.getParent();
            }
            else if(passedElement.isInstance(Park.class))
            {
                this.viewModel.parentPark = (Park) passedElement;
            }
        }

        super.addToolbar();
        super.addToolbarHomeButton();
        this.decorateToolbar();


        if(this.viewModel.visit == null)
        {
            Log.d(Constants.LOG_TAG, "ShowVisitActivity.onResume:: creating visit...");
            this.createVisit();
        }
        else
        {
            Log.d(Constants.LOG_TAG, String.format("ShowVisitActivity.onResume:: initializing ShowAttractionsFragment with %s...", this.viewModel.visit));
            addShowAttractionsFragment();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowVisitActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == Constants.REQUEST_PICK_ATTRACTIONS)
            {
                List<Element> selectedElements = App.content.fetchElementsByUuidStrings(data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS));
                this.viewModel.visit.addChildren(selectedElements);
                addShowAttractionsFragment();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                Log.d(Constants.LOG_TAG, "ShowVisitActivity.onKeyDown<BACK>:: hardware back button pressed");
                this.onToolbarHomeButtonBackClicked();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onToolbarHomeButtonBackClicked()
    {
        Log.i(Constants.LOG_TAG, "ShowVisitActivity.onToolbarHomeButtonBackClicked:: finishing activity...");
        returnResult(RESULT_OK);
    }

    private void createVisit()
    {
        Log.i(Constants.LOG_TAG, String.format("ShowVisitActivity.createVisit:: creating visit for %s", this.viewModel.parentPark));

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(ShowVisitActivity.this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day)
            {
                calendar.set(year, month, day);
                onDateSetCreateVisit(calendar);
                showPickAttractionsDialog();

            }
        }, year, month, day);

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.text_cancel), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int position)
            {
                if (position == DialogInterface.BUTTON_NEGATIVE)
                {
                    returnResult(RESULT_CANCELED);
                }
            }
        });

        datePickerDialog.getDatePicker().setFirstDayOfWeek(App.settings.getFirstDayOfTheWeek());
        datePickerDialog.setCancelable(false);
        datePickerDialog.setCanceledOnTouchOutside(false);
        datePickerDialog.show();
    }

    private void onDateSetCreateVisit(Calendar calendar)
    {
        this.datePickerDialog.dismiss();

        this.viewModel.visit = Visit.create(calendar);
        this.viewModel.parentPark.addChild(this.viewModel.visit);
        App.content.addElement(this.viewModel.visit);

        if(Visit.isSameDay(this.viewModel.visit.getCalendar(), Calendar.getInstance()))
        {
            Log.i(Constants.LOG_TAG, "ShowVisitActivity.onViewCreated:: created visit is today - set as open visit");
            Visit.setOpenVisit(this.viewModel.visit);
        }

        this.decorateToolbar();
    }

    private void showPickAttractionsDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog;

        builder.setTitle(R.string.alert_dialog_add_attractions_to_visit_title);
        builder.setMessage(getString(R.string.alert_dialog_add_attractions_to_visit_message));
        builder.setPositiveButton(R.string.text_accept, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();

                //Todo: pass AttractionCategoyHeaders
                ActivityTool.startActivityPickForResult(ShowVisitActivity.this, Constants.REQUEST_PICK_ATTRACTIONS, viewModel.parentPark.getChildrenOfType(Attraction.class));
            }
        });

        builder.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
                addShowAttractionsFragment();
            }
        });

        alertDialog = builder.create();
        alertDialog.setIcon(R.drawable.ic_baseline_notification_important);

        alertDialog.show();
    }


    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle(
                this.viewModel.visit != null ? this.viewModel.visit.getName() : getString(R.string.title_visit_create),
                this.viewModel.visit != null ? this.viewModel.parentPark.getName() : null);
    }

    protected void addShowAttractionsFragment()
    {
        Log.d(Constants.LOG_TAG, "ShowVisitActivity.addShowAttractionsFragment:: adding fragment...");

        if(this.showAttractionsFragment == null || super.savedInstanceState == null)
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            this.showAttractionsFragment = ShowAttractionsFragment.newInstance(this.viewModel.visit.getUuid().toString());
            fragmentTransaction.add(R.id.linearLayoutShowVisit, this.showAttractionsFragment, Constants.FRAGMENT_TAG_SHOW_VISIT_ATTRACTIONS);
            fragmentTransaction.commitAllowingStateLoss();
        }
        else
        {
            this.showAttractionsFragment = (ShowAttractionsFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_SHOW_VISIT_ATTRACTIONS);
        }
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowVisitActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();
        if(resultCode == RESULT_OK)
        {
            Log.i(Constants.LOG_TAG, String.format("ShowVisitActivity.returnResult:: returning %s", this.viewModel.visit));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.visit.getUuid().toString());
        }

        setResult(resultCode, intent);
        finish();
    }
}
