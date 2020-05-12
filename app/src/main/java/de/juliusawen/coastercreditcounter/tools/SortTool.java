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
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasStatus;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IProperty;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.enums.SortType;

public abstract class SortTool
{
    public static List<IElement> sortElements(List<IElement> elements, SortType sortType, SortOrder sortOrder)
    {
        if(elements.size() <= 1)
        {
            Log.v(Constants.LOG_TAG,  String.format("SortTool.sortElements:: not sorted: [%d] element(s) passed", elements.size()));
            return elements;
        }

        List<IElement> sortedElements = new ArrayList<>(elements); //passed list stays unsorted
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
                                return compareElement1.getName().compareToIgnoreCase(compareElement2.getName());
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
                                return compareElement2.getName().compareToIgnoreCase(compareElement1.getName());
                            }
                        }
                    });
                }
                sortedElements = ConvertTool.convertElementsToType(sortedAttractions, IElement.class);
                break;
            }

            case BY_CREDIT_TYPE:
            {
                List<IHasCreditType> sortedElementsWithCreditType = ConvertTool.convertElementsToType(elements, IHasCreditType.class);
                if(sortOrder == SortOrder.ASCENDING)
                {
                    Collections.sort(sortedElementsWithCreditType, new Comparator<IHasCreditType>()
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
                                return compareElement1.getName().compareToIgnoreCase(compareElement2.getName());
                            }
                        }
                    });
                }
                else
                {
                    Collections.sort(sortedElementsWithCreditType, new Comparator<IHasCreditType>()
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
                                return compareElement2.getName().compareToIgnoreCase(compareElement1.getName());
                            }
                        }
                    });
                }
                sortedElements = ConvertTool.convertElementsToType(sortedElementsWithCreditType, IElement.class);
                break;
            }

            case BY_CATEGORY:
            {
                List<IHasCategory> sortedElementsWithCategory = ConvertTool.convertElementsToType(elements, IHasCategory.class);
                if(sortOrder == SortOrder.ASCENDING)
                {
                    Collections.sort(sortedElementsWithCategory, new Comparator<IHasCategory>()
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
                                return compareElement1.getName().compareToIgnoreCase(compareElement2.getName());
                            }
                        }
                    });
                }
                else
                {
                    Collections.sort(sortedElementsWithCategory, new Comparator<IHasCategory>()
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
                                return compareElement2.getName().compareToIgnoreCase(compareElement1.getName());
                            }
                        }
                    });
                }
                sortedElements = ConvertTool.convertElementsToType(sortedElementsWithCategory, IElement.class);
                break;
            }

            case BY_MANUFACTURER:
            {
                List<IHasManufacturer> sortedElementsWithManufacturer = ConvertTool.convertElementsToType(elements, IHasManufacturer.class);
                if(sortOrder == SortOrder.ASCENDING)
                {
                    Collections.sort(sortedElementsWithManufacturer, new Comparator<IHasManufacturer>()
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
                                return compareElement1.getName().compareToIgnoreCase(compareElement2.getName());
                            }
                        }
                    });
                }
                else
                {
                    Collections.sort(sortedElementsWithManufacturer, new Comparator<IHasManufacturer>()
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
                                return compareElement2.getName().compareToIgnoreCase(compareElement1.getName());
                            }
                        }
                    });
                }
                sortedElements = ConvertTool.convertElementsToType(sortedElementsWithManufacturer, IElement.class);
                break;
            }

            case BY_MODEL:
            {
                List<IHasModel> sortedElementsWithModel = ConvertTool.convertElementsToType(elements, IHasModel.class);
                if(sortOrder == SortOrder.ASCENDING)
                {
                    Collections.sort(sortedElementsWithModel, new Comparator<IHasModel>()
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
                                return compareElement1.getName().compareToIgnoreCase(compareElement2.getName());
                            }
                        }
                    });
                }
                else
                {
                    Collections.sort(sortedElementsWithModel, new Comparator<IHasModel>()
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
                                return compareElement2.getName().compareToIgnoreCase(compareElement1.getName());
                            }
                        }
                    });
                }
                sortedElements = ConvertTool.convertElementsToType(sortedElementsWithModel, IElement.class);
                break;
            }

            case BY_STATUS:
            {
                List<IHasStatus> sortedElementsWithStatus = ConvertTool.convertElementsToType(elements, IHasStatus.class);
                if(sortOrder == SortOrder.ASCENDING)
                {
                    Collections.sort(sortedElementsWithStatus, new Comparator<IHasStatus>()
                    {
                        @Override
                        public int compare(IHasStatus element1, IHasStatus element2)
                        {
                            IElement compareElement1 = element1.getStatus();
                            IElement compareElement2 = element2.getStatus();

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
                                return compareElement1.getName().compareToIgnoreCase(compareElement2.getName());
                            }
                        }
                    });
                }
                else
                {
                    Collections.sort(sortedElementsWithStatus, new Comparator<IHasStatus>()
                    {
                        @Override
                        public int compare(IHasStatus element1, IHasStatus element2)
                        {
                            IElement compareElement1 = element1.getStatus();
                            IElement compareElement2 = element2.getStatus();

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
                                return compareElement2.getName().compareToIgnoreCase(compareElement1.getName());
                            }
                        }
                    });
                }
                sortedElements = ConvertTool.convertElementsToType(sortedElementsWithStatus, IElement.class);
                break;
            }
        }

        Log.d(Constants.LOG_TAG,
                String.format("SortTool.sortElements:: sorted [%s] elements [%s] of type [%s] [%s]",
                        elements.size(), sortType, elements.get(0).getClass().getSimpleName(), sortOrder));
        return SortTool.sortDefaultPropertyToTopAccordingToPreferences(sortedElements);
    }

    public static List<IElement> sortDefaultPropertyToTopAccordingToPreferences(List<IElement> elements)
    {
        if(!elements.get(0).isProperty())
        {
            Log.v(Constants.LOG_TAG,  "SortTool.sortDefaultPropertyToTopAccordingToPreferences:: no Property in list");
            return elements;
        }

        List<IElement> sortedProperties = new ArrayList<>(elements);
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
