package com.velto.pattern.builder;

import com.velto.model.Ride;
import com.velto.model.RideStatus;

import java.time.LocalDateTime;

/**
 * =========================================================================
 * DESIGN PATTERN 3 — BUILDER PATTERN (Creational)
 * =========================================================================
 * The RideBuilder provides a fluent, step-by-step construction pipeline
 * for complex Ride documents, preventing the telescoping constructor anti-pattern
 * and enforcing domain invariants prior to instantiation.
 */
public class RideBuilder {

    private String id;
    private String driverId;
    private String driverName;
    private String pickup;
    private String destination;
    private String date;
    private String time;
    private int seats;
    private int availableSeats;
    private String vehicleType;
    private double price;
    private RideStatus status = RideStatus.REQUESTED;
    private String preferences;
    private LocalDateTime createdAt = LocalDateTime.now();

    public RideBuilder() {}

    public RideBuilder id(String id) {
        this.id = id;
        return this;
    }

    public RideBuilder driverId(String driverId) {
        this.driverId = driverId;
        return this;
    }

    public RideBuilder driverName(String driverName) {
        this.driverName = driverName;
        return this;
    }

    public RideBuilder pickup(String pickup) {
        this.pickup = pickup;
        return this;
    }

    public RideBuilder destination(String destination) {
        this.destination = destination;
        return this;
    }

    public RideBuilder date(String date) {
        this.date = date;
        return this;
    }

    public RideBuilder time(String time) {
        this.time = time;
        return this;
    }

    public RideBuilder seats(int seats) {
        this.seats = seats;
        return this;
    }

    public RideBuilder availableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
        return this;
    }

    public RideBuilder vehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
        return this;
    }

    public RideBuilder price(double price) {
        this.price = price;
        return this;
    }

    public RideBuilder status(RideStatus status) {
        this.status = status;
        return this;
    }

    public RideBuilder preferences(String preferences) {
        this.preferences = preferences;
        return this;
    }

    public RideBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Ride build() {
        if (pickup == null || pickup.isBlank()) {
            throw new IllegalStateException("Pickup location cannot be blank");
        }
        if (destination == null || destination.isBlank()) {
            throw new IllegalStateException("Destination cannot be blank");
        }
        if (date == null || date.isBlank()) {
            throw new IllegalStateException("Ride date cannot be blank");
        }
        if (time == null || time.isBlank()) {
            throw new IllegalStateException("Ride time cannot be blank");
        }
        if (seats <= 0) {
            throw new IllegalStateException("Seats must be greater than zero");
        }
        if (price < 0) {
            throw new IllegalStateException("Price cannot be negative");
        }

        int finalAvailableSeats = availableSeats > 0 ? availableSeats : seats;

        return new Ride.Builder()
                .id(this.id)
                .driverId(this.driverId)
                .driverName(this.driverName != null ? this.driverName : "Driver")
                .pickup(this.pickup)
                .destination(this.destination)
                .date(this.date)
                .time(this.time)
                .seats(this.seats)
                .availableSeats(finalAvailableSeats)
                .vehicleType(this.vehicleType)
                .price(this.price)
                .status(this.status != null ? this.status : RideStatus.REQUESTED)
                .preferences(this.preferences)
                .createdAt(this.createdAt != null ? this.createdAt : LocalDateTime.now())
                .build();
    }
}
