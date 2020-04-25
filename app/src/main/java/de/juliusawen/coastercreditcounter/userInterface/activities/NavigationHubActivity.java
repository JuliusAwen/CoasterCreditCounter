package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

import static de.juliusawen.coastercreditcounter.application.Constants.LOG_TAG;

public class NavigationHubActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener
{
    private NavigationHubActivityViewModel viewModel;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TextView textViewTotalVisitedParksCount;
    private TextView textViewTotalCoasterCreditCount;
    private TextView textViewTotalCoasterRidesCount;

    Toast clickBackAgainToExitToast;

    protected void setContentView()
    {
        setContentView(R.layout.activity_navigation_hub);
    }

    protected void create()
    {
        this.viewModel = new ViewModelProvider(this).get(NavigationHubActivityViewModel.class);

        if(this.viewModel.optionsMenuAgent == null)
        {
            this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
        }

        this.textViewTotalVisitedParksCount = findViewById(R.id.textViewNavigationHub_totalVisitedParksCount);
        this.textViewTotalCoasterCreditCount = findViewById(R.id.textViewNavigationHub_totalCoasterCreditsCount);
        this.textViewTotalCoasterRidesCount = findViewById(R.id.textViewNavigationHub_totalCoasterRidesCount);

        this.drawerLayout = findViewById(R.id.navigationDrawer);
        this.navigationView = this.drawerLayout.findViewById(R.id.navigationView);

        this.navigationView.setNavigationItemSelectedListener(this.getNavigationItemSelectedListener());

        super.createHelpOverlayFragment(getString(R.string.title_help, getString(R.string.subtitle_navigation_hub)), getString(R.string.help_text_navigation_hub));
        super.createToolbar()
                .addToolbarMenuIcon()
                .setToolbarTitleAndSubtitle(getString(R.string.name_app), getString(R.string.subtitle_navigation_hub));
    }

