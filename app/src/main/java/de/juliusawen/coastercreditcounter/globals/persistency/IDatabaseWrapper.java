package de.juliusawen.coastercreditcounter.globals.persistency;

import de.juliusawen.coastercreditcounter.globals.Content;
import de.juliusawen.coastercreditcounter.globals.Settings;

public interface IDatabaseWrapper
{
    void loadContent(Content content);
    void loadSettings(Settings settings);
}
