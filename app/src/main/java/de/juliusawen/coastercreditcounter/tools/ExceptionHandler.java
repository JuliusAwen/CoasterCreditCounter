package de.juliusawen.coastercreditcounter.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import java.io.PrintWriter;
import java.io.StringWriter;

import de.juliusawen.coastercreditcounter.BuildConfig;
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
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));

        Log.e(stackTrace.toString());

        if(!BuildConfig.DEBUG)
        {
            Log.wrap(LogLevel.INFO, String.format("trying to restart {%s]", this.activity.getName()), '-', true);
            Intent intent = new Intent(context, activity);
            this.context.startActivity(intent);
        }
        else
        {
            ActivityManager activityManager = (ActivityManager) this.context.getSystemService(Context.ACTIVITY_SERVICE);
            if(activityManager != null)
            {
                for(ActivityManager.AppTask appTask : activityManager.getAppTasks())
                {
                    Log.i(String.format("trying to finishAndRemoveTask [%s]", appTask.toString()));
                    appTask.finishAndRemoveTask();
                }
            }
            else
            {
                Log.frame(LogLevel.INFO, "not able to get ActivityManager - exiting system with -1", '-', true);
                System.exit(-1);
            }
        }
    }
}
