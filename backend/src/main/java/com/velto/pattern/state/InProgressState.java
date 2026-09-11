package com.velto.pattern.state;

import com.velto.exception.InvalidRideStateException;
import com.velto.model.RideStatus;

public class InProgressState implements RideState {

    @Override
    public RideStatus getStatus() {
        return RideStatus.IN_PROGRESS;
    }

    @Override
    public void completeRide(RideContext context) {
        context.setState(new CompletedState());
    }

    @Override
    public void cancelRide(RideContext context) {
        throw new InvalidRideStateException("Cannot cancel a ride that is already in progress.");
    }
}
