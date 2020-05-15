package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;

public class CreateSimpleElementViewModel extends ViewModel
{
    public RequestCode requestCode;

    public int maxCharacterCount;
    public IElement createdElement;
}
