package de.juliusawen.coastercreditcounter.globals.persistency;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TemporaryElement
{
    public String name;
    public UUID uuid;
    public UUID parentUuid;
    public List<UUID> childrenUuids = new ArrayList<>();
    public int day;
    public int month;
    public int year;
    public Map<UUID, Integer> rideCountByAttractionUuids = new LinkedHashMap<>();
    public UUID blueprintUuid;
    public UUID attractionCategoryUuid;
    public int totalRideCount;
    public boolean isDefault;
}
