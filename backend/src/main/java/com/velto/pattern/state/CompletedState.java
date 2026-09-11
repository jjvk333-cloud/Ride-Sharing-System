package com.velto.pattern.state;

import com.velto.exception.InvalidRideStateException;
import com.velto.model.RideStatus;

public class CompletedState implements RideState {

    @Override
    public RideStatus getStatus() {
        return RideStatus.COMPLETED;
    }

    @Override
    public void cancelRide(RideContext context) {
        throw new InvalidRideStateException("Ride cannot be cancelled after completion.");
    }
}
