package de.juliusawen.coastercreditcounter.presentation.navigationHub;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.AppSettings;
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
                return NavigationHubActivity.this.onNavigationItemSelected(item);
            }
        };
    }

    private boolean onNavigationItemSelected(MenuItem item)
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
                if(this.requestPermissionWriteExternalStorage(item))
                {
                    String toast = App.persistency.importContent()
                            ? getString(R.string.action_import_success)
                            : getString(R.string.action_import_fail, AppSettings.getExternalStorageDocumentsDirectory().getAbsolutePath());
                    Toaster.makeLongToast(NavigationHubActivity.this, toast);
                }
                break;
            }
            case R.id.navigationItem_Export:
            {
                if(this.requestPermissionWriteExternalStorage(item))
                {
                    String toast =  App.persistency.exportContent()
                            ? getString(R.string.action_export_success, AppSettings.getExternalStorageDocumentsDirectory().getAbsolutePath())
                            : getString(R.string.action_export_fail);
                    Toaster.makeLongToast(NavigationHubActivity.this, toast);
                }
                break;
            }
        }

        return true;
    }

    private boolean requestPermissionWriteExternalStorage(MenuItem item)
    {
        if(ContextCompat.checkSelfPermission(App.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            Log.i(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemExportSelected:: Permission to write to external storage denied - requesting permission");

            this.viewModel.menuItem = item;
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE);
            return false;
        }

        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == Constants.REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.i(Constants.LOG_TAG, "NavigationHubActivity.onRequestPermissionsResult:: Permission to write to external storage granted by user");
                this.onNavigationItemSelected(this.viewModel.menuItem);
            }
            else
            {
                Log.i(Constants.LOG_TAG, "NavigationHubActivity.onRequestPermissionsResult:: Permission to write to external storage not granted by user");
            }
        }
    }
}

