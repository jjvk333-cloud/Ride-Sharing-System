package com.velto.pattern;

import com.velto.model.Ride;
import com.velto.pattern.factory.RideVehicleFactory;
import com.velto.pattern.factory.ride.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RideFactoryMethodTests {

    @Autowired
    private RideCreatorRegistry registry;

    @Autowired
    private RideVehicleFactory vehicleFactory;

    @Autowired
    private BikeRideCreator bikeCreator;

    @Autowired
    private AutoRideCreator autoCreator;

    @Autowired
    private SedanRideCreator sedanCreator;

    @Autowired
    private SuvRideCreator suvCreator;

    @Test
    @DisplayName("BikeRideCreator correctly instantiates Bike ride with 1 seat and bike multiplier")
    void testBikeRideCreator() {
        Ride ride = bikeCreator.createRide("d-1", "Rajesh", "Pune Station", "Hinjewadi", 15.0, "2026-10-10", "10:00", null);

        assertNotNull(ride);
        assertEquals("BIKE", ride.getVehicleType());
        assertEquals(1, ride.getSeats());
        assertEquals(15.0, ride.getDistance());
        assertTrue(ride.getPrice() > 0, "Price should be calculated dynamically");
    }

    @Test
    @DisplayName("AutoRideCreator correctly instantiates Auto ride with 3 seats")
    void testAutoRideCreator() {
        Ride ride = autoCreator.createRide("d-2", "Suresh", "Kothrud", "Viman Nagar", 12.0, "2026-10-10", "11:00", null);

        assertNotNull(ride);
        assertEquals("AUTO", ride.getVehicleType());
        assertEquals(3, ride.getSeats());
        assertEquals(12.0, ride.getDistance());
    }

    @Test
    @DisplayName("SedanRideCreator correctly instantiates Sedan ride with 4 seats")
    void testSedanRideCreator() {
        Ride ride = sedanCreator.createRide("d-3", "Amit", "Baner", "Kharadi", 20.0, "2026-10-10", "12:00", null);

        assertNotNull(ride);
        assertEquals("SEDAN", ride.getVehicleType());
        assertEquals(4, ride.getSeats());
        assertEquals(20.0, ride.getDistance());
    }

    @Test
    @DisplayName("SuvRideCreator correctly instantiates SUV ride with 6 seats")
    void testSuvRideCreator() {
        Ride ride = suvCreator.createRide("d-4", "Vikram", "Swargate", "Airport", 18.0, "2026-10-10", "14:00", null);

        assertNotNull(ride);
        assertEquals("SUV", ride.getVehicleType());
        assertEquals(6, ride.getSeats());
        assertEquals(18.0, ride.getDistance());
    }

    @Test
    @DisplayName("RideVehicleFactory delegates to polymorphic RideCreator via registry")
    void testRideVehicleFactoryDelegation() {
        Ride bikeRide = vehicleFactory.createVehicleRide("BIKE", "d-1", "Rajesh", "Pune Station", "Hinjewadi", 15.0, "2026-10-10", "10:00", null);
        Ride suvRide = vehicleFactory.createVehicleRide("SUV", "d-4", "Vikram", "Swargate", "Airport", 18.0, "2026-10-10", "14:00", null);

        assertEquals("BIKE", bikeRide.getVehicleType());
        assertEquals(1, bikeRide.getSeats());

        assertEquals("SUV", suvRide.getVehicleType());
        assertEquals(6, suvRide.getSeats());
    }
}
