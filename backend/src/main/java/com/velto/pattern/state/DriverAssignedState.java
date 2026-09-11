package com.velto.pattern.state;

import com.velto.model.RideStatus;

public class DriverAssignedState implements RideState {

    @Override
    public RideStatus getStatus() {
        return RideStatus.DRIVER_ASSIGNED;
    }

    @Override
    public void driverArriving(RideContext context) {
        context.setState(new DriverArrivingState());
    }

    @Override
    public void cancelRide(RideContext context) {
        context.setState(new CancelledState());
    }
}
