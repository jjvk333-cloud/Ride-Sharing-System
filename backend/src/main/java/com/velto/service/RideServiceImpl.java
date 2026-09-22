package com.velto.service;

import com.velto.dto.CreateRideRequest;
import com.velto.exception.RideNotFoundException;
import com.velto.model.Ride;
import com.velto.model.RideStatus;
import com.velto.pattern.observer.RideEvent;
import com.velto.pattern.observer.RideEventSubject;
import com.velto.pattern.state.RideContext;
import com.velto.repository.RideRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideEventSubject rideEventSubject;

    public RideServiceImpl(RideRepository rideRepository, RideEventSubject rideEventSubject) {
        this.rideRepository = rideRepository;
        this.rideEventSubject = rideEventSubject;
    }

    @Override
    public Ride createRide(CreateRideRequest request) {
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
            return rideRepository.findByPickupContainingIgnoreCaseAndDestinationContainingIgnoreCase(
                    pickup, destination
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
    public Ride updateRideStatus(String rideId, RideStatus targetStatus) {
        Ride ride = getRideById(rideId);
        RideStatus previousStatus = ride.getStatus();

        // 1. Employ State Pattern for validation and transition
        RideContext context = RideContext.fromRide(ride);
        context.transitionTo(targetStatus);

        Ride savedRide = rideRepository.save(context.getRide());

        // 2. Employ Observer Pattern to broadcast notification event to all registered observers
        RideEvent event = new RideEvent(savedRide, previousStatus, targetStatus, "Ride status changed to " + targetStatus);
        rideEventSubject.notifyObservers(event);

        return savedRide;
    }

    @Override
    public void deleteRide(String rideId) {
        if (!rideRepository.existsById(rideId)) {
            throw new RideNotFoundException("Ride not found with ID: " + rideId);
        }
        rideRepository.deleteById(rideId);
    }
}
