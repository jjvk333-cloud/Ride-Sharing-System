package com.velto.service;

import com.velto.dto.CreateRideRequest;
import com.velto.model.Ride;

import java.util.List;

public interface RideService {

    Ride createRide(CreateRideRequest request);

    Ride getRideById(String rideId);

    List<Ride> getAllRides();

    List<Ride> searchRides(String pickup, String destination);

    List<Ride> getRidesByDriver(String driverId);

    Ride updateRide(String rideId, CreateRideRequest request);

    void deleteRide(String rideId);
}
