package com.velto.service;

import com.velto.dto.CreateRideRequest;
import com.velto.exception.RideNotFoundException;
import com.velto.model.Ride;
import com.velto.model.RideStatus;
import com.velto.repository.RideRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RideService utilizing the Builder Pattern (Ride.Builder) to safely construct Ride objects.
 */
@Service
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;

    public RideServiceImpl(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    @Override
    public Ride createRide(CreateRideRequest request) {
        // Employ GoF Builder Pattern to construct the Ride object step-by-step
        Ride ride = new Ride.Builder()
                .driverId(request.getDriverId())
                .driverName(request.getDriverName() != null ? request.getDriverName() : "Driver")
                .pickup(request.getPickup())
                .destination(request.getDestination())
                .date(request.getDate())
                .time(request.getTime())
                .seats(request.getSeats())
                .availableSeats(request.getSeats())
                .vehicleType(request.getVehicleType())
                .price(request.getPrice())
                .preferences(request.getPreferences())
                .status(RideStatus.REQUESTED)
                .build();

        return rideRepository.save(ride);
    }

    @Override
    public Ride getRideById(String rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with ID: " + rideId));
    }

    @Override
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    @Override
    public List<Ride> searchRides(String pickup, String destination) {
        if ((pickup == null || pickup.isBlank()) && (destination == null || destination.isBlank())) {
            return rideRepository.findAll();
        }

        if (pickup != null && !pickup.isBlank() && destination != null && !destination.isBlank()) {
            return rideRepository.findByPickupContainingIgnoreCaseAndDestinationContainingIgnoreCaseAndStatus(
                    pickup, destination, RideStatus.REQUESTED
            );
        }

        String search = (pickup != null && !pickup.isBlank()) ? pickup : destination;
        return rideRepository.findByPickupContainingIgnoreCaseOrDestinationContainingIgnoreCase(search, search);
    }

    @Override
    public List<Ride> getRidesByDriver(String driverId) {
        return rideRepository.findByDriverId(driverId);
    }

    @Override
    public Ride updateRide(String rideId, CreateRideRequest request) {
        Ride existingRide = getRideById(rideId);

        existingRide.setPickup(request.getPickup());
        existingRide.setDestination(request.getDestination());
        existingRide.setDate(request.getDate());
        existingRide.setTime(request.getTime());
        existingRide.setSeats(request.getSeats());
        existingRide.setVehicleType(request.getVehicleType());
        existingRide.setPrice(request.getPrice());
        existingRide.setPreferences(request.getPreferences());

        return rideRepository.save(existingRide);
    }

    @Override
    public void deleteRide(String rideId) {
        if (!rideRepository.existsById(rideId)) {
            throw new RideNotFoundException("Ride not found with ID: " + rideId);
        }
        rideRepository.deleteById(rideId);
    }
}
