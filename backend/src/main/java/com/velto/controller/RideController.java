package com.velto.controller;

import com.velto.dto.CreateRideRequest;
import com.velto.dto.PriceCalculationResponse;
import com.velto.model.Ride;
import com.velto.model.RideStatus;
import com.velto.pattern.strategy.PricingContext;
import com.velto.pattern.strategy.PricingType;
import com.velto.service.RideService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rides")
@CrossOrigin(origins = "*")
public class RideController {

    private final RideService rideService;
    private final PricingContext pricingContext;
    private final com.velto.service.RouteService routeService;
    private final com.velto.pattern.factory.ride.RideCreatorRegistry rideCreatorRegistry;

    public RideController(RideService rideService,
                          PricingContext pricingContext,
                          com.velto.service.RouteService routeService,
                          com.velto.pattern.factory.ride.RideCreatorRegistry rideCreatorRegistry) {
        this.rideService = rideService;
        this.pricingContext = pricingContext;
        this.routeService = routeService;
        this.rideCreatorRegistry = rideCreatorRegistry;
    }

    @PostMapping
    public ResponseEntity<Ride> createRide(@Valid @RequestBody CreateRideRequest request) {
        Ride created = rideService.createRide(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Ride>> getRides(
            @RequestParam(required = false) String pickup,
            @RequestParam(required = false) String destination) {
        List<Ride> rides = rideService.searchRides(pickup, destination);
        return ResponseEntity.ok(rides);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ride> getRideById(@PathVariable String id) {
        Ride ride = rideService.getRideById(id);
        return ResponseEntity.ok(ride);
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Ride>> getRidesByDriver(@PathVariable String driverId) {
        List<Ride> rides = rideService.getRidesByDriver(driverId);
        return ResponseEntity.ok(rides);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ride> updateRide(@PathVariable String id, @Valid @RequestBody CreateRideRequest request) {
        Ride updated = rideService.updateRide(id, request);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Ride> updateRideStatus(@PathVariable String id, @RequestParam RideStatus status) {
        Ride updated = rideService.updateRideStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRide(@PathVariable String id) {
        rideService.deleteRide(id);
        return ResponseEntity.ok(Map.of("message", "Ride deleted successfully"));
    }

    @GetMapping("/{id}/calculate-price")
    public ResponseEntity<PriceCalculationResponse> calculatePrice(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") int seats,
            @RequestParam(defaultValue = "STANDARD") PricingType pricingType) {
        Ride ride = rideService.getRideById(id);

        pricingContext.setStrategyByType(pricingType);
        double finalPrice = pricingContext.calculatePrice(ride.getPrice(), seats);

        PriceCalculationResponse response = new PriceCalculationResponse(
                ride.getId(),
                ride.getPrice(),
                seats,
                pricingType,
                pricingContext.getStrategy().getStrategyName(),
                finalPrice
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/estimate")
    public ResponseEntity<Map<String, Object>> estimateFare(
            @RequestParam String pickup,
            @RequestParam String destination,
            @RequestParam(defaultValue = "SEDAN") String vehicleType,
            @RequestParam(defaultValue = "1") int seats,
            @RequestParam(defaultValue = "STANDARD") PricingType pricingType) {

        double distance = routeService.calculateDistance(pickup, destination);
        com.velto.pattern.factory.ride.RideCreator creator = rideCreatorRegistry.getCreator(vehicleType);
        Ride simulatedRide = creator.createRide("ESTIMATE", "Driver", pickup, destination, distance, "TODAY", "NOW", null);

        pricingContext.setStrategyByType(pricingType);
        double totalFare = pricingContext.calculatePrice(simulatedRide.getPrice(), seats);

        double[] pickupCoords = routeService.getCoordinates(pickup);
        double[] destCoords = routeService.getCoordinates(destination);

        Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("pickup", pickup);
        result.put("destination", destination);
        result.put("distanceKm", distance);
        result.put("vehicleType", creator.getVehicleType());
        result.put("seats", seats);
        result.put("pricingType", pricingType);
        result.put("strategyName", pricingContext.getStrategy().getStrategyName());
        result.put("baseRidePrice", simulatedRide.getPrice());
        result.put("estimatedFare", totalFare);
        result.put("pickupCoords", pickupCoords);
        result.put("destCoords", destCoords);

        return ResponseEntity.ok(result);
    }
}
