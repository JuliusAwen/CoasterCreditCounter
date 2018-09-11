package de.juliusawen.coastercreditcounter.globals;

public interface IDatabaseWrapper
{
    void fetchContent(Content content);
    void fetchSettings(Settings settings);
}
