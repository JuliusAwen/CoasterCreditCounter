package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.graphics.Typeface;
import android.text.SpannableString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCategory;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasManufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasModel;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasStatus;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Model;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

public class ContentRecyclerViewDecoration
{
    protected final HashMap<ElementType, Integer> specialStringResourcesByElementType = new HashMap<>();
    protected final HashMap<ElementType, Integer> typefacesByElementType = new HashMap<>();
    protected final HashMap<DetailType, Integer> typefacesByDetailType = new HashMap<>();
    protected final HashMap<DetailType, HashMap<DetailDisplayMode, Set<ElementType>>> elementTypesByDetailDisplayModeByDetailType = new HashMap<>();

    ContentRecyclerViewDecoration()
    {
        this.initializeContentTypesByDetailDisplayModeByDetailType();
        Log.frame(LogLevel.VERBOSE, "instantiated", '=', true);
    }

    private void initializeContentTypesByDetailDisplayModeByDetailType()
    {
        Log.v("initializing...");

        HashMap<DetailDisplayMode, Set<ElementType>> elementTypesByDetailDisplayModeLocation = new HashMap<>();
        elementTypesByDetailDisplayModeLocation.put(DetailDisplayMode.ABOVE, new HashSet<ElementType>());
        elementTypesByDetailDisplayModeLocation.put(DetailDisplayMode.BELOW, new HashSet<ElementType>());
        this.elementTypesByDetailDisplayModeByDetailType.put(DetailType.LOCATION, elementTypesByDetailDisplayModeLocation);

        HashMap<DetailDisplayMode, Set<ElementType>> elementTypesByDetailDisplayModeCreditType = new HashMap<>();
        elementTypesByDetailDisplayModeCreditType.put(DetailDisplayMode.ABOVE, new HashSet<ElementType>());
        elementTypesByDetailDisplayModeCreditType.put(DetailDisplayMode.BELOW, new HashSet<ElementType>());
        this.elementTypesByDetailDisplayModeByDetailType.put(DetailType.CREDIT_TYPE, elementTypesByDetailDisplayModeCreditType);

        HashMap<DetailDisplayMode, Set<ElementType>> elementTypesByDetailDisplayModeCategory = new HashMap<>();
        elementTypesByDetailDisplayModeCategory.put(DetailDisplayMode.ABOVE, new HashSet<ElementType>());
        elementTypesByDetailDisplayModeCategory.put(DetailDisplayMode.BELOW, new HashSet<ElementType>());
        this.elementTypesByDetailDisplayModeByDetailType.put(DetailType.CATEGORY, elementTypesByDetailDisplayModeCategory);

        HashMap<DetailDisplayMode, Set<ElementType>> elementTypesByDetailDisplayManufacturer = new HashMap<>();
        elementTypesByDetailDisplayManufacturer.put(DetailDisplayMode.ABOVE, new HashSet<ElementType>());
        elementTypesByDetailDisplayManufacturer.put(DetailDisplayMode.BELOW, new HashSet<ElementType>());
        this.elementTypesByDetailDisplayModeByDetailType.put(DetailType.MANUFACTURER, elementTypesByDetailDisplayManufacturer);

        HashMap<DetailDisplayMode, Set<ElementType>> elementTypesByDetailDisplayModel = new HashMap<>();
        elementTypesByDetailDisplayModel.put(DetailDisplayMode.ABOVE, new HashSet<ElementType>());
        elementTypesByDetailDisplayModel.put(DetailDisplayMode.BELOW, new HashSet<ElementType>());
        this.elementTypesByDetailDisplayModeByDetailType.put(DetailType.MODEL, elementTypesByDetailDisplayModel);

        HashMap<DetailDisplayMode, Set<ElementType>> elementTypesByDetailDisplayStatus = new HashMap<>();
        elementTypesByDetailDisplayStatus.put(DetailDisplayMode.ABOVE, new HashSet<ElementType>());
        elementTypesByDetailDisplayStatus.put(DetailDisplayMode.BELOW, new HashSet<ElementType>());
        this.elementTypesByDetailDisplayModeByDetailType.put(DetailType.STATUS, elementTypesByDetailDisplayStatus);

        HashMap<DetailDisplayMode, Set<ElementType>> elementTypesByDetailDisplayTotalRideCount = new HashMap<>();
        elementTypesByDetailDisplayTotalRideCount.put(DetailDisplayMode.ABOVE, new HashSet<ElementType>());
        elementTypesByDetailDisplayTotalRideCount.put(DetailDisplayMode.BELOW, new HashSet<ElementType>());
        this.elementTypesByDetailDisplayModeByDetailType.put(DetailType.TOTAL_RIDE_COUNT, elementTypesByDetailDisplayTotalRideCount);
    }

