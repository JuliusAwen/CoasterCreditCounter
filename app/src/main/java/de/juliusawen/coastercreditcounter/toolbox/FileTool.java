package de.juliusawen.coastercreditcounter.toolbox;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import de.juliusawen.coastercreditcounter.globals.Constants;

public abstract class FileTool
{
    public static boolean writeStringToFile(String fileName, String input, Context context)
    {
        try
        {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(input);
            outputStreamWriter.close();

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("FileTool.writeStringToFile:: Exception File [%s] [%s]", fileName, e.getMessage()));
            return false;
        }

    }

    public static String readStringFromFile(String fileName, Context context)
    {
        String output = "";

        try
        {
            InputStream inputStream = context.openFileInput(fileName);

            if(inputStream != null)
            {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while((receiveString = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                output = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e)
        {
            Log.e(Constants.LOG_TAG, String.format("FileTool.readStringFromFile:: FileNotFoundException: File [%s] not found: [%s]", fileName, e.getMessage()));
        }
        catch (IOException e)
        {
            Log.e(Constants.LOG_TAG, String.format("FileTool.readStringFromFile:: IOException: File [%s]: [%s]", fileName, e.getMessage()));
        }

        return output;
    }
}
