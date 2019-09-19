package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;

public class ShowParkActivityViewModel extends ViewModel
{
    public Park park;
    public OptionsMenuAgent optionsMenuAgent;
}