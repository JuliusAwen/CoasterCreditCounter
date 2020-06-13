package de.juliusawen.coastercreditcounter.tools.logger;

import android.text.SpannableStringBuilder;

public interface ILogBrokerClient
{
    void onLogFormatted(SpannableStringBuilder formattedLog, LogLevel logLevel);
}
