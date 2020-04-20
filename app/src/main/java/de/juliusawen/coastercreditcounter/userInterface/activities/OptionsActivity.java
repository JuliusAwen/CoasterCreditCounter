package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.SpannableStringBuilder;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.tools.StringTool;

public class OptionsActivity extends BaseActivity
{
    TextView textViewLog;
    ScrollView scrollViewLog;

    SpannableStringBuilder formattedLog;

    @Override
    protected void setContentView()
    {
        setContentView(R.layout.activity_options);
    }

    @Override
    protected void create()
    {
        this.textViewLog = findViewById(R.id.textViewOptions);
        this.scrollViewLog = findViewById(R.id.scrollViewOptions);

        this.initializeShowFormattedLog();
    }

    private void initializeShowFormattedLog()
    {
        super.showProgressBar(true);
        new FormattedLogBuilder().execute(this);
    }

    private static class FormattedLogBuilder extends AsyncTask<OptionsActivity, Void, OptionsActivity>
    {
        @Override
        protected OptionsActivity doInBackground(OptionsActivity... optionsActivities)
        {
            optionsActivities[0].buildFormattedLog();
            return optionsActivities[0];
        }

        @Override
        protected void onPostExecute(OptionsActivity optionsActivity)
        {
            super.onPostExecute(optionsActivity);
            optionsActivity.finishShowFormattedLog();
        }
    }

    private void buildFormattedLog()
    {
        try
        {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            SpannableStringBuilder formattedLog = new SpannableStringBuilder();
            String line;
            String lineBreak = System.getProperty("line.separator");
            while((line = bufferedReader.readLine()) != null)
            {
                if(line.contains(Constants.LOG_TAG))
                {
                    String relevantSubstring = line.substring(line.indexOf(Constants.LOG_TAG) + 10);
                    if(relevantSubstring.contains("::"))
                    {
                        formattedLog.append(StringTool.buildSpannableString(relevantSubstring, relevantSubstring.substring(0, relevantSubstring.indexOf("::") + 2), Typeface.BOLD));
                    }
                    else
                    {
                        formattedLog.append(relevantSubstring);
                    }
                    formattedLog.append(lineBreak);
                    formattedLog.append(lineBreak);
                }
            }
            formattedLog.append("\n\n\n\n\n\n ");

            this.formattedLog = formattedLog;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private void finishShowFormattedLog()
    {
        this.textViewLog.setText(this.formattedLog);

        this.scrollViewLog.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                scrollViewLog.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 250);

        super.showProgressBar(false);
    }
}
