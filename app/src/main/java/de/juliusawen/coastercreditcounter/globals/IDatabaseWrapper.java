package de.juliusawen.coastercreditcounter.globals;

import de.juliusawen.coastercreditcounter.data.elements.Location;

public interface IDatabaseWrapper
{
    void fetchContent(Content content);
    Location fetchRootLocation();
    void fetchSettings(Settings settings);
}
