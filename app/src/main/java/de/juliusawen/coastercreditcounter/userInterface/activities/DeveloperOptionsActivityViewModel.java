package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.tools.menuTools.OptionsMenuProvider;

public class DeveloperOptionsActivityViewModel extends ViewModel
{
    public OptionsMenuProvider optionsMenuProvider;

    public DeveloperOptionsActivity.Mode mode;
}
