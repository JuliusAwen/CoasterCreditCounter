package de.juliusawen.coastercreditcounter.tools.logger;

import java.io.PrintWriter;
import java.io.StringWriter;

import de.juliusawen.coastercreditcounter.application.Constants;

public final class Log
{
    private static final String tag = Constants.LOG_TAG;
    private static LogLevel printRestrictionLogLevel = LogLevel.NONE;

    public static void restrictLogging(LogLevel logLevel, String justification)
    {
        String message = String.format("called Log.restrictLogging() - printing restriced to [%s]%s",
                logLevel, !justification.isEmpty() ? String.format("\nJustification: \"%s\"", justification) : "");
        Log.print(LogLevel.WARNING, message, true);

        Log.printRestrictionLogLevel = logLevel;
    }

    public static void v(String message)
    {
        Log.print(LogLevel.VERBOSE, message, true);
    }

    public static void d(String message)
    {
        Log.print(LogLevel.DEBUG, message, true);
    }

    public static void i(String message)
    {
        Log.print(LogLevel.INFO, message, true);
    }

    public static void w(String message)
    {
        Log.print(LogLevel.WARNING, message, true);
    }

    public static void e(String message)
    {
        Log.print(LogLevel.ERROR, message, true);
    }

    public static void e(String message, Throwable exception)
    {
        Log.print(LogLevel.ERROR, String.format("%s\n%s", message, Log.getExceptionStackTrace(exception)), true);
    }

    public static void e(Throwable exception)
    {
        Log.print(LogLevel.ERROR, Log.getExceptionStackTrace(exception), true);
    }

    private static String getExceptionStackTrace(Throwable exception)
    {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));

        return stackTrace.toString();
    }

    private static void print(LogLevel logLevel, String message, boolean printCallerTag)
    {
        if(logLevel.ordinal() >= Log.printRestrictionLogLevel.ordinal())
        {
            if(printCallerTag)
            {
                message = Log.fetchCallerTag() + message;
            }

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

    public static void frame(LogLevel logLevel, String message, Character frameChar, boolean includeCallerTag)
    {
        if(includeCallerTag)
        {
            Log.print(logLevel, String.format(" \n%s", Log.frame(frameChar, Log.fetchCallerTag() + message)), !includeCallerTag);
        }
        else
        {
            Log.print(logLevel, String.format(" \n%s", Log.frame(frameChar, message)), !includeCallerTag);
        }
    }

    public static void wrap(LogLevel logLevel, String message, Character wrapChar, boolean includeCallerTag)
    {
        String wrapper = Log.getLogDivider(wrapChar, 32);

        if(includeCallerTag)
        {
            Log.print(logLevel, String.format("\n%s\n%s%s\n%s", wrapper, Log.fetchCallerTag(), message, wrapper), !includeCallerTag);
        }
        else
        {
            Log.print(logLevel, String.format("\n%s\n%s\n%s", wrapper, message, wrapper), !includeCallerTag);
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
        StringBuilder decorativeBorder = new StringBuilder();

        for(int i = 0; i < lenght; i++)
        {
            decorativeBorder.append(frameChar);
        }

        return decorativeBorder.toString();
    }
}
