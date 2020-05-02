package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.Note;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;

public class ShowParkOverviewFragmentViewModel extends ViewModel
{
    public Park park;
    public Note note;

    public OptionsMenuAgent optionsMenuAgent;
}
