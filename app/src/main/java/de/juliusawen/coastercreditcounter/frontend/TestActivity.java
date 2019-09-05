package de.juliusawen.coastercreditcounter.frontend;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.elements.Element;
import de.juliusawen.coastercreditcounter.backend.elements.Location;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.ActivityDistributor;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

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
        ActivityDistributor.startActivityShow(this, Constants.REQUEST_CODE_MANAGE_ATTRACTION_CATEGORIES, null);
    }
}
