package de.juliusawen.coastercreditcounter.tools;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

import de.juliusawen.coastercreditcounter.BuildConfig;
import de.juliusawen.coastercreditcounter.application.Constants;

public class ExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler
{
    private final Context context;
    private final Class<?> activity;

    public ExceptionHandler(Context context, Class<?> activity)
    {
        this.context = context;
        this.activity = activity;

        Log.i(Constants.LOG_TAG, "ExceptionHandler.Constructor:: ExeptionHandler instantiated");
    }

    public void uncaughtException(Thread thread, Throwable exception)
    {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));

        Log.e(Constants.LOG_TAG, String.format("ExceptionHandler.uncaughtException:: %s", stackTrace.toString()));

        if(!BuildConfig.DEBUG)
        {
            Intent intent = new Intent(context, activity);
            context.startActivity(intent);
        }
        System.exit(-1);
    }
}
