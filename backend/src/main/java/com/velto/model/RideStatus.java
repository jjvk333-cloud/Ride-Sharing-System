package com.velto.model;

/**
 * Lifecycle states of a Ride.
 */
public enum RideStatus {
    REQUESTED,
    CONFIRMED,
    DRIVER_ASSIGNED,
    DRIVER_ARRIVING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
