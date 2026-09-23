package com.velto.pattern.factory.ride;

import com.velto.model.Ride;

/**
 * =========================================================================
 * DESIGN PATTERN 1 — FACTORY METHOD PATTERN (Creational)
 * =========================================================================
 * RideCreator declares the factory method 'createRide' that returns a Ride object.
 * Concrete subclasses (BikeRideCreator, AutoRideCreator, SedanRideCreator, SuvRideCreator)
 * implement the method to create vehicle-specific Ride instances configured with
 * vehicle-specific seat capacities, base pricing models, and constraints.
 */
public abstract class RideCreator {

    /**
     * Factory Method to be implemented by concrete vehicle ride creators.
     */
    public abstract Ride createRide(String driverId,
                                    String driverName,
                                    String pickup,
                                    String destination,
                                    double distance,
                                    String date,
                                    String time,
                                    Double customPrice);

    public abstract String getVehicleType();

    public abstract int getDefaultSeats();

    public abstract double getVehicleMultiplier();
}
