package de.juliusawen.coastercreditcounter.backend.persistency;

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
import java.util.Set;

import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.application.Settings;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Content;

import static de.juliusawen.coastercreditcounter.globals.Constants.LOG_TAG;

public class Persistency
{
    private IDatabaseWrapper databaseWrapper;
    private final JsonHandler jsonHandler;

    private static Persistency instance;

    public static Persistency getInstance()
    {
        if(Persistency.instance == null)
        {
            Persistency.instance = new Persistency();
        }
        return instance;
    }

    private Persistency()
    {
        this.jsonHandler = new JsonHandler();

        switch(App.config.databaseWrapperToUse())
        {
            case Constants.DATABASE_WRAPPER_DATABASE_MOCK:
            {
                this.databaseWrapper = DatabaseMock.getInstance();
                break;
            }

            case Constants.DATABASE_WRAPPER_JSON_HANDLER:
            {
                this.databaseWrapper = this.jsonHandler;
                break;
            }
        }

        Log.i(Constants.LOG_TAG, "Persistency.Constructor:: <Persistency> instantiated");
    }

    IDatabaseWrapper getDatabaseWrapper()
    {
        return this.databaseWrapper;
    }

    public boolean loadContent(Content content)
    {
        Log.d(Constants.LOG_TAG, "Persistency.loadContent:: loading content...");
        return this.databaseWrapper.loadContent(content);
    }

    public boolean loadSettings(Settings settings)
    {
        Log.d(Constants.LOG_TAG, "Persistency.loadSettings:: loading settings...");

        return this.jsonHandler.loadSettings(settings);
    }

    public boolean saveSettings(Settings settings)
    {
        Log.e(Constants.LOG_TAG, "Persistency.saveSettings:: saving settings...");
        return this.jsonHandler.saveSettings(settings);
    }

    public boolean importContent()
    {
        Log.d(Constants.LOG_TAG, "Persistency.exportContent:: importing content...");
        return this.jsonHandler.importContent(App.content);
    }

    public boolean exportContent()
    {
        Log.d(Constants.LOG_TAG, "Persistency.exportContent:: exporting content...");
        return this.jsonHandler.exportContent(App.content);
    }

    public boolean synchronize(Set<IElement> elementsToCreate, Set<IElement> elementsToUpdate, Set<IElement> elementsToDelete)
    {
        int size;

        size = elementsToCreate.size();
        elementsToCreate.removeAll(elementsToDelete);
        if(elementsToCreate.size() != size)
        {
            Log.d(Constants.LOG_TAG, String.format("Persistency.synchronize:: removed [%d] elements from ElementsToCreate - since they will be deleted anyway",
                    size - elementsToCreate.size()));
        }

        size = elementsToUpdate.size();
        elementsToUpdate.removeAll(elementsToDelete);
        if(elementsToUpdate.size() != size)
        {
            Log.d(Constants.LOG_TAG, String.format("Persistency.synchronize:: removed [%d] elements from ElementsToUpdate - since they will be deleted anyway",
                    size - elementsToUpdate.size()));
        }

        size = elementsToUpdate.size();
        elementsToUpdate.removeAll(elementsToCreate);
        if(elementsToUpdate.size() != size)
        {
            Log.d(Constants.LOG_TAG, String.format("Persistency.synchronize:: removed [%d] elements from ElementsToUpdate - since they will be created in updated state anyway",
                    size - elementsToUpdate.size()));
        }

        Log.i(Constants.LOG_TAG, String.format("Persistency.synchronize:: creating [%d], updating [%d], deleting [%d] elements...",
                elementsToCreate.size(), elementsToUpdate.size(), elementsToDelete.size()));

        return this.databaseWrapper.synchronize(elementsToCreate, elementsToUpdate, elementsToDelete);
    }


    public File getExternalStorageDocumentsDirectory()
    {
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if(!directory.exists())
        {
            if(directory.mkdirs())
            {
                Log.d(LOG_TAG, String.format("Persistency.getExternalStorageDocumentsDirectory:: created Directory [%s]", directory.getAbsolutePath()));
            }
            else
            {
                Log.e(LOG_TAG, String.format("Persistency.getExternalStorageDocumentsDirectory:: Directory [%s] not created!", directory.getAbsolutePath()));
            }
        }
        return directory;
    }

    public boolean writeStringToInternalFile(String fileName, String input)
    {
        try
        {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(App.getContext().openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(input);
            outputStreamWriter.close();

            Log.d(LOG_TAG, String.format("Persistency.writeStringToExternalFile:: file [%s] written to internal storage", fileName));

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e(LOG_TAG, String.format("Persistency.writeStringToInternalFile:: Exception while writing string to internal file [%s] [%s]", fileName, e.getMessage()));
            return false;
        }
    }

    public boolean writeStringToExternalFile(File file, String input)
    {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            try
            {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(input.getBytes());
                fileOutputStream.close();

                Log.d(LOG_TAG, String.format("Persistency.writeStringToExternalFile:: file written to external storage [%s]", file.getAbsolutePath()));

                return true;
            }
            catch(FileNotFoundException e)
            {
                Log.e(LOG_TAG, String.format("Persistency.writeStringToExternalFile:: FileNotFoundException: file [%s] does not exist: [%s]", file.getAbsolutePath(), e.getMessage()));
            }
            catch(IOException e)
            {
                Log.e(LOG_TAG, String.format("Persistency.writeStringToExternalFile:: IOException: [%s]: [%s]", file.getAbsolutePath(), e.getMessage()));
            }
        }
        else
        {
            Log.e(LOG_TAG, String.format("Persistency.writeStringToExternalFile:: External storage is not writeable for [%s]", file.getAbsolutePath()));
        }

        return false;
    }

    public String readStringFromInternalFile(String fileName)
    {
        Log.d(Constants.LOG_TAG, String.format("Persistency.readStringFromInternalFile:: reading string from internal file [%s]...", fileName));

        String output = "";
        try
        {
            FileInputStream fileInputStream = App.getContext().openFileInput(fileName);

            if(fileInputStream != null)
            {
                output = this.readStringFromFile(fileInputStream);
            }
        }
        catch (FileNotFoundException e)
        {
            Log.e(LOG_TAG, String.format("Persistency.readStringFromInternalFile:: FileNotFoundException: [%s] does not exists: [%s]", fileName, e.getMessage()));
        }
        return output;
    }

    public String readStringFromExternalFile(File file)
    {
        Log.d(Constants.LOG_TAG, String.format("Persistency.readStringFromInternalFile:: reading string from external storage [%s]...", file.getAbsolutePath()));

        String output = "";
        try
        {
            FileInputStream fileInputStream = new FileInputStream(file);
            output = this.readStringFromFile(fileInputStream);
        }
        catch (FileNotFoundException e)
        {
            Log.e(LOG_TAG, String.format("Persistency.readStringFromExternalFile:: FileNotFoundException: file [%s] does not exists: [%s]", file.getAbsolutePath(), e.getMessage()));
        }
        return output;
    }

    private String readStringFromFile(FileInputStream fileInputStream)
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
            Log.e(LOG_TAG, String.format("Persistency.readStringFromFile:: IOException: [%s]", e.getMessage()));
        }

        return output;
    }

    public boolean fileExists(String absolutePath)
    {
        File file = new File(absolutePath);
        Log.v(LOG_TAG, String.format("Persistency.fileExists:: file [%s] exists:[%s]", file.getAbsolutePath(), file.exists()));
        return file.exists();
    }
}
