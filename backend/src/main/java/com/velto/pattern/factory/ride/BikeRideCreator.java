package com.velto.pattern.factory.ride;

import com.velto.model.Ride;
import com.velto.model.RideStatus;
import com.velto.pattern.builder.RideBuilder;
import com.velto.pattern.singleton.AppConfigSingleton;
import org.springframework.stereotype.Component;

/**
 * Concrete Creator for Bike rides in GoF Factory Method pattern.
 */
@Component
public class BikeRideCreator extends RideCreator {

    @Override
    public String getVehicleType() {
        return "BIKE";
    }

    @Override
    public int getDefaultSeats() {
        return 1;
    }

    @Override
    public double getVehicleMultiplier() {
        return 0.6;
    }

    @Override
    public Ride createRide(String driverId, String driverName, String pickup, String destination,
                           double distance, String date, String time, Double customPrice) {
        AppConfigSingleton config = AppConfigSingleton.getInstance();
        double calculatedFare = Math.round((config.getBaseFare() * 0.7 + distance * config.getPerKmRate() * getVehicleMultiplier()) * 10.0) / 10.0;
        double finalFare = (customPrice != null && customPrice > 0) ? customPrice : Math.max(30.0, calculatedFare);

        return new RideBuilder()
                .driverId(driverId)
                .driverName(driverName != null ? driverName : "Bike Pilot")
                .pickup(pickup)
                .destination(destination)
                .date(date)
                .time(time)
                .seats(getDefaultSeats())
                .availableSeats(getDefaultSeats())
                .vehicleType(getVehicleType())
                .distance(distance)
                .price(finalFare)
                .status(RideStatus.REQUESTED)
                .build();
    }
}
