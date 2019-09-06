package de.juliusawen.coastercreditcounter.frontend.navigationHub;

import android.view.MenuItem;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.toolbox.MenuAgent;

public class NavigationHubActivityViewModel extends ViewModel
{
    MenuItem selectedMenuItem;
    String exportFileAbsolutePath;
    boolean isImporting;
    boolean isExporting;
    MenuAgent optionsMenuAgent;
}
