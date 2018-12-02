package de.juliusawen.coastercreditcounter.globals;

public class Persistency
{
    /*

    The idea behind this is as follows:

    Persistency fetches settings from settings-json file and then decides based on settings which IDatabaseWrapper will be used.

     */

    private IDatabaseWrapper databaseWrapper;

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
        switch(AppSettings.DATABASE_WRAPPER)
        {
            case Constants.DATABASE_WRAPPER_DATABASE_MOCK:
            {
                this.databaseWrapper = DatabaseMock.getInstance();
                break;
            }

            case Constants.DATABASE_WRAPPER_JSON_HANDLER:
            {

            }
        }
    }

    public void fetchContent(Content content)
    {
        this.databaseWrapper.fetchContent(content);
    }


    public void fetchSettings(Settings settings)
    {
        this.databaseWrapper.fetchSettings(settings);
    }
}
