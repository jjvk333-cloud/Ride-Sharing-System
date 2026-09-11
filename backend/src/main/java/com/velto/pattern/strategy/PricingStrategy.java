package com.velto.pattern.strategy;

/**
 * Strategy Pattern interface defining dynamic pricing algorithms.
 */
public interface PricingStrategy {

    String getStrategyName();

    PricingType getPricingType();

    double calculatePrice(double basePrice, int seats);
}
