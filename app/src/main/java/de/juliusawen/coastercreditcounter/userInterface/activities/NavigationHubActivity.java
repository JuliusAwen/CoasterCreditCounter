package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

import static de.juliusawen.coastercreditcounter.globals.Constants.LOG_TAG;

public class NavigationHubActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener
{
    private NavigationHubActivityViewModel viewModel;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TextView textViewTotalVisitedParksCount;
    private TextView textViewTotalCoasterCreditCount;
    private TextView textViewTotalCoasterRidesCount;

    protected void setContentView()
    {
        setContentView(R.layout.activity_navigation_hub);
    }

    protected void create()
    {
        this.viewModel = ViewModelProviders.of(this).get(NavigationHubActivityViewModel.class);

        if(this.viewModel.exportFileAbsolutePath == null)
        {
            this.setExportFileAbsolutPath();
        }

        if(this.viewModel.optionsMenuAgent == null)
        {
            this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
        }

        this.textViewTotalVisitedParksCount = findViewById(R.id.textViewNavigationHub_totalVisitedParksCount);
        this.textViewTotalCoasterCreditCount = findViewById(R.id.textViewNavigationHub_totalCoasterCreditsCount);
        this.textViewTotalCoasterRidesCount = findViewById(R.id.textViewNavigationHub_totalCoasterRidesCount);

        this.drawerLayout = findViewById(R.id.navigationDrawer);
        this.navigationView = this.drawerLayout.findViewById(R.id.navigationView);
        this.setMenuItemImportAvailability();

        this.navigationView.setNavigationItemSelectedListener(this.getNavigationItemSelectedListener());

        super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.subtitle_navigation_hub)), getString(R.string.help_text_navigation_hub));
        super.addToolbar();
        super.addToolbarMenuIcon();
        super.setToolbarTitleAndSubtitle(getString(R.string.name_app), getString(R.string.subtitle_navigation_hub));
    }

    @Override
    protected void resume()
    {
        invalidateOptionsMenu();

        this.setStatistics();

        for (int i = 0; i < this.navigationView.getMenu().size(); i++)
        {
            this.navigationView.getMenu().getItem(i).setChecked(false);
        }

        this.closeNavigationDrawer();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(Constants.LOG_TAG, String.format("NavigationHubActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == RequestCode.PICK_VISIT.ordinal())
            {
                Visit resultElement = (Visit)ResultFetcher.fetchResultElement(data);

                Log.i(LOG_TAG, String.format("NavigationHubActivity.onActivityResult<GO_TO_CURRENT_VISIT>:: opening current visit %s...", resultElement));
                ActivityDistributor.goToCurrentVisit(this, resultElement);
            }
        }
    }

    @Override
    protected Menu createOptionsMenu(Menu menu)
    {
        return this.viewModel.optionsMenuAgent.add(OptionsItem.GO_TO_CURRENT_VISIT).create(menu);
    }

    @Override
    protected Menu prepareOptionsMenu(Menu menu)
    {
        return this.viewModel.optionsMenuAgent
                .setVisible(OptionsItem.GO_TO_CURRENT_VISIT, !Visit.getCurrentVisits().isEmpty())
                .prepare(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(!this.viewModel.isExporting && !this.viewModel.isImporting)
        {
            if(item.getItemId() == android.R.id.home)
            {
                Log.d(LOG_TAG, "NavigationHubActivity.onOptionsItemSelected<HOME>:: opening navigation drawer...");
                this.drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            else
            {
                if(this.viewModel.optionsMenuAgent.handleOptionsItemSelected(item, this))
                {
                    return true;
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean handleOptionsItemSelected(OptionsItem item)
    {
        if(item == OptionsItem.GO_TO_CURRENT_VISIT)
        {
            if(Visit.getCurrentVisits().size() > 1)
            {
                Log.i(LOG_TAG, String.format("NavigationHubActivity.handleGoToCurrentVisitSelected:: [%d] current visits found - offering pick",
                        Visit.getCurrentVisits().size()));

                ActivityDistributor.startActivityPickForResult(
                        this,
                        RequestCode.PICK_VISIT,
                        new ArrayList<IElement>(Visit.getCurrentVisits()));
            }
            else
            {
                Log.i(LOG_TAG, String.format("NavigationHubActivity.handleGoToCurrentVisitSelected:: only one current visit found - opening %s...",
                        Visit.getCurrentVisits().get(0)));

                ActivityDistributor.goToCurrentVisit(this, Visit.getCurrentVisits().get(0));
            }
            return true;
        }
        return super.handleOptionsItemSelected(item);
    }

    private void setExportFileAbsolutPath()
    {
        this.viewModel.exportFileAbsolutePath = App.persistence.getExternalStorageDocumentsDirectory().getAbsolutePath() + "/" + App.config.getContentFileName();
    }

    private void setMenuItemImportAvailability()
    {
        boolean enabled = App.persistence.fileExists(this.viewModel.exportFileAbsolutePath);

        Menu navigationMenu = navigationView.getMenu();
        navigationMenu.findItem(R.id.navigationItem_Import).setEnabled(enabled);

        Log.d(Constants.LOG_TAG, String.format("NavigationHubActivity.setMenuItemImportAvailability:: import enabled [%S]", enabled));


        //Todo: remove setEnabled(false) when implemented
        navigationMenu.findItem(R.id.navigationItem_ManageModels).setEnabled(false);
        navigationMenu.findItem(R.id.navigationItem_ManageBlueprints).setEnabled(false);
    }

    private void setStatistics()
    {
        Log.d(LOG_TAG, "NavigationHubActivity.setStatistics:: setting statistics");

        this.textViewTotalVisitedParksCount.setText(getString(R.string.text_total_parks_visited, App.persistence.fetchTotalVisitedParksCount()));
        this.textViewTotalCoasterCreditCount.setText(getString(R.string.text_total_coaster_credits, App.persistence.fetchTotalCreditsCount()));
        this.textViewTotalCoasterRidesCount.setText(getString(R.string.text_total_credit_rides, App.persistence.fetchTotalCreditsRideCount()));
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(!this.viewModel.isExporting && !this.viewModel.isImporting)
        {
            if(keyCode == KeyEvent.KEYCODE_BACK)
            {
                Log.d(LOG_TAG, "NavigationHubActivity.onKeyDown<BACK>:: hardware back button pressed");
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
            public boolean onNavigationItemSelected(MenuItem item)
            {
                return NavigationHubActivity.this.onNavigationItemSelected(item);
            }
        };
    }

    private boolean onNavigationItemSelected(MenuItem item)
    {
        if(!this.viewModel.isExporting && !this.viewModel.isImporting)
        {
            int id = item.getItemId();
            switch(id)
            {
                case R.id.navigationItem_BrowseLocations:
                    Log.d(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <BrowseLocations> selected");
                    ActivityDistributor.startActivityShow(NavigationHubActivity.this, RequestCode.SHOW_LOCATION, App.content.getRootLocation());
                    break;

                case R.id.navigationItem_ManageCreditTypes:
                    Log.d(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <ManageCreditTypes> selected");
                    ActivityDistributor.startActivityManageForResult(NavigationHubActivity.this, RequestCode.MANAGE_CREDIT_TYPES);
                    break;

                case R.id.navigationItem_ManageCategories:
                    Log.d(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <ManageCategories> selected");
                    ActivityDistributor.startActivityManageForResult(NavigationHubActivity.this, RequestCode.MANAGE_CATEGORIES);
                    break;

                case R.id.navigationItem_ManageManufacturers:
                    Log.d(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <ManageManufacturers> selected");
                    ActivityDistributor.startActivityManageForResult(NavigationHubActivity.this, RequestCode.MANAGE_MANUFACTURERS);
                    break;

                case R.id.navigationItem_ManageBlueprints:
                case R.id.navigationItem_ManageModels:
                    Log.d(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <ManageBlueprints/Models> selected");
                    Toaster.notYetImplemented(NavigationHubActivity.this);
                    break;

                case R.id.navigationItem_ManageStatuses:
                    Log.d(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <ManageStatuses> selected");
                    ActivityDistributor.startActivityManageForResult(NavigationHubActivity.this, RequestCode.MANAGE_STATUSES);
                    break;


                case R.id.navigationItem_Import:
                    Log.d(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <Import> selected");
                    if(this.requestPermissionWriteExternalStorage(item))
                    {
                        this.handleNavigationItemImportSelected();
                    }

                    break;

                case R.id.navigationItem_Export:
                    Log.d(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <Export> selected");
                    if(this.requestPermissionWriteExternalStorage(item))
                    {
                        handleNavigationItemExportSelected();
                    }
                    break;
            }
        }

        return true;
    }

    private void handleNavigationItemImportSelected()
    {
        if(App.persistence.fileExists(this.viewModel.exportFileAbsolutePath))
        {
            AlertDialogFragment alertDialogFragmentOverwriteFile = AlertDialogFragment.newInstance(
                    R.drawable.ic_baseline_warning,
                    getString(R.string.alert_dialog_title_overwrite_content),
                    getString(R.string.alert_dialog_message_confirm_overwrite_content),
                    getString(R.string.text_accept),
                    getString(R.string.text_cancel),
                    RequestCode.OVERWRITE_CONTENT,
                    false
            );

            alertDialogFragmentOverwriteFile.setCancelable(false);
            alertDialogFragmentOverwriteFile.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
        }
        else
        {
            String message = String.format("Import file %s does not exist!", this.viewModel.exportFileAbsolutePath);
            Log.e(LOG_TAG, String.format("NavigationHubActivity.handleNavigationItemImportSelected:: %s", message));
            Toaster.makeLongToast(NavigationHubActivity.this, message);
        }
    }

    private void handleNavigationItemExportSelected()
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
                    RequestCode.OVERWRITE_FILE,
                    false
            );

            alertDialogFragmentOverwriteFile.setCancelable(false);
            alertDialogFragmentOverwriteFile.show(fragmentManager, Constants.FRAGMENT_TAG_ALERT_DIALOG);
        }
        else
        {
            this.startExportContent();
        }
    }

    private boolean requestPermissionWriteExternalStorage(MenuItem menuItem)
    {
        this.viewModel.selectedMenuItem = menuItem;
        return super.requestPermissionWriteExternalStorage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            this.onNavigationItemSelected(this.viewModel.selectedMenuItem);
        }
    }

    @Override
    public void handleAlertDialogClick(RequestCode requestCode, int which)
    {
        switch(requestCode)
        {
            case OVERWRITE_FILE:
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    this.startExportContent();
                }
                break;

            case OVERWRITE_CONTENT:
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    this.startImportContent();
                }
                break;
        }
    }

    private void startImportContent()
    {
        if(this.viewModel.isImporting)
        {
            Log.i(Constants.LOG_TAG, "NavigationHubActivity.startImportContent:: app is importing...");
            super.showProgressBar(true);
        }
        else
        {
            Log.i(Constants.LOG_TAG, "NavigationHubActivity.startImportContent:: starting async import...");

            this.viewModel.isImporting = true;
            super.showProgressBar(true);
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
        super.showProgressBar(false);
        this.viewModel.isImporting = false;

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
            super.showProgressBar(true);
        }
        else
        {
            Log.i(Constants.LOG_TAG, "NavigationHubActivity.startExport:: starting async export...");

            this.viewModel.isExporting = true;
            super.showProgressBar(true);
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

        super.showProgressBar(false);
        this.setMenuItemImportAvailability();
        this.viewModel.isExporting = false;
        Toaster.makeLongToast(NavigationHubActivity.this, getString(R.string.information_export_success,
                App.persistence.getExternalStorageDocumentsDirectory().getAbsolutePath() + "/" + App.config.getContentFileName()));
    }
}


