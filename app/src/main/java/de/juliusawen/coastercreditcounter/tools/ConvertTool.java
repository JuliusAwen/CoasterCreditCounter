package de.juliusawen.coastercreditcounter.tools;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.globals.Constants;

public abstract class ConvertTool
{
    public static <T extends IElement> List<T> convertElementsToType(List<? extends IElement> elements, Class<T> type)
    {
        Log.v(Constants.LOG_TAG,String.format("Element.convertElementsToType:: casting [%d] elements to type <%s>", elements.size(), type.getSimpleName()));

        List<T> returnList = new ArrayList<>();
        for(IElement element : elements)
        {
            try
            {
                returnList.add(type.cast(element));
            }
            catch(ClassCastException e)
            {
                String errorMessage = String.format("%s is not of type <%s>", element, type.getSimpleName());
                Log.v(Constants.LOG_TAG, "Element.convertElementsToType:: " + errorMessage);
                throw new IllegalStateException(errorMessage + "\n" + e);
            }
        }
        return returnList;
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
