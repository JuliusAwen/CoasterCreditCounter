package de.juliusawen.coastercreditcounter.presentation.activities;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.Element;
import de.juliusawen.coastercreditcounter.data.Location;
import de.juliusawen.coastercreditcounter.data.requests.GetContentRecyclerAdapterRequest;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.presentation.recycler.ContentRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class TestActivity extends BaseActivity
{
    private Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e(Constants.LOG_TAG, Constants.LOG_DIVIDER + "TestActivity.onCreate:: creating activity...");
        setContentView(R.layout.activity_test);
        super.onCreate(savedInstanceState);

        this.initializeContent();

        super.addToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle("Test", "Test");

        super.addHelpOverlay(getString(R.string.title_help, "Test"), "Test");

        this.createContentRecyclerAdapter();
    }

    @Override
    protected void onResume()
    {
        Log.e(Constants.LOG_TAG, String.format("TestActivity.onResume:: location is %s", this.location));
        super.onResume();
    }

    private void initializeContent()
    {
        this.location = (Location) App.content.getRootLocation();
    }

    private void createContentRecyclerAdapter()
    {
        RecyclerOnClickListener.OnClickListener onParentClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Toaster.makeToast(getApplicationContext(), String.format("Parent %s clicked", (Element) view.getTag()));
            }

            @Override
            public boolean onLongClick(final View view, int position)
            {
                Toaster.makeToast(getApplicationContext(), String.format("Parent %s long clicked", (Element) view.getTag()));
                return true;
            }
        };

        RecyclerOnClickListener.OnClickListener onChildClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Toaster.makeToast(getApplicationContext(), String.format("Child %s clicked", (Element) view.getTag()));
            }

            @Override
            public boolean onLongClick(final View view, int position)
            {
                Toaster.makeToast(getApplicationContext(), String.format("Child %s long clicked", (Element) view.getTag()));
                return true;
            }
        };

        GetContentRecyclerAdapterRequest request = new GetContentRecyclerAdapterRequest();
        request.childrenByParents = Location.getParksByLocations(this.location);
        request.onParentClickListener = onParentClickListener;
        request.onChildClickListener = onChildClickListener;
        request.parentsAreExpandable = true;
        request.parentsAreSelectable = true;
        request.selectMultipleParentsIsPossible = true;
        request.childrenAreSelectable = true;
        request.selectMultipleChildrenIsPossible = true;

        ContentRecyclerAdapter contentRecyclerAdapter = new ContentRecyclerAdapter(request);

        RecyclerView recyclerView = findViewById(android.R.id.content).findViewById(R.id.recyclerViewTest);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contentRecyclerAdapter);
    }
}
