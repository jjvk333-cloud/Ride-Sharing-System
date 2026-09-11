package com.velto.service;

import com.velto.dto.CreateRideRequest;
import com.velto.model.Ride;
import com.velto.model.RideStatus;

import java.util.List;

public interface RideService {

    Ride createRide(CreateRideRequest request);

    Ride getRideById(String rideId);

    List<Ride> getAllRides();

    List<Ride> searchRides(String pickup, String destination);

    List<Ride> getRidesByDriver(String driverId);

    Ride updateRide(String rideId, CreateRideRequest request);

    Ride updateRideStatus(String rideId, RideStatus targetStatus);

    void deleteRide(String rideId);
}
