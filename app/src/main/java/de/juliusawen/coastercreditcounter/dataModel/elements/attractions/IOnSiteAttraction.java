package de.juliusawen.coastercreditcounter.dataModel.elements.attractions;

import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCategoryProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCreditTypeProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasManufacturerProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasStatusProperty;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;

/**
 * Marks attraction as concrete inidividual entity located at a particular location - can be StockAttraction or CustomAttraction/-Coaster
 *
 * Parent: Park
 * Children: none
 */
public interface IOnSiteAttraction extends IAttraction, IPersistable, IHasCreditTypeProperty, IHasCategoryProperty, IHasManufacturerProperty, IHasStatusProperty {}
