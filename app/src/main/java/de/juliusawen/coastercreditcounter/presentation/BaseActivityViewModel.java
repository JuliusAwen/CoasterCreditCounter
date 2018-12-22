package de.juliusawen.coastercreditcounter.presentation;

import androidx.lifecycle.ViewModel;

public class BaseActivityViewModel extends ViewModel
{
    boolean isInitializingApp = false;

    boolean helpOverlayFragmentIsVisible = false;
    String helpOverlayFragmentTitle;
    String getHelpOverlayFragmentMessage;
}
