package de.juliusawen.coastercreditcounter.tools;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public abstract class ConvertTool
{
    public static <T extends IElement> LinkedList<T> convertElementsToType(List<? extends IElement> elementsToConvert, Class<T> type)
    {
        LinkedList<T> convertedElements = new LinkedList<>();
        if(!elementsToConvert.isEmpty())
        {
            Log.d(Constants.LOG_TAG, String.format("ConvertTool.convertElementsToType:: converting [%d] elements of type [%s] to type [%s]",
                    elementsToConvert.size(), elementsToConvert.get(0).getClass().getSimpleName(), type.getSimpleName()));

            for(IElement element : elementsToConvert)
            {
                if(type.isInstance(element))
                {
                    convertedElements.add(type.cast(element));
                }
                else
                {
                    Log.e(Constants.LOG_TAG, String.format("ConvertTool.convertElementsToType:: %s is not of type <%s>", element, type.getSimpleName()));
                }

//                try
//                {
//                    convertedElements.add(type.cast(element));
//                }
//                catch(ClassCastException e)
//                {
//                    String errorMessage = String.format("%s is not of type <%s>", element, type.getSimpleName());
//                    Log.e(Constants.LOG_TAG, "ConvertTool.convertElementsToType:: " + errorMessage);
//                    throw new IllegalStateException(errorMessage + "\n" + e);
//                }
            }
        }
        else
        {
            Log.v(Constants.LOG_TAG, "ConvertTool.convertElementsToType:: no elements to convert");
        }

        return convertedElements;
    }

    public static int convertDpToPx(int dp)
    {
        return (int) (dp * App.getContext().getResources().getDisplayMetrics().density);
    }

    public static int convertPxToDp(int px)
    {
        return (int) (px / App.getContext().getResources().getDisplayMetrics().density);
    }
}
