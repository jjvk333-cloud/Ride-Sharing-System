package com.velto.model;

import org.springframework.data.annotation.TypeAlias;

/**
 * Concrete User subclass representing a Driver.
 */
@TypeAlias("driver")
public class Driver extends User {

    private String vehicleNumber;
    private String vehicleType; // e.g. Sedan, SUV, Hatchback, Auto
    private String licenseNumber;
    private boolean available = true;
    private double rating = 5.0;

    public Driver() {
        super();
        setRole(Role.DRIVER);
    }

    public Driver(String name, String email, String password, String phone) {
        super(name, email, password, Role.DRIVER, phone);
        this.available = true;
        this.rating = 5.0;
    }

    public Driver(String name, String email, String password, String phone, String vehicleNumber, String vehicleType, String licenseNumber) {
        super(name, email, password, Role.DRIVER, phone);
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.licenseNumber = licenseNumber;
        this.available = true;
        this.rating = 5.0;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
