package de.juliusawen.coastercreditcounter.frontend;

import androidx.lifecycle.ViewModel;

public class BaseActivityViewModel extends ViewModel
{
    public boolean isInitializingApp = false;

    public boolean helpOverlayFragmentIsVisible = false;
    public String helpOverlayFragmentTitle;
    public CharSequence helpOverlayFragmentMessage;

    public boolean writeToExternalStoragePermissionNeededToInitialize;
}
