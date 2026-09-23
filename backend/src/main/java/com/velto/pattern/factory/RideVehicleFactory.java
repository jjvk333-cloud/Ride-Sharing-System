package com.velto.pattern.factory;

import com.velto.model.Ride;
import com.velto.pattern.factory.ride.RideCreator;
import com.velto.pattern.factory.ride.RideCreatorRegistry;
import org.springframework.stereotype.Component;

/**
 * =========================================================================
 * DESIGN PATTERN 1 — FACTORY METHOD PATTERN (Creational)
 * =========================================================================
 * RideVehicleFactory delegates creation to the appropriate GoF RideCreator
 * subclass (BikeRideCreator, AutoRideCreator, SedanRideCreator, SuvRideCreator).
 */
@Component
public class RideVehicleFactory {

    private final RideCreatorRegistry rideCreatorRegistry;

    public RideVehicleFactory(RideCreatorRegistry rideCreatorRegistry) {
        this.rideCreatorRegistry = rideCreatorRegistry;
    }

    public Ride createVehicleRide(String vehicleType,
                                  String driverId,
                                  String driverName,
                                  String pickup,
                                  String destination,
                                  String date,
                                  String time,
                                  Double customPrice) {
        return createVehicleRide(vehicleType, driverId, driverName, pickup, destination, 10.0, date, time, customPrice);
    }

    public Ride createVehicleRide(String vehicleType,
                                  String driverId,
                                  String driverName,
                                  String pickup,
                                  String destination,
                                  double distance,
                                  String date,
                                  String time,
                                  Double customPrice) {
        RideCreator creator = rideCreatorRegistry.getCreator(vehicleType);
        return creator.createRide(driverId, driverName, pickup, destination, distance, date, time, customPrice);
    }
}
