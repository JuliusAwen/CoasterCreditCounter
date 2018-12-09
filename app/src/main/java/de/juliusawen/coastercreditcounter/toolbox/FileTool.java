package de.juliusawen.coastercreditcounter.toolbox;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.AppSettings;

import static de.juliusawen.coastercreditcounter.globals.Constants.LOG_TAG;

public abstract class FileTool
{
    public static boolean writeStringToInternalFile(String fileName, String input)
    {
        try
        {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(App.applicationContext.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(input);
            outputStreamWriter.close();

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e(LOG_TAG, String.format("FileTool.writeStringToInternalFile:: Exception File [%s] [%s]", fileName, e.getMessage()));
            return false;
        }
    }

    public static String readStringFromInternalFile(String fileName)
    {
        String output = "";
        try
        {
            FileInputStream fileInputStream = App.applicationContext.openFileInput(fileName);

            if(fileInputStream != null)
            {
                output = FileTool.readStringFromFile(fileInputStream);
            }
        }
        catch (FileNotFoundException e)
        {
            Log.e(LOG_TAG, String.format("FileTool.readStringFromInternalFile:: FileNotFoundException: [%s] does not exists: [%s]", fileName, e.getMessage()));
        }
        return output;
    }

    public static String readStringFromExternalFile(String fileName)
    {
        String output = "";
        try
        {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            output = FileTool.readStringFromFile(fileInputStream);
        }
        catch (FileNotFoundException e)
        {
            Log.e(LOG_TAG, String.format("FileTool.readStringFromExternalFile:: FileNotFoundException: [%s] does not exists: [%s]", fileName, e.getMessage()));
        }
        return output;
    }
    public static String readStringFromFile(FileInputStream fileInputStream)
    {
        String output = "";

        try
        {
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString;
            StringBuilder stringBuilder = new StringBuilder();

            while((receiveString = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(receiveString);
            }

            fileInputStream.close();
            output = stringBuilder.toString();

        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, String.format("FileTool.readStringFromInternalFile:: IOException: [%s]", e.getMessage()));
        }

        return output;
    }

    public static boolean writeStringToExternalFile(String fileName, String input)
    {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            try
            {
                File file = new File(AppSettings.getExternalStorageDocumentsDirectory(), fileName);

                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(input.getBytes());
                fileOutputStream.close();

                return true;
            }
            catch(FileNotFoundException e)
            {
                Log.e(LOG_TAG, String.format("FileTool.writeStringToExternalFile:: FileNotFoundException: [%s] does not exists: [%s]", fileName, e.getMessage()));
            }
            catch(IOException e)
            {
                Log.e(LOG_TAG, String.format("FileTool.writeStringToExternalFile:: IOException: [%s]: [%s]", fileName, e.getMessage()));
            }
        }
        else
        {
            Log.e(LOG_TAG, String.format("FileTool.writeStringToExternalFile:: External storage is not writeable for [%s]!", fileName));
        }

        return false;
    }
}
