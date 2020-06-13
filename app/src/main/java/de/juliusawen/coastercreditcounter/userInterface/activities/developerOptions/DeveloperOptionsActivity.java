package de.juliusawen.coastercreditcounter.userInterface.activities.developerOptions;

import android.text.SpannableStringBuilder;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import java.util.Locale;

import de.juliusawen.coastercreditcounter.BuildConfig;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.tools.logger.ILogBrokerClient;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;
import de.juliusawen.coastercreditcounter.userInterface.activities.BaseActivity;

public class DeveloperOptionsActivity extends BaseActivity implements ILogBrokerClient
{
    protected enum Mode
    {
        SHOW_BUILD_CONFIG,
        SHOW_LOG
    }

    private DeveloperOptionsViewModel viewModel;

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
        this.viewModel = new ViewModelProvider(this).get(DeveloperOptionsViewModel.class);

        this.linearLayoutShowBuildConfig = findViewById(R.id.linearLayoutDeveloperOptions_ShowBuildConfig);
        this.textViewShowBuildConfigApplicationId = findViewById(R.id.textViewShowDeveloperOptions_ShowBuildConfig_ApplicationId);
        this.textViewShowBuildConfigVersionName = findViewById(R.id.textViewDeveloperOptions_ShowBuildConfig_VersionName);
        this.textViewShowBuildConfigVersionCode = findViewById(R.id.textViewDeveloperOptions_ShowBuildConfig_VersionCode);
        this.textViewShowBuildConfigBuildType = findViewById(R.id.textViewDeveloperOptions_ShowBuildConfig_BuildType);
        this.textViewShowBuildConfigIsDebug = findViewById(R.id.textViewDeveloperOptions_ShowBuildConfig_IsDebug);

        this.scrollViewShowLog = findViewById(R.id.scrollViewDeveloperOptions_ShowLog);
        this.textViewShowLog = findViewById(R.id.textViewDeveloperOptions_ShowLog);

        super.createHelpOverlayFragment(getString(R.string.title_help, "DeveloperOptions"), "You are a developer - you can do this on your own...!");
        super.createToolbar();
        super.addToolbarHomeButton();

        super.setOptionsMenuButlerViewModel(this.viewModel);

        this.changeViewMode(Mode.SHOW_BUILD_CONFIG);
        this.showBuildConfig();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(super.getOptionsItem(item))
        {
            case SHOW_BUILD_CONFIG:
                this.changeViewMode(Mode.SHOW_BUILD_CONFIG);
                this.showBuildConfig();
                return true;

            case SHOW_LOG_VERBOSE:
                this.changeViewMode(Mode.SHOW_LOG);
                this.requestFormattedLog(LogLevel.VERBOSE);
                return true;

            case SHOW_LOG_DEBUG:
                this.changeViewMode(Mode.SHOW_LOG);
                this.requestFormattedLog(LogLevel.DEBUG);
                return true;

            case SHOW_LOG_INFO:
                this.changeViewMode(Mode.SHOW_LOG);
                this.requestFormattedLog(LogLevel.INFO);
                return true;

            case SHOW_LOG_WARNING:
                this.changeViewMode(Mode.SHOW_LOG);
                this.requestFormattedLog(LogLevel.WARNING);
                return true;

            case SHOW_LOG_ERROR:
                this.changeViewMode(Mode.SHOW_LOG);
                this.requestFormattedLog(LogLevel.ERROR);
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

    private void requestFormattedLog(LogLevel logLevel)
    {
        super.setToolbarTitleAndSubtitle(toolbarTitle, "formatting log...");
        super.showProgressBar(true);
        new Log.Formatter().execute(logLevel, this);
    }

    @Override
    public void onLogFormatted(SpannableStringBuilder formattedLog, LogLevel logLevel)
    {
        super.setToolbarTitleAndSubtitle(toolbarTitle, String.format("LogLevel = %S", logLevel));

        this.textViewShowLog.setText(formattedLog);
        this.scrollViewShowLog.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                DeveloperOptionsActivity.this.scrollViewShowLog.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 100);

        super.showProgressBar(false);
    }
}