    public void resetStyles()
    {
        Log.d("resetting...");
        this    .clearTypefacesForElementType()
                .clearTypefacesForDetailType()
                .clearDetailTypesAndModeForElementType();
    }

    private ContentRecyclerViewDecoration clearTypefacesForElementType()
    {
        Log.v("clearing...");

        this.typefacesByElementType.clear();
        return this;
    }

    private ContentRecyclerViewDecoration clearTypefacesForDetailType()
    {
        Log.v("clearing...");
        this.typefacesByDetailType.clear();
        return this;
    }

    private ContentRecyclerViewDecoration clearDetailTypesAndModeForElementType()
    {
        Log.v("clearing...");
        this.elementTypesByDetailDisplayModeByDetailType.clear();
        this.initializeContentTypesByDetailDisplayModeByDetailType();
        return this;
    }

    public ContentRecyclerViewDecoration addTypefaceForElementType(ElementType elementType, int typeface)
    {
        if(typeface <= 3)
        {
            this.typefacesByElementType.put(elementType, typeface);
            Log.v(String.format("added %s for %s", StringTool.typefaceToString(typeface), elementType));
        }
        else
        {
            Log.e("unknown typeface");
        }

        return this;
    }

    public ContentRecyclerViewDecoration addTypefaceForDetailType(DetailType detailType, int typeface)
    {
        if(typeface <= 3)
        {
            this.typefacesByDetailType.put(detailType, typeface);
            Log.v(String.format("added %s for %s", StringTool.typefaceToString(typeface), detailType));
        }
        else
        {
            Log.e(String.format("unexpected Typeface: %s", typeface));
        }

        return this;
    }

    public ContentRecyclerViewDecoration addSpecialStringResourceForElementType(ElementType elementType, int stringResource)
    {
        this.specialStringResourcesByElementType.put(elementType, stringResource);
        Log.v(String.format("added StringResource[%s] for %s", StringTool.getString(stringResource), elementType));
        return this;
    }

    public ContentRecyclerViewDecoration addDetailTypesAndModeForContentType(ElementType elementType, DetailType detailType, DetailDisplayMode detailDisplayMode)
    {
        Log.v(String.format("added %s and %s for [%s]", detailType, detailDisplayMode, elementType));
        this.elementTypesByDetailDisplayModeByDetailType.get(detailType).get(detailDisplayMode).add(elementType);
        return this;
    }

    int getTypeface(IElement element)
    {
        for(ElementType elementType : this.typefacesByElementType.keySet())
        {
            if(elementType.getType().isAssignableFrom(element.getClass()))
            {
                int typeface = this.typefacesByElementType.get(elementType);
                Log.v(String.format("found %s for %s", StringTool.typefaceToString(typeface), elementType));
                return typeface;
            }
        }

        return Typeface.NORMAL;
    }

    String getSpecialString(IElement element)
    {
        for(ElementType elementType : this.specialStringResourcesByElementType.keySet())
        {
            if(elementType.getType().isAssignableFrom(element.getClass()))
            {
                String specialString = "";
                if(element.isVisit())
                {
                    specialString = App.getContext().getString(this.specialStringResourcesByElementType.get(ElementType.VISIT), element.getName(), element.getParent().getName());
                }
                else if(element.isProperty() && ((IProperty)element).isDefault())
                {
                    specialString = App.getContext().getString(this.specialStringResourcesByElementType.get(ElementType.IPROPERTY), element.getName());
                }

                if(specialString.isEmpty())
                {
                    return null;
                }

                Log.v(String.format("SpecialString [%s] found for %s", specialString, element));
                return specialString;
            }
        }

        return null;
    }

