package de.juliusawen.coastercreditcounter.tools;

import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.juliusawen.coastercreditcounter.BuildConfig;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

public class ExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler
{
    private final Context context;
    private final Class<?> activity;

    public ExceptionHandler(Context context, Class<?> activity)
    {
        this.context = context;
        this.activity = activity;

        Log.frame(LogLevel.INFO, "instantiated", '#', true);
    }

    public void uncaughtException(Thread thread, Throwable exception)
    {
        Log.e(exception);
        this.dumpLog();

        if(!BuildConfig.DEBUG)
        {
            Log.wrap(LogLevel.INFO, String.format("trying to restart {%s]", this.activity.getName()), '-', true);

            Intent intent = new Intent(this.context, this.activity);
            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
            ActivityDistributor.startActivityViaIntent(this.context, intent);
        }
        else
        {
            App.terminate();
        }
    }

    private void dumpLog()
    {
        String fileName = App.config.getDumpedLogFileName();
        String input = Log.getRawOutput();

        File file = new File(App.getContext().getFilesDir(), fileName);

        FileOutputStream fileOutputStream = null;
        try
        {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(input.getBytes());
        }
        catch(FileNotFoundException e)
        {
            Log.e(String.format("FileNotFoundException: [%s] does not exist!\n[%s]", fileName, e.getMessage()));
        }
        catch(IOException e)
        {
            Log.e(String.format("IOException: writing FileOutputStream failed!\n[%s]", e.getMessage()));
        }
        finally
        {
            try
            {
                fileOutputStream.close();
            }
            catch(IOException e)
            {
                Log.e(String.format("IOException: closing FileOutputStream failed!\n[%s]", e.getMessage()));
            }
        }

        Log.w("dumped log");
    }
}