    @Override
    protected void resume()
    {
        this.viewModel.currentVisits = App.persistence.fetchCurrentVisits();

        invalidateOptionsMenu();

        for (int i = 0; i < this.navigationView.getMenu().size(); i++)
        {
            this.navigationView.getMenu().getItem(i).setChecked(false);
        }
        this.closeNavigationDrawer();

        this.setStatistics();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(Constants.LOG_TAG, String.format("NavigationHubActivity.onActivityResult:: requestCode[%s], resultCode[%s]", RequestCode.getValue(requestCode), resultCode));

        if(resultCode == RESULT_OK)
        {
            switch(RequestCode.getValue(requestCode))
            {
                case PICK_VISIT:
                {
                    Visit resultElement = (Visit) ResultFetcher.fetchResultElement(data);

                    Log.i(LOG_TAG, String.format("NavigationHubActivity.onActivityResult<GO_TO_CURRENT_VISIT>:: opening current visit %s...", resultElement));
                    ActivityDistributor.goToCurrentVisit(this, resultElement);
                    break;
                }

                case PICK_IMPORT_FILE:
                {
                    if(data != null)
                    {
                        this.handleImportFilePicked(data.getData());
                    }
                    else
                    {
                        Log.e(LOG_TAG, "NavigationHubActivity.onActivityResult<PICK_IMPORT_FILE>:: result data is null");
                    }
                    break;
                }

                case PICK_IMPORT_FILE_LOCATION:
                {
                    if(data != null)
                    {
                        this.handleImportFileLocationPicked(data.getData());
                    }
                    else
                    {
                        Log.e(LOG_TAG, "NavigationHubActivity.onActivityResult<PICK_IMPORT_FILE_LOCATION>:: result data is null");
                    }
                    break;
                }

                case PICK_EXPORT_FILE_LOCATION:
                {
                    if(data != null)
                    {
                        this.handleExportFileLocationPicked(data.getData());
                    }
                    else
                    {
                        Log.e(LOG_TAG, "NavigationHubActivity.onActivityResult<PICK_EXPORT_FILE_LOCATION>:: result data is null");
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected Menu createOptionsMenu(Menu menu)
    {
        return this.viewModel.optionsMenuAgent
                .add(OptionsItem.GO_TO_CURRENT_VISIT)
                .create(menu);
    }

    @Override
    protected Menu prepareOptionsMenu(Menu menu)
    {
        this.viewModel.optionsMenuAgent
                .setVisible(OptionsItem.GO_TO_CURRENT_VISIT, !this.viewModel.currentVisits.isEmpty())
                .prepare(menu);

        return menu;
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
            if(this.viewModel.currentVisits.size() > 1)
            {
                Log.i(LOG_TAG, String.format("NavigationHubActivity.handleGoToCurrentVisitSelected:: [%d] current visits found - offering pick", this.viewModel.currentVisits.size()));

                ActivityDistributor.startActivityPickForResult(this, RequestCode.PICK_VISIT, new ArrayList<IElement>(this.viewModel.currentVisits));
            }
            else
            {
                Log.i(LOG_TAG, String.format("NavigationHubActivity.handleGoToCurrentVisitSelected:: only one current visit found - opening %s...", this.viewModel.currentVisits.get(0)));

                ActivityDistributor.goToCurrentVisit(this, this.viewModel.currentVisits.get(0));
            }
            return true;
        }

        return super.handleOptionsItemSelected(item);
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
                Log.i(LOG_TAG, "NavigationHubActivity.onKeyDown<BACK>:: hardware back button pressed");

                if(this.isNavigationDrawerOpen())
                {
                    this.closeNavigationDrawer();
                }
                else
                {
                    long MAX_DELAY_FOR_BACK_DOUBLE_CLICK_TO_EXIT = 2000;
                    if(this.viewModel.lastBackClickedInMS + MAX_DELAY_FOR_BACK_DOUBLE_CLICK_TO_EXIT > System.currentTimeMillis())
                    {
                        this.clickBackAgainToExitToast.cancel();
                        finish();
                    }
                    else
                    {
                        this.clickBackAgainToExitToast = Toaster.makeShortToast(this, "Click BACK again to exit");
                        this.viewModel.lastBackClickedInMS = System.currentTimeMillis();
                    }
                }
            }
        }
        return true;
    }

    private boolean isNavigationDrawerOpen()
    {
        return this.drawerLayout.isDrawerOpen(GravityCompat.START);
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
                {
                    Log.i(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <BrowseLocations> selected");
                    ActivityDistributor.startActivityShow(NavigationHubActivity.this, RequestCode.SHOW_LOCATION, App.content.getRootLocation());
                    break;
                }

                case R.id.navigationItem_ManageCreditTypes:
                {
                    Log.i(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <ManageCreditTypes> selected");
                    ActivityDistributor.startActivityManageForResult(NavigationHubActivity.this, RequestCode.MANAGE_CREDIT_TYPES);
                    break;
                }

                case R.id.navigationItem_ManageCategories:
                {
                    Log.i(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <ManageCategories> selected");
                    ActivityDistributor.startActivityManageForResult(NavigationHubActivity.this, RequestCode.MANAGE_CATEGORIES);
                    break;
                }

                case R.id.navigationItem_ManageManufacturers:
                {
                    Log.i(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <ManageManufacturers> selected");
                    ActivityDistributor.startActivityManageForResult(NavigationHubActivity.this, RequestCode.MANAGE_MANUFACTURERS);
                    break;
                }

                case R.id.navigationItem_ManageBlueprints:
                {
                    Log.i(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <ManageBlueprints> selected");
                    ActivityDistributor.startActivityManageForResult(NavigationHubActivity.this, RequestCode.MANAGE_BLUEPRINTS);
                    break;
                }

                case R.id.navigationItem_ManageModels:
                {
                    Log.i(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <ManageBlueprints/Models> selected");
                    Toaster.notYetImplemented(NavigationHubActivity.this);
                    break;
                }

                case R.id.navigationItem_ManageStatuses:
                {
                    Log.i(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <ManageStatuses> selected");
                    ActivityDistributor.startActivityManageForResult(NavigationHubActivity.this, RequestCode.MANAGE_STATUSES);
                    break;
                }

                case R.id.navigationItem_Import:
                {
                    Log.i(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <Import> selected");
                    this.launchPickImportFileLocationIntent();
                    break;
                }

                case R.id.navigationItem_Export:
                {
                    Log.d(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: <Export> selected");
                    this.launchPickExportFileLocationIntent();
                    break;
                }

                default:
                    Log.e(Constants.LOG_TAG, "NavigationHubActivity.onNavigationItemSelected:: unknown item selected");
                    return false;
            }
        }

        return true;
    }

    private void launchPickImportFileLocationIntent()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, RequestCode.PICK_IMPORT_FILE_LOCATION.ordinal());
    }

    private void handleImportFileLocationPicked(Uri exportFileDocumentTreeUri)
    {
        if(App.persistence.validateImportFileUri(exportFileDocumentTreeUri, App.preferences.getExportFileName()))
        {
            this.viewModel.uri = exportFileDocumentTreeUri;
            this.showAlertDialogFragmentOverwriteContent();
        }
        else
        {
            String exportFileName = App.preferences.getExportFileName();
            LinkedHashMap<String, Integer> substringsByTypeface = new LinkedHashMap<>();
            substringsByTypeface.put(exportFileName, Typeface.BOLD_ITALIC);

            AlertDialogFragment alertDialogFragmentOverwriteFile = AlertDialogFragment.newInstance(
                    R.drawable.ic_baseline_error_outline,
                    getString(R.string.alert_dialog_title_export_file_not_found),
                    getString(R.string.alert_dialog_message_export_file_not_found, exportFileName),
                    substringsByTypeface,
                    getString(R.string.text_yes),
                    getString(R.string.text_no),
                    RequestCode.PICK_IMPORT_FILE,
                    false
            );

            alertDialogFragmentOverwriteFile.setCancelable(false);
            alertDialogFragmentOverwriteFile.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
        }
    }

    private void launchPickImportFileIntent()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        startActivityForResult(intent, RequestCode.PICK_IMPORT_FILE.ordinal());
    }

    private void handleImportFilePicked(Uri importFileUri)
    {
        if(App.persistence.validateImportFileUri(importFileUri, null))
        {
            this.viewModel.uri = importFileUri;
            this.showAlertDialogFragmentOverwriteContent();
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("NavigationHubActivity.handleImportFilePicked:: invalid uri [%s] - aborting", importFileUri.getPath()));
            Toaster.makeShortToast(this, getString(R.string.error_import_fail_invalid_uri));
        }
    }

    private void showAlertDialogFragmentOverwriteContent()
    {
        AlertDialogFragment alertDialogFragmentOverwriteContent = AlertDialogFragment.newInstance(
                R.drawable.ic_baseline_warning,
                getString(R.string.alert_dialog_title_overwrite_content),
                getString(R.string.alert_dialog_message_confirm_overwrite_content),
                getString(R.string.text_accept),
                getString(R.string.text_cancel),
                RequestCode.IMPORT_CONTENT,
                false
        );

        alertDialogFragmentOverwriteContent.setCancelable(false);
        alertDialogFragmentOverwriteContent.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
    }

    private void launchPickExportFileLocationIntent()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, RequestCode.PICK_EXPORT_FILE_LOCATION.ordinal());
    }

    private void handleExportFileLocationPicked(Uri exportFileDocumentTreeUri)
    {
        this.viewModel.uri = exportFileDocumentTreeUri;

        if(App.persistence.exportFileExists(exportFileDocumentTreeUri, App.preferences.getExportFileName()))
        {
            FragmentManager fragmentManager = getSupportFragmentManager();

            AlertDialogFragment alertDialogFragmentOverwriteFile = AlertDialogFragment.newInstance(
                    R.drawable.ic_baseline_warning,
                    getString(R.string.alert_dialog_title_overwrite_file),
                    getString(R.string.alert_dialog_message_overwrite_file),
                    getString(R.string.text_accept),
                    getString(R.string.text_cancel),
                    RequestCode.EXPORT_CONTENT,
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

    @Override
    public void handleAlertDialogClick(RequestCode requestCode, int which)
    {
        switch(requestCode)
        {
            case PICK_IMPORT_FILE:
            {
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    this.launchPickImportFileIntent();
                }
                break;
            }

            case IMPORT_CONTENT:
            {
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    this.startImportContent();
                }
                else
                {
                    this.viewModel.uri = null;
                }
                break;
            }

            case EXPORT_CONTENT:
            {
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    this.startExportContent();
                }
                else
                {
                    this.viewModel.uri = null;
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
            super.showProgressBar(true);
        }
        else
        {
            Log.i(Constants.LOG_TAG, "NavigationHubActivity.startImportContent:: starting async import...");

            this.viewModel.isImporting = true;
            this.viewModel.isImportSuccessful = false;
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

            navigationHubActivity.viewModel.isImportSuccessful = App.persistence.importContent(navigationHubActivity.viewModel.uri, App.preferences.getExportFileName());

            return navigationHubActivities[0];
        }

        @Override
        protected void onPostExecute(NavigationHubActivity navigationHubActivity)
        {
            super.onPostExecute(navigationHubActivity);
            navigationHubActivity.finishImportContent();
        }
    }


    public void finishImportContent()
    {
        Log.i(Constants.LOG_TAG, "NavigationHubActivity.finishImportContent:: finishing import...");
        super.showProgressBar(false);
        this.viewModel.isImporting = false;

        if(this.viewModel.isImportSuccessful)
        {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.information_import_success), Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.action_undo_title, new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Log.i(Constants.LOG_TAG, "NavigationHubActivity.finishImportContent.onSnackbarClick:: restoring content backup...");

                    if(App.content.restoreBackup(true))
                    {
                        Toaster.makeShortToast(NavigationHubActivity.this, getString(R.string.information_restore_content_backup_success));
                        setStatistics();
                    }
                    else
                    {
                        Log.e(Constants.LOG_TAG, "NavigationHubActivity.finishImportContent.onSnackbarClick:: restoring content backup failed");
                        Toaster.makeShortToast(NavigationHubActivity.this, getString(R.string.error_undo_not_possible));
                    }
                }
            });
            snackbar.show();
        }
        else
        {
            Toaster.makeLongToast(NavigationHubActivity.this, getString(R.string.error_import_fail));
            Log.e(Constants.LOG_TAG, "NavigationHubActivity.finishImportContent:: importing content failed");
        }

        this.resume();
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
            this.viewModel.isExportSuccessful = false;
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
            navigationHubActivity.viewModel.isExportSuccessful = App.persistence.exportContent(navigationHubActivity.viewModel.uri, App.preferences.getExportFileName());
            return navigationHubActivities[0];
        }

        @Override
        protected void onPostExecute(NavigationHubActivity navigationHubActivity)
        {
            super.onPostExecute(navigationHubActivity);
            navigationHubActivity.finishExportContent();
        }
    }

    public void finishExportContent()
    {
        Log.i(Constants.LOG_TAG, "NavigationHubActivity.finishExportContent:: finishing import/export...");

        super.showProgressBar(false);
        this.viewModel.isExporting = false;

        if(this.viewModel.isExportSuccessful)
        {
            this.viewModel.uri = null;
            Toaster.makeLongToast(NavigationHubActivity.this, getString(R.string.information_export_success, App.preferences.getExportFileName()));
        }
        else
        {
            Toaster.makeLongToast(NavigationHubActivity.this, getString(R.string.error_export_fail));
            Log.e(Constants.LOG_TAG, "NavigationHubActivity.finishExportContent:: exporting content failed");
        }

        this.closeNavigationDrawer();
    }

    private void closeNavigationDrawer()
    {
        if(this.isNavigationDrawerOpen())
        {
            Log.d(Constants.LOG_TAG, "NavigationHubActivity.closeNavigationDrawer:: closing navigation drawer...");
            this.drawerLayout.closeDrawers();
        }
    }
}


