package com.velto.pattern.factory.ride;

import com.velto.model.Ride;
import com.velto.model.RideStatus;
import com.velto.pattern.builder.RideBuilder;
import com.velto.pattern.singleton.AppConfigSingleton;
import org.springframework.stereotype.Component;

/**
 * Concrete Creator for SUV rides in GoF Factory Method pattern.
 */
@Component
public class SuvRideCreator extends RideCreator {

    @Override
    public String getVehicleType() {
        return "SUV";
    }

    @Override
    public int getDefaultSeats() {
        return 6;
    }

    @Override
    public double getVehicleMultiplier() {
        return 1.6;
    }

    @Override
    public Ride createRide(String driverId, String driverName, String pickup, String destination,
                           double distance, String date, String time, Double customPrice) {
        AppConfigSingleton config = AppConfigSingleton.getInstance();
        double calculatedFare = Math.round((config.getBaseFare() * 1.5 + distance * config.getPerKmRate() * getVehicleMultiplier()) * 10.0) / 10.0;
        double finalFare = (customPrice != null && customPrice > 0) ? customPrice : Math.max(140.0, calculatedFare);

        return new RideBuilder()
                .driverId(driverId)
                .driverName(driverName != null ? driverName : "SUV Captain")
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
