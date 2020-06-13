package de.juliusawen.coastercreditcounter.tools.logger;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.SpannableStringBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.tools.StringTool;

public abstract class Log
{
    private static StringBuilder rawOutput = new StringBuilder();

    private static final String tag = Constants.LOG_TAG;
    private static LogLevel printRestrictionLogLevel = LogLevel.NONE;

    public static void restrictLogging(LogLevel logLevel, String justification)
    {
        String message = String.format("%scalled Log.restrictLogging() - printing restriced to [%s]%s",
                Log.fetchCallerTag(), logLevel, !justification.isEmpty() ? String.format("\nJustification: \"%s\"", justification) : "");
        Log.print(LogLevel.WARNING, message);

        Log.printRestrictionLogLevel = logLevel;
    }

    public static void v(String message)
    {
        String callerTag = Log.fetchCallerTag();
        Log.store(LogLevel.VERBOSE, String.format("%s%s", callerTag, message));
        Log.print(LogLevel.VERBOSE, String.format("%s%s", callerTag, message));
    }

    public static void d(String message)
    {
        String callerTag = Log.fetchCallerTag();
        Log.store(LogLevel.DEBUG, String.format("%s%s", callerTag, message));
        Log.print(LogLevel.DEBUG, String.format("%s%s", callerTag, message));
    }

    public static void i(String message)
    {
        String callerTag = Log.fetchCallerTag();
        Log.store(LogLevel.INFO, String.format("%s%s", callerTag, message));
        Log.print(LogLevel.INFO, String.format("%s%s", callerTag, message));
    }

    public static void w(String message)
    {
        String callerTag = Log.fetchCallerTag();
        Log.store(LogLevel.WARNING, String.format("%s%s", callerTag, message));
        Log.print(LogLevel.WARNING, String.format("%s%s", callerTag, message));
    }

    public static void e(String message)
    {
        String callerTag = Log.fetchCallerTag();
        Log.store(LogLevel.ERROR, String.format("%s%s", callerTag, message));
        Log.print(LogLevel.ERROR, String.format("%s%s", callerTag, message));
    }

    public static void e(String message, Throwable exception)
    {
        String callerTag = Log.fetchCallerTag();
        Log.store(LogLevel.ERROR, String.format("%s%s\n%s", callerTag, message, Log.getExceptionStackTrace(exception)));
        Log.print(LogLevel.ERROR, String.format("%s%s\n%s", callerTag, message, Log.getExceptionStackTrace(exception)));
    }

    public static void e(Throwable exception)
    {
        String callerTag = Log.fetchCallerTag();
        Log.store(LogLevel.ERROR, String.format("%s\n%s", callerTag, Log.getExceptionStackTrace(exception)));
        Log.print(LogLevel.ERROR, String.format("%s\n%s", callerTag, Log.getExceptionStackTrace(exception)));
    }

