package de.juliusawen.coastercreditcounter.backend.attractions;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;

/**
 * Marks attraction as having a category - can be Attraction-/CoasterBlueprint or CustomAttraction/-Coaster
 */
public interface ICategorized extends IElement, IAttraction {}
