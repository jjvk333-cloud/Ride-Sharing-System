package com.velto.pattern.state;

import com.velto.exception.InvalidRideStateException;
import com.velto.model.RideStatus;

/**
 * =========================================================================
 * DESIGN PATTERN 5 — STATE PATTERN
 * =========================================================================
 * RideState interface declaring transition behaviors for each ride status.
 * Invalid transitions default to throwing InvalidRideStateException.
 */
public interface RideState {

    RideStatus getStatus();

    default void confirm(RideContext context) {
        throw new InvalidRideStateException("Cannot confirm ride in '" + getStatus() + "' state.");
    }

    default void assignDriver(RideContext context) {
        throw new InvalidRideStateException("Cannot assign driver in '" + getStatus() + "' state.");
    }

    default void driverArriving(RideContext context) {
        throw new InvalidRideStateException("Cannot mark driver arriving in '" + getStatus() + "' state.");
    }

    default void startRide(RideContext context) {
        throw new InvalidRideStateException("Cannot start ride in '" + getStatus() + "' state.");
    }

    default void completeRide(RideContext context) {
        throw new InvalidRideStateException("Cannot complete ride in '" + getStatus() + "' state.");
    }

    default void cancelRide(RideContext context) {
        throw new InvalidRideStateException("Cannot cancel ride in '" + getStatus() + "' state.");
    }
}
