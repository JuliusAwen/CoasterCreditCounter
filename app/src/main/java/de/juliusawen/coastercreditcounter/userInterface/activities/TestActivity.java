package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.RecyclerOnClickListener;

public class TestActivity extends BaseActivity
{
    private Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "TestActivity.onCreate:: creating activity...");
        setContentView(R.layout.activity_test);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            //        this.initializeContent();

            super.addToolbar();
            super.addToolbarHomeButton();
            super.setToolbarTitleAndSubtitle("Test", "Test");

            super.addHelpOverlayFragment(getString(R.string.title_help, "Test"), "Test");

            //        this.createContentRecyclerAdapter();
            //        this.createLoremIpsum();

//            this.startActivityViaIntent();
        }
    }

    private void initializeContent()
    {
        this.location = App.content.getRootLocation();
    }

    private void createContentRecyclerAdapter()
    {
        ContentRecyclerViewAdapter contentRecyclerViewAdapter =
                ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(this.location.getChildrenOfType(Location.class), null, true);

        contentRecyclerViewAdapter.setOnClickListener(new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toaster.makeToast(TestActivity.this, String.format("%s clicked", (Element) view.getTag()));
            }

            @Override
            public boolean onLongClick(final View view)
            {
                Toaster.makeToast(TestActivity.this, String.format("%s long clicked", (Element) view.getTag()));
                return true;
            }
        });

        RecyclerView recyclerView = findViewById(android.R.id.content).findViewById(R.id.recyclerViewTestActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contentRecyclerViewAdapter);
    }

    private void createLoremIpsum()
    {
        TextView textView = findViewById(R.id.textViewTestActivity);
        textView.setText(StringTool.getLoremIpsum(501));
    }

    private void startActivity()
    {
        ActivityDistributor.startActivityShow(this, RequestCode.MANAGE_ATTRACTION_CATEGORIES, null);
    }
}
