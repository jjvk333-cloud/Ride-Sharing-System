package com.velto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Ride domain model representing a shared ride in VELTO.
 * Employs the GoF Builder Pattern (Ride.Builder) for safe, readable object construction.
 */
@Document(collection = "rides")
@CompoundIndex(name = "pickup_destination_status_idx", def = "{'pickup': 1, 'destination': 1, 'status': 1}")
public class Ride {

    @Id
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
    private RideStatus status;
    private String preferences;
    private LocalDateTime createdAt;

    // Default constructor for Spring Data MongoDB reflection
    public Ride() {
        this.createdAt = LocalDateTime.now();
        this.status = RideStatus.REQUESTED;
    }

    // Private constructor enforcing instantiation via Ride.Builder
    private Ride(Builder builder) {
        this.id = builder.id;
        this.driverId = builder.driverId;
        this.driverName = builder.driverName;
        this.pickup = builder.pickup;
        this.destination = builder.destination;
        this.date = builder.date;
        this.time = builder.time;
        this.seats = builder.seats;
        this.availableSeats = builder.availableSeats > 0 ? builder.availableSeats : builder.seats;
        this.vehicleType = builder.vehicleType;
        this.price = builder.price;
        this.status = builder.status != null ? builder.status : RideStatus.REQUESTED;
        this.preferences = builder.preferences;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
    }

    // ==========================================
    // DESIGN PATTERN 3: BUILDER PATTERN
    // ==========================================
    public static class Builder {
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
        private RideStatus status;
        private String preferences;
        private LocalDateTime createdAt;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder driverId(String driverId) {
            this.driverId = driverId;
            return this;
        }

        public Builder driverName(String driverName) {
            this.driverName = driverName;
            return this;
        }

        public Builder pickup(String pickup) {
            this.pickup = pickup;
            return this;
        }

        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder time(String time) {
            this.time = time;
            return this;
        }

        public Builder seats(int seats) {
            this.seats = seats;
            return this;
        }

        public Builder availableSeats(int availableSeats) {
            this.availableSeats = availableSeats;
            return this;
        }

        public Builder vehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
            return this;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }

        public Builder status(RideStatus status) {
            this.status = status;
            return this;
        }

        public Builder preferences(String preferences) {
            this.preferences = preferences;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Ride build() {
            // Validation of mandatory fields
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

            return new Ride(this);
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
