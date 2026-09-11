package com.velto.pattern.strategy;

import org.springframework.stereotype.Component;

/**
 * Concrete Strategy: Shared ride / carpooling discount applying a 20% discount (0.8x multiplier).
 */
@Component
public class SharedRidePricingStrategy implements PricingStrategy {

    private static final double SHARED_DISCOUNT_MULTIPLIER = 0.8;

    @Override
    public String getStrategyName() {
        return "Shared Ride Carpooling Discount (0.8x)";
    }

    @Override
    public PricingType getPricingType() {
        return PricingType.SHARED;
    }

    @Override
    public double calculatePrice(double basePrice, int seats) {
        if (seats <= 0) {
            throw new IllegalArgumentException("Seats must be at least 1");
        }
        return Math.round((basePrice * SHARED_DISCOUNT_MULTIPLIER * seats) * 100.0) / 100.0;
    }
}
