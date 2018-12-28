package de.juliusawen.coastercreditcounter.backend.persistency;

import de.juliusawen.coastercreditcounter.globals.Content;

public interface IDatabaseWrapper
{
    boolean loadContent(Content content);
    boolean saveContent(Content content);
}
