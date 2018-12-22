package de.juliusawen.coastercreditcounter.globals.persistency;

import de.juliusawen.coastercreditcounter.globals.Content;
import de.juliusawen.coastercreditcounter.globals.UserSettings;

public interface IDatabaseWrapper
{
    void loadContent(Content content);
    void loadSettings(UserSettings userSettings);
}