    Map<DetailDisplayMode, Set<DetailType>> getDetailTypesByDetailDisplayMode(IElement element)
    {
        Set<DetailType> detailTypesToDisplayAbove = new HashSet<>();
        Set<DetailType> detailTypesToDisplayBelow = new HashSet<>();

        for(DetailType detailType : this.elementTypesByDetailDisplayModeByDetailType.keySet())
        {
            for(ElementType elementType : this.elementTypesByDetailDisplayModeByDetailType.get(detailType).get(DetailDisplayMode.ABOVE))
            {
                if(elementType.getType().isInstance(element))
                {
                    detailTypesToDisplayAbove.add(detailType);
                    break;
                }
            }

            for(ElementType elementType : this.elementTypesByDetailDisplayModeByDetailType.get(detailType).get(DetailDisplayMode.BELOW))
            {
                if(elementType.getType().isInstance(element))
                {
                    detailTypesToDisplayBelow.add(detailType);
                    break;
                }
            }
        }

        HashMap<DetailDisplayMode, Set<DetailType>> detailTypesByDetailDisplayMode = new HashMap<>();
        detailTypesByDetailDisplayMode.put(DetailDisplayMode.ABOVE, detailTypesToDisplayAbove);
        detailTypesByDetailDisplayMode.put(DetailDisplayMode.BELOW, detailTypesToDisplayBelow);

        Log.v(String.format(Locale.getDefault(), "found [%d] Details to display above and [%d] to display below %s",
                detailTypesToDisplayAbove.size(),
                detailTypesToDisplayBelow.size(),
                element));
        return detailTypesByDetailDisplayMode;
    }

