package de.juliusawen.coastercreditcounter.tools;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IProperty;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.enums.SortType;

public abstract class SortTool
{
    public static List<IElement> sortElements(List<IElement> elements, SortType sortType, SortOrder sortOrder)
    {
        List<IElement> sortedElements = new ArrayList<>(elements); //passed list stays unsorted
        if(elements.size() > 1)
        {
            switch(sortType)
            {
                case BY_NAME:
                {
                    if(sortOrder == SortOrder.ASCENDING)
                    {
                        Collections.sort(sortedElements, new Comparator<IElement>()
                        {
                            @Override
                            public int compare(IElement element1, IElement element2)
                            {
                                return element1.getName().compareToIgnoreCase(element2.getName());
                            }
                        });
                    }
                    else
                    {
                        Collections.sort(sortedElements, new Comparator<IElement>()
                        {
                            @Override
                            public int compare(IElement element1, IElement element2)
                            {
                                return element2.getName().compareToIgnoreCase(element1.getName());
                            }
                        });
                    }
                    break;
                }

                case BY_LOCATION:
                {
                    List<IAttraction> sortedAttractions = ConvertTool.convertElementsToType(elements, IAttraction.class);

                    if(sortOrder == SortOrder.ASCENDING)
                    {
                        Collections.sort(sortedAttractions, new Comparator<IAttraction>()
                        {
                            @Override
                            public int compare(IAttraction attraction1, IAttraction attraction2)
                            {
                                return attraction1.getParent().getName().compareToIgnoreCase(attraction2.getParent().getName());
                            }
                        });
                    }
                    else
                    {
                        Collections.sort(sortedAttractions, new Comparator<IAttraction>()
                        {
                            @Override
                            public int compare(IAttraction attraction1, IAttraction attraction2)
                            {
                                return attraction2.getParent().getName().compareToIgnoreCase(attraction1.getParent().getName());
                            }
                        });
                    }
                    sortedElements = ConvertTool.convertElementsToType(sortedAttractions, IElement.class);
                    break;
                }

                case BY_CREDIT_TYPE:
                {
                    List<IAttraction> sortedAttractions = ConvertTool.convertElementsToType(elements, IAttraction.class);

                    if(sortOrder == SortOrder.ASCENDING)
                    {
                        Collections.sort(sortedAttractions, new Comparator<IAttraction>()
                        {
                            @Override
                            public int compare(IAttraction attraction1, IAttraction attraction2)
                            {
                                return attraction1.getCreditType().getName().compareToIgnoreCase(attraction2.getCreditType().getName());
                            }
                        });
                    }
                    else
                    {
                        Collections.sort(sortedAttractions, new Comparator<IAttraction>()
                        {
                            @Override
                            public int compare(IAttraction attraction1, IAttraction attraction2)
                            {
                                return attraction2.getCreditType().getName().compareToIgnoreCase(attraction1.getCreditType().getName());
                            }
                        });
                    }
                    sortedElements = ConvertTool.convertElementsToType(sortedAttractions, IElement.class);
                    break;
                }

                case BY_CATEGORY:
                {
                    List<IAttraction> sortedAttractions = ConvertTool.convertElementsToType(elements, IAttraction.class);

                    if(sortOrder == SortOrder.ASCENDING)
                    {
                        Collections.sort(sortedAttractions, new Comparator<IAttraction>()
                        {
                            @Override
                            public int compare(IAttraction attraction1, IAttraction attraction2)
                            {
                                return attraction1.getCategory().getName().compareToIgnoreCase(attraction2.getCategory().getName());
                            }
                        });
                    }
                    else
                    {
                        Collections.sort(sortedAttractions, new Comparator<IAttraction>()
                        {
                            @Override
                            public int compare(IAttraction attraction1, IAttraction attraction2)
                            {
                                return attraction2.getCategory().getName().compareToIgnoreCase(attraction1.getCategory().getName());
                            }
                        });
                    }
                    sortedElements = ConvertTool.convertElementsToType(sortedAttractions, IElement.class);
                    break;
                }

                case BY_MANUFACTURER:
                {
                    List<IAttraction> sortedAttractions = ConvertTool.convertElementsToType(elements, IAttraction.class);

                    if(sortOrder == SortOrder.ASCENDING)
                    {
                        Collections.sort(sortedAttractions, new Comparator<IAttraction>()
                        {
                            @Override
                            public int compare(IAttraction attraction1, IAttraction attraction2)
                            {
                                return attraction1.getManufacturer().getName().compareToIgnoreCase(attraction2.getManufacturer().getName());
                            }
                        });
                    }
                    else
                    {
                        Collections.sort(sortedAttractions, new Comparator<IAttraction>()
                        {
                            @Override
                            public int compare(IAttraction attraction1, IAttraction attraction2)
                            {
                                return attraction2.getManufacturer().getName().compareToIgnoreCase(attraction1.getManufacturer().getName());
                            }
                        });
                    }
                    sortedElements = ConvertTool.convertElementsToType(sortedAttractions, IElement.class);
                    break;
                }
            }
            Log.i(Constants.LOG_TAG,  String.format("SortTool.sortElements:: sorted [%s] elements [%s] [%s]", elements.size(), sortType, sortOrder));
        }
        else
        {
            Log.v(Constants.LOG_TAG,  String.format("SortTool.sortElements:: not sorted: [%d] element(s) passed", elements.size()));
        }

        return sortedElements;
    }

    public static List<IElement> sortDefaultPropertyToTopAccordingToPreferences(List<IElement> properties)
    {
        List<IElement> sortedProperties = new ArrayList<>(properties);

        if(App.preferences.defaultPropertiesAlwaysAtTop())
        {
            IElement defaultProperty = null;

            for(IElement element : sortedProperties)
            {
                if(element.isProperty() && ((IProperty)element).isDefault())
                {
                    Log.d(Constants.LOG_TAG,  String.format("SortTool.sortDefaultPropertyToTopAccordingToPreferences:: sorting [%s] to top of list", element));
                    defaultProperty = element;
                    break;
                }
            }

            if(defaultProperty != null)
            {
                sortedProperties.remove(defaultProperty);
                sortedProperties.add(0, defaultProperty);
            }
        }

        return sortedProperties;
    }

    public static List<IElement> sortElementsBasedOnComparisonList(List<IElement> elementsToSort, List<IElement> comparisonList)
    {
        if(elementsToSort.size() > 1)
        {
            Log.v(Constants.LOG_TAG,  String.format("SortTool.sortElementsBasedOnComparisonList:: sorted [%d] elements based on comparison list containing [%d] elements",
                    elementsToSort.size(), comparisonList.size()));
            List<IElement> sortedElements = new ArrayList<>();
            for(IElement element : comparisonList)
            {
                if(elementsToSort.contains(element))
                {
                    sortedElements.add(elementsToSort.get(elementsToSort.indexOf(element)));
                }
            }
            return sortedElements;
        }
        else
        {
            Log.v(Constants.LOG_TAG,"SortTool.sortElementsBasedOnComparisonList:: not sorted - list contains only one element");
            return elementsToSort;
        }
    }
}
