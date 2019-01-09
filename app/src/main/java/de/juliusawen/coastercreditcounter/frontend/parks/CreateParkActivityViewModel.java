package de.juliusawen.coastercreditcounter.frontend.parks;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Location;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Park;

class CreateParkActivityViewModel extends ViewModel
{
    Location parentLocation;
    Park newPark;
}
