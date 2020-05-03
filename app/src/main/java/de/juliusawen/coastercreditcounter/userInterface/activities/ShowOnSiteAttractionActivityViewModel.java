package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;

public class ShowOnSiteAttractionActivityViewModel extends ViewModel
{
    public IOnSiteAttraction onSiteAttraction;

    public OptionsMenuAgent optionsMenuAgent;
}
