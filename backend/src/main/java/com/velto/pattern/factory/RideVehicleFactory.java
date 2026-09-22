package com.velto.pattern.factory;

import com.velto.model.Ride;
import com.velto.model.RideStatus;
import com.velto.pattern.builder.RideBuilder;
import org.springframework.stereotype.Component;

/**
 * =========================================================================
 * DESIGN PATTERN 1 — FACTORY METHOD PATTERN (Creational)
 * =========================================================================
 * RideVehicleFactory acts as a specialized Factory Method for creating
 * vehicle-specific Ride configurations (Bike, Auto, Sedan, SUV) with
 * domain-specific seat capacity, pricing rates, and vehicle constraints.
 */
@Component
public class RideVehicleFactory {

    public Ride createVehicleRide(String vehicleType,
                                  String driverId,
                                  String driverName,
                                  String pickup,
                                  String destination,
                                  String date,
                                  String time,
                                  Double customPrice) {
        String type = (vehicleType != null && !vehicleType.isBlank())
                ? vehicleType.trim().toUpperCase()
                : "SEDAN";

        int defaultSeats;
        double baseFare;

        switch (type) {
            case "BIKE" -> {
                defaultSeats = 1;
                baseFare = 50.0;
            }
            case "AUTO" -> {
                defaultSeats = 3;
                baseFare = 90.0;
            }
            case "SUV" -> {
                defaultSeats = 6;
                baseFare = 280.0;
            }
            case "SEDAN" -> {
                defaultSeats = 4;
                baseFare = 180.0;
            }
            default -> {
                defaultSeats = 4;
                baseFare = 150.0;
            }
        }

        double finalPrice = (customPrice != null && customPrice > 0) ? customPrice : baseFare;

        return new RideBuilder()
                .driverId(driverId)
                .driverName(driverName != null ? driverName : "Driver")
                .pickup(pickup)
                .destination(destination)
                .date(date)
                .time(time)
                .seats(defaultSeats)
                .availableSeats(defaultSeats)
                .vehicleType(type)
                .price(finalPrice)
                .status(RideStatus.REQUESTED)
                .build();
    }
}
