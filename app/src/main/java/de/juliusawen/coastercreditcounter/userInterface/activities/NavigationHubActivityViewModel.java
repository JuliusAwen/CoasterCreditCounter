package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;

public class NavigationHubActivityViewModel extends ViewModel
{
    public Uri uri;
    public boolean isImporting = false;
    public boolean isImportSuccessful = false;

    public boolean isExporting = false;
    public boolean isExportSuccessful = false;

    long lastBackClickedInMS;

    long lastClickInMs;
    int clickCount;
    boolean enabled;

    public OptionsMenuAgent optionsMenuAgent;

    public List<Visit> currentVisits;
}
