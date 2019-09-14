package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.view.MenuItem;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.tools.menuAgent.OptionsMenuAgent;

public class NavigationHubActivityViewModel extends ViewModel
{
    public MenuItem selectedMenuItem;
    public String exportFileAbsolutePath;
    public boolean isImporting;
    public boolean isExporting;
    public OptionsMenuAgent optionsMenuAgent;
}
