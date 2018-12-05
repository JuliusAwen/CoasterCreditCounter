package de.juliusawen.coastercreditcounter.globals.persistency;

import android.content.Context;

import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.AppSettings;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Content;
import de.juliusawen.coastercreditcounter.globals.Settings;

public class Persistency
{
    /*

    The idea behind this is as follows:

    Persistency fetches settings from settings-json file and then decides based on settings which IDatabaseWrapper will be used.

     */

    private IDatabaseWrapper databaseWrapper;
    private JsonHandler jsonHandler;

    private static Persistency instance;

    public static Persistency getInstance()
    {
        if(Persistency.instance == null)
        {
            Persistency.instance = new Persistency();
        }
        return instance;
    }

    private Persistency()
    {
        this.jsonHandler = new JsonHandler();

        switch(AppSettings.DATABASE_WRAPPER)
        {
            case Constants.DATABASE_WRAPPER_DATABASE_MOCK:
            {
                this.databaseWrapper = DatabaseMock.getInstance();
                break;
            }

            case Constants.DATABASE_WRAPPER_JSON_HANDLER:
            {
                this.databaseWrapper = this.jsonHandler;
                break;
            }
        }
    }

    public void loadContent(Content content)
    {
        this.databaseWrapper.loadContent(content);
    }

    public void loadSettings(Settings settings)
    {
        this.databaseWrapper.loadSettings(settings);
    }

    public boolean importContent()
    {
        return this.jsonHandler.importContent(App.content);
    }

    public boolean exportContent()
    {
        return this.jsonHandler.exportContent(App.content);
    }
}
