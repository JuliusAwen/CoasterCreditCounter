package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import java.util.HashSet;
import java.util.Set;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;

public class BaseActivityViewModel extends ViewModel
{
    public boolean isInitializingApp = false;
    public boolean isAppProperlyInitialized = false;
    public boolean activityIsCreated = false;

    public boolean isHelpOverlayAdded = false;
    public boolean helpOverlayFragmentIsVisible = false;
    public String helpOverlayFragmentTitle;
    public CharSequence helpOverlayFragmentMessage;

    public final Set<IElement> elementsToCreate = new HashSet<>();
    public final Set<IElement> elementsToUpdate = new HashSet<>();
    public final Set<IElement> elementsToDelete = new HashSet<>();

    public boolean wasFloatingActionButtonVisibleBeforeShowingHelpOverlay = false;

    public OptionsMenuAgent optionsMenuAgent;
}
