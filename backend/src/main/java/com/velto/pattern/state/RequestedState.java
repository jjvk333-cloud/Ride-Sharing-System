package com.velto.pattern.state;

import com.velto.model.RideStatus;

public class RequestedState implements RideState {

    @Override
    public RideStatus getStatus() {
        return RideStatus.REQUESTED;
    }

    @Override
    public void confirm(RideContext context) {
        context.setState(new ConfirmedState());
    }

    @Override
    public void cancelRide(RideContext context) {
        context.setState(new CancelledState());
    }
}
