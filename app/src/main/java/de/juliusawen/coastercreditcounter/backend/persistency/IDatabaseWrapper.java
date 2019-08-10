package de.juliusawen.coastercreditcounter.backend.persistency;

import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Visit;
import de.juliusawen.coastercreditcounter.globals.Content;

public interface IDatabaseWrapper
{
    boolean loadContent(Content content);
    boolean saveContent(Content content);

    boolean create(Set<IElement> elements);
    boolean update(Set<IElement> elements);
    boolean delete(Set<IElement> elements);

    boolean synchronize(Set<IElement> elementsToCreate, Set<IElement> elementsToUpdate, Set<IElement> elementsToDelete);

    int fetchTotalCoasterCreditsCount();
    int fetchTotalCoasterRidesCount();
    int fetchTotalVisitedParksCount();

    List<Visit> fetchCurrentVisits();
}
