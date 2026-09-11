package com.velto.pattern.observer;

import com.velto.model.Ride;
import com.velto.model.RideStatus;

import java.time.LocalDateTime;

/**
 * Event object passed to observers when a ride status change occurs.
 */
public class RideEvent {

    private final Ride ride;
    private final RideStatus previousStatus;
    private final RideStatus newStatus;
    private final String description;
    private final LocalDateTime timestamp;

    public RideEvent(Ride ride, RideStatus previousStatus, RideStatus newStatus, String description) {
        this.ride = ride;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public Ride getRide() {
        return ride;
    }

    public RideStatus getPreviousStatus() {
        return previousStatus;
    }

    public RideStatus getNewStatus() {
        return newStatus;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
