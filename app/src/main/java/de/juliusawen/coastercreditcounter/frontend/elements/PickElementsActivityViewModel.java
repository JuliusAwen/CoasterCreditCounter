package de.juliusawen.coastercreditcounter.frontend.elements;

import androidx.lifecycle.ViewModel;

import java.util.List;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.frontend.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.frontend.menuAgent.OptionsMenuAgent;

public class PickElementsActivityViewModel extends ViewModel
{
    RequestCode requestCode;
    boolean isSimplePick;
    List<IElement> elementsToPickFrom;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    OptionsMenuAgent optionsMenuAgent;
}
