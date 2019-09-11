package de.juliusawen.coastercreditcounter.frontend;

import androidx.lifecycle.ViewModel;

import java.util.HashSet;
import java.util.Set;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.frontend.menuAgent.OptionsMenuAgent;

public class BaseActivityViewModel extends ViewModel
{
    public boolean isInitializingApp = false;

    public boolean helpOverlayFragmentIsVisible = false;
    public String helpOverlayFragmentTitle;
    public CharSequence helpOverlayFragmentMessage;

    final Set<IElement> elementsToCreate = new HashSet<>();
    final Set<IElement> elementsToUpdate = new HashSet<>();
    final Set<IElement> elementsToDelete = new HashSet<>();

    boolean writeToExternalStoragePermissionNeededToInitialize;

    boolean wasFloatingActionButtonVisibleBeforeShowingHelpOverlay = false;

    OptionsMenuAgent optionsMenuAgent;
}
