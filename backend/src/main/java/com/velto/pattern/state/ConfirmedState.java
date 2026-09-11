package com.velto.pattern.state;

import com.velto.model.RideStatus;

public class ConfirmedState implements RideState {

    @Override
    public RideStatus getStatus() {
        return RideStatus.CONFIRMED;
    }

    @Override
    public void assignDriver(RideContext context) {
        context.setState(new DriverAssignedState());
    }

    @Override
    public void cancelRide(RideContext context) {
        context.setState(new CancelledState());
    }
}
