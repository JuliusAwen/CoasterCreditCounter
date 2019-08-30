package de.juliusawen.coastercreditcounter.frontend.navigationHub;

import android.view.MenuItem;

import androidx.lifecycle.ViewModel;

public class NavigationHubActivityViewModel extends ViewModel
{
    MenuItem selectedMenuItem;
    String exportFileAbsolutePath;
    boolean isImporting;
    boolean isExporting;
}
