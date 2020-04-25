package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.text.SpannableStringBuilder;

import androidx.lifecycle.ViewModel;

import java.io.BufferedReader;
import java.util.HashMap;

import de.juliusawen.coastercreditcounter.enums.LogLevel;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;

public class DeveloperOptionsActivityViewModel extends ViewModel
{
    public OptionsMenuAgent optionsMenuAgent;

    BufferedReader completeLog;
    HashMap<LogLevel, SpannableStringBuilder> logsByLogLevel = new HashMap<>();

    DeveloperOptionsActivity.Mode mode;
}
