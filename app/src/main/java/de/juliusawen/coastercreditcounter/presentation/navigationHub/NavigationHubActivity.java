package de.juliusawen.coastercreditcounter.presentation.navigationHub;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.presentation.BaseActivity;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;

public class NavigationHubActivity extends BaseActivity
{
    private NavigationHubActivityViewModel viewModel;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_navigation_hub);
        super.onCreate(savedInstanceState);

        this.drawerLayout = findViewById(R.id.navigationDrawer);
        NavigationView navigationView = this.drawerLayout.findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(this.getNavigationItemSelectedListener());



        this.viewModel = ViewModelProviders.of(this).get(NavigationHubActivityViewModel.class);

        super.addToolbar();
        super.addToolbarMenuIcon();
        super.setToolbarTitleAndSubtitle(getString(R.string.title_app_name), getString(R.string.subtitle_navigation_hub));

        super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.subtitle_navigation_hub)), getString(R.string.help_text_navigation_hub));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
            {
                this.drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
            {
                Log.d(Constants.LOG_TAG, "NavigationHubActivity.onKeyDown<BACK>:: hardware back button pressed");

                if(this.drawerLayout.isDrawerOpen(GravityCompat.START))
                {
                    this.drawerLayout.closeDrawers();
                    Log.d(Constants.LOG_TAG, "NavigationHubActivity.onKeyDown<BACK>:: closed navigation drawer");
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private NavigationView.OnNavigationItemSelectedListener getNavigationItemSelectedListener()
    {
        return new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem item)
            {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.navigationItem_BrowseLocations:
                    {
                        ActivityTool.startActivityShow(NavigationHubActivity.this, Constants.REQUEST_SHOW_LOCATION, App.content.getRootLocation());
                        return true;
                    }

                    case R.id.navigationItem_ManageAttractionCategories:
                    {
                        ActivityTool.startActivityManage(NavigationHubActivity.this, Constants.REQUEST_MANAGE_ATTRACTION_CATEGORIES);
                        return true;
                    }

                    default:
                        return true;
                }
            }
        };
    }
}