    SpannableString getSpannableDetailString(IElement element, Set<DetailType> detailTypes)
    {
        HashMap<String, Integer> typefacesByDetailSubString = new HashMap<>();
        HashMap<DetailType, String> detailSubStringsByDetailType = new HashMap<>();
        IElement parent = element.getParent();

        for(DetailType detailType : detailTypes)
        {
            switch(detailType)
            {
                case LOCATION:
                {
                    if(parent != null && (parent.isLocation() || parent.isPark()))
                    {
                        String locationDetail = parent.getName();
                        detailSubStringsByDetailType.put(DetailType.LOCATION, locationDetail);
                        typefacesByDetailSubString.put(locationDetail, this.typefacesByDetailType.containsKey(DetailType.LOCATION)
                                ? this.typefacesByDetailType.get(DetailType.LOCATION)
                                : Typeface.NORMAL);
                    }
                    break;
                }

                case CREDIT_TYPE:
                {
                    if(element.hasCreditType())
                    {
                        CreditType creditType = ((IHasCreditType)element).getCreditType();
                        if(creditType != null && !creditType.isDefault())
                        {
                            String creditTypeDetail = creditType.getName();
                            detailSubStringsByDetailType.put(DetailType.CREDIT_TYPE, creditTypeDetail);
                            typefacesByDetailSubString.put(creditTypeDetail, this.typefacesByDetailType.containsKey(DetailType.CREDIT_TYPE)
                                    ? this.typefacesByDetailType.get(DetailType.CREDIT_TYPE)
                                    : Typeface.NORMAL);
                        }
                    }
                    break;
                }

                case CATEGORY:
                {
                    if(element.hasCategory())
                    {
                        Category category = ((IHasCategory) element).getCategory();
                        if(category != null && !category.isDefault())
                        {
                            String categoryDetail = category.getName();
                            detailSubStringsByDetailType.put(DetailType.CATEGORY, categoryDetail);
                            typefacesByDetailSubString.put(categoryDetail, this.typefacesByDetailType.containsKey(DetailType.CATEGORY)
                                    ? this.typefacesByDetailType.get(DetailType.CATEGORY)
                                    : Typeface.NORMAL);
                        }
                    }
                    break;
                }

                case MANUFACTURER:
                {
                    if(element.hasManufacturer())
                    {
                        Manufacturer manufacturer = ((IHasManufacturer) element).getManufacturer();
                        if(manufacturer != null && !manufacturer.isDefault())
                        {
                            String manufacturerDetail = manufacturer.getName();
                            detailSubStringsByDetailType.put(DetailType.MANUFACTURER, manufacturerDetail);
                            typefacesByDetailSubString.put(manufacturerDetail, this.typefacesByDetailType.containsKey(DetailType.MANUFACTURER)
                                    ? this.typefacesByDetailType.get(DetailType.MANUFACTURER)
                                    : Typeface.NORMAL);
                        }
                    }
                    break;
                }

                case MODEL:
                {
                    if(element.hasModel())
                    {
                        Model model = ((IHasModel) element).getModel();
                        if(model != null && !model.isDefault())
                        {
                            String modelDetail = model.getName();
                            detailSubStringsByDetailType.put(DetailType.MODEL, modelDetail);
                            typefacesByDetailSubString.put(modelDetail, this.typefacesByDetailType.containsKey(DetailType.MODEL)
                                    ? this.typefacesByDetailType.get(DetailType.MODEL)
                                    : Typeface.NORMAL);
                        }
                    }
                    break;
                }

                case STATUS:
                {
                    if(element.hasStatus())
                    {
                        Status status = ((IHasStatus) element).getStatus();
                        if(status != null)
                        {
                            String statusDetail = status.getName();
                            detailSubStringsByDetailType.put(DetailType.STATUS, statusDetail);
                            typefacesByDetailSubString.put(statusDetail, this.typefacesByDetailType.containsKey(DetailType.STATUS)
                                    ? this.typefacesByDetailType.get(DetailType.STATUS)
                                    : Typeface.NORMAL);
                        }
                    }
                    break;
                }

                case TOTAL_RIDE_COUNT:
                {
                    if(element.isAttraction())
                    {
                        String totalRideCountDetail = App.getContext().getString(R.string.text_total_rides, ((IAttraction)element).fetchTotalRideCount());
                        detailSubStringsByDetailType.put(DetailType.TOTAL_RIDE_COUNT, totalRideCountDetail);
                        typefacesByDetailSubString.put(totalRideCountDetail, this.typefacesByDetailType.containsKey(DetailType.TOTAL_RIDE_COUNT)
                                ? this.typefacesByDetailType.get(DetailType.TOTAL_RIDE_COUNT)
                                : Typeface.NORMAL);
                    }
                    break;
                }

                default:
                    throw new IllegalStateException(String.format("%s for %s not found", DetailType.getValue(detailType.ordinal()), element));
            }
        }

        String orderedDetailString = this.getOrderedDetailString(detailSubStringsByDetailType);
        Log.v(String.format("DetailString[%s] built for %s", orderedDetailString, element));

        return StringTool.buildSpannableStringWithTypefaces(orderedDetailString, typefacesByDetailSubString);
    }

    private String getOrderedDetailString(HashMap<DetailType, String> detailSubstringsByDetailType)
    {
        StringBuilder stringBuilder = new StringBuilder();

        for(DetailType detailType : App.preferences.getDetailsOrder())
        {
            if(detailSubstringsByDetailType.containsKey(detailType))
            {
                if(stringBuilder.length() != 0)
                {
                    stringBuilder.append(" - ");
                }
                stringBuilder.append(detailSubstringsByDetailType.get(detailType));
            }
        }

        return stringBuilder.toString();
    }

