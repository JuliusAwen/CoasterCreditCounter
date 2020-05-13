package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;

public class EditSimpleElementActivityViewModel extends ViewModel
{
    public RequestCode requestCode;

    public IElement elementToEdit;
    public int maxCharacterCount;
}
