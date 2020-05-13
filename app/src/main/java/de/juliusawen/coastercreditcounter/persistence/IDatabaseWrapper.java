package de.juliusawen.coastercreditcounter.persistence;

import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.application.Content;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.statistics.StatisticsGlobalTotals;

public interface IDatabaseWrapper
{
    boolean loadContent(Content content);
    boolean saveContent(Content content);

    boolean create(Set<IElement> elements);
    boolean update(Set<IElement> elements);
    boolean delete(Set<IElement> elements);

    boolean synchronize(Set<IElement> elementsToCreate, Set<IElement> elementsToUpdate, Set<IElement> elementsToDelete);

    StatisticsGlobalTotals fetchStatisticsGlobalTotals();

    List<IElement> fetchCurrentVisits();
}
