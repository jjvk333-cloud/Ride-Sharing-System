package com.velto.dto;

import com.velto.pattern.strategy.PricingType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CreateBookingRequest {

    @NotBlank(message = "Ride ID is required")
    private String rideId;

    @NotBlank(message = "Passenger ID is required")
    private String passengerId;

    @Min(value = 1, message = "Must book at least 1 seat")
    private int seats;

    private PricingType pricingType = PricingType.STANDARD;

    public CreateBookingRequest() {
    }

    public CreateBookingRequest(String rideId, String passengerId, int seats, PricingType pricingType) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.seats = seats;
        this.pricingType = pricingType != null ? pricingType : PricingType.STANDARD;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public PricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(PricingType pricingType) {
        this.pricingType = pricingType;
    }
}
