package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.Constants;

public class OptionsActivity extends BaseActivity
{
    @Override
    protected void setContentView()
    {
        setContentView(R.layout.activity_options);
    }

    @Override
    protected void create()
    {
        try
        {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder log = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                if(line.contains(Constants.LOG_TAG))
                {
                    log.append(line.substring(line.indexOf(Constants.LOG_TAG) + 9));
                    log.append("\n");
                }
            }

            TextView textViewOptions = findViewById(R.id.textViewOptions);
            textViewOptions.setText(log.toString());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
