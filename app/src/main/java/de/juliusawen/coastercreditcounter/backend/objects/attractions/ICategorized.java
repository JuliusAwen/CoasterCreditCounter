package de.juliusawen.coastercreditcounter.backend.objects.attractions;

import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;

/**
 * Marks attraction as having a category - can be Attraction-/CoasterBlueprint or CustomAttraction/-Coaster
 */
public interface ICategorized extends IElement, IAttraction {}
