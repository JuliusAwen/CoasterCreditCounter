package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.SpannableStringBuilder;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.BuildConfig;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.enums.LogLevel;
import de.juliusawen.coastercreditcounter.tools.StringTool;

public class DeveloperOptionsActivity extends BaseActivity
{
    protected enum Mode
    {
        SHOW_BUILD_CONFIG,
        SHOW_LOG
    }

    private DeveloperOptionsActivityViewModel viewModel;

    private String toolbarTitle = "DevelopersOptions";

    private LinearLayout linearLayoutShowBuildConfig;
    private TextView textViewShowBuildConfigApplicationId;
    private TextView textViewShowBuildConfigVersionCode;
    private TextView textViewShowBuildConfigVersionName;
    private TextView textViewShowBuildConfigBuildType;
    private TextView textViewShowBuildConfigIsDebug;

    private ScrollView scrollViewShowLog;
    private TextView textViewShowLog;

    @Override
    protected void setContentView()
    {
        setContentView(R.layout.activity_developer_options);
    }

    @Override
    protected void create()
    {
        this.viewModel = new ViewModelProvider(this).get(DeveloperOptionsActivityViewModel.class);

        this.linearLayoutShowBuildConfig = findViewById(R.id.linearLayoutDeveloperOptions_ShowBuildConfig);
        this.textViewShowBuildConfigApplicationId = findViewById(R.id.textViewShowDeveloperOptions_ShowBuildConfig_ApplicationId);
        this.textViewShowBuildConfigVersionName = findViewById(R.id.textViewDeveloperOptions_ShowBuildConfig_VersionName);
        this.textViewShowBuildConfigVersionCode = findViewById(R.id.textViewDeveloperOptions_ShowBuildConfig_VersionCode);
        this.textViewShowBuildConfigBuildType = findViewById(R.id.textViewDeveloperOptions_ShowBuildConfig_BuildType);
        this.textViewShowBuildConfigIsDebug = findViewById(R.id.textViewDeveloperOptions_ShowBuildConfig_IsDebug);

        this.scrollViewShowLog = findViewById(R.id.scrollViewDeveloperOptions_ShowLog);
        this.textViewShowLog = findViewById(R.id.textViewDeveloperOptions_ShowLog);

        super.createHelpOverlayFragment(getString(R.string.title_help, "DeveloperOptions"), "You are a developer - you can do it on your own...!");
        super.createToolbar()
                .addToolbarHomeButton();

        super.getOptionsMenuButler().setViewModel(this.viewModel);

        this.changeViewMode(Mode.SHOW_BUILD_CONFIG);
        this.showBuildConfig();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(super.getOptionsMenuButler().getOptionsItem(item))
        {
            case SHOW_BUILD_CONFIG:
                this.changeViewMode(Mode.SHOW_BUILD_CONFIG);
                this.showBuildConfig();
                return true;

            case SHOW_LOG_VERBOSE:
                this.changeViewMode(Mode.SHOW_LOG);
                this.showLog(LogLevel.VERBOSE);
                return true;

            case SHOW_LOG_DEBUG:
                this.changeViewMode(Mode.SHOW_LOG);
                this.showLog(LogLevel.DEBUG);
                return true;

            case SHOW_LOG_INFO:
                this.changeViewMode(Mode.SHOW_LOG);
                this.showLog(LogLevel.INFO);
                return true;

            case SHOW_LOG_WARNING:
                this.changeViewMode(Mode.SHOW_LOG);
                this.showLog(LogLevel.WARNING);
                return true;

            case SHOW_LOG_ERROR:
                this.changeViewMode(Mode.SHOW_LOG);
                this.showLog(LogLevel.ERROR);
                return true;

            case SHOW_LOG:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeViewMode(Mode mode)
    {
        this.viewModel.mode = mode;

        this.linearLayoutShowBuildConfig.setVisibility(View.GONE);
        this.scrollViewShowLog.setVisibility(View.GONE);

        switch(mode)
        {
            case SHOW_BUILD_CONFIG:
            {
                this.linearLayoutShowBuildConfig.setVisibility(View.VISIBLE);
                break;
            }

            case SHOW_LOG:
            {
                this.scrollViewShowLog.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    private void showBuildConfig()
    {
        super.setToolbarTitleAndSubtitle(toolbarTitle, "BuildConfig");

        this.textViewShowBuildConfigApplicationId.setText(BuildConfig.APPLICATION_ID);
        this.textViewShowBuildConfigVersionName.setText(String.format("VersionName = %s", BuildConfig.VERSION_NAME));
        this.textViewShowBuildConfigVersionCode.setText(String.format(Locale.getDefault(), "VersionCode = %d", BuildConfig.VERSION_CODE));
        this.textViewShowBuildConfigBuildType.setText(String.format("BuildType = %s", BuildConfig.BUILD_TYPE));
        this.textViewShowBuildConfigIsDebug.setText(String.format("IsDebug = %S", BuildConfig.DEBUG));
    }

    private void showLog(LogLevel logLevel)
    {
        super.showProgressBar(true);
        new LogBuilder().execute(this, logLevel);
    }

    private static class LogBuilder extends AsyncTask<Object, Void, DeveloperOptionsActivity>
    {
        private LogLevel logLevel;
        private SpannableStringBuilder formattedLog;

        @Override
        protected DeveloperOptionsActivity doInBackground(Object... params)
        {
            DeveloperOptionsActivity developerOptionsActivity = (DeveloperOptionsActivity)params[0];
            this.logLevel = (LogLevel)params[1];

            BufferedReader log = null;
            try
            {
                 log = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("logcat -d").getInputStream()));
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

            this.formattedLog = developerOptionsActivity.formatLog(log, this.logLevel);

            return developerOptionsActivity;
        }

        @Override
        protected void onPostExecute(DeveloperOptionsActivity developerOptionsActivity)
        {
            super.onPostExecute(developerOptionsActivity);
            developerOptionsActivity.showProgressBar(false);
            developerOptionsActivity.showFormattedLog(this.logLevel, this.formattedLog);
        }
    }

    private SpannableStringBuilder formatLog(BufferedReader log, LogLevel logLevel)
    {
        SpannableStringBuilder formattedLog = new SpannableStringBuilder();
        String line;
        String lineBreak = System.getProperty("line.separator");

        try
        {
            while((line = log.readLine()) != null)
            {
                if(line.contains(Constants.LOG_TAG))
                {
                    if(line.contains("::"))
                    {
                        String[] substringsLine = line.split("::");
                        String[] substringsHeader = substringsLine[0].split(" ");
                        String logLevelAbbreviation = substringsHeader[4];

                        boolean logLine = false;
                        switch(logLevel)
                        {
                            case VERBOSE:
                                logLine = true;
                                break;

                            case DEBUG:
                                if(logLevelAbbreviation.equals("D"))
                                {
                                    logLine = true;
                                    break;
                                }

                            case INFO:
                                if(logLevelAbbreviation.equals("I"))
                                {
                                    logLine = true;
                                    break;
                                }

                            case WARNING:
                                if(logLevelAbbreviation.equals("W"))
                                {
                                    logLine = true;
                                    break;
                                }

                            case ERROR:
                                if(logLevelAbbreviation.equals("E"))
                                {
                                    logLine = true;
                                }
                        }

                        if(logLine)
                        {
                            formattedLog.append(StringTool.getSpannableStringWithTypeface(String.format("[%s %s] [%s] ",
                                    substringsHeader[0], substringsHeader[1], logLevelAbbreviation), Typeface.BOLD_ITALIC));

                            formattedLog.append(StringTool.getSpannableStringWithTypeface(String.format("%s ::", substringsHeader[10]), Typeface.BOLD));
                            formattedLog.append(lineBreak);
                            formattedLog.append(substringsLine[1].substring(1));
                            formattedLog.append(lineBreak);
                        }
                    }
                    else
                    {
                        formattedLog.append(StringTool.getSpannableStringWithColor(line, Color.BLUE));
                        formattedLog.append(lineBreak);
                    }

                }
                else if(line.contains("EXCEPTION"))
                {
                    formattedLog.append(StringTool.getSpannableStringWithColor(line, Color.RED));
                    formattedLog.append(lineBreak);
                }
            }

            formattedLog.append("\n\n\n\n\n\n ");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return formattedLog;
    }

    private void showFormattedLog(LogLevel logLevel, SpannableStringBuilder log)
    {
        super.setToolbarTitleAndSubtitle(toolbarTitle, String.format("LogLevel = %S", logLevel));
        this.textViewShowLog.setText(log);

        this.scrollViewShowLog.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                scrollViewShowLog.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 100);
    }
}
