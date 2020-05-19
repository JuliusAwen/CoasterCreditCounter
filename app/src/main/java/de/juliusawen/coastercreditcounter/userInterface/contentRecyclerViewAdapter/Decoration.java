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
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
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

public class Decoration
{
    protected final HashMap<Class<? extends IElement>, Integer> specialStringResourcesByContentType = new HashMap<>();
    protected final HashMap<Class<? extends IElement>, Integer> typefacesByContentType = new HashMap<>();
    protected final HashMap<DetailType, Integer> typefacesByDetailType = new HashMap<>();
    protected final HashMap<DetailType, HashMap<DetailDisplayMode, Set<Class<? extends IElement>>>> contentTypesByDetailDisplayModeByDetailType = new HashMap<>();

    Decoration()
    {
        this.initializeContentTypesByDetailDisplayModeByDetailType();
        Log.v("instantiated");
    }

    private void initializeContentTypesByDetailDisplayModeByDetailType()
    {
        Log.v("initializing...");

        HashMap<DetailDisplayMode, Set<Class<? extends IElement>>> contentTypesByDetailDisplayModeLocation = new HashMap<>();
        contentTypesByDetailDisplayModeLocation.put(DetailDisplayMode.ABOVE, new HashSet<Class<? extends IElement>>());
        contentTypesByDetailDisplayModeLocation.put(DetailDisplayMode.BELOW, new HashSet<Class<? extends IElement>>());
        this.contentTypesByDetailDisplayModeByDetailType.put(DetailType.LOCATION, contentTypesByDetailDisplayModeLocation);

        HashMap<DetailDisplayMode, Set<Class<? extends IElement>>> contentTypesByDetailDisplayModeCreditType = new HashMap<>();
        contentTypesByDetailDisplayModeCreditType.put(DetailDisplayMode.ABOVE, new HashSet<Class<? extends IElement>>());
        contentTypesByDetailDisplayModeCreditType.put(DetailDisplayMode.BELOW, new HashSet<Class<? extends IElement>>());
        this.contentTypesByDetailDisplayModeByDetailType.put(DetailType.CREDIT_TYPE, contentTypesByDetailDisplayModeCreditType);

        HashMap<DetailDisplayMode, Set<Class<? extends IElement>>> contentTypesByDetailDisplayModeCategory = new HashMap<>();
        contentTypesByDetailDisplayModeCategory.put(DetailDisplayMode.ABOVE, new HashSet<Class<? extends IElement>>());
        contentTypesByDetailDisplayModeCategory.put(DetailDisplayMode.BELOW, new HashSet<Class<? extends IElement>>());
        this.contentTypesByDetailDisplayModeByDetailType.put(DetailType.CATEGORY, contentTypesByDetailDisplayModeCategory);

        HashMap<DetailDisplayMode, Set<Class<? extends IElement>>> contentTypesByDetailDisplayManufacturer = new HashMap<>();
        contentTypesByDetailDisplayManufacturer.put(DetailDisplayMode.ABOVE, new HashSet<Class<? extends IElement>>());
        contentTypesByDetailDisplayManufacturer.put(DetailDisplayMode.BELOW, new HashSet<Class<? extends IElement>>());
        this.contentTypesByDetailDisplayModeByDetailType.put(DetailType.MANUFACTURER, contentTypesByDetailDisplayManufacturer);

        HashMap<DetailDisplayMode, Set<Class<? extends IElement>>> contentTypesByDetailDisplayModel = new HashMap<>();
        contentTypesByDetailDisplayModel.put(DetailDisplayMode.ABOVE, new HashSet<Class<? extends IElement>>());
        contentTypesByDetailDisplayModel.put(DetailDisplayMode.BELOW, new HashSet<Class<? extends IElement>>());
        this.contentTypesByDetailDisplayModeByDetailType.put(DetailType.MODEL, contentTypesByDetailDisplayModel);

        HashMap<DetailDisplayMode, Set<Class<? extends IElement>>> contentTypesByDetailDisplayStatus = new HashMap<>();
        contentTypesByDetailDisplayStatus.put(DetailDisplayMode.ABOVE, new HashSet<Class<? extends IElement>>());
        contentTypesByDetailDisplayStatus.put(DetailDisplayMode.BELOW, new HashSet<Class<? extends IElement>>());
        this.contentTypesByDetailDisplayModeByDetailType.put(DetailType.STATUS, contentTypesByDetailDisplayStatus);

        HashMap<DetailDisplayMode, Set<Class<? extends IElement>>> contentTypesByDetailDisplayTotalRideCount = new HashMap<>();
        contentTypesByDetailDisplayTotalRideCount.put(DetailDisplayMode.ABOVE, new HashSet<Class<? extends IElement>>());
        contentTypesByDetailDisplayTotalRideCount.put(DetailDisplayMode.BELOW, new HashSet<Class<? extends IElement>>());
        this.contentTypesByDetailDisplayModeByDetailType.put(DetailType.TOTAL_RIDE_COUNT, contentTypesByDetailDisplayTotalRideCount);
    }

