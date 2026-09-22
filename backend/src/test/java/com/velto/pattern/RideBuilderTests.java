package com.velto.pattern;

import com.velto.model.Ride;
import com.velto.model.RideStatus;
import com.velto.pattern.builder.RideBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RideBuilderTests {

    @Test
    @DisplayName("Ride.Builder successfully constructs valid Ride object with all attributes")
    void testBuilderConstructsRide() {
        Ride ride = new Ride.Builder()
                .driverId("driver-123")
                .driverName("Ravi Driver")
                .pickup("Majestic, Bangalore")
                .destination("Electronic City, Bangalore")
                .date("2026-09-20")
                .time("08:30")
                .seats(4)
                .availableSeats(4)
                .vehicleType("Sedan")
                .price(250.0)
                .preferences("AC, Music")
                .status(RideStatus.REQUESTED)
                .build();

        assertNotNull(ride);
        assertEquals("driver-123", ride.getDriverId());
        assertEquals("Ravi Driver", ride.getDriverName());
        assertEquals("Majestic, Bangalore", ride.getPickup());
        assertEquals("Electronic City, Bangalore", ride.getDestination());
        assertEquals("2026-09-20", ride.getDate());
        assertEquals("08:30", ride.getTime());
        assertEquals(4, ride.getSeats());
        assertEquals(4, ride.getAvailableSeats());
        assertEquals("Sedan", ride.getVehicleType());
        assertEquals(250.0, ride.getPrice());
        assertEquals("AC, Music", ride.getPreferences());
        assertEquals(RideStatus.REQUESTED, ride.getStatus());
        assertNotNull(ride.getCreatedAt());
    }

    @Test
    @DisplayName("com.velto.pattern.builder.RideBuilder successfully constructs valid Ride entity")
    void testStandaloneRideBuilder() {
        Ride ride = new RideBuilder()
                .driverId("driver-456")
                .driverName("Anita Driver")
                .pickup("Shivajinagar, Pune")
                .destination("Hinjewadi, Pune")
                .date("2026-10-01")
                .time("09:15 AM")
                .seats(3)
                .availableSeats(3)
                .vehicleType("SUV")
                .price(300.0)
                .preferences("Non-Smoking")
                .status(RideStatus.REQUESTED)
                .build();

        assertNotNull(ride);
        assertEquals("driver-456", ride.getDriverId());
        assertEquals("Anita Driver", ride.getDriverName());
        assertEquals("Shivajinagar, Pune", ride.getPickup());
        assertEquals("Hinjewadi, Pune", ride.getDestination());
        assertEquals(3, ride.getSeats());
        assertEquals(3, ride.getAvailableSeats());
        assertEquals("SUV", ride.getVehicleType());
        assertEquals(300.0, ride.getPrice());
    }

    @Test
    @DisplayName("Ride.Builder throws exception when mandatory fields are missing")
    void testBuilderValidation() {
        // Missing pickup
        assertThrows(IllegalStateException.class, () ->
                new Ride.Builder()
                        .destination("Airport")
                        .date("2026-09-20")
                        .time("09:00")
                        .seats(3)
                        .price(500.0)
                        .build()
        );

        // Missing destination
        assertThrows(IllegalStateException.class, () ->
                new Ride.Builder()
                        .pickup("MG Road")
                        .date("2026-09-20")
                        .time("09:00")
                        .seats(3)
                        .price(500.0)
                        .build()
        );

        // Invalid seats (<= 0)
        assertThrows(IllegalStateException.class, () ->
                new Ride.Builder()
                        .pickup("MG Road")
                        .destination("Airport")
                        .date("2026-09-20")
                        .time("09:00")
                        .seats(0)
                        .price(500.0)
                        .build()
        );

        // Negative price
        assertThrows(IllegalStateException.class, () ->
                new Ride.Builder()
                        .pickup("MG Road")
                        .destination("Airport")
                        .date("2026-09-20")
                        .time("09:00")
                        .seats(2)
                        .price(-50.0)
                        .build()
        );

        // Standalone RideBuilder validation check
        assertThrows(IllegalStateException.class, () ->
                new RideBuilder()
                        .pickup("MG Road")
                        .destination("")
                        .date("2026-09-20")
                        .time("09:00")
                        .seats(2)
                        .price(100.0)
                        .build()
        );
    }
}
