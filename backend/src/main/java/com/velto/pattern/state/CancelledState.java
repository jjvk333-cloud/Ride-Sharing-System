package com.velto.pattern.state;

import com.velto.exception.InvalidRideStateException;
import com.velto.model.RideStatus;

public class CancelledState implements RideState {

    @Override
    public RideStatus getStatus() {
        return RideStatus.CANCELLED;
    }

    @Override
    public void confirm(RideContext context) {
        throw new InvalidRideStateException("Ride is cancelled and cannot be reopened.");
    }
}