    private void resetStyles()
    {
        Log.v("resetting...");
        this    .clearTypefacesForContentType()
                .clearTypefacesForDetailType()
                .clearDetailTypesAndModeForContentType();
    }

    private Decoration clearTypefacesForContentType()
    {
        Log.v("clearing...");

        this.typefacesByContentType.clear();
        return this;
    }

    private Decoration clearTypefacesForDetailType()
    {
        Log.v("clearing...");
        this.typefacesByDetailType.clear();
        return this;
    }

    private Decoration clearDetailTypesAndModeForContentType()
    {
        Log.v("clearing...");
        this.contentTypesByDetailDisplayModeByDetailType.clear();
        this.initializeContentTypesByDetailDisplayModeByDetailType();
        return this;
    }

    public Decoration addTypefaceForContentType(Class<? extends IElement> contentType, int typeface)
    {
        if(typeface <= 3)
        {
            this.typefacesByContentType.put(contentType, typeface);
            Log.v(String.format("added [%s] for [%s]", StringTool.typefaceToString(typeface), contentType.getSimpleName()));
        }
        else
        {
            Log.e("unknown typeface");
        }

        return this;
    }

    public Decoration addTypefaceForDetailType(DetailType type, int typeface)
    {
        if(typeface <= 3)
        {
            this.typefacesByDetailType.put(type, typeface);
            Log.v(String.format("added [%s] for [%s]", StringTool.typefaceToString(typeface), type));
        }
        else
        {
            Log.e(String.format("unknown typeface[%s]", StringTool.typefaceToString(typeface)));
        }

        return this;
    }

    public Decoration addSpecialStringResourceForType(Class<? extends IElement> contentType, int stringResource)
    {
        Log.v(String.format(Locale.getDefault(), "added StringResource[%d (%s)] for [%s]", stringResource, StringTool.getString(stringResource), contentType.getSimpleName()));
        this.specialStringResourcesByContentType.put(contentType, stringResource);
        return this;
    }

    public Decoration addDetailTypesAndModeForContentType(Class<? extends IElement> contentType, DetailType detailType, DetailDisplayMode detailDisplayMode)
    {
        Log.v(String.format("added DetailType[%s] and DetailDisplayMode[%s] for [%s]", detailType, detailDisplayMode, contentType.getSimpleName()));
        this.contentTypesByDetailDisplayModeByDetailType.get(detailType).get(detailDisplayMode).add(contentType);
        return this;
    }

    int getTypeface(IElement element)
    {
        for(Class<? extends IElement> type : this.typefacesByContentType.keySet())
        {
            if(type.isAssignableFrom(element.getClass()))
            {
                int typeface = this.typefacesByContentType.get(type);
                Log.v(String.format("found Typeface [%s] for [%s]", StringTool.typefaceToString(typeface), type.getSimpleName()));
                return typeface;
            }
        }

        return Typeface.NORMAL;
    }

    String getSpecialString(IElement element)
    {
        for(Class<? extends IElement> type : this.specialStringResourcesByContentType.keySet())
        {
            if(type.isAssignableFrom(element.getClass()))
            {
                String specialString = "";
                if(element.isVisit())
                {
                    specialString = App.getContext().getString(this.specialStringResourcesByContentType.get(Visit.class), element.getName(), element.getParent().getName());
                }
                else if(element.isProperty() && ((IProperty)element).isDefault())
                {
                    specialString = App.getContext().getString(this.specialStringResourcesByContentType.get(IProperty.class), element.getName());
                }

                Log.v(String.format("SpecialString [%s] found for [%s]", specialString, element));
                return specialString;
            }
        }

        return null;
    }

