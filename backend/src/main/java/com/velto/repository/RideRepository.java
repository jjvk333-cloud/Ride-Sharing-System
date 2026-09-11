package com.velto.repository;

import com.velto.model.Ride;
import com.velto.model.RideStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {

    List<Ride> findByStatus(RideStatus status);

    List<Ride> findByDriverId(String driverId);

    List<Ride> findByPickupContainingIgnoreCaseAndDestinationContainingIgnoreCaseAndStatus(String pickup, String destination, RideStatus status);

    List<Ride> findByPickupContainingIgnoreCaseOrDestinationContainingIgnoreCase(String pickup, String destination);
}
