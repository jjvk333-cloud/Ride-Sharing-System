package com.velto.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CreateRideRequest {

    private String driverId;
    private String driverName;

    @NotBlank(message = "Pickup location is required")
    private String pickup;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotBlank(message = "Date is required")
    private String date;

    @NotBlank(message = "Time is required")
    private String time;

    @Min(value = 1, message = "Seats must be at least 1")
    private int seats;

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    @Min(value = 0, message = "Price cannot be negative")
    private double price;

    private String preferences;

    public CreateRideRequest() {
    }

    public CreateRideRequest(String driverId, String driverName, String pickup, String destination, String date, String time, int seats, String vehicleType, double price, String preferences) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.pickup = pickup;
        this.destination = destination;
        this.date = date;
        this.time = time;
        this.seats = seats;
        this.vehicleType = vehicleType;
        this.price = price;
        this.preferences = preferences;
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

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
}
