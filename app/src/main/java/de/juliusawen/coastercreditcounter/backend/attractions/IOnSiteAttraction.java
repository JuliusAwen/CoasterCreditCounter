package de.juliusawen.coastercreditcounter.backend.attractions;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;

/**
 * Marks attraction as concrete inidividual entity located at a particular location - can be StockAttraction or CustomAttraction/-Coaster
 *
 * Parent: Park
 * Children: none
 */
public interface IOnSiteAttraction extends IElement, IAttraction {}
