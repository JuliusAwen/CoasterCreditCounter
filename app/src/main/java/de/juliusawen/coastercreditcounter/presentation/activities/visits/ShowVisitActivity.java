package de.juliusawen.coastercreditcounter.presentation.activities.visits;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.Attraction;
import de.juliusawen.coastercreditcounter.data.AttractionCategory;
import de.juliusawen.coastercreditcounter.data.Element;
import de.juliusawen.coastercreditcounter.data.Park;
import de.juliusawen.coastercreditcounter.data.Visit;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.recycler.CountableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ShowVisitActivity extends BaseActivity
{
    private Visit visit = null;
    private Element parentPark;

    private DatePickerDialog datePickerDialog;
    private CountableRecyclerAdapter attractionsRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowVisitActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_show_visit);
        super.onCreate(savedInstanceState);

        this.initializeContent();

        super.addToolbar();
        super.addToolbarHomeButton();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        this.decorateToolbar();
        if(this.visit == null)
        {
            this.createVisit();
        }
        else
        {
            if(this.attractionsRecyclerAdapter == null)
            {
                this.createAttractionsRecyclerAdapter();
            }
            else
            {
                this.updateAttractionsRecyclerView();
            }
        }
    }

    @Override
    protected void onToolbarHomeButtonBackClicked()
    {
        Log.i(Constants.LOG_TAG, "ShowVisitActivity.onToolbarHomeButtonBackClicked:: staring ShowParkActivity");
        ActivityTool.startActivityShow(this, this.parentPark);
    }

    private void initializeContent()
    {
        Element passedElement = App.content.getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        if(passedElement.isInstance(Visit.class))
        {
            this.visit = (Visit) passedElement;
            this.parentPark = visit.getParent();
        }
        else if(passedElement.isInstance(Park.class))
        {
            this.parentPark = passedElement;
        }
    }

    private void createVisit()
    {
        Log.i(Constants.LOG_TAG, String.format("ShowVisitActivity.createVisit:: creating visit for %s", this.parentPark));

        View view = findViewById(R.id.buttonShowVisit_DatePickerDialog);
        view.setVisibility(View.VISIBLE);

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
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setFirstDayOfWeek(App.settings.getFirstDayOfTheWeek());
        datePickerDialog.show();
    }

    private void onDateSetCreateVisit(Calendar calendar)
    {
        this.datePickerDialog.dismiss();
        View view = findViewById(R.id.buttonShowVisit_DatePickerDialog);
        view.setVisibility(View.GONE);

        this.visit = Visit.create(calendar);
        this.parentPark.addChild(this.visit);
        this.visit.initialize();
        Visit.setOpenVisit(this.visit);
        App.content.addElement(this.visit);

        this.decorateToolbar();
        this.createAttractionsRecyclerAdapter();
    }

    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle(this.visit != null ? this.parentPark.getName() : getString(R.string.title_create_visit), this.visit != null ? this.visit.getName() : null);
    }

    private void createAttractionsRecyclerAdapter()
    {
        Log.d(Constants.LOG_TAG, "ShowVisitActivity.onViewCreated:: creating RecyclerView...");

        RecyclerOnClickListener.OnClickListener recyclerOnClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Element element = (Element) view.getTag();

                if(element.isInstance(Attraction.class))
                {
                    Toaster.makeToast(getApplicationContext(), String.format("ShowAttractions not yet implemented %s", (Element) view.getTag()));
                }
            }

            @Override
            public boolean onLongClick(final View view, int position)
            {
                return true;
            }
        };

        this.attractionsRecyclerAdapter =
                new CountableRecyclerAdapter(AttractionCategory.addAttractionCategoryHeaders(new ArrayList<>(this.visit.getRideCountByAttraction().keySet())), recyclerOnClickListener);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewShowVisit);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.expandAttractionsCategoriesAccordingToSettings();
        recyclerView.setAdapter(this.attractionsRecyclerAdapter);
    }

    private void updateAttractionsRecyclerView()
    {
        if(this.parentPark.getChildCountOfInstance(Attraction.class) > 0)
        {
            List<Element> preparedAttractions = AttractionCategory.addAttractionCategoryHeaders(this.parentPark.getChildrenOfInstance(Attraction.class));
            this.expandAttractionsCategoriesAccordingToSettings();
            this.attractionsRecyclerAdapter.updateElements(preparedAttractions);
        }
        else
        {
            Log.v(Constants.LOG_TAG, "ShowParkAttractionsFragment.updateAttractionsRecyclerView:: no elements to update");
        }
    }

    private void expandAttractionsCategoriesAccordingToSettings()
    {
        for(AttractionCategory attractionCategory : App.settings.getAttractionCategoriesToExpandByDefault())
        {
            Log.v(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.expandAttractionsCategoriesAccordingToSettings:: expanding #[%s] according to settings...", attractionCategory));
            this.attractionsRecyclerAdapter.expandElement(attractionCategory);
        }
    }
}
