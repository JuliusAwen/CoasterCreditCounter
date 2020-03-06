package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.view.MenuItem;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;

public class NavigationHubActivityViewModel extends ViewModel
{
    public MenuItem selectedMenuItem;
    public String exportFileAbsolutePath;

    public boolean isImporting = false;
    public boolean isImportSuccessful = false;

    public boolean isExporting = false;
    public boolean isExportSuccessful = false;

    public OptionsMenuAgent optionsMenuAgent;
}
