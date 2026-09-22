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

    // Payment Adapter parameters
    private String paymentMethod = "MOCK";
    private String upiId;
    private String cardNumber;
    private String expiryDate;
    private String cvv;

    public CreateBookingRequest() {
    }

    public CreateBookingRequest(String rideId, String passengerId, int seats, PricingType pricingType) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.seats = seats;
        this.pricingType = pricingType != null ? pricingType : PricingType.STANDARD;
        this.paymentMethod = "MOCK";
    }

    public CreateBookingRequest(String rideId, String passengerId, int seats, PricingType pricingType, String paymentMethod) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.seats = seats;
        this.pricingType = pricingType != null ? pricingType : PricingType.STANDARD;
        this.paymentMethod = paymentMethod != null ? paymentMethod : "MOCK";
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
