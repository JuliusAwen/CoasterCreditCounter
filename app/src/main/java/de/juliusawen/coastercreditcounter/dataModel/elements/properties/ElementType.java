package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.Note;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.GroupHeader;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.IGroupHeader;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public enum ElementType
{
    IELEMENT(IElement.class),

    LOCATION(Location.class),
    PARK(Park.class),
    VISIT(Visit.class),

    IATTRACTION(IAttraction.class),

    IPROPERTY(IProperty.class),
    CREDIT_TYPE(CreditType.class),
    CATEGORY(Category.class),
    MANUFACTURER(Manufacturer.class),
    MODEL(Model.class),
    STATUS(Status.class),

    IGROUP_HEADER(IGroupHeader.class),
    GROUP_HEADER(GroupHeader.class),

    NOTE(Note.class);

    private final Class<? extends IElement> type;

    ElementType(Class<? extends IElement> type)
    {
        this.type = type;
    }

    public Class<? extends IElement> getType()
    {
        return this.type;
    }

    public static ElementType getValue(int ordinal)
    {
        if(ElementType.values().length >= ordinal)
        {
            return ElementType.values()[ordinal];
        }
        else
        {
            Log.e(String.format("ordinal [%s] out of bounds (Enum has [%s] values) - returning [%s]", ordinal, values().length, values()[0]));
            return values()[0];
        }
    }

    public String toString()
    {
        return String.format("ElementType[%s]", this.name());
    }
}
