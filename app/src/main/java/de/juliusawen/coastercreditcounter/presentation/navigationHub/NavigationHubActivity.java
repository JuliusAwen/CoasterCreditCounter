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
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class NavigationHubActivity extends BaseActivity
{
    private NavigationHubActivityViewModel viewModel;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_navigation_hub);
        super.onCreate(savedInstanceState);

        this.drawerLayout = findViewById(R.id.navigationDrawer);
        this.navigationView = this.drawerLayout.findViewById(R.id.navigationView);

        this.navigationView.setNavigationItemSelectedListener(this.getNavigationItemSelectedListener());

        this.viewModel = ViewModelProviders.of(this).get(NavigationHubActivityViewModel.class);

        super.addToolbar();
        super.addToolbarMenuIcon();
        super.setToolbarTitleAndSubtitle(getString(R.string.title_app_name), getString(R.string.subtitle_navigation_hub));

        super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.subtitle_navigation_hub)), getString(R.string.help_text_navigation_hub));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        for (int i = 0; i < this.navigationView.getMenu().size(); i++)
        {
            this.navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
            {
                Log.d(Constants.LOG_TAG, "NavigationHubActivity.onOptionsItemSelected<HOME>:: opening navigation drawer...");
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
                this.closeNavigationDrawer();
                return true;
            }
        }
        return true;
    }

    private void closeNavigationDrawer()
    {
        if(this.drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            Log.d(Constants.LOG_TAG, "NavigationHubActivity.onKeyDown<BACK>:: closing navigation drawer...");
            this.drawerLayout.closeDrawers();
        }
    }

    private NavigationView.OnNavigationItemSelectedListener getNavigationItemSelectedListener()
    {
        return new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@SuppressWarnings("NullableProblems") MenuItem item)
            {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.navigationItem_BrowseContent:
                    {
                        Log.d(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <BrowseContent> selected");
                        ActivityTool.startActivityShow(NavigationHubActivity.this, Constants.REQUEST_SHOW_LOCATION, App.content.getRootLocation());
                        break;
                    }

                    case R.id.navigationItem_ManageCategories:
                    {
                        Log.d(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <ManageAttractionCategories> selected");
                        ActivityTool.startActivityManage(NavigationHubActivity.this, Constants.REQUEST_MANAGE_ATTRACTION_CATEGORIES);
                        break;
                    }
                    case R.id.navigationItem_ManageManufacturers:
                    {
                        Toaster.makeToast(NavigationHubActivity.this, "not yet implemented");
                        break;
                    }
                    case R.id.navigationItem_ManageModels:
                    {
                        Toaster.makeToast(NavigationHubActivity.this, "not yet implemented");
                        break;
                    }

                    case R.id.navigationItem_Import:
                    {
                        String toast = getString(App.persistency.importContent() ? R.string.action_import_success : R.string.action_import_fail);
                        Toaster.makeToast(NavigationHubActivity.this, toast);
                        break;
                    }
                    case R.id.navigationItem_Export:
                    {
                        String toast =  getString(App.persistency.exportContent() ? R.string.action_export_success : R.string.action_export_fail);
                        Toaster.makeToast(NavigationHubActivity.this, toast);
                        break;
                    }
                }

                return true;
            }
        };
    }
}

