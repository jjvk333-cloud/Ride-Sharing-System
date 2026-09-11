package com.velto.pattern;

import com.velto.exception.InvalidRideStateException;
import com.velto.model.Ride;
import com.velto.model.RideStatus;
import com.velto.pattern.state.RideContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RideStateTests {

    private Ride createSampleRide() {
        return new Ride.Builder()
                .driverId("d-1")
                .driverName("Driver D")
                .pickup("Point A")
                .destination("Point B")
                .date("2026-10-01")
                .time("10:00")
                .seats(4)
                .price(100.0)
                .status(RideStatus.REQUESTED)
                .build();
    }

    @Test
    @DisplayName("Valid lifecycle state transitions succeed in correct order")
    void testValidLifecycleTransitions() {
        Ride ride = createSampleRide();
        RideContext context = RideContext.fromRide(ride);

        assertEquals(RideStatus.REQUESTED, ride.getStatus());

        // 1. REQUESTED -> CONFIRMED
        context.confirm();
        assertEquals(RideStatus.CONFIRMED, ride.getStatus());

        // 2. CONFIRMED -> DRIVER_ASSIGNED
        context.assignDriver();
        assertEquals(RideStatus.DRIVER_ASSIGNED, ride.getStatus());

        // 3. DRIVER_ASSIGNED -> DRIVER_ARRIVING
        context.driverArriving();
        assertEquals(RideStatus.DRIVER_ARRIVING, ride.getStatus());

        // 4. DRIVER_ARRIVING -> IN_PROGRESS
        context.startRide();
        assertEquals(RideStatus.IN_PROGRESS, ride.getStatus());

        // 5. IN_PROGRESS -> COMPLETED
        context.completeRide();
        assertEquals(RideStatus.COMPLETED, ride.getStatus());
    }

    @Test
    @DisplayName("Invalid state transition throws InvalidRideStateException")
    void testInvalidTransitionThrowsException() {
        Ride ride = createSampleRide();
        RideContext context = RideContext.fromRide(ride);

        // Cannot start ride directly when status is REQUESTED
        assertThrows(InvalidRideStateException.class, context::startRide);

        // Cannot complete ride directly when status is REQUESTED
        assertThrows(InvalidRideStateException.class, context::completeRide);
    }

    @Test
    @DisplayName("Cannot cancel completed ride")
    void testCannotCancelCompletedRide() {
        Ride ride = createSampleRide();
        RideContext context = RideContext.fromRide(ride);

        context.confirm();
        context.assignDriver();
        context.driverArriving();
        context.startRide();
        context.completeRide();

        assertEquals(RideStatus.COMPLETED, ride.getStatus());

        // Attempting to cancel completed ride
        InvalidRideStateException ex = assertThrows(InvalidRideStateException.class, context::cancelRide);
        assertTrue(ex.getMessage().contains("Ride cannot be cancelled after completion"));
    }

    @Test
    @DisplayName("TransitionTo helper method executes correct transition")
    void testTransitionToHelper() {
        Ride ride = createSampleRide();
        RideContext context = RideContext.fromRide(ride);

        context.transitionTo(RideStatus.CONFIRMED);
        assertEquals(RideStatus.CONFIRMED, ride.getStatus());

        context.transitionTo(RideStatus.DRIVER_ASSIGNED);
        assertEquals(RideStatus.DRIVER_ASSIGNED, ride.getStatus());
    }
}
