package de.juliusawen.coastercreditcounter.dataModel.elements.attractions;

import de.juliusawen.coastercreditcounter.persistence.IPersistable;

/**
 * Marks attraction as concrete inidividual entity located at a particular location - can be StockAttraction or CustomAttraction/-Coaster
 *
 * Parent: Park
 * Children: none
 */
public interface IOnSiteAttraction extends IAttraction, IPersistable
{}