    Map<DetailDisplayMode, Set<DetailType>> getDetailTypesByDetailDisplayMode(IElement element)
    {
        Set<DetailType> detailTypesToDisplayAbove = new HashSet<>();
        Set<DetailType> detailTypesToDisplayBelow = new HashSet<>();

        for(DetailType detailType : this.contentTypesByDetailDisplayModeByDetailType.keySet())
        {
            for(Class<? extends IElement> contentType : this.contentTypesByDetailDisplayModeByDetailType.get(detailType).get(DetailDisplayMode.ABOVE))
            {
                if(contentType.isInstance(element))
                {
                    detailTypesToDisplayAbove.add(detailType);
                    break;
                }
            }

            for(Class<? extends IElement> contentType : this.contentTypesByDetailDisplayModeByDetailType.get(detailType).get(DetailDisplayMode.BELOW))
            {
                if(contentType.isInstance(element))
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
                    throw new IllegalStateException(String.format("no DetailType[%s] for %s found", DetailType.getValue(detailType.ordinal()), element));
            }
        }

        String orderedDetailString = this.getOrderedDetailString(detailSubStringsByDetailType);
        Log.v(String.format("DetailString [%s] built for %s", orderedDetailString, element));

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
        if(this.specialStringResourcesByContentType.isEmpty())
        {
            string.append(String.format("\n%s[none]", this.getIntend(lenght)));
        }
        else
        {
            for(Class<? extends IElement> contentType : this.specialStringResourcesByContentType.keySet())
            {
                if(this.specialStringResourcesByContentType.get(contentType) != null)
                {
                    string.append(String.format("\n%s[none]", this.getIntend(lenght)));
                }
                else
                {
                    string.append(String.format(Locale.getDefault(), "\n%sContentType[%s]: SpecialStringResource[%d (%s)]",
                            this.getIntend(lenght),
                            contentType.getSimpleName(), this.specialStringResourcesByContentType.get(contentType),
                            StringTool.getString(this.specialStringResourcesByContentType.get(contentType))));
                }
            }
        }
        lenght -= 4;


        string.append(String.format("\n%sTypefaces for ContentTypes:", this.getIntend(lenght)));
        lenght += 4;
        if(this.typefacesByContentType.isEmpty())
        {
            string.append(String.format("\n%s[none]", this.getIntend(lenght)));
        }
        else
        {
            for(Class<? extends IElement> contentType : this.typefacesByContentType.keySet())
            {
                if(this.typefacesByContentType.get(contentType) == null)
                {
                    string.append(String.format("\n%s[none]", this.getIntend(lenght)));
                }
                else
                {
                    string.append(String.format("\n%sContentType[%s]: Typeface[%s]",
                            this.getIntend(lenght),
                            contentType.getSimpleName(),
                            StringTool.typefaceToString(this.typefacesByContentType.get(contentType))));
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
                    string.append(String.format("\n%sDetailType[%s]: Typeface[%s]",
                            this.getIntend(lenght),
                            detailType,
                            StringTool.typefaceToString(this.typefacesByDetailType.get(detailType))));
                }
            }
        }
        lenght -= 4;


        string.append(String.format("\n%sDetailTypes for DetailDisplayMode for ContentType:", this.getIntend(lenght)));
        lenght += 4;
        boolean none = true;
        for(DetailType detailType : this.contentTypesByDetailDisplayModeByDetailType.keySet())
        {
            if(!this.contentTypesByDetailDisplayModeByDetailType.get(detailType).get(DetailDisplayMode.ABOVE).isEmpty()
                    || !this.contentTypesByDetailDisplayModeByDetailType.get(detailType).get(DetailDisplayMode.BELOW).isEmpty())
            {
                none = false;
                string.append(String.format("\n%sDetailType[%s]", this.getIntend(lenght), detailType));
                lenght += 4;
                for(DetailDisplayMode detailDisplayMode : this.contentTypesByDetailDisplayModeByDetailType.get(detailType).keySet())
                {
                    if(!this.contentTypesByDetailDisplayModeByDetailType.get(detailType).get(detailDisplayMode).isEmpty())
                    {
                        string.append(String.format("\n%sDetailDisplayMode[%s]", this.getIntend(lenght), detailDisplayMode));
                        lenght += 4;
                        for(Class<? extends IElement> contentType : this.contentTypesByDetailDisplayModeByDetailType.get(detailType).get(detailDisplayMode))
                        {
                            string.append(String.format("\n%sContentType[%s]", this.getIntend(lenght), contentType.getSimpleName()));
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
