package de.juliusawen.coastercreditcounter.globals;

import android.os.Environment;
import android.util.Log;

import java.io.File;

import static de.juliusawen.coastercreditcounter.globals.Constants.LOG_TAG;

public abstract class AppSettings
{
    public static boolean jumpToTestActivityOnStart = false;

    public static final String DATABASE_WRAPPER = Constants.DATABASE_WRAPPER_DATABASE_MOCK;
//    public static final String DATABASE_WRAPPER = Constants.DATABASE_WRAPPER_JSON_HANDLER;


    public static String contentFileName = "CoasterCreditCounterExport.json";
    public static String userSettingsFileName = "UserSettings.json";
    public static String appSettingsFileName = "AppSettings.json";

    public static File getExternalStorageDocumentsDirectory()
    {
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if(!directory.exists())
        {
            if(directory.mkdirs())
            {
                Log.i(LOG_TAG, String.format("AppSettings.getExternalStorageDocumentsDirectory:: created Directory [%s] in Downloads", directory.getName()));
            }
            else
            {
                Log.e(LOG_TAG, String.format("AppSettings.getExternalStorageDocumentsDirectory:: Directory [%s] not created!", directory.getName()));
            }
        }
        return directory;
    }
}
