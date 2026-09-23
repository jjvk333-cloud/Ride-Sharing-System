package com.velto.pattern.factory.ride;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry mapping vehicle types to their respective GoF Factory Method RideCreator instances.
 */
@Component
public class RideCreatorRegistry {

    private final Map<String, RideCreator> creators = new HashMap<>();
    private final RideCreator defaultCreator;

    public RideCreatorRegistry(List<RideCreator> creatorList) {
        RideCreator fallback = null;
        for (RideCreator creator : creatorList) {
            creators.put(creator.getVehicleType().toUpperCase(), creator);
            if ("SEDAN".equalsIgnoreCase(creator.getVehicleType())) {
                fallback = creator;
            }
        }
        this.defaultCreator = fallback != null ? fallback : (!creatorList.isEmpty() ? creatorList.get(0) : null);
    }

    public RideCreator getCreator(String vehicleType) {
        if (vehicleType == null || vehicleType.isBlank()) {
            return defaultCreator;
        }
        return creators.getOrDefault(vehicleType.trim().toUpperCase(), defaultCreator);
    }
}
