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
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCategory;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasManufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasModel;
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

                case BY_PARK:
                {
                    List<IAttraction> sortedAttractions = ConvertTool.convertElementsToType(elements, IAttraction.class);

                    if(sortOrder == SortOrder.ASCENDING)
                    {
                        Collections.sort(sortedAttractions, new Comparator<IAttraction>()
                        {
                            @Override
                            public int compare(IAttraction element1, IAttraction element2)
                            {
                                IElement compareElement1 = element1.getParent();
                                IElement compareElement2 = element2.getParent();

                                if(compareElement1 == null && compareElement2 == null)
                                {
                                    return 0;
                                }
                                else if(compareElement1 == null)
                                {
                                    return 1;
                                }
                                else if(compareElement2 == null)
                                {
                                    return -1;
                                }
                                else
                                {
                                    return element1.getParent().getName().compareToIgnoreCase(element2.getParent().getName());
                                }
                            }
                        });
                    }
                    else
                    {
                        Collections.sort(sortedAttractions, new Comparator<IAttraction>()
                        {
                            @Override
                            public int compare(IAttraction element1, IAttraction element2)
                            {
                                IElement compareElement1 = element1.getParent();
                                IElement compareElement2 = element2.getParent();

                                if(compareElement1 == null && compareElement2 == null)
                                {
                                    return 0;
                                }
                                else if(compareElement2 == null)
                                {
                                    return 1;
                                }
                                else if(compareElement1 == null)
                                {
                                    return -1;
                                }
                                else
                                {
                                    return element2.getParent().getName().compareToIgnoreCase(element1.getParent().getName());
                                }
                            }
                        });
                    }
                    sortedElements = ConvertTool.convertElementsToType(sortedAttractions, IElement.class);
                    break;
                }

                case BY_CREDIT_TYPE:
                {
                    List<IHasCreditType> sortedAttractions = ConvertTool.convertElementsToType(elements, IHasCreditType.class);

                    if(sortOrder == SortOrder.ASCENDING)
                    {
                        Collections.sort(sortedAttractions, new Comparator<IHasCreditType>()
                        {
                            @Override
                            public int compare(IHasCreditType element1, IHasCreditType element2)
                            {
                                IElement compareElement1 = element1.getCreditType();
                                IElement compareElement2 = element2.getCreditType();

                                if(compareElement1 == null && compareElement2 == null)
                                {
                                    return 0;
                                }
                                else if(compareElement1 == null)
                                {
                                    return 1;
                                }
                                else if(compareElement2 == null)
                                {
                                    return -1;
                                }
                                else
                                {
                                    return element1.getCreditType().getName().compareToIgnoreCase(element2.getCreditType().getName());
                                }
                            }
                        });
                    }
                    else
                    {
                        Collections.sort(sortedAttractions, new Comparator<IHasCreditType>()
                        {
                            @Override
                            public int compare(IHasCreditType element1, IHasCreditType element2)
                            {
                                IElement compareElement1 = element1.getCreditType();
                                IElement compareElement2 = element2.getCreditType();

                                if(compareElement1 == null && compareElement2 == null)
                                {
                                    return 0;
                                }
                                else if(compareElement2 == null)
                                {
                                    return 1;
                                }
                                else if(compareElement1 == null)
                                {
                                    return -1;
                                }
                                else
                                {
                                    return element2.getCreditType().getName().compareToIgnoreCase(element1.getCreditType().getName());
                                }
                            }
                        });
                    }
                    sortedElements = ConvertTool.convertElementsToType(sortedAttractions, IElement.class);
                    break;
                }

                case BY_CATEGORY:
                {
                    List<IHasCategory> sortedAttractions = ConvertTool.convertElementsToType(elements, IHasCategory.class);

                    if(sortOrder == SortOrder.ASCENDING)
                    {
                        Collections.sort(sortedAttractions, new Comparator<IHasCategory>()
                        {
                            @Override
                            public int compare(IHasCategory element1, IHasCategory element2)
                            {
                                IElement compareElement1 = element1.getCategory();
                                IElement compareElement2 = element2.getCategory();

                                if(compareElement1 == null && compareElement2 == null)
                                {
                                    return 0;
                                }
                                else if(compareElement1 == null)
                                {
                                    return 1;
                                }
                                else if(compareElement2 == null)
                                {
                                    return -1;
                                }
                                else
                                {
                                    return element1.getCategory().getName().compareToIgnoreCase(element2.getCategory().getName());
                                }
                            }
                        });
                    }
                    else
                    {
                        Collections.sort(sortedAttractions, new Comparator<IHasCategory>()
                        {
                            @Override
                            public int compare(IHasCategory element1, IHasCategory element2)
                            {
                                IElement compareElement1 = element1.getCategory();
                                IElement compareElement2 = element2.getCategory();

                                if(compareElement1 == null && compareElement2 == null)
                                {
                                    return 0;
                                }
                                else if(compareElement2 == null)
                                {
                                    return 1;
                                }
                                else if(compareElement1 == null)
                                {
                                    return -1;
                                }
                                else
                                {
                                    return element2.getCategory().getName().compareToIgnoreCase(element1.getCategory().getName());
                                }
                            }
                        });
                    }
                    sortedElements = ConvertTool.convertElementsToType(sortedAttractions, IElement.class);
                    break;
                }

                case BY_MANUFACTURER:
                {
                    List<IHasManufacturer> sortedAttractions = ConvertTool.convertElementsToType(elements, IHasManufacturer.class);

                    if(sortOrder == SortOrder.ASCENDING)
                    {
                        Collections.sort(sortedAttractions, new Comparator<IHasManufacturer>()
                        {
                            @Override
                            public int compare(IHasManufacturer element1, IHasManufacturer element2)
                            {
                                IElement compareElement1 = element1.getManufacturer();
                                IElement compareElement2 = element2.getManufacturer();

                                if(compareElement1 == null && compareElement2 == null)
                                {
                                    return 0;
                                }
                                else if(compareElement1 == null)
                                {
                                    return 1;
                                }
                                else if(compareElement2 == null)
                                {
                                    return -1;
                                }
                                else
                                {
                                    return element1.getManufacturer().getName().compareToIgnoreCase(element2.getManufacturer().getName());
                                }
                            }
                        });
                    }
                    else
                    {
                        Collections.sort(sortedAttractions, new Comparator<IHasManufacturer>()
                        {
                            @Override
                            public int compare(IHasManufacturer element1, IHasManufacturer element2)
                            {
                                IElement compareElement1 = element1.getManufacturer();
                                IElement compareElement2 = element2.getManufacturer();

                                if(compareElement1 == null && compareElement2 == null)
                                {
                                    return 0;
                                }
                                else if(compareElement2 == null)
                                {
                                    return 1;
                                }
                                else if(compareElement1 == null)
                                {
                                    return -1;
                                }
                                else
                                {
                                    return element2.getManufacturer().getName().compareToIgnoreCase(element1.getManufacturer().getName());
                                }
                            }
                        });
                    }
                    sortedElements = ConvertTool.convertElementsToType(sortedAttractions, IElement.class);
                    break;
                }

                case BY_MODEL:
                {
                    List<IHasModel> sortedAttractions = ConvertTool.convertElementsToType(elements, IHasModel.class);

                    if(sortOrder == SortOrder.ASCENDING)
                    {
                        Collections.sort(sortedAttractions, new Comparator<IHasModel>()
                        {
                            @Override
                            public int compare(IHasModel element1, IHasModel element2)
                            {
                                IElement compareElement1 = element1.getModel();
                                IElement compareElement2 = element2.getModel();

                                if(compareElement1 == null && compareElement2 == null)
                                {
                                    return 0;
                                }
                                else if(compareElement1 == null)
                                {
                                    return 1;
                                }
                                else if(compareElement2 == null)
                                {
                                    return -1;
                                }
                                else
                                {
                                    return element1.getModel().getName().compareToIgnoreCase(element2.getModel().getName());
                                }
                            }
                        });
                    }
                    else
                    {
                        Collections.sort(sortedAttractions, new Comparator<IHasModel>()
                        {
                            @Override
                            public int compare(IHasModel element1, IHasModel element2)
                            {
                                IElement compareElement1 = element1.getModel();
                                IElement compareElement2 = element2.getModel();

                                if(compareElement1 == null && compareElement2 == null)
                                {
                                    return 0;
                                }
                                else if(compareElement2 == null)
                                {
                                    return 1;
                                }
                                else if(compareElement1 == null)
                                {
                                    return -1;
                                }
                                else
                                {
                                    return element2.getModel().getName().compareToIgnoreCase(element1.getModel().getName());
                                }
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
