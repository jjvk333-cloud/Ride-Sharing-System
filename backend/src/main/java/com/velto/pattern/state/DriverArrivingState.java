package com.velto.pattern.state;

import com.velto.model.RideStatus;

public class DriverArrivingState implements RideState {

    @Override
    public RideStatus getStatus() {
        return RideStatus.DRIVER_ARRIVING;
    }

    @Override
    public void startRide(RideContext context) {
        context.setState(new InProgressState());
    }

    @Override
    public void cancelRide(RideContext context) {
        context.setState(new CancelledState());
    }
}
