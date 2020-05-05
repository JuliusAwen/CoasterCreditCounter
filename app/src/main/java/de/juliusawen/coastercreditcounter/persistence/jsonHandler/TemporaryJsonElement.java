package de.juliusawen.coastercreditcounter.persistence.jsonHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TemporaryJsonElement
{
    public String name;
    public UUID uuid;
    public final List<UUID> childrenUuids = new ArrayList<>();
    public int day;
    public int month;
    public int year;
    public final Map<UUID, Integer> rideCountsByAttraction = new LinkedHashMap<>();
    public int untrackedRideCount;
    public UUID creditTypeUuid;
    public UUID categoryUuid;
    public UUID manufacturerUuid;
    public UUID statusUuid;
    public String noteText;
    public boolean isDefault;
}
