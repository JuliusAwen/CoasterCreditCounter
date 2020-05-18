package de.juliusawen.coastercreditcounter.tools;

import android.content.Context;
import android.content.Intent;

import java.io.PrintWriter;
import java.io.StringWriter;

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
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));

        Log.e(stackTrace.toString());

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
}
