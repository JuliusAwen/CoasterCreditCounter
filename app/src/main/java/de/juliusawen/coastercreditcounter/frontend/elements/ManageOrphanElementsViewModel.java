package de.juliusawen.coastercreditcounter.frontend.elements;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.toolbox.ActivityDistributor;

public class ManageOrphanElementsViewModel extends ViewModel
{
    int typeToManage;
    IElement longClickedElement;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    ActivityDistributor activityDistributor;
}
