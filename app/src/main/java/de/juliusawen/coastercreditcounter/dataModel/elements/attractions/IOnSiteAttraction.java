package de.juliusawen.coastercreditcounter.dataModel.elements.attractions;

import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.IHasNote;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCategory;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasManufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasStatus;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;

/**
 * Marks attraction as concrete inidividual entity located at a particular location - can be StockAttraction or CustomAttraction/-Coaster
 *
 * Parent: Park
 * Children: none
 */
public interface IOnSiteAttraction extends IAttraction, IPersistable, IHasCreditType, IHasCategory, IHasManufacturer, IHasStatus, IHasNote {}
