package de.juliusawen.coastercreditcounter.tools;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public abstract class ConvertTool
{
    public static <T extends IElement> LinkedList<T> convertElementsToType(List<? extends IElement> elementsToConvert, Class<T> type)
    {
        LinkedList<T> convertedElements = new LinkedList<>();
        if(!elementsToConvert.isEmpty())
        {
            Log.d(String.format(Locale.getDefault(), "converting [%d] elements of type [%s] to type [%s]",
                    elementsToConvert.size(), elementsToConvert.get(0).getClass().getSimpleName(), type.getSimpleName()));

            for(IElement element : elementsToConvert)
            {
                if(type.isInstance(element))
                {
                    convertedElements.add(type.cast(element));
                }
                else
                {
                    Log.e(String.format("%s is not of type <%s>", element, type.getSimpleName()));
                }
            }
        }
        else
        {
            Log.v("no elements to convert");
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
