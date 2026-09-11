package com.velto.pattern.state;

import com.velto.exception.InvalidRideStateException;
import com.velto.model.Ride;
import com.velto.model.RideStatus;

/**
 * Context class in the GoF State Pattern.
 * Manages the current RideState and transitions the Ride entity safely.
 */
public class RideContext {

    private final Ride ride;
    private RideState currentState;

    public RideContext(Ride ride) {
        this.ride = ride;
        this.currentState = resolveState(ride.getStatus());
    }

    public static RideContext fromRide(Ride ride) {
        return new RideContext(ride);
    }

    public static RideState resolveState(RideStatus status) {
        if (status == null) {
            return new RequestedState();
        }
        return switch (status) {
            case REQUESTED -> new RequestedState();
            case CONFIRMED -> new ConfirmedState();
            case DRIVER_ASSIGNED -> new DriverAssignedState();
            case DRIVER_ARRIVING -> new DriverArrivingState();
            case IN_PROGRESS -> new InProgressState();
            case COMPLETED -> new CompletedState();
            case CANCELLED -> new CancelledState();
        };
    }

    public void setState(RideState state) {
        this.currentState = state;
        this.ride.setStatus(state.getStatus());
    }

    public RideState getCurrentState() {
        return currentState;
    }

    public Ride getRide() {
        return ride;
    }

    public void confirm() {
        currentState.confirm(this);
    }

    public void assignDriver() {
        currentState.assignDriver(this);
    }

    public void driverArriving() {
        currentState.driverArriving(this);
    }

    public void startRide() {
        currentState.startRide(this);
    }

    public void completeRide() {
        currentState.completeRide(this);
    }

    public void cancelRide() {
        currentState.cancelRide(this);
    }

    /**
     * Dispatches a transition based on the target RideStatus.
     */
    public void transitionTo(RideStatus targetStatus) {
        if (targetStatus == null) {
            throw new IllegalArgumentException("Target ride status cannot be null");
        }
        if (targetStatus == ride.getStatus()) {
            return; // No-op if already in that state
        }

        switch (targetStatus) {
            case CONFIRMED -> confirm();
            case DRIVER_ASSIGNED -> assignDriver();
            case DRIVER_ARRIVING -> driverArriving();
            case IN_PROGRESS -> startRide();
            case COMPLETED -> completeRide();
            case CANCELLED -> cancelRide();
            default -> throw new InvalidRideStateException("Cannot transition to status: " + targetStatus);
        }
    }
}
