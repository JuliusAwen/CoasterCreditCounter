package de.juliusawen.coastercreditcounter.presentation.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Location;
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

        super.addHelpOverlayFragment(getString(R.string.title_help, "Test"), "Test");

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
        ContentRecyclerViewAdapter contentRecyclerViewAdapter =
                ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                        this.location.getChildrenOfType(Location.class), true);

        contentRecyclerViewAdapter.setOnClickListener(new RecyclerOnClickListener.OnClickListener()
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
        });

        RecyclerView recyclerView = findViewById(android.R.id.content).findViewById(R.id.recyclerViewTest);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contentRecyclerViewAdapter);
    }
}
