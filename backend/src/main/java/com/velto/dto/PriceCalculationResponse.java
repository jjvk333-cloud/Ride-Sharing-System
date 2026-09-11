package com.velto.dto;

import com.velto.pattern.strategy.PricingType;

public class PriceCalculationResponse {

    private String rideId;
    private double basePrice;
    private int seats;
    private PricingType pricingType;
    private String strategyName;
    private double finalPrice;

    public PriceCalculationResponse() {
    }

    public PriceCalculationResponse(String rideId, double basePrice, int seats, PricingType pricingType, String strategyName, double finalPrice) {
        this.rideId = rideId;
        this.basePrice = basePrice;
        this.seats = seats;
        this.pricingType = pricingType;
        this.strategyName = strategyName;
        this.finalPrice = finalPrice;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
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

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }
}