    private static String getExceptionStackTrace(Throwable exception)
    {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));

        return stackTrace.toString();
    }

    public static void frame(LogLevel logLevel, String message, Character frameChar, boolean includeCallerTag)
    {
        String callerTag = Log.fetchCallerTag();
        Log.store(logLevel, callerTag + message);

        if(includeCallerTag)
        {
            Log.print(logLevel, String.format(" \n%s", Log.frame(frameChar, callerTag + message)));
        }
        else
        {
            Log.print(logLevel, String.format("%s \n%s", callerTag, Log.frame(frameChar, message)));
        }
    }

    public static void wrap(LogLevel logLevel, String message, Character wrapChar, boolean includeCallerTag)
    {
        String callerTag = Log.fetchCallerTag();
        Log.store(logLevel, callerTag + message);

        String wrapper;

        if(includeCallerTag)
        {
            wrapper = Log.getLogDivider(wrapChar, callerTag.length() + 3 + message.length());
            Log.print(logLevel, String.format(" \n%s\n %s%s\n%s", wrapper, callerTag, message, wrapper));
        }
        else
        {
            wrapper = Log.getLogDivider(wrapChar, message.length() + 2);
            Log.print(logLevel, String.format(" %s\n%s\n%s\n%s", callerTag, wrapper, message, wrapper));
        }
    }

    private static String fetchCallerTag()
    {
        String callerTag = "";

        StackTraceElement stackTraceElement = Log.fetchStackTraceElementForCallingClass();
        if(stackTraceElement != null)
        {
            String callingSimpleClassName = Log.parseSimpleClassName(stackTraceElement.getClassName());
            callerTag = String.format("%s.%s:: ", callingSimpleClassName, stackTraceElement.getMethodName());
        }

        return callerTag;
    }

    private static StackTraceElement fetchStackTraceElementForCallingClass()
    {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stackTraceElements.length; i++)
        {
            StackTraceElement stackTraceElement = stackTraceElements[i];
            if (!stackTraceElement.getClassName().equals(Log.class.getName()) && stackTraceElement.getClassName().indexOf("java.lang.Thread") !=0)
            {
                return stackTraceElement;
            }
        }

        return null;
    }

    private static String parseSimpleClassName(String className)
    {
        if(!className.contains("."))
        {
            return className;
        }

        return className.substring(className.lastIndexOf(".") + 1);
    }

    private static String frame(Character frameChar, String message)
    {
        int messageLenght = message.length();
        String logDivider = Log.getLogDivider(frameChar, messageLenght + 6);
        return String.format("%s\n%s  %s  %s\n%s", logDivider, frameChar, message, frameChar, logDivider);
    }

    private static String getLogDivider(Character frameChar, int lenght)
    {
        if(lenght > 150)
        {
            lenght = 150;
        }

        StringBuilder logDivider = new StringBuilder();
        for(int i = 0; i < lenght; i++)
        {
            logDivider.append(frameChar);
        }

        return logDivider.toString();
    }

    private static void print(LogLevel logLevel, String message)
    {
        if(logLevel.ordinal() >= Log.printRestrictionLogLevel.ordinal())
        {
            switch(logLevel)
            {
                case VERBOSE:
                    android.util.Log.v(Log.tag, message);
                    break;

                case DEBUG:
                    android.util.Log.d(Log.tag, message);
                    break;

                case INFO:
                    android.util.Log.i(Log.tag, message);
                    break;

                case WARNING:
                    android.util.Log.w(Log.tag, message);
                    break;

                case ERROR:
                    android.util.Log.e(Log.tag, message);
                    break;
            }
        }
    }

    private static void store(LogLevel logLevel, String message)
    {
        Log.rawOutput.append(String.format("%s %s-%s\n", new Date(), logLevel.ordinal(), message));
    }

    public static class Formatter extends AsyncTask<Object, Void, ILogBrokerClient>
    {
        private SpannableStringBuilder formattedLog;
        private LogLevel requestedLogLevel;

        @Override
        protected ILogBrokerClient doInBackground(Object... params)
        {
            this.requestedLogLevel = (LogLevel) params[0];

            this.formattedLog = new SpannableStringBuilder();
            String line;
            String lineBreak = System.getProperty("line.separator");

            BufferedReader log = new BufferedReader(new StringReader(Log.rawOutput.toString()));
            int color = App.config.colorsByLogLevel.get(LogLevel.NONE);

            try
            {
                while((line = log.readLine()) != null)
                {
                    if(!line.startsWith(" "))
                    {
                        String[] substrings = line.split("-");
                        String[] dateTimeAndLogLevel = substrings[0].split(" ");
                        int logLevel = Integer.parseInt(dateTimeAndLogLevel[6]);

                        if(logLevel >= this.requestedLogLevel.ordinal())
                        {
                            color = App.config.colorsByLogLevel.get(LogLevel.getValue(logLevel));

                            String dayOfMonth = dateTimeAndLogLevel[2];
                            String month = dateTimeAndLogLevel[1];
                            String year = dateTimeAndLogLevel[5];
                            String time = dateTimeAndLogLevel[3];

                            String callerTag = substrings[1].substring(0, substrings[1].indexOf(" "));
                            String message = substrings[1].substring(substrings[1].indexOf(" ") + 1);

                            SpannableStringBuilder formattedLine = new SpannableStringBuilder();

                            formattedLine.append(StringTool.getSpannableStringWithTypeface(String.format("[%s. %s %s %s %s]",
                                    dayOfMonth, month, year, time, LogLevel.getValue(logLevel)), Typeface.ITALIC));
                            formattedLine.append(lineBreak);
                            formattedLine.append(StringTool.getSpannableStringWithTypeface(callerTag, Typeface.BOLD));
                            formattedLine.append(lineBreak);
                            formattedLine.append(message);
                            formattedLine.append(lineBreak);

                            this.formattedLog.append(StringTool.getSpannableStringBuilderWithColor(formattedLine, color));
                        }
                    }
                    else
                    {
                        this.formattedLog.append(StringTool.getSpannableStringWithColor(line, color));
                        this.formattedLog.append(lineBreak);
                    }
                }

                this.formattedLog.append("\n\n\n\n\n\n ");
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

            return (ILogBrokerClient) params[1];
        }

        @Override
        protected void onPostExecute(ILogBrokerClient client)
        {
            super.onPostExecute(client);
            client.onLogFormatted(this.formattedLog, this.requestedLogLevel);
        }
    }
}
