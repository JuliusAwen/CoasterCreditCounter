package de.juliusawen.coastercreditcounter.frontend.navigationHub;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.fragments.AlertDialogFragment;
import de.juliusawen.coastercreditcounter.globals.Constants;
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
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "NavigationHubActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_navigation_hub);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            this.viewModel = ViewModelProviders.of(this).get(NavigationHubActivityViewModel.class);
            if(this.viewModel.exportFileAbsolutePath == null)
            {
                this.viewModel.exportFileAbsolutePath = App.persistence.getExternalStorageDocumentsDirectory().getAbsolutePath() + "/" + App.config.getContentFileName();
            }

            this.drawerLayout = findViewById(R.id.navigationDrawer);
            this.navigationView = this.drawerLayout.findViewById(R.id.navigationView);
            this.setMenuItemImportAvailability();

            this.navigationView.setNavigationItemSelectedListener(this.getNavigationItemSelectedListener());

            super.addToolbar();
            super.addToolbarMenuIcon();
            super.setToolbarTitleAndSubtitle(getString(R.string.name_app), getString(R.string.subtitle_navigation_hub));
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
        boolean enabled = App.persistence.fileExists(this.viewModel.exportFileAbsolutePath);

        Menu navigationMenu = navigationView.getMenu();
        navigationMenu.findItem(R.id.navigationItem_Import).setEnabled(enabled);

        Log.d(Constants.LOG_TAG, String.format("NavigationHubActivity.setMenuItemImportAvailability:: import enabled [%S]", enabled));


        //Todo: remove
        navigationMenu.findItem(R.id.navigationItem_ManageModels).setEnabled(false);
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
                ActivityTool.startActivityShow(NavigationHubActivity.this, Constants.REQUEST_CODE_SHOW_LOCATION, App.content.getRootLocation());
                break;
            }

            case R.id.navigationItem_ManageCategories:
            {
                Log.d(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <ManageCategories> selected");
                ActivityTool.startActivityManage(NavigationHubActivity.this, Constants.REQUEST_CODE_MANAGE_ATTRACTION_CATEGORIES);
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
                Log.d(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <Import> selected");

                if(this.requestPermissionWriteExternalStorage(item))
                {
                    if(App.persistence.fileExists(this.viewModel.exportFileAbsolutePath))
                    {
                        FragmentManager fragmentManager = getSupportFragmentManager();

                        AlertDialogFragment alertDialogFragmentOverwriteFile = AlertDialogFragment.newInstance(
                                R.drawable.ic_baseline_warning,
                                getString(R.string.alert_dialog_title_overwrite_content),
                                getString(R.string.alert_dialog_message_overwrite_content),
                                getString(R.string.text_accept),
                                getString(R.string.text_cancel),
                                Constants.REQUEST_CODE_OVERWRITE_CONTENT
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
                Log.d(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <Export> selected");

                if(this.requestPermissionWriteExternalStorage(item))
                {
                    if(App.persistence.fileExists(this.viewModel.exportFileAbsolutePath))
                    {
                        FragmentManager fragmentManager = getSupportFragmentManager();

                        AlertDialogFragment alertDialogFragmentOverwriteFile = AlertDialogFragment.newInstance(
                                R.drawable.ic_baseline_warning,
                                getString(R.string.alert_dialog_title_overwrite_file),
                                getString(R.string.alert_dialog_message_overwrite_file),
                                getString(R.string.text_accept),
                                getString(R.string.text_cancel),
                                Constants.REQUEST_OVERWRITE_FILE
                        );

                        alertDialogFragmentOverwriteFile.setCancelable(false);
                        alertDialogFragmentOverwriteFile.show(fragmentManager, Constants.FRAGMENT_TAG_ALERT_DIALOG);
                    }
                    else
                    {
                        this.startExportContent();
                    }
                }
                break;
            }
        }

        return true;
    }

    private boolean requestPermissionWriteExternalStorage(MenuItem menuItem)
    {
        this.viewModel.selectedMenuItem = menuItem;
        return super.requestPermissionWriteExternalStorage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            this.onNavigationItemSelected(this.viewModel.selectedMenuItem);
        }
    }

    @Override
    public void onAlertDialogClick(int requestCode, DialogInterface dialog, int which)
    {
        dialog.dismiss();

        switch(requestCode)
        {
            case Constants.REQUEST_OVERWRITE_FILE:
            {
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    this.startExportContent();
                }
                break;
            }

            case Constants.REQUEST_CODE_OVERWRITE_CONTENT:
            {
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    this.startImportContent();
                }
                break;
            }
        }
    }

    private void startImportContent()
    {
        if(this.viewModel.isImporting)
        {
            Log.i(Constants.LOG_TAG, "NavigationHubActivity.startImportContent:: app is importing...");
            this.showProgressBar();
        }
        else
        {
            Log.i(Constants.LOG_TAG, "NavigationHubActivity.startImportContent:: starting async import...");

            this.viewModel.isImporting = true;
            this.showProgressBar();
            new ImportContent().execute(this);
        }
    }

    private static class ImportContent extends AsyncTask<NavigationHubActivity, Void, NavigationHubActivity>
    {
        @Override
        protected NavigationHubActivity doInBackground(NavigationHubActivity... navigationHubActivities)
        {
            NavigationHubActivity navigationHubActivity = navigationHubActivities[0];

            boolean success = App.persistence.importContent();

            if(success)
            {
                return navigationHubActivity;
            }

            navigationHubActivity.reportImportContentFailed();

            String message = "Import failed";
            Log.e(Constants.LOG_TAG, String.format("NavigationHubActivity.importContent.doInBackground:: %s", message));
            throw new IllegalStateException(message);
        }

        @Override
        protected void onPostExecute(NavigationHubActivity navigationHubActivity)
        {
            super.onPostExecute(navigationHubActivity);
            navigationHubActivity.finishImportContent();
        }
    }

    public void reportImportContentFailed()
    {
        Toaster.makeLongToast(NavigationHubActivity.this, getString(R.string.error_import_fail, App.persistence.getExternalStorageDocumentsDirectory().getAbsolutePath()));
    }

    public void finishImportContent()
    {
        Log.i(Constants.LOG_TAG, "NavigationHubActivity.finishImportContent:: finishing import...");
        this.hideProgressBar();

        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.information_import_success), Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_undo_title, new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(Constants.LOG_TAG, "NavigationHubActivity.finishImportContent.onSnackbarClick:: restoring content backup...");

                if(App.content.restoreBackup())
                {
                    Toaster.makeToast(NavigationHubActivity.this, getString(R.string.information_restore_content_backup_success));
                }
                else
                {
                    Log.e(Constants.LOG_TAG, "NavigationHubActivity.finishImportContent.onSnackbarClick:: restoring content backup failed!");
                    Toaster.makeToast(NavigationHubActivity.this, getString(R.string.error_undo_not_possible));
                }
            }
        });
        snackbar.show();
    }

    private void startExportContent()
    {
        if(this.viewModel.isExporting)
        {
            Log.i(Constants.LOG_TAG, "NavigationHubActivity.startExport:: app is exporting...");
            this.showProgressBar();
        }
        else
        {
            Log.i(Constants.LOG_TAG, "NavigationHubActivity.startExport:: starting async export...");

            this.viewModel.isExporting = true;
            this.showProgressBar();
            new ExportContent().execute(this);
        }
    }

    private static class ExportContent extends AsyncTask<NavigationHubActivity, Void, NavigationHubActivity>
    {
        @Override
        protected NavigationHubActivity doInBackground(NavigationHubActivity... navigationHubActivities)
        {
            NavigationHubActivity navigationHubActivity = navigationHubActivities[0];

            boolean success = App.persistence.exportContent();

            if(success)
            {
                return navigationHubActivity;
            }

            navigationHubActivity.reportExportContentFailed();

            String message = "Export failed";
            Log.e(Constants.LOG_TAG, String.format("NavigationHubActivity.ExportContent.doInBackground:: %s", message));
            throw new IllegalStateException(message);
        }

        @Override
        protected void onPostExecute(NavigationHubActivity navigationHubActivity)
        {
            super.onPostExecute(navigationHubActivity);
            navigationHubActivity.finishExportContent();
        }
    }

    public void reportExportContentFailed()
    {
        Toaster.makeLongToast(NavigationHubActivity.this, getString(R.string.error_export_fail));
    }

    public void finishExportContent()
    {
        Log.i(Constants.LOG_TAG, "NavigationHubActivity.finishImportExport:: finishing import/export...");

        this.hideProgressBar();
        this.setMenuItemImportAvailability();
        Toaster.makeLongToast(NavigationHubActivity.this, getString(R.string.information_export_success, App.persistence.getExternalStorageDocumentsDirectory().getAbsolutePath()));
    }
}


