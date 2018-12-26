package de.juliusawen.coastercreditcounter.presentation.navigationHub;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.presentation.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.fragments.AlertDialogFragment;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

import static de.juliusawen.coastercreditcounter.globals.Constants.LOG_TAG;

public class NavigationHubActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener
{
    private NavigationHubActivityViewModel viewModel;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_navigation_hub);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            this.viewModel = ViewModelProviders.of(this).get(NavigationHubActivityViewModel.class);
            if(this.viewModel.exportFileAbsolutePath == null)
            {
                this.viewModel.exportFileAbsolutePath = App.persistency.getExternalStorageDocumentsDirectory().getAbsolutePath() + "/" + App.config.getContentFileName();
            }

            this.drawerLayout = findViewById(R.id.navigationDrawer);
            this.navigationView = this.drawerLayout.findViewById(R.id.navigationView);
            this.setMenuItemImportAvailability();

            this.navigationView.setNavigationItemSelectedListener(this.getNavigationItemSelectedListener());

            super.addToolbar();
            super.addToolbarMenuIcon();
            super.setToolbarTitleAndSubtitle(getString(R.string.title_app_name), getString(R.string.subtitle_navigation_hub));
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(App.isInitialized)
        {
            super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.subtitle_navigation_hub)), getString(R.string.help_text_navigation_hub));

            for (int i = 0; i < this.navigationView.getMenu().size(); i++)
            {
                this.navigationView.getMenu().getItem(i).setChecked(false);
            }
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

    private void setMenuItemImportAvailability()
    {
        boolean enabled = App.persistency.fileExists(this.viewModel.exportFileAbsolutePath);

        Menu navigationMenu = navigationView.getMenu();
        MenuItem menuItemImport = navigationMenu.findItem(R.id.navigationItem_Import);
        menuItemImport.setEnabled(enabled);

        Log.d(Constants.LOG_TAG, String.format("NavigationHubActivity.setMenuItemImportAvailability:: import enabled [%S]", enabled));
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
            Log.d(Constants.LOG_TAG, "NavigationHubActivity.closeNavigationDrawer:: closing navigation drawer...");
            this.drawerLayout.closeDrawers();
        }
    }

    private NavigationView.OnNavigationItemSelectedListener getNavigationItemSelectedListener()
    {
        return new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
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
                    if(App.persistency.fileExists(this.viewModel.exportFileAbsolutePath))
                    {
                        FragmentManager fragmentManager = getSupportFragmentManager();

                        AlertDialogFragment alertDialogFragmentOverwriteFile = AlertDialogFragment.newInstance(
                                R.drawable.ic_baseline_warning,
                                getString(R.string.alert_dialog_overwrite_content_title),
                                getString(R.string.alert_dialog_overwrite_content_message),
                                getString(R.string.text_accept),
                                getString(R.string.text_cancel),
                                Constants.ALERT_DIALOG_REQUEST_CODE_OVERWRITE_CONTENT
                        );

                        alertDialogFragmentOverwriteFile.setCancelable(false);
                        alertDialogFragmentOverwriteFile.show(fragmentManager, Constants.FRAGMENT_TAG_ALERT_DIALOG);
                    }
                    else
                    {
                        String message = String.format("Import file %s does not exist!", this.viewModel.exportFileAbsolutePath);
                        Log.e(LOG_TAG, String.format("NavigationHubActivity.onNavigationItemSelected<navigationItem_Import>:: %s", message));
                        Toaster.makeLongToast(NavigationHubActivity.this, message);
                    }
                }
                break;
            }
            case R.id.navigationItem_Export:
            {
                if(this.requestPermissionWriteExternalStorage(item))
                {
                    if(App.persistency.fileExists(this.viewModel.exportFileAbsolutePath))
                    {
                        FragmentManager fragmentManager = getSupportFragmentManager();

                        AlertDialogFragment alertDialogFragmentOverwriteFile = AlertDialogFragment.newInstance(
                                R.drawable.ic_baseline_warning,
                                getString(R.string.alert_dialog_overwrite_file_title),
                                getString(R.string.alert_dialog_overwrite_file_message),
                                getString(R.string.text_accept),
                                getString(R.string.text_cancel),
                                Constants.ALERT_DIALOG_REQUEST_CODE_OVERWRITE_FILE
                        );

                        alertDialogFragmentOverwriteFile.setCancelable(false);
                        alertDialogFragmentOverwriteFile.show(fragmentManager, Constants.FRAGMENT_TAG_ALERT_DIALOG);
                    }
                    else
                    {
                        this.exportContent();
                    }
                }
                break;
            }
        }

        return true;
    }

    private boolean requestPermissionWriteExternalStorage(MenuItem item)
    {
        if(ContextCompat.checkSelfPermission(App.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            Log.i(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemExportSelected:: Permission to write to external storage denied - requesting permission");

            this.viewModel.selectedMenuItem = item;
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
                this.onNavigationItemSelected(this.viewModel.selectedMenuItem);
            }
            else
            {
                Log.i(Constants.LOG_TAG, "NavigationHubActivity.onRequestPermissionsResult:: Permission to write to external storage not granted by user");
            }
        }
    }


    @Override
    public void onAlertDialogClick(int requestCode, DialogInterface dialog, int which)
    {
        dialog.dismiss();

        switch(requestCode)
        {
            case Constants.ALERT_DIALOG_REQUEST_CODE_OVERWRITE_FILE:
            {
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    this.exportContent();
                }
                break;
            }

            case Constants.ALERT_DIALOG_REQUEST_CODE_OVERWRITE_CONTENT:
            {
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    this.importContent();
                }
                break;
            }
        }
    }

    private void importContent()
    {
        if(!App.persistency.importContent())
        {
            Toaster.makeLongToast(NavigationHubActivity.this, getString(R.string.action_import_fail, App.persistency.getExternalStorageDocumentsDirectory().getAbsolutePath()));
        }
        else
        {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.action_import_success), Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.action_undo_title, new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Log.i(Constants.LOG_TAG, "NavigationHubActivity.importContent.onSnackbarClick:: restoring content backup...");

                    if(App.content.restoreBackup())
                    {
                        Toaster.makeToast(NavigationHubActivity.this, getString(R.string.action_restore_content_backup_success));
                    }
                    else
                    {
                        Log.e(Constants.LOG_TAG, "NavigationHubActivity.importContent.onSnackbarClick:: restoring content backup failed!");
                        Toaster.makeToast(NavigationHubActivity.this, getString(R.string.error_text_undo_not_possible));
                    }
                }
            });
            snackbar.show();
        }
    }

    private void exportContent()
    {
        String toast;

        if(App.persistency.exportContent())
        {
            toast = getString(R.string.action_export_success, App.persistency.getExternalStorageDocumentsDirectory().getAbsolutePath());
            this.setMenuItemImportAvailability();
        }
        else
        {
            toast = getString(R.string.action_export_fail);
        }

        Toaster.makeLongToast(NavigationHubActivity.this, toast);
    }
}


