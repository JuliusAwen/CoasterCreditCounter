package de.juliusawen.coastercreditcounter.frontend;

import java.util.HashSet;
import java.util.Set;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;

public class BaseActivityViewModel extends ViewModel
{
    public boolean isInitializingApp = false;

    public boolean helpOverlayFragmentIsVisible = false;
    public String helpOverlayFragmentTitle;
    public CharSequence helpOverlayFragmentMessage;

    Set<IElement> elementsToCreate = new HashSet<>();
    Set<IElement> elementsToUpdate = new HashSet<>();
    Set<IElement> elementsToDelete = new HashSet<>();

    boolean writeToExternalStoragePermissionNeededToInitialize;
}
