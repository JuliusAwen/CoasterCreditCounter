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

    private boolean createAddShowAttractionsFragmentOnResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowVisitActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_show_visit);
        super.onCreate(savedInstanceState);

        this.viewModel = ViewModelProviders.of(this).get(ShowVisitActivityViewModel.class);

        if(this.viewModel.parentPark == null)
        {
            Element receivedElement = App.content.getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
            if(receivedElement.isInstance(Visit.class))
            {
                this.viewModel.visit = (Visit) receivedElement;
                this.viewModel.parentPark = (Park) this.viewModel.visit.getParent();
            }
            else if(receivedElement.isInstance(Park.class))
            {
                this.viewModel.parentPark = (Park) receivedElement;
            }
        }

        super.addToolbar();
        super.addToolbarHomeButton();
        this.decorateToolbar();

        if(this.viewModel.visit == null)
        {
            this.createVisit();
        }
        else
        {
            this.createAddShowAttractionsFragmentOnResume = true;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(this.createAddShowAttractionsFragmentOnResume)
        {
            this.addShowAttractionsFragment();
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
                this.createAddShowAttractionsFragmentOnResume = true;
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
                if(isExistingVisit(calendar))
                {
                    showVisitAlreadyExistsDialog(calendar);
                }
                else
                {
                    onDateSetCreateVisit(calendar);
                    showPickAttractionsDialog();
                }
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

    private boolean isExistingVisit(Calendar calendar)
    {
        for(Visit visit : viewModel.parentPark.getChildrenAsType(Visit.class))
        {
            if(Visit.isSameDay(visit.getCalendar(), calendar))
            {
                return true;
            }
        }
        return false;
    }

    private void showVisitAlreadyExistsDialog(final Calendar calendar)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog;

        builder.setTitle(R.string.alert_dialog_visit_already_exists_title);
        builder.setMessage(getString(R.string.alert_dialog_visit_already_exists_message));
        builder.setPositiveButton(R.string.text_accept, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();

                onDateSetCreateVisit(calendar);
                showPickAttractionsDialog();
            }
        });

        builder.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
                finish();
            }
        });

        alertDialog = builder.create();
        alertDialog.setIcon(R.drawable.ic_baseline_warning);

        alertDialog.show();
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
        if(super.savedInstanceState == null)
        {
            Log.d(Constants.LOG_TAG, "ShowVisitActivity.addShowAttractionsFragment:: adding fragment...");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            this.showAttractionsFragment = ShowAttractionsFragment.newInstance(this.viewModel.visit.getUuid().toString());
            fragmentTransaction.add(R.id.linearLayoutShowVisit, this.showAttractionsFragment, Constants.FRAGMENT_TAG_SHOW_VISIT_ATTRACTIONS);
            fragmentTransaction.commit();
        }
        else
        {
            Log.d(Constants.LOG_TAG, "ShowVisitActivity.addShowAttractionsFragment:: re-using fragment...");
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