    @Override
    public String toString()
    {
        StringBuilder string = new StringBuilder();
        int lenght = 0;

        string.append("ContentRecyclerViewDecoration:");
        lenght += 2;

        string.append(String.format("\n%sSpecialStringResources for ContentTypes:", this.getIntend(lenght)));
        lenght += 4;
        if(this.specialStringResourcesByElementType.isEmpty())
        {
            string.append(String.format("\n%s[none]", this.getIntend(lenght)));
        }
        else
        {
            for(ElementType elementType : this.specialStringResourcesByElementType.keySet())
            {
                if(this.specialStringResourcesByElementType.get(elementType) != null)
                {
                    string.append(String.format("\n%s[none]", this.getIntend(lenght)));
                }
                else
                {
                    string.append(String.format(Locale.getDefault(), "\n%s%s: SpecialStringResource[%d (%s)]",
                            this.getIntend(lenght),
                            elementType, this.specialStringResourcesByElementType.get(elementType),
                            StringTool.getString(this.specialStringResourcesByElementType.get(elementType))));
                }
            }
        }
        lenght -= 4;


        string.append(String.format("\n%sTypefaces for ElementTypes:", this.getIntend(lenght)));
        lenght += 4;
        if(this.typefacesByElementType.isEmpty())
        {
            string.append(String.format("\n%s[none]", this.getIntend(lenght)));
        }
        else
        {
            for(ElementType elementType : this.typefacesByElementType.keySet())
            {
                if(this.typefacesByElementType.get(elementType) == null)
                {
                    string.append(String.format("\n%s[none]", this.getIntend(lenght)));
                }
                else
                {
                    string.append(String.format("\n%s%s: %s", this.getIntend(lenght), elementType, StringTool.typefaceToString(this.typefacesByElementType.get(elementType))));
                }
            }
        }
        lenght -= 4;


        string.append(String.format("\n%sTypefaces for DetailTypes:", this.getIntend(lenght)));
        lenght += 4;
        if(this.typefacesByDetailType.isEmpty())
        {
            string.append(String.format("\n%s[none]", this.getIntend(lenght)));
        }
        else
        {
            for(DetailType detailType : this.typefacesByDetailType.keySet())
            {
                if(this.typefacesByDetailType.get(detailType) == null)
                {
                    string.append(String.format("\n%s[none]", this.getIntend(lenght)));
                }
                else
                {
                    string.append(String.format("\n%s%s: %s", this.getIntend(lenght), detailType, StringTool.typefaceToString(this.typefacesByDetailType.get(detailType))));
                }
            }
        }
        lenght -= 4;


        string.append(String.format("\n%sDetailTypes for DetailDisplayMode for ContentType:", this.getIntend(lenght)));
        lenght += 4;
        boolean none = true;
        for(DetailType detailType : this.elementTypesByDetailDisplayModeByDetailType.keySet())
        {
            if(!this.elementTypesByDetailDisplayModeByDetailType.get(detailType).get(DetailDisplayMode.ABOVE).isEmpty()
                    || !this.elementTypesByDetailDisplayModeByDetailType.get(detailType).get(DetailDisplayMode.BELOW).isEmpty())
            {
                none = false;
                string.append(String.format("\n%s%s", this.getIntend(lenght), detailType));
                lenght += 4;
                for(DetailDisplayMode detailDisplayMode : this.elementTypesByDetailDisplayModeByDetailType.get(detailType).keySet())
                {
                    if(!this.elementTypesByDetailDisplayModeByDetailType.get(detailType).get(detailDisplayMode).isEmpty())
                    {
                        string.append(String.format("\n%s%s", this.getIntend(lenght), detailDisplayMode));
                        lenght += 4;
                        for(ElementType elementType : this.elementTypesByDetailDisplayModeByDetailType.get(detailType).get(detailDisplayMode))
                        {
                            string.append(String.format("\n%sContentType[%s]", this.getIntend(lenght), elementType));
                        }
                        lenght -= 4;
                    }
                }
                lenght -= 4;
            }
        }

        if(none)
        {
            string.append(String.format("\n%s[none]", this.getIntend(lenght)));
        }

        return string.toString();
    }

    private String getIntend(int lenght)
    {
        StringBuilder intend = new StringBuilder();

        for(int i = 0; i <= lenght; i++)
        {
            intend.append(" ");
        }

        return intend.toString();
    }
}
