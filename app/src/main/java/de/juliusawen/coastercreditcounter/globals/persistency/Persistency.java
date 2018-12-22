package de.juliusawen.coastercreditcounter.globals.persistency;

import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.AppSettings;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Content;
import de.juliusawen.coastercreditcounter.globals.UserSettings;

public class Persistency
{
    /*

    The idea behind this is as follows:

    Persistency fetches userSettings from userSettings-json file and then decides based on userSettings which IDatabaseWrapper will be used.

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

    public void loadSettings(UserSettings userSettings)
    {
        this.databaseWrapper.loadSettings(userSettings);
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
