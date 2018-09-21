package de.juliusawen.coastercreditcounter.presentation.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Location;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.RecyclerOnClickListener;
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
        RecyclerOnClickListener.OnClickListener onClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Toaster.makeToast(TestActivity.this, String.format("%s clicked", (Element) view.getTag()));
            }

            @Override
            public boolean onLongClick(final View view, int position)
            {
                Toaster.makeToast(TestActivity.this, String.format("%s long clicked", (Element) view.getTag()));
                return true;
            }
        };

//        ContentRecyclerViewAdapter contentRecyclerViewAdapter =
//                ContentRecyclerViewAdapterProvider.getBasicContentRecyclerViewAdapter(this.location.getChildrenOfType(Location.class), onClickListener);

        ContentRecyclerViewAdapter contentRecyclerViewAdapter =
                ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(this.location.getChildrenOfType(Location.class), Park.class, onClickListener);

        RecyclerView recyclerView = findViewById(android.R.id.content).findViewById(R.id.recyclerViewTest);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        recyclerView.setAdapter(contentRecyclerViewAdapter);
    }
}
