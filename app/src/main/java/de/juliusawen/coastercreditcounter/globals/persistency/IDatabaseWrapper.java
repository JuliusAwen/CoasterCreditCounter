package de.juliusawen.coastercreditcounter.globals.persistency;

import de.juliusawen.coastercreditcounter.globals.Content;

public interface IDatabaseWrapper
{
    boolean loadContent(Content content);
    boolean saveContent(Content content);
}
